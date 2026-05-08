package com.github.foxy.client;

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ICheekyClientChunkCache {
    @Nullable
    LevelChunk foxy$cheekyGetChunk(int x, int z);

    void foxy$forEachLoadedChunk(Consumer<LevelChunk> consumer);
}
