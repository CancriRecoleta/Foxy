package com.github.foxy.client.gui;

import com.github.foxy.client.ClientSessionEvents;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import com.github.foxy.client.core.SSAO;
import com.github.foxy.client.core.FoxyRenderSystem;
import com.github.foxy.client.core.util.IrisUtil;
import com.github.foxy.common.util.cpu.CpuLayout;
import com.github.foxy.commonImpl.FoxyCommon;
import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

// Adds Foxy's config options to Embeddium's video-settings GUI. Upstream Foxy (FoxyConfigMenu) uses
// Sodium 0.6's declarative net.caffeinemc...api.config.ConfigBuilder, which Embeddium 0.5 does not
// have; Embeddium 0.5 exposes the older imperative me.jellysquid...gui.options.OptionImpl builder
// plus the OptionGUIConstructionEvent. So the construction is rewritten against Embeddium's API while
// the option set/semantics mirror upstream 1:1 (same options, ranges, bindings, enablers, apply
// actions).
//
// Apply model: Embeddium pushes each changed option's value through its binding setter, then calls
// OptionStorage.save() once. We persist FoxyConfig in save() and apply the live side effects (thread
// update, iris reload, renderer recreate, instance create/shutdown, render-distance) there, driven by
// a pending-flag set the setters populate — this reproduces upstream's post-change flags/runners
// (which Embeddium's simpler API lacks) with the same de-duplication.
public final class EmbeddiumConfigIntegration {
    private EmbeddiumConfigIntegration() {}

    private enum Hook { ENABLED_CHANGED, RENDER_DISTANCE_CHANGED, RENDER_RELOAD, UPDATE_THREADS, IRIS_RELOAD }
    private static final EnumSet<Hook> PENDING = EnumSet.noneOf(Hook.class);

    // Reference to the master enable option so dependent options can read its LIVE (pending) value
    // for enable/disable, matching upstream's setEnabler("foxy:enabled").
    private static OptionImpl<FoxyConfig, Boolean> enabledOption;

    private static final OptionStorage<FoxyConfig> STORAGE = new OptionStorage<>() {
        @Override public FoxyConfig getData() { return FoxyConfig.CONFIG; }
        @Override public void save() {
            FoxyConfig.CONFIG.save();
            applyPending();
        }
    };

    // Subscribe once (client-side) during mod setup. Embeddium is a hard dependency, so the API is
    // always present.
    public static void register() {
        OptionGUIConstructionEvent.BUS.addListener(event -> event.addPage(buildFoxyPage()));
    }

    private static void applyPending() {
        if (PENDING.isEmpty()) return;
        var cfg = FoxyConfig.CONFIG;
        var lr = Minecraft.getInstance().levelRenderer;
        IGetFoxyRenderSystem vrsh = lr != null ? (IGetFoxyRenderSystem) lr : null;

        // Instance lifecycle (upstream: enabled on -> createInstance in session; off -> shutdown).
        if (PENDING.contains(Hook.ENABLED_CHANGED)) {
            if (cfg.enabled) {
                if (ClientSessionEvents.inSession && FoxyCommon.getInstance() == null) {
                    FoxyCommon.createInstance();
                }
            } else {
                if (vrsh != null) vrsh.foxy$shutdownRenderer();
                FoxyCommon.shutdownInstance();
            }
        }

        // A full shutdown+recreate applies subdivision/fog/ssao/threads/render-distance etc. at once;
        // foxy$createRenderer() guards on enabled/isRenderingEnabled/level/instance, so it is safe in
        // any state. Only the per-option runners (rendering toggle, render distance) are used when no
        // full reload is pending, matching upstream's "don't run if RENDER_RELOAD also changed".
        boolean recreate = PENDING.contains(Hook.RENDER_RELOAD) || PENDING.contains(Hook.ENABLED_CHANGED);
        if (recreate) {
            if (vrsh != null) {
                vrsh.foxy$shutdownRenderer();
                vrsh.foxy$createRenderer();
            }
        } else if (vrsh != null) {
            if (PENDING.contains(Hook.RENDER_DISTANCE_CHANGED)) {
                FoxyRenderSystem vrs = vrsh.foxy$getRenderSystem();
                if (vrs != null) vrs.setRenderDistance(cfg.sectionRenderDistance);
            }
        }

        // Mirror upstream's "voxy:update_threads" conflicts-with "voxy:enabled" de-dup: when enabled also
        // changed, the recreate path already calls updateDedicatedThreads() (foxy$createRenderer), so skip
        // the redundant call here.
        if (PENDING.contains(Hook.UPDATE_THREADS) && !PENDING.contains(Hook.ENABLED_CHANGED)) {
            var instance = FoxyCommon.getInstance();
            if (instance != null) instance.updateDedicatedThreads();
        }
        if (PENDING.contains(Hook.IRIS_RELOAD)) {
            IrisUtil.reload();
        }
        PENDING.clear();
    }

