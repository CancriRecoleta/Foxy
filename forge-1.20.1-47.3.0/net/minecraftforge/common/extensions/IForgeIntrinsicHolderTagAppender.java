//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IForgeIntrinsicHolderTagAppender<T> extends IForgeTagAppender<T> {
    private IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> self() {
        return (IntrinsicHolderTagsProvider.IntrinsicTagAppender)this;
    }

    ResourceKey<T> getKey(T var1);

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(T entry) {
        return this.remove(this.getKey(entry));
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(T first, T... entries) {
        this.remove(first);
        Object[] var3 = entries;
        int var4 = entries.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            T entry = var3[var5];
            this.remove(entry);
        }

        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> addTags(TagKey<T>... values) {
        IForgeTagAppender.super.addTags(values);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace() {
        IForgeTagAppender.super.replace();
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace(boolean value) {
        IForgeTagAppender.super.replace(value);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceLocation location) {
        IForgeTagAppender.super.remove(location);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceLocation first, ResourceLocation... locations) {
        IForgeTagAppender.super.remove(first, locations);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceKey<T> resourceKey) {
        IForgeTagAppender.super.remove(resourceKey);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceKey<T> firstResourceKey, ResourceKey<T>... resourceKeys) {
        IForgeTagAppender.super.remove(firstResourceKey, resourceKeys);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> tag) {
        IForgeTagAppender.super.remove(tag);
        return this.self();
    }

    default IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> first, TagKey<T>... tags) {
        IForgeTagAppender.super.remove(first, tags);
        return this.self();
    }
}
