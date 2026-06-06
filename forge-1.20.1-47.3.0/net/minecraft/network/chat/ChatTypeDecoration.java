//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.util.StringRepresentable;

public record ChatTypeDecoration(String translationKey, List<Parameter> parameters, Style style) {
    public static final Codec<ChatTypeDecoration> CODEC = RecordCodecBuilder.create((p_239989_) -> {
        return p_239989_.group(Codec.STRING.fieldOf("translation_key").forGetter(ChatTypeDecoration::translationKey), net.minecraft.network.chat.ChatTypeDecoration.Parameter.CODEC.listOf().fieldOf("parameters").forGetter(ChatTypeDecoration::parameters), Style.FORMATTING_CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(ChatTypeDecoration::style)).apply(p_239989_, ChatTypeDecoration::new);
    });

    public ChatTypeDecoration(String translationKey, List<Parameter> parameters, Style style) {
        this.translationKey = translationKey;
        this.parameters = parameters;
        this.style = style;
    }

    public static ChatTypeDecoration withSender(String p_239223_) {
        return new ChatTypeDecoration(p_239223_, List.of(net.minecraft.network.chat.ChatTypeDecoration.Parameter.SENDER, net.minecraft.network.chat.ChatTypeDecoration.Parameter.CONTENT), Style.EMPTY);
    }

    public static ChatTypeDecoration incomingDirectMessage(String p_239425_) {
        Style $$1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
        return new ChatTypeDecoration(p_239425_, List.of(net.minecraft.network.chat.ChatTypeDecoration.Parameter.SENDER, net.minecraft.network.chat.ChatTypeDecoration.Parameter.CONTENT), $$1);
    }

    public static ChatTypeDecoration outgoingDirectMessage(String p_240772_) {
        Style $$1 = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
        return new ChatTypeDecoration(p_240772_, List.of(net.minecraft.network.chat.ChatTypeDecoration.Parameter.TARGET, net.minecraft.network.chat.ChatTypeDecoration.Parameter.CONTENT), $$1);
    }

    public static ChatTypeDecoration teamMessage(String p_239095_) {
        return new ChatTypeDecoration(p_239095_, List.of(net.minecraft.network.chat.ChatTypeDecoration.Parameter.TARGET, net.minecraft.network.chat.ChatTypeDecoration.Parameter.SENDER, net.minecraft.network.chat.ChatTypeDecoration.Parameter.CONTENT), Style.EMPTY);
    }

    public Component decorate(Component p_241301_, ChatType.Bound p_241391_) {
        Object[] $$2 = this.resolveParameters(p_241301_, p_241391_);
        return Component.translatable(this.translationKey, $$2).withStyle(this.style);
    }

    private Component[] resolveParameters(Component p_241365_, ChatType.Bound p_241559_) {
        Component[] $$2 = new Component[this.parameters.size()];

        for(int $$3 = 0; $$3 < $$2.length; ++$$3) {
            Parameter $$4 = (Parameter)this.parameters.get($$3);
            $$2[$$3] = $$4.select(p_241365_, p_241559_);
        }

        return $$2;
    }

    public String translationKey() {
        return this.translationKey;
    }

    public List<Parameter> parameters() {
        return this.parameters;
    }

    public Style style() {
        return this.style;
    }

    public static enum Parameter implements StringRepresentable {
        SENDER("sender", (p_241238_, p_241239_) -> {
            return p_241239_.name();
        }),
        TARGET("target", (p_241236_, p_241237_) -> {
            return p_241237_.targetName();
        }),
        CONTENT("content", (p_239974_, p_241427_) -> {
            return p_239974_;
        });

        public static final Codec<Parameter> CODEC = StringRepresentable.fromEnum(Parameter::values);
        private final String name;
        private final Selector selector;

        private Parameter(String p_239588_, Selector p_239589_) {
            this.name = p_239588_;
            this.selector = p_239589_;
        }

        public Component select(Component p_241369_, ChatType.Bound p_241509_) {
            Component $$2 = this.selector.select(p_241369_, p_241509_);
            return (Component)Objects.requireNonNullElse($$2, CommonComponents.EMPTY);
        }

        public String getSerializedName() {
            return this.name;
        }

        public interface Selector {
            @Nullable
            Component select(Component var1, ChatType.Bound var2);
        }
    }
}
