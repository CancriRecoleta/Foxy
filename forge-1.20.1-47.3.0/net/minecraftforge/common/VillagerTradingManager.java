//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager {
    private static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> VANILLA_TRADES = new HashMap();
    private static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap();

    public VillagerTradingManager() {
    }

    static void loadTrades(ServerAboutToStartEvent e) {
        postWandererEvent();
        postVillagerEvents();
    }

    private static void postWandererEvent() {
        List<VillagerTrades.ItemListing> generic = NonNullList.create();
        List<VillagerTrades.ItemListing> rare = NonNullList.create();
        Stream var10000 = Arrays.stream((VillagerTrades.ItemListing[])WANDERER_TRADES.get(1));
        Objects.requireNonNull(generic);
        var10000.forEach(generic::add);
        var10000 = Arrays.stream((VillagerTrades.ItemListing[])WANDERER_TRADES.get(2));
        Objects.requireNonNull(rare);
        var10000.forEach(rare::add);
        MinecraftForge.EVENT_BUS.post(new WandererTradesEvent(generic, rare));
        VillagerTrades.WANDERING_TRADER_TRADES.put(1, (VillagerTrades.ItemListing[])generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, (VillagerTrades.ItemListing[])rare.toArray(new VillagerTrades.ItemListing[0]));
    }

    private static void postVillagerEvents() {
        Iterator var0 = ForgeRegistries.VILLAGER_PROFESSIONS.iterator();

        while(var0.hasNext()) {
            VillagerProfession prof = (VillagerProfession)var0.next();
            Int2ObjectMap<VillagerTrades.ItemListing[]> trades = (Int2ObjectMap)VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap());
            Int2ObjectMap<List<VillagerTrades.ItemListing>> mutableTrades = new Int2ObjectOpenHashMap();

            for(int i = 1; i < 6; ++i) {
                mutableTrades.put(i, NonNullList.create());
            }

            trades.int2ObjectEntrySet().forEach((e) -> {
                Stream var10000 = Arrays.stream((VillagerTrades.ItemListing[])e.getValue());
                List var10001 = (List)mutableTrades.get(e.getIntKey());
                Objects.requireNonNull(var10001);
                var10000.forEach(var10001::add);
            });
            MinecraftForge.EVENT_BUS.post(new VillagerTradesEvent(mutableTrades, prof));
            Int2ObjectMap<VillagerTrades.ItemListing[]> newTrades = new Int2ObjectOpenHashMap();
            mutableTrades.int2ObjectEntrySet().forEach((e) -> {
                newTrades.put(e.getIntKey(), (VillagerTrades.ItemListing[])((List)e.getValue()).toArray(new VillagerTrades.ItemListing[0]));
            });
            VillagerTrades.TRADES.put(prof, newTrades);
        }

    }

    static {
        VillagerTrades.TRADES.entrySet().forEach((e) -> {
            Int2ObjectMap<VillagerTrades.ItemListing[]> copy = new Int2ObjectOpenHashMap();
            ((Int2ObjectMap)e.getValue()).int2ObjectEntrySet().forEach((ent) -> {
                copy.put(ent.getIntKey(), (VillagerTrades.ItemListing[])Arrays.copyOf((VillagerTrades.ItemListing[])ent.getValue(), ((VillagerTrades.ItemListing[])ent.getValue()).length));
            });
            VANILLA_TRADES.put((VillagerProfession)e.getKey(), copy);
        });
        VillagerTrades.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach((e) -> {
            WANDERER_TRADES.put(e.getIntKey(), (VillagerTrades.ItemListing[])Arrays.copyOf((VillagerTrades.ItemListing[])e.getValue(), ((VillagerTrades.ItemListing[])e.getValue()).length));
        });
    }
}
