//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class BannerPattern {
    final String hashname;

    public BannerPattern(String p_222696_) {
        this.hashname = p_222696_;
    }

    public static ResourceLocation location(ResourceKey<BannerPattern> p_222698_, boolean p_222699_) {
        String $$2 = p_222699_ ? "banner" : "shield";
        return p_222698_.location().withPrefix("entity/" + $$2 + "/");
    }

    public String getHashname() {
        return this.hashname;
    }

    @Nullable
    public static Holder<BannerPattern> byHash(String p_222701_) {
        return (Holder)BuiltInRegistries.BANNER_PATTERN.holders().filter((p_222704_) -> {
            return ((BannerPattern)p_222704_.value()).hashname.equals(p_222701_);
        }).findAny().orElse((Object)null);
    }

    public static class Builder {
        private final List<Pair<Holder<BannerPattern>, DyeColor>> patterns = Lists.newArrayList();

        public Builder() {
        }

        public Builder addPattern(ResourceKey<BannerPattern> p_222706_, DyeColor p_222707_) {
            return this.addPattern((Holder)BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(p_222706_), p_222707_);
        }

        public Builder addPattern(Holder<BannerPattern> p_222709_, DyeColor p_222710_) {
            return this.addPattern(Pair.of(p_222709_, p_222710_));
        }

        public Builder addPattern(Pair<Holder<BannerPattern>, DyeColor> p_155049_) {
            this.patterns.add(p_155049_);
            return this;
        }

        public ListTag toListTag() {
            ListTag $$0 = new ListTag();
            Iterator var2 = this.patterns.iterator();

            while(var2.hasNext()) {
                Pair<Holder<BannerPattern>, DyeColor> $$1 = (Pair)var2.next();
                CompoundTag $$2 = new CompoundTag();
                $$2.putString("Pattern", ((BannerPattern)((Holder)$$1.getFirst()).value()).hashname);
                $$2.putInt("Color", ((DyeColor)$$1.getSecond()).getId());
                $$0.add($$2);
            }

            return $$0;
        }
    }
}
