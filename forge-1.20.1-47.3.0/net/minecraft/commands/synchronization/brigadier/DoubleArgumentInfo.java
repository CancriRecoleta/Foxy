//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentInfo implements ArgumentTypeInfo<DoubleArgumentType, Template> {
    public DoubleArgumentInfo() {
    }

    public void serializeToNetwork(Template p_235485_, FriendlyByteBuf p_235486_) {
        boolean $$2 = p_235485_.min != -1.7976931348623157E308;
        boolean $$3 = p_235485_.max != Double.MAX_VALUE;
        p_235486_.writeByte(ArgumentUtils.createNumberFlags($$2, $$3));
        if ($$2) {
            p_235486_.writeDouble(p_235485_.min);
        }

        if ($$3) {
            p_235486_.writeDouble(p_235485_.max);
        }

    }

    public Template deserializeFromNetwork(FriendlyByteBuf p_235488_) {
        byte $$1 = p_235488_.readByte();
        double $$2 = ArgumentUtils.numberHasMin($$1) ? p_235488_.readDouble() : -1.7976931348623157E308;
        double $$3 = ArgumentUtils.numberHasMax($$1) ? p_235488_.readDouble() : Double.MAX_VALUE;
        return new Template($$2, $$3);
    }

    public void serializeToJson(Template p_235482_, JsonObject p_235483_) {
        if (p_235482_.min != -1.7976931348623157E308) {
            p_235483_.addProperty("min", p_235482_.min);
        }

        if (p_235482_.max != Double.MAX_VALUE) {
            p_235483_.addProperty("max", p_235482_.max);
        }

    }

    public Template unpack(DoubleArgumentType p_235474_) {
        return new Template(p_235474_.getMinimum(), p_235474_.getMaximum());
    }

    public final class Template implements ArgumentTypeInfo.Template<DoubleArgumentType> {
        final double min;
        final double max;

        Template(double p_235496_, double p_235497_) {
            this.min = p_235496_;
            this.max = p_235497_;
        }

        public DoubleArgumentType instantiate(CommandBuildContext p_235500_) {
            return DoubleArgumentType.doubleArg(this.min, this.max);
        }

        public ArgumentTypeInfo<DoubleArgumentType, ?> type() {
            return DoubleArgumentInfo.this;
        }
    }
}
