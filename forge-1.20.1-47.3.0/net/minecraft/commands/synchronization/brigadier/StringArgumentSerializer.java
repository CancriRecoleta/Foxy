//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentTypeInfo<StringArgumentType, Template> {
    public StringArgumentSerializer() {
    }

    public void serializeToNetwork(Template p_235616_, FriendlyByteBuf p_235617_) {
        p_235617_.writeEnum(p_235616_.type);
    }

    public Template deserializeFromNetwork(FriendlyByteBuf p_235619_) {
        StringArgumentType.StringType $$1 = (StringArgumentType.StringType)p_235619_.readEnum(StringArgumentType.StringType.class);
        return new Template($$1);
    }

    public void serializeToJson(Template p_235613_, JsonObject p_235614_) {
        String var10002;
        switch (p_235613_.type) {
            case SINGLE_WORD -> var10002 = "word";
            case QUOTABLE_PHRASE -> var10002 = "phrase";
            case GREEDY_PHRASE -> var10002 = "greedy";
            default -> throw new IncompatibleClassChangeError();
        }

        p_235614_.addProperty("type", var10002);
    }

    public Template unpack(StringArgumentType p_235605_) {
        return new Template(p_235605_.getType());
    }

    public final class Template implements ArgumentTypeInfo.Template<StringArgumentType> {
        final StringArgumentType.StringType type;

        public Template(StringArgumentType.StringType p_235626_) {
            this.type = p_235626_;
        }

        public StringArgumentType instantiate(CommandBuildContext p_235629_) {
            StringArgumentType var10000;
            switch (this.type) {
                case SINGLE_WORD -> var10000 = StringArgumentType.word();
                case QUOTABLE_PHRASE -> var10000 = StringArgumentType.string();
                case GREEDY_PHRASE -> var10000 = StringArgumentType.greedyString();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public ArgumentTypeInfo<StringArgumentType, ?> type() {
            return StringArgumentSerializer.this;
        }
    }
}
