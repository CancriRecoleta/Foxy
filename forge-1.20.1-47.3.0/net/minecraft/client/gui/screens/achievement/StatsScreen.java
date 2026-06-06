//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen implements StatsUpdateListener {
    private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
    private static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
    protected final Screen lastScreen;
    private GeneralStatisticsList statsList;
    ItemStatisticsList itemStatsList;
    private MobsStatisticsList mobsStatsList;
    final StatsCounter stats;
    @Nullable
    private ObjectSelectionList<?> activeList;
    private boolean isLoading = true;
    private static final int SLOT_TEX_SIZE = 128;
    private static final int SLOT_BG_SIZE = 18;
    private static final int SLOT_STAT_HEIGHT = 20;
    private static final int SLOT_BG_X = 1;
    private static final int SLOT_BG_Y = 1;
    private static final int SLOT_FG_X = 2;
    private static final int SLOT_FG_Y = 2;
    private static final int SLOT_LEFT_INSERT = 40;
    private static final int SLOT_TEXT_OFFSET = 5;
    private static final int SORT_NONE = 0;
    private static final int SORT_DOWN = -1;
    private static final int SORT_UP = 1;

    public StatsScreen(Screen p_96906_, StatsCounter p_96907_) {
        super(Component.translatable("gui.stats"));
        this.lastScreen = p_96906_;
        this.stats = p_96907_;
    }

    protected void init() {
        this.isLoading = true;
        this.minecraft.getConnection().send((Packet)(new ServerboundClientCommandPacket(Action.REQUEST_STATS)));
    }

    public void initLists() {
        this.statsList = new GeneralStatisticsList(this.minecraft);
        this.itemStatsList = new ItemStatisticsList(this.minecraft);
        this.mobsStatsList = new MobsStatisticsList(this.minecraft);
    }

    public void initButtons() {
        this.addRenderableWidget(Button.builder(Component.translatable("stat.generalButton"), (p_96963_) -> {
            this.setActiveList(this.statsList);
        }).bounds(this.width / 2 - 120, this.height - 52, 80, 20).build());
        Button $$0 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("stat.itemsButton"), (p_96959_) -> {
            this.setActiveList(this.itemStatsList);
        }).bounds(this.width / 2 - 40, this.height - 52, 80, 20).build());
        Button $$1 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("stat.mobsButton"), (p_96949_) -> {
            this.setActiveList(this.mobsStatsList);
        }).bounds(this.width / 2 + 40, this.height - 52, 80, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280843_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
        if (this.itemStatsList.children().isEmpty()) {
            $$0.active = false;
        }

        if (this.mobsStatsList.children().isEmpty()) {
            $$1.active = false;
        }

    }

    public void render(GuiGraphics p_281866_, int p_96914_, int p_96915_, float p_96916_) {
        if (this.isLoading) {
            this.renderBackground(p_281866_);
            p_281866_.drawCenteredString(this.font, PENDING_TEXT, this.width / 2, this.height / 2, 16777215);
            Font var10001 = this.font;
            String var10002 = LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)];
            int var10003 = this.width / 2;
            int var10004 = this.height / 2;
            Objects.requireNonNull(this.font);
            p_281866_.drawCenteredString(var10001, var10002, var10003, var10004 + 9 * 2, 16777215);
        } else {
            this.getActiveList().render(p_281866_, p_96914_, p_96915_, p_96916_);
            p_281866_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 20, 16777215);
            super.render(p_281866_, p_96914_, p_96915_, p_96916_);
        }

    }

    public void onStatsUpdated() {
        if (this.isLoading) {
            this.initLists();
            this.initButtons();
            this.setActiveList(this.statsList);
            this.isLoading = false;
        }

    }

    public boolean isPauseScreen() {
        return !this.isLoading;
    }

    @Nullable
    public ObjectSelectionList<?> getActiveList() {
        return this.activeList;
    }

    public void setActiveList(@Nullable ObjectSelectionList<?> p_96925_) {
        if (this.activeList != null) {
            this.removeWidget(this.activeList);
        }

        if (p_96925_ != null) {
            this.addWidget(p_96925_);
            this.activeList = p_96925_;
        }

    }

    static String getTranslationKey(Stat<ResourceLocation> p_96947_) {
        String var10000 = ((ResourceLocation)p_96947_.getValue()).toString();
        return "stat." + var10000.replace(':', '.');
    }

    int getColumnX(int p_96909_) {
        return 115 + 40 * p_96909_;
    }

    void blitSlot(GuiGraphics p_282402_, int p_283228_, int p_283232_, Item p_282368_) {
        this.blitSlotIcon(p_282402_, p_283228_ + 1, p_283232_ + 1, 0, 0);
        p_282402_.renderFakeItem(p_282368_.getDefaultInstance(), p_283228_ + 2, p_283232_ + 2);
    }

    void blitSlotIcon(GuiGraphics p_281402_, int p_283145_, int p_283100_, int p_282128_, int p_281483_) {
        p_281402_.blit(STATS_ICON_LOCATION, p_283145_, p_283100_, 0, (float)p_282128_, (float)p_281483_, 18, 18, 128, 128);
    }

    @OnlyIn(Dist.CLIENT)
    private class GeneralStatisticsList extends ObjectSelectionList<Entry> {
        public GeneralStatisticsList(Minecraft p_96995_) {
            super(p_96995_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            ObjectArrayList<Stat<ResourceLocation>> $$1 = new ObjectArrayList(Stats.CUSTOM.iterator());
            $$1.sort(Comparator.comparing((p_96997_) -> {
                return I18n.get(StatsScreen.getTranslationKey(p_96997_));
            }));
            ObjectListIterator var4 = $$1.iterator();

            while(var4.hasNext()) {
                Stat<ResourceLocation> $$2 = (Stat)var4.next();
                this.addEntry(new Entry($$2));
            }

        }

        protected void renderBackground(GuiGraphics p_282785_) {
            StatsScreen.this.renderBackground(p_282785_);
        }

        @OnlyIn(Dist.CLIENT)
        private class Entry extends ObjectSelectionList.Entry<Entry> {
            private final Stat<ResourceLocation> stat;
            private final Component statDisplay;

            Entry(Stat<ResourceLocation> p_97005_) {
                this.stat = p_97005_;
                this.statDisplay = Component.translatable(StatsScreen.getTranslationKey(p_97005_));
            }

            private String getValueText() {
                return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            }

            public void render(GuiGraphics p_283043_, int p_97012_, int p_97013_, int p_97014_, int p_97015_, int p_97016_, int p_97017_, int p_97018_, boolean p_97019_, float p_97020_) {
                p_283043_.drawString(StatsScreen.this.font, this.statDisplay, p_97014_ + 2, p_97013_ + 1, p_97012_ % 2 == 0 ? 16777215 : 9474192);
                String $$10 = this.getValueText();
                p_283043_.drawString(StatsScreen.this.font, $$10, p_97014_ + 2 + 213 - StatsScreen.this.font.width($$10), p_97013_ + 1, p_97012_ % 2 == 0 ? 16777215 : 9474192);
            }

            public Component getNarration() {
                return Component.translatable("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ItemStatisticsList extends ObjectSelectionList<ItemRow> {
        protected final List<StatType<Block>> blockColumns = Lists.newArrayList();
        protected final List<StatType<Item>> itemColumns;
        private final int[] iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
        protected int headerPressed = -1;
        protected final Comparator<ItemRow> itemStatSorter = new ItemRowComparator();
        @Nullable
        protected StatType<?> sortColumn;
        protected int sortOrder;

        public ItemStatisticsList(Minecraft p_97032_) {
            super(p_97032_, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.blockColumns.add(Stats.BLOCK_MINED);
            this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
            this.setRenderHeader(true, 20);
            Set<Item> $$1 = Sets.newIdentityHashSet();
            Iterator var4 = BuiltInRegistries.ITEM.iterator();

            Item $$8;
            boolean $$6;
            Iterator var7;
            StatType $$7;
            while(var4.hasNext()) {
                $$8 = (Item)var4.next();
                $$6 = false;
                var7 = this.itemColumns.iterator();

                while(var7.hasNext()) {
                    $$7 = (StatType)var7.next();
                    if ($$7.contains($$8) && StatsScreen.this.stats.getValue($$7.get($$8)) > 0) {
                        $$6 = true;
                    }
                }

                if ($$6) {
                    $$1.add($$8);
                }
            }

            var4 = BuiltInRegistries.BLOCK.iterator();

            while(var4.hasNext()) {
                Block $$5 = (Block)var4.next();
                $$6 = false;
                var7 = this.blockColumns.iterator();

                while(var7.hasNext()) {
                    $$7 = (StatType)var7.next();
                    if ($$7.contains($$5) && StatsScreen.this.stats.getValue($$7.get($$5)) > 0) {
                        $$6 = true;
                    }
                }

                if ($$6) {
                    $$1.add($$5.asItem());
                }
            }

            $$1.remove(Items.AIR);
            var4 = $$1.iterator();

            while(var4.hasNext()) {
                $$8 = (Item)var4.next();
                this.addEntry(new ItemRow($$8));
            }

        }

        protected void renderHeader(GuiGraphics p_282214_, int p_97050_, int p_97051_) {
            if (!this.minecraft.mouseHandler.isLeftPressed()) {
                this.headerPressed = -1;
            }

            int $$6;
            for($$6 = 0; $$6 < this.iconOffsets.length; ++$$6) {
                StatsScreen.this.blitSlotIcon(p_282214_, p_97050_ + StatsScreen.this.getColumnX($$6) - 18, p_97051_ + 1, 0, this.headerPressed == $$6 ? 0 : 18);
            }

            int $$7;
            if (this.sortColumn != null) {
                $$6 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
                $$7 = this.sortOrder == 1 ? 2 : 1;
                StatsScreen.this.blitSlotIcon(p_282214_, p_97050_ + $$6, p_97051_ + 1, 18 * $$7, 0);
            }

            for($$6 = 0; $$6 < this.iconOffsets.length; ++$$6) {
                $$7 = this.headerPressed == $$6 ? 1 : 0;
                StatsScreen.this.blitSlotIcon(p_282214_, p_97050_ + StatsScreen.this.getColumnX($$6) - 18 + $$7, p_97051_ + 1 + $$7, 18 * this.iconOffsets[$$6], 18);
            }

        }

        public int getRowWidth() {
            return 375;
        }

        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }

        protected void renderBackground(GuiGraphics p_281850_) {
            StatsScreen.this.renderBackground(p_281850_);
        }

        protected void clickedHeader(int p_97036_, int p_97037_) {
            this.headerPressed = -1;

            for(int $$2 = 0; $$2 < this.iconOffsets.length; ++$$2) {
                int $$3 = p_97036_ - StatsScreen.this.getColumnX($$2);
                if ($$3 >= -36 && $$3 <= 0) {
                    this.headerPressed = $$2;
                    break;
                }
            }

            if (this.headerPressed >= 0) {
                this.sortByColumn(this.getColumn(this.headerPressed));
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

        }

        private StatType<?> getColumn(int p_97034_) {
            return p_97034_ < this.blockColumns.size() ? (StatType)this.blockColumns.get(p_97034_) : (StatType)this.itemColumns.get(p_97034_ - this.blockColumns.size());
        }

        private int getColumnIndex(StatType<?> p_97059_) {
            int $$1 = this.blockColumns.indexOf(p_97059_);
            if ($$1 >= 0) {
                return $$1;
            } else {
                int $$2 = this.itemColumns.indexOf(p_97059_);
                return $$2 >= 0 ? $$2 + this.blockColumns.size() : -1;
            }
        }

        protected void renderDecorations(GuiGraphics p_283203_, int p_97046_, int p_97047_) {
            if (p_97047_ >= this.y0 && p_97047_ <= this.y1) {
                ItemRow $$3 = (ItemRow)this.getHovered();
                int $$4 = (this.width - this.getRowWidth()) / 2;
                if ($$3 != null) {
                    if (p_97046_ < $$4 + 40 || p_97046_ > $$4 + 40 + 20) {
                        return;
                    }

                    Item $$5 = $$3.getItem();
                    this.renderMousehoverTooltip(p_283203_, this.getString($$5), p_97046_, p_97047_);
                } else {
                    Component $$6 = null;
                    int $$7 = p_97046_ - $$4;

                    for(int $$8 = 0; $$8 < this.iconOffsets.length; ++$$8) {
                        int $$9 = StatsScreen.this.getColumnX($$8);
                        if ($$7 >= $$9 - 18 && $$7 <= $$9) {
                            $$6 = this.getColumn($$8).getDisplayName();
                            break;
                        }
                    }

                    this.renderMousehoverTooltip(p_283203_, $$6, p_97046_, p_97047_);
                }

            }
        }

        protected void renderMousehoverTooltip(GuiGraphics p_283023_, @Nullable Component p_282505_, int p_282229_, int p_282222_) {
            if (p_282505_ != null) {
                int $$4 = p_282229_ + 12;
                int $$5 = p_282222_ - 12;
                int $$6 = StatsScreen.this.font.width((FormattedText)p_282505_);
                p_283023_.fillGradient($$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
                p_283023_.pose().pushPose();
                p_283023_.pose().translate(0.0F, 0.0F, 400.0F);
                p_283023_.drawString(StatsScreen.this.font, (Component)p_282505_, $$4, $$5, -1);
                p_283023_.pose().popPose();
            }
        }

        protected Component getString(Item p_97041_) {
            return p_97041_.getDescription();
        }

        protected void sortByColumn(StatType<?> p_97039_) {
            if (p_97039_ != this.sortColumn) {
                this.sortColumn = p_97039_;
                this.sortOrder = -1;
            } else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            } else {
                this.sortColumn = null;
                this.sortOrder = 0;
            }

            this.children().sort(this.itemStatSorter);
        }

        @OnlyIn(Dist.CLIENT)
        private class ItemRowComparator implements Comparator<ItemRow> {
            ItemRowComparator() {
            }

            public int compare(ItemRow p_169524_, ItemRow p_169525_) {
                Item $$2 = p_169524_.getItem();
                Item $$3 = p_169525_.getItem();
                int $$7;
                int $$11;
                if (ItemStatisticsList.this.sortColumn == null) {
                    $$7 = 0;
                    $$11 = 0;
                } else {
                    StatType $$6;
                    if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                        $$6 = ItemStatisticsList.this.sortColumn;
                        $$7 = $$2 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$2).getBlock()) : -1;
                        $$11 = $$3 instanceof BlockItem ? StatsScreen.this.stats.getValue($$6, ((BlockItem)$$3).getBlock()) : -1;
                    } else {
                        $$6 = ItemStatisticsList.this.sortColumn;
                        $$7 = StatsScreen.this.stats.getValue($$6, $$2);
                        $$11 = StatsScreen.this.stats.getValue($$6, $$3);
                    }
                }

                return $$7 == $$11 ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId($$2), Item.getId($$3)) : ItemStatisticsList.this.sortOrder * Integer.compare($$7, $$11);
            }
        }

        @OnlyIn(Dist.CLIENT)
        private class ItemRow extends ObjectSelectionList.Entry<ItemRow> {
            private final Item item;

            ItemRow(Item p_169517_) {
                this.item = p_169517_;
            }

            public Item getItem() {
                return this.item;
            }

            public void render(GuiGraphics p_283614_, int p_97082_, int p_97083_, int p_97084_, int p_97085_, int p_97086_, int p_97087_, int p_97088_, boolean p_97089_, float p_97090_) {
                StatsScreen.this.blitSlot(p_283614_, p_97084_ + 40, p_97083_, this.item);

                int $$10;
                for($$10 = 0; $$10 < StatsScreen.this.itemStatsList.blockColumns.size(); ++$$10) {
                    Stat $$12;
                    if (this.item instanceof BlockItem) {
                        $$12 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get($$10)).get(((BlockItem)this.item).getBlock());
                    } else {
                        $$12 = null;
                    }

                    this.renderStat(p_283614_, $$12, p_97084_ + StatsScreen.this.getColumnX($$10), p_97083_, p_97082_ % 2 == 0);
                }

                for($$10 = 0; $$10 < StatsScreen.this.itemStatsList.itemColumns.size(); ++$$10) {
                    this.renderStat(p_283614_, ((StatType)StatsScreen.this.itemStatsList.itemColumns.get($$10)).get(this.item), p_97084_ + StatsScreen.this.getColumnX($$10 + StatsScreen.this.itemStatsList.blockColumns.size()), p_97083_, p_97082_ % 2 == 0);
                }

            }

            protected void renderStat(GuiGraphics p_282544_, @Nullable Stat<?> p_97093_, int p_97094_, int p_97095_, boolean p_97096_) {
                String $$5 = p_97093_ == null ? "-" : p_97093_.format(StatsScreen.this.stats.getValue(p_97093_));
                p_282544_.drawString(StatsScreen.this.font, $$5, p_97094_ - StatsScreen.this.font.width($$5), p_97095_ + 5, p_97096_ ? 16777215 : 9474192);
            }

            public Component getNarration() {
                return Component.translatable("narrator.select", this.item.getDescription());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class MobsStatisticsList extends ObjectSelectionList<MobRow> {
        public MobsStatisticsList(Minecraft p_97100_) {
            int var10002 = StatsScreen.this.width;
            int var10003 = StatsScreen.this.height;
            int var10005 = StatsScreen.this.height - 64;
            Objects.requireNonNull(StatsScreen.this.font);
            super(p_97100_, var10002, var10003, 32, var10005, 9 * 4);
            Iterator var3 = BuiltInRegistries.ENTITY_TYPE.iterator();

            while(true) {
                EntityType $$1;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    $$1 = (EntityType)var3.next();
                } while(StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get($$1)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get($$1)) <= 0);

                this.addEntry(new MobRow($$1));
            }
        }

        protected void renderBackground(GuiGraphics p_282935_) {
            StatsScreen.this.renderBackground(p_282935_);
        }

        @OnlyIn(Dist.CLIENT)
        class MobRow extends ObjectSelectionList.Entry<MobRow> {
            private final Component mobName;
            private final Component kills;
            private final boolean hasKills;
            private final Component killedBy;
            private final boolean wasKilledBy;

            public MobRow(EntityType<?> p_97112_) {
                this.mobName = p_97112_.getDescription();
                int $$1 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(p_97112_));
                if ($$1 == 0) {
                    this.kills = Component.translatable("stat_type.minecraft.killed.none", this.mobName);
                    this.hasKills = false;
                } else {
                    this.kills = Component.translatable("stat_type.minecraft.killed", $$1, this.mobName);
                    this.hasKills = true;
                }

                int $$2 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(p_97112_));
                if ($$2 == 0) {
                    this.killedBy = Component.translatable("stat_type.minecraft.killed_by.none", this.mobName);
                    this.wasKilledBy = false;
                } else {
                    this.killedBy = Component.translatable("stat_type.minecraft.killed_by", this.mobName, $$2);
                    this.wasKilledBy = true;
                }

            }

            public void render(GuiGraphics p_283265_, int p_97115_, int p_97116_, int p_97117_, int p_97118_, int p_97119_, int p_97120_, int p_97121_, boolean p_97122_, float p_97123_) {
                p_283265_.drawString(StatsScreen.this.font, this.mobName, p_97117_ + 2, p_97116_ + 1, 16777215);
                Font var10001 = StatsScreen.this.font;
                Component var10002 = this.kills;
                int var10003 = p_97117_ + 2 + 10;
                int var10004 = p_97116_ + 1;
                Objects.requireNonNull(StatsScreen.this.font);
                p_283265_.drawString(var10001, var10002, var10003, var10004 + 9, this.hasKills ? 9474192 : 6316128);
                var10001 = StatsScreen.this.font;
                var10002 = this.killedBy;
                var10003 = p_97117_ + 2 + 10;
                var10004 = p_97116_ + 1;
                Objects.requireNonNull(StatsScreen.this.font);
                p_283265_.drawString(var10001, var10002, var10003, var10004 + 9 * 2, this.wasKilledBy ? 9474192 : 6316128);
            }

            public Component getNarration() {
                return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
            }
        }
    }
}
