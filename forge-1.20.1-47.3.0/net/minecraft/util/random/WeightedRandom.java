//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.random;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

public class WeightedRandom {
    private WeightedRandom() {
    }

    public static int getTotalWeight(List<? extends WeightedEntry> p_146313_) {
        long $$1 = 0L;

        WeightedEntry $$2;
        for(Iterator var3 = p_146313_.iterator(); var3.hasNext(); $$1 += (long)$$2.getWeight().asInt()) {
            $$2 = (WeightedEntry)var3.next();
        }

        if ($$1 > 2147483647L) {
            throw new IllegalArgumentException("Sum of weights must be <= 2147483647");
        } else {
            return (int)$$1;
        }
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource p_216826_, List<T> p_216827_, int p_216828_) {
        if (p_216828_ < 0) {
            throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Negative total weight in getRandomItem"));
        } else if (p_216828_ == 0) {
            return Optional.empty();
        } else {
            int $$3 = p_216826_.nextInt(p_216828_);
            return getWeightedItem(p_216827_, $$3);
        }
    }

    public static <T extends WeightedEntry> Optional<T> getWeightedItem(List<T> p_146315_, int p_146316_) {
        Iterator var2 = p_146315_.iterator();

        WeightedEntry $$2;
        do {
            if (!var2.hasNext()) {
                return Optional.empty();
            }

            $$2 = (WeightedEntry)var2.next();
            p_146316_ -= $$2.getWeight().asInt();
        } while(p_146316_ >= 0);

        return Optional.of($$2);
    }

    public static <T extends WeightedEntry> Optional<T> getRandomItem(RandomSource p_216823_, List<T> p_216824_) {
        return getRandomItem(p_216823_, p_216824_, getTotalWeight(p_216824_));
    }
}
