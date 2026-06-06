//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.ElementsModel.Loader;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
    value = {Dist.CLIENT},
    bus = Bus.MOD,
    modid = "forge"
)
public class ClientForgeMod {
    public ClientForgeMod() {
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("empty", EmptyModel.LOADER);
        event.register("elements", Loader.INSTANCE);
        event.register("obj", ObjLoader.INSTANCE);
        event.register("fluid_container", net.minecraftforge.client.model.DynamicFluidContainerModel.Loader.INSTANCE);
        event.register("composite", net.minecraftforge.client.model.CompositeModel.Loader.INSTANCE);
        event.register("item_layers", net.minecraftforge.client.model.ItemLayerModel.Loader.INSTANCE);
        event.register("separate_transforms", net.minecraftforge.client.model.SeparateTransformsModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ObjLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register("item_unlit", RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());
    }
}
