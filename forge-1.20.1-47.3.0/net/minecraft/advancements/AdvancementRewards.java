//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandFunction.CacheableFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AdvancementRewards {
    public static final AdvancementRewards EMPTY;
    private final int experience;
    private final ResourceLocation[] loot;
    private final ResourceLocation[] recipes;
    private final CommandFunction.CacheableFunction function;

    public AdvancementRewards(int p_9985_, ResourceLocation[] p_9986_, ResourceLocation[] p_9987_, CommandFunction.CacheableFunction p_9988_) {
        this.experience = p_9985_;
        this.loot = p_9986_;
        this.recipes = p_9987_;
        this.function = p_9988_;
    }

    public ResourceLocation[] getRecipes() {
        return this.recipes;
    }

    public void grant(ServerPlayer p_9990_) {
        p_9990_.giveExperiencePoints(this.experience);
        LootParams lootparams = (new LootParams.Builder(p_9990_.serverLevel())).withParameter(LootContextParams.THIS_ENTITY, p_9990_).withParameter(LootContextParams.ORIGIN, p_9990_.position()).withLuck(p_9990_.getLuck()).create(LootContextParamSets.ADVANCEMENT_REWARD);
        boolean flag = false;
        ResourceLocation[] var4 = this.loot;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            ResourceLocation resourcelocation = var4[var6];
            ObjectListIterator var8 = p_9990_.server.getLootData().getLootTable(resourcelocation).getRandomItems(lootparams).iterator();

            while(var8.hasNext()) {
                ItemStack itemstack = (ItemStack)var8.next();
                if (p_9990_.addItem(itemstack)) {
                    p_9990_.level().playSound((Player)null, p_9990_.getX(), p_9990_.getY(), p_9990_.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((p_9990_.getRandom().nextFloat() - p_9990_.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    flag = true;
                } else {
                    ItemEntity itementity = p_9990_.drop(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickUpDelay();
                        itementity.setTarget(p_9990_.getUUID());
                    }
                }
            }
        }

        if (flag) {
            p_9990_.containerMenu.broadcastChanges();
        }

        if (this.recipes.length > 0) {
            p_9990_.awardRecipesByKey(this.recipes);
        }

        MinecraftServer minecraftserver = p_9990_.server;
        this.function.get(minecraftserver.getFunctions()).ifPresent((p_289236_) -> {
            minecraftserver.getFunctions().execute(p_289236_, p_9990_.createCommandSourceStack().withSuppressedOutput().withPermission(2));
        });
    }

    public String toString() {
        int var10000 = this.experience;
        return "AdvancementRewards{experience=" + var10000 + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + "}";
    }

    public JsonElement serializeToJson() {
        if (this == EMPTY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            if (this.experience != 0) {
                jsonobject.addProperty("experience", this.experience);
            }

            JsonArray jsonarray1;
            ResourceLocation[] var3;
            int var4;
            int var5;
            ResourceLocation resourcelocation1;
            if (this.loot.length > 0) {
                jsonarray1 = new JsonArray();
                var3 = this.loot;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    resourcelocation1 = var3[var5];
                    jsonarray1.add(resourcelocation1.toString());
                }

                jsonobject.add("loot", jsonarray1);
            }

            if (this.recipes.length > 0) {
                jsonarray1 = new JsonArray();
                var3 = this.recipes;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    resourcelocation1 = var3[var5];
                    jsonarray1.add(resourcelocation1.toString());
                }

                jsonobject.add("recipes", jsonarray1);
            }

            if (this.function.getId() != null) {
                jsonobject.addProperty("function", this.function.getId().toString());
            }

            return jsonobject;
        }
    }

    public static AdvancementRewards deserialize(JsonObject p_9992_) throws JsonParseException {
        int i = GsonHelper.getAsInt(p_9992_, "experience", 0);
        JsonArray jsonarray = GsonHelper.getAsJsonArray(p_9992_, "loot", new JsonArray());
        ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

        for(int j = 0; j < aresourcelocation.length; ++j) {
            aresourcelocation[j] = new ResourceLocation(GsonHelper.convertToString(jsonarray.get(j), "loot[" + j + "]"));
        }

        JsonArray jsonarray1 = GsonHelper.getAsJsonArray(p_9992_, "recipes", new JsonArray());
        ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

        for(int k = 0; k < aresourcelocation1.length; ++k) {
            aresourcelocation1[k] = new ResourceLocation(GsonHelper.convertToString(jsonarray1.get(k), "recipes[" + k + "]"));
        }

        CommandFunction.CacheableFunction commandfunction$cacheablefunction;
        if (p_9992_.has("function")) {
            commandfunction$cacheablefunction = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(p_9992_, "function")));
        } else {
            commandfunction$cacheablefunction = CacheableFunction.NONE;
        }

        return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, commandfunction$cacheablefunction);
    }

    static {
        EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CacheableFunction.NONE);
    }

    public static class Builder {
        private int experience;
        private final List<ResourceLocation> loot = Lists.newArrayList();
        private final List<ResourceLocation> recipes = Lists.newArrayList();
        @Nullable
        private ResourceLocation function;

        public Builder() {
        }

        public static Builder experience(int p_10006_) {
            return (new Builder()).addExperience(p_10006_);
        }

        public Builder addExperience(int p_10008_) {
            this.experience += p_10008_;
            return this;
        }

        public static Builder loot(ResourceLocation p_144823_) {
            return (new Builder()).addLootTable(p_144823_);
        }

        public Builder addLootTable(ResourceLocation p_144825_) {
            this.loot.add(p_144825_);
            return this;
        }

        public static Builder recipe(ResourceLocation p_10010_) {
            return (new Builder()).addRecipe(p_10010_);
        }

        public Builder addRecipe(ResourceLocation p_10012_) {
            this.recipes.add(p_10012_);
            return this;
        }

        public static Builder function(ResourceLocation p_144827_) {
            return (new Builder()).runs(p_144827_);
        }

        public Builder runs(ResourceLocation p_144829_) {
            this.function = p_144829_;
            return this;
        }

        public AdvancementRewards build() {
            return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray(new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray(new ResourceLocation[0]), this.function == null ? CacheableFunction.NONE : new CommandFunction.CacheableFunction(this.function));
        }
    }
}
