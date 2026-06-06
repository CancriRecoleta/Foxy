//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT;
    private static final Ingredient GUNPOWDER_INGREDIENT;
    private static final Ingredient STAR_INGREDIENT;

    public FireworkRocketRecipe(ResourceLocation p_250923_, CraftingBookCategory p_250134_) {
        super(p_250923_, p_250134_);
    }

    public boolean matches(CraftingContainer p_43854_, Level p_43855_) {
        boolean $$2 = false;
        int $$3 = 0;

        for(int $$4 = 0; $$4 < p_43854_.getContainerSize(); ++$$4) {
            ItemStack $$5 = p_43854_.getItem($$4);
            if (!$$5.isEmpty()) {
                if (PAPER_INGREDIENT.test($$5)) {
                    if ($$2) {
                        return false;
                    }

                    $$2 = true;
                } else if (GUNPOWDER_INGREDIENT.test($$5)) {
                    ++$$3;
                    if ($$3 > 3) {
                        return false;
                    }
                } else if (!STAR_INGREDIENT.test($$5)) {
                    return false;
                }
            }
        }

        return $$2 && $$3 >= 1;
    }

    public ItemStack assemble(CraftingContainer p_43852_, RegistryAccess p_266791_) {
        ItemStack $$2 = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag $$3 = $$2.getOrCreateTagElement("Fireworks");
        ListTag $$4 = new ListTag();
        int $$5 = 0;

        for(int $$6 = 0; $$6 < p_43852_.getContainerSize(); ++$$6) {
            ItemStack $$7 = p_43852_.getItem($$6);
            if (!$$7.isEmpty()) {
                if (GUNPOWDER_INGREDIENT.test($$7)) {
                    ++$$5;
                } else if (STAR_INGREDIENT.test($$7)) {
                    CompoundTag $$8 = $$7.getTagElement("Explosion");
                    if ($$8 != null) {
                        $$4.add($$8);
                    }
                }
            }
        }

        $$3.putByte("Flight", (byte)$$5);
        if (!$$4.isEmpty()) {
            $$3.put("Explosions", $$4);
        }

        return $$2;
    }

    public boolean canCraftInDimensions(int p_43844_, int p_43845_) {
        return p_43844_ * p_43845_ >= 2;
    }

    public ItemStack getResultItem(RegistryAccess p_267261_) {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }

    static {
        PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
        GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
        STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);
    }
}
