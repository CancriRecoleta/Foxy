//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public final class FalseCondition implements ICondition {
    public static final FalseCondition INSTANCE = new FalseCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "false");

    private FalseCondition() {
    }

    public ResourceLocation getID() {
        return NAME;
    }

    public boolean test(ICondition.IContext condition) {
        return false;
    }

    public String toString() {
        return "false";
    }

    public static class Serializer implements IConditionSerializer<FalseCondition> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public void write(JsonObject json, FalseCondition value) {
        }

        public FalseCondition read(JsonObject json) {
            return FalseCondition.INSTANCE;
        }

        public ResourceLocation getID() {
            return FalseCondition.NAME;
        }
    }
}
