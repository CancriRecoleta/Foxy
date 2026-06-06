//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModeCheck {
    private final String tagName;
    @Nullable
    private BlockInWorld lastCheckedBlock;
    private boolean lastResult;
    private boolean checksBlockEntity;

    public AdventureModeCheck(String p_186327_) {
        this.tagName = p_186327_;
    }

    private static boolean areSameBlocks(BlockInWorld p_186333_, @Nullable BlockInWorld p_186334_, boolean p_186335_) {
        if (p_186334_ != null && p_186333_.getState() == p_186334_.getState()) {
            if (!p_186335_) {
                return true;
            } else if (p_186333_.getEntity() == null && p_186334_.getEntity() == null) {
                return true;
            } else {
                return p_186333_.getEntity() != null && p_186334_.getEntity() != null ? Objects.equals(p_186333_.getEntity().saveWithId(), p_186334_.getEntity().saveWithId()) : false;
            }
        } else {
            return false;
        }
    }

    public boolean test(ItemStack p_204086_, Registry<Block> p_204087_, BlockInWorld p_204088_) {
        if (areSameBlocks(p_204088_, this.lastCheckedBlock, this.checksBlockEntity)) {
            return this.lastResult;
        } else {
            this.lastCheckedBlock = p_204088_;
            this.checksBlockEntity = false;
            CompoundTag $$3 = p_204086_.getTag();
            if ($$3 != null && $$3.contains(this.tagName, 9)) {
                ListTag $$4 = $$3.getList(this.tagName, 8);

                for(int $$5 = 0; $$5 < $$4.size(); ++$$5) {
                    String $$6 = $$4.getString($$5);

                    try {
                        BlockPredicateArgument.Result $$7 = BlockPredicateArgument.parse(p_204087_.asLookup(), new StringReader($$6));
                        this.checksBlockEntity |= $$7.requiresNbt();
                        if ($$7.test(p_204088_)) {
                            this.lastResult = true;
                            return true;
                        }
                    } catch (CommandSyntaxException var9) {
                    }
                }
            }

            this.lastResult = false;
            return false;
        }
    }
}
