//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class WeatherCheck implements LootItemCondition {
    @Nullable
    final Boolean isRaining;
    @Nullable
    final Boolean isThundering;

    WeatherCheck(@Nullable Boolean p_82059_, @Nullable Boolean p_82060_) {
        this.isRaining = p_82059_;
        this.isThundering = p_82060_;
    }

    public LootItemConditionType getType() {
        return LootItemConditions.WEATHER_CHECK;
    }

    public boolean test(LootContext p_82066_) {
        ServerLevel $$1 = p_82066_.getLevel();
        if (this.isRaining != null && this.isRaining != $$1.isRaining()) {
            return false;
        } else {
            return this.isThundering == null || this.isThundering == $$1.isThundering();
        }
    }

    public static Builder weather() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {
        @Nullable
        private Boolean isRaining;
        @Nullable
        private Boolean isThundering;

        public Builder() {
        }

        public Builder setRaining(@Nullable Boolean p_165557_) {
            this.isRaining = p_165557_;
            return this;
        }

        public Builder setThundering(@Nullable Boolean p_165560_) {
            this.isThundering = p_165560_;
            return this;
        }

        public WeatherCheck build() {
            return new WeatherCheck(this.isRaining, this.isThundering);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<WeatherCheck> {
        public Serializer() {
        }

        public void serialize(JsonObject p_82079_, WeatherCheck p_82080_, JsonSerializationContext p_82081_) {
            p_82079_.addProperty("raining", p_82080_.isRaining);
            p_82079_.addProperty("thundering", p_82080_.isThundering);
        }

        public WeatherCheck deserialize(JsonObject p_82087_, JsonDeserializationContext p_82088_) {
            Boolean $$2 = p_82087_.has("raining") ? GsonHelper.getAsBoolean(p_82087_, "raining") : null;
            Boolean $$3 = p_82087_.has("thundering") ? GsonHelper.getAsBoolean(p_82087_, "thundering") : null;
            return new WeatherCheck($$2, $$3);
        }
    }
}
