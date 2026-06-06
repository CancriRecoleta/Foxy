//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fml.ModList;

public class ModLoadedCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "mod_loaded");
    private final String modid;

    public ModLoadedCondition(String modid) {
        this.modid = modid;
    }

    public ResourceLocation getID() {
        return NAME;
    }

    public boolean test(ICondition.IContext context) {
        return ModList.get().isLoaded(this.modid);
    }

    public String toString() {
        return "mod_loaded(\"" + this.modid + "\")";
    }

    public static class Serializer implements IConditionSerializer<ModLoadedCondition> {
        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
        }

        public void write(JsonObject json, ModLoadedCondition value) {
            json.addProperty("modid", value.modid);
        }

        public ModLoadedCondition read(JsonObject json) {
            return new ModLoadedCondition(GsonHelper.getAsString(json, "modid"));
        }

        public ResourceLocation getID() {
            return ModLoadedCondition.NAME;
        }
    }
}
