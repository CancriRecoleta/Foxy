//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries.tags;

import java.util.stream.Stream;
import net.minecraft.tags.TagKey;

public interface IReverseTag<V> {
    Stream<TagKey<V>> getTagKeys();

    boolean containsTag(TagKey<V> var1);

    default boolean containsTag(ITag<V> tag) {
        return this.containsTag(tag.getKey());
    }
}
