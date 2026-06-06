//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.translatable("argument.player.unknown"));

    public GameProfileArgument() {
    }

    public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> p_94591_, String p_94592_) throws CommandSyntaxException {
        return ((Result)p_94591_.getArgument(p_94592_, Result.class)).getNames((CommandSourceStack)p_94591_.getSource());
    }

    public static GameProfileArgument gameProfile() {
        return new GameProfileArgument();
    }

    public Result parse(StringReader p_94586_) throws CommandSyntaxException {
        if (p_94586_.canRead() && p_94586_.peek() == '@') {
            EntitySelectorParser $$1 = new EntitySelectorParser(p_94586_);
            EntitySelector $$2 = $$1.parse();
            if ($$2.includesEntities()) {
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
            } else {
                return new SelectorResult($$2);
            }
        } else {
            int $$3 = p_94586_.getCursor();

            while(p_94586_.canRead() && p_94586_.peek() != ' ') {
                p_94586_.skip();
            }

            String $$4 = p_94586_.getString().substring($$3, p_94586_.getCursor());
            return (p_94595_) -> {
                Optional<GameProfile> $$2 = p_94595_.getServer().getProfileCache().get($$4);
                SimpleCommandExceptionType var10001 = ERROR_UNKNOWN_PLAYER;
                Objects.requireNonNull(var10001);
                return Collections.singleton((GameProfile)$$2.orElseThrow(var10001::create));
            };
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_94598_, SuggestionsBuilder p_94599_) {
        if (p_94598_.getSource() instanceof SharedSuggestionProvider) {
            StringReader $$2 = new StringReader(p_94599_.getInput());
            $$2.setCursor(p_94599_.getStart());
            EntitySelectorParser $$3 = new EntitySelectorParser($$2);

            try {
                $$3.parse();
            } catch (CommandSyntaxException var6) {
            }

            return $$3.fillSuggestions(p_94599_, (p_94589_) -> {
                SharedSuggestionProvider.suggest((Iterable)((SharedSuggestionProvider)p_94598_.getSource()).getOnlinePlayerNames(), p_94589_);
            });
        } else {
            return Suggestions.empty();
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public interface Result {
        Collection<GameProfile> getNames(CommandSourceStack var1) throws CommandSyntaxException;
    }

    public static class SelectorResult implements Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector p_94605_) {
            this.selector = p_94605_;
        }

        public Collection<GameProfile> getNames(CommandSourceStack p_94607_) throws CommandSyntaxException {
            List<ServerPlayer> $$1 = this.selector.findPlayers(p_94607_);
            if ($$1.isEmpty()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            } else {
                List<GameProfile> $$2 = Lists.newArrayList();
                Iterator var4 = $$1.iterator();

                while(var4.hasNext()) {
                    ServerPlayer $$3 = (ServerPlayer)var4.next();
                    $$2.add($$3.getGameProfile());
                }

                return $$2;
            }
        }
    }
}
