//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class BrushItem extends Item {
    public static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    private static final double MAX_BRUSH_DISTANCE;

    public BrushItem(Item.Properties p_272907_) {
        super(p_272907_);
    }

    public InteractionResult useOn(UseOnContext p_272607_) {
        Player $$1 = p_272607_.getPlayer();
        if ($$1 != null && this.calculateHitResult($$1).getType() == Type.BLOCK) {
            $$1.startUsingItem(p_272607_.getHand());
        }

        return InteractionResult.CONSUME;
    }

    public UseAnim getUseAnimation(ItemStack p_273490_) {
        return UseAnim.BRUSH;
    }

    public int getUseDuration(ItemStack p_272765_) {
        return 200;
    }

    public void onUseTick(Level p_273467_, LivingEntity p_273619_, ItemStack p_273316_, int p_273101_) {
        if (p_273101_ >= 0 && p_273619_ instanceof Player $$5) {
            HitResult $$6 = this.calculateHitResult(p_273619_);
            if ($$6 instanceof BlockHitResult $$8) {
                if ($$6.getType() == Type.BLOCK) {
                    int $$9 = this.getUseDuration(p_273316_) - p_273101_ + 1;
                    boolean $$10 = $$9 % 10 == 5;
                    if ($$10) {
                        BlockPos $$11 = $$8.getBlockPos();
                        BlockState $$12 = p_273467_.getBlockState($$11);
                        HumanoidArm $$13 = p_273619_.getUsedItemHand() == InteractionHand.MAIN_HAND ? $$5.getMainArm() : $$5.getMainArm().getOpposite();
                        this.spawnDustParticles(p_273467_, $$8, $$12, p_273619_.getViewVector(0.0F), $$13);
                        Block var15 = $$12.getBlock();
                        SoundEvent $$16;
                        if (var15 instanceof BrushableBlock) {
                            BrushableBlock $$14 = (BrushableBlock)var15;
                            $$16 = $$14.getBrushSound();
                        } else {
                            $$16 = SoundEvents.BRUSH_GENERIC;
                        }

                        p_273467_.playSound($$5, $$11, $$16, SoundSource.BLOCKS);
                        if (!p_273467_.isClientSide()) {
                            BlockEntity var18 = p_273467_.getBlockEntity($$11);
                            if (var18 instanceof BrushableBlockEntity) {
                                BrushableBlockEntity $$17 = (BrushableBlockEntity)var18;
                                boolean $$18 = $$17.brush(p_273467_.getGameTime(), $$5, $$8.getDirection());
                                if ($$18) {
                                    EquipmentSlot $$19 = p_273316_.equals($$5.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                                    p_273316_.hurtAndBreak(1, p_273619_, (p_279044_) -> {
                                        p_279044_.broadcastBreakEvent($$19);
                                    });
                                }
                            }
                        }
                    }

                    return;
                }
            }

            p_273619_.releaseUsingItem();
        } else {
            p_273619_.releaseUsingItem();
        }
    }

    private HitResult calculateHitResult(LivingEntity p_281264_) {
        return ProjectileUtil.getHitResultOnViewVector(p_281264_, (p_281111_) -> {
            return !p_281111_.isSpectator() && p_281111_.isPickable();
        }, MAX_BRUSH_DISTANCE);
    }

    public void spawnDustParticles(Level p_278327_, BlockHitResult p_278272_, BlockState p_278235_, Vec3 p_278337_, HumanoidArm p_285071_) {
        double $$5 = 3.0;
        int $$6 = p_285071_ == HumanoidArm.RIGHT ? 1 : -1;
        int $$7 = p_278327_.getRandom().nextInt(7, 12);
        BlockParticleOption $$8 = new BlockParticleOption(ParticleTypes.BLOCK, p_278235_);
        Direction $$9 = p_278272_.getDirection();
        DustParticlesDelta $$10 = net.minecraft.world.item.BrushItem.DustParticlesDelta.fromDirection(p_278337_, $$9);
        Vec3 $$11 = p_278272_.getLocation();

        for(int $$12 = 0; $$12 < $$7; ++$$12) {
            p_278327_.addParticle($$8, $$11.x - (double)($$9 == Direction.WEST ? 1.0E-6F : 0.0F), $$11.y, $$11.z - (double)($$9 == Direction.NORTH ? 1.0E-6F : 0.0F), $$10.xd() * (double)$$6 * 3.0 * p_278327_.getRandom().nextDouble(), 0.0, $$10.zd() * (double)$$6 * 3.0 * p_278327_.getRandom().nextDouble());
        }

    }

    static {
        MAX_BRUSH_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0;
    }

    static record DustParticlesDelta(double xd, double yd, double zd) {
        private static final double ALONG_SIDE_DELTA = 1.0;
        private static final double OUT_FROM_SIDE_DELTA = 0.1;

        private DustParticlesDelta(double xd, double yd, double zd) {
            this.xd = xd;
            this.yd = yd;
            this.zd = zd;
        }

        public static DustParticlesDelta fromDirection(Vec3 p_273421_, Direction p_272987_) {
            double $$2 = 0.0;
            DustParticlesDelta var10000;
            switch (p_272987_) {
                case DOWN:
                case UP:
                    var10000 = new DustParticlesDelta(p_273421_.z(), 0.0, -p_273421_.x());
                    break;
                case NORTH:
                    var10000 = new DustParticlesDelta(1.0, 0.0, -0.1);
                    break;
                case SOUTH:
                    var10000 = new DustParticlesDelta(-1.0, 0.0, 0.1);
                    break;
                case WEST:
                    var10000 = new DustParticlesDelta(-0.1, 0.0, -1.0);
                    break;
                case EAST:
                    var10000 = new DustParticlesDelta(0.1, 0.0, 1.0);
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public double xd() {
            return this.xd;
        }

        public double yd() {
            return this.yd;
        }

        public double zd() {
            return this.zd;
        }
    }
}
