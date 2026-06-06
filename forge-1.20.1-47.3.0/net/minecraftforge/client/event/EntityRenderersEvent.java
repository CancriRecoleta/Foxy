//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class EntityRenderersEvent extends Event implements IModBusEvent {
    @Internal
    protected EntityRenderersEvent() {
    }

    public static class CreateSkullModels extends EntityRenderersEvent {
        private final ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder;
        private final EntityModelSet entityModelSet;

        @Internal
        public CreateSkullModels(ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder, EntityModelSet entityModelSet) {
            this.builder = builder;
            this.entityModelSet = entityModelSet;
        }

        public EntityModelSet getEntityModelSet() {
            return this.entityModelSet;
        }

        public void registerSkullModel(SkullBlock.Type type, SkullModelBase model) {
            this.builder.put(type, model);
        }
    }

    public static class AddLayers extends EntityRenderersEvent {
        private final Map<EntityType<?>, EntityRenderer<?>> renderers;
        private final Map<String, EntityRenderer<? extends Player>> skinMap;
        private final EntityRendererProvider.Context context;

        @Internal
        public AddLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<String, EntityRenderer<? extends Player>> playerRenderers, EntityRendererProvider.Context context) {
            this.renderers = renderers;
            this.skinMap = playerRenderers;
            this.context = context;
        }

        public Set<String> getSkins() {
            return this.skinMap.keySet();
        }

        public <R extends LivingEntityRenderer<? extends Player, ? extends EntityModel<? extends Player>>> @Nullable R getSkin(String skinName) {
            return (LivingEntityRenderer)this.skinMap.get(skinName);
        }

        public <T extends LivingEntity, R extends LivingEntityRenderer<T, ? extends EntityModel<T>>> @Nullable R getRenderer(EntityType<? extends T> entityType) {
            return (LivingEntityRenderer)this.renderers.get(entityType);
        }

        public EntityModelSet getEntityModels() {
            return this.context.getModelSet();
        }

        public EntityRendererProvider.Context getContext() {
            return this.context;
        }
    }

    public static class RegisterRenderers extends EntityRenderersEvent {
        @Internal
        public RegisterRenderers() {
        }

        public <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
            EntityRenderers.register(entityType, entityRendererProvider);
        }

        public <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<? extends T> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
            BlockEntityRenderers.register(blockEntityType, blockEntityRendererProvider);
        }
    }

    public static class RegisterLayerDefinitions extends EntityRenderersEvent {
        @Internal
        public RegisterLayerDefinitions() {
        }

        public void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
            ForgeHooksClient.registerLayerDefinition(layerLocation, supplier);
        }
    }
}
