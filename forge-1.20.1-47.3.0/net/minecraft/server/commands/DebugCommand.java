//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class DebugCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(Component.translatable("commands.debug.alreadyRunning"));

    public DebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_136906_) {
        p_136906_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires((p_180073_) -> {
            return p_180073_.hasPermission(3);
        })).then(Commands.literal("start").executes((p_180069_) -> {
            return start((CommandSourceStack)p_180069_.getSource());
        }))).then(Commands.literal("stop").executes((p_136918_) -> {
            return stop((CommandSourceStack)p_136918_.getSource());
        }))).then(((LiteralArgumentBuilder)Commands.literal("function").requires((p_180071_) -> {
            return p_180071_.hasPermission(3);
        })).then(Commands.argument("name", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).executes((p_136908_) -> {
            return traceFunction((CommandSourceStack)p_136908_.getSource(), FunctionArgument.getFunctions(p_136908_, "name"));
        }))));
    }

    private static int start(CommandSourceStack p_136910_) throws CommandSyntaxException {
        MinecraftServer $$1 = p_136910_.getServer();
        if ($$1.isTimeProfilerRunning()) {
            throw ERROR_ALREADY_RUNNING.create();
        } else {
            $$1.startTimeProfiler();
            p_136910_.sendSuccess(() -> {
                return Component.translatable("commands.debug.started");
            }, true);
            return 0;
        }
    }

    private static int stop(CommandSourceStack p_136916_) throws CommandSyntaxException {
        MinecraftServer $$1 = p_136916_.getServer();
        if (!$$1.isTimeProfilerRunning()) {
            throw ERROR_NOT_RUNNING.create();
        } else {
            ProfileResults $$2 = $$1.stopTimeProfiler();
            double $$3 = (double)$$2.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
            double $$4 = (double)$$2.getTickDuration() / $$3;
            p_136916_.sendSuccess(() -> {
                return Component.translatable("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", $$3), $$2.getTickDuration(), String.format(Locale.ROOT, "%.2f", $$4));
            }, true);
            return (int)$$4;
        }
    }

    private static int traceFunction(CommandSourceStack p_180066_, Collection<CommandFunction> p_180067_) {
        int $$2 = 0;
        MinecraftServer $$3 = p_180066_.getServer();
        String $$4 = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";

        try {
            Path $$5 = $$3.getFile("debug").toPath();
            Files.createDirectories($$5);
            Writer $$6 = Files.newBufferedWriter($$5.resolve($$4), StandardCharsets.UTF_8);

            try {
                PrintWriter $$7 = new PrintWriter($$6);

                CommandFunction $$8;
                Tracer $$9;
                for(Iterator var8 = p_180067_.iterator(); var8.hasNext(); $$2 += p_180066_.getServer().getFunctions().execute($$8, p_180066_.withSource($$9).withMaximumPermission(2), $$9)) {
                    $$8 = (CommandFunction)var8.next();
                    $$7.println($$8.getId());
                    $$9 = new Tracer($$7);
                }
            } catch (Throwable var12) {
                if ($$6 != null) {
                    try {
                        $$6.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }

                throw var12;
            }

            if ($$6 != null) {
                $$6.close();
            }
        } catch (IOException | UncheckedIOException var13) {
            Exception $$10 = var13;
            LOGGER.warn("Tracing failed", $$10);
            p_180066_.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
        }

        int $$11 = $$2;
        if (p_180067_.size() == 1) {
            p_180066_.sendSuccess(() -> {
                return Component.translatable("commands.debug.function.success.single", $$11, ((CommandFunction)p_180067_.iterator().next()).getId(), $$4);
            }, true);
        } else {
            p_180066_.sendSuccess(() -> {
                return Component.translatable("commands.debug.function.success.multiple", $$11, p_180067_.size(), $$4);
            }, true);
        }

        return $$2;
    }

    private static class Tracer implements ServerFunctionManager.TraceCallbacks, CommandSource {
        public static final int INDENT_OFFSET = 1;
        private final PrintWriter output;
        private int lastIndent;
        private boolean waitingForResult;

        Tracer(PrintWriter p_180079_) {
            this.output = p_180079_;
        }

        private void indentAndSave(int p_180082_) {
            this.printIndent(p_180082_);
            this.lastIndent = p_180082_;
        }

        private void printIndent(int p_180098_) {
            for(int $$1 = 0; $$1 < p_180098_ + 1; ++$$1) {
                this.output.write("    ");
            }

        }

        private void newLine() {
            if (this.waitingForResult) {
                this.output.println();
                this.waitingForResult = false;
            }

        }

        public void onCommand(int p_180084_, String p_180085_) {
            this.newLine();
            this.indentAndSave(p_180084_);
            this.output.print("[C] ");
            this.output.print(p_180085_);
            this.waitingForResult = true;
        }

        public void onReturn(int p_180087_, String p_180088_, int p_180089_) {
            if (this.waitingForResult) {
                this.output.print(" -> ");
                this.output.println(p_180089_);
                this.waitingForResult = false;
            } else {
                this.indentAndSave(p_180087_);
                this.output.print("[R = ");
                this.output.print(p_180089_);
                this.output.print("] ");
                this.output.println(p_180088_);
            }

        }

        public void onCall(int p_180091_, ResourceLocation p_180092_, int p_180093_) {
            this.newLine();
            this.indentAndSave(p_180091_);
            this.output.print("[F] ");
            this.output.print(p_180092_);
            this.output.print(" size=");
            this.output.println(p_180093_);
        }

        public void onError(int p_180100_, String p_180101_) {
            this.newLine();
            this.indentAndSave(p_180100_ + 1);
            this.output.print("[E] ");
            this.output.print(p_180101_);
        }

        public void sendSystemMessage(Component p_214427_) {
            this.newLine();
            this.printIndent(this.lastIndent + 1);
            this.output.print("[M] ");
            this.output.println(p_214427_.getString());
        }

        public boolean acceptsSuccess() {
            return true;
        }

        public boolean acceptsFailure() {
            return true;
        }

        public boolean shouldInformAdmins() {
            return false;
        }

        public boolean alwaysAccepts() {
            return true;
        }
    }
}
