//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AllOfPredicate extends CombiningPredicate {
    public static final Codec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

    public AllOfPredicate(List<BlockPredicate> p_190373_) {
        super(p_190373_);
    }

    public boolean test(WorldGenLevel p_190376_, BlockPos p_190377_) {
        Iterator var3 = this.predicates.iterator();

        BlockPredicate $$2;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            $$2 = (BlockPredicate)var3.next();
        } while($$2.test(p_190376_, p_190377_));

        return false;
    }

    public BlockPredicateType<?> type() {
        return BlockPredicateType.ALL_OF;
    }
}
