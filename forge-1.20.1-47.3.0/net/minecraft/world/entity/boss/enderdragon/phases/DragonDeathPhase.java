//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;
    private int time;

    public DragonDeathPhase(EnderDragon p_31217_) {
        super(p_31217_);
    }

    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            float $$0 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
            float $$1 = (this.dragon.getRandom().nextFloat() - 0.5F) * 4.0F;
            float $$2 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
            this.dragon.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getX() + (double)$$0, this.dragon.getY() + 2.0 + (double)$$1, this.dragon.getZ() + (double)$$2, 0.0, 0.0, 0.0);
        }

    }

    public void doServerTick() {
        ++this.time;
        if (this.targetLocation == null) {
            BlockPos $$0 = this.dragon.level().getHeightmapPos(Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.dragon.getFightOrigin()));
            this.targetLocation = Vec3.atBottomCenterOf($$0);
        }

        double $$1 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (!($$1 < 100.0) && !($$1 > 22500.0) && !this.dragon.horizontalCollision && !this.dragon.verticalCollision) {
            this.dragon.setHealth(1.0F);
        } else {
            this.dragon.setHealth(0.0F);
        }

    }

    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }

    public float getFlySpeed() {
        return 3.0F;
    }

    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonDeathPhase> getPhase() {
        return EnderDragonPhase.DYING;
    }
}
