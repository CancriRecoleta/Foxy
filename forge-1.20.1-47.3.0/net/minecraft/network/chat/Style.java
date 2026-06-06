//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
    public static final Style EMPTY = new Style((TextColor)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (ClickEvent)null, (HoverEvent)null, (String)null, (ResourceLocation)null);
    public static final Codec<Style> FORMATTING_CODEC = RecordCodecBuilder.create((p_237256_) -> {
        return p_237256_.group(TextColor.CODEC.optionalFieldOf("color").forGetter((p_237281_) -> {
            return Optional.ofNullable(p_237281_.color);
        }), Codec.BOOL.optionalFieldOf("bold").forGetter((p_237279_) -> {
            return Optional.ofNullable(p_237279_.bold);
        }), Codec.BOOL.optionalFieldOf("italic").forGetter((p_237277_) -> {
            return Optional.ofNullable(p_237277_.italic);
        }), Codec.BOOL.optionalFieldOf("underlined").forGetter((p_237275_) -> {
            return Optional.ofNullable(p_237275_.underlined);
        }), Codec.BOOL.optionalFieldOf("strikethrough").forGetter((p_237273_) -> {
            return Optional.ofNullable(p_237273_.strikethrough);
        }), Codec.BOOL.optionalFieldOf("obfuscated").forGetter((p_237271_) -> {
            return Optional.ofNullable(p_237271_.obfuscated);
        }), Codec.STRING.optionalFieldOf("insertion").forGetter((p_237269_) -> {
            return Optional.ofNullable(p_237269_.insertion);
        }), ResourceLocation.CODEC.optionalFieldOf("font").forGetter((p_237267_) -> {
            return Optional.ofNullable(p_237267_.font);
        })).apply(p_237256_, Style::create);
    });
    public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
    @Nullable
    final TextColor color;
    @Nullable
    final Boolean bold;
    @Nullable
    final Boolean italic;
    @Nullable
    final Boolean underlined;
    @Nullable
    final Boolean strikethrough;
    @Nullable
    final Boolean obfuscated;
    @Nullable
    final ClickEvent clickEvent;
    @Nullable
    final HoverEvent hoverEvent;
    @Nullable
    final String insertion;
    @Nullable
    final ResourceLocation font;

    private static Style create(Optional<TextColor> p_237258_, Optional<Boolean> p_237259_, Optional<Boolean> p_237260_, Optional<Boolean> p_237261_, Optional<Boolean> p_237262_, Optional<Boolean> p_237263_, Optional<String> p_237264_, Optional<ResourceLocation> p_237265_) {
        return new Style((TextColor)p_237258_.orElse((Object)null), (Boolean)p_237259_.orElse((Object)null), (Boolean)p_237260_.orElse((Object)null), (Boolean)p_237261_.orElse((Object)null), (Boolean)p_237262_.orElse((Object)null), (Boolean)p_237263_.orElse((Object)null), (ClickEvent)null, (HoverEvent)null, (String)p_237264_.orElse((Object)null), (ResourceLocation)p_237265_.orElse((Object)null));
    }

    Style(@Nullable TextColor p_131113_, @Nullable Boolean p_131114_, @Nullable Boolean p_131115_, @Nullable Boolean p_131116_, @Nullable Boolean p_131117_, @Nullable Boolean p_131118_, @Nullable ClickEvent p_131119_, @Nullable HoverEvent p_131120_, @Nullable String p_131121_, @Nullable ResourceLocation p_131122_) {
        this.color = p_131113_;
        this.bold = p_131114_;
        this.italic = p_131115_;
        this.underlined = p_131116_;
        this.strikethrough = p_131117_;
        this.obfuscated = p_131118_;
        this.clickEvent = p_131119_;
        this.hoverEvent = p_131120_;
        this.insertion = p_131121_;
        this.font = p_131122_;
    }

    @Nullable
    public TextColor getColor() {
        return this.color;
    }

    public boolean isBold() {
        return this.bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return this.italic == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return this.strikethrough == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return this.underlined == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return this.obfuscated == Boolean.TRUE;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    @Nullable
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    @Nullable
    public String getInsertion() {
        return this.insertion;
    }

    public ResourceLocation getFont() {
        return this.font != null ? this.font : DEFAULT_FONT;
    }

    public Style withColor(@Nullable TextColor p_131149_) {
        return new Style(p_131149_, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withColor(@Nullable ChatFormatting p_131141_) {
        return this.withColor(p_131141_ != null ? TextColor.fromLegacyFormat(p_131141_) : null);
    }

    public Style withColor(int p_178521_) {
        return this.withColor(TextColor.fromRgb(p_178521_));
    }

    public Style withBold(@Nullable Boolean p_131137_) {
        return new Style(this.color, p_131137_, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withItalic(@Nullable Boolean p_131156_) {
        return new Style(this.color, this.bold, p_131156_, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withUnderlined(@Nullable Boolean p_131163_) {
        return new Style(this.color, this.bold, this.italic, p_131163_, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withStrikethrough(@Nullable Boolean p_178523_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, p_178523_, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withObfuscated(@Nullable Boolean p_178525_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, p_178525_, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style withClickEvent(@Nullable ClickEvent p_131143_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, p_131143_, this.hoverEvent, this.insertion, this.font);
    }

    public Style withHoverEvent(@Nullable HoverEvent p_131145_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, p_131145_, this.insertion, this.font);
    }

    public Style withInsertion(@Nullable String p_131139_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, p_131139_, this.font);
    }

    public Style withFont(@Nullable ResourceLocation p_131151_) {
        return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, p_131151_);
    }

    public Style applyFormat(ChatFormatting p_131158_) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        switch (p_131158_) {
            case OBFUSCATED -> $$6 = true;
            case BOLD -> $$2 = true;
            case STRIKETHROUGH -> $$4 = true;
            case UNDERLINE -> $$5 = true;
            case ITALIC -> $$3 = true;
            case RESET -> return EMPTY;
            default -> $$1 = TextColor.fromLegacyFormat(p_131158_);
        }

        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyLegacyFormat(ChatFormatting p_131165_) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        switch (p_131165_) {
            case OBFUSCATED:
                $$6 = true;
                break;
            case BOLD:
                $$2 = true;
                break;
            case STRIKETHROUGH:
                $$4 = true;
                break;
            case UNDERLINE:
                $$5 = true;
                break;
            case ITALIC:
                $$3 = true;
                break;
            case RESET:
                return EMPTY;
            default:
                $$6 = false;
                $$2 = false;
                $$4 = false;
                $$5 = false;
                $$3 = false;
                $$1 = TextColor.fromLegacyFormat(p_131165_);
        }

        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyFormats(ChatFormatting... p_131153_) {
        TextColor $$1 = this.color;
        Boolean $$2 = this.bold;
        Boolean $$3 = this.italic;
        Boolean $$4 = this.strikethrough;
        Boolean $$5 = this.underlined;
        Boolean $$6 = this.obfuscated;
        ChatFormatting[] var8 = p_131153_;
        int var9 = p_131153_.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            ChatFormatting $$7 = var8[var10];
            switch ($$7) {
                case OBFUSCATED:
                    $$6 = true;
                    break;
                case BOLD:
                    $$2 = true;
                    break;
                case STRIKETHROUGH:
                    $$4 = true;
                    break;
                case UNDERLINE:
                    $$5 = true;
                    break;
                case ITALIC:
                    $$3 = true;
                    break;
                case RESET:
                    return EMPTY;
                default:
                    $$1 = TextColor.fromLegacyFormat($$7);
            }
        }

        return new Style($$1, $$2, $$3, $$5, $$4, $$6, this.clickEvent, this.hoverEvent, this.insertion, this.font);
    }

    public Style applyTo(Style p_131147_) {
        if (this == EMPTY) {
            return p_131147_;
        } else {
            return p_131147_ == EMPTY ? this : new Style(this.color != null ? this.color : p_131147_.color, this.bold != null ? this.bold : p_131147_.bold, this.italic != null ? this.italic : p_131147_.italic, this.underlined != null ? this.underlined : p_131147_.underlined, this.strikethrough != null ? this.strikethrough : p_131147_.strikethrough, this.obfuscated != null ? this.obfuscated : p_131147_.obfuscated, this.clickEvent != null ? this.clickEvent : p_131147_.clickEvent, this.hoverEvent != null ? this.hoverEvent : p_131147_.hoverEvent, this.insertion != null ? this.insertion : p_131147_.insertion, this.font != null ? this.font : p_131147_.font);
        }
    }

    public String toString() {
        final StringBuilder $$0 = new StringBuilder("{");

        class Collector {
            private boolean isNotFirst;

            Collector() {
            }

            private void prependSeparator() {
                if (this.isNotFirst) {
                    $$0.append(',');
                }

                this.isNotFirst = true;
            }

            void addFlagString(String p_237290_, @Nullable Boolean p_237291_) {
                if (p_237291_ != null) {
                    this.prependSeparator();
                    if (!p_237291_) {
                        $$0.append('!');
                    }

                    $$0.append(p_237290_);
                }

            }

            void addValueString(String p_237293_, @Nullable Object p_237294_) {
                if (p_237294_ != null) {
                    this.prependSeparator();
                    $$0.append(p_237293_);
                    $$0.append('=');
                    $$0.append(p_237294_);
                }

            }
        }

        Collector $$1 = new Collector();
        $$1.addValueString("color", this.color);
        $$1.addFlagString("bold", this.bold);
        $$1.addFlagString("italic", this.italic);
        $$1.addFlagString("underlined", this.underlined);
        $$1.addFlagString("strikethrough", this.strikethrough);
        $$1.addFlagString("obfuscated", this.obfuscated);
        $$1.addValueString("clickEvent", this.clickEvent);
        $$1.addValueString("hoverEvent", this.hoverEvent);
        $$1.addValueString("insertion", this.insertion);
        $$1.addValueString("font", this.font);
        $$0.append("}");
        return $$0.toString();
    }

    public boolean equals(Object p_131175_) {
        if (this == p_131175_) {
            return true;
        } else if (!(p_131175_ instanceof Style)) {
            return false;
        } else {
            Style $$1 = (Style)p_131175_;
            return this.isBold() == $$1.isBold() && Objects.equals(this.getColor(), $$1.getColor()) && this.isItalic() == $$1.isItalic() && this.isObfuscated() == $$1.isObfuscated() && this.isStrikethrough() == $$1.isStrikethrough() && this.isUnderlined() == $$1.isUnderlined() && Objects.equals(this.getClickEvent(), $$1.getClickEvent()) && Objects.equals(this.getHoverEvent(), $$1.getHoverEvent()) && Objects.equals(this.getInsertion(), $$1.getInsertion()) && Objects.equals(this.getFont(), $$1.getFont());
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
    }

    public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
        public Serializer() {
        }

        @Nullable
        public Style deserialize(JsonElement p_131200_, Type p_131201_, JsonDeserializationContext p_131202_) throws JsonParseException {
            if (p_131200_.isJsonObject()) {
                JsonObject $$3 = p_131200_.getAsJsonObject();
                if ($$3 == null) {
                    return null;
                } else {
                    Boolean $$4 = getOptionalFlag($$3, "bold");
                    Boolean $$5 = getOptionalFlag($$3, "italic");
                    Boolean $$6 = getOptionalFlag($$3, "underlined");
                    Boolean $$7 = getOptionalFlag($$3, "strikethrough");
                    Boolean $$8 = getOptionalFlag($$3, "obfuscated");
                    TextColor $$9 = getTextColor($$3);
                    String $$10 = getInsertion($$3);
                    ClickEvent $$11 = getClickEvent($$3);
                    HoverEvent $$12 = getHoverEvent($$3);
                    ResourceLocation $$13 = getFont($$3);
                    return new Style($$9, $$4, $$5, $$6, $$7, $$8, $$11, $$12, $$10, $$13);
                }
            } else {
                return null;
            }
        }

        @Nullable
        private static ResourceLocation getFont(JsonObject p_131204_) {
            if (p_131204_.has("font")) {
                String $$1 = GsonHelper.getAsString(p_131204_, "font");

                try {
                    return new ResourceLocation($$1);
                } catch (ResourceLocationException var3) {
                    throw new JsonSyntaxException("Invalid font name: " + $$1);
                }
            } else {
                return null;
            }
        }

        @Nullable
        private static HoverEvent getHoverEvent(JsonObject p_131213_) {
            if (p_131213_.has("hoverEvent")) {
                JsonObject $$1 = GsonHelper.getAsJsonObject(p_131213_, "hoverEvent");
                HoverEvent $$2 = HoverEvent.deserialize($$1);
                if ($$2 != null && $$2.getAction().isAllowedFromServer()) {
                    return $$2;
                }
            }

            return null;
        }

        @Nullable
        private static ClickEvent getClickEvent(JsonObject p_131215_) {
            if (p_131215_.has("clickEvent")) {
                JsonObject $$1 = GsonHelper.getAsJsonObject(p_131215_, "clickEvent");
                String $$2 = GsonHelper.getAsString($$1, "action", (String)null);
                ClickEvent.Action $$3 = $$2 == null ? null : Action.getByName($$2);
                String $$4 = GsonHelper.getAsString($$1, "value", (String)null);
                if ($$3 != null && $$4 != null && $$3.isAllowedFromServer()) {
                    return new ClickEvent($$3, $$4);
                }
            }

            return null;
        }

        @Nullable
        private static String getInsertion(JsonObject p_131217_) {
            return GsonHelper.getAsString(p_131217_, "insertion", (String)null);
        }

        @Nullable
        private static TextColor getTextColor(JsonObject p_131223_) {
            if (p_131223_.has("color")) {
                String $$1 = GsonHelper.getAsString(p_131223_, "color");
                return TextColor.parseColor($$1);
            } else {
                return null;
            }
        }

        @Nullable
        private static Boolean getOptionalFlag(JsonObject p_131206_, String p_131207_) {
            return p_131206_.has(p_131207_) ? p_131206_.get(p_131207_).getAsBoolean() : null;
        }

        @Nullable
        public JsonElement serialize(Style p_131209_, Type p_131210_, JsonSerializationContext p_131211_) {
            if (p_131209_.isEmpty()) {
                return null;
            } else {
                JsonObject $$3 = new JsonObject();
                if (p_131209_.bold != null) {
                    $$3.addProperty("bold", p_131209_.bold);
                }

                if (p_131209_.italic != null) {
                    $$3.addProperty("italic", p_131209_.italic);
                }

                if (p_131209_.underlined != null) {
                    $$3.addProperty("underlined", p_131209_.underlined);
                }

                if (p_131209_.strikethrough != null) {
                    $$3.addProperty("strikethrough", p_131209_.strikethrough);
                }

                if (p_131209_.obfuscated != null) {
                    $$3.addProperty("obfuscated", p_131209_.obfuscated);
                }

                if (p_131209_.color != null) {
                    $$3.addProperty("color", p_131209_.color.serialize());
                }

                if (p_131209_.insertion != null) {
                    $$3.add("insertion", p_131211_.serialize(p_131209_.insertion));
                }

                if (p_131209_.clickEvent != null) {
                    JsonObject $$4 = new JsonObject();
                    $$4.addProperty("action", p_131209_.clickEvent.getAction().getName());
                    $$4.addProperty("value", p_131209_.clickEvent.getValue());
                    $$3.add("clickEvent", $$4);
                }

                if (p_131209_.hoverEvent != null) {
                    $$3.add("hoverEvent", p_131209_.hoverEvent.serialize());
                }

                if (p_131209_.font != null) {
                    $$3.addProperty("font", p_131209_.font.toString());
                }

                return $$3;
            }
        }
    }
}
