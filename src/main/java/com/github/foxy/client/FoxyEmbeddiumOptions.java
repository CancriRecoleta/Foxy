package com.github.foxy.client;

import com.google.common.collect.ImmutableList;
import com.github.foxy.Foxy;
import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.common.Logger;
import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;
import org.embeddedt.embeddium.client.gui.options.StandardOptions;

import java.util.List;

public final class FoxyEmbeddiumOptions {
    private static final OptionStorage<FoxyConfig> STORAGE = new OptionStorage<>() {
        @Override
        public FoxyConfig getData() {
            return FoxyConfig.CONFIG;
        }

        @Override
        public void save() {
            FoxyConfig.CONFIG.save();
        }
    };

    private FoxyEmbeddiumOptions() {}

    private static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(Foxy.MODID, path);
    }

    @SubscribeEvent
    public static void addFoxyPage(OptionGUIConstructionEvent event) {
        event.addPage(new OptionPage(
                OptionIdentifier.create(Foxy.MODID, "foxy_options"),
                Component.translatable("foxy.options.page"),
                ImmutableList.of(
                        OptionGroup.createBuilder()
                                .setId(id("runtime"))
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("enabled"))
                                        .setName(Component.translatable("foxy.options.enabled.name"))
                                        .setTooltip(Component.translatable("foxy.options.enabled.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.enabled = value, cfg -> cfg.enabled)
                                        .setImpact(OptionImpact.HIGH)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .build())
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("enable_rendering"))
                                        .setName(Component.translatable("foxy.options.enable_rendering.name"))
                                        .setTooltip(Component.translatable("foxy.options.enable_rendering.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.enableRendering = value, cfg -> cfg.enableRendering)
                                        .setImpact(OptionImpact.HIGH)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .build())
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("ingest_enabled"))
                                        .setName(Component.translatable("foxy.options.ingest_enabled.name"))
                                        .setTooltip(Component.translatable("foxy.options.ingest_enabled.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.ingestEnabled = value, cfg -> cfg.ingestEnabled)
                                        .setImpact(OptionImpact.MEDIUM)
                                        .build())
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("auto_backfill_singleplayer"))
                                        .setName(Component.translatable("foxy.options.auto_backfill_singleplayer.name"))
                                        .setTooltip(Component.translatable("foxy.options.auto_backfill_singleplayer.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.autoBackfillSingleplayer = value, cfg -> cfg.autoBackfillSingleplayer)
                                        .setImpact(OptionImpact.MEDIUM)
                                        .build())
                                .build(),
                        OptionGroup.createBuilder()
                                .setId(id("lod"))
                                .add(OptionImpl.createBuilder(int.class, STORAGE)
                                        .setId(id("section_render_distance"))
                                        .setName(Component.translatable("foxy.options.section_render_distance.name"))
                                        .setTooltip(Component.translatable("foxy.options.section_render_distance.tooltip"))
                                        .setControl(option -> new SliderControl(option, 2, 64, 1, value -> Component.translatable("foxy.options.section_render_distance.value", value)))
                                        .setBinding((cfg, value) -> cfg.sectionRenderDistance = value, cfg -> Math.round(cfg.sectionRenderDistance))
                                        .setImpact(OptionImpact.HIGH)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .build())
                                // Vanilla view distance proxy: the binding writes directly into
                                // Minecraft's own options.renderDistance() so the value Foxy
                                // shows here is always the live vanilla value. Embeddium's
                                // built-in Quality page exposes the same field, so users have
                                // a single canonical control regardless of which page they
                                // open. Range matches Embeddium's extended cap (2..32).
                                .add(OptionImpl.createBuilder(int.class, STORAGE)
                                        .setId(id("vanilla_view_distance"))
                                        .setName(Component.translatable("foxy.options.vanilla_view_distance.name"))
                                        .setTooltip(Component.translatable("foxy.options.vanilla_view_distance.tooltip"))
                                        .setControl(option -> new SliderControl(option, 2, 32, 1,
                                                value -> Component.translatable("foxy.options.vanilla_view_distance.value", value)))
                                        .setBinding(
                                                (cfg, value) -> {
                                                    var opts = Minecraft.getInstance().options;
                                                    opts.renderDistance().set(value);
                                                    opts.save();
                                                },
                                                cfg -> Minecraft.getInstance().options.renderDistance().get())
                                        .setImpact(OptionImpact.HIGH)
                                        .build())
                                .add(OptionImpl.createBuilder(int.class, STORAGE)
                                        .setId(id("service_threads"))
                                        .setName(Component.translatable("foxy.options.service_threads.name"))
                                        .setTooltip(Component.translatable("foxy.options.service_threads.tooltip"))
                                        .setControl(option -> new SliderControl(option, 1, 16, 1, ControlValueFormatter.number()))
                                        .setBinding((cfg, value) -> cfg.serviceThreads = value, cfg -> cfg.serviceThreads)
                                        .setImpact(OptionImpact.MEDIUM)
                                        .build())
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("keep_embeddium_builder_threads"))
                                        .setName(Component.translatable("foxy.options.keep_embeddium_builder_threads.name"))
                                        .setTooltip(Component.translatable("foxy.options.keep_embeddium_builder_threads.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.dontUseSodiumBuilderThreads = value, cfg -> cfg.dontUseSodiumBuilderThreads)
                                        .setImpact(OptionImpact.MEDIUM)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                                        .build())
                                .add(OptionImpl.createBuilder(boolean.class, STORAGE)
                                        .setId(id("debug_disable_vanilla_terrain"))
                                        .setName(Component.translatable("foxy.options.debug_disable_vanilla_terrain.name"))
                                        .setTooltip(Component.translatable("foxy.options.debug_disable_vanilla_terrain.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding((cfg, value) -> cfg.debugDisableVanillaTerrain = value, cfg -> cfg.debugDisableVanillaTerrain)
                                        .setImpact(OptionImpact.HIGH)
                                        .build())
                                .build())));
    }

    /**
     * Strips Embeddium's built-in vanilla render-distance slider from every
     * option page once Foxy is providing its own dedicated control on the
     * Foxy page. Walks the live page list, finds any group still carrying an
     * option whose id matches {@link StandardOptions.Option#RENDER_DISTANCE},
     * rebuilds that group and that page without the offending option, and
     * replaces the page in-place. Empty groups are dropped to avoid showing a
     * heading with nothing under it.
     *
     * <p>{@link EventPriority#LOWEST} so we observe the final page list after
     * Embeddium has finished registering its standard pages and any other mod
     * has had a chance to add its own.</p>
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hideVanillaRenderDistance(OptionGUIConstructionEvent event) {
        if (!FoxyConfig.CONFIG.enabled) {
            // When Foxy itself is off, leave the vanilla slider alone so the
            // user isn't stranded without any way to change render distance.
            return;
        }
        List<OptionPage> pages = event.getPages();
        for (int pi = 0; pi < pages.size(); pi++) {
            OptionPage page = pages.get(pi);
            // Skip our own page; the vanilla slider can't be on it.
            if (page.getId() != null && page.getId().getModId().equals(Foxy.MODID)) {
                continue;
            }
            ImmutableList.Builder<OptionGroup> rebuiltGroups = ImmutableList.builder();
            boolean pageChanged = false;
            for (OptionGroup group : page.getGroups()) {
                boolean groupChanged = false;
                OptionGroup.Builder gb = OptionGroup.createBuilder();
                if (group.getId() != null) {
                    gb.setId(group.getId());
                }
                int kept = 0;
                for (Option<?> opt : group.getOptions()) {
                    OptionIdentifier<?> oid = opt.getId();
                    if (oid != null && oid.matches(StandardOptions.Option.RENDER_DISTANCE)) {
                        groupChanged = true;
                        continue;
                    }
                    gb.add(opt);
                    kept++;
                }
                if (groupChanged) {
                    pageChanged = true;
                    if (kept > 0) {
                        rebuiltGroups.add(gb.build());
                    }
                    // If kept == 0 the entire group was just the vanilla VD;
                    // drop it instead of emitting an empty heading.
                } else {
                    rebuiltGroups.add(group);
                }
            }
            if (pageChanged) {
                try {
                    pages.set(pi, new OptionPage(page.getId(), page.getName(), rebuiltGroups.build()));
                } catch (UnsupportedOperationException uoe) {
                    Logger.warn("Foxy: Embeddium pages list is immutable; cannot hide vanilla render"
                            + " distance slider. The Foxy 'vanilla view distance' control still"
                            + " works but the duplicate slider will remain.");
                    return;
                }
            }
        }
    }
}
