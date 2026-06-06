//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.world.ForgeChunkManager;

public class ForcedChunksSavedData extends SavedData {
    public static final String FILE_ID = "chunks";
    private static final String TAG_FORCED = "Forced";
    private final LongSet chunks;
    private ForgeChunkManager.TicketTracker<BlockPos> blockForcedChunks;
    private ForgeChunkManager.TicketTracker<UUID> entityForcedChunks;

    private ForcedChunksSavedData(LongSet p_151482_) {
        this.blockForcedChunks = new ForgeChunkManager.TicketTracker();
        this.entityForcedChunks = new ForgeChunkManager.TicketTracker();
        this.chunks = p_151482_;
    }

    public ForcedChunksSavedData() {
        this(new LongOpenHashSet());
    }

    public static ForcedChunksSavedData load(CompoundTag p_151484_) {
        ForcedChunksSavedData savedData = new ForcedChunksSavedData(new LongOpenHashSet(p_151484_.getLongArray("Forced")));
        ForgeChunkManager.readForgeForcedChunks(p_151484_, savedData.blockForcedChunks, savedData.entityForcedChunks);
        return savedData;
    }

    public CompoundTag save(CompoundTag p_46120_) {
        p_46120_.putLongArray("Forced", this.chunks.toLongArray());
        ForgeChunkManager.writeForgeForcedChunks(p_46120_, this.blockForcedChunks, this.entityForcedChunks);
        return p_46120_;
    }

    public LongSet getChunks() {
        return this.chunks;
    }

    public ForgeChunkManager.TicketTracker<BlockPos> getBlockForcedChunks() {
        return this.blockForcedChunks;
    }

    public ForgeChunkManager.TicketTracker<UUID> getEntityForcedChunks() {
        return this.entityForcedChunks;
    }
}
