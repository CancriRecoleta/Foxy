//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
    public static final int SECTION_WIDTH = 16;
    public static final int SECTION_HEIGHT = 16;
    public static final int SECTION_SIZE = 4096;
    public static final int BIOME_CONTAINER_BITS = 2;
    private short nonEmptyBlockCount;
    private short tickingBlockCount;
    private short tickingFluidCount;
    private final PalettedContainer<BlockState> states;
    private PalettedContainerRO<Holder<Biome>> biomes;

    public LevelChunkSection(PalettedContainer<BlockState> p_282846_, PalettedContainerRO<Holder<Biome>> p_281695_) {
        this.states = p_282846_;
        this.biomes = p_281695_;
        this.recalcBlockCounts();
    }

    public LevelChunkSection(Registry<Biome> p_282873_) {
        this.states = new PalettedContainer(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), Strategy.SECTION_STATES);
        this.biomes = new PalettedContainer(p_282873_.asHolderIdMap(), p_282873_.getHolderOrThrow(Biomes.PLAINS), Strategy.SECTION_BIOMES);
    }

    public BlockState getBlockState(int p_62983_, int p_62984_, int p_62985_) {
        return (BlockState)this.states.get(p_62983_, p_62984_, p_62985_);
    }

    public FluidState getFluidState(int p_63008_, int p_63009_, int p_63010_) {
        return ((BlockState)this.states.get(p_63008_, p_63009_, p_63010_)).getFluidState();
    }

    public void acquire() {
        this.states.acquire();
    }

    public void release() {
        this.states.release();
    }

    public BlockState setBlockState(int p_62987_, int p_62988_, int p_62989_, BlockState p_62990_) {
        return this.setBlockState(p_62987_, p_62988_, p_62989_, p_62990_, true);
    }

    public BlockState setBlockState(int p_62992_, int p_62993_, int p_62994_, BlockState p_62995_, boolean p_62996_) {
        BlockState $$6;
        if (p_62996_) {
            $$6 = (BlockState)this.states.getAndSet(p_62992_, p_62993_, p_62994_, p_62995_);
        } else {
            $$6 = (BlockState)this.states.getAndSetUnchecked(p_62992_, p_62993_, p_62994_, p_62995_);
        }

        FluidState $$7 = $$6.getFluidState();
        FluidState $$8 = p_62995_.getFluidState();
        if (!$$6.isAir()) {
            --this.nonEmptyBlockCount;
            if ($$6.isRandomlyTicking()) {
                --this.tickingBlockCount;
            }
        }

        if (!$$7.isEmpty()) {
            --this.tickingFluidCount;
        }

        if (!p_62995_.isAir()) {
            ++this.nonEmptyBlockCount;
            if (p_62995_.isRandomlyTicking()) {
                ++this.tickingBlockCount;
            }
        }

        if (!$$8.isEmpty()) {
            ++this.tickingFluidCount;
        }

        return $$6;
    }

    public boolean hasOnlyAir() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean isRandomlyTicking() {
        return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
    }

    public boolean isRandomlyTickingBlocks() {
        return this.tickingBlockCount > 0;
    }

    public boolean isRandomlyTickingFluids() {
        return this.tickingFluidCount > 0;
    }

    public void recalcBlockCounts() {
        class BlockCounter implements PalettedContainer.CountConsumer<BlockState> {
            public int nonEmptyBlockCount;
            public int tickingBlockCount;
            public int tickingFluidCount;

            BlockCounter() {
            }

            public void accept(BlockState p_204444_, int p_204445_) {
                FluidState $$2 = p_204444_.getFluidState();
                if (!p_204444_.isAir()) {
                    this.nonEmptyBlockCount += p_204445_;
                    if (p_204444_.isRandomlyTicking()) {
                        this.tickingBlockCount += p_204445_;
                    }
                }

                if (!$$2.isEmpty()) {
                    this.nonEmptyBlockCount += p_204445_;
                    if ($$2.isRandomlyTicking()) {
                        this.tickingFluidCount += p_204445_;
                    }
                }

            }
        }

        BlockCounter $$0 = new BlockCounter();
        this.states.count($$0);
        this.nonEmptyBlockCount = (short)$$0.nonEmptyBlockCount;
        this.tickingBlockCount = (short)$$0.tickingBlockCount;
        this.tickingFluidCount = (short)$$0.tickingFluidCount;
    }

    public PalettedContainer<BlockState> getStates() {
        return this.states;
    }

    public PalettedContainerRO<Holder<Biome>> getBiomes() {
        return this.biomes;
    }

    public void read(FriendlyByteBuf p_63005_) {
        this.nonEmptyBlockCount = p_63005_.readShort();
        this.states.read(p_63005_);
        PalettedContainer<Holder<Biome>> $$1 = this.biomes.recreate();
        $$1.read(p_63005_);
        this.biomes = $$1;
    }

    public void readBiomes(FriendlyByteBuf p_275669_) {
        PalettedContainer<Holder<Biome>> $$1 = this.biomes.recreate();
        $$1.read(p_275669_);
        this.biomes = $$1;
    }

    public void write(FriendlyByteBuf p_63012_) {
        p_63012_.writeShort(this.nonEmptyBlockCount);
        this.states.write(p_63012_);
        this.biomes.write(p_63012_);
    }

    public int getSerializedSize() {
        return 2 + this.states.getSerializedSize() + this.biomes.getSerializedSize();
    }

    public boolean maybeHas(Predicate<BlockState> p_63003_) {
        return this.states.maybeHas(p_63003_);
    }

    public Holder<Biome> getNoiseBiome(int p_204434_, int p_204435_, int p_204436_) {
        return (Holder)this.biomes.get(p_204434_, p_204435_, p_204436_);
    }

    public void fillBiomesFromNoise(BiomeResolver p_282075_, Climate.Sampler p_283084_, int p_282310_, int p_281510_, int p_283057_) {
        PalettedContainer<Holder<Biome>> $$5 = this.biomes.recreate();
        int $$6 = true;

        for(int $$7 = 0; $$7 < 4; ++$$7) {
            for(int $$8 = 0; $$8 < 4; ++$$8) {
                for(int $$9 = 0; $$9 < 4; ++$$9) {
                    $$5.getAndSetUnchecked($$7, $$8, $$9, p_282075_.getNoiseBiome(p_282310_ + $$7, p_281510_ + $$8, p_283057_ + $$9, p_283084_));
                }
            }
        }

        this.biomes = $$5;
    }
}
