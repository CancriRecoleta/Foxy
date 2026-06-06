//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.color.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class ItemColors {
    private static final int DEFAULT = -1;
    private final Map<Holder.Reference<Item>, ItemColor> itemColors = new HashMap();

    public ItemColors() {
    }

    public static ItemColors createDefault(BlockColors p_92684_) {
        ItemColors itemcolors = new ItemColors();
        itemcolors.register((p_92708_, p_92709_) -> {
            return p_92709_ > 0 ? -1 : ((DyeableLeatherItem)p_92708_.getItem()).getColor(p_92708_);
        }, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR);
        itemcolors.register((p_92705_, p_92706_) -> {
            return GrassColor.get(0.5, 1.0);
        }, Blocks.TALL_GRASS, Blocks.LARGE_FERN);
        itemcolors.register((p_92702_, p_92703_) -> {
            if (p_92703_ != 1) {
                return -1;
            } else {
                CompoundTag compoundtag = p_92702_.getTagElement("Explosion");
                int[] aint = compoundtag != null && compoundtag.contains("Colors", 11) ? compoundtag.getIntArray("Colors") : null;
                if (aint != null && aint.length != 0) {
                    if (aint.length == 1) {
                        return aint[0];
                    } else {
                        int i = 0;
                        int j = 0;
                        int k = 0;
                        int[] var7 = aint;
                        int var8 = aint.length;

                        for(int var9 = 0; var9 < var8; ++var9) {
                            int l = var7[var9];
                            i += (l & 16711680) >> 16;
                            j += (l & '\uff00') >> 8;
                            k += (l & 255) >> 0;
                        }

                        i /= aint.length;
                        j /= aint.length;
                        k /= aint.length;
                        return i << 16 | j << 8 | k;
                    }
                } else {
                    return 9079434;
                }
            }
        }, Items.FIREWORK_STAR);
        itemcolors.register((p_92699_, p_92700_) -> {
            return p_92700_ > 0 ? -1 : PotionUtils.getColor(p_92699_);
        }, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        Iterator var2 = SpawnEggItem.eggs().iterator();

        while(var2.hasNext()) {
            SpawnEggItem spawneggitem = (SpawnEggItem)var2.next();
            itemcolors.register((p_92681_, p_92682_) -> {
                return spawneggitem.getColor(p_92682_);
            }, spawneggitem);
        }

        itemcolors.register((p_92687_, p_92688_) -> {
            BlockState blockstate = ((BlockItem)p_92687_.getItem()).getBlock().defaultBlockState();
            return p_92684_.getColor(blockstate, (BlockAndTintGetter)null, (BlockPos)null, p_92688_);
        }, Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD);
        itemcolors.register((p_92696_, p_92697_) -> {
            return FoliageColor.getMangroveColor();
        }, Blocks.MANGROVE_LEAVES);
        itemcolors.register((p_92693_, p_92694_) -> {
            return p_92694_ == 0 ? PotionUtils.getColor(p_92693_) : -1;
        }, Items.TIPPED_ARROW);
        itemcolors.register((p_232352_, p_232353_) -> {
            return p_232353_ == 0 ? -1 : MapItem.getColor(p_232352_);
        }, Items.FILLED_MAP);
        ForgeHooksClient.onItemColorsInit(itemcolors, p_92684_);
        return itemcolors;
    }

    public int getColor(ItemStack p_92677_, int p_92678_) {
        ItemColor itemcolor = (ItemColor)this.itemColors.get(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)p_92677_.getItem()));
        return itemcolor == null ? -1 : itemcolor.getColor(p_92677_, p_92678_);
    }

    /** @deprecated */
    @Deprecated
    public void register(ItemColor p_92690_, ItemLike... p_92691_) {
        ItemLike[] var3 = p_92691_;
        int var4 = p_92691_.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ItemLike itemlike = var3[var5];
            this.itemColors.put(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)itemlike.asItem()), p_92690_);
        }

    }
}
