//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(Component.translatable("commands.scoreboard.objectives.add.duplicate"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(Component.translatable("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(Component.translatable("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(Component.translatable("commands.scoreboard.players.enable.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(Component.translatable("commands.scoreboard.players.enable.invalid"));
    private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((p_138534_, p_138535_) -> {
        return Component.translatable("commands.scoreboard.players.get.null", p_138534_, p_138535_);
    });

    public ScoreboardCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_138469_) {
        p_138469_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires((p_138552_) -> {
            return p_138552_.hasPermission(2);
        })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes((p_138585_) -> {
            return listObjectives((CommandSourceStack)p_138585_.getSource());
        }))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes((p_138583_) -> {
            return addObjective((CommandSourceStack)p_138583_.getSource(), StringArgumentType.getString(p_138583_, "objective"), ObjectiveCriteriaArgument.getCriteria(p_138583_, "criteria"), Component.literal(StringArgumentType.getString(p_138583_, "objective")));
        })).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138581_) -> {
            return addObjective((CommandSourceStack)p_138581_.getSource(), StringArgumentType.getString(p_138581_, "objective"), ObjectiveCriteriaArgument.getCriteria(p_138581_, "criteria"), ComponentArgument.getComponent(p_138581_, "displayName"));
        })))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((p_138579_) -> {
            return setDisplayName((CommandSourceStack)p_138579_.getSource(), ObjectiveArgument.getObjective(p_138579_, "objective"), ComponentArgument.getComponent(p_138579_, "displayName"));
        })))).then(createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138577_) -> {
            return removeObjective((CommandSourceStack)p_138577_.getSource(), ObjectiveArgument.getObjective(p_138577_, "objective"));
        })))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes((p_138575_) -> {
            return clearDisplaySlot((CommandSourceStack)p_138575_.getSource(), ScoreboardSlotArgument.getDisplaySlot(p_138575_, "slot"));
        })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138573_) -> {
            return setDisplaySlot((CommandSourceStack)p_138573_.getSource(), ScoreboardSlotArgument.getDisplaySlot(p_138573_, "slot"), ObjectiveArgument.getObjective(p_138573_, "objective"));
        })))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes((p_138571_) -> {
            return listTrackedPlayers((CommandSourceStack)p_138571_.getSource());
        })).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138569_) -> {
            return listTrackedPlayerScores((CommandSourceStack)p_138569_.getSource(), ScoreHolderArgument.getName(p_138569_, "target"));
        })))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((p_138567_) -> {
            return setScore((CommandSourceStack)p_138567_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138567_, "targets"), ObjectiveArgument.getWritableObjective(p_138567_, "objective"), IntegerArgumentType.getInteger(p_138567_, "score"));
        })))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138565_) -> {
            return getScore((CommandSourceStack)p_138565_.getSource(), ScoreHolderArgument.getName(p_138565_, "target"), ObjectiveArgument.getObjective(p_138565_, "objective"));
        }))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_138563_) -> {
            return addScore((CommandSourceStack)p_138563_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138563_, "targets"), ObjectiveArgument.getWritableObjective(p_138563_, "objective"), IntegerArgumentType.getInteger(p_138563_, "score"));
        })))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((p_138561_) -> {
            return removeScore((CommandSourceStack)p_138561_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138561_, "targets"), ObjectiveArgument.getWritableObjective(p_138561_, "objective"), IntegerArgumentType.getInteger(p_138561_, "score"));
        })))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((p_138559_) -> {
            return resetScores((CommandSourceStack)p_138559_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138559_, "targets"));
        })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((p_138550_) -> {
            return resetScore((CommandSourceStack)p_138550_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138550_, "targets"), ObjectiveArgument.getObjective(p_138550_, "objective"));
        }))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_138473_, p_138474_) -> {
            return suggestTriggers((CommandSourceStack)p_138473_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138473_, "targets"), p_138474_);
        }).executes((p_138537_) -> {
            return enableTrigger((CommandSourceStack)p_138537_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138537_, "targets"), ObjectiveArgument.getObjective(p_138537_, "objective"));
        }))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((p_138471_) -> {
            return performOperation((CommandSourceStack)p_138471_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138471_, "targets"), ObjectiveArgument.getWritableObjective(p_138471_, "targetObjective"), OperationArgument.getOperation(p_138471_, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard(p_138471_, "source"), ObjectiveArgument.getObjective(p_138471_, "sourceObjective"));
        })))))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
        LiteralArgumentBuilder<CommandSourceStack> $$0 = Commands.literal("rendertype");
        ObjectiveCriteria.RenderType[] var1 = RenderType.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ObjectiveCriteria.RenderType $$1 = var1[var3];
            $$0.then(Commands.literal($$1.getId()).executes((p_138532_) -> {
                return setRenderType((CommandSourceStack)p_138532_.getSource(), ObjectiveArgument.getObjective(p_138532_, "objective"), $$1);
            }));
        }

        return $$0;
    }

    private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack p_138511_, Collection<String> p_138512_, SuggestionsBuilder p_138513_) {
        List<String> $$3 = Lists.newArrayList();
        Scoreboard $$4 = p_138511_.getServer().getScoreboard();
        Iterator var5 = $$4.getObjectives().iterator();

        while(true) {
            Objective $$5;
            do {
                if (!var5.hasNext()) {
                    return SharedSuggestionProvider.suggest((Iterable)$$3, p_138513_);
                }

                $$5 = (Objective)var5.next();
            } while($$5.getCriteria() != ObjectiveCriteria.TRIGGER);

            boolean $$6 = false;
            Iterator var8 = p_138512_.iterator();

            label32: {
                String $$7;
                do {
                    if (!var8.hasNext()) {
                        break label32;
                    }

                    $$7 = (String)var8.next();
                } while($$4.hasPlayerScore($$7, $$5) && !$$4.getOrCreatePlayerScore($$7, $$5).isLocked());

                $$6 = true;
            }

            if ($$6) {
                $$3.add($$5.getName());
            }
        }
    }

    private static int getScore(CommandSourceStack p_138499_, String p_138500_, Objective p_138501_) throws CommandSyntaxException {
        Scoreboard $$3 = p_138499_.getServer().getScoreboard();
        if (!$$3.hasPlayerScore(p_138500_, p_138501_)) {
            throw ERROR_NO_VALUE.create(p_138501_.getName(), p_138500_);
        } else {
            Score $$4 = $$3.getOrCreatePlayerScore(p_138500_, p_138501_);
            p_138499_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.get.success", p_138500_, $$4.getScore(), p_138501_.getFormattedDisplayName());
            }, false);
            return $$4.getScore();
        }
    }

    private static int performOperation(CommandSourceStack p_138524_, Collection<String> p_138525_, Objective p_138526_, OperationArgument.Operation p_138527_, Collection<String> p_138528_, Objective p_138529_) throws CommandSyntaxException {
        Scoreboard $$6 = p_138524_.getServer().getScoreboard();
        int $$7 = 0;

        Score $$9;
        for(Iterator var8 = p_138525_.iterator(); var8.hasNext(); $$7 += $$9.getScore()) {
            String $$8 = (String)var8.next();
            $$9 = $$6.getOrCreatePlayerScore($$8, p_138526_);
            Iterator var11 = p_138528_.iterator();

            while(var11.hasNext()) {
                String $$10 = (String)var11.next();
                Score $$11 = $$6.getOrCreatePlayerScore($$10, p_138529_);
                p_138527_.apply($$9, $$11);
            }
        }

        if (p_138525_.size() == 1) {
            int $$12 = $$7;
            p_138524_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.operation.success.single", p_138526_.getFormattedDisplayName(), p_138525_.iterator().next(), $$12);
            }, true);
        } else {
            p_138524_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.operation.success.multiple", p_138526_.getFormattedDisplayName(), p_138525_.size());
            }, true);
        }

        return $$7;
    }

    private static int enableTrigger(CommandSourceStack p_138515_, Collection<String> p_138516_, Objective p_138517_) throws CommandSyntaxException {
        if (p_138517_.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_NOT_TRIGGER.create();
        } else {
            Scoreboard $$3 = p_138515_.getServer().getScoreboard();
            int $$4 = 0;
            Iterator var5 = p_138516_.iterator();

            while(var5.hasNext()) {
                String $$5 = (String)var5.next();
                Score $$6 = $$3.getOrCreatePlayerScore($$5, p_138517_);
                if ($$6.isLocked()) {
                    $$6.setLocked(false);
                    ++$$4;
                }
            }

            if ($$4 == 0) {
                throw ERROR_TRIGGER_ALREADY_ENABLED.create();
            } else {
                if (p_138516_.size() == 1) {
                    p_138515_.sendSuccess(() -> {
                        return Component.translatable("commands.scoreboard.players.enable.success.single", p_138517_.getFormattedDisplayName(), p_138516_.iterator().next());
                    }, true);
                } else {
                    p_138515_.sendSuccess(() -> {
                        return Component.translatable("commands.scoreboard.players.enable.success.multiple", p_138517_.getFormattedDisplayName(), p_138516_.size());
                    }, true);
                }

                return $$4;
            }
        }
    }

    private static int resetScores(CommandSourceStack p_138508_, Collection<String> p_138509_) {
        Scoreboard $$2 = p_138508_.getServer().getScoreboard();
        Iterator var3 = p_138509_.iterator();

        while(var3.hasNext()) {
            String $$3 = (String)var3.next();
            $$2.resetPlayerScore($$3, (Objective)null);
        }

        if (p_138509_.size() == 1) {
            p_138508_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.reset.all.single", p_138509_.iterator().next());
            }, true);
        } else {
            p_138508_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.reset.all.multiple", p_138509_.size());
            }, true);
        }

        return p_138509_.size();
    }

    private static int resetScore(CommandSourceStack p_138541_, Collection<String> p_138542_, Objective p_138543_) {
        Scoreboard $$3 = p_138541_.getServer().getScoreboard();
        Iterator var4 = p_138542_.iterator();

        while(var4.hasNext()) {
            String $$4 = (String)var4.next();
            $$3.resetPlayerScore($$4, p_138543_);
        }

        if (p_138542_.size() == 1) {
            p_138541_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.reset.specific.single", p_138543_.getFormattedDisplayName(), p_138542_.iterator().next());
            }, true);
        } else {
            p_138541_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.reset.specific.multiple", p_138543_.getFormattedDisplayName(), p_138542_.size());
            }, true);
        }

        return p_138542_.size();
    }

    private static int setScore(CommandSourceStack p_138519_, Collection<String> p_138520_, Objective p_138521_, int p_138522_) {
        Scoreboard $$4 = p_138519_.getServer().getScoreboard();
        Iterator var5 = p_138520_.iterator();

        while(var5.hasNext()) {
            String $$5 = (String)var5.next();
            Score $$6 = $$4.getOrCreatePlayerScore($$5, p_138521_);
            $$6.setScore(p_138522_);
        }

        if (p_138520_.size() == 1) {
            p_138519_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.set.success.single", p_138521_.getFormattedDisplayName(), p_138520_.iterator().next(), p_138522_);
            }, true);
        } else {
            p_138519_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.set.success.multiple", p_138521_.getFormattedDisplayName(), p_138520_.size(), p_138522_);
            }, true);
        }

        return p_138522_ * p_138520_.size();
    }

    private static int addScore(CommandSourceStack p_138545_, Collection<String> p_138546_, Objective p_138547_, int p_138548_) {
        Scoreboard $$4 = p_138545_.getServer().getScoreboard();
        int $$5 = 0;

        Score $$7;
        for(Iterator var6 = p_138546_.iterator(); var6.hasNext(); $$5 += $$7.getScore()) {
            String $$6 = (String)var6.next();
            $$7 = $$4.getOrCreatePlayerScore($$6, p_138547_);
            $$7.setScore($$7.getScore() + p_138548_);
        }

        if (p_138546_.size() == 1) {
            int $$8 = $$5;
            p_138545_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.add.success.single", p_138548_, p_138547_.getFormattedDisplayName(), p_138546_.iterator().next(), $$8);
            }, true);
        } else {
            p_138545_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.add.success.multiple", p_138548_, p_138547_.getFormattedDisplayName(), p_138546_.size());
            }, true);
        }

        return $$5;
    }

    private static int removeScore(CommandSourceStack p_138554_, Collection<String> p_138555_, Objective p_138556_, int p_138557_) {
        Scoreboard $$4 = p_138554_.getServer().getScoreboard();
        int $$5 = 0;

        Score $$7;
        for(Iterator var6 = p_138555_.iterator(); var6.hasNext(); $$5 += $$7.getScore()) {
            String $$6 = (String)var6.next();
            $$7 = $$4.getOrCreatePlayerScore($$6, p_138556_);
            $$7.setScore($$7.getScore() - p_138557_);
        }

        if (p_138555_.size() == 1) {
            int $$8 = $$5;
            p_138554_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.remove.success.single", p_138557_, p_138556_.getFormattedDisplayName(), p_138555_.iterator().next(), $$8);
            }, true);
        } else {
            p_138554_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.remove.success.multiple", p_138557_, p_138556_.getFormattedDisplayName(), p_138555_.size());
            }, true);
        }

        return $$5;
    }

    private static int listTrackedPlayers(CommandSourceStack p_138476_) {
        Collection<String> $$1 = p_138476_.getServer().getScoreboard().getTrackedPlayers();
        if ($$1.isEmpty()) {
            p_138476_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.list.empty");
            }, false);
        } else {
            p_138476_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.list.success", $$1.size(), ComponentUtils.formatList($$1));
            }, false);
        }

        return $$1.size();
    }

    private static int listTrackedPlayerScores(CommandSourceStack p_138496_, String p_138497_) {
        Map<Objective, Score> $$2 = p_138496_.getServer().getScoreboard().getPlayerScores(p_138497_);
        if ($$2.isEmpty()) {
            p_138496_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.list.entity.empty", p_138497_);
            }, false);
        } else {
            p_138496_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.players.list.entity.success", p_138497_, $$2.size());
            }, false);
            Iterator var3 = $$2.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<Objective, Score> $$3 = (Map.Entry)var3.next();
                p_138496_.sendSuccess(() -> {
                    return Component.translatable("commands.scoreboard.players.list.entity.entry", ((Objective)$$3.getKey()).getFormattedDisplayName(), ((Score)$$3.getValue()).getScore());
                }, false);
            }
        }

        return $$2.size();
    }

    private static int clearDisplaySlot(CommandSourceStack p_138478_, int p_138479_) throws CommandSyntaxException {
        Scoreboard $$2 = p_138478_.getServer().getScoreboard();
        if ($$2.getDisplayObjective(p_138479_) == null) {
            throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
        } else {
            $$2.setDisplayObjective(p_138479_, (Objective)null);
            p_138478_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[p_138479_]);
            }, true);
            return 0;
        }
    }

    private static int setDisplaySlot(CommandSourceStack p_138481_, int p_138482_, Objective p_138483_) throws CommandSyntaxException {
        Scoreboard $$3 = p_138481_.getServer().getScoreboard();
        if ($$3.getDisplayObjective(p_138482_) == p_138483_) {
            throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
        } else {
            $$3.setDisplayObjective(p_138482_, p_138483_);
            p_138481_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[p_138482_], p_138483_.getDisplayName());
            }, true);
            return 0;
        }
    }

    private static int setDisplayName(CommandSourceStack p_138492_, Objective p_138493_, Component p_138494_) {
        if (!p_138493_.getDisplayName().equals(p_138494_)) {
            p_138493_.setDisplayName(p_138494_);
            p_138492_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.modify.displayname", p_138493_.getName(), p_138493_.getFormattedDisplayName());
            }, true);
        }

        return 0;
    }

    private static int setRenderType(CommandSourceStack p_138488_, Objective p_138489_, ObjectiveCriteria.RenderType p_138490_) {
        if (p_138489_.getRenderType() != p_138490_) {
            p_138489_.setRenderType(p_138490_);
            p_138488_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.modify.rendertype", p_138489_.getFormattedDisplayName());
            }, true);
        }

        return 0;
    }

    private static int removeObjective(CommandSourceStack p_138485_, Objective p_138486_) {
        Scoreboard $$2 = p_138485_.getServer().getScoreboard();
        $$2.removeObjective(p_138486_);
        p_138485_.sendSuccess(() -> {
            return Component.translatable("commands.scoreboard.objectives.remove.success", p_138486_.getFormattedDisplayName());
        }, true);
        return $$2.getObjectives().size();
    }

    private static int addObjective(CommandSourceStack p_138503_, String p_138504_, ObjectiveCriteria p_138505_, Component p_138506_) throws CommandSyntaxException {
        Scoreboard $$4 = p_138503_.getServer().getScoreboard();
        if ($$4.getObjective(p_138504_) != null) {
            throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
        } else {
            $$4.addObjective(p_138504_, p_138505_, p_138506_, p_138505_.getDefaultRenderType());
            Objective $$5 = $$4.getObjective(p_138504_);
            p_138503_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.add.success", $$5.getFormattedDisplayName());
            }, true);
            return $$4.getObjectives().size();
        }
    }

    private static int listObjectives(CommandSourceStack p_138539_) {
        Collection<Objective> $$1 = p_138539_.getServer().getScoreboard().getObjectives();
        if ($$1.isEmpty()) {
            p_138539_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.list.empty");
            }, false);
        } else {
            p_138539_.sendSuccess(() -> {
                return Component.translatable("commands.scoreboard.objectives.list.success", $$1.size(), ComponentUtils.formatList($$1, Objective::getFormattedDisplayName));
            }, false);
        }

        return $$1.size();
    }
}
