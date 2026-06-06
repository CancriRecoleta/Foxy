//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
    private static final int BURN_COOL_SPEED = 2;
    private static final int NUM_SLOTS = 4;
    private final NonNullList<ItemStack> items;
    private final int[] cookingProgress;
    private final int[] cookingTime;
    private final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck;

    public CampfireBlockEntity(BlockPos p_155301_, BlockState p_155302_) {
        super(BlockEntityType.CAMPFIRE, p_155301_, p_155302_);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
        this.cookingProgress = new int[4];
        this.cookingTime = new int[4];
        this.quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    }

    public static void cookTick(Level p_155307_, BlockPos p_155308_, BlockState p_155309_, CampfireBlockEntity p_155310_) {
        boolean $$4 = false;

        for(int $$5 = 0; $$5 < p_155310_.items.size(); ++$$5) {
            ItemStack $$6 = (ItemStack)p_155310_.items.get($$5);
            if (!$$6.isEmpty()) {
                $$4 = true;
                int var10002 = p_155310_.cookingProgress[$$5]++;
                if (p_155310_.cookingProgress[$$5] >= p_155310_.cookingTime[$$5]) {
                    Container $$7 = new SimpleContainer(new ItemStack[]{$$6});
                    ItemStack $$8 = (ItemStack)p_155310_.quickCheck.getRecipeFor($$7, p_155307_).map((p_270054_) -> {
                        return p_270054_.assemble($$7, p_155307_.registryAccess());
                    }).orElse($$6);
                    if ($$8.isItemEnabled(p_155307_.enabledFeatures())) {
                        Containers.dropItemStack(p_155307_, (double)p_155308_.getX(), (double)p_155308_.getY(), (double)p_155308_.getZ(), $$8);
                        p_155310_.items.set($$5, ItemStack.EMPTY);
                        p_155307_.sendBlockUpdated(p_155308_, p_155309_, p_155309_, 3);
                        p_155307_.gameEvent(GameEvent.BLOCK_CHANGE, p_155308_, Context.of(p_155309_));
                    }
                }
            }
        }

        if ($$4) {
            setChanged(p_155307_, p_155308_, p_155309_);
        }

    }

    public static void cooldownTick(Level p_155314_, BlockPos p_155315_, BlockState p_155316_, CampfireBlockEntity p_155317_) {
        boolean $$4 = false;

        for(int $$5 = 0; $$5 < p_155317_.items.size(); ++$$5) {
            if (p_155317_.cookingProgress[$$5] > 0) {
                $$4 = true;
                p_155317_.cookingProgress[$$5] = Mth.clamp(p_155317_.cookingProgress[$$5] - 2, 0, p_155317_.cookingTime[$$5]);
            }
        }

        if ($$4) {
            setChanged(p_155314_, p_155315_, p_155316_);
        }

    }

    public static void particleTick(Level p_155319_, BlockPos p_155320_, BlockState p_155321_, CampfireBlockEntity p_155322_) {
        RandomSource $$4 = p_155319_.random;
        int $$6;
        if ($$4.nextFloat() < 0.11F) {
            for($$6 = 0; $$6 < $$4.nextInt(2) + 2; ++$$6) {
                CampfireBlock.makeParticles(p_155319_, p_155320_, (Boolean)p_155321_.getValue(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        $$6 = ((Direction)p_155321_.getValue(CampfireBlock.FACING)).get2DDataValue();

        for(int $$7 = 0; $$7 < p_155322_.items.size(); ++$$7) {
            if (!((ItemStack)p_155322_.items.get($$7)).isEmpty() && $$4.nextFloat() < 0.2F) {
                Direction $$8 = Direction.from2DDataValue(Math.floorMod($$7 + $$6, 4));
                float $$9 = 0.3125F;
                double $$10 = (double)p_155320_.getX() + 0.5 - (double)((float)$$8.getStepX() * 0.3125F) + (double)((float)$$8.getClockWise().getStepX() * 0.3125F);
                double $$11 = (double)p_155320_.getY() + 0.5;
                double $$12 = (double)p_155320_.getZ() + 0.5 - (double)((float)$$8.getStepZ() * 0.3125F) + (double)((float)$$8.getClockWise().getStepZ() * 0.3125F);

                for(int $$13 = 0; $$13 < 4; ++$$13) {
                    p_155319_.addParticle(ParticleTypes.SMOKE, $$10, $$11, $$12, 0.0, 5.0E-4, 0.0);
                }
            }
        }

    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public void load(CompoundTag p_155312_) {
        super.load(p_155312_);
        this.items.clear();
        ContainerHelper.loadAllItems(p_155312_, this.items);
        int[] $$2;
        if (p_155312_.contains("CookingTimes", 11)) {
            $$2 = p_155312_.getIntArray("CookingTimes");
            System.arraycopy($$2, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, $$2.length));
        }

        if (p_155312_.contains("CookingTotalTimes", 11)) {
            $$2 = p_155312_.getIntArray("CookingTotalTimes");
            System.arraycopy($$2, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, $$2.length));
        }

    }

    protected void saveAdditional(CompoundTag p_187486_) {
        super.saveAdditional(p_187486_);
        ContainerHelper.saveAllItems(p_187486_, this.items, true);
        p_187486_.putIntArray("CookingTimes", this.cookingProgress);
        p_187486_.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag $$0 = new CompoundTag();
        ContainerHelper.saveAllItems($$0, this.items, true);
        return $$0;
    }

    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack p_59052_) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.quickCheck.getRecipeFor(new SimpleContainer(new ItemStack[]{p_59052_}), this.level);
    }

    public boolean placeFood(@Nullable Entity p_238285_, ItemStack p_238286_, int p_238287_) {
        for(int $$3 = 0; $$3 < this.items.size(); ++$$3) {
            ItemStack $$4 = (ItemStack)this.items.get($$3);
            if ($$4.isEmpty()) {
                this.cookingTime[$$3] = p_238287_;
                this.cookingProgress[$$3] = 0;
                this.items.set($$3, p_238286_.split(1));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), Context.of(p_238285_, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) {
            this.markUpdated();
        }

    }
}
