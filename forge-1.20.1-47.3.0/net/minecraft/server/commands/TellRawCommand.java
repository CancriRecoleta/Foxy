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
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
    public TellRawCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_139064_) {
        p_139064_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires((p_139068_) -> {
            return p_139068_.hasPermission(2);
        })).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent()).executes((p_139066_) -> {
            int $$1 = 0;

            for(Iterator var2 = EntityArgument.getPlayers(p_139066_, "targets").iterator(); var2.hasNext(); ++$$1) {
                ServerPlayer $$2 = (ServerPlayer)var2.next();
                $$2.sendSystemMessage(ComponentUtils.updateForEntity((CommandSourceStack)p_139066_.getSource(), (Component)ComponentArgument.getComponent(p_139066_, "message"), $$2, 0), false);
            }

            return $$1;
        }))));
    }
}
