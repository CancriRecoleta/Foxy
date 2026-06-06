//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class CreativeModeTabSearchRegistry {
    private static final Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> NAME_SEARCH_KEYS = new IdentityHashMap();
    private static final Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> TAG_SEARCH_KEYS = new IdentityHashMap();

    public CreativeModeTabSearchRegistry() {
    }

    public static Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> getNameSearchKeys() {
        Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> nameSearchKeys = new IdentityHashMap();
        nameSearchKeys.put(CreativeModeTabs.searchTab(), getNameSearchKey(CreativeModeTabs.searchTab()));
        Iterator var1 = CreativeModeTabRegistry.getSortedCreativeModeTabs().iterator();

        while(var1.hasNext()) {
            CreativeModeTab tab = (CreativeModeTab)var1.next();
            SearchRegistry.Key<ItemStack> nameSearchKey = getNameSearchKey(tab);
            if (nameSearchKey != null) {
                nameSearchKeys.put(tab, nameSearchKey);
            }
        }

        return nameSearchKeys;
    }

    public static Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> getTagSearchKeys() {
        Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> tagSearchKeys = new IdentityHashMap();
        tagSearchKeys.put(CreativeModeTabs.searchTab(), getTagSearchKey(CreativeModeTabs.searchTab()));
        Iterator var1 = CreativeModeTabRegistry.getSortedCreativeModeTabs().iterator();

        while(var1.hasNext()) {
            CreativeModeTab tab = (CreativeModeTab)var1.next();
            SearchRegistry.Key<ItemStack> tagSearchKey = getTagSearchKey(tab);
            if (tagSearchKey != null) {
                tagSearchKeys.put(tab, tagSearchKey);
            }
        }

        return tagSearchKeys;
    }

    @Nullable
    public static SearchRegistry.@Nullable Key<ItemStack> getNameSearchKey(CreativeModeTab tab) {
        if (tab == CreativeModeTabs.searchTab()) {
            return SearchRegistry.CREATIVE_NAMES;
        } else {
            return !tab.hasSearchBar() ? null : (SearchRegistry.Key)NAME_SEARCH_KEYS.computeIfAbsent(tab, (k) -> {
                return new SearchRegistry.Key();
            });
        }
    }

    @Nullable
    public static SearchRegistry.@Nullable Key<ItemStack> getTagSearchKey(CreativeModeTab tab) {
        if (tab == CreativeModeTabs.searchTab()) {
            return SearchRegistry.CREATIVE_TAGS;
        } else {
            return !tab.hasSearchBar() ? null : (SearchRegistry.Key)TAG_SEARCH_KEYS.computeIfAbsent(tab, (k) -> {
                return new SearchRegistry.Key();
            });
        }
    }

    @Internal
    public static void createSearchTrees() {
        Minecraft.getInstance().createSearchTrees();
    }
}
