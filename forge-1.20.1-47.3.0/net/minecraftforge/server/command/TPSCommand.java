//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;

class TPSCommand {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");
    private static final long[] UNLOADED = new long[]{0L};

    TPSCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tps").requires((cs) -> {
            return cs.hasPermission(0);
        })).then(Commands.argument("dim", DimensionArgument.dimension()).executes((ctx) -> {
            return sendTime((CommandSourceStack)ctx.getSource(), DimensionArgument.getDimension(ctx, "dim"));
        }))).executes((ctx) -> {
            Iterator var1 = ((CommandSourceStack)ctx.getSource()).getServer().getAllLevels().iterator();

            while(var1.hasNext()) {
                ServerLevel dim = (ServerLevel)var1.next();
                sendTime((CommandSourceStack)ctx.getSource(), dim);
            }

            double meanTickTime = (double)mean(((CommandSourceStack)ctx.getSource()).getServer().tickTimes) * 1.0E-6;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20.0);
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                return Component.translatable("commands.forge.tps.summary.all", TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS));
            }, false);
            return 0;
        });
    }

    private static int sendTime(CommandSourceStack cs, ServerLevel dim) throws CommandSyntaxException {
        long[] times = cs.getServer().getTickTime(dim.dimension());
        if (times == null) {
            times = UNLOADED;
        }

        Registry<DimensionType> reg = cs.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        double worldTickTime = (double)mean(times) * 1.0E-6;
        double worldTPS = Math.min(1000.0 / worldTickTime, 20.0);
        cs.sendSuccess(() -> {
            return Component.translatable("commands.forge.tps.summary.named", dim.dimension().location().toString(), reg.getKey(dim.dimensionType()), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS));
        }, false);
        return 1;
    }

    private static long mean(long[] values) {
        long sum = 0L;
        long[] var3 = values;
        int var4 = values.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            long v = var3[var5];
            sum += v;
        }

        return sum / (long)values.length;
    }
}
