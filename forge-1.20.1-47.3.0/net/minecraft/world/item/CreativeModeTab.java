//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ForgeHooks;

public class CreativeModeTab {
    private final Component displayName;
    String backgroundSuffix;
    boolean canScroll;
    boolean showTitle;
    boolean alignedRight;
    private final Row row;
    private final int column;
    private final Type type;
    @Nullable
    private ItemStack iconItemStack;
    private Collection<ItemStack> displayItems;
    private Set<ItemStack> displayItemsSearchTab;
    @Nullable
    private Consumer<List<ItemStack>> searchTreeBuilder;
    private final Supplier<ItemStack> iconGenerator;
    private final DisplayItemsGenerator displayItemsGenerator;
    private ResourceLocation backgroundLocation;
    private final boolean hasSearchBar;
    private final int searchBarWidth;
    private final ResourceLocation tabsImage;
    private final int labelColor;
    private final int slotColor;
    public final List<ResourceLocation> tabsBefore;
    public final List<ResourceLocation> tabsAfter;

    CreativeModeTab(Row p_260217_, int p_259557_, Type p_260176_, Component p_260100_, Supplier<ItemStack> p_259543_, DisplayItemsGenerator p_259085_, ResourceLocation backgroundLocation, boolean hasSearchBar, int searchBarWidth, ResourceLocation tabsImage, int labelColor, int slotColor, List<ResourceLocation> tabsBefore, List<ResourceLocation> tabsAfter) {
        this.backgroundSuffix = "items.png";
        this.canScroll = true;
        this.showTitle = true;
        this.alignedRight = false;
        this.displayItems = ItemStackLinkedSet.createTypeAndTagSet();
        this.displayItemsSearchTab = ItemStackLinkedSet.createTypeAndTagSet();
        this.row = p_260217_;
        this.column = p_259557_;
        this.displayName = p_260100_;
        this.iconGenerator = p_259543_;
        this.displayItemsGenerator = p_259085_;
        this.type = p_260176_;
        this.backgroundLocation = backgroundLocation;
        this.hasSearchBar = hasSearchBar;
        this.searchBarWidth = searchBarWidth;
        this.tabsImage = tabsImage;
        this.labelColor = labelColor;
        this.slotColor = slotColor;
        this.tabsBefore = List.copyOf(tabsBefore);
        this.tabsAfter = List.copyOf(tabsAfter);
    }

    protected CreativeModeTab(Builder builder) {
        this(builder.row, builder.column, builder.type, builder.displayName, builder.iconGenerator, builder.displayItemsGenerator, builder.backgroundLocation, builder.hasSearchBar, builder.searchBarWidth, builder.tabsImage, builder.labelColor, builder.slotColor, builder.tabsBefore, builder.tabsAfter);
    }

    public static Builder builder() {
        return new Builder(net.minecraft.world.item.CreativeModeTab.Row.TOP, 0);
    }

