//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
    private static final Component NO_RECURSIVE_TRACES = Component.translatable("commands.debug.function.noRecursion");
    private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
    private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
    final MinecraftServer server;
    @Nullable
    private ExecutionContext context;
    private List<CommandFunction> ticking = ImmutableList.of();
    private boolean postReload;
    private ServerFunctionLibrary library;

    public ServerFunctionManager(MinecraftServer p_136110_, ServerFunctionLibrary p_136111_) {
        this.server = p_136110_;
        this.library = p_136111_;
        this.postReload(p_136111_);
    }

    public int getCommandLimit() {
        return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }

    public void tick() {
        if (this.postReload) {
            this.postReload = false;
            Collection<CommandFunction> $$0 = this.library.getTag(LOAD_FUNCTION_TAG);
            this.executeTagFunctions($$0, LOAD_FUNCTION_TAG);
        }

        this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
    }

    private void executeTagFunctions(Collection<CommandFunction> p_136116_, ResourceLocation p_136117_) {
        ProfilerFiller var10000 = this.server.getProfiler();
        Objects.requireNonNull(p_136117_);
        var10000.push(p_136117_::toString);
        Iterator var3 = p_136116_.iterator();

        while(var3.hasNext()) {
            CommandFunction $$2 = (CommandFunction)var3.next();
            this.execute($$2, this.getGameLoopSender());
        }

        this.server.getProfiler().pop();
    }

    public int execute(CommandFunction p_136113_, CommandSourceStack p_136114_) {
        return this.execute(p_136113_, p_136114_, (TraceCallbacks)null);
    }

    public int execute(CommandFunction p_179961_, CommandSourceStack p_179962_, @Nullable TraceCallbacks p_179963_) {
        if (this.context != null) {
            if (p_179963_ != null) {
                this.context.reportError(NO_RECURSIVE_TRACES.getString());
                return 0;
            } else {
                this.context.delayFunctionCall(p_179961_, p_179962_);
                return 0;
            }
        } else {
            int var4;
            try {
                this.context = new ExecutionContext(p_179963_);
                var4 = this.context.runTopCommand(p_179961_, p_179962_);
            } finally {
                this.context = null;
            }

            return var4;
        }
    }

    public void replaceLibrary(ServerFunctionLibrary p_136121_) {
        this.library = p_136121_;
        this.postReload(p_136121_);
    }

    private void postReload(ServerFunctionLibrary p_136126_) {
        this.ticking = ImmutableList.copyOf(p_136126_.getTag(TICK_FUNCTION_TAG));
        this.postReload = true;
    }

    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }

    public Optional<CommandFunction> get(ResourceLocation p_136119_) {
        return this.library.getFunction(p_136119_);
    }

    public Collection<CommandFunction> getTag(ResourceLocation p_214332_) {
        return this.library.getTag(p_214332_);
    }

    public Iterable<ResourceLocation> getFunctionNames() {
        return this.library.getFunctions().keySet();
    }

    public Iterable<ResourceLocation> getTagNames() {
        return this.library.getAvailableTags();
    }

    public interface TraceCallbacks {
        void onCommand(int var1, String var2);

        void onReturn(int var1, String var2, int var3);

        void onError(int var1, String var2);

        void onCall(int var1, ResourceLocation var2, int var3);
    }

    private class ExecutionContext {
        private int depth;
        @Nullable
        private final TraceCallbacks tracer;
        private final Deque<QueuedCommand> commandQueue = Queues.newArrayDeque();
        private final List<QueuedCommand> nestedCalls = Lists.newArrayList();
        boolean abortCurrentDepth = false;

        ExecutionContext(@Nullable TraceCallbacks p_179971_) {
            this.tracer = p_179971_;
        }

        void delayFunctionCall(CommandFunction p_179973_, CommandSourceStack p_179974_) {
            int $$2 = ServerFunctionManager.this.getCommandLimit();
            CommandSourceStack $$3 = this.wrapSender(p_179974_);
            if (this.commandQueue.size() + this.nestedCalls.size() < $$2) {
                this.nestedCalls.add(new QueuedCommand($$3, this.depth, new CommandFunction.FunctionEntry(p_179973_)));
            }

        }

        private CommandSourceStack wrapSender(CommandSourceStack p_282848_) {
            IntConsumer $$1 = p_282848_.getReturnValueConsumer();
            return $$1 instanceof AbortingReturnValueConsumer ? p_282848_ : p_282848_.withReturnValueConsumer(new AbortingReturnValueConsumer($$1));
        }

        int runTopCommand(CommandFunction p_179978_, CommandSourceStack p_179979_) {
            int $$2 = ServerFunctionManager.this.getCommandLimit();
            CommandSourceStack $$3 = this.wrapSender(p_179979_);
            int $$4 = 0;
            CommandFunction.Entry[] $$5 = p_179978_.getEntries();

            for(int $$6 = $$5.length - 1; $$6 >= 0; --$$6) {
                this.commandQueue.push(new QueuedCommand($$3, 0, $$5[$$6]));
            }

            do {
                if (this.commandQueue.isEmpty()) {
                    return $$4;
                }

                try {
                    QueuedCommand $$7 = (QueuedCommand)this.commandQueue.removeFirst();
                    ProfilerFiller var10000 = ServerFunctionManager.this.server.getProfiler();
                    Objects.requireNonNull($$7);
                    var10000.push($$7::toString);
                    this.depth = $$7.depth;
                    $$7.execute(ServerFunctionManager.this, this.commandQueue, $$2, this.tracer);
                    if (!this.abortCurrentDepth) {
                        if (!this.nestedCalls.isEmpty()) {
                            List var11 = Lists.reverse(this.nestedCalls);
                            Deque var10001 = this.commandQueue;
                            Objects.requireNonNull(var10001);
                            var11.forEach(var10001::addFirst);
                        }
                    } else {
                        while(!this.commandQueue.isEmpty() && ((QueuedCommand)this.commandQueue.peek()).depth >= this.depth) {
                            this.commandQueue.removeFirst();
                        }

                        this.abortCurrentDepth = false;
                    }

                    this.nestedCalls.clear();
                } finally {
                    ServerFunctionManager.this.server.getProfiler().pop();
                }

                ++$$4;
            } while($$4 < $$2);

            return $$4;
        }

        public void reportError(String p_179976_) {
            if (this.tracer != null) {
                this.tracer.onError(this.depth, p_179976_);
            }

        }

        private class AbortingReturnValueConsumer implements IntConsumer {
            private final IntConsumer wrapped;

            AbortingReturnValueConsumer(IntConsumer p_281634_) {
                this.wrapped = p_281634_;
            }

            public void accept(int p_281286_) {
                this.wrapped.accept(p_281286_);
                ExecutionContext.this.abortCurrentDepth = true;
            }
        }
    }

    public static class QueuedCommand {
        private final CommandSourceStack sender;
        final int depth;
        private final CommandFunction.Entry entry;

        public QueuedCommand(CommandSourceStack p_179982_, int p_179983_, CommandFunction.Entry p_179984_) {
            this.sender = p_179982_;
            this.depth = p_179983_;
            this.entry = p_179984_;
        }

        public void execute(ServerFunctionManager p_179986_, Deque<QueuedCommand> p_179987_, int p_179988_, @Nullable TraceCallbacks p_179989_) {
            try {
                this.entry.execute(p_179986_, this.sender, p_179987_, p_179988_, this.depth, p_179989_);
            } catch (CommandSyntaxException var6) {
                CommandSyntaxException $$4 = var6;
                if (p_179989_ != null) {
                    p_179989_.onError(this.depth, $$4.getRawMessage().getString());
                }
            } catch (Exception var7) {
                Exception $$5 = var7;
                if (p_179989_ != null) {
                    p_179989_.onError(this.depth, $$5.getMessage());
                }
            }

        }

        public String toString() {
            return this.entry.toString();
        }
    }
}
