//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
    private static final Joiner COMMA_JOINED = Joiner.on(",");
    private final List<String[]> pattern = Lists.newArrayList();
    private final Map<Character, Predicate<BlockInWorld>> lookup = Maps.newHashMap();
    private int height;
    private int width;

    private BlockPatternBuilder() {
        this.lookup.put(' ', (p_187549_) -> {
            return true;
        });
    }

    public BlockPatternBuilder aisle(String... p_61248_) {
        if (!ArrayUtils.isEmpty(p_61248_) && !StringUtils.isEmpty(p_61248_[0])) {
            if (this.pattern.isEmpty()) {
                this.height = p_61248_.length;
                this.width = p_61248_[0].length();
            }

            if (p_61248_.length != this.height) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + p_61248_.length + ")");
            } else {
                String[] var2 = p_61248_;
                int var3 = p_61248_.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String $$1 = var2[var4];
                    if ($$1.length() != this.width) {
                        int var10002 = this.width;
                        throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + var10002 + ", found one with " + $$1.length() + ")");
                    }

                    char[] var6 = $$1.toCharArray();
                    int var7 = var6.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        char $$2 = var6[var8];
                        if (!this.lookup.containsKey($$2)) {
                            this.lookup.put($$2, (Object)null);
                        }
                    }
                }

                this.pattern.add(p_61248_);
                return this;
            }
        } else {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    public static BlockPatternBuilder start() {
        return new BlockPatternBuilder();
    }

    public BlockPatternBuilder where(char p_61245_, Predicate<BlockInWorld> p_61246_) {
        this.lookup.put(p_61245_, p_61246_);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.createPattern());
    }

    private Predicate<BlockInWorld>[][][] createPattern() {
        this.ensureAllCharactersMatched();
        Predicate<BlockInWorld>[][][] $$0 = (Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.pattern.size(), this.height, this.width});

        for(int $$1 = 0; $$1 < this.pattern.size(); ++$$1) {
            for(int $$2 = 0; $$2 < this.height; ++$$2) {
                for(int $$3 = 0; $$3 < this.width; ++$$3) {
                    $$0[$$1][$$2][$$3] = (Predicate)this.lookup.get(((String[])this.pattern.get($$1))[$$2].charAt($$3));
                }
            }
        }

        return $$0;
    }

    private void ensureAllCharactersMatched() {
        List<Character> $$0 = Lists.newArrayList();
        Iterator var2 = this.lookup.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<Character, Predicate<BlockInWorld>> $$1 = (Map.Entry)var2.next();
            if ($$1.getValue() == null) {
                $$0.add((Character)$$1.getKey());
            }
        }

        if (!$$0.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join($$0) + " are missing");
        }
    }
}