    /** @deprecated */
    @Deprecated
    public static Builder builder(Row p_259342_, int p_260312_) {
        return new Builder(p_259342_, p_260312_);
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public ItemStack getIconItem() {
        if (this.iconItemStack == null) {
            this.iconItemStack = (ItemStack)this.iconGenerator.get();
        }

        return this.iconItemStack;
    }

    /** @deprecated */
    @Deprecated
    public String getBackgroundSuffix() {
        return this.backgroundSuffix;
    }

    public boolean showTitle() {
        return this.showTitle;
    }

    public boolean canScroll() {
        return this.canScroll;
    }

    public int column() {
        return this.column;
    }

    public Row row() {
        return this.row;
    }

    public boolean hasAnyItems() {
        return !this.displayItems.isEmpty();
    }

    public boolean shouldDisplay() {
        return this.type != net.minecraft.world.item.CreativeModeTab.Type.CATEGORY || this.hasAnyItems();
    }

    public boolean isAlignedRight() {
        return this.alignedRight;
    }

    public Type getType() {
        return this.type;
    }

    public void buildContents(ItemDisplayParameters p_270156_) {
        ItemDisplayBuilder creativemodetab$itemdisplaybuilder = new ItemDisplayBuilder(this, p_270156_.enabledFeatures);
        ResourceKey<CreativeModeTab> resourcekey = (ResourceKey)BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(this).orElseThrow(() -> {
            return new IllegalStateException("Unregistered creative tab: " + this);
        });
        ForgeHooks.onCreativeModeTabBuildContents(this, resourcekey, this.displayItemsGenerator, p_270156_, creativemodetab$itemdisplaybuilder);
        this.displayItems = creativemodetab$itemdisplaybuilder.tabContents;
        this.displayItemsSearchTab = creativemodetab$itemdisplaybuilder.searchTabContents;
        this.rebuildSearchTree();
    }

    public Collection<ItemStack> getDisplayItems() {
        return this.displayItems;
    }

    public Collection<ItemStack> getSearchTabDisplayItems() {
        return this.displayItemsSearchTab;
    }

    public boolean contains(ItemStack p_259317_) {
        return this.displayItemsSearchTab.contains(p_259317_);
    }

    public void setSearchTreeBuilder(Consumer<List<ItemStack>> p_259669_) {
        this.searchTreeBuilder = p_259669_;
    }

    public void rebuildSearchTree() {
        if (this.searchTreeBuilder != null) {
            this.searchTreeBuilder.accept(Lists.newArrayList(this.displayItemsSearchTab));
        }

    }

    public ResourceLocation getBackgroundLocation() {
        return this.backgroundLocation;
    }

    public boolean hasSearchBar() {
        return this.hasSearchBar;
    }

    public int getSearchBarWidth() {
        return this.searchBarWidth;
    }

    public ResourceLocation getTabsImage() {
        return this.tabsImage;
    }

    public int getLabelColor() {
        return this.labelColor;
    }

    public int getSlotColor() {
        return this.slotColor;
    }

    public static enum Row {
        TOP,
        BOTTOM;

        private Row() {
        }
    }

    @FunctionalInterface
    public interface DisplayItemsGenerator {
        void accept(ItemDisplayParameters var1, Output var2);
    }

    public static enum Type {
        CATEGORY,
        INVENTORY,
        HOTBAR,
        SEARCH;

        private Type() {
        }
    }

    public static class Builder {
        private static final DisplayItemsGenerator EMPTY_GENERATOR = (p_270422_, p_259433_) -> {
        };
        private static final ResourceLocation CREATIVE_INVENTORY_TABS_IMAGE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
        private final Row row;
        private final int column;
        private Component displayName = Component.empty();
        private Supplier<ItemStack> iconGenerator = () -> {
            return ItemStack.EMPTY;
        };
        private DisplayItemsGenerator displayItemsGenerator;
        private boolean canScroll;
        private boolean showTitle;
        private boolean alignedRight;
        private Type type;
        private String backgroundSuffix;
        private @org.jetbrains.annotations.Nullable ResourceLocation backgroundLocation;
        private boolean hasSearchBar;
        private int searchBarWidth;
        private ResourceLocation tabsImage;
        private int labelColor;
        private int slotColor;
        private Function<Builder, CreativeModeTab> tabFactory;
        private final List<ResourceLocation> tabsBefore;
        private final List<ResourceLocation> tabsAfter;

        public Builder(Row p_259171_, int p_259661_) {
            this.displayItemsGenerator = EMPTY_GENERATOR;
            this.canScroll = true;
            this.showTitle = true;
            this.alignedRight = false;
            this.type = net.minecraft.world.item.CreativeModeTab.Type.CATEGORY;
            this.backgroundSuffix = "items.png";
            this.hasSearchBar = false;
            this.searchBarWidth = 89;
            this.tabsImage = CREATIVE_INVENTORY_TABS_IMAGE;
            this.labelColor = 4210752;
            this.slotColor = -2130706433;
            this.tabFactory = CreativeModeTab::new;
            this.tabsBefore = new ArrayList();
            this.tabsAfter = new ArrayList();
            this.row = p_259171_;
            this.column = p_259661_;
        }

        public Builder title(Component p_259616_) {
            this.displayName = p_259616_;
            return this;
        }

        public Builder icon(Supplier<ItemStack> p_259333_) {
            this.iconGenerator = p_259333_;
            return this;
        }

        public Builder displayItems(DisplayItemsGenerator p_259814_) {
            this.displayItemsGenerator = p_259814_;
            return this;
        }

        public Builder alignedRight() {
            this.alignedRight = true;
            return this;
        }

        public Builder hideTitle() {
            this.showTitle = false;
            return this;
        }

        public Builder noScrollBar() {
            this.canScroll = false;
            return this;
        }

        protected Builder type(Type p_259283_) {
            this.type = p_259283_;
            return p_259283_ == net.minecraft.world.item.CreativeModeTab.Type.SEARCH ? this.withSearchBar() : this;
        }

        public Builder backgroundSuffix(String p_259981_) {
            return this.withBackgroundLocation(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + p_259981_));
        }

        public Builder withBackgroundLocation(ResourceLocation background) {
            this.backgroundLocation = background;
            return this;
        }

        public Builder withSearchBar() {
            this.hasSearchBar = true;
            return this.backgroundLocation == null ? this.backgroundSuffix("item_search.png") : this;
        }

        public Builder withSearchBar(int searchBarWidth) {
            this.searchBarWidth = searchBarWidth;
            return this.withSearchBar();
        }

        public Builder withTabsImage(ResourceLocation tabsImage) {
            this.tabsImage = tabsImage;
            return this;
        }

        public Builder withLabelColor(int labelColor) {
            this.labelColor = labelColor;
            return this;
        }

        public Builder withSlotColor(int slotColor) {
            this.slotColor = slotColor;
            return this;
        }

        public Builder withTabFactory(Function<Builder, CreativeModeTab> tabFactory) {
            this.tabFactory = tabFactory;
            return this;
        }

        public Builder withTabsBefore(ResourceLocation... tabs) {
            this.tabsBefore.addAll(List.of(tabs));
            return this;
        }

        public Builder withTabsAfter(ResourceLocation... tabs) {
            this.tabsAfter.addAll(List.of(tabs));
            return this;
        }

        @SafeVarargs
        public final Builder withTabsBefore(ResourceKey<CreativeModeTab>... tabs) {
            Stream var10000 = Stream.of(tabs).map(ResourceKey::location);
            List var10001 = this.tabsBefore;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
            return this;
        }

        @SafeVarargs
        public final Builder withTabsAfter(ResourceKey<CreativeModeTab>... tabs) {
            Stream var10000 = Stream.of(tabs).map(ResourceKey::location);
            List var10001 = this.tabsAfter;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
            return this;
        }

        public CreativeModeTab build() {
            if ((this.type == net.minecraft.world.item.CreativeModeTab.Type.HOTBAR || this.type == net.minecraft.world.item.CreativeModeTab.Type.INVENTORY) && this.displayItemsGenerator != EMPTY_GENERATOR) {
                throw new IllegalStateException("Special tabs can't have display items");
            } else {
                CreativeModeTab creativemodetab = (CreativeModeTab)this.tabFactory.apply(this);
                creativemodetab.alignedRight = this.alignedRight;
                creativemodetab.showTitle = this.showTitle;
                creativemodetab.canScroll = this.canScroll;
                creativemodetab.backgroundSuffix = this.backgroundSuffix;
                creativemodetab.backgroundLocation = this.backgroundLocation != null ? this.backgroundLocation : new ResourceLocation("textures/gui/container/creative_inventory/tab_" + this.backgroundSuffix);
                return creativemodetab;
            }
        }
    }

