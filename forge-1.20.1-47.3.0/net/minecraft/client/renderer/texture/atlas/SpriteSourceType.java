//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record SpriteSourceType(Codec<? extends SpriteSource> codec) {
    public SpriteSourceType(Codec<? extends SpriteSource> codec) {
        this.codec = codec;
    }

    public Codec<? extends SpriteSource> codec() {
        return this.codec;
    }
}
