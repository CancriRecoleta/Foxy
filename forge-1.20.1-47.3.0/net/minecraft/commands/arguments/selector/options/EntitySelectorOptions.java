//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.advancements.critereon.MinMaxBounds.Doubles;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class EntitySelectorOptions {
    private static final Map<String, Option> OPTIONS = Maps.newHashMap();
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType((p_121520_) -> {
        return Component.translatable("argument.entity.options.unknown", p_121520_);
    });
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType((p_121516_) -> {
        return Component.translatable("argument.entity.options.inapplicable", p_121516_);
    });
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(Component.translatable("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType((p_121508_) -> {
        return Component.translatable("argument.entity.options.sort.irreversible", p_121508_);
    });
    public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType((p_121493_) -> {
        return Component.translatable("argument.entity.options.mode.invalid", p_121493_);
    });
    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType((p_121452_) -> {
        return Component.translatable("argument.entity.options.type.invalid", p_121452_);
    });

    public EntitySelectorOptions() {
    }

    public static void register(String p_121454_, Modifier p_121455_, Predicate<EntitySelectorParser> p_121456_, Component p_121457_) {
        OPTIONS.put(p_121454_, new Option(p_121455_, p_121456_, p_121457_));
    }

    public static void bootStrap() {
        if (OPTIONS.isEmpty()) {
            register("name", (p_121425_) -> {
                int $$1 = p_121425_.getReader().getCursor();
                boolean $$2 = p_121425_.shouldInvertValue();
                String $$3 = p_121425_.getReader().readString();
                if (p_121425_.hasNameNotEquals() && !$$2) {
                    p_121425_.getReader().setCursor($$1);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_121425_.getReader(), "name");
                } else {
                    if ($$2) {
                        p_121425_.setHasNameNotEquals(true);
                    } else {
                        p_121425_.setHasNameEquals(true);
                    }

                    p_121425_.addPredicate((p_175209_) -> {
                        return p_175209_.getName().getString().equals($$3) != $$2;
                    });
                }
            }, (p_121423_) -> {
                return !p_121423_.hasNameEquals();
            }, Component.translatable("argument.entity.options.name.description"));
            register("distance", (p_121421_) -> {
                int $$1 = p_121421_.getReader().getCursor();
                MinMaxBounds.Doubles $$2 = Doubles.fromReader(p_121421_.getReader());
                if (($$2.getMin() == null || !((Double)$$2.getMin() < 0.0)) && ($$2.getMax() == null || !((Double)$$2.getMax() < 0.0))) {
                    p_121421_.setDistance($$2);
                    p_121421_.setWorldLimited();
                } else {
                    p_121421_.getReader().setCursor($$1);
                    throw ERROR_RANGE_NEGATIVE.createWithContext(p_121421_.getReader());
                }
            }, (p_121419_) -> {
                return p_121419_.getDistance().isAny();
            }, Component.translatable("argument.entity.options.distance.description"));
            register("level", (p_121417_) -> {
                int $$1 = p_121417_.getReader().getCursor();
                MinMaxBounds.Ints $$2 = Ints.fromReader(p_121417_.getReader());
                if (($$2.getMin() == null || (Integer)$$2.getMin() >= 0) && ($$2.getMax() == null || (Integer)$$2.getMax() >= 0)) {
                    p_121417_.setLevel($$2);
                    p_121417_.setIncludesEntities(false);
                } else {
                    p_121417_.getReader().setCursor($$1);
                    throw ERROR_LEVEL_NEGATIVE.createWithContext(p_121417_.getReader());
                }
            }, (p_121415_) -> {
                return p_121415_.getLevel().isAny();
            }, Component.translatable("argument.entity.options.level.description"));
            register("x", (p_121413_) -> {
                p_121413_.setWorldLimited();
                p_121413_.setX(p_121413_.getReader().readDouble());
            }, (p_121411_) -> {
                return p_121411_.getX() == null;
            }, Component.translatable("argument.entity.options.x.description"));
            register("y", (p_121409_) -> {
                p_121409_.setWorldLimited();
                p_121409_.setY(p_121409_.getReader().readDouble());
            }, (p_121407_) -> {
                return p_121407_.getY() == null;
            }, Component.translatable("argument.entity.options.y.description"));
            register("z", (p_121405_) -> {
                p_121405_.setWorldLimited();
                p_121405_.setZ(p_121405_.getReader().readDouble());
            }, (p_121403_) -> {
                return p_121403_.getZ() == null;
            }, Component.translatable("argument.entity.options.z.description"));
            register("dx", (p_121401_) -> {
                p_121401_.setWorldLimited();
                p_121401_.setDeltaX(p_121401_.getReader().readDouble());
            }, (p_121399_) -> {
                return p_121399_.getDeltaX() == null;
            }, Component.translatable("argument.entity.options.dx.description"));
            register("dy", (p_121397_) -> {
                p_121397_.setWorldLimited();
                p_121397_.setDeltaY(p_121397_.getReader().readDouble());
            }, (p_121395_) -> {
                return p_121395_.getDeltaY() == null;
            }, Component.translatable("argument.entity.options.dy.description"));
            register("dz", (p_121562_) -> {
                p_121562_.setWorldLimited();
                p_121562_.setDeltaZ(p_121562_.getReader().readDouble());
            }, (p_121560_) -> {
                return p_121560_.getDeltaZ() == null;
            }, Component.translatable("argument.entity.options.dz.description"));
            register("x_rotation", (p_121558_) -> {
                p_121558_.setRotX(WrappedMinMaxBounds.fromReader(p_121558_.getReader(), true, Mth::wrapDegrees));
            }, (p_121556_) -> {
                return p_121556_.getRotX() == WrappedMinMaxBounds.ANY;
            }, Component.translatable("argument.entity.options.x_rotation.description"));
            register("y_rotation", (p_121554_) -> {
                p_121554_.setRotY(WrappedMinMaxBounds.fromReader(p_121554_.getReader(), true, Mth::wrapDegrees));
            }, (p_121552_) -> {
                return p_121552_.getRotY() == WrappedMinMaxBounds.ANY;
            }, Component.translatable("argument.entity.options.y_rotation.description"));
            register("limit", (p_121550_) -> {
                int $$1 = p_121550_.getReader().getCursor();
                int $$2 = p_121550_.getReader().readInt();
                if ($$2 < 1) {
                    p_121550_.getReader().setCursor($$1);
                    throw ERROR_LIMIT_TOO_SMALL.createWithContext(p_121550_.getReader());
                } else {
                    p_121550_.setMaxResults($$2);
                    p_121550_.setLimited(true);
                }
            }, (p_121548_) -> {
                return !p_121548_.isCurrentEntity() && !p_121548_.isLimited();
            }, Component.translatable("argument.entity.options.limit.description"));
            register("sort", (p_247983_) -> {
                int $$1 = p_247983_.getReader().getCursor();
                String $$2 = p_247983_.getReader().readUnquotedString();
                p_247983_.setSuggestions((p_175153_, p_175154_) -> {
                    return SharedSuggestionProvider.suggest((Iterable)Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_175153_);
                });
                BiConsumer var10001;
                switch ($$2) {
                    case "nearest":
                        var10001 = EntitySelectorParser.ORDER_NEAREST;
                        break;
                    case "furthest":
                        var10001 = EntitySelectorParser.ORDER_FURTHEST;
                        break;
                    case "random":
                        var10001 = EntitySelectorParser.ORDER_RANDOM;
                        break;
                    case "arbitrary":
                        var10001 = EntitySelector.ORDER_ARBITRARY;
                        break;
                    default:
                        p_247983_.getReader().setCursor($$1);
                        throw ERROR_SORT_UNKNOWN.createWithContext(p_247983_.getReader(), $$2);
                }

                p_247983_.setOrder(var10001);
                p_247983_.setSorted(true);
            }, (p_121544_) -> {
                return !p_121544_.isCurrentEntity() && !p_121544_.isSorted();
            }, Component.translatable("argument.entity.options.sort.description"));
            register("gamemode", (p_121542_) -> {
                p_121542_.setSuggestions((p_175193_, p_175194_) -> {
                    String $$3 = p_175193_.getRemaining().toLowerCase(Locale.ROOT);
                    boolean $$4 = !p_121542_.hasGamemodeNotEquals();
                    boolean $$5 = true;
                    if (!$$3.isEmpty()) {
                        if ($$3.charAt(0) == '!') {
                            $$4 = false;
                            $$3 = $$3.substring(1);
                        } else {
                            $$5 = false;
                        }
                    }

                    GameType[] var6 = GameType.values();
                    int var7 = var6.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        GameType $$6 = var6[var8];
                        if ($$6.getName().toLowerCase(Locale.ROOT).startsWith($$3)) {
                            if ($$5) {
                                p_175193_.suggest("!" + $$6.getName());
                            }

                            if ($$4) {
                                p_175193_.suggest($$6.getName());
                            }
                        }
                    }

                    return p_175193_.buildFuture();
                });
                int $$1 = p_121542_.getReader().getCursor();
                boolean $$2 = p_121542_.shouldInvertValue();
                if (p_121542_.hasGamemodeNotEquals() && !$$2) {
                    p_121542_.getReader().setCursor($$1);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_121542_.getReader(), "gamemode");
                } else {
                    String $$3 = p_121542_.getReader().readUnquotedString();
                    GameType $$4 = GameType.byName($$3, (GameType)null);
                    if ($$4 == null) {
                        p_121542_.getReader().setCursor($$1);
                        throw ERROR_GAME_MODE_INVALID.createWithContext(p_121542_.getReader(), $$3);
                    } else {
                        p_121542_.setIncludesEntities(false);
                        p_121542_.addPredicate((p_175190_) -> {
                            if (!(p_175190_ instanceof ServerPlayer)) {
                                return false;
                            } else {
                                GameType $$3 = ((ServerPlayer)p_175190_).gameMode.getGameModeForPlayer();
                                return $$2 ? $$3 != $$4 : $$3 == $$4;
                            }
                        });
                        if ($$2) {
                            p_121542_.setHasGamemodeNotEquals(true);
                        } else {
                            p_121542_.setHasGamemodeEquals(true);
                        }

                    }
                }
            }, (p_121540_) -> {
                return !p_121540_.hasGamemodeEquals();
            }, Component.translatable("argument.entity.options.gamemode.description"));
            register("team", (p_121538_) -> {
                boolean $$1 = p_121538_.shouldInvertValue();
                String $$2 = p_121538_.getReader().readUnquotedString();
                p_121538_.addPredicate((p_175198_) -> {
                    if (!(p_175198_ instanceof LivingEntity)) {
                        return false;
                    } else {
                        Team $$3 = p_175198_.getTeam();
                        String $$4 = $$3 == null ? "" : $$3.getName();
                        return $$4.equals($$2) != $$1;
                    }
                });
                if ($$1) {
                    p_121538_.setHasTeamNotEquals(true);
                } else {
                    p_121538_.setHasTeamEquals(true);
                }

            }, (p_121536_) -> {
                return !p_121536_.hasTeamEquals();
            }, Component.translatable("argument.entity.options.team.description"));
            register("type", (p_121534_) -> {
                p_121534_.setSuggestions((p_258162_, p_258163_) -> {
                    SharedSuggestionProvider.suggestResource((Iterable)BuiltInRegistries.ENTITY_TYPE.keySet(), p_258162_, String.valueOf('!'));
                    SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), p_258162_, "!#");
                    if (!p_121534_.isTypeLimitedInversely()) {
                        SharedSuggestionProvider.suggestResource((Iterable)BuiltInRegistries.ENTITY_TYPE.keySet(), p_258162_);
                        SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), p_258162_, String.valueOf('#'));
                    }

                    return p_258162_.buildFuture();
                });
                int $$1 = p_121534_.getReader().getCursor();
                boolean $$2 = p_121534_.shouldInvertValue();
                if (p_121534_.isTypeLimitedInversely() && !$$2) {
                    p_121534_.getReader().setCursor($$1);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_121534_.getReader(), "type");
                } else {
                    if ($$2) {
                        p_121534_.setTypeLimitedInversely();
                    }

                    if (p_121534_.isTag()) {
                        TagKey<EntityType<?>> $$3 = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.read(p_121534_.getReader()));
                        p_121534_.addPredicate((p_205691_) -> {
                            return p_205691_.getType().is($$3) != $$2;
                        });
                    } else {
                        ResourceLocation $$4 = ResourceLocation.read(p_121534_.getReader());
                        EntityType<?> $$5 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional($$4).orElseThrow(() -> {
                            p_121534_.getReader().setCursor($$1);
                            return ERROR_ENTITY_TYPE_INVALID.createWithContext(p_121534_.getReader(), $$4.toString());
                        });
                        if (Objects.equals(EntityType.PLAYER, $$5) && !$$2) {
                            p_121534_.setIncludesEntities(false);
                        }

                        p_121534_.addPredicate((p_175151_) -> {
                            return Objects.equals($$5, p_175151_.getType()) != $$2;
                        });
                        if (!$$2) {
                            p_121534_.limitToType($$5);
                        }
                    }

                }
            }, (p_121532_) -> {
                return !p_121532_.isTypeLimited();
            }, Component.translatable("argument.entity.options.type.description"));
            register("tag", (p_121530_) -> {
                boolean $$1 = p_121530_.shouldInvertValue();
                String $$2 = p_121530_.getReader().readUnquotedString();
                p_121530_.addPredicate((p_175166_) -> {
                    if ("".equals($$2)) {
                        return p_175166_.getTags().isEmpty() != $$1;
                    } else {
                        return p_175166_.getTags().contains($$2) != $$1;
                    }
                });
            }, (p_121528_) -> {
                return true;
            }, Component.translatable("argument.entity.options.tag.description"));
            register("nbt", (p_121526_) -> {
                boolean $$1 = p_121526_.shouldInvertValue();
                CompoundTag $$2 = (new TagParser(p_121526_.getReader())).readStruct();
                p_121526_.addPredicate((p_175176_) -> {
                    CompoundTag $$3 = p_175176_.saveWithoutId(new CompoundTag());
                    if (p_175176_ instanceof ServerPlayer) {
                        ItemStack $$4 = ((ServerPlayer)p_175176_).getInventory().getSelected();
                        if (!$$4.isEmpty()) {
                            $$3.put("SelectedItem", $$4.save(new CompoundTag()));
                        }
                    }

                    return NbtUtils.compareNbt($$2, $$3, true) != $$1;
                });
            }, (p_121524_) -> {
                return true;
            }, Component.translatable("argument.entity.options.nbt.description"));
            register("scores", (p_121522_) -> {
                StringReader $$1 = p_121522_.getReader();
                Map<String, MinMaxBounds.Ints> $$2 = Maps.newHashMap();
                $$1.expect('{');
                $$1.skipWhitespace();

                while($$1.canRead() && $$1.peek() != '}') {
                    $$1.skipWhitespace();
                    String $$3 = $$1.readUnquotedString();
                    $$1.skipWhitespace();
                    $$1.expect('=');
                    $$1.skipWhitespace();
                    MinMaxBounds.Ints $$4 = Ints.fromReader($$1);
                    $$2.put($$3, $$4);
                    $$1.skipWhitespace();
                    if ($$1.canRead() && $$1.peek() == ',') {
                        $$1.skip();
                    }
                }

                $$1.expect('}');
                if (!$$2.isEmpty()) {
                    p_121522_.addPredicate((p_175201_) -> {
                        Scoreboard $$2x = p_175201_.getServer().getScoreboard();
                        String $$3 = p_175201_.getScoreboardName();
                        Iterator var4 = $$2.entrySet().iterator();

                        Map.Entry $$4;
                        int $$7;
                        do {
                            if (!var4.hasNext()) {
                                return true;
                            }

                            $$4 = (Map.Entry)var4.next();
                            Objective $$5 = $$2x.getObjective((String)$$4.getKey());
                            if ($$5 == null) {
                                return false;
                            }

                            if (!$$2x.hasPlayerScore($$3, $$5)) {
                                return false;
                            }

                            Score $$6 = $$2x.getOrCreatePlayerScore($$3, $$5);
                            $$7 = $$6.getScore();
                        } while(((MinMaxBounds.Ints)$$4.getValue()).matches($$7));

                        return false;
                    });
                }

                p_121522_.setHasScores(true);
            }, (p_121518_) -> {
                return !p_121518_.hasScores();
            }, Component.translatable("argument.entity.options.scores.description"));
            register("advancements", (p_121514_) -> {
                StringReader $$1 = p_121514_.getReader();
                Map<ResourceLocation, Predicate<AdvancementProgress>> $$2 = Maps.newHashMap();
                $$1.expect('{');
                $$1.skipWhitespace();

                while($$1.canRead() && $$1.peek() != '}') {
                    $$1.skipWhitespace();
                    ResourceLocation $$3 = ResourceLocation.read($$1);
                    $$1.skipWhitespace();
                    $$1.expect('=');
                    $$1.skipWhitespace();
                    if ($$1.canRead() && $$1.peek() == '{') {
                        Map<String, Predicate<CriterionProgress>> $$4 = Maps.newHashMap();
                        $$1.skipWhitespace();
                        $$1.expect('{');
                        $$1.skipWhitespace();

                        while($$1.canRead() && $$1.peek() != '}') {
                            $$1.skipWhitespace();
                            String $$5 = $$1.readUnquotedString();
                            $$1.skipWhitespace();
                            $$1.expect('=');
                            $$1.skipWhitespace();
                            boolean $$6 = $$1.readBoolean();
                            $$4.put($$5, (p_175186_) -> {
                                return p_175186_.isDone() == $$6;
                            });
                            $$1.skipWhitespace();
                            if ($$1.canRead() && $$1.peek() == ',') {
                                $$1.skip();
                            }
                        }

                        $$1.skipWhitespace();
                        $$1.expect('}');
                        $$1.skipWhitespace();
                        $$2.put($$3, (p_175169_) -> {
                            Iterator var2 = $$4.entrySet().iterator();

                            Map.Entry $$2;
                            CriterionProgress $$3;
                            do {
                                if (!var2.hasNext()) {
                                    return true;
                                }

                                $$2 = (Map.Entry)var2.next();
                                $$3 = p_175169_.getCriterion((String)$$2.getKey());
                            } while($$3 != null && ((Predicate)$$2.getValue()).test($$3));

                            return false;
                        });
                    } else {
                        boolean $$7 = $$1.readBoolean();
                        $$2.put($$3, (p_175183_) -> {
                            return p_175183_.isDone() == $$7;
                        });
                    }

                    $$1.skipWhitespace();
                    if ($$1.canRead() && $$1.peek() == ',') {
                        $$1.skip();
                    }
                }

                $$1.expect('}');
                if (!$$2.isEmpty()) {
                    p_121514_.addPredicate((p_175172_) -> {
                        if (!(p_175172_ instanceof ServerPlayer $$2x)) {
                            return false;
                        } else {
                            PlayerAdvancements $$3 = $$2x.getAdvancements();
                            ServerAdvancementManager $$4 = $$2x.getServer().getAdvancements();
                            Iterator var5 = $$2.entrySet().iterator();

                            Map.Entry $$5;
                            Advancement $$6;
                            do {
                                if (!var5.hasNext()) {
                                    return true;
                                }

                                $$5 = (Map.Entry)var5.next();
                                $$6 = $$4.getAdvancement((ResourceLocation)$$5.getKey());
                            } while($$6 != null && ((Predicate)$$5.getValue()).test($$3.getOrStartProgress($$6)));

                            return false;
                        }
                    });
                    p_121514_.setIncludesEntities(false);
                }

                p_121514_.setHasAdvancements(true);
            }, (p_121506_) -> {
                return !p_121506_.hasAdvancements();
            }, Component.translatable("argument.entity.options.advancements.description"));
            register("predicate", (p_121487_) -> {
                boolean $$1 = p_121487_.shouldInvertValue();
                ResourceLocation $$2 = ResourceLocation.read(p_121487_.getReader());
                p_121487_.addPredicate((p_287325_) -> {
                    if (!(p_287325_.level() instanceof ServerLevel)) {
                        return false;
                    } else {
                        ServerLevel $$3 = (ServerLevel)p_287325_.level();
                        LootItemCondition $$4 = (LootItemCondition)$$3.getServer().getLootData().getElement(LootDataType.PREDICATE, $$2);
                        if ($$4 == null) {
                            return false;
                        } else {
                            LootParams $$5 = (new LootParams.Builder($$3)).withParameter(LootContextParams.THIS_ENTITY, p_287325_).withParameter(LootContextParams.ORIGIN, p_287325_.position()).create(LootContextParamSets.SELECTOR);
                            LootContext $$6 = (new LootContext.Builder($$5)).create((ResourceLocation)null);
                            $$6.pushVisitedElement(LootContext.createVisitedEntry($$4));
                            return $$1 ^ $$4.test($$6);
                        }
                    }
                });
            }, (p_121435_) -> {
                return true;
            }, Component.translatable("argument.entity.options.predicate.description"));
        }
    }

    public static Modifier get(EntitySelectorParser p_121448_, String p_121449_, int p_121450_) throws CommandSyntaxException {
        Option $$3 = (Option)OPTIONS.get(p_121449_);
        if ($$3 != null) {
            if ($$3.canUse.test(p_121448_)) {
                return $$3.modifier;
            } else {
                throw ERROR_INAPPLICABLE_OPTION.createWithContext(p_121448_.getReader(), p_121449_);
            }
        } else {
            p_121448_.getReader().setCursor(p_121450_);
            throw ERROR_UNKNOWN_OPTION.createWithContext(p_121448_.getReader(), p_121449_);
        }
    }

    public static void suggestNames(EntitySelectorParser p_121441_, SuggestionsBuilder p_121442_) {
        String $$2 = p_121442_.getRemaining().toLowerCase(Locale.ROOT);
        Iterator var3 = OPTIONS.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, Option> $$3 = (Map.Entry)var3.next();
            if (((Option)$$3.getValue()).canUse.test(p_121441_) && ((String)$$3.getKey()).toLowerCase(Locale.ROOT).startsWith($$2)) {
                p_121442_.suggest((String)$$3.getKey() + "=", ((Option)$$3.getValue()).description);
            }
        }

    }

    static record Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
        Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
            this.modifier = modifier;
            this.canUse = canUse;
            this.description = description;
        }

        public Modifier modifier() {
            return this.modifier;
        }

        public Predicate<EntitySelectorParser> canUse() {
            return this.canUse;
        }

        public Component description() {
            return this.description;
        }
    }

    public interface Modifier {
        void handle(EntitySelectorParser var1) throws CommandSyntaxException;
    }
}
