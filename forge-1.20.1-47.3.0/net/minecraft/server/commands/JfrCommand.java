//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;

public class JfrCommand {
    private static final SimpleCommandExceptionType START_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.jfr.start.failed"));
    private static final DynamicCommandExceptionType DUMP_FAILED = new DynamicCommandExceptionType((p_183652_) -> {
        return Component.translatable("commands.jfr.dump.failed", p_183652_);
    });

    private JfrCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_183646_) {
        p_183646_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("jfr").requires((p_183661_) -> {
            return p_183661_.hasPermission(4);
        })).then(Commands.literal("start").executes((p_183657_) -> {
            return startJfr((CommandSourceStack)p_183657_.getSource());
        }))).then(Commands.literal("stop").executes((p_183648_) -> {
            return stopJfr((CommandSourceStack)p_183648_.getSource());
        })));
    }

    private static int startJfr(CommandSourceStack p_183650_) throws CommandSyntaxException {
        Environment $$1 = Environment.from(p_183650_.getServer());
        if (!JvmProfiler.INSTANCE.start($$1)) {
            throw START_FAILED.create();
        } else {
            p_183650_.sendSuccess(() -> {
                return Component.translatable("commands.jfr.started");
            }, false);
            return 1;
        }
    }

    private static int stopJfr(CommandSourceStack p_183659_) throws CommandSyntaxException {
        try {
            Path $$1 = Paths.get(".").relativize(JvmProfiler.INSTANCE.stop().normalize());
            Path $$2 = p_183659_.getServer().isPublished() && !SharedConstants.IS_RUNNING_IN_IDE ? $$1 : $$1.toAbsolutePath();
            Component $$3 = Component.literal($$1.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((p_183655_) -> {
                return p_183655_.withClickEvent(new ClickEvent(Action.COPY_TO_CLIPBOARD, $$2.toString())).withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")));
            });
            p_183659_.sendSuccess(() -> {
                return Component.translatable("commands.jfr.stopped", $$3);
            }, false);
            return 1;
        } catch (Throwable var4) {
            Throwable $$4 = var4;
            throw DUMP_FAILED.create($$4.getMessage());
        }
    }
}
