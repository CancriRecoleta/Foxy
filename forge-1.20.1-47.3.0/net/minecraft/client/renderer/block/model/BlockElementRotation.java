//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.block.model;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public record BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
    public BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
        this.origin = origin;
        this.axis = axis;
        this.angle = angle;
        this.rescale = rescale;
    }

    public Vector3f origin() {
        return this.origin;
    }

    public Direction.Axis axis() {
        return this.axis;
    }

    public float angle() {
        return this.angle;
    }

    public boolean rescale() {
        return this.rescale;
    }
}
