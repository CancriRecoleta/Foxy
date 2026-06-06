//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable {
    protected final LivingEntity entity;
    protected final List<EquipmentSlot> slots;

    public EntityEquipmentInvWrapper(LivingEntity entity, EquipmentSlot.Type slotType) {
        this.entity = entity;
        List<EquipmentSlot> slots = new ArrayList();
        EquipmentSlot[] var4 = EquipmentSlot.values();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            EquipmentSlot slot = var4[var6];
            if (slot.getType() == slotType) {
                slots.add(slot);
            }
        }

        this.slots = ImmutableList.copyOf(slots);
    }

    public int getSlots() {
        return this.slots.size();
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.entity.getItemBySlot(this.validateSlotIndex(slot));
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
            ItemStack existing = this.entity.getItemBySlot(equipmentSlot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.entity.setItemSlot(equipmentSlot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }
                }

                return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
            ItemStack existing = this.entity.getItemBySlot(equipmentSlot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.entity.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                    }

                    return existing;
                } else {
                    if (!simulate) {
                        this.entity.setItemSlot(equipmentSlot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    }

                    return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
                }
            }
        }
    }

    public int getSlotLimit(int slot) {
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        return equipmentSlot.getType() == Type.ARMOR ? 1 : 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        EquipmentSlot equipmentSlot = this.validateSlotIndex(slot);
        if (!ItemStack.matches(this.entity.getItemBySlot(equipmentSlot), stack)) {
            this.entity.setItemSlot(equipmentSlot, stack);
        }
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    protected EquipmentSlot validateSlotIndex(int slot) {
        if (slot >= 0 && slot < this.slots.size()) {
            return (EquipmentSlot)this.slots.get(slot);
        } else {
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + this.slots.size() + ")");
        }
    }

    public static LazyOptional<IItemHandlerModifiable>[] create(LivingEntity entity) {
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[]{LazyOptional.of(() -> {
            return new EntityHandsInvWrapper(entity);
        }), LazyOptional.of(() -> {
            return new EntityArmorInvWrapper(entity);
        }), null};
        ret[2] = LazyOptional.of(() -> {
            return new CombinedInvWrapper(new IItemHandlerModifiable[]{(IItemHandlerModifiable)ret[0].orElse((Object)null), (IItemHandlerModifiable)ret[1].orElse((Object)null)});
        });
        return ret;
    }
}
