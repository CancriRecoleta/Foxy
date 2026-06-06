//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

public interface IShapedRecipe<T extends Container> extends Recipe<T> {
    int getRecipeWidth();

    int getRecipeHeight();
}
