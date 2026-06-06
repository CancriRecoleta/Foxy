//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class MemoryTracker {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

    public MemoryTracker() {
    }

    public static ByteBuffer create(int p_182528_) {
        long $$1 = ALLOCATOR.malloc((long)p_182528_);
        if ($$1 == 0L) {
            throw new OutOfMemoryError("Failed to allocate " + p_182528_ + " bytes");
        } else {
            return MemoryUtil.memByteBuffer($$1, p_182528_);
        }
    }

    public static ByteBuffer resize(ByteBuffer p_182530_, int p_182531_) {
        long $$2 = ALLOCATOR.realloc(MemoryUtil.memAddress0(p_182530_), (long)p_182531_);
        if ($$2 == 0L) {
            int var10002 = p_182530_.capacity();
            throw new OutOfMemoryError("Failed to resize buffer from " + var10002 + " bytes to " + p_182531_ + " bytes");
        } else {
            return MemoryUtil.memByteBuffer($$2, p_182531_);
        }
    }
}
