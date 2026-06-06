//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (p_136344_, p_136345_) -> {
        Collection<Advancement> $$2 = ((CommandSourceStack)p_136344_.getSource()).getServer().getAdvancements().getAllAdvancements();
        return SharedSuggestionProvider.suggestResource($$2.stream().map(Advancement::getId), p_136345_);
    };

    public AdvancementCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_136311_) {
        p_136311_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires((p_136318_) -> {
            return p_136318_.hasPermission(2);
        })).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136363_) -> {
            return perform((CommandSourceStack)p_136363_.getSource(), EntityArgument.getPlayers(p_136363_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136363_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.ONLY));
        })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_136339_, p_136340_) -> {
            return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(p_136339_, "advancement").getCriteria().keySet(), p_136340_);
        }).executes((p_136361_) -> {
            return performCriterion((CommandSourceStack)p_136361_.getSource(), EntityArgument.getPlayers(p_136361_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, ResourceLocationArgument.getAdvancement(p_136361_, "advancement"), StringArgumentType.getString(p_136361_, "criterion"));
        }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136359_) -> {
            return perform((CommandSourceStack)p_136359_.getSource(), EntityArgument.getPlayers(p_136359_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136359_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.FROM));
        })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136357_) -> {
            return perform((CommandSourceStack)p_136357_.getSource(), EntityArgument.getPlayers(p_136357_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136357_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.UNTIL));
        })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136355_) -> {
            return perform((CommandSourceStack)p_136355_.getSource(), EntityArgument.getPlayers(p_136355_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_136355_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.THROUGH));
        })))).then(Commands.literal("everything").executes((p_136353_) -> {
            return perform((CommandSourceStack)p_136353_.getSource(), EntityArgument.getPlayers(p_136353_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.GRANT, ((CommandSourceStack)p_136353_.getSource()).getServer().getAdvancements().getAllAdvancements());
        }))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136351_) -> {
            return perform((CommandSourceStack)p_136351_.getSource(), EntityArgument.getPlayers(p_136351_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136351_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.ONLY));
        })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_136315_, p_136316_) -> {
            return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(p_136315_, "advancement").getCriteria().keySet(), p_136316_);
        }).executes((p_136349_) -> {
            return performCriterion((CommandSourceStack)p_136349_.getSource(), EntityArgument.getPlayers(p_136349_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, ResourceLocationArgument.getAdvancement(p_136349_, "advancement"), StringArgumentType.getString(p_136349_, "criterion"));
        }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136347_) -> {
            return perform((CommandSourceStack)p_136347_.getSource(), EntityArgument.getPlayers(p_136347_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136347_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.FROM));
        })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136342_) -> {
            return perform((CommandSourceStack)p_136342_.getSource(), EntityArgument.getPlayers(p_136342_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136342_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.UNTIL));
        })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_136337_) -> {
            return perform((CommandSourceStack)p_136337_.getSource(), EntityArgument.getPlayers(p_136337_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_136337_, "advancement"), net.minecraft.server.commands.AdvancementCommands.Mode.THROUGH));
        })))).then(Commands.literal("everything").executes((p_136313_) -> {
            return perform((CommandSourceStack)p_136313_.getSource(), EntityArgument.getPlayers(p_136313_, "targets"), net.minecraft.server.commands.AdvancementCommands.Action.REVOKE, ((CommandSourceStack)p_136313_.getSource()).getServer().getAdvancements().getAllAdvancements());
        })))));
    }

    private static int perform(CommandSourceStack p_136320_, Collection<ServerPlayer> p_136321_, Action p_136322_, Collection<Advancement> p_136323_) {
        int $$4 = 0;

        ServerPlayer $$5;
        for(Iterator var5 = p_136321_.iterator(); var5.hasNext(); $$4 += p_136322_.perform($$5, (Iterable)p_136323_)) {
            $$5 = (ServerPlayer)var5.next();
        }

        if ($$4 == 0) {
            if (p_136323_.size() == 1) {
                if (p_136321_.size() == 1) {
                    throw new CommandRuntimeException(Component.translatable(p_136322_.getKey() + ".one.to.one.failure", ((Advancement)p_136323_.iterator().next()).getChatComponent(), ((ServerPlayer)p_136321_.iterator().next()).getDisplayName()));
                } else {
                    throw new CommandRuntimeException(Component.translatable(p_136322_.getKey() + ".one.to.many.failure", ((Advancement)p_136323_.iterator().next()).getChatComponent(), p_136321_.size()));
                }
            } else if (p_136321_.size() == 1) {
                throw new CommandRuntimeException(Component.translatable(p_136322_.getKey() + ".many.to.one.failure", p_136323_.size(), ((ServerPlayer)p_136321_.iterator().next()).getDisplayName()));
            } else {
                throw new CommandRuntimeException(Component.translatable(p_136322_.getKey() + ".many.to.many.failure", p_136323_.size(), p_136321_.size()));
            }
        } else {
            if (p_136323_.size() == 1) {
                if (p_136321_.size() == 1) {
                    p_136320_.sendSuccess(() -> {
                        return Component.translatable(p_136322_.getKey() + ".one.to.one.success", ((Advancement)p_136323_.iterator().next()).getChatComponent(), ((ServerPlayer)p_136321_.iterator().next()).getDisplayName());
                    }, true);
                } else {
                    p_136320_.sendSuccess(() -> {
                        return Component.translatable(p_136322_.getKey() + ".one.to.many.success", ((Advancement)p_136323_.iterator().next()).getChatComponent(), p_136321_.size());
                    }, true);
                }
            } else if (p_136321_.size() == 1) {
                p_136320_.sendSuccess(() -> {
                    return Component.translatable(p_136322_.getKey() + ".many.to.one.success", p_136323_.size(), ((ServerPlayer)p_136321_.iterator().next()).getDisplayName());
                }, true);
            } else {
                p_136320_.sendSuccess(() -> {
                    return Component.translatable(p_136322_.getKey() + ".many.to.many.success", p_136323_.size(), p_136321_.size());
                }, true);
            }

            return $$4;
        }
    }

    private static int performCriterion(CommandSourceStack p_136325_, Collection<ServerPlayer> p_136326_, Action p_136327_, Advancement p_136328_, String p_136329_) {
        int $$5 = 0;
        if (!p_136328_.getCriteria().containsKey(p_136329_)) {
            throw new CommandRuntimeException(Component.translatable("commands.advancement.criterionNotFound", p_136328_.getChatComponent(), p_136329_));
        } else {
            Iterator var6 = p_136326_.iterator();

            while(var6.hasNext()) {
                ServerPlayer $$6 = (ServerPlayer)var6.next();
                if (p_136327_.performCriterion($$6, p_136328_, p_136329_)) {
                    ++$$5;
                }
            }

            if ($$5 == 0) {
                if (p_136326_.size() == 1) {
                    throw new CommandRuntimeException(Component.translatable(p_136327_.getKey() + ".criterion.to.one.failure", p_136329_, p_136328_.getChatComponent(), ((ServerPlayer)p_136326_.iterator().next()).getDisplayName()));
                } else {
                    throw new CommandRuntimeException(Component.translatable(p_136327_.getKey() + ".criterion.to.many.failure", p_136329_, p_136328_.getChatComponent(), p_136326_.size()));
                }
            } else {
                if (p_136326_.size() == 1) {
                    p_136325_.sendSuccess(() -> {
                        return Component.translatable(p_136327_.getKey() + ".criterion.to.one.success", p_136329_, p_136328_.getChatComponent(), ((ServerPlayer)p_136326_.iterator().next()).getDisplayName());
                    }, true);
                } else {
                    p_136325_.sendSuccess(() -> {
                        return Component.translatable(p_136327_.getKey() + ".criterion.to.many.success", p_136329_, p_136328_.getChatComponent(), p_136326_.size());
                    }, true);
                }

                return $$5;
            }
        }
    }

    private static List<Advancement> getAdvancements(Advancement p_136334_, Mode p_136335_) {
        List<Advancement> $$2 = Lists.newArrayList();
        if (p_136335_.parents) {
            for(Advancement $$3 = p_136334_.getParent(); $$3 != null; $$3 = $$3.getParent()) {
                $$2.add($$3);
            }
        }

        $$2.add(p_136334_);
        if (p_136335_.children) {
            addChildren(p_136334_, $$2);
        }

        return $$2;
    }

    private static void addChildren(Advancement p_136331_, List<Advancement> p_136332_) {
        Iterator var2 = p_136331_.getChildren().iterator();

        while(var2.hasNext()) {
            Advancement $$2 = (Advancement)var2.next();
            p_136332_.add($$2);
            addChildren($$2, p_136332_);
        }

    }

    private static enum Action {
        GRANT("grant") {
            protected boolean perform(ServerPlayer p_136395_, Advancement p_136396_) {
                AdvancementProgress $$2 = p_136395_.getAdvancements().getOrStartProgress(p_136396_);
                if ($$2.isDone()) {
                    return false;
                } else {
                    Iterator var4 = $$2.getRemainingCriteria().iterator();

                    while(var4.hasNext()) {
                        String $$3 = (String)var4.next();
                        p_136395_.getAdvancements().award(p_136396_, $$3);
                    }

                    return true;
                }
            }

            protected boolean performCriterion(ServerPlayer p_136398_, Advancement p_136399_, String p_136400_) {
                return p_136398_.getAdvancements().award(p_136399_, p_136400_);
            }
        },
        REVOKE("revoke") {
            protected boolean perform(ServerPlayer p_136406_, Advancement p_136407_) {
                AdvancementProgress $$2 = p_136406_.getAdvancements().getOrStartProgress(p_136407_);
                if (!$$2.hasProgress()) {
                    return false;
                } else {
                    Iterator var4 = $$2.getCompletedCriteria().iterator();

                    while(var4.hasNext()) {
                        String $$3 = (String)var4.next();
                        p_136406_.getAdvancements().revoke(p_136407_, $$3);
                    }

                    return true;
                }
            }

            protected boolean performCriterion(ServerPlayer p_136409_, Advancement p_136410_, String p_136411_) {
                return p_136409_.getAdvancements().revoke(p_136410_, p_136411_);
            }
        };

        private final String key;

        Action(String p_136372_) {
            this.key = "commands.advancement." + p_136372_;
        }

        public int perform(ServerPlayer p_136380_, Iterable<Advancement> p_136381_) {
            int $$2 = 0;
            Iterator var4 = p_136381_.iterator();

            while(var4.hasNext()) {
                Advancement $$3 = (Advancement)var4.next();
                if (this.perform(p_136380_, $$3)) {
                    ++$$2;
                }
            }

            return $$2;
        }

        protected abstract boolean perform(ServerPlayer var1, Advancement var2);

        protected abstract boolean performCriterion(ServerPlayer var1, Advancement var2, String var3);

        protected String getKey() {
            return this.key;
        }
    }

    static enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        final boolean parents;
        final boolean children;

        private Mode(boolean p_136424_, boolean p_136425_) {
            this.parents = p_136424_;
            this.children = p_136425_;
        }
    }
}
