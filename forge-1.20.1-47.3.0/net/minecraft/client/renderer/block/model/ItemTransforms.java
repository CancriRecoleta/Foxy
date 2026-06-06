//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTransforms {
    public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
    public final ItemTransform thirdPersonLeftHand;
    public final ItemTransform thirdPersonRightHand;
    public final ItemTransform firstPersonLeftHand;
    public final ItemTransform firstPersonRightHand;
    public final ItemTransform head;
    public final ItemTransform gui;
    public final ItemTransform ground;
    public final ItemTransform fixed;
    public final ImmutableMap<ItemDisplayContext, ItemTransform> moddedTransforms;

    private ItemTransforms() {
        this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
    }

    /** @deprecated */
    @Deprecated
    public ItemTransforms(ItemTransforms p_111807_) {
        this.thirdPersonLeftHand = p_111807_.thirdPersonLeftHand;
        this.thirdPersonRightHand = p_111807_.thirdPersonRightHand;
        this.firstPersonLeftHand = p_111807_.firstPersonLeftHand;
        this.firstPersonRightHand = p_111807_.firstPersonRightHand;
        this.head = p_111807_.head;
        this.gui = p_111807_.gui;
        this.ground = p_111807_.ground;
        this.fixed = p_111807_.fixed;
        this.moddedTransforms = p_111807_.moddedTransforms;
    }

    /** @deprecated */
    @Deprecated
    public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_) {
        this(p_111798_, p_111799_, p_111800_, p_111801_, p_111802_, p_111803_, p_111804_, p_111805_, ImmutableMap.of());
    }

    public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_, ImmutableMap<ItemDisplayContext, ItemTransform> moddedTransforms) {
        this.thirdPersonLeftHand = p_111798_;
        this.thirdPersonRightHand = p_111799_;
        this.firstPersonLeftHand = p_111800_;
        this.firstPersonRightHand = p_111801_;
        this.head = p_111802_;
        this.gui = p_111803_;
        this.ground = p_111804_;
        this.fixed = p_111805_;
        this.moddedTransforms = moddedTransforms;
    }

    public ItemTransform getTransform(ItemDisplayContext p_270619_) {
        ItemTransform itemtransform;
        switch (p_270619_) {
            case THIRD_PERSON_LEFT_HAND -> itemtransform = this.thirdPersonLeftHand;
            case THIRD_PERSON_RIGHT_HAND -> itemtransform = this.thirdPersonRightHand;
            case FIRST_PERSON_LEFT_HAND -> itemtransform = this.firstPersonLeftHand;
            case FIRST_PERSON_RIGHT_HAND -> itemtransform = this.firstPersonRightHand;
            case HEAD -> itemtransform = this.head;
            case GUI -> itemtransform = this.gui;
            case GROUND -> itemtransform = this.ground;
            case FIXED -> itemtransform = this.fixed;
            default -> return (ItemTransform)this.moddedTransforms.getOrDefault(p_270619_, ItemTransform.NO_TRANSFORM);
        }

        return itemtransform;
    }

    public boolean hasTransform(ItemDisplayContext p_270365_) {
        return this.getTransform(p_270365_) != ItemTransform.NO_TRANSFORM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<ItemTransforms> {
        public Deserializer() {
        }

        public ItemTransforms deserialize(JsonElement p_111820_, Type p_111821_, JsonDeserializationContext p_111822_) throws JsonParseException {
            JsonObject jsonobject = p_111820_.getAsJsonObject();
            ItemTransform itemtransform = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            ItemTransform itemtransform1 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            if (itemtransform1 == ItemTransform.NO_TRANSFORM) {
                itemtransform1 = itemtransform;
            }

            ItemTransform itemtransform2 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
            ItemTransform itemtransform3 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
            if (itemtransform3 == ItemTransform.NO_TRANSFORM) {
                itemtransform3 = itemtransform2;
            }

            ItemTransform itemtransform4 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.HEAD);
            ItemTransform itemtransform5 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.GUI);
            ItemTransform itemtransform6 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.GROUND);
            ItemTransform itemtransform7 = this.getTransform(p_111822_, jsonobject, ItemDisplayContext.FIXED);
            ImmutableMap.Builder<ItemDisplayContext, ItemTransform> builder = ImmutableMap.builder();
            ItemDisplayContext[] var14 = ItemDisplayContext.values();
            int var15 = var14.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                ItemDisplayContext type = var14[var16];
                if (type.isModded()) {
                    ItemTransform transform = this.getTransform(p_111822_, jsonobject, type);

                    for(ItemDisplayContext fallbackType = type; transform == ItemTransform.NO_TRANSFORM && fallbackType.fallback() != null; transform = this.getTransform(p_111822_, jsonobject, fallbackType)) {
                        fallbackType = fallbackType.fallback();
                    }

                    if (transform != ItemTransform.NO_TRANSFORM) {
                        builder.put(type, transform);
                    }
                }
            }

            return new ItemTransforms(itemtransform1, itemtransform, itemtransform3, itemtransform2, itemtransform4, itemtransform5, itemtransform6, itemtransform7, builder.build());
        }

        private ItemTransform getTransform(JsonDeserializationContext p_270385_, JsonObject p_270436_, ItemDisplayContext p_270100_) {
            String s = p_270100_.getSerializedName();
            return p_270436_.has(s) ? (ItemTransform)p_270385_.deserialize(p_270436_.get(s), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
        }
    }
}
