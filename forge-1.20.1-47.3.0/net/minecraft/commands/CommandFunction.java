//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
    private final Entry[] entries;
    final ResourceLocation id;

    public CommandFunction(ResourceLocation p_77979_, Entry[] p_77980_) {
        this.id = p_77979_;
        this.entries = p_77980_;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public static CommandFunction fromLines(ResourceLocation p_77985_, CommandDispatcher<CommandSourceStack> p_77986_, CommandSourceStack p_77987_, List<String> p_77988_) {
        List<Entry> $$4 = Lists.newArrayListWithCapacity(p_77988_.size());

        for(int $$5 = 0; $$5 < p_77988_.size(); ++$$5) {
            int $$6 = $$5 + 1;
            String $$7 = ((String)p_77988_.get($$5)).trim();
            StringReader $$8 = new StringReader($$7);
            if ($$8.canRead() && $$8.peek() != '#') {
                if ($$8.peek() == '/') {
                    $$8.skip();
                    if ($$8.peek() == '/') {
                        throw new IllegalArgumentException("Unknown or invalid command '" + $$7 + "' on line " + $$6 + " (if you intended to make a comment, use '#' not '//')");
                    }

                    String $$9 = $$8.readUnquotedString();
                    throw new IllegalArgumentException("Unknown or invalid command '" + $$7 + "' on line " + $$6 + " (did you mean '" + $$9 + "'? Do not use a preceding forwards slash.)");
                }

                try {
                    ParseResults<CommandSourceStack> $$10 = p_77986_.parse($$8, p_77987_);
                    if ($$10.getReader().canRead()) {
                        throw Commands.getParseException($$10);
                    }

                    $$4.add(new CommandEntry($$10));
                } catch (CommandSyntaxException var10) {
                    CommandSyntaxException $$11 = var10;
                    throw new IllegalArgumentException("Whilst parsing command on line " + $$6 + ": " + $$11.getMessage());
                }
            }
        }

        return new CommandFunction(p_77985_, (Entry[])$$4.toArray(new Entry[0]));
    }

    @FunctionalInterface
    public interface Entry {
        void execute(ServerFunctionManager var1, CommandSourceStack var2, Deque<ServerFunctionManager.QueuedCommand> var3, int var4, int var5, @Nullable ServerFunctionManager.TraceCallbacks var6) throws CommandSyntaxException;
    }

    public static class CommandEntry implements Entry {
        private final ParseResults<CommandSourceStack> parse;

        public CommandEntry(ParseResults<CommandSourceStack> p_78006_) {
            this.parse = p_78006_;
        }

        public void execute(ServerFunctionManager p_164879_, CommandSourceStack p_164880_, Deque<ServerFunctionManager.QueuedCommand> p_164881_, int p_164882_, int p_164883_, @Nullable ServerFunctionManager.TraceCallbacks p_164884_) throws CommandSyntaxException {
            if (p_164884_ != null) {
                String $$6 = this.parse.getReader().getString();
                p_164884_.onCommand(p_164883_, $$6);
                int $$7 = this.execute(p_164879_, p_164880_);
                p_164884_.onReturn(p_164883_, $$6, $$7);
            } else {
                this.execute(p_164879_, p_164880_);
            }

        }

        private int execute(ServerFunctionManager p_164876_, CommandSourceStack p_164877_) throws CommandSyntaxException {
            return p_164876_.getDispatcher().execute(Commands.mapSource(this.parse, (p_242934_) -> {
                return p_164877_;
            }));
        }

        public String toString() {
            return this.parse.getReader().getString();
        }
    }

    public static class CacheableFunction {
        public static final CacheableFunction NONE = new CacheableFunction((ResourceLocation)null);
        @Nullable
        private final ResourceLocation id;
        private boolean resolved;
        private Optional<CommandFunction> function = Optional.empty();

        public CacheableFunction(@Nullable ResourceLocation p_77998_) {
            this.id = p_77998_;
        }

        public CacheableFunction(CommandFunction p_77996_) {
            this.resolved = true;
            this.id = null;
            this.function = Optional.of(p_77996_);
        }

        public Optional<CommandFunction> get(ServerFunctionManager p_78003_) {
            if (!this.resolved) {
                if (this.id != null) {
                    this.function = p_78003_.get(this.id);
                }

                this.resolved = true;
            }

            return this.function;
        }

        @Nullable
        public ResourceLocation getId() {
            return (ResourceLocation)this.function.map((p_78001_) -> {
                return p_78001_.id;
            }).orElse(this.id);
        }
    }

    public static class FunctionEntry implements Entry {
        private final CacheableFunction function;

        public FunctionEntry(CommandFunction p_78019_) {
            this.function = new CacheableFunction(p_78019_);
        }

        public void execute(ServerFunctionManager p_164902_, CommandSourceStack p_164903_, Deque<ServerFunctionManager.QueuedCommand> p_164904_, int p_164905_, int p_164906_, @Nullable ServerFunctionManager.TraceCallbacks p_164907_) {
            Util.ifElse(this.function.get(p_164902_), (p_164900_) -> {
                Entry[] $$6 = p_164900_.getEntries();
                if (p_164907_ != null) {
                    p_164907_.onCall(p_164906_, p_164900_.getId(), $$6.length);
                }

                int $$7 = p_164905_ - p_164904_.size();
                int $$8 = Math.min($$6.length, $$7);

                for(int $$9 = $$8 - 1; $$9 >= 0; --$$9) {
                    p_164904_.addFirst(new ServerFunctionManager.QueuedCommand(p_164903_, p_164906_ + 1, $$6[$$9]));
                }

            }, () -> {
                if (p_164907_ != null) {
                    p_164907_.onCall(p_164906_, this.function.getId(), -1);
                }

            });
        }

        public String toString() {
            return "function " + this.function.getId();
        }
    }
}
