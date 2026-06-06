//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class SetSpawnCommand {
    public SetSpawnCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_138644_) {
        p_138644_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires((p_138648_) -> {
            return p_138648_.hasPermission(2);
        })).executes((p_274828_) -> {
            return setSpawn((CommandSourceStack)p_274828_.getSource(), Collections.singleton(((CommandSourceStack)p_274828_.getSource()).getPlayerOrException()), BlockPos.containing(((CommandSourceStack)p_274828_.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_274829_) -> {
            return setSpawn((CommandSourceStack)p_274829_.getSource(), EntityArgument.getPlayers(p_274829_, "targets"), BlockPos.containing(((CommandSourceStack)p_274829_.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_138655_) -> {
            return setSpawn((CommandSourceStack)p_138655_.getSource(), EntityArgument.getPlayers(p_138655_, "targets"), BlockPosArgument.getSpawnablePos(p_138655_, "pos"), 0.0F);
        })).then(Commands.argument("angle", AngleArgument.angle()).executes((p_138646_) -> {
            return setSpawn((CommandSourceStack)p_138646_.getSource(), EntityArgument.getPlayers(p_138646_, "targets"), BlockPosArgument.getSpawnablePos(p_138646_, "pos"), AngleArgument.getAngle(p_138646_, "angle"));
        })))));
    }

    private static int setSpawn(CommandSourceStack p_138650_, Collection<ServerPlayer> p_138651_, BlockPos p_138652_, float p_138653_) {
        ResourceKey<Level> $$4 = p_138650_.getLevel().dimension();
        Iterator var5 = p_138651_.iterator();

        while(var5.hasNext()) {
            ServerPlayer $$5 = (ServerPlayer)var5.next();
            $$5.setRespawnPosition($$4, p_138652_, p_138653_, true, false);
        }

        String $$6 = $$4.location().toString();
        if (p_138651_.size() == 1) {
            p_138650_.sendSuccess(() -> {
                return Component.translatable("commands.spawnpoint.success.single", p_138652_.getX(), p_138652_.getY(), p_138652_.getZ(), p_138653_, $$6, ((ServerPlayer)p_138651_.iterator().next()).getDisplayName());
            }, true);
        } else {
            p_138650_.sendSuccess(() -> {
                return Component.translatable("commands.spawnpoint.success.multiple", p_138652_.getX(), p_138652_.getY(), p_138652_.getZ(), p_138653_, $$6, p_138651_.size());
            }, true);
        }

        return p_138651_.size();
    }
}
