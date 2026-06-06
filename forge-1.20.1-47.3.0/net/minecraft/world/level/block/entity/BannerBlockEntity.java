//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerBlockEntity extends BlockEntity implements Nameable {
    public static final int MAX_PATTERNS = 6;
    public static final String TAG_PATTERNS = "Patterns";
    public static final String TAG_PATTERN = "Pattern";
    public static final String TAG_COLOR = "Color";
    @Nullable
    private Component name;
    private DyeColor baseColor;
    @Nullable
    private ListTag itemPatterns;
    @Nullable
    private List<Pair<Holder<BannerPattern>, DyeColor>> patterns;

    public BannerBlockEntity(BlockPos p_155035_, BlockState p_155036_) {
        super(BlockEntityType.BANNER, p_155035_, p_155036_);
        this.baseColor = ((AbstractBannerBlock)p_155036_.getBlock()).getColor();
    }

    public BannerBlockEntity(BlockPos p_155038_, BlockState p_155039_, DyeColor p_155040_) {
        this(p_155038_, p_155039_);
        this.baseColor = p_155040_;
    }

    @Nullable
    public static ListTag getItemPatterns(ItemStack p_58488_) {
        ListTag $$1 = null;
        CompoundTag $$2 = BlockItem.getBlockEntityData(p_58488_);
        if ($$2 != null && $$2.contains("Patterns", 9)) {
            $$1 = $$2.getList("Patterns", 10).copy();
        }

        return $$1;
    }

    public void fromItem(ItemStack p_58490_, DyeColor p_58491_) {
        this.baseColor = p_58491_;
        this.fromItem(p_58490_);
    }

    public void fromItem(ItemStack p_187454_) {
        this.itemPatterns = getItemPatterns(p_187454_);
        this.patterns = null;
        this.name = p_187454_.hasCustomHoverName() ? p_187454_.getHoverName() : null;
    }

    public Component getName() {
        return (Component)(this.name != null ? this.name : Component.translatable("block.minecraft.banner"));
    }

    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    public void setCustomName(Component p_58502_) {
        this.name = p_58502_;
    }

    protected void saveAdditional(CompoundTag p_187456_) {
        super.saveAdditional(p_187456_);
        if (this.itemPatterns != null) {
            p_187456_.put("Patterns", this.itemPatterns);
        }

        if (this.name != null) {
            p_187456_.putString("CustomName", Serializer.toJson(this.name));
        }

    }

    public void load(CompoundTag p_155042_) {
        super.load(p_155042_);
        if (p_155042_.contains("CustomName", 8)) {
            this.name = Serializer.fromJson(p_155042_.getString("CustomName"));
        }

        this.itemPatterns = p_155042_.getList("Patterns", 10);
        this.patterns = null;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static int getPatternCount(ItemStack p_58505_) {
        CompoundTag $$1 = BlockItem.getBlockEntityData(p_58505_);
        return $$1 != null && $$1.contains("Patterns") ? $$1.getList("Patterns", 10).size() : 0;
    }

    public List<Pair<Holder<BannerPattern>, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = createPatterns(this.baseColor, this.itemPatterns);
        }

        return this.patterns;
    }

    public static List<Pair<Holder<BannerPattern>, DyeColor>> createPatterns(DyeColor p_58485_, @Nullable ListTag p_58486_) {
        List<Pair<Holder<BannerPattern>, DyeColor>> $$2 = Lists.newArrayList();
        $$2.add(Pair.of(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(BannerPatterns.BASE), p_58485_));
        if (p_58486_ != null) {
            for(int $$3 = 0; $$3 < p_58486_.size(); ++$$3) {
                CompoundTag $$4 = p_58486_.getCompound($$3);
                Holder<BannerPattern> $$5 = BannerPattern.byHash($$4.getString("Pattern"));
                if ($$5 != null) {
                    int $$6 = $$4.getInt("Color");
                    $$2.add(Pair.of($$5, DyeColor.byId($$6)));
                }
            }
        }

        return $$2;
    }

    public static void removeLastPattern(ItemStack p_58510_) {
        CompoundTag $$1 = BlockItem.getBlockEntityData(p_58510_);
        if ($$1 != null && $$1.contains("Patterns", 9)) {
            ListTag $$2 = $$1.getList("Patterns", 10);
            if (!$$2.isEmpty()) {
                $$2.remove($$2.size() - 1);
                if ($$2.isEmpty()) {
                    $$1.remove("Patterns");
                }

                BlockItem.setBlockEntityData(p_58510_, BlockEntityType.BANNER, $$1);
            }
        }
    }

    public ItemStack getItem() {
        ItemStack $$0 = new ItemStack(BannerBlock.byColor(this.baseColor));
        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            CompoundTag $$1 = new CompoundTag();
            $$1.put("Patterns", this.itemPatterns.copy());
            BlockItem.setBlockEntityData($$0, this.getType(), $$1);
        }

        if (this.name != null) {
            $$0.setHoverName(this.name);
        }

        return $$0;
    }

    public DyeColor getBaseColor() {
        return this.baseColor;
    }
}
