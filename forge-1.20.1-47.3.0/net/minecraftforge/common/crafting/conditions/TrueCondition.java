//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public final class TrueCondition implements ICondition {
    public static final TrueCondition INSTANCE = new TrueCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "true");

    private TrueCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    public boolean test(ICondition.IContext context) {
        return true;
    }

    public String toString() {
        return "true";
    }

    public static class Serializer implements IConditionSerializer<TrueCondition> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public void write(JsonObject json, TrueCondition value) {
        }

        public TrueCondition read(JsonObject json) {
            return TrueCondition.INSTANCE;
        }

        public ResourceLocation getID() {
            return TrueCondition.NAME;
        }
    }
}
