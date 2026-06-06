//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;

public class TagPredicate<T> {
    private final TagKey<T> tag;
    private final boolean expected;

    public TagPredicate(TagKey<T> p_270819_, boolean p_270913_) {
        this.tag = p_270819_;
        this.expected = p_270913_;
    }

    public static <T> TagPredicate<T> is(TagKey<T> p_270668_) {
        return new TagPredicate(p_270668_, true);
    }

    public static <T> TagPredicate<T> isNot(TagKey<T> p_270264_) {
        return new TagPredicate(p_270264_, false);
    }

    public boolean matches(Holder<T> p_270125_) {
        return p_270125_.is(this.tag) == this.expected;
    }

    public JsonElement serializeToJson() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("id", this.tag.location().toString());
        $$0.addProperty("expected", this.expected);
        return $$0;
    }

    public static <T> TagPredicate<T> fromJson(@Nullable JsonElement p_270982_, ResourceKey<? extends Registry<T>> p_270978_) {
        if (p_270982_ == null) {
            throw new JsonParseException("Expected a tag predicate");
        } else {
            JsonObject $$2 = GsonHelper.convertToJsonObject(p_270982_, "Tag Predicate");
            ResourceLocation $$3 = new ResourceLocation(GsonHelper.getAsString($$2, "id"));
            boolean $$4 = GsonHelper.getAsBoolean($$2, "expected");
            return new TagPredicate(TagKey.create(p_270978_, $$3), $$4);
        }
    }
}
