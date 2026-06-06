//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.lighting;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;

public final class BlockLightEngine extends LightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
    private final BlockPos.MutableBlockPos mutablePos;

    public BlockLightEngine(LightChunkGetter p_75492_) {
        this(p_75492_, new BlockLightSectionStorage(p_75492_));
    }

    @VisibleForTesting
    public BlockLightEngine(LightChunkGetter p_278252_, BlockLightSectionStorage p_278255_) {
        super(p_278252_, p_278255_);
        this.mutablePos = new BlockPos.MutableBlockPos();
    }

    protected void checkNode(long p_285169_) {
        long i = SectionPos.blockToSection(p_285169_);
        if (((BlockLightSectionStorage)this.storage).storingLightForSection(i)) {
            BlockState blockstate = this.getState(this.mutablePos.set(p_285169_));
            int j = this.getEmission(p_285169_, blockstate);
            int k = ((BlockLightSectionStorage)this.storage).getStoredLevel(p_285169_);
            if (j < k) {
                ((BlockLightSectionStorage)this.storage).setStoredLevel(p_285169_, 0);
                this.enqueueDecrease(p_285169_, net.minecraft.world.level.lighting.LightEngine.QueueEntry.decreaseAllDirections(k));
            } else {
                this.enqueueDecrease(p_285169_, PULL_LIGHT_IN_ENTRY);
            }

            if (j > 0) {
                this.enqueueIncrease(p_285169_, net.minecraft.world.level.lighting.LightEngine.QueueEntry.increaseLightFromEmission(j, isEmptyShape(blockstate)));
            }
        }

    }

    protected void propagateIncrease(long p_285500_, long p_285410_, int p_285492_) {
        BlockState blockstate = null;
        Direction[] var7 = PROPAGATION_DIRECTIONS;
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            Direction direction = var7[var9];
            if (net.minecraft.world.level.lighting.LightEngine.QueueEntry.shouldPropagateInDirection(p_285410_, direction)) {
                long i = BlockPos.offset(p_285500_, direction);
                if (((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(i))) {
                    int j = ((BlockLightSectionStorage)this.storage).getStoredLevel(i);
                    int k = p_285492_ - 1;
                    if (k > j) {
                        this.mutablePos.set(i);
                        BlockState blockstate1 = this.getState(this.mutablePos);
                        int l = p_285492_ - this.getOpacity(blockstate1, this.mutablePos);
                        if (l > j) {
                            if (blockstate == null) {
                                blockstate = net.minecraft.world.level.lighting.LightEngine.QueueEntry.isFromEmptyShape(p_285410_) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set(p_285500_));
                            }

                            if (!this.shapeOccludes(p_285500_, blockstate, i, blockstate1, direction)) {
                                ((BlockLightSectionStorage)this.storage).setStoredLevel(i, l);
                                if (l > 1) {
                                    this.enqueueIncrease(i, net.minecraft.world.level.lighting.LightEngine.QueueEntry.increaseSkipOneDirection(l, isEmptyShape(blockstate1), direction.getOpposite()));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    protected void propagateDecrease(long p_285435_, long p_285230_) {
        int i = net.minecraft.world.level.lighting.LightEngine.QueueEntry.getFromLevel(p_285230_);
        Direction[] var6 = PROPAGATION_DIRECTIONS;
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            if (net.minecraft.world.level.lighting.LightEngine.QueueEntry.shouldPropagateInDirection(p_285230_, direction)) {
                long j = BlockPos.offset(p_285435_, direction);
                if (((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(j))) {
                    int k = ((BlockLightSectionStorage)this.storage).getStoredLevel(j);
                    if (k != 0) {
                        if (k <= i - 1) {
                            BlockState blockstate = this.getState(this.mutablePos.set(j));
                            int l = this.getEmission(j, blockstate);
                            ((BlockLightSectionStorage)this.storage).setStoredLevel(j, 0);
                            if (l < k) {
                                this.enqueueDecrease(j, net.minecraft.world.level.lighting.LightEngine.QueueEntry.decreaseSkipOneDirection(k, direction.getOpposite()));
                            }

                            if (l > 0) {
                                this.enqueueIncrease(j, net.minecraft.world.level.lighting.LightEngine.QueueEntry.increaseLightFromEmission(l, isEmptyShape(blockstate)));
                            }
                        } else {
                            this.enqueueIncrease(j, net.minecraft.world.level.lighting.LightEngine.QueueEntry.increaseOnlyOneDirection(k, false, direction.getOpposite()));
                        }
                    }
                }
            }
        }

    }

    private int getEmission(long p_285243_, BlockState p_284973_) {
        int i = p_284973_.getLightEmission(this.chunkSource.getLevel(), this.mutablePos);
        return i > 0 && ((BlockLightSectionStorage)this.storage).lightOnInSection(SectionPos.blockToSection(p_285243_)) ? i : 0;
    }

    public void propagateLightSources(ChunkPos p_285274_) {
        this.setLightEnabled(p_285274_, true);
        LightChunk lightchunk = this.chunkSource.getChunkForLighting(p_285274_.x, p_285274_.z);
        if (lightchunk != null) {
            lightchunk.findBlockLightSources((p_285266_, p_285452_) -> {
                int i = p_285452_.getLightEmission(this.chunkSource.getLevel(), p_285266_);
                this.enqueueIncrease(p_285266_.asLong(), net.minecraft.world.level.lighting.LightEngine.QueueEntry.increaseLightFromEmission(i, isEmptyShape(p_285452_)));
            });
        }

    }
}
