//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import java.util.Locale;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraftforge.common.IExtensibleEnum;

public enum RecipeBookType implements IExtensibleEnum {
    CRAFTING,
    FURNACE,
    BLAST_FURNACE,
    SMOKER;

    private RecipeBookType() {
    }

    public static RecipeBookType create(String name) {
        throw new IllegalStateException("Enum not extended!");
    }

    public void init() {
        String name = this.name().toLowerCase(Locale.ROOT).replace("_", "");
        RecipeBookSettings.addTagsForType(this, "is" + name + "GuiOpen", "is" + name + "FilteringCraftable");
    }
}
