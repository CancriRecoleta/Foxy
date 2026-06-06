//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WorkAtComposter extends WorkAtPoi {
    private static final List<Item> COMPOSTABLE_ITEMS;

    public WorkAtComposter() {
    }

    protected void useWorkstation(ServerLevel p_24790_, Villager p_24791_) {
        Optional<GlobalPos> $$2 = p_24791_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if ($$2.isPresent()) {
            GlobalPos $$3 = (GlobalPos)$$2.get();
            BlockState $$4 = p_24790_.getBlockState($$3.pos());
            if ($$4.is(Blocks.COMPOSTER)) {
                this.makeBread(p_24791_);
                this.compostItems(p_24790_, p_24791_, $$3, $$4);
            }

        }
    }

    private void compostItems(ServerLevel p_24793_, Villager p_24794_, GlobalPos p_24795_, BlockState p_24796_) {
        BlockPos $$4 = p_24795_.pos();
        if ((Integer)p_24796_.getValue(ComposterBlock.LEVEL) == 8) {
            p_24796_ = ComposterBlock.extractProduce(p_24794_, p_24796_, p_24793_, $$4);
        }

        int $$5 = 20;
        int $$6 = true;
        int[] $$7 = new int[COMPOSTABLE_ITEMS.size()];
        SimpleContainer $$8 = p_24794_.getInventory();
        int $$9 = $$8.getContainerSize();
        BlockState $$10 = p_24796_;

        for(int $$11 = $$9 - 1; $$11 >= 0 && $$5 > 0; --$$11) {
            ItemStack $$12 = $$8.getItem($$11);
            int $$13 = COMPOSTABLE_ITEMS.indexOf($$12.getItem());
            if ($$13 != -1) {
                int $$14 = $$12.getCount();
                int $$15 = $$7[$$13] + $$14;
                $$7[$$13] = $$15;
                int $$16 = Math.min(Math.min($$15 - 10, $$5), $$14);
                if ($$16 > 0) {
                    $$5 -= $$16;

                    for(int $$17 = 0; $$17 < $$16; ++$$17) {
                        $$10 = ComposterBlock.insertItem(p_24794_, $$10, p_24793_, $$12, $$4);
                        if ((Integer)$$10.getValue(ComposterBlock.LEVEL) == 7) {
                            this.spawnComposterFillEffects(p_24793_, p_24796_, $$4, $$10);
                            return;
                        }
                    }
                }
            }
        }

        this.spawnComposterFillEffects(p_24793_, p_24796_, $$4, $$10);
    }

    private void spawnComposterFillEffects(ServerLevel p_24798_, BlockState p_24799_, BlockPos p_24800_, BlockState p_24801_) {
        p_24798_.levelEvent(1500, p_24800_, p_24801_ != p_24799_ ? 1 : 0);
    }

    private void makeBread(Villager p_24803_) {
        SimpleContainer $$1 = p_24803_.getInventory();
        if ($$1.countItem(Items.BREAD) <= 36) {
            int $$2 = $$1.countItem(Items.WHEAT);
            int $$3 = true;
            int $$4 = true;
            int $$5 = Math.min(3, $$2 / 3);
            if ($$5 != 0) {
                int $$6 = $$5 * 3;
                $$1.removeItemType(Items.WHEAT, $$6);
                ItemStack $$7 = $$1.addItem(new ItemStack(Items.BREAD, $$5));
                if (!$$7.isEmpty()) {
                    p_24803_.spawnAtLocation($$7, 0.5F);
                }

            }
        }
    }

    static {
        COMPOSTABLE_ITEMS = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS);
    }
}
