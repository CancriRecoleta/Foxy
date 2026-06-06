//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class EndGatewayBlock extends BaseEntityBlock {
    public EndGatewayBlock(BlockBehaviour.Properties p_52999_) {
        super(p_52999_);
    }

    public BlockEntity newBlockEntity(BlockPos p_153193_, BlockState p_153194_) {
        return new TheEndGatewayBlockEntity(p_153193_, p_153194_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153189_, BlockState p_153190_, BlockEntityType<T> p_153191_) {
        return createTickerHelper(p_153191_, BlockEntityType.END_GATEWAY, p_153189_.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::teleportTick);
    }

    public void animateTick(BlockState p_221097_, Level p_221098_, BlockPos p_221099_, RandomSource p_221100_) {
        BlockEntity $$4 = p_221098_.getBlockEntity(p_221099_);
        if ($$4 instanceof TheEndGatewayBlockEntity) {
            int $$5 = ((TheEndGatewayBlockEntity)$$4).getParticleAmount();

            for(int $$6 = 0; $$6 < $$5; ++$$6) {
                double $$7 = (double)p_221099_.getX() + p_221100_.nextDouble();
                double $$8 = (double)p_221099_.getY() + p_221100_.nextDouble();
                double $$9 = (double)p_221099_.getZ() + p_221100_.nextDouble();
                double $$10 = (p_221100_.nextDouble() - 0.5) * 0.5;
                double $$11 = (p_221100_.nextDouble() - 0.5) * 0.5;
                double $$12 = (p_221100_.nextDouble() - 0.5) * 0.5;
                int $$13 = p_221100_.nextInt(2) * 2 - 1;
                if (p_221100_.nextBoolean()) {
                    $$9 = (double)p_221099_.getZ() + 0.5 + 0.25 * (double)$$13;
                    $$12 = (double)(p_221100_.nextFloat() * 2.0F * (float)$$13);
                } else {
                    $$7 = (double)p_221099_.getX() + 0.5 + 0.25 * (double)$$13;
                    $$10 = (double)(p_221100_.nextFloat() * 2.0F * (float)$$13);
                }

                p_221098_.addParticle(ParticleTypes.PORTAL, $$7, $$8, $$9, $$10, $$11, $$12);
            }

        }
    }

    public ItemStack getCloneItemStack(BlockGetter p_53003_, BlockPos p_53004_, BlockState p_53005_) {
        return ItemStack.EMPTY;
    }

    public boolean canBeReplaced(BlockState p_53012_, Fluid p_53013_) {
        return false;
    }
}
