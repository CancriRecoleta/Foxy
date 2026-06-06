//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
    public DefaultGameModeCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_136927_) {
        p_136927_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires((p_136929_) -> {
            return p_136929_.hasPermission(2);
        })).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes((p_258227_) -> {
            return setMode((CommandSourceStack)p_258227_.getSource(), GameModeArgument.getGameMode(p_258227_, "gamemode"));
        })));
    }

    private static int setMode(CommandSourceStack p_136931_, GameType p_136932_) {
        int $$2 = 0;
        MinecraftServer $$3 = p_136931_.getServer();
        $$3.setDefaultGameType(p_136932_);
        GameType $$4 = $$3.getForcedGameType();
        if ($$4 != null) {
            Iterator var5 = $$3.getPlayerList().getPlayers().iterator();

            while(var5.hasNext()) {
                ServerPlayer $$5 = (ServerPlayer)var5.next();
                if ($$5.setGameMode($$4)) {
                    ++$$2;
                }
            }
        }

        p_136931_.sendSuccess(() -> {
            return Component.translatable("commands.defaultgamemode.success", p_136932_.getLongDisplayName());
        }, true);
        return $$2;
    }
}
