//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ForceLoadCommand {
    private static final int MAX_CHUNK_LIMIT = 256;
    private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((p_137698_, p_137699_) -> {
        return Component.translatable("commands.forceload.toobig", p_137698_, p_137699_);
    });
    private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((p_137691_, p_137692_) -> {
        return Component.translatable("commands.forceload.query.failure", p_137691_, p_137692_);
    });
    private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(Component.translatable("commands.forceload.removed.failure"));

    public ForceLoadCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_137677_) {
        p_137677_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires((p_137703_) -> {
            return p_137703_.hasPermission(2);
        })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_137711_) -> {
            return changeForceLoad((CommandSourceStack)p_137711_.getSource(), ColumnPosArgument.getColumnPos(p_137711_, "from"), ColumnPosArgument.getColumnPos(p_137711_, "from"), true);
        })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_137709_) -> {
            return changeForceLoad((CommandSourceStack)p_137709_.getSource(), ColumnPosArgument.getColumnPos(p_137709_, "from"), ColumnPosArgument.getColumnPos(p_137709_, "to"), true);
        }))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_137707_) -> {
            return changeForceLoad((CommandSourceStack)p_137707_.getSource(), ColumnPosArgument.getColumnPos(p_137707_, "from"), ColumnPosArgument.getColumnPos(p_137707_, "from"), false);
        })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_137705_) -> {
            return changeForceLoad((CommandSourceStack)p_137705_.getSource(), ColumnPosArgument.getColumnPos(p_137705_, "from"), ColumnPosArgument.getColumnPos(p_137705_, "to"), false);
        })))).then(Commands.literal("all").executes((p_137701_) -> {
            return removeAll((CommandSourceStack)p_137701_.getSource());
        })))).then(((LiteralArgumentBuilder)Commands.literal("query").executes((p_137694_) -> {
            return listForceLoad((CommandSourceStack)p_137694_.getSource());
        })).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((p_137679_) -> {
            return queryForceLoad((CommandSourceStack)p_137679_.getSource(), ColumnPosArgument.getColumnPos(p_137679_, "pos"));
        }))));
    }

    private static int queryForceLoad(CommandSourceStack p_137683_, ColumnPos p_137684_) throws CommandSyntaxException {
        ChunkPos $$2 = p_137684_.toChunkPos();
        ServerLevel $$3 = p_137683_.getLevel();
        ResourceKey<Level> $$4 = $$3.dimension();
        boolean $$5 = $$3.getForcedChunks().contains($$2.toLong());
        if ($$5) {
            p_137683_.sendSuccess(() -> {
                return Component.translatable("commands.forceload.query.success", $$2, $$4.location());
            }, false);
            return 1;
        } else {
            throw ERROR_NOT_TICKING.create($$2, $$4.location());
        }
    }

    private static int listForceLoad(CommandSourceStack p_137681_) {
        ServerLevel $$1 = p_137681_.getLevel();
        ResourceKey<Level> $$2 = $$1.dimension();
        LongSet $$3 = $$1.getForcedChunks();
        int $$4 = $$3.size();
        if ($$4 > 0) {
            String $$5 = Joiner.on(", ").join($$3.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if ($$4 == 1) {
                p_137681_.sendSuccess(() -> {
                    return Component.translatable("commands.forceload.list.single", $$2.location(), $$5);
                }, false);
            } else {
                p_137681_.sendSuccess(() -> {
                    return Component.translatable("commands.forceload.list.multiple", $$4, $$2.location(), $$5);
                }, false);
            }
        } else {
            p_137681_.sendFailure(Component.translatable("commands.forceload.added.none", $$2.location()));
        }

        return $$4;
    }

    private static int removeAll(CommandSourceStack p_137696_) {
        ServerLevel $$1 = p_137696_.getLevel();
        ResourceKey<Level> $$2 = $$1.dimension();
        LongSet $$3 = $$1.getForcedChunks();
        $$3.forEach((p_137675_) -> {
            $$1.setChunkForced(ChunkPos.getX(p_137675_), ChunkPos.getZ(p_137675_), false);
        });
        p_137696_.sendSuccess(() -> {
            return Component.translatable("commands.forceload.removed.all", $$2.location());
        }, true);
        return 0;
    }

    private static int changeForceLoad(CommandSourceStack p_137686_, ColumnPos p_137687_, ColumnPos p_137688_, boolean p_137689_) throws CommandSyntaxException {
        int $$4 = Math.min(p_137687_.x(), p_137688_.x());
        int $$5 = Math.min(p_137687_.z(), p_137688_.z());
        int $$6 = Math.max(p_137687_.x(), p_137688_.x());
        int $$7 = Math.max(p_137687_.z(), p_137688_.z());
        if ($$4 >= -30000000 && $$5 >= -30000000 && $$6 < 30000000 && $$7 < 30000000) {
            int $$8 = SectionPos.blockToSectionCoord($$4);
            int $$9 = SectionPos.blockToSectionCoord($$5);
            int $$10 = SectionPos.blockToSectionCoord($$6);
            int $$11 = SectionPos.blockToSectionCoord($$7);
            long $$12 = ((long)($$10 - $$8) + 1L) * ((long)($$11 - $$9) + 1L);
            if ($$12 > 256L) {
                throw ERROR_TOO_MANY_CHUNKS.create(256, $$12);
            } else {
                ServerLevel $$13 = p_137686_.getLevel();
                ResourceKey<Level> $$14 = $$13.dimension();
                ChunkPos $$15 = null;
                int $$16 = 0;

                for(int $$17 = $$8; $$17 <= $$10; ++$$17) {
                    for(int $$18 = $$9; $$18 <= $$11; ++$$18) {
                        boolean $$19 = $$13.setChunkForced($$17, $$18, p_137689_);
                        if ($$19) {
                            ++$$16;
                            if ($$15 == null) {
                                $$15 = new ChunkPos($$17, $$18);
                            }
                        }
                    }
                }

                if ($$16 == 0) {
                    throw (p_137689_ ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
                } else {
                    if ($$16 == 1) {
                        p_137686_.sendSuccess(() -> {
                            return Component.translatable("commands.forceload." + (p_137689_ ? "added" : "removed") + ".single", $$15, $$14.location());
                        }, true);
                    } else {
                        ChunkPos $$21 = new ChunkPos($$8, $$9);
                        ChunkPos $$22 = new ChunkPos($$10, $$11);
                        p_137686_.sendSuccess(() -> {
                            return Component.translatable("commands.forceload." + (p_137689_ ? "added" : "removed") + ".multiple", $$15, $$14.location(), $$21, $$22);
                        }, true);
                    }

                    return $$16;
                }
            }
        } else {
            throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
        }
    }
}
