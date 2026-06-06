//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.animal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IForgeShearable;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class MushroomCow extends Cow implements Shearable, IForgeShearable {
    private static final EntityDataAccessor<String> DATA_TYPE;
    private static final int MUTATE_CHANCE = 1024;
    @Nullable
    private MobEffect effect;
    private int effectDuration;
    @Nullable
    private UUID lastLightningBoltUUID;

    public MushroomCow(EntityType<? extends MushroomCow> p_28914_, Level p_28915_) {
        super(p_28914_, p_28915_);
    }

    public float getWalkTargetValue(BlockPos p_28933_, LevelReader p_28934_) {
        return p_28934_.getBlockState(p_28933_.below()).is(Blocks.MYCELIUM) ? 10.0F : p_28934_.getPathfindingCostFromLightLevels(p_28933_);
    }

    public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> p_218201_, LevelAccessor p_218202_, MobSpawnType p_218203_, BlockPos p_218204_, RandomSource p_218205_) {
        return p_218202_.getBlockState(p_218204_.below()).is(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218202_, p_218204_);
    }

    public void thunderHit(ServerLevel p_28921_, LightningBolt p_28922_) {
        UUID uuid = p_28922_.getUUID();
        if (!uuid.equals(this.lastLightningBoltUUID)) {
            this.setVariant(this.getVariant() == net.minecraft.world.entity.animal.MushroomCow.MushroomType.RED ? net.minecraft.world.entity.animal.MushroomCow.MushroomType.BROWN : net.minecraft.world.entity.animal.MushroomCow.MushroomType.RED);
            this.lastLightningBoltUUID = uuid;
            this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
        }

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TYPE, net.minecraft.world.entity.animal.MushroomCow.MushroomType.RED.type);
    }

    public InteractionResult mobInteract(Player p_28941_, InteractionHand p_28942_) {
        ItemStack itemstack = p_28941_.getItemInHand(p_28942_);
        if (itemstack.is(Items.BOWL) && !this.isBaby()) {
            boolean flag = false;
            ItemStack itemstack1;
            if (this.effect != null) {
                flag = true;
                itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.saveMobEffect(itemstack1, this.effect, this.effectDuration);
                this.effect = null;
                this.effectDuration = 0;
            } else {
                itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
            }

            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, p_28941_, itemstack1, false);
            p_28941_.setItemInHand(p_28942_, itemstack2);
            SoundEvent soundevent;
            if (flag) {
                soundevent = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
            } else {
                soundevent = SoundEvents.MOOSHROOM_MILK;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (this.getVariant() == net.minecraft.world.entity.animal.MushroomCow.MushroomType.BROWN && itemstack.is(ItemTags.SMALL_FLOWERS)) {
            if (this.effect != null) {
                for(int i = 0; i < 2; ++i) {
                    this.level().addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
            } else {
                Optional<Pair<MobEffect, Integer>> optional = this.getEffectFromItemStack(itemstack);
                if (!optional.isPresent()) {
                    return InteractionResult.PASS;
                }

                Pair<MobEffect, Integer> pair = (Pair)optional.get();
                if (!p_28941_.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                for(int j = 0; j < 4; ++j) {
                    this.level().addParticle(ParticleTypes.EFFECT, this.getX() + this.random.nextDouble() / 2.0, this.getY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }

                this.effect = (MobEffect)pair.getLeft();
                this.effectDuration = (Integer)pair.getRight();
                this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(p_28941_, p_28942_);
        }
    }

    public List<ItemStack> onSheared(@org.jetbrains.annotations.Nullable Player player, @NotNull ItemStack item, Level world, BlockPos pos, int fortune) {
        this.gameEvent(GameEvent.SHEAR, player);
        return this.shearInternal(player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS);
    }

    public void shear(SoundSource p_28924_) {
        this.shearInternal(p_28924_).forEach((s) -> {
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(1.0), this.getZ(), s));
        });
    }

    private List<ItemStack> shearInternal(SoundSource p_28924_) {
        this.level().playSound((Player)((Player)null), (Entity)this, SoundEvents.MOOSHROOM_SHEAR, p_28924_, 1.0F, 1.0F);
        if (!this.level().isClientSide()) {
            Cow cow = (Cow)EntityType.COW.create(this.level());
            if (cow != null) {
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                this.discard();
                cow.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                cow.setHealth(this.getHealth());
                cow.yBodyRot = this.yBodyRot;
                if (this.hasCustomName()) {
                    cow.setCustomName(this.getCustomName());
                    cow.setCustomNameVisible(this.isCustomNameVisible());
                }

                if (this.isPersistenceRequired()) {
                    cow.setPersistenceRequired();
                }

                cow.setInvulnerable(this.isInvulnerable());
                this.level().addFreshEntity(cow);
                List<ItemStack> items = new ArrayList();

                for(int i = 0; i < 5; ++i) {
                    items.add(new ItemStack(this.getVariant().blockState.getBlock()));
                }

                return items;
            }
        }

        return Collections.emptyList();
    }

    public boolean readyForShearing() {
        return this.isAlive() && !this.isBaby();
    }

    public void addAdditionalSaveData(CompoundTag p_28944_) {
        super.addAdditionalSaveData(p_28944_);
        p_28944_.putString("Type", this.getVariant().getSerializedName());
        if (this.effect != null) {
            p_28944_.putInt("EffectId", MobEffect.getId(this.effect));
            ForgeHooks.saveMobEffect(p_28944_, "forge:effect_id", this.effect);
            p_28944_.putInt("EffectDuration", this.effectDuration);
        }

    }

    public void readAdditionalSaveData(CompoundTag p_28936_) {
        super.readAdditionalSaveData(p_28936_);
        this.setVariant(net.minecraft.world.entity.animal.MushroomCow.MushroomType.byType(p_28936_.getString("Type")));
        if (p_28936_.contains("EffectId", 99)) {
            this.effect = MobEffect.byId(p_28936_.getInt("EffectId"));
            this.effect = ForgeHooks.loadMobEffect(p_28936_, "forge:effect_id", this.effect);
        }

        if (p_28936_.contains("EffectDuration", 99)) {
            this.effectDuration = p_28936_.getInt("EffectDuration");
        }

    }

    private Optional<Pair<MobEffect, Integer>> getEffectFromItemStack(ItemStack p_28957_) {
        SuspiciousEffectHolder suspiciouseffectholder = SuspiciousEffectHolder.tryGet(p_28957_.getItem());
        return suspiciouseffectholder != null ? Optional.of(Pair.of(suspiciouseffectholder.getSuspiciousEffect(), suspiciouseffectholder.getEffectDuration())) : Optional.empty();
    }

    public void setVariant(MushroomType p_28929_) {
        this.entityData.set(DATA_TYPE, p_28929_.type);
    }

    public MushroomType getVariant() {
        return net.minecraft.world.entity.animal.MushroomCow.MushroomType.byType((String)this.entityData.get(DATA_TYPE));
    }

    @Nullable
    public MushroomCow getBreedOffspring(ServerLevel p_148942_, AgeableMob p_148943_) {
        MushroomCow mushroomcow = (MushroomCow)EntityType.MOOSHROOM.create(p_148942_);
        if (mushroomcow != null) {
            mushroomcow.setVariant(this.getOffspringType((MushroomCow)p_148943_));
        }

        return mushroomcow;
    }

    private MushroomType getOffspringType(MushroomCow p_28931_) {
        MushroomType mushroomcow$mushroomtype = this.getVariant();
        MushroomType mushroomcow$mushroomtype1 = p_28931_.getVariant();
        MushroomType mushroomcow$mushroomtype2;
        if (mushroomcow$mushroomtype == mushroomcow$mushroomtype1 && this.random.nextInt(1024) == 0) {
            mushroomcow$mushroomtype2 = mushroomcow$mushroomtype == net.minecraft.world.entity.animal.MushroomCow.MushroomType.BROWN ? net.minecraft.world.entity.animal.MushroomCow.MushroomType.RED : net.minecraft.world.entity.animal.MushroomCow.MushroomType.BROWN;
        } else {
            mushroomcow$mushroomtype2 = this.random.nextBoolean() ? mushroomcow$mushroomtype : mushroomcow$mushroomtype1;
        }

        return mushroomcow$mushroomtype2;
    }

    public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
        return this.readyForShearing();
    }

    static {
        DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
    }

    public static enum MushroomType implements StringRepresentable {
        RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

        public static final StringRepresentable.EnumCodec<MushroomType> CODEC = StringRepresentable.fromEnum(MushroomType::values);
        final String type;
        final BlockState blockState;

        private MushroomType(String p_28967_, BlockState p_28968_) {
            this.type = p_28967_;
            this.blockState = p_28968_;
        }

        public BlockState getBlockState() {
            return this.blockState;
        }

        public String getSerializedName() {
            return this.type;
        }

        static MushroomType byType(String p_28977_) {
            return (MushroomType)CODEC.byName(p_28977_, RED);
        }
    }
}
