//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.ticket;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AABBTicket extends SimpleTicket<Vec3> {
    public final @NotNull AABB axisAlignedBB;

    public AABBTicket(@NotNull AABB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
    }

    public boolean matches(Vec3 toMatch) {
        return this.axisAlignedBB.contains(toMatch);
    }
}
