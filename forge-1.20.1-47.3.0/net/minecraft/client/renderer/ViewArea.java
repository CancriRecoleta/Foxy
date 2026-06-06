//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewArea {
    protected final LevelRenderer levelRenderer;
    protected final Level level;
    protected int chunkGridSizeY;
    protected int chunkGridSizeX;
    protected int chunkGridSizeZ;
    public ChunkRenderDispatcher.RenderChunk[] chunks;

    public ViewArea(ChunkRenderDispatcher p_110845_, Level p_110846_, int p_110847_, LevelRenderer p_110848_) {
        this.levelRenderer = p_110848_;
        this.level = p_110846_;
        this.setViewDistance(p_110847_);
        this.createChunks(p_110845_);
    }

    protected void createChunks(ChunkRenderDispatcher p_110865_) {
        if (!Minecraft.getInstance().isSameThread()) {
            throw new IllegalStateException("createChunks called from wrong thread: " + Thread.currentThread().getName());
        } else {
            int $$1 = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
            this.chunks = new ChunkRenderDispatcher.RenderChunk[$$1];

            for(int $$2 = 0; $$2 < this.chunkGridSizeX; ++$$2) {
                for(int $$3 = 0; $$3 < this.chunkGridSizeY; ++$$3) {
                    for(int $$4 = 0; $$4 < this.chunkGridSizeZ; ++$$4) {
                        int $$5 = this.getChunkIndex($$2, $$3, $$4);
                        ChunkRenderDispatcher.RenderChunk[] var10000 = this.chunks;
                        Objects.requireNonNull(p_110865_);
                        var10000[$$5] = p_110865_.new RenderChunk($$5, $$2 * 16, $$3 * 16, $$4 * 16);
                    }
                }
            }

        }
    }

    public void releaseAllBuffers() {
        ChunkRenderDispatcher.RenderChunk[] var1 = this.chunks;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ChunkRenderDispatcher.RenderChunk $$0 = var1[var3];
            $$0.releaseBuffers();
        }

    }

    private int getChunkIndex(int p_110856_, int p_110857_, int p_110858_) {
        return (p_110858_ * this.chunkGridSizeY + p_110857_) * this.chunkGridSizeX + p_110856_;
    }

    protected void setViewDistance(int p_110854_) {
        int $$1 = p_110854_ * 2 + 1;
        this.chunkGridSizeX = $$1;
        this.chunkGridSizeY = this.level.getSectionsCount();
        this.chunkGridSizeZ = $$1;
    }

    public void repositionCamera(double p_110851_, double p_110852_) {
        int $$2 = Mth.ceil(p_110851_);
        int $$3 = Mth.ceil(p_110852_);

        for(int $$4 = 0; $$4 < this.chunkGridSizeX; ++$$4) {
            int $$5 = this.chunkGridSizeX * 16;
            int $$6 = $$2 - 8 - $$5 / 2;
            int $$7 = $$6 + Math.floorMod($$4 * 16 - $$6, $$5);

            for(int $$8 = 0; $$8 < this.chunkGridSizeZ; ++$$8) {
                int $$9 = this.chunkGridSizeZ * 16;
                int $$10 = $$3 - 8 - $$9 / 2;
                int $$11 = $$10 + Math.floorMod($$8 * 16 - $$10, $$9);

                for(int $$12 = 0; $$12 < this.chunkGridSizeY; ++$$12) {
                    int $$13 = this.level.getMinBuildHeight() + $$12 * 16;
                    ChunkRenderDispatcher.RenderChunk $$14 = this.chunks[this.getChunkIndex($$4, $$12, $$8)];
                    BlockPos $$15 = $$14.getOrigin();
                    if ($$7 != $$15.getX() || $$13 != $$15.getY() || $$11 != $$15.getZ()) {
                        $$14.setOrigin($$7, $$13, $$11);
                    }
                }
            }
        }

    }

    public void setDirty(int p_110860_, int p_110861_, int p_110862_, boolean p_110863_) {
        int $$4 = Math.floorMod(p_110860_, this.chunkGridSizeX);
        int $$5 = Math.floorMod(p_110861_ - this.level.getMinSection(), this.chunkGridSizeY);
        int $$6 = Math.floorMod(p_110862_, this.chunkGridSizeZ);
        ChunkRenderDispatcher.RenderChunk $$7 = this.chunks[this.getChunkIndex($$4, $$5, $$6)];
        $$7.setDirty(p_110863_);
    }

    @Nullable
    protected ChunkRenderDispatcher.RenderChunk getRenderChunkAt(BlockPos p_110867_) {
        int $$1 = Mth.floorDiv(p_110867_.getX(), 16);
        int $$2 = Mth.floorDiv(p_110867_.getY() - this.level.getMinBuildHeight(), 16);
        int $$3 = Mth.floorDiv(p_110867_.getZ(), 16);
        if ($$2 >= 0 && $$2 < this.chunkGridSizeY) {
            $$1 = Mth.positiveModulo($$1, this.chunkGridSizeX);
            $$3 = Mth.positiveModulo($$3, this.chunkGridSizeZ);
            return this.chunks[this.getChunkIndex($$1, $$2, $$3)];
        } else {
            return null;
        }
    }
}
