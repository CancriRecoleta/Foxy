//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.renderable;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class CompositeRenderable implements IRenderable<Transforms> {
    private final List<Component> components = new ArrayList();

    private CompositeRenderable() {
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, float partialTick, Transforms context) {
        Iterator var8 = this.components.iterator();

        while(var8.hasNext()) {
            Component component = (Component)var8.next();
            component.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Component {
        private final String name;
        private final List<Component> children = new ArrayList();
        private final List<Mesh> meshes = new ArrayList();

        public Component(String name) {
            this.name = name;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay, Transforms context) {
            Matrix4f matrix = context.getTransform(this.name);
            if (matrix != null) {
                poseStack.pushPose();
                poseStack.mulPoseMatrix(matrix);
            }

            Iterator var8 = this.children.iterator();

            while(var8.hasNext()) {
                Component part = (Component)var8.next();
                part.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay, context);
            }

            var8 = this.meshes.iterator();

            while(var8.hasNext()) {
                Mesh mesh = (Mesh)var8.next();
                mesh.render(poseStack, bufferSource, textureRenderTypeLookup, lightmap, overlay);
            }

            if (matrix != null) {
                poseStack.popPose();
            }

        }
    }

    public static class Transforms {
        public static final Transforms EMPTY = new Transforms(ImmutableMap.of());
        private final ImmutableMap<String, Matrix4f> parts;

        public static Transforms of(ImmutableMap<String, Matrix4f> parts) {
            return new Transforms(parts);
        }

        private Transforms(ImmutableMap<String, Matrix4f> parts) {
            this.parts = parts;
        }

        public @Nullable Matrix4f getTransform(String part) {
            return (Matrix4f)this.parts.get(part);
        }
    }

    public static class Builder {
        private final CompositeRenderable renderable = new CompositeRenderable();

        private Builder() {
        }

        public PartBuilder<Builder> child(String name) {
            Component child = new Component(name);
            this.renderable.components.add(child);
            return new PartBuilder(this, child);
        }

        public CompositeRenderable get() {
            return this.renderable;
        }
    }

    public static class PartBuilder<T> {
        private final T parent;
        private final Component component;

        private PartBuilder(T parent, Component component) {
            this.parent = parent;
            this.component = component;
        }

        public PartBuilder<PartBuilder<T>> child(String name) {
            Component child = new Component(this.component.name + "/" + name);
            this.component.children.add(child);
            return new PartBuilder(this, child);
        }

        public PartBuilder<T> addMesh(ResourceLocation texture, List<BakedQuad> quads) {
            Mesh mesh = new Mesh(texture);
            mesh.quads.addAll(quads);
            this.component.meshes.add(mesh);
            return this;
        }

        public T end() {
            return this.parent;
        }
    }

    private static class Mesh {
        private final ResourceLocation texture;
        private final List<BakedQuad> quads = new ArrayList();

        public Mesh(ResourceLocation texture) {
            this.texture = texture;
        }

        public void render(PoseStack poseStack, MultiBufferSource bufferSource, ITextureRenderTypeLookup textureRenderTypeLookup, int lightmap, int overlay) {
            VertexConsumer consumer = bufferSource.getBuffer(textureRenderTypeLookup.get(this.texture));
            Iterator var7 = this.quads.iterator();

            while(var7.hasNext()) {
                BakedQuad quad = (BakedQuad)var7.next();
                consumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, 1.0F, lightmap, overlay, true);
            }

        }
    }
}
