//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkBufferBuilderPack {
    private final Map<RenderType, BufferBuilder> builders = (Map)RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((p_108845_) -> {
        return p_108845_;
    }, (p_108843_) -> {
        return new BufferBuilder(p_108843_.bufferSize());
    }));

    public ChunkBufferBuilderPack() {
    }

    public BufferBuilder builder(RenderType p_108840_) {
        return (BufferBuilder)this.builders.get(p_108840_);
    }

    public void clearAll() {
        this.builders.values().forEach(BufferBuilder::clear);
    }

    public void discardAll() {
        this.builders.values().forEach(BufferBuilder::discard);
    }
}
