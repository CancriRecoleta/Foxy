//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem extends Item {
    public static final int TITLE_LENGTH = 16;
    public static final int TITLE_MAX_LENGTH = 32;
    public static final int PAGE_EDIT_LENGTH = 1024;
    public static final int PAGE_LENGTH = 32767;
    public static final int MAX_PAGES = 100;
    public static final int MAX_GENERATION = 2;
    public static final String TAG_TITLE = "title";
    public static final String TAG_FILTERED_TITLE = "filtered_title";
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_PAGES = "pages";
    public static final String TAG_FILTERED_PAGES = "filtered_pages";
    public static final String TAG_GENERATION = "generation";
    public static final String TAG_RESOLVED = "resolved";

    public WrittenBookItem(Item.Properties p_43455_) {
        super(p_43455_);
    }

    public static boolean makeSureTagIsValid(@Nullable CompoundTag p_43472_) {
        if (!WritableBookItem.makeSureTagIsValid(p_43472_)) {
            return false;
        } else if (!p_43472_.contains("title", 8)) {
            return false;
        } else {
            String $$1 = p_43472_.getString("title");
            return $$1.length() > 32 ? false : p_43472_.contains("author", 8);
        }
    }

    public static int getGeneration(ItemStack p_43474_) {
        return p_43474_.getTag().getInt("generation");
    }

    public static int getPageCount(ItemStack p_43478_) {
        CompoundTag $$1 = p_43478_.getTag();
        return $$1 != null ? $$1.getList("pages", 8).size() : 0;
    }

    public Component getName(ItemStack p_43480_) {
        CompoundTag $$1 = p_43480_.getTag();
        if ($$1 != null) {
            String $$2 = $$1.getString("title");
            if (!StringUtil.isNullOrEmpty($$2)) {
                return Component.literal($$2);
            }
        }

        return super.getName(p_43480_);
    }

    public void appendHoverText(ItemStack p_43457_, @Nullable Level p_43458_, List<Component> p_43459_, TooltipFlag p_43460_) {
        if (p_43457_.hasTag()) {
            CompoundTag $$4 = p_43457_.getTag();
            String $$5 = $$4.getString("author");
            if (!StringUtil.isNullOrEmpty($$5)) {
                p_43459_.add(Component.translatable("book.byAuthor", $$5).withStyle(ChatFormatting.GRAY));
            }

            p_43459_.add(Component.translatable("book.generation." + $$4.getInt("generation")).withStyle(ChatFormatting.GRAY));
        }

    }

    public InteractionResult useOn(UseOnContext p_43466_) {
        Level $$1 = p_43466_.getLevel();
        BlockPos $$2 = p_43466_.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$3.is(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook(p_43466_.getPlayer(), $$1, $$2, $$3, p_43466_.getItemInHand()) ? InteractionResult.sidedSuccess($$1.isClientSide) : InteractionResult.PASS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level p_43468_, Player p_43469_, InteractionHand p_43470_) {
        ItemStack $$3 = p_43469_.getItemInHand(p_43470_);
        p_43469_.openItemGui($$3, p_43470_);
        p_43469_.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess($$3, p_43468_.isClientSide());
    }

    public static boolean resolveBookComponents(ItemStack p_43462_, @Nullable CommandSourceStack p_43463_, @Nullable Player p_43464_) {
        CompoundTag $$3 = p_43462_.getTag();
        if ($$3 != null && !$$3.getBoolean("resolved")) {
            $$3.putBoolean("resolved", true);
            if (!makeSureTagIsValid($$3)) {
                return false;
            } else {
                ListTag $$4 = $$3.getList("pages", 8);
                ListTag $$5 = new ListTag();

                for(int $$6 = 0; $$6 < $$4.size(); ++$$6) {
                    String $$7 = resolvePage(p_43463_, p_43464_, $$4.getString($$6));
                    if ($$7.length() > 32767) {
                        return false;
                    }

                    $$5.add($$6, (Tag)StringTag.valueOf($$7));
                }

                if ($$3.contains("filtered_pages", 10)) {
                    CompoundTag $$8 = $$3.getCompound("filtered_pages");
                    CompoundTag $$9 = new CompoundTag();
                    Iterator var8 = $$8.getAllKeys().iterator();

                    while(var8.hasNext()) {
                        String $$10 = (String)var8.next();
                        String $$11 = resolvePage(p_43463_, p_43464_, $$8.getString($$10));
                        if ($$11.length() > 32767) {
                            return false;
                        }

                        $$9.putString($$10, $$11);
                    }

                    $$3.put("filtered_pages", $$9);
                }

                $$3.put("pages", $$5);
                return true;
            }
        } else {
            return false;
        }
    }

    private static String resolvePage(@Nullable CommandSourceStack p_151249_, @Nullable Player p_151250_, String p_151251_) {
        MutableComponent $$5;
        try {
            $$5 = Serializer.fromJsonLenient(p_151251_);
            $$5 = ComponentUtils.updateForEntity(p_151249_, (Component)$$5, p_151250_, 0);
        } catch (Exception var5) {
            $$5 = Component.literal(p_151251_);
        }

        return Serializer.toJson($$5);
    }

    public boolean isFoil(ItemStack p_43476_) {
        return true;
    }
}