    // ===== Single page (upstream FoxyConfigMenu's two pages merged into one) =====

    private static OptionPage buildFoxyPage() {
        List<OptionGroup> groups = new ArrayList<>();

        // --- master enable / threads / ingest (was the "General" page) ---
        enabledOption = boolOption("enabled", "foxy.config.general.enabled",
                cfg -> cfg.enabled,
                (cfg, v) -> { cfg.enabled = v; PENDING.add(Hook.ENABLED_CHANGED); PENDING.add(Hook.RENDER_RELOAD); PENDING.add(Hook.IRIS_RELOAD); },
                null); // always enabled so it can always be toggled
        groups.add(OptionGroup.createBuilder().add(enabledOption).build());

        BooleanSupplier enabledGate = () -> enabledOption.getValue();

        groups.add(OptionGroup.createBuilder()
                .add(intOption("thread_count", "foxy.config.general.serviceThreads",
                        1, Math.max(1, CpuLayout.getCoreCount()), 1,
                        v -> Component.literal(Integer.toString(v)),
                        cfg -> cfg.serviceThreads,
                        (cfg, v) -> { cfg.serviceThreads = v; PENDING.add(Hook.UPDATE_THREADS); },
                        null, enabledGate))
                .add(boolOption("use_sodium_threads", "foxy.config.general.useSodiumBuilder",
                        cfg -> !cfg.dontUseSodiumBuilderThreads,
                        (cfg, v) -> { cfg.dontUseSodiumBuilderThreads = !v; PENDING.add(Hook.UPDATE_THREADS); PENDING.add(Hook.RENDER_RELOAD); },
                        enabledGate))
                .build());

        groups.add(OptionGroup.createBuilder()
                .add(boolOption("ingest_enabled", "foxy.config.general.ingest",
                        cfg -> cfg.ingestEnabled,
                        (cfg, v) -> cfg.ingestEnabled = v,
                        enabledGate))
                .build());

        // Master logging toggle. Independent of the enable gate so logs can always be silenced.
        groups.add(OptionGroup.createBuilder()
                .add(boolOption("logging", "foxy.config.general.logging",
                        cfg -> cfg.loggingEnabled,
                        (cfg, v) -> { cfg.loggingEnabled = v; cfg.applyLogging(); },
                        null))
                .build());

        // Debug / diagnostics (was upstream's F3 voxy:version + voxy:gpu_debug entries; on 1.20.1
        // there is no DebugScreenEntries registry so they become config toggles instead).
        // debug_hud: show Foxy's version/instance/renderer lines on F3 — always toggleable (like
        //   logging) so status is visible even when Foxy is off; read live, no reload needed.
        // render_statistics: also collect the heavy per-LOD statistics + per-pass GPU timing and
        //   compile the HAS_STATISTICS shader path. applyDebug() flips the global flags and a
        //   RENDER_RELOAD recreates the renderer so the shaders recompile. Gated by enabled.
        groups.add(OptionGroup.createBuilder()
                .add(boolOption("debug_hud", "foxy.config.general.debug_hud",
                        cfg -> cfg.debugHud,
                        (cfg, v) -> cfg.debugHud = v,
                        null))
                .add(boolOption("render_statistics", "foxy.config.general.render_statistics",
                        cfg -> cfg.renderStatistics,
                        (cfg, v) -> { cfg.renderStatistics = v; cfg.applyDebug(); PENDING.add(Hook.RENDER_RELOAD); },
                        enabledGate))
                .build());

        // --- rendering / quality (was the "Rendering" page). The separate "Foxy rendering" toggle was
        // merged into the master "Enable Foxy" switch, so these are gated only by enabled. ---
        groups.add(OptionGroup.createBuilder()
                .add(intOption("subdivsize", "foxy.config.general.subDivisionSize",
                        0, SUBDIV_IN_MAX, 1,
                        v -> Component.literal(Integer.toString(Math.round(ln2subDiv(v)))),
                        cfg -> subDiv2ln(cfg.subDivisionSize),
                        (cfg, v) -> cfg.subDivisionSize = ln2subDiv(v),
                        OptionImpact.HIGH, enabledGate))
                .add(intOption("render_distance", "foxy.config.general.renderDistance",
                        10, 64 * 16, 1,
                        v -> Component.literal(Integer.toString(v * 2)),
                        cfg -> Math.round(cfg.sectionRenderDistance * 16),
                        (cfg, v) -> { cfg.sectionRenderDistance = ((float) v) / 16; PENDING.add(Hook.RENDER_DISTANCE_CHANGED); },
                        OptionImpact.MEDIUM, enabledGate))
                .build());

        // Fog + SSAO are also disabled while an Iris shader pack is active (upstream: setEnablerInherit
        // !irisShadersEnabledInConfig), in addition to the enabled gate.
        BooleanSupplier shaderGate = () -> enabledOption.getValue() && !IrisUtil.irisShadersEnabledInConfig();
        groups.add(OptionGroup.createBuilder()
                .add(boolOption("environmental_fog", "foxy.config.general.environmental_fog",
                        cfg -> cfg.useEnvironmentalFog,
                        (cfg, v) -> { cfg.useEnvironmentalFog = v; PENDING.add(Hook.RENDER_RELOAD); },
                        shaderGate))
                .add(enumOption("ssao_mode", "foxy.config.general.ssao_mode", SSAO.SSAOMode.class,
                        FoxyConfig::getSSAOMode,
                        (cfg, v) -> { cfg.setSSAOMode(v); PENDING.add(Hook.RENDER_RELOAD); },
                        OptionImpact.MEDIUM, shaderGate))
                .build());

        // modId "foxy" => Embeddium files this page under a "Foxy" section whose header shows the mod
        // name + its logoFile icon (TabHeaderWidget resolves it via the Forge ModContainer for "foxy").
        return new OptionPage(OptionIdentifier.create("foxy", "settings"),
                Component.translatable("foxy.config.page"), ImmutableList.copyOf(groups));
    }

