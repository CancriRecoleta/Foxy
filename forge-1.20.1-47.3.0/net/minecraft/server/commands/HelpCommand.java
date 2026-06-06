//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HelpCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.help.failed"));

    public HelpCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_137788_) {
        p_137788_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((p_288460_) -> {
            Map<CommandNode<CommandSourceStack>, String> $$2 = p_137788_.getSmartUsage(p_137788_.getRoot(), (CommandSourceStack)p_288460_.getSource());
            Iterator var3 = $$2.values().iterator();

            while(var3.hasNext()) {
                String $$3 = (String)var3.next();
                ((CommandSourceStack)p_288460_.getSource()).sendSuccess(() -> {
                    return Component.literal("/" + $$3);
                }, false);
            }

            return $$2.size();
        })).then(Commands.argument("command", StringArgumentType.greedyString()).executes((p_288458_) -> {
            ParseResults<CommandSourceStack> $$2 = p_137788_.parse(StringArgumentType.getString(p_288458_, "command"), (CommandSourceStack)p_288458_.getSource());
            if ($$2.getContext().getNodes().isEmpty()) {
                throw ERROR_FAILED.create();
            } else {
                Map<CommandNode<CommandSourceStack>, String> $$3 = p_137788_.getSmartUsage(((ParsedCommandNode)Iterables.getLast($$2.getContext().getNodes())).getNode(), (CommandSourceStack)p_288458_.getSource());
                Iterator var4 = $$3.values().iterator();

                while(var4.hasNext()) {
                    String $$4 = (String)var4.next();
                    ((CommandSourceStack)p_288458_.getSource()).sendSuccess(() -> {
                        String var10000 = $$2.getReader().getString();
                        return Component.literal("/" + var10000 + " " + $$4);
                    }, false);
                }

                return $$3.size();
            }
        })));
    }
}
