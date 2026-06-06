//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

public class GameModeArgument implements ArgumentType<GameType> {
    private static final Collection<String> EXAMPLES;
    private static final GameType[] VALUES;
    private static final DynamicCommandExceptionType ERROR_INVALID;

    public GameModeArgument() {
    }

    public GameType parse(StringReader p_260111_) throws CommandSyntaxException {
        String $$1 = p_260111_.readUnquotedString();
        GameType $$2 = GameType.byName($$1, (GameType)null);
        if ($$2 == null) {
            throw ERROR_INVALID.createWithContext(p_260111_, $$1);
        } else {
            return $$2;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_259767_, SuggestionsBuilder p_259515_) {
        return p_259767_.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(Arrays.stream(VALUES).map(GameType::getName), p_259515_) : Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static GameModeArgument gameMode() {
        return new GameModeArgument();
    }

    public static GameType getGameMode(CommandContext<CommandSourceStack> p_259927_, String p_260246_) throws CommandSyntaxException {
        return (GameType)p_259927_.getArgument(p_260246_, GameType.class);
    }

    static {
        EXAMPLES = (Collection)Stream.of(GameType.SURVIVAL, GameType.CREATIVE).map(GameType::getName).collect(Collectors.toList());
        VALUES = GameType.values();
        ERROR_INVALID = new DynamicCommandExceptionType((p_260119_) -> {
            return Component.translatable("argument.gamemode.invalid", p_260119_);
        });
    }
}
