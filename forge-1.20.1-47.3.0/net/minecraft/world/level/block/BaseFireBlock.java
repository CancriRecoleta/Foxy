//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class BaseFireBlock extends Block {
    private static final int SECONDS_ON_FIRE = 8;
    private final float fireDamage;
    protected static final float AABB_OFFSET = 1.0F;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public BaseFireBlock(BlockBehaviour.Properties p_49241_, float p_49242_) {
        super(p_49241_);
        this.fireDamage = p_49242_;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_49244_) {
        return getState(p_49244_.getLevel(), p_49244_.getClickedPos());
    }

    public static BlockState getState(BlockGetter p_49246_, BlockPos p_49247_) {
        BlockPos blockpos = p_49247_.below();
        BlockState blockstate = p_49246_.getBlockState(blockpos);
        return SoulFireBlock.canSurviveOnBlock(blockstate) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(p_49246_, p_49247_);
    }

    public VoxelShape getShape(BlockState p_49274_, BlockGetter p_49275_, BlockPos p_49276_, CollisionContext p_49277_) {
        return DOWN_AABB;
    }

    public void animateTick(BlockState p_220763_, Level p_220764_, BlockPos p_220765_, RandomSource p_220766_) {
        if (p_220766_.nextInt(24) == 0) {
            p_220764_.playLocalSound((double)p_220765_.getX() + 0.5, (double)p_220765_.getY() + 0.5, (double)p_220765_.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_220766_.nextFloat(), p_220766_.nextFloat() * 0.7F + 0.3F, false);
        }

        BlockPos blockpos = p_220765_.below();
        BlockState blockstate = p_220764_.getBlockState(blockpos);
        int j1;
        double d7;
        double d12;
        double d17;
        if (!this.canBurn(blockstate) && !blockstate.isFaceSturdy(p_220764_, blockpos, Direction.UP)) {
            if (this.canBurn(p_220764_.getBlockState(p_220765_.west()))) {
                for(j1 = 0; j1 < 2; ++j1) {
                    d7 = (double)p_220765_.getX() + p_220766_.nextDouble() * 0.10000000149011612;
                    d12 = (double)p_220765_.getY() + p_220766_.nextDouble();
                    d17 = (double)p_220765_.getZ() + p_220766_.nextDouble();
                    p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(p_220764_.getBlockState(p_220765_.east()))) {
                for(j1 = 0; j1 < 2; ++j1) {
                    d7 = (double)(p_220765_.getX() + 1) - p_220766_.nextDouble() * 0.10000000149011612;
                    d12 = (double)p_220765_.getY() + p_220766_.nextDouble();
                    d17 = (double)p_220765_.getZ() + p_220766_.nextDouble();
                    p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(p_220764_.getBlockState(p_220765_.north()))) {
                for(j1 = 0; j1 < 2; ++j1) {
                    d7 = (double)p_220765_.getX() + p_220766_.nextDouble();
                    d12 = (double)p_220765_.getY() + p_220766_.nextDouble();
                    d17 = (double)p_220765_.getZ() + p_220766_.nextDouble() * 0.10000000149011612;
                    p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(p_220764_.getBlockState(p_220765_.south()))) {
                for(j1 = 0; j1 < 2; ++j1) {
                    d7 = (double)p_220765_.getX() + p_220766_.nextDouble();
                    d12 = (double)p_220765_.getY() + p_220766_.nextDouble();
                    d17 = (double)(p_220765_.getZ() + 1) - p_220766_.nextDouble() * 0.10000000149011612;
                    p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }

            if (this.canBurn(p_220764_.getBlockState(p_220765_.above()))) {
                for(j1 = 0; j1 < 2; ++j1) {
                    d7 = (double)p_220765_.getX() + p_220766_.nextDouble();
                    d12 = (double)(p_220765_.getY() + 1) - p_220766_.nextDouble() * 0.10000000149011612;
                    d17 = (double)p_220765_.getZ() + p_220766_.nextDouble();
                    p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
            }
        } else {
            for(j1 = 0; j1 < 3; ++j1) {
                d7 = (double)p_220765_.getX() + p_220766_.nextDouble();
                d12 = (double)p_220765_.getY() + p_220766_.nextDouble() * 0.5 + 0.5;
                d17 = (double)p_220765_.getZ() + p_220766_.nextDouble();
                p_220764_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
            }
        }

    }

    protected abstract boolean canBurn(BlockState var1);

    public void entityInside(BlockState p_49260_, Level p_49261_, BlockPos p_49262_, Entity p_49263_) {
        if (!p_49263_.fireImmune()) {
            p_49263_.setRemainingFireTicks(p_49263_.getRemainingFireTicks() + 1);
            if (p_49263_.getRemainingFireTicks() == 0) {
                p_49263_.setSecondsOnFire(8);
            }
        }

        p_49263_.hurt(p_49261_.damageSources().inFire(), this.fireDamage);
        super.entityInside(p_49260_, p_49261_, p_49262_, p_49263_);
    }

    public void onPlace(BlockState p_49279_, Level p_49280_, BlockPos p_49281_, BlockState p_49282_, boolean p_49283_) {
        if (!p_49282_.is(p_49279_.getBlock())) {
            if (inPortalDimension(p_49280_)) {
                Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(p_49280_, p_49281_, Axis.X);
                optional = ForgeEventFactory.onTrySpawnPortal(p_49280_, p_49281_, optional);
                if (optional.isPresent()) {
                    ((PortalShape)optional.get()).createPortalBlocks();
                    return;
                }
            }

            if (!p_49279_.canSurvive(p_49280_, p_49281_)) {
                p_49280_.removeBlock(p_49281_, false);
            }
        }

    }

    private static boolean inPortalDimension(Level p_49249_) {
        return p_49249_.dimension() == Level.OVERWORLD || p_49249_.dimension() == Level.NETHER;
    }

    protected void spawnDestroyParticles(Level p_152139_, Player p_152140_, BlockPos p_152141_, BlockState p_152142_) {
    }

    public void playerWillDestroy(Level p_49251_, BlockPos p_49252_, BlockState p_49253_, Player p_49254_) {
        if (!p_49251_.isClientSide()) {
            p_49251_.levelEvent((Player)null, 1009, p_49252_, 0);
        }

        super.playerWillDestroy(p_49251_, p_49252_, p_49253_, p_49254_);
    }

    public static boolean canBePlacedAt(Level p_49256_, BlockPos p_49257_, Direction p_49258_) {
        BlockState blockstate = p_49256_.getBlockState(p_49257_);
        if (!blockstate.isAir()) {
            return false;
        } else {
            return getState(p_49256_, p_49257_).canSurvive(p_49256_, p_49257_) || isPortal(p_49256_, p_49257_, p_49258_);
        }
    }

    private static boolean isPortal(Level p_49270_, BlockPos p_49271_, Direction p_49272_) {
        if (!inPortalDimension(p_49270_)) {
            return false;
        } else {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = p_49271_.mutable();
            boolean flag = false;
            Direction[] var5 = Direction.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Direction direction = var5[var7];
                if (p_49270_.getBlockState(blockpos$mutableblockpos.set(p_49271_).move(direction)).isPortalFrame(p_49270_, blockpos$mutableblockpos)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            } else {
                Direction.Axis direction$axis = p_49272_.getAxis().isHorizontal() ? p_49272_.getCounterClockWise().getAxis() : Plane.HORIZONTAL.getRandomAxis(p_49270_.random);
                return PortalShape.findEmptyPortalShape(p_49270_, p_49271_, direction$axis).isPresent();
            }
        }
    }
}
