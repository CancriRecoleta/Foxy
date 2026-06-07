package com.github.foxy.client;

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public interface ICheekyClientChunkCache {
    @Nullable
    LevelChunk foxy$cheekyGetChunk(int x, int z);
}
