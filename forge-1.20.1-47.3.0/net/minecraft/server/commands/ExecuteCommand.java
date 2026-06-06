//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.commands.arguments.RangeArgument.Ints;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ExecuteCommand {
    private static final int MAX_TEST_AREA = 32768;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_137129_, p_137130_) -> {
        return Component.translatable("commands.execute.blocks.toobig", p_137129_, p_137130_);
    });
    private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((p_137127_) -> {
        return Component.translatable("commands.execute.conditional.fail_count", p_137127_);
    });
    private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER = (p_137045_, p_137046_) -> {
        return (p_180160_, p_180161_, p_180162_) -> {
            p_137045_.onCommandComplete(p_180160_, p_180161_, p_180162_);
            p_137046_.onCommandComplete(p_180160_, p_180161_, p_180162_);
        };
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE = (p_278905_, p_278906_) -> {
        LootDataManager $$2 = ((CommandSourceStack)p_278905_.getSource()).getServer().getLootData();
        return SharedSuggestionProvider.suggestResource((Iterable)$$2.getKeys(LootDataType.PREDICATE), p_278906_);
    };

    public ExecuteCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_214435_, CommandBuildContext p_214436_) {
        LiteralCommandNode<CommandSourceStack> $$2 = p_214435_.register((LiteralArgumentBuilder)Commands.literal("execute").requires((p_137197_) -> {
            return p_137197_.hasPermission(2);
        }));
        p_214435_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires((p_137103_) -> {
            return p_137103_.hasPermission(2);
        })).then(Commands.literal("run").redirect(p_214435_.getRoot()))).then(addConditionals($$2, Commands.literal("if"), true, p_214436_))).then(addConditionals($$2, Commands.literal("unless"), false, p_214436_))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork($$2, (p_137299_) -> {
            List<CommandSourceStack> $$1 = Lists.newArrayList();
            Iterator var2 = EntityArgument.getOptionalEntities(p_137299_, "targets").iterator();

            while(var2.hasNext()) {
                Entity $$2 = (Entity)var2.next();
                $$1.add(((CommandSourceStack)p_137299_.getSource()).withEntity($$2));
            }

            return $$1;
        })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork($$2, (p_284653_) -> {
            List<CommandSourceStack> $$1 = Lists.newArrayList();
            Iterator var2 = EntityArgument.getOptionalEntities(p_284653_, "targets").iterator();

            while(var2.hasNext()) {
                Entity $$2 = (Entity)var2.next();
                $$1.add(((CommandSourceStack)p_284653_.getSource()).withLevel((ServerLevel)$$2.level()).withPosition($$2.position()).withRotation($$2.getRotationVector()));
            }

            return $$1;
        })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(wrapStores($$2, Commands.literal("result"), true))).then(wrapStores($$2, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect($$2, (p_137295_) -> {
            return ((CommandSourceStack)p_137295_.getSource()).withPosition(Vec3Argument.getVec3(p_137295_, "pos")).withAnchor(Anchor.FEET);
        }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork($$2, (p_137293_) -> {
            List<CommandSourceStack> $$1 = Lists.newArrayList();
            Iterator var2 = EntityArgument.getOptionalEntities(p_137293_, "targets").iterator();

            while(var2.hasNext()) {
                Entity $$2 = (Entity)var2.next();
                $$1.add(((CommandSourceStack)p_137293_.getSource()).withPosition($$2.position()));
            }

            return $$1;
        })))).then(Commands.literal("over").then(Commands.argument("heightmap", HeightmapTypeArgument.heightmap()).redirect($$2, (p_274814_) -> {
            Vec3 $$1 = ((CommandSourceStack)p_274814_.getSource()).getPosition();
            ServerLevel $$2 = ((CommandSourceStack)p_274814_.getSource()).getLevel();
            double $$3 = $$1.x();
            double $$4 = $$1.z();
            if (!$$2.hasChunk(SectionPos.blockToSectionCoord($$3), SectionPos.blockToSectionCoord($$4))) {
                throw BlockPosArgument.ERROR_NOT_LOADED.create();
            } else {
                int $$5 = $$2.getHeight(HeightmapTypeArgument.getHeightmap(p_274814_, "heightmap"), Mth.floor($$3), Mth.floor($$4));
                return ((CommandSourceStack)p_274814_.getSource()).withPosition(new Vec3($$3, (double)$$5, $$4));
            }
        }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect($$2, (p_137291_) -> {
            return ((CommandSourceStack)p_137291_.getSource()).withRotation(RotationArgument.getRotation(p_137291_, "rot").getRotation((CommandSourceStack)p_137291_.getSource()));
        }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork($$2, (p_137289_) -> {
            List<CommandSourceStack> $$1 = Lists.newArrayList();
            Iterator var2 = EntityArgument.getOptionalEntities(p_137289_, "targets").iterator();

            while(var2.hasNext()) {
                Entity $$2 = (Entity)var2.next();
                $$1.add(((CommandSourceStack)p_137289_.getSource()).withRotation($$2.getRotationVector()));
            }

            return $$1;
        }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork($$2, (p_137287_) -> {
            List<CommandSourceStack> $$1 = Lists.newArrayList();
            EntityAnchorArgument.Anchor $$2 = EntityAnchorArgument.getAnchor(p_137287_, "anchor");
            Iterator var3 = EntityArgument.getOptionalEntities(p_137287_, "targets").iterator();

            while(var3.hasNext()) {
                Entity $$3 = (Entity)var3.next();
                $$1.add(((CommandSourceStack)p_137287_.getSource()).facing($$3, $$2));
            }

            return $$1;
        }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect($$2, (p_137285_) -> {
            return ((CommandSourceStack)p_137285_.getSource()).facing(Vec3Argument.getVec3(p_137285_, "pos"));
        })))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect($$2, (p_137283_) -> {
            return ((CommandSourceStack)p_137283_.getSource()).withPosition(((CommandSourceStack)p_137283_.getSource()).getPosition().align(SwizzleArgument.getSwizzle(p_137283_, "axes")));
        })))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect($$2, (p_137281_) -> {
            return ((CommandSourceStack)p_137281_.getSource()).withAnchor(EntityAnchorArgument.getAnchor(p_137281_, "anchor"));
        })))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect($$2, (p_137279_) -> {
            return ((CommandSourceStack)p_137279_.getSource()).withLevel(DimensionArgument.getDimension(p_137279_, "dimension"));
        })))).then(Commands.literal("summon").then(Commands.argument("entity", ResourceArgument.resource(p_214436_, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).redirect($$2, (p_269759_) -> {
            return spawnEntityAndRedirect((CommandSourceStack)p_269759_.getSource(), ResourceArgument.getSummonableEntityType(p_269759_, "entity"));
        })))).then(createRelationOperations($$2, Commands.literal("on"))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapStores(LiteralCommandNode<CommandSourceStack> p_137094_, LiteralArgumentBuilder<CommandSourceStack> p_137095_, boolean p_137096_) {
        p_137095_.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(p_137094_, (p_137271_) -> {
            return storeValue((CommandSourceStack)p_137271_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_137271_, "targets"), ObjectiveArgument.getObjective(p_137271_, "objective"), p_137096_);
        }))));
        p_137095_.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(p_137094_, (p_137259_) -> {
            return storeValue((CommandSourceStack)p_137259_.getSource(), BossBarCommands.getBossBar(p_137259_), true, p_137096_);
        }))).then(Commands.literal("max").redirect(p_137094_, (p_137247_) -> {
            return storeValue((CommandSourceStack)p_137247_.getSource(), BossBarCommands.getBossBar(p_137247_), false, p_137096_);
        }))));
        Iterator var3 = DataCommands.TARGET_PROVIDERS.iterator();

        while(var3.hasNext()) {
            DataCommands.DataProvider $$3 = (DataCommands.DataProvider)var3.next();
            $$3.wrap(p_137095_, (p_137101_) -> {
                return p_137101_.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180216_) -> {
                    return storeData((CommandSourceStack)p_180216_.getSource(), $$3.access(p_180216_), NbtPathArgument.getPath(p_180216_, "path"), (p_180219_) -> {
                        return IntTag.valueOf((int)((double)p_180219_ * DoubleArgumentType.getDouble(p_180216_, "scale")));
                    }, p_137096_);
                })))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180209_) -> {
                    return storeData((CommandSourceStack)p_180209_.getSource(), $$3.access(p_180209_), NbtPathArgument.getPath(p_180209_, "path"), (p_180212_) -> {
                        return FloatTag.valueOf((float)((double)p_180212_ * DoubleArgumentType.getDouble(p_180209_, "scale")));
                    }, p_137096_);
                })))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180199_) -> {
                    return storeData((CommandSourceStack)p_180199_.getSource(), $$3.access(p_180199_), NbtPathArgument.getPath(p_180199_, "path"), (p_180202_) -> {
                        return ShortTag.valueOf((short)((int)((double)p_180202_ * DoubleArgumentType.getDouble(p_180199_, "scale"))));
                    }, p_137096_);
                })))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180189_) -> {
                    return storeData((CommandSourceStack)p_180189_.getSource(), $$3.access(p_180189_), NbtPathArgument.getPath(p_180189_, "path"), (p_180192_) -> {
                        return LongTag.valueOf((long)((double)p_180192_ * DoubleArgumentType.getDouble(p_180189_, "scale")));
                    }, p_137096_);
                })))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180179_) -> {
                    return storeData((CommandSourceStack)p_180179_.getSource(), $$3.access(p_180179_), NbtPathArgument.getPath(p_180179_, "path"), (p_180182_) -> {
                        return DoubleTag.valueOf((double)p_180182_ * DoubleArgumentType.getDouble(p_180179_, "scale"));
                    }, p_137096_);
                })))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_137094_, (p_180156_) -> {
                    return storeData((CommandSourceStack)p_180156_.getSource(), $$3.access(p_180156_), NbtPathArgument.getPath(p_180156_, "path"), (p_180165_) -> {
                        return ByteTag.valueOf((byte)((int)((double)p_180165_ * DoubleArgumentType.getDouble(p_180156_, "scale"))));
                    }, p_137096_);
                }))));
            });
        }

        return p_137095_;
    }

    private static CommandSourceStack storeValue(CommandSourceStack p_137108_, Collection<String> p_137109_, Objective p_137110_, boolean p_137111_) {
        Scoreboard $$4 = p_137108_.getServer().getScoreboard();
        return p_137108_.withCallback((p_137136_, p_137137_, p_137138_) -> {
            Iterator var7 = p_137109_.iterator();

            while(var7.hasNext()) {
                String $$7 = (String)var7.next();
                Score $$8 = $$4.getOrCreatePlayerScore($$7, p_137110_);
                int $$9 = p_137111_ ? p_137138_ : (p_137137_ ? 1 : 0);
                $$8.setScore($$9);
            }

        }, CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeValue(CommandSourceStack p_137113_, CustomBossEvent p_137114_, boolean p_137115_, boolean p_137116_) {
        return p_137113_.withCallback((p_137185_, p_137186_, p_137187_) -> {
            int $$6 = p_137116_ ? p_137187_ : (p_137186_ ? 1 : 0);
            if (p_137115_) {
                p_137114_.setValue($$6);
            } else {
                p_137114_.setMax($$6);
            }

        }, CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeData(CommandSourceStack p_137118_, DataAccessor p_137119_, NbtPathArgument.NbtPath p_137120_, IntFunction<Tag> p_137121_, boolean p_137122_) {
        return p_137118_.withCallback((p_137153_, p_137154_, p_137155_) -> {
            try {
                CompoundTag $$7 = p_137119_.getData();
                int $$8 = p_137122_ ? p_137155_ : (p_137154_ ? 1 : 0);
                p_137120_.set($$7, (Tag)p_137121_.apply($$8));
                p_137119_.setData($$7);
            } catch (CommandSyntaxException var9) {
            }

        }, CALLBACK_CHAINER);
    }

    private static boolean isChunkLoaded(ServerLevel p_265261_, BlockPos p_265260_) {
        ChunkPos $$2 = new ChunkPos(p_265260_);
        LevelChunk $$3 = p_265261_.getChunkSource().getChunkNow($$2.x, $$2.z);
        if ($$3 == null) {
            return false;
        } else {
            return $$3.getFullStatus() == FullChunkStatus.ENTITY_TICKING && p_265261_.areEntitiesLoaded($$2.toLong());
        }
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditionals(CommandNode<CommandSourceStack> p_214438_, LiteralArgumentBuilder<CommandSourceStack> p_214439_, boolean p_214440_, CommandBuildContext p_214441_) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)p_214439_.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(p_214438_, Commands.argument("block", BlockPredicateArgument.blockPredicate(p_214441_)), p_214440_, (p_137277_) -> {
            return BlockPredicateArgument.getBlockPredicate(p_137277_, "block").test(new BlockInWorld(((CommandSourceStack)p_137277_.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(p_137277_, "pos"), true));
        }))))).then(Commands.literal("biome").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(p_214438_, Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(p_214441_, Registries.BIOME)), p_214440_, (p_277265_) -> {
            return ResourceOrTagArgument.getResourceOrTag(p_277265_, "biome", Registries.BIOME).test(((CommandSourceStack)p_277265_.getSource()).getLevel().getBiome(BlockPosArgument.getLoadedBlockPos(p_277265_, "pos")));
        }))))).then(Commands.literal("loaded").then(addConditional(p_214438_, Commands.argument("pos", BlockPosArgument.blockPos()), p_214440_, (p_269757_) -> {
            return isChunkLoaded(((CommandSourceStack)p_269757_.getSource()).getLevel(), BlockPosArgument.getBlockPos(p_269757_, "pos"));
        })))).then(Commands.literal("dimension").then(addConditional(p_214438_, Commands.argument("dimension", DimensionArgument.dimension()), p_214440_, (p_264789_) -> {
            return DimensionArgument.getDimension(p_264789_, "dimension") == ((CommandSourceStack)p_264789_.getSource()).getLevel();
        })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_214438_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_214440_, (p_137275_) -> {
            return checkScore(p_137275_, Integer::equals);
        }))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_214438_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_214440_, (p_137273_) -> {
            return checkScore(p_137273_, (p_180204_, p_180205_) -> {
                return p_180204_ < p_180205_;
            });
        }))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_214438_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_214440_, (p_137261_) -> {
            return checkScore(p_137261_, (p_180194_, p_180195_) -> {
                return p_180194_ <= p_180195_;
            });
        }))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_214438_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_214440_, (p_137249_) -> {
            return checkScore(p_137249_, (p_180184_, p_180185_) -> {
                return p_180184_ > p_180185_;
            });
        }))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_214438_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_214440_, (p_137234_) -> {
            return checkScore(p_137234_, (p_180167_, p_180168_) -> {
                return p_180167_ >= p_180168_;
            });
        }))))).then(Commands.literal("matches").then(addConditional(p_214438_, Commands.argument("range", RangeArgument.intRange()), p_214440_, (p_137216_) -> {
            return checkScore(p_137216_, Ints.getRange(p_137216_, "range"));
        }))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(addIfBlocksConditional(p_214438_, Commands.literal("all"), p_214440_, false))).then(addIfBlocksConditional(p_214438_, Commands.literal("masked"), p_214440_, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork(p_214438_, (p_137232_) -> {
            return expect(p_137232_, p_214440_, !EntityArgument.getOptionalEntities(p_137232_, "entities").isEmpty());
        })).executes(createNumericConditionalHandler(p_214440_, (p_137189_) -> {
            return EntityArgument.getOptionalEntities(p_137189_, "entities").size();
        }))))).then(Commands.literal("predicate").then(addConditional(p_214438_, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), p_214440_, (p_137054_) -> {
            return checkCustomPredicate((CommandSourceStack)p_137054_.getSource(), ResourceLocationArgument.getPredicate(p_137054_, "predicate"));
        })));
        Iterator var4 = DataCommands.SOURCE_PROVIDERS.iterator();

        while(var4.hasNext()) {
            DataCommands.DataProvider $$4 = (DataCommands.DataProvider)var4.next();
            p_214439_.then($$4.wrap(Commands.literal("data"), (p_137092_) -> {
                return p_137092_.then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).fork(p_214438_, (p_180175_) -> {
                    return expect(p_180175_, p_214440_, checkMatchingData($$4.access(p_180175_), NbtPathArgument.getPath(p_180175_, "path")) > 0);
                })).executes(createNumericConditionalHandler(p_214440_, (p_180152_) -> {
                    return checkMatchingData($$4.access(p_180152_), NbtPathArgument.getPath(p_180152_, "path"));
                })));
            }));
        }

        return p_214439_;
    }

    private static Command<CommandSourceStack> createNumericConditionalHandler(boolean p_137167_, CommandNumericPredicate p_137168_) {
        return p_137167_ ? (p_288391_) -> {
            int $$2 = p_137168_.test(p_288391_);
            if ($$2 > 0) {
                ((CommandSourceStack)p_288391_.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.execute.conditional.pass_count", $$2);
                }, false);
                return $$2;
            } else {
                throw ERROR_CONDITIONAL_FAILED.create();
            }
        } : (p_288393_) -> {
            int $$2 = p_137168_.test(p_288393_);
            if ($$2 == 0) {
                ((CommandSourceStack)p_288393_.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.execute.conditional.pass");
                }, false);
                return 1;
            } else {
                throw ERROR_CONDITIONAL_FAILED_COUNT.create($$2);
            }
        };
    }

    private static int checkMatchingData(DataAccessor p_137146_, NbtPathArgument.NbtPath p_137147_) throws CommandSyntaxException {
        return p_137147_.countMatching(p_137146_.getData());
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> p_137065_, BiPredicate<Integer, Integer> p_137066_) throws CommandSyntaxException {
        String $$2 = ScoreHolderArgument.getName(p_137065_, "target");
        Objective $$3 = ObjectiveArgument.getObjective(p_137065_, "targetObjective");
        String $$4 = ScoreHolderArgument.getName(p_137065_, "source");
        Objective $$5 = ObjectiveArgument.getObjective(p_137065_, "sourceObjective");
        Scoreboard $$6 = ((CommandSourceStack)p_137065_.getSource()).getServer().getScoreboard();
        if ($$6.hasPlayerScore($$2, $$3) && $$6.hasPlayerScore($$4, $$5)) {
            Score $$7 = $$6.getOrCreatePlayerScore($$2, $$3);
            Score $$8 = $$6.getOrCreatePlayerScore($$4, $$5);
            return p_137066_.test($$7.getScore(), $$8.getScore());
        } else {
            return false;
        }
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> p_137059_, MinMaxBounds.Ints p_137060_) throws CommandSyntaxException {
        String $$2 = ScoreHolderArgument.getName(p_137059_, "target");
        Objective $$3 = ObjectiveArgument.getObjective(p_137059_, "targetObjective");
        Scoreboard $$4 = ((CommandSourceStack)p_137059_.getSource()).getServer().getScoreboard();
        return !$$4.hasPlayerScore($$2, $$3) ? false : p_137060_.matches($$4.getOrCreatePlayerScore($$2, $$3).getScore());
    }

    private static boolean checkCustomPredicate(CommandSourceStack p_137105_, LootItemCondition p_137106_) {
        ServerLevel $$2 = p_137105_.getLevel();
        LootParams $$3 = (new LootParams.Builder($$2)).withParameter(LootContextParams.ORIGIN, p_137105_.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, p_137105_.getEntity()).create(LootContextParamSets.COMMAND);
        LootContext $$4 = (new LootContext.Builder($$3)).create((ResourceLocation)null);
        $$4.pushVisitedElement(LootContext.createVisitedEntry(p_137106_));
        return p_137106_.test($$4);
    }

    private static Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> p_137071_, boolean p_137072_, boolean p_137073_) {
        return (Collection)(p_137073_ == p_137072_ ? Collections.singleton((CommandSourceStack)p_137071_.getSource()) : Collections.emptyList());
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> p_137075_, ArgumentBuilder<CommandSourceStack, ?> p_137076_, boolean p_137077_, CommandPredicate p_137078_) {
        return p_137076_.fork(p_137075_, (p_137214_) -> {
            return expect(p_137214_, p_137077_, p_137078_.test(p_137214_));
        }).executes((p_288396_) -> {
            if (p_137077_ == p_137078_.test(p_288396_)) {
                ((CommandSourceStack)p_288396_.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.execute.conditional.pass");
                }, false);
                return 1;
            } else {
                throw ERROR_CONDITIONAL_FAILED.create();
            }
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(CommandNode<CommandSourceStack> p_137080_, ArgumentBuilder<CommandSourceStack, ?> p_137081_, boolean p_137082_, boolean p_137083_) {
        return p_137081_.fork(p_137080_, (p_137180_) -> {
            return expect(p_137180_, p_137082_, checkRegions(p_137180_, p_137083_).isPresent());
        }).executes(p_137082_ ? (p_137210_) -> {
            return checkIfRegions(p_137210_, p_137083_);
        } : (p_137165_) -> {
            return checkUnlessRegions(p_137165_, p_137083_);
        });
    }

    private static int checkIfRegions(CommandContext<CommandSourceStack> p_137068_, boolean p_137069_) throws CommandSyntaxException {
        OptionalInt $$2 = checkRegions(p_137068_, p_137069_);
        if ($$2.isPresent()) {
            ((CommandSourceStack)p_137068_.getSource()).sendSuccess(() -> {
                return Component.translatable("commands.execute.conditional.pass_count", $$2.getAsInt());
            }, false);
            return $$2.getAsInt();
        } else {
            throw ERROR_CONDITIONAL_FAILED.create();
        }
    }

    private static int checkUnlessRegions(CommandContext<CommandSourceStack> p_137194_, boolean p_137195_) throws CommandSyntaxException {
        OptionalInt $$2 = checkRegions(p_137194_, p_137195_);
        if ($$2.isPresent()) {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create($$2.getAsInt());
        } else {
            ((CommandSourceStack)p_137194_.getSource()).sendSuccess(() -> {
                return Component.translatable("commands.execute.conditional.pass");
            }, false);
            return 1;
        }
    }

    private static OptionalInt checkRegions(CommandContext<CommandSourceStack> p_137221_, boolean p_137222_) throws CommandSyntaxException {
        return checkRegions(((CommandSourceStack)p_137221_.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(p_137221_, "start"), BlockPosArgument.getLoadedBlockPos(p_137221_, "end"), BlockPosArgument.getLoadedBlockPos(p_137221_, "destination"), p_137222_);
    }

    private static OptionalInt checkRegions(ServerLevel p_137037_, BlockPos p_137038_, BlockPos p_137039_, BlockPos p_137040_, boolean p_137041_) throws CommandSyntaxException {
        BoundingBox $$5 = BoundingBox.fromCorners(p_137038_, p_137039_);
        BoundingBox $$6 = BoundingBox.fromCorners(p_137040_, p_137040_.offset($$5.getLength()));
        BlockPos $$7 = new BlockPos($$6.minX() - $$5.minX(), $$6.minY() - $$5.minY(), $$6.minZ() - $$5.minZ());
        int $$8 = $$5.getXSpan() * $$5.getYSpan() * $$5.getZSpan();
        if ($$8 > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, $$8);
        } else {
            int $$9 = 0;

            for(int $$10 = $$5.minZ(); $$10 <= $$5.maxZ(); ++$$10) {
                for(int $$11 = $$5.minY(); $$11 <= $$5.maxY(); ++$$11) {
                    for(int $$12 = $$5.minX(); $$12 <= $$5.maxX(); ++$$12) {
                        BlockPos $$13 = new BlockPos($$12, $$11, $$10);
                        BlockPos $$14 = $$13.offset($$7);
                        BlockState $$15 = p_137037_.getBlockState($$13);
                        if (!p_137041_ || !$$15.is(Blocks.AIR)) {
                            if ($$15 != p_137037_.getBlockState($$14)) {
                                return OptionalInt.empty();
                            }

                            BlockEntity $$16 = p_137037_.getBlockEntity($$13);
                            BlockEntity $$17 = p_137037_.getBlockEntity($$14);
                            if ($$16 != null) {
                                if ($$17 == null) {
                                    return OptionalInt.empty();
                                }

                                if ($$17.getType() != $$16.getType()) {
                                    return OptionalInt.empty();
                                }

                                CompoundTag $$18 = $$16.saveWithoutMetadata();
                                CompoundTag $$19 = $$17.saveWithoutMetadata();
                                if (!$$18.equals($$19)) {
                                    return OptionalInt.empty();
                                }
                            }

                            ++$$9;
                        }
                    }
                }
            }

            return OptionalInt.of($$9);
        }
    }

    private static RedirectModifier<CommandSourceStack> expandOneToOneEntityRelation(Function<Entity, Optional<Entity>> p_265114_) {
        return (p_264786_) -> {
            CommandSourceStack $$2 = (CommandSourceStack)p_264786_.getSource();
            Entity $$3 = $$2.getEntity();
            return (Collection)($$3 == null ? List.of() : (Collection)((Optional)p_265114_.apply($$3)).filter((p_264783_) -> {
                return !p_264783_.isRemoved();
            }).map((p_264775_) -> {
                return List.of($$2.withEntity(p_264775_));
            }).orElse(List.of()));
        };
    }

    private static RedirectModifier<CommandSourceStack> expandOneToManyEntityRelation(Function<Entity, Stream<Entity>> p_265496_) {
        return (p_264780_) -> {
            CommandSourceStack $$2 = (CommandSourceStack)p_264780_.getSource();
            Entity $$3 = $$2.getEntity();
            if ($$3 == null) {
                return List.of();
            } else {
                Stream var10000 = ((Stream)p_265496_.apply($$3)).filter((p_264784_) -> {
                    return !p_264784_.isRemoved();
                });
                Objects.requireNonNull($$2);
                return var10000.map($$2::withEntity).toList();
            }
        };
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRelationOperations(CommandNode<CommandSourceStack> p_265189_, LiteralArgumentBuilder<CommandSourceStack> p_265783_) {
        return (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)p_265783_.then(Commands.literal("owner").fork(p_265189_, expandOneToOneEntityRelation((p_269758_) -> {
            Optional var10000;
            if (p_269758_ instanceof OwnableEntity $$1) {
                var10000 = Optional.ofNullable($$1.getOwner());
            } else {
                var10000 = Optional.empty();
            }

            return var10000;
        })))).then(Commands.literal("leasher").fork(p_265189_, expandOneToOneEntityRelation((p_264782_) -> {
            Optional var10000;
            if (p_264782_ instanceof Mob $$1) {
                var10000 = Optional.ofNullable($$1.getLeashHolder());
            } else {
                var10000 = Optional.empty();
            }

            return var10000;
        })))).then(Commands.literal("target").fork(p_265189_, expandOneToOneEntityRelation((p_272389_) -> {
            Optional var10000;
            if (p_272389_ instanceof Targeting $$1) {
                var10000 = Optional.ofNullable($$1.getTarget());
            } else {
                var10000 = Optional.empty();
            }

            return var10000;
        })))).then(Commands.literal("attacker").fork(p_265189_, expandOneToOneEntityRelation((p_272388_) -> {
            Optional var10000;
            if (p_272388_ instanceof Attackable $$1) {
                var10000 = Optional.ofNullable($$1.getLastAttacker());
            } else {
                var10000 = Optional.empty();
            }

            return var10000;
        })))).then(Commands.literal("vehicle").fork(p_265189_, expandOneToOneEntityRelation((p_264776_) -> {
            return Optional.ofNullable(p_264776_.getVehicle());
        })))).then(Commands.literal("controller").fork(p_265189_, expandOneToOneEntityRelation((p_274815_) -> {
            return Optional.ofNullable(p_274815_.getControllingPassenger());
        })))).then(Commands.literal("origin").fork(p_265189_, expandOneToOneEntityRelation((p_266631_) -> {
            Optional var10000;
            if (p_266631_ instanceof TraceableEntity $$1) {
                var10000 = Optional.ofNullable($$1.getOwner());
            } else {
                var10000 = Optional.empty();
            }

            return var10000;
        })))).then(Commands.literal("passengers").fork(p_265189_, expandOneToManyEntityRelation((p_264777_) -> {
            return p_264777_.getPassengers().stream();
        })));
    }

    private static CommandSourceStack spawnEntityAndRedirect(CommandSourceStack p_270320_, Holder.Reference<EntityType<?>> p_270344_) throws CommandSyntaxException {
        Entity $$2 = SummonCommand.createEntity(p_270320_, p_270344_, p_270320_.getPosition(), new CompoundTag(), true);
        return p_270320_.withEntity($$2);
    }

    @FunctionalInterface
    interface CommandPredicate {
        boolean test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    interface CommandNumericPredicate {
        int test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }
}