    static class ItemDisplayBuilder implements Output {
        public final Collection<ItemStack> tabContents = ItemStackLinkedSet.createTypeAndTagSet();
        public final Set<ItemStack> searchTabContents = ItemStackLinkedSet.createTypeAndTagSet();
        private final CreativeModeTab tab;
        private final FeatureFlagSet featureFlagSet;

        public ItemDisplayBuilder(CreativeModeTab p_251040_, FeatureFlagSet p_249331_) {
            this.tab = p_251040_;
            this.featureFlagSet = p_249331_;
        }

        public void accept(ItemStack p_250391_, TabVisibility p_251472_) {
            if (p_250391_.getCount() != 1) {
                throw new IllegalArgumentException("Stack size must be exactly 1");
            } else {
                boolean flag = this.tabContents.contains(p_250391_) && p_251472_ != net.minecraft.world.item.CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
                if (flag) {
                    String var10002 = p_250391_.getDisplayName().getString();
                    throw new IllegalStateException("Accidentally adding the same item stack twice " + var10002 + " to a Creative Mode Tab: " + this.tab.getDisplayName().getString());
                } else {
                    if (p_250391_.getItem().isEnabled(this.featureFlagSet)) {
                        switch (p_251472_) {
                            case PARENT_AND_SEARCH_TABS:
                                this.tabContents.add(p_250391_);
                                this.searchTabContents.add(p_250391_);
                                break;
                            case PARENT_TAB_ONLY:
                                this.tabContents.add(p_250391_);
                                break;
                            case SEARCH_TAB_ONLY:
                                this.searchTabContents.add(p_250391_);
                        }
                    }

                }
            }
        }
    }

    public static record ItemDisplayParameters(FeatureFlagSet enabledFeatures, boolean hasPermissions, HolderLookup.Provider holders) {
        public ItemDisplayParameters(FeatureFlagSet enabledFeatures, boolean hasPermissions, HolderLookup.Provider holders) {
            this.enabledFeatures = enabledFeatures;
            this.hasPermissions = hasPermissions;
            this.holders = holders;
        }

        public boolean needsUpdate(FeatureFlagSet p_270338_, boolean p_270835_, HolderLookup.Provider p_270575_) {
            return !this.enabledFeatures.equals(p_270338_) || this.hasPermissions != p_270835_ || this.holders != p_270575_;
        }

        public FeatureFlagSet enabledFeatures() {
            return this.enabledFeatures;
        }

        public boolean hasPermissions() {
            return this.hasPermissions;
        }

        public HolderLookup.Provider holders() {
            return this.holders;
        }
    }

    public interface Output {
        void accept(ItemStack var1, TabVisibility var2);

        default void accept(ItemStack p_249977_) {
            this.accept(p_249977_, net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void accept(ItemLike p_251528_, TabVisibility p_249821_) {
            this.accept(new ItemStack(p_251528_), p_249821_);
        }

        default void accept(ItemLike p_248610_) {
            this.accept(new ItemStack(p_248610_), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void acceptAll(Collection<ItemStack> p_251548_, TabVisibility p_252285_) {
            p_251548_.forEach((p_252337_) -> {
                this.accept(p_252337_, p_252285_);
            });
        }

        default void acceptAll(Collection<ItemStack> p_250244_) {
            this.acceptAll(p_250244_, net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    public static enum TabVisibility {
        PARENT_AND_SEARCH_TABS,
        PARENT_TAB_ONLY,
        SEARCH_TAB_ONLY;

        private TabVisibility() {
        }
    }
}
