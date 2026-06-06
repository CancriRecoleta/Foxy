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

class AnyOfPredicate extends CombiningPredicate {
    public static final Codec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

    public AnyOfPredicate(List<BlockPredicate> p_190384_) {
        super(p_190384_);
    }

    public boolean test(WorldGenLevel p_190387_, BlockPos p_190388_) {
        Iterator var3 = this.predicates.iterator();

        BlockPredicate $$2;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            $$2 = (BlockPredicate)var3.next();
        } while(!$$2.test(p_190387_, p_190388_));

        return true;
    }

    public BlockPredicateType<?> type() {
        return BlockPredicateType.ANY_OF;
    }
}
