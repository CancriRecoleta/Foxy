//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.command.CommandHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ClientCommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static CommandDispatcher<CommandSourceStack> commands = null;

    public ClientCommandHandler() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientCommandHandler::handleClientPlayerLogin);
    }

    private static void handleClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPacketListener connection = event.getPlayer().connection;
        connection.commands = mergeServerCommands(new CommandDispatcher(), CommandBuildContext.simple(connection.registryAccess(), connection.enabledFeatures()));
    }

    @Internal
    public static CommandDispatcher<SharedSuggestionProvider> mergeServerCommands(CommandDispatcher<SharedSuggestionProvider> serverCommands, CommandBuildContext buildContext) {
        CommandDispatcher<CommandSourceStack> commandsTemp = new CommandDispatcher();
        MinecraftForge.EVENT_BUS.post(new RegisterClientCommandsEvent(commandsTemp, buildContext));
        commands = new CommandDispatcher();
        copy(commandsTemp.getRoot(), commands.getRoot());
        RootCommandNode<SharedSuggestionProvider> serverCommandsRoot = serverCommands.getRoot();
        CommandDispatcher<SharedSuggestionProvider> newServerCommands = new CommandDispatcher();
        copy(serverCommandsRoot, newServerCommands.getRoot());
        CommandHelper.mergeCommandNode(commands.getRoot(), newServerCommands.getRoot(), new IdentityHashMap(), getSource(), (context) -> {
            return 0;
        }, (suggestions) -> {
            SuggestionProvider<SharedSuggestionProvider> suggestionProvider = SuggestionProviders.safelySwap(suggestions);
            if (suggestionProvider == SuggestionProviders.ASK_SERVER) {
                suggestionProvider = (context, builder) -> {
                    ClientCommandSourceStack source = getSource();
                    StringReader reader = new StringReader(context.getInput());
                    if (reader.canRead() && reader.peek() == '/') {
                        reader.skip();
                    }

                    ParseResults<CommandSourceStack> parse = commands.parse(reader, source);
                    return commands.getCompletionSuggestions(parse);
                };
            }

            return suggestionProvider;
        });
        return newServerCommands;
    }

    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return commands;
    }

    public static ClientCommandSourceStack getSource() {
        LocalPlayer player = Minecraft.getInstance().player;
        return new ClientCommandSourceStack(player, player.position(), player.getRotationVector(), player.getPermissionLevel(), player.getName().getString(), player.getDisplayName(), player);
    }

    private static <S> void copy(CommandNode<S> sourceNode, CommandNode<S> resultNode) {
        Map<CommandNode<S>, CommandNode<S>> newNodes = new IdentityHashMap();
        newNodes.put(sourceNode, resultNode);
        Iterator var3 = sourceNode.getChildren().iterator();

        while(var3.hasNext()) {
            CommandNode<S> child = (CommandNode)var3.next();
            CommandNode<S> copy = (CommandNode)newNodes.computeIfAbsent(child, (innerChild) -> {
                ArgumentBuilder<S, ?> builder = innerChild.createBuilder();
                CommandNode<S> innerCopy = builder.build();
                copy(innerChild, innerCopy);
                return innerCopy;
            });
            resultNode.addChild(copy);
        }

    }

    public static boolean runCommand(String command) {
        StringReader reader = new StringReader(command);
        ClientCommandSourceStack source = getSource();

        try {
            commands.execute(reader, source);
        } catch (CommandRuntimeException var6) {
            CommandRuntimeException execution = var6;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("").append(execution.getComponent()).withStyle(ChatFormatting.RED));
        } catch (CommandSyntaxException var7) {
            CommandSyntaxException syntax = var7;
            if (syntax.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand() || syntax.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()) {
                return false;
            }

            Minecraft.getInstance().player.sendSystemMessage(Component.literal("").append(ComponentUtils.fromMessage(syntax.getRawMessage())).withStyle(ChatFormatting.RED));
            if (syntax.getInput() != null && syntax.getCursor() >= 0) {
                int position = Math.min(syntax.getInput().length(), syntax.getCursor());
                MutableComponent details = Component.literal("").withStyle(ChatFormatting.GRAY).withStyle((style) -> {
                    return style.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, reader.getString()));
                });
                if (position > 10) {
                    details.append("...");
                }

                details.append(syntax.getInput().substring(Math.max(0, position - 10), position));
                if (position < syntax.getInput().length()) {
                    details.append((Component)Component.literal(syntax.getInput().substring(position)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE));
                }

                details.append((Component)Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("").append((Component)details).withStyle(ChatFormatting.RED));
            }
        } catch (Exception var8) {
            Exception generic = var8;
            MutableComponent message = Component.literal(generic.getMessage() == null ? generic.getClass().getName() : generic.getMessage());
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("command.failed").withStyle(ChatFormatting.RED).withStyle((style) -> {
                return style.withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, message));
            }));
            LOGGER.error("Error executing client command \"{}\"", command, generic);
        }

        return true;
    }
}
