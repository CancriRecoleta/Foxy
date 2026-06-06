//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class RandomSequences extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long seed;
    private final Map<ResourceLocation, RandomSequence> sequences = new Object2ObjectOpenHashMap();

    public RandomSequences(long p_287622_) {
        this.seed = p_287622_;
    }

    public RandomSource get(ResourceLocation p_287751_) {
        final RandomSource $$1 = ((RandomSequence)this.sequences.computeIfAbsent(p_287751_, (p_287666_) -> {
            return new RandomSequence(this.seed, p_287666_);
        })).random();
        return new RandomSource() {
            public RandomSource fork() {
                RandomSequences.this.setDirty();
                return $$1.fork();
            }

            public PositionalRandomFactory forkPositional() {
                RandomSequences.this.setDirty();
                return $$1.forkPositional();
            }

            public void setSeed(long p_287659_) {
                RandomSequences.this.setDirty();
                $$1.setSeed(p_287659_);
            }

            public int nextInt() {
                RandomSequences.this.setDirty();
                return $$1.nextInt();
            }

            public int nextInt(int p_287717_) {
                RandomSequences.this.setDirty();
                return $$1.nextInt(p_287717_);
            }

            public long nextLong() {
                RandomSequences.this.setDirty();
                return $$1.nextLong();
            }

            public boolean nextBoolean() {
                RandomSequences.this.setDirty();
                return $$1.nextBoolean();
            }

            public float nextFloat() {
                RandomSequences.this.setDirty();
                return $$1.nextFloat();
            }

            public double nextDouble() {
                RandomSequences.this.setDirty();
                return $$1.nextDouble();
            }

            public double nextGaussian() {
                RandomSequences.this.setDirty();
                return $$1.nextGaussian();
            }
        };
    }

    public CompoundTag save(CompoundTag p_287658_) {
        this.sequences.forEach((p_287627_, p_287578_) -> {
            p_287658_.put(p_287627_.toString(), (Tag)RandomSequence.CODEC.encodeStart(NbtOps.INSTANCE, p_287578_).result().orElseThrow());
        });
        return p_287658_;
    }

    public static RandomSequences load(long p_287756_, CompoundTag p_287587_) {
        RandomSequences $$2 = new RandomSequences(p_287756_);
        Set<String> $$3 = p_287587_.getAllKeys();
        Iterator var5 = $$3.iterator();

        while(var5.hasNext()) {
            String $$4 = (String)var5.next();

            try {
                RandomSequence $$5 = (RandomSequence)((Pair)RandomSequence.CODEC.decode(NbtOps.INSTANCE, p_287587_.get($$4)).result().get()).getFirst();
                $$2.sequences.put(new ResourceLocation($$4), $$5);
            } catch (Exception var8) {
                Exception $$6 = var8;
                LOGGER.error("Failed to load random sequence {}", $$4, $$6);
            }
        }

        return $$2;
    }
}
