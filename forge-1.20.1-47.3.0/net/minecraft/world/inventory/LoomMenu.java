//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.inventory;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomMenu extends AbstractContainerMenu {
    private static final int PATTERN_NOT_SET = -1;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int USE_ROW_SLOT_START = 31;
    private static final int USE_ROW_SLOT_END = 40;
    private final ContainerLevelAccess access;
    final DataSlot selectedBannerPatternIndex;
    private List<Holder<BannerPattern>> selectablePatterns;
    Runnable slotUpdateListener;
    final Slot bannerSlot;
    final Slot dyeSlot;
    private final Slot patternSlot;
    private final Slot resultSlot;
    long lastSoundTime;
    private final Container inputContainer;
    private final Container outputContainer;

    public LoomMenu(int p_39856_, Inventory p_39857_) {
        this(p_39856_, p_39857_, ContainerLevelAccess.NULL);
    }

    public LoomMenu(int p_39859_, Inventory p_39860_, final ContainerLevelAccess p_39861_) {
        super(MenuType.LOOM, p_39859_);
        this.selectedBannerPatternIndex = DataSlot.standalone();
        this.selectablePatterns = List.of();
        this.slotUpdateListener = () -> {
        };
        this.inputContainer = new SimpleContainer(3) {
            public void setChanged() {
                super.setChanged();
                LoomMenu.this.slotsChanged(this);
                LoomMenu.this.slotUpdateListener.run();
            }
        };
        this.outputContainer = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                LoomMenu.this.slotUpdateListener.run();
            }
        };
        this.access = p_39861_;
        this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
            public boolean mayPlace(ItemStack p_39918_) {
                return p_39918_.getItem() instanceof BannerItem;
            }
        });
        this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
            public boolean mayPlace(ItemStack p_39927_) {
                return p_39927_.getItem() instanceof DyeItem;
            }
        });
        this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
            public boolean mayPlace(ItemStack p_39936_) {
                return p_39936_.getItem() instanceof BannerPatternItem;
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
            public boolean mayPlace(ItemStack p_39950_) {
                return false;
            }

            public void onTake(Player p_150617_, ItemStack p_150618_) {
                LoomMenu.this.bannerSlot.remove(1);
                LoomMenu.this.dyeSlot.remove(1);
                if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
                    LoomMenu.this.selectedBannerPatternIndex.set(-1);
                }

                p_39861_.execute((p_39952_, p_39953_) -> {
                    long $$2 = p_39952_.getGameTime();
                    if (LoomMenu.this.lastSoundTime != $$2) {
                        p_39952_.playSound((Player)null, (BlockPos)p_39953_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        LoomMenu.this.lastSoundTime = $$2;
                    }

                });
                super.onTake(p_150617_, p_150618_);
            }
        });

        int $$5;
        for($$5 = 0; $$5 < 3; ++$$5) {
            for(int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot(p_39860_, $$4 + $$5 * 9 + 9, 8 + $$4 * 18, 84 + $$5 * 18));
            }
        }

        for($$5 = 0; $$5 < 9; ++$$5) {
            this.addSlot(new Slot(p_39860_, $$5, 8 + $$5 * 18, 142));
        }

        this.addDataSlot(this.selectedBannerPatternIndex);
    }

    public boolean stillValid(Player p_39865_) {
        return stillValid(this.access, p_39865_, Blocks.LOOM);
    }

    public boolean clickMenuButton(Player p_39867_, int p_39868_) {
        if (p_39868_ >= 0 && p_39868_ < this.selectablePatterns.size()) {
            this.selectedBannerPatternIndex.set(p_39868_);
            this.setupResultSlot((Holder)this.selectablePatterns.get(p_39868_));
            return true;
        } else {
            return false;
        }
    }

    private List<Holder<BannerPattern>> getSelectablePatterns(ItemStack p_219994_) {
        if (p_219994_.isEmpty()) {
            return (List)BuiltInRegistries.BANNER_PATTERN.getTag(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse(ImmutableList.of());
        } else {
            Item var3 = p_219994_.getItem();
            if (var3 instanceof BannerPatternItem) {
                BannerPatternItem $$1 = (BannerPatternItem)var3;
                return (List)BuiltInRegistries.BANNER_PATTERN.getTag($$1.getBannerPattern()).map(ImmutableList::copyOf).orElse(ImmutableList.of());
            } else {
                return List.of();
            }
        }
    }

    private boolean isValidPatternIndex(int p_242850_) {
        return p_242850_ >= 0 && p_242850_ < this.selectablePatterns.size();
    }

    public void slotsChanged(Container p_39863_) {
        ItemStack $$1 = this.bannerSlot.getItem();
        ItemStack $$2 = this.dyeSlot.getItem();
        ItemStack $$3 = this.patternSlot.getItem();
        if (!$$1.isEmpty() && !$$2.isEmpty()) {
            int $$4 = this.selectedBannerPatternIndex.get();
            boolean $$5 = this.isValidPatternIndex($$4);
            List<Holder<BannerPattern>> $$6 = this.selectablePatterns;
            this.selectablePatterns = this.getSelectablePatterns($$3);
            Holder $$12;
            if (this.selectablePatterns.size() == 1) {
                this.selectedBannerPatternIndex.set(0);
                $$12 = (Holder)this.selectablePatterns.get(0);
            } else if (!$$5) {
                this.selectedBannerPatternIndex.set(-1);
                $$12 = null;
            } else {
                Holder<BannerPattern> $$9 = (Holder)$$6.get($$4);
                int $$10 = this.selectablePatterns.indexOf($$9);
                if ($$10 != -1) {
                    $$12 = $$9;
                    this.selectedBannerPatternIndex.set($$10);
                } else {
                    $$12 = null;
                    this.selectedBannerPatternIndex.set(-1);
                }
            }

            if ($$12 != null) {
                CompoundTag $$13 = BlockItem.getBlockEntityData($$1);
                boolean $$14 = $$13 != null && $$13.contains("Patterns", 9) && !$$1.isEmpty() && $$13.getList("Patterns", 10).size() >= 6;
                if ($$14) {
                    this.selectedBannerPatternIndex.set(-1);
                    this.resultSlot.set(ItemStack.EMPTY);
                } else {
                    this.setupResultSlot($$12);
                }
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }

            this.broadcastChanges();
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
            this.selectablePatterns = List.of();
            this.selectedBannerPatternIndex.set(-1);
        }
    }

    public List<Holder<BannerPattern>> getSelectablePatterns() {
        return this.selectablePatterns;
    }

    public int getSelectedBannerPatternIndex() {
        return this.selectedBannerPatternIndex.get();
    }

    public void registerUpdateListener(Runnable p_39879_) {
        this.slotUpdateListener = p_39879_;
    }

    public ItemStack quickMoveStack(Player p_39883_, int p_39884_) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get(p_39884_);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if (p_39884_ == this.resultSlot.index) {
                if (!this.moveItemStackTo($$4, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                $$3.onQuickCraft($$4, $$2);
            } else if (p_39884_ != this.dyeSlot.index && p_39884_ != this.bannerSlot.index && p_39884_ != this.patternSlot.index) {
                if ($$4.getItem() instanceof BannerItem) {
                    if (!this.moveItemStackTo($$4, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if ($$4.getItem() instanceof DyeItem) {
                    if (!this.moveItemStackTo($$4, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if ($$4.getItem() instanceof BannerPatternItem) {
                    if (!this.moveItemStackTo($$4, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_39884_ >= 4 && p_39884_ < 31) {
                    if (!this.moveItemStackTo($$4, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_39884_ >= 31 && p_39884_ < 40 && !this.moveItemStackTo($$4, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo($$4, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if ($$4.isEmpty()) {
                $$3.setByPlayer(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }

            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }

            $$3.onTake(p_39883_, $$4);
        }

        return $$2;
    }

    public void removed(Player p_39881_) {
        super.removed(p_39881_);
        this.access.execute((p_39871_, p_39872_) -> {
            this.clearContainer(p_39881_, this.inputContainer);
        });
    }

    private void setupResultSlot(Holder<BannerPattern> p_219992_) {
        ItemStack $$1 = this.bannerSlot.getItem();
        ItemStack $$2 = this.dyeSlot.getItem();
        ItemStack $$3 = ItemStack.EMPTY;
        if (!$$1.isEmpty() && !$$2.isEmpty()) {
            $$3 = $$1.copyWithCount(1);
            DyeColor $$4 = ((DyeItem)$$2.getItem()).getDyeColor();
            CompoundTag $$5 = BlockItem.getBlockEntityData($$3);
            ListTag $$7;
            if ($$5 != null && $$5.contains("Patterns", 9)) {
                $$7 = $$5.getList("Patterns", 10);
            } else {
                $$7 = new ListTag();
                if ($$5 == null) {
                    $$5 = new CompoundTag();
                }

                $$5.put("Patterns", $$7);
            }

            CompoundTag $$8 = new CompoundTag();
            $$8.putString("Pattern", ((BannerPattern)p_219992_.value()).getHashname());
            $$8.putInt("Color", $$4.getId());
            $$7.add($$8);
            BlockItem.setBlockEntityData($$3, BlockEntityType.BANNER, $$5);
        }

        if (!ItemStack.matches($$3, this.resultSlot.getItem())) {
            this.resultSlot.set($$3);
        }

    }

    public Slot getBannerSlot() {
        return this.bannerSlot;
    }

    public Slot getDyeSlot() {
        return this.dyeSlot;
    }

    public Slot getPatternSlot() {
        return this.patternSlot;
    }

    public Slot getResultSlot() {
        return this.resultSlot;
    }
}
