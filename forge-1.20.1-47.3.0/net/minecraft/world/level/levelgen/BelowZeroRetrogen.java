//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ProtoChunk;

public final class BelowZeroRetrogen {
    private static final BitSet EMPTY = new BitSet(0);
    private static final Codec<BitSet> BITSET_CODEC;
    private static final Codec<ChunkStatus> NON_EMPTY_CHUNK_STATUS;
    public static final Codec<BelowZeroRetrogen> CODEC;
    private static final Set<ResourceKey<Biome>> RETAINED_RETROGEN_BIOMES;
    public static final LevelHeightAccessor UPGRADE_HEIGHT_ACCESSOR;
    private final ChunkStatus targetStatus;
    private final BitSet missingBedrock;

    private BelowZeroRetrogen(ChunkStatus p_188464_, Optional<BitSet> p_188465_) {
        this.targetStatus = p_188464_;
        this.missingBedrock = (BitSet)p_188465_.orElse(EMPTY);
    }

    @Nullable
    public static BelowZeroRetrogen read(CompoundTag p_188486_) {
        ChunkStatus $$1 = ChunkStatus.byName(p_188486_.getString("target_status"));
        return $$1 == ChunkStatus.EMPTY ? null : new BelowZeroRetrogen($$1, Optional.of(BitSet.valueOf(p_188486_.getLongArray("missing_bedrock"))));
    }

    public static void replaceOldBedrock(ProtoChunk p_188475_) {
        int $$1 = true;
        BlockPos.betweenClosed(0, 0, 0, 15, 4, 15).forEach((p_188492_) -> {
            if (p_188475_.getBlockState(p_188492_).is(Blocks.BEDROCK)) {
                p_188475_.setBlockState(p_188492_, Blocks.DEEPSLATE.defaultBlockState(), false);
            }

        });
    }

    public void applyBedrockMask(ProtoChunk p_198222_) {
        LevelHeightAccessor $$1 = p_198222_.getHeightAccessorForGeneration();
        int $$2 = $$1.getMinBuildHeight();
        int $$3 = $$1.getMaxBuildHeight() - 1;

        for(int $$4 = 0; $$4 < 16; ++$$4) {
            for(int $$5 = 0; $$5 < 16; ++$$5) {
                if (this.hasBedrockHole($$4, $$5)) {
                    BlockPos.betweenClosed($$4, $$2, $$5, $$4, $$3, $$5).forEach((p_198219_) -> {
                        p_198222_.setBlockState(p_198219_, Blocks.AIR.defaultBlockState(), false);
                    });
                }
            }
        }

    }

    public ChunkStatus targetStatus() {
        return this.targetStatus;
    }

    public boolean hasBedrockHoles() {
        return !this.missingBedrock.isEmpty();
    }

    public boolean hasBedrockHole(int p_198215_, int p_198216_) {
        return this.missingBedrock.get((p_198216_ & 15) * 16 + (p_198215_ & 15));
    }

    public static BiomeResolver getBiomeResolver(BiomeResolver p_204532_, ChunkAccess p_204533_) {
        if (!p_204533_.isUpgrading()) {
            return p_204532_;
        } else {
            Set var10000 = RETAINED_RETROGEN_BIOMES;
            Objects.requireNonNull(var10000);
            Predicate<ResourceKey<Biome>> $$2 = var10000::contains;
            return (p_204538_, p_204539_, p_204540_, p_204541_) -> {
                Holder<Biome> $$7 = p_204532_.getNoiseBiome(p_204538_, p_204539_, p_204540_, p_204541_);
                return $$7.is($$2) ? $$7 : p_204533_.getNoiseBiome(p_204538_, 0, p_204540_);
            };
        }
    }

    static {
        BITSET_CODEC = Codec.LONG_STREAM.xmap((p_188484_) -> {
            return BitSet.valueOf(p_188484_.toArray());
        }, (p_188482_) -> {
            return LongStream.of(p_188482_.toLongArray());
        });
        NON_EMPTY_CHUNK_STATUS = BuiltInRegistries.CHUNK_STATUS.byNameCodec().comapFlatMap((p_275180_) -> {
            return p_275180_ == ChunkStatus.EMPTY ? DataResult.error(() -> {
                return "target_status cannot be empty";
            }) : DataResult.success(p_275180_);
        }, Function.identity());
        CODEC = RecordCodecBuilder.create((p_188471_) -> {
            return p_188471_.group(NON_EMPTY_CHUNK_STATUS.fieldOf("target_status").forGetter(BelowZeroRetrogen::targetStatus), BITSET_CODEC.optionalFieldOf("missing_bedrock").forGetter((p_188480_) -> {
                return p_188480_.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(p_188480_.missingBedrock);
            })).apply(p_188471_, BelowZeroRetrogen::new);
        });
        RETAINED_RETROGEN_BIOMES = Set.of(Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES);
        UPGRADE_HEIGHT_ACCESSOR = new LevelHeightAccessor() {
            public int getHeight() {
                return 64;
            }

            public int getMinBuildHeight() {
                return -64;
            }
        };
    }
}
