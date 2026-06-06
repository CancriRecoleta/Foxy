//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IForgeTagAppender<T> {
    private TagsProvider.TagAppender<T> self() {
        return (TagsProvider.TagAppender)this;
    }

    default TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
        TagsProvider.TagAppender<T> builder = this.self();
        TagKey[] var3 = values;
        int var4 = values.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TagKey<T> value = var3[var5];
            builder.addTag(value);
        }

        return builder;
    }

    default TagsProvider.TagAppender<T> addOptionalTag(TagKey<T> value) {
        return this.self().addOptionalTag(value.location());
    }

    default TagsProvider.TagAppender<T> addOptionalTags(TagKey<T>... values) {
        TagsProvider.TagAppender<T> builder = this.self();
        TagKey[] var3 = values;
        int var4 = values.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TagKey<T> value = var3[var5];
            builder.addOptionalTag(value.location());
        }

        return builder;
    }

    default TagsProvider.TagAppender<T> replace() {
        return this.replace(true);
    }

    default TagsProvider.TagAppender<T> replace(boolean value) {
        this.self().getInternalBuilder().replace(value);
        return this.self();
    }

    default TagsProvider.TagAppender<T> remove(ResourceLocation location) {
        TagsProvider.TagAppender<T> builder = this.self();
        builder.getInternalBuilder().removeElement(location, builder.getModID());
        return builder;
    }

    default TagsProvider.TagAppender<T> remove(ResourceLocation first, ResourceLocation... locations) {
        this.remove(first);
        ResourceLocation[] var3 = locations;
        int var4 = locations.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ResourceLocation location = var3[var5];
            this.remove(location);
        }

        return this.self();
    }

    default TagsProvider.TagAppender<T> remove(ResourceKey<T> resourceKey) {
        this.remove(resourceKey.location());
        return this.self();
    }

    default TagsProvider.TagAppender<T> remove(ResourceKey<T> firstResourceKey, ResourceKey<T>... resourceKeys) {
        this.remove(firstResourceKey.location());
        ResourceKey[] var3 = resourceKeys;
        int var4 = resourceKeys.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ResourceKey<T> resourceKey = var3[var5];
            this.remove(resourceKey.location());
        }

        return this.self();
    }

    default TagsProvider.TagAppender<T> remove(TagKey<T> tag) {
        TagsProvider.TagAppender<T> builder = this.self();
        builder.getInternalBuilder().removeTag(tag.location(), builder.getModID());
        return builder;
    }

    default TagsProvider.TagAppender<T> remove(TagKey<T> first, TagKey<T>... tags) {
        this.remove(first);
        TagKey[] var3 = tags;
        int var4 = tags.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            TagKey<T> tag = var3[var5];
            this.remove(tag);
        }

        return this.self();
    }
}
