//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;

public class FillBiomeCommand {
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("argument.pos.unloaded"));
    private static final Dynamic2CommandExceptionType ERROR_VOLUME_TOO_LARGE = new Dynamic2CommandExceptionType((p_262025_, p_261647_) -> {
        return Component.translatable("commands.fillbiome.toobig", p_262025_, p_261647_);
    });

    public FillBiomeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_261867_, CommandBuildContext p_262155_) {
        p_261867_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fillbiome").requires((p_261890_) -> {
            return p_261890_.hasPermission(2);
        })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("biome", ResourceArgument.resource(p_262155_, Registries.BIOME)).executes((p_262554_) -> {
            return fill((CommandSourceStack)p_262554_.getSource(), BlockPosArgument.getLoadedBlockPos(p_262554_, "from"), BlockPosArgument.getLoadedBlockPos(p_262554_, "to"), ResourceArgument.getResource(p_262554_, "biome", Registries.BIOME), (p_262543_) -> {
                return true;
            });
        })).then(Commands.literal("replace").then(Commands.argument("filter", ResourceOrTagArgument.resourceOrTag(p_262155_, Registries.BIOME)).executes((p_262544_) -> {
            CommandSourceStack var10000 = (CommandSourceStack)p_262544_.getSource();
            BlockPos var10001 = BlockPosArgument.getLoadedBlockPos(p_262544_, "from");
            BlockPos var10002 = BlockPosArgument.getLoadedBlockPos(p_262544_, "to");
            Holder.Reference var10003 = ResourceArgument.getResource(p_262544_, "biome", Registries.BIOME);
            ResourceOrTagArgument.Result var10004 = ResourceOrTagArgument.getResourceOrTag(p_262544_, "filter", Registries.BIOME);
            Objects.requireNonNull(var10004);
            return fill(var10000, var10001, var10002, var10003, var10004::test);
        })))))));
    }

    private static int quantize(int p_261998_) {
        return QuartPos.toBlock(QuartPos.fromBlock(p_261998_));
    }

    private static BlockPos quantize(BlockPos p_262148_) {
        return new BlockPos(quantize(p_262148_.getX()), quantize(p_262148_.getY()), quantize(p_262148_.getZ()));
    }

    private static BiomeResolver makeResolver(MutableInt p_262615_, ChunkAccess p_262698_, BoundingBox p_262622_, Holder<Biome> p_262705_, Predicate<Holder<Biome>> p_262695_) {
        return (p_262550_, p_262551_, p_262552_, p_262553_) -> {
            int $$9 = QuartPos.toBlock(p_262550_);
            int $$10 = QuartPos.toBlock(p_262551_);
            int $$11 = QuartPos.toBlock(p_262552_);
            Holder<Biome> $$12 = p_262698_.getNoiseBiome(p_262550_, p_262551_, p_262552_);
            if (p_262622_.isInside($$9, $$10, $$11) && p_262695_.test($$12)) {
                p_262615_.increment();
                return p_262705_;
            } else {
                return $$12;
            }
        };
    }

    private static int fill(CommandSourceStack p_262664_, BlockPos p_262651_, BlockPos p_262678_, Holder.Reference<Biome> p_262612_, Predicate<Holder<Biome>> p_262697_) throws CommandSyntaxException {
        BlockPos $$5 = quantize(p_262651_);
        BlockPos $$6 = quantize(p_262678_);
        BoundingBox $$7 = BoundingBox.fromCorners($$5, $$6);
        int $$8 = $$7.getXSpan() * $$7.getYSpan() * $$7.getZSpan();
        int $$9 = p_262664_.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);
        if ($$8 > $$9) {
            throw ERROR_VOLUME_TOO_LARGE.create($$9, $$8);
        } else {
            ServerLevel $$10 = p_262664_.getLevel();
            List<ChunkAccess> $$11 = new ArrayList();

            ChunkAccess $$14;
            for(int $$12 = SectionPos.blockToSectionCoord($$7.minZ()); $$12 <= SectionPos.blockToSectionCoord($$7.maxZ()); ++$$12) {
                for(int $$13 = SectionPos.blockToSectionCoord($$7.minX()); $$13 <= SectionPos.blockToSectionCoord($$7.maxX()); ++$$13) {
                    $$14 = $$10.getChunk($$13, $$12, ChunkStatus.FULL, false);
                    if ($$14 == null) {
                        throw ERROR_NOT_LOADED.create();
                    }

                    $$11.add($$14);
                }
            }

            MutableInt $$15 = new MutableInt(0);
            Iterator var16 = $$11.iterator();

            while(var16.hasNext()) {
                $$14 = (ChunkAccess)var16.next();
                $$14.fillBiomesFromNoise(makeResolver($$15, $$14, $$7, p_262612_, p_262697_), $$10.getChunkSource().randomState().sampler());
                $$14.setUnsaved(true);
            }

            $$10.getChunkSource().chunkMap.resendBiomesForChunks($$11);
            p_262664_.sendSuccess(() -> {
                return Component.translatable("commands.fillbiome.success.count", $$15.getValue(), $$7.minX(), $$7.minY(), $$7.minZ(), $$7.maxX(), $$7.maxY(), $$7.maxZ());
            }, true);
            return $$15.getValue();
        }
    }
}
