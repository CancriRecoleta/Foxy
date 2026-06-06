//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonLandingPhase extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;

    public DragonLandingPhase(EnderDragon p_31305_) {
        super(p_31305_);
    }

    public void doClientTick() {
        Vec3 $$0 = this.dragon.getHeadLookVector(1.0F).normalize();
        $$0.yRot(-0.7853982F);
        double $$1 = this.dragon.head.getX();
        double $$2 = this.dragon.head.getY(0.5);
        double $$3 = this.dragon.head.getZ();

        for(int $$4 = 0; $$4 < 8; ++$$4) {
            RandomSource $$5 = this.dragon.getRandom();
            double $$6 = $$1 + $$5.nextGaussian() / 2.0;
            double $$7 = $$2 + $$5.nextGaussian() / 2.0;
            double $$8 = $$3 + $$5.nextGaussian() / 2.0;
            Vec3 $$9 = this.dragon.getDeltaMovement();
            this.dragon.level().addParticle(ParticleTypes.DRAGON_BREATH, $$6, $$7, $$8, -$$0.x * 0.07999999821186066 + $$9.x, -$$0.y * 0.30000001192092896 + $$9.y, -$$0.z * 0.07999999821186066 + $$9.z);
            $$0.yRot(0.19634955F);
        }

    }

    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = Vec3.atBottomCenterOf(this.dragon.level().getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.dragon.getFightOrigin())));
        }

        if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
            ((DragonSittingFlamingPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.SITTING_FLAMING)).resetFlameCount();
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
        }

    }

    public float getFlySpeed() {
        return 1.5F;
    }

    public float getTurnSpeed() {
        float $$0 = (float)this.dragon.getDeltaMovement().horizontalDistance() + 1.0F;
        float $$1 = Math.min($$0, 40.0F);
        return $$1 / $$0;
    }

    public void begin() {
        this.targetLocation = null;
    }

    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonLandingPhase> getPhase() {
        return EnderDragonPhase.LANDING;
    }
}
