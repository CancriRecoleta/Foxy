//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    @Nullable
    private final ResourceLocation background;
    private final FrameType frame;
    private final boolean showToast;
    private final boolean announceChat;
    private final boolean hidden;
    private float x;
    private float y;

    public DisplayInfo(ItemStack p_14969_, Component p_14970_, Component p_14971_, @Nullable ResourceLocation p_14972_, FrameType p_14973_, boolean p_14974_, boolean p_14975_, boolean p_14976_) {
        this.title = p_14970_;
        this.description = p_14971_;
        this.icon = p_14969_;
        this.background = p_14972_;
        this.frame = p_14973_;
        this.showToast = p_14974_;
        this.announceChat = p_14975_;
        this.hidden = p_14976_;
    }

    public void setLocation(float p_14979_, float p_14980_) {
        this.x = p_14979_;
        this.y = p_14980_;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    @Nullable
    public ResourceLocation getBackground() {
        return this.background;
    }

    public FrameType getFrame() {
        return this.frame;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceChat() {
        return this.announceChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static DisplayInfo fromJson(JsonObject p_14982_) {
        Component $$1 = Serializer.fromJson(p_14982_.get("title"));
        Component $$2 = Serializer.fromJson(p_14982_.get("description"));
        if ($$1 != null && $$2 != null) {
            ItemStack $$3 = getIcon(GsonHelper.getAsJsonObject(p_14982_, "icon"));
            ResourceLocation $$4 = p_14982_.has("background") ? new ResourceLocation(GsonHelper.getAsString(p_14982_, "background")) : null;
            FrameType $$5 = p_14982_.has("frame") ? FrameType.byName(GsonHelper.getAsString(p_14982_, "frame")) : FrameType.TASK;
            boolean $$6 = GsonHelper.getAsBoolean(p_14982_, "show_toast", true);
            boolean $$7 = GsonHelper.getAsBoolean(p_14982_, "announce_to_chat", true);
            boolean $$8 = GsonHelper.getAsBoolean(p_14982_, "hidden", false);
            return new DisplayInfo($$3, $$1, $$2, $$4, $$5, $$6, $$7, $$8);
        } else {
            throw new JsonSyntaxException("Both title and description must be set");
        }
    }

    private static ItemStack getIcon(JsonObject p_14987_) {
        if (!p_14987_.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        } else {
            Item $$1 = GsonHelper.getAsItem(p_14987_, "item");
            if (p_14987_.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                ItemStack $$2 = new ItemStack($$1);
                if (p_14987_.has("nbt")) {
                    try {
                        CompoundTag $$3 = TagParser.parseTag(GsonHelper.convertToString(p_14987_.get("nbt"), "nbt"));
                        $$2.setTag($$3);
                    } catch (CommandSyntaxException var4) {
                        CommandSyntaxException $$4 = var4;
                        throw new JsonSyntaxException("Invalid nbt tag: " + $$4.getMessage());
                    }
                }

                return $$2;
            }
        }
    }

    public void serializeToNetwork(FriendlyByteBuf p_14984_) {
        p_14984_.writeComponent(this.title);
        p_14984_.writeComponent(this.description);
        p_14984_.writeItem(this.icon);
        p_14984_.writeEnum(this.frame);
        int $$1 = 0;
        if (this.background != null) {
            $$1 |= 1;
        }

        if (this.showToast) {
            $$1 |= 2;
        }

        if (this.hidden) {
            $$1 |= 4;
        }

        p_14984_.writeInt($$1);
        if (this.background != null) {
            p_14984_.writeResourceLocation(this.background);
        }

        p_14984_.writeFloat(this.x);
        p_14984_.writeFloat(this.y);
    }

    public static DisplayInfo fromNetwork(FriendlyByteBuf p_14989_) {
        Component $$1 = p_14989_.readComponent();
        Component $$2 = p_14989_.readComponent();
        ItemStack $$3 = p_14989_.readItem();
        FrameType $$4 = (FrameType)p_14989_.readEnum(FrameType.class);
        int $$5 = p_14989_.readInt();
        ResourceLocation $$6 = ($$5 & 1) != 0 ? p_14989_.readResourceLocation() : null;
        boolean $$7 = ($$5 & 2) != 0;
        boolean $$8 = ($$5 & 4) != 0;
        DisplayInfo $$9 = new DisplayInfo($$3, $$1, $$2, $$6, $$4, $$7, false, $$8);
        $$9.setLocation(p_14989_.readFloat(), p_14989_.readFloat());
        return $$9;
    }

    public JsonElement serializeToJson() {
        JsonObject $$0 = new JsonObject();
        $$0.add("icon", this.serializeIcon());
        $$0.add("title", Serializer.toJsonTree(this.title));
        $$0.add("description", Serializer.toJsonTree(this.description));
        $$0.addProperty("frame", this.frame.getName());
        $$0.addProperty("show_toast", this.showToast);
        $$0.addProperty("announce_to_chat", this.announceChat);
        $$0.addProperty("hidden", this.hidden);
        if (this.background != null) {
            $$0.addProperty("background", this.background.toString());
        }

        return $$0;
    }

    private JsonObject serializeIcon() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("item", BuiltInRegistries.ITEM.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            $$0.addProperty("nbt", this.icon.getTag().toString());
        }

        return $$0;
    }
}
