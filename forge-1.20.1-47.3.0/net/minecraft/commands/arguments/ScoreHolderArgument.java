//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;

public class ScoreHolderArgument implements ArgumentType<Result> {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_SCORE_HOLDERS = (p_108221_, p_108222_) -> {
        StringReader $$2 = new StringReader(p_108222_.getInput());
        $$2.setCursor(p_108222_.getStart());
        EntitySelectorParser $$3 = new EntitySelectorParser($$2);

        try {
            $$3.parse();
        } catch (CommandSyntaxException var5) {
        }

        return $$3.fillSuggestions(p_108222_, (p_171606_) -> {
            SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)p_108221_.getSource()).getOnlinePlayerNames(), p_171606_);
        });
    };
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
    private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(Component.translatable("argument.scoreHolder.empty"));
    final boolean multiple;

    public ScoreHolderArgument(boolean p_108216_) {
        this.multiple = p_108216_;
    }

    public static String getName(CommandContext<CommandSourceStack> p_108224_, String p_108225_) throws CommandSyntaxException {
        return (String)getNames(p_108224_, p_108225_).iterator().next();
    }

    public static Collection<String> getNames(CommandContext<CommandSourceStack> p_108244_, String p_108245_) throws CommandSyntaxException {
        return getNames(p_108244_, p_108245_, Collections::emptyList);
    }

    public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSourceStack> p_108247_, String p_108248_) throws CommandSyntaxException {
        ServerScoreboard var10002 = ((CommandSourceStack)p_108247_.getSource()).getServer().getScoreboard();
        Objects.requireNonNull(var10002);
        return getNames(p_108247_, p_108248_, var10002::getTrackedPlayers);
    }

    public static Collection<String> getNames(CommandContext<CommandSourceStack> p_108227_, String p_108228_, Supplier<Collection<String>> p_108229_) throws CommandSyntaxException {
        Collection<String> $$3 = ((Result)p_108227_.getArgument(p_108228_, Result.class)).getNames((CommandSourceStack)p_108227_.getSource(), p_108229_);
        if ($$3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        } else {
            return $$3;
        }
    }

    public static ScoreHolderArgument scoreHolder() {
        return new ScoreHolderArgument(false);
    }

    public static ScoreHolderArgument scoreHolders() {
        return new ScoreHolderArgument(true);
    }

    public Result parse(StringReader p_108219_) throws CommandSyntaxException {
        if (p_108219_.canRead() && p_108219_.peek() == '@') {
            EntitySelectorParser $$1 = new EntitySelectorParser(p_108219_);
            EntitySelector $$2 = $$1.parse();
            if (!this.multiple && $$2.getMaxResults() > 1) {
                throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            } else {
                return new SelectorResult($$2);
            }
        } else {
            int $$3 = p_108219_.getCursor();

            while(p_108219_.canRead() && p_108219_.peek() != ' ') {
                p_108219_.skip();
            }

            String $$4 = p_108219_.getString().substring($$3, p_108219_.getCursor());
            if ($$4.equals("*")) {
                return (p_108231_, p_108232_) -> {
                    Collection<String> $$2 = (Collection)p_108232_.get();
                    if ($$2.isEmpty()) {
                        throw ERROR_NO_RESULTS.create();
                    } else {
                        return $$2;
                    }
                };
            } else {
                Collection<String> $$5 = Collections.singleton($$4);
                return (p_108237_, p_108238_) -> {
                    return $$5;
                };
            }
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public interface Result {
        Collection<String> getNames(CommandSourceStack var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
    }

    public static class SelectorResult implements Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector p_108256_) {
            this.selector = p_108256_;
        }

        public Collection<String> getNames(CommandSourceStack p_108258_, Supplier<Collection<String>> p_108259_) throws CommandSyntaxException {
            List<? extends Entity> $$2 = this.selector.findEntities(p_108258_);
            if ($$2.isEmpty()) {
                throw EntityArgument.NO_ENTITIES_FOUND.create();
            } else {
                List<String> $$3 = Lists.newArrayList();
                Iterator var5 = $$2.iterator();

                while(var5.hasNext()) {
                    Entity $$4 = (Entity)var5.next();
                    $$3.add($$4.getScoreboardName());
                }

                return $$3;
            }
        }
    }

    public static class Info implements ArgumentTypeInfo<ScoreHolderArgument, Template> {
        private static final byte FLAG_MULTIPLE = 1;

        public Info() {
        }

        public void serializeToNetwork(Template p_233469_, FriendlyByteBuf p_233470_) {
            int $$2 = 0;
            if (p_233469_.multiple) {
                $$2 |= 1;
            }

            p_233470_.writeByte($$2);
        }

        public Template deserializeFromNetwork(FriendlyByteBuf p_233480_) {
            byte $$1 = p_233480_.readByte();
            boolean $$2 = ($$1 & 1) != 0;
            return new Template($$2);
        }

        public void serializeToJson(Template p_233466_, JsonObject p_233467_) {
            p_233467_.addProperty("amount", p_233466_.multiple ? "multiple" : "single");
        }

        public Template unpack(ScoreHolderArgument p_233472_) {
            return new Template(p_233472_.multiple);
        }

        public final class Template implements ArgumentTypeInfo.Template<ScoreHolderArgument> {
            final boolean multiple;

            Template(boolean p_233487_) {
                this.multiple = p_233487_;
            }

            public ScoreHolderArgument instantiate(CommandBuildContext p_233490_) {
                return new ScoreHolderArgument(this.multiple);
            }

            public ArgumentTypeInfo<ScoreHolderArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
