package com.github.foxy.client;

import com.google.common.collect.ImmutableList;
import com.github.foxy.Foxy;
import com.github.foxy.client.config.FoxyConfig;
import me.jellysquid.mods.sodium.client.gui.options.OptionFlag;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlValueFormatter;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;

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
                                .build(),
                        OptionGroup.createBuilder()
                                .setId(id("lod"))
                                .add(OptionImpl.createBuilder(int.class, STORAGE)
                                        .setId(id("section_render_distance"))
                                        .setName(Component.translatable("foxy.options.section_render_distance.name"))
                                        .setTooltip(Component.translatable("foxy.options.section_render_distance.tooltip"))
                                        .setControl(option -> new SliderControl(option, 2, 96, 1, value -> Component.translatable("foxy.options.section_render_distance.value", value)))
                                        .setBinding((cfg, value) -> cfg.sectionRenderDistance = value, cfg -> Math.round(cfg.sectionRenderDistance))
                                        .setImpact(OptionImpact.HIGH)
                                        .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
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
                                .build())));
    }
}
