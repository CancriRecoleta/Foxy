//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;

public class DebugMobSpawningCommand {
    public DebugMobSpawningCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_180111_) {
        LiteralArgumentBuilder<CommandSourceStack> $$1 = (LiteralArgumentBuilder)Commands.literal("debugmobspawning").requires((p_180113_) -> {
            return p_180113_.hasPermission(2);
        });
        MobCategory[] var2 = MobCategory.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            MobCategory $$2 = var2[var4];
            $$1.then(Commands.literal($$2.getName()).then(Commands.argument("at", BlockPosArgument.blockPos()).executes((p_180109_) -> {
                return spawnMobs((CommandSourceStack)p_180109_.getSource(), $$2, BlockPosArgument.getLoadedBlockPos(p_180109_, "at"));
            })));
        }

        p_180111_.register($$1);
    }

    private static int spawnMobs(CommandSourceStack p_180115_, MobCategory p_180116_, BlockPos p_180117_) {
        NaturalSpawner.spawnCategoryForPosition(p_180116_, p_180115_.getLevel(), p_180117_);
        return 1;
    }
}
