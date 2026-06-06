//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.File;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigCommand {
    public ConfigCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.literal("config").then(net.minecraftforge.server.command.ConfigCommand.ShowFile.register()));
    }

    public static class ShowFile {
        public ShowFile() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)Commands.literal("showfile").requires((cs) -> {
                return cs.hasPermission(0);
            })).then(Commands.argument("mod", ModIdArgument.modIdArgument()).then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).executes(ShowFile::showFile)));
        }

        private static int showFile(CommandContext<CommandSourceStack> context) {
            String modId = (String)context.getArgument("mod", String.class);
            ModConfig.Type type = (ModConfig.Type)context.getArgument("type", ModConfig.Type.class);
            String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type);
            if (configFileName != null) {
                File f = new File(configFileName);
                ((CommandSourceStack)context.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.config.getwithtype", modId, type, Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((style) -> {
                        return style.withClickEvent(new ClickEvent(Action.OPEN_FILE, f.getAbsolutePath()));
                    }));
                }, true);
            } else {
                ((CommandSourceStack)context.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.config.noconfig", modId, type);
                }, true);
            }

            return 0;
        }
    }
}
