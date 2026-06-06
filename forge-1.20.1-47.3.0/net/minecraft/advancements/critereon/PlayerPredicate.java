//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerPredicate implements EntitySubPredicate {
    public static final int LOOKING_AT_RANGE = 100;
    private final MinMaxBounds.Ints level;
    @Nullable
    private final GameType gameType;
    private final Map<Stat<?>, MinMaxBounds.Ints> stats;
    private final Object2BooleanMap<ResourceLocation> recipes;
    private final Map<ResourceLocation, AdvancementPredicate> advancements;
    private final EntityPredicate lookingAt;

    private static AdvancementPredicate advancementPredicateFromJson(JsonElement p_62290_) {
        if (p_62290_.isJsonPrimitive()) {
            boolean $$1 = p_62290_.getAsBoolean();
            return new AdvancementDonePredicate($$1);
        } else {
            Object2BooleanMap<String> $$2 = new Object2BooleanOpenHashMap();
            JsonObject $$3 = GsonHelper.convertToJsonObject(p_62290_, "criterion data");
            $$3.entrySet().forEach((p_62288_) -> {
                boolean $$2x = GsonHelper.convertToBoolean((JsonElement)p_62288_.getValue(), "criterion test");
                $$2.put((String)p_62288_.getKey(), $$2x);
            });
            return new AdvancementCriterionsPredicate($$2);
        }
    }

    PlayerPredicate(MinMaxBounds.Ints p_156746_, @Nullable GameType p_156747_, Map<Stat<?>, MinMaxBounds.Ints> p_156748_, Object2BooleanMap<ResourceLocation> p_156749_, Map<ResourceLocation, AdvancementPredicate> p_156750_, EntityPredicate p_156751_) {
        this.level = p_156746_;
        this.gameType = p_156747_;
        this.stats = p_156748_;
        this.recipes = p_156749_;
        this.advancements = p_156750_;
        this.lookingAt = p_156751_;
    }

    public boolean matches(Entity p_222484_, ServerLevel p_222485_, @Nullable Vec3 p_222486_) {
        if (!(p_222484_ instanceof ServerPlayer $$3)) {
            return false;
        } else if (!this.level.matches($$3.experienceLevel)) {
            return false;
        } else if (this.gameType != null && this.gameType != $$3.gameMode.getGameModeForPlayer()) {
            return false;
        } else {
            StatsCounter $$4 = $$3.getStats();
            Iterator var6 = this.stats.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<Stat<?>, MinMaxBounds.Ints> $$5 = (Map.Entry)var6.next();
                int $$6 = $$4.getValue((Stat)$$5.getKey());
                if (!((MinMaxBounds.Ints)$$5.getValue()).matches($$6)) {
                    return false;
                }
            }

            RecipeBook $$7 = $$3.getRecipeBook();
            ObjectIterator var13 = this.recipes.object2BooleanEntrySet().iterator();

            while(var13.hasNext()) {
                Object2BooleanMap.Entry<ResourceLocation> $$8 = (Object2BooleanMap.Entry)var13.next();
                if ($$7.contains((ResourceLocation)$$8.getKey()) != $$8.getBooleanValue()) {
                    return false;
                }
            }

            if (!this.advancements.isEmpty()) {
                label88: {
                    PlayerAdvancements $$9 = $$3.getAdvancements();
                    ServerAdvancementManager $$10 = $$3.getServer().getAdvancements();
                    Iterator var9 = this.advancements.entrySet().iterator();

                    Map.Entry $$11;
                    Advancement $$12;
                    do {
                        if (!var9.hasNext()) {
                            break label88;
                        }

                        $$11 = (Map.Entry)var9.next();
                        $$12 = $$10.getAdvancement((ResourceLocation)$$11.getKey());
                    } while($$12 != null && ((AdvancementPredicate)$$11.getValue()).test($$9.getOrStartProgress($$12)));

                    return false;
                }
            }

            if (this.lookingAt != EntityPredicate.ANY) {
                Vec3 $$13 = $$3.getEyePosition();
                Vec3 $$14 = $$3.getViewVector(1.0F);
                Vec3 $$15 = $$13.add($$14.x * 100.0, $$14.y * 100.0, $$14.z * 100.0);
                EntityHitResult $$16 = ProjectileUtil.getEntityHitResult($$3.level(), $$3, $$13, $$15, (new AABB($$13, $$15)).inflate(1.0), (p_156765_) -> {
                    return !p_156765_.isSpectator();
                }, 0.0F);
                if ($$16 != null && $$16.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                    Entity $$17 = $$16.getEntity();
                    if (this.lookingAt.matches($$3, $$17) && $$3.hasLineOfSight($$17)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static PlayerPredicate fromJson(JsonObject p_222492_) {
        MinMaxBounds.Ints $$1 = Ints.fromJson(p_222492_.get("level"));
        String $$2 = GsonHelper.getAsString(p_222492_, "gamemode", "");
        GameType $$3 = GameType.byName($$2, (GameType)null);
        Map<Stat<?>, MinMaxBounds.Ints> $$4 = Maps.newHashMap();
        JsonArray $$5 = GsonHelper.getAsJsonArray(p_222492_, "stats", (JsonArray)null);
        if ($$5 != null) {
            Iterator var6 = $$5.iterator();

            while(var6.hasNext()) {
                JsonElement $$6 = (JsonElement)var6.next();
                JsonObject $$7 = GsonHelper.convertToJsonObject($$6, "stats entry");
                ResourceLocation $$8 = new ResourceLocation(GsonHelper.getAsString($$7, "type"));
                StatType<?> $$9 = (StatType)BuiltInRegistries.STAT_TYPE.get($$8);
                if ($$9 == null) {
                    throw new JsonParseException("Invalid stat type: " + $$8);
                }

                ResourceLocation $$10 = new ResourceLocation(GsonHelper.getAsString($$7, "stat"));
                Stat<?> $$11 = getStat($$9, $$10);
                MinMaxBounds.Ints $$12 = Ints.fromJson($$7.get("value"));
                $$4.put($$11, $$12);
            }
        }

        Object2BooleanMap<ResourceLocation> $$13 = new Object2BooleanOpenHashMap();
        JsonObject $$14 = GsonHelper.getAsJsonObject(p_222492_, "recipes", new JsonObject());
        Iterator var16 = $$14.entrySet().iterator();

        while(var16.hasNext()) {
            Map.Entry<String, JsonElement> $$15 = (Map.Entry)var16.next();
            ResourceLocation $$16 = new ResourceLocation((String)$$15.getKey());
            boolean $$17 = GsonHelper.convertToBoolean((JsonElement)$$15.getValue(), "recipe present");
            $$13.put($$16, $$17);
        }

        Map<ResourceLocation, AdvancementPredicate> $$18 = Maps.newHashMap();
        JsonObject $$19 = GsonHelper.getAsJsonObject(p_222492_, "advancements", new JsonObject());
        Iterator var21 = $$19.entrySet().iterator();

        while(var21.hasNext()) {
            Map.Entry<String, JsonElement> $$20 = (Map.Entry)var21.next();
            ResourceLocation $$21 = new ResourceLocation((String)$$20.getKey());
            AdvancementPredicate $$22 = advancementPredicateFromJson((JsonElement)$$20.getValue());
            $$18.put($$21, $$22);
        }

        EntityPredicate $$23 = EntityPredicate.fromJson(p_222492_.get("looking_at"));
        return new PlayerPredicate($$1, $$3, $$4, $$13, $$18, $$23);
    }

    private static <T> Stat<T> getStat(StatType<T> p_62268_, ResourceLocation p_62269_) {
        Registry<T> $$2 = p_62268_.getRegistry();
        T $$3 = $$2.get(p_62269_);
        if ($$3 == null) {
            throw new JsonParseException("Unknown object " + p_62269_ + " for stat type " + BuiltInRegistries.STAT_TYPE.getKey(p_62268_));
        } else {
            return p_62268_.get($$3);
        }
    }

    private static <T> ResourceLocation getStatValueId(Stat<T> p_62266_) {
        return p_62266_.getType().getRegistry().getKey(p_62266_.getValue());
    }

    public JsonObject serializeCustomData() {
        JsonObject $$0 = new JsonObject();
        $$0.add("level", this.level.serializeToJson());
        if (this.gameType != null) {
            $$0.addProperty("gamemode", this.gameType.getName());
        }

        if (!this.stats.isEmpty()) {
            JsonArray $$1 = new JsonArray();
            this.stats.forEach((p_222489_, p_222490_) -> {
                JsonObject $$3 = new JsonObject();
                $$3.addProperty("type", BuiltInRegistries.STAT_TYPE.getKey(p_222489_.getType()).toString());
                $$3.addProperty("stat", getStatValueId(p_222489_).toString());
                $$3.add("value", p_222490_.serializeToJson());
                $$1.add($$3);
            });
            $$0.add("stats", $$1);
        }

        JsonObject $$3;
        if (!this.recipes.isEmpty()) {
            $$3 = new JsonObject();
            this.recipes.forEach((p_222499_, p_222500_) -> {
                $$3.addProperty(p_222499_.toString(), p_222500_);
            });
            $$0.add("recipes", $$3);
        }

        if (!this.advancements.isEmpty()) {
            $$3 = new JsonObject();
            this.advancements.forEach((p_222495_, p_222496_) -> {
                $$3.add(p_222495_.toString(), p_222496_.toJson());
            });
            $$0.add("advancements", $$3);
        }

        $$0.add("looking_at", this.lookingAt.serializeToJson());
        return $$0;
    }

    public EntitySubPredicate.Type type() {
        return net.minecraft.advancements.critereon.EntitySubPredicate.Types.PLAYER;
    }

    private static class AdvancementDonePredicate implements AdvancementPredicate {
        private final boolean state;

        public AdvancementDonePredicate(boolean p_62301_) {
            this.state = p_62301_;
        }

        public JsonElement toJson() {
            return new JsonPrimitive(this.state);
        }

        public boolean test(AdvancementProgress p_62304_) {
            return p_62304_.isDone() == this.state;
        }
    }

    private static class AdvancementCriterionsPredicate implements AdvancementPredicate {
        private final Object2BooleanMap<String> criterions;

        public AdvancementCriterionsPredicate(Object2BooleanMap<String> p_62293_) {
            this.criterions = p_62293_;
        }

        public JsonElement toJson() {
            JsonObject $$0 = new JsonObject();
            Object2BooleanMap var10000 = this.criterions;
            Objects.requireNonNull($$0);
            var10000.forEach($$0::addProperty);
            return $$0;
        }

        public boolean test(AdvancementProgress p_62296_) {
            ObjectIterator var2 = this.criterions.object2BooleanEntrySet().iterator();

            Object2BooleanMap.Entry $$1;
            CriterionProgress $$2;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                $$1 = (Object2BooleanMap.Entry)var2.next();
                $$2 = p_62296_.getCriterion((String)$$1.getKey());
            } while($$2 != null && $$2.isDone() == $$1.getBooleanValue());

            return false;
        }
    }

    private interface AdvancementPredicate extends Predicate<AdvancementProgress> {
        JsonElement toJson();
    }

    public static class Builder {
        private MinMaxBounds.Ints level;
        @Nullable
        private GameType gameType;
        private final Map<Stat<?>, MinMaxBounds.Ints> stats;
        private final Object2BooleanMap<ResourceLocation> recipes;
        private final Map<ResourceLocation, AdvancementPredicate> advancements;
        private EntityPredicate lookingAt;

        public Builder() {
            this.level = Ints.ANY;
            this.stats = Maps.newHashMap();
            this.recipes = new Object2BooleanOpenHashMap();
            this.advancements = Maps.newHashMap();
            this.lookingAt = EntityPredicate.ANY;
        }

        public static Builder player() {
            return new Builder();
        }

        public Builder setLevel(MinMaxBounds.Ints p_156776_) {
            this.level = p_156776_;
            return this;
        }

        public Builder addStat(Stat<?> p_156769_, MinMaxBounds.Ints p_156770_) {
            this.stats.put(p_156769_, p_156770_);
            return this;
        }

        public Builder addRecipe(ResourceLocation p_156781_, boolean p_156782_) {
            this.recipes.put(p_156781_, p_156782_);
            return this;
        }

        public Builder setGameType(GameType p_156774_) {
            this.gameType = p_156774_;
            return this;
        }

        public Builder setLookingAt(EntityPredicate p_156772_) {
            this.lookingAt = p_156772_;
            return this;
        }

        public Builder checkAdvancementDone(ResourceLocation p_156784_, boolean p_156785_) {
            this.advancements.put(p_156784_, new AdvancementDonePredicate(p_156785_));
            return this;
        }

        public Builder checkAdvancementCriterions(ResourceLocation p_156778_, Map<String, Boolean> p_156779_) {
            this.advancements.put(p_156778_, new AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(p_156779_)));
            return this;
        }

        public PlayerPredicate build() {
            return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
        }
    }
}
