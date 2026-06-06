//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerMenuSlotDefinition {
    private final List<SlotDefinition> slots;
    private final SlotDefinition resultSlot;

    ItemCombinerMenuSlotDefinition(List<SlotDefinition> p_266947_, SlotDefinition p_266715_) {
        if (!p_266947_.isEmpty() && !p_266715_.equals(net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY)) {
            this.slots = p_266947_;
            this.resultSlot = p_266715_;
        } else {
            throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public boolean hasSlot(int p_267185_) {
        return this.slots.size() >= p_267185_;
    }

    public SlotDefinition getSlot(int p_266907_) {
        return (SlotDefinition)this.slots.get(p_266907_);
    }

    public SlotDefinition getResultSlot() {
        return this.resultSlot;
    }

    public List<SlotDefinition> getSlots() {
        return this.slots;
    }

    public int getNumOfInputSlots() {
        return this.slots.size();
    }

    public int getResultSlotIndex() {
        return this.getNumOfInputSlots();
    }

    public List<Integer> getInputSlotIndexes() {
        return (List)this.slots.stream().map(SlotDefinition::slotIndex).collect(Collectors.toList());
    }

    public static record SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
        static final SlotDefinition EMPTY = new SlotDefinition(0, 0, 0, (p_267109_) -> {
            return true;
        });

        public SlotDefinition(int slotIndex, int x, int y, Predicate<ItemStack> mayPlace) {
            this.slotIndex = slotIndex;
            this.x = x;
            this.y = y;
            this.mayPlace = mayPlace;
        }

        public int slotIndex() {
            return this.slotIndex;
        }

        public int x() {
            return this.x;
        }

        public int y() {
            return this.y;
        }

        public Predicate<ItemStack> mayPlace() {
            return this.mayPlace;
        }
    }

    public static class Builder {
        private final List<SlotDefinition> slots = new ArrayList();
        private SlotDefinition resultSlot;

        public Builder() {
            this.resultSlot = net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition.SlotDefinition.EMPTY;
        }

        public Builder withSlot(int p_267315_, int p_267028_, int p_266815_, Predicate<ItemStack> p_267120_) {
            this.slots.add(new SlotDefinition(p_267315_, p_267028_, p_266815_, p_267120_));
            return this;
        }

        public Builder withResultSlot(int p_267180_, int p_267130_, int p_266910_) {
            this.resultSlot = new SlotDefinition(p_267180_, p_267130_, p_266910_, (p_266825_) -> {
                return false;
            });
            return this;
        }

        public ItemCombinerMenuSlotDefinition build() {
            return new ItemCombinerMenuSlotDefinition(this.slots, this.resultSlot);
        }
    }
}
