//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Containers {
    public Containers() {
    }

    public static void dropContents(Level p_19003_, BlockPos p_19004_, Container p_19005_) {
        dropContents(p_19003_, (double)p_19004_.getX(), (double)p_19004_.getY(), (double)p_19004_.getZ(), p_19005_);
    }

    public static void dropContents(Level p_18999_, Entity p_19000_, Container p_19001_) {
        dropContents(p_18999_, p_19000_.getX(), p_19000_.getY(), p_19000_.getZ(), p_19001_);
    }

    private static void dropContents(Level p_18987_, double p_18988_, double p_18989_, double p_18990_, Container p_18991_) {
        for(int $$5 = 0; $$5 < p_18991_.getContainerSize(); ++$$5) {
            dropItemStack(p_18987_, p_18988_, p_18989_, p_18990_, p_18991_.getItem($$5));
        }

    }

    public static void dropContents(Level p_19011_, BlockPos p_19012_, NonNullList<ItemStack> p_19013_) {
        p_19013_.forEach((p_19009_) -> {
            dropItemStack(p_19011_, (double)p_19012_.getX(), (double)p_19012_.getY(), (double)p_19012_.getZ(), p_19009_);
        });
    }

    public static void dropItemStack(Level p_18993_, double p_18994_, double p_18995_, double p_18996_, ItemStack p_18997_) {
        double $$5 = (double)EntityType.ITEM.getWidth();
        double $$6 = 1.0 - $$5;
        double $$7 = $$5 / 2.0;
        double $$8 = Math.floor(p_18994_) + p_18993_.random.nextDouble() * $$6 + $$7;
        double $$9 = Math.floor(p_18995_) + p_18993_.random.nextDouble() * $$6;
        double $$10 = Math.floor(p_18996_) + p_18993_.random.nextDouble() * $$6 + $$7;

        while(!p_18997_.isEmpty()) {
            ItemEntity $$11 = new ItemEntity(p_18993_, $$8, $$9, $$10, p_18997_.split(p_18993_.random.nextInt(21) + 10));
            float $$12 = 0.05F;
            $$11.setDeltaMovement(p_18993_.random.triangle(0.0, 0.11485000171139836), p_18993_.random.triangle(0.2, 0.11485000171139836), p_18993_.random.triangle(0.0, 0.11485000171139836));
            p_18993_.addFreshEntity($$11);
        }

    }
}
