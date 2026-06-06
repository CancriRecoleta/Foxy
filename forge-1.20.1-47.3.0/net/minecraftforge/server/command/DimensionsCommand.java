//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;

class DimensionsCommand {
    DimensionsCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal("dimensions").requires((cs) -> {
            return cs.hasPermission(0);
        })).executes((ctx) -> {
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                return Component.translatable("commands.forge.dimensions.list");
            }, true);
            Registry<DimensionType> reg = ((CommandSourceStack)ctx.getSource()).registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
            Map<ResourceLocation, List<ResourceLocation>> types = new HashMap();
            Iterator var3 = ((CommandSourceStack)ctx.getSource()).getServer().getAllLevels().iterator();

            while(var3.hasNext()) {
                ServerLevel dim = (ServerLevel)var3.next();
                ((List)types.computeIfAbsent(reg.getKey(dim.dimensionType()), (k) -> {
                    return new ArrayList();
                })).add(dim.dimension().location());
            }

            types.keySet().stream().sorted().forEach((key) -> {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                    return Component.literal("" + key + ": " + (String)((List)types.get(key)).stream().map(ResourceLocation::toString).sorted().collect(Collectors.joining(", ")));
                }, false);
            });
            return 0;
        });
    }
}
