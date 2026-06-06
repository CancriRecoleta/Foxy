//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
    public KickCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_137796_) {
        p_137796_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires((p_137800_) -> {
            return p_137800_.hasPermission(3);
        })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_137806_) -> {
            return kickPlayers((CommandSourceStack)p_137806_.getSource(), EntityArgument.getPlayers(p_137806_, "targets"), Component.translatable("multiplayer.disconnect.kicked"));
        })).then(Commands.argument("reason", MessageArgument.message()).executes((p_137798_) -> {
            return kickPlayers((CommandSourceStack)p_137798_.getSource(), EntityArgument.getPlayers(p_137798_, "targets"), MessageArgument.getMessage(p_137798_, "reason"));
        }))));
    }

    private static int kickPlayers(CommandSourceStack p_137802_, Collection<ServerPlayer> p_137803_, Component p_137804_) {
        Iterator var3 = p_137803_.iterator();

        while(var3.hasNext()) {
            ServerPlayer $$3 = (ServerPlayer)var3.next();
            $$3.connection.disconnect(p_137804_);
            p_137802_.sendSuccess(() -> {
                return Component.translatable("commands.kick.success", $$3.getDisplayName(), p_137804_);
            }, true);
        }

        return p_137803_.size();
    }
}