    // ===== Option builder helpers =====

    private static OptionImpl<FoxyConfig, Boolean> boolOption(String id, String key,
            Function<FoxyConfig, Boolean> getter, BiConsumer<FoxyConfig, Boolean> setter, BooleanSupplier enabler) {
        var b = OptionImpl.createBuilder(boolean.class, STORAGE)
                .setId(new ResourceLocation("foxy", id))
                .setName(Component.translatable(key))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(setter, getter);
        if (enabler != null) b.setEnabledPredicate(enabler);
        return b.build();
    }

    private static OptionImpl<FoxyConfig, Integer> intOption(String id, String key, int min, int max, int step,
            ControlValueFormatter formatter, Function<FoxyConfig, Integer> getter, BiConsumer<FoxyConfig, Integer> setter,
            OptionImpact impact, BooleanSupplier enabler) {
        var b = OptionImpl.createBuilder(int.class, STORAGE)
                .setId(new ResourceLocation("foxy", id))
                .setName(Component.translatable(key))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setControl(opt -> new SliderControl(opt, min, max, step, formatter))
                .setBinding(setter, getter);
        if (impact != null) b.setImpact(impact);
        if (enabler != null) b.setEnabledPredicate(enabler);
        return b.build();
    }

    private static <E extends Enum<E>> OptionImpl<FoxyConfig, E> enumOption(String id, String key, Class<E> type,
            Function<FoxyConfig, E> getter, BiConsumer<FoxyConfig, E> setter, OptionImpact impact, BooleanSupplier enabler) {
        var b = OptionImpl.createBuilder(type, STORAGE)
                .setId(new ResourceLocation("foxy", id))
                .setName(Component.translatable(key))
                .setTooltip(Component.translatable(key + ".tooltip"))
                .setControl(opt -> new CyclingControl<>(opt, type))
                .setBinding(setter, getter);
        if (impact != null) b.setImpact(impact);
        if (enabler != null) b.setEnabledPredicate(enabler);
        return b.build();
    }

    // ===== subdivision-size <-> slider transforms (ported verbatim from upstream FoxyConfigMenu) =====
    // slider value is 0..SUBDIV_IN_MAX; stored subDivisionSize is SUBDIV_MIN..SUBDIV_MAX (log scale).
    private static final int SUBDIV_IN_MAX = 100;
    private static final double SUBDIV_MIN = 28;
    private static final double SUBDIV_MAX = 256;
    private static final double SUBDIV_CONST = Math.log(SUBDIV_MAX / SUBDIV_MIN) / Math.log(2);

    private static float ln2subDiv(int in) {
        return (float) (SUBDIV_MIN * Math.pow(2, SUBDIV_CONST * ((double) in / SUBDIV_IN_MAX)));
    }

    private static int subDiv2ln(float in) {
        return (int) (((Math.log(((double) in) / SUBDIV_MIN) / Math.log(2)) / SUBDIV_CONST) * SUBDIV_IN_MAX);
    }
}
