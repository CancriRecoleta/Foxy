//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

class ModListCommand {
    ModListCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal("mods").requires((cs) -> {
            return cs.hasPermission(0);
        })).executes((ctx) -> {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                return Component.translatable("commands.forge.mods.list", ModList.get().applyForEachModFile((modFile) -> {
                    return String.format(Locale.ROOT, "%s %s : %s (%s) - %d", modFile.getProvider().name().replace(' ', '_'), modFile.getFileName(), ((IModInfo)modFile.getModInfos().get(0)).getModId(), ((IModInfo)modFile.getModInfos().get(0)).getVersion(), modFile.getModInfos().size());
                }).collect(Collectors.joining("\n• ", "\n• ", "")));
            }, false);
            return 0;
        });
    }
}
