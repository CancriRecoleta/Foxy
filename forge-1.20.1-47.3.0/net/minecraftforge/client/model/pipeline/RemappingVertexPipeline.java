//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.pipeline;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RemappingVertexPipeline implements VertexConsumer {
    private static final Set<VertexFormatElement> KNOWN_ELEMENTS;
    private static final int[] EMPTY_INT_ARRAY;
    private final VertexConsumer parent;
    private final VertexFormat targetFormat;
    private final Vector3d position = new Vector3d();
    private final Vector3f normal = new Vector3f();
    private final int[] color = new int[]{255, 255, 255, 255};
    private final float[] uv0 = new float[]{0.0F, 0.0F};
    private final int[] uv1 = new int[]{0, 10};
    private final int[] uv2 = new int[]{0, 0};
    private final Map<VertexFormatElement, Integer> miscElementIds;
    private final int[][] misc;

    public RemappingVertexPipeline(VertexConsumer parent, VertexFormat targetFormat) {
        this.parent = parent;
        this.targetFormat = targetFormat;
        this.miscElementIds = new IdentityHashMap();
        int i = 0;
        UnmodifiableIterator var4 = targetFormat.getElements().iterator();

        while(var4.hasNext()) {
            VertexFormatElement element = (VertexFormatElement)var4.next();
            if (element.getUsage() != Usage.PADDING && !KNOWN_ELEMENTS.contains(element)) {
                this.miscElementIds.put(element, i++);
            }
        }

        this.misc = new int[i][];
        Arrays.fill(this.misc, EMPTY_INT_ARRAY);
    }

    public VertexConsumer vertex(double x, double y, double z) {
        this.position.set(x, y, z);
        return this;
    }

    public VertexConsumer normal(float x, float y, float z) {
        this.normal.set(x, y, z);
        return this;
    }

    public VertexConsumer color(int r, int g, int b, int a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
        return this;
    }

    public VertexConsumer uv(float u, float v) {
        this.uv0[0] = u;
        this.uv0[1] = v;
        return this;
    }

    public VertexConsumer overlayCoords(int u, int v) {
        this.uv1[0] = u;
        this.uv1[1] = v;
        return this;
    }

    public VertexConsumer uv2(int u, int v) {
        this.uv2[0] = u;
        this.uv2[1] = v;
        return this;
    }

    public VertexConsumer misc(VertexFormatElement element, int... values) {
        Integer id = (Integer)this.miscElementIds.get(element);
        if (id != null) {
            this.misc[id] = Arrays.copyOf(values, values.length);
        }

        return this;
    }

    public void endVertex() {
        UnmodifiableIterator var1 = this.targetFormat.getElements().iterator();

        while(var1.hasNext()) {
            VertexFormatElement element = (VertexFormatElement)var1.next();
            if (element.getUsage() != Usage.PADDING) {
                if (element.equals(DefaultVertexFormat.ELEMENT_POSITION)) {
                    this.parent.vertex(this.position.x, this.position.y, this.position.z);
                } else if (element.equals(DefaultVertexFormat.ELEMENT_NORMAL)) {
                    this.parent.normal(this.normal.x(), this.normal.y(), this.normal.z());
                } else if (element.equals(DefaultVertexFormat.ELEMENT_COLOR)) {
                    this.parent.color(this.color[0], this.color[1], this.color[2], this.color[3]);
                } else if (element.equals(DefaultVertexFormat.ELEMENT_UV0)) {
                    this.parent.uv(this.uv0[0], this.uv0[1]);
                } else if (element.equals(DefaultVertexFormat.ELEMENT_UV1)) {
                    this.parent.overlayCoords(this.uv1[0], this.uv1[1]);
                } else if (element.equals(DefaultVertexFormat.ELEMENT_UV2)) {
                    this.parent.uv2(this.uv2[0], this.uv2[1]);
                } else {
                    this.parent.misc(element, this.misc[(Integer)this.miscElementIds.get(element)]);
                }
            }
        }

        this.parent.endVertex();
    }

    public void defaultColor(int r, int g, int b, int a) {
        this.parent.defaultColor(r, g, b, a);
    }

    public void unsetDefaultColor() {
        this.parent.unsetDefaultColor();
    }

    static {
        KNOWN_ELEMENTS = Set.of(DefaultVertexFormat.ELEMENT_POSITION, DefaultVertexFormat.ELEMENT_COLOR, DefaultVertexFormat.ELEMENT_UV, DefaultVertexFormat.ELEMENT_UV1, DefaultVertexFormat.ELEMENT_UV2, DefaultVertexFormat.ELEMENT_NORMAL, DefaultVertexFormat.ELEMENT_PADDING);
        EMPTY_INT_ARRAY = new int[0];
    }
}
