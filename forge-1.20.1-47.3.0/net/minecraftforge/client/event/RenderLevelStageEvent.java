//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.joml.Matrix4f;

public class RenderLevelStageEvent extends Event {
    private final Stage stage;
    private final LevelRenderer levelRenderer;
    private final PoseStack poseStack;
    private final Matrix4f projectionMatrix;
    private final int renderTick;
    private final float partialTick;
    private final Camera camera;
    private final Frustum frustum;

    public RenderLevelStageEvent(Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, float partialTick, Camera camera, Frustum frustum) {
        this.stage = stage;
        this.levelRenderer = levelRenderer;
        this.poseStack = poseStack;
        this.projectionMatrix = projectionMatrix;
        this.renderTick = renderTick;
        this.partialTick = partialTick;
        this.camera = camera;
        this.frustum = frustum;
    }

    public Stage getStage() {
        return this.stage;
    }

    public LevelRenderer getLevelRenderer() {
        return this.levelRenderer;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public int getRenderTick() {
        return this.renderTick;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Frustum getFrustum() {
        return this.frustum;
    }

    public static class Stage {
        private static final Map<RenderType, Stage> RENDER_TYPE_STAGES = new HashMap();
        public static final Stage AFTER_SKY = register((String)"after_sky", (RenderType)null);
        public static final Stage AFTER_SOLID_BLOCKS = register("after_solid_blocks", RenderType.solid());
        public static final Stage AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS = register("after_cutout_mipped_blocks", RenderType.cutoutMipped());
        public static final Stage AFTER_CUTOUT_BLOCKS = register("after_cutout_blocks", RenderType.cutout());
        public static final Stage AFTER_ENTITIES = register((String)"after_entities", (RenderType)null);
        public static final Stage AFTER_BLOCK_ENTITIES = register((String)"after_block_entities", (RenderType)null);
        public static final Stage AFTER_TRANSLUCENT_BLOCKS = register("after_translucent_blocks", RenderType.translucent());
        public static final Stage AFTER_TRIPWIRE_BLOCKS = register("after_tripwire_blocks", RenderType.tripwire());
        public static final Stage AFTER_PARTICLES = register((String)"after_particles", (RenderType)null);
        public static final Stage AFTER_WEATHER = register((String)"after_weather", (RenderType)null);
        public static final Stage AFTER_LEVEL = register((String)"after_level", (RenderType)null);
        private final String name;

        private Stage(String name) {
            this.name = name;
        }

        private static Stage register(ResourceLocation name, @Nullable RenderType renderType) throws IllegalArgumentException {
            Stage stage = new Stage(name.toString());
            if (renderType != null && RENDER_TYPE_STAGES.putIfAbsent(renderType, stage) != null) {
                throw new IllegalArgumentException("Attempted to replace an existing RenderLevelStageEvent.Stage for a RenderType: Stage = " + stage + ", RenderType = " + renderType);
            } else {
                return stage;
            }
        }

        private static Stage register(String name, @Nullable RenderType renderType) throws IllegalArgumentException {
            return register(new ResourceLocation(name), renderType);
        }

        public String toString() {
            return this.name;
        }

        @Nullable
        public static Stage fromRenderType(RenderType renderType) {
            return (Stage)RENDER_TYPE_STAGES.get(renderType);
        }
    }

    public static class RegisterStageEvent extends Event implements IModBusEvent {
        public RegisterStageEvent() {
        }

        public Stage register(ResourceLocation name, @Nullable RenderType renderType) throws IllegalArgumentException {
            return net.minecraftforge.client.event.RenderLevelStageEvent.Stage.register(name, renderType);
        }
    }
}
