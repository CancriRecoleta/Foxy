//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class DeserializationContext {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation id;
    private final LootDataManager lootData;
    private final Gson predicateGson = Deserializers.createConditionSerializer().create();

    public DeserializationContext(ResourceLocation p_279318_, LootDataManager p_279364_) {
        this.id = p_279318_;
        this.lootData = p_279364_;
    }

    public final LootItemCondition[] deserializeConditions(JsonArray p_25875_, String p_25876_, LootContextParamSet p_25877_) {
        LootItemCondition[] $$3 = (LootItemCondition[])this.predicateGson.fromJson(p_25875_, LootItemCondition[].class);
        ValidationContext $$4 = new ValidationContext(p_25877_, this.lootData);
        LootItemCondition[] var6 = $$3;
        int var7 = $$3.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            LootItemCondition $$5 = var6[var8];
            $$5.validate($$4);
            $$4.getProblems().forEach((p_25880_, p_25881_) -> {
                LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", new Object[]{p_25876_, p_25880_, p_25881_});
            });
        }

        return $$3;
    }

    public ResourceLocation getAdvancementId() {
        return this.id;
    }
}
