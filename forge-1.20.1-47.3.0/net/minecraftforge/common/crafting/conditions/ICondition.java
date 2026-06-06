//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.crafting.CraftingHelper;

public interface ICondition {
    static boolean shouldRegisterEntry(JsonElement json) {
        if (json instanceof JsonObject obj) {
            if (obj.has("forge:conditions")) {
                return CraftingHelper.processConditions(obj, "forge:conditions", net.minecraftforge.common.crafting.conditions.ICondition.IContext.TAGS_INVALID);
            }
        }

        return true;
    }

    ResourceLocation getID();

    boolean test(IContext var1);

    public interface IContext {
        IContext EMPTY = new IContext() {
            public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
                return Collections.emptyMap();
            }
        };
        IContext TAGS_INVALID = new IContext() {
            public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
                throw new UnsupportedOperationException("Usage of tag-based conditions is not permitted in this context!");
            }
        };

        default <T> Collection<Holder<T>> getTag(TagKey<T> key) {
            return (Collection)this.getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
        }

        <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> var1);
    }
}
