//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeManager {
    private static TrackedList<BiomeEntry>[] biomes = setupBiomes();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomes = new ArrayList();
    private static final List<ResourceKey<Biome>> additionalOverworldBiomesView;

    public BiomeManager() {
    }

    private static TrackedList<BiomeEntry>[] setupBiomes() {
        TrackedList<BiomeEntry>[] currentBiomes = new TrackedList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];
        currentBiomes[net.minecraftforge.common.BiomeManager.BiomeType.DESERT_LEGACY.ordinal()] = new TrackedList(new BiomeEntry[]{new BiomeEntry(Biomes.DESERT, 10), new BiomeEntry(Biomes.FOREST, 10), new BiomeEntry(Biomes.SWAMP, 10), new BiomeEntry(Biomes.PLAINS, 10), new BiomeEntry(Biomes.TAIGA, 10)});
        currentBiomes[net.minecraftforge.common.BiomeManager.BiomeType.DESERT.ordinal()] = new TrackedList(new BiomeEntry[]{new BiomeEntry(Biomes.DESERT, 30), new BiomeEntry(Biomes.SAVANNA, 20), new BiomeEntry(Biomes.PLAINS, 10)});
        currentBiomes[net.minecraftforge.common.BiomeManager.BiomeType.WARM.ordinal()] = new TrackedList(new BiomeEntry[]{new BiomeEntry(Biomes.FOREST, 10), new BiomeEntry(Biomes.DARK_FOREST, 10), new BiomeEntry(Biomes.PLAINS, 10), new BiomeEntry(Biomes.BIRCH_FOREST, 10), new BiomeEntry(Biomes.SWAMP, 10)});
        currentBiomes[net.minecraftforge.common.BiomeManager.BiomeType.COOL.ordinal()] = new TrackedList(new BiomeEntry[]{new BiomeEntry(Biomes.FOREST, 10), new BiomeEntry(Biomes.TAIGA, 10), new BiomeEntry(Biomes.PLAINS, 10)});
        currentBiomes[net.minecraftforge.common.BiomeManager.BiomeType.ICY.ordinal()] = new TrackedList(new BiomeEntry[]{new BiomeEntry(Biomes.SNOWY_TAIGA, 10)});
        return currentBiomes;
    }

    public static void addAdditionalOverworldBiomes(ResourceKey<Biome> biome) {
        if (!"minecraft".equals(biome.location().getNamespace()) && additionalOverworldBiomes.stream().noneMatch((entry) -> {
            return entry.location().equals(biome.location());
        })) {
            additionalOverworldBiomes.add(biome);
        }

    }

    public static boolean addBiome(BiomeType type, BiomeEntry entry) {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        if (list != null) {
            additionalOverworldBiomes.add(entry.key);
            return list.add(entry);
        } else {
            return false;
        }
    }

    public static boolean removeBiome(BiomeType type, BiomeEntry entry) {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.remove(entry);
    }

    public static List<ResourceKey<Biome>> getAdditionalOverworldBiomes() {
        return additionalOverworldBiomesView;
    }

    public static ImmutableList<BiomeEntry> getBiomes(BiomeType type) {
        int idx = type.ordinal();
        List<BiomeEntry> list = idx >= biomes.length ? null : biomes[idx];
        return list != null ? ImmutableList.copyOf(list) : ImmutableList.of();
    }

    public static boolean isTypeListModded(BiomeType type) {
        int idx = type.ordinal();
        TrackedList<BiomeEntry> list = idx > biomes.length ? null : biomes[idx];
        return list == null ? false : list.isModded();
    }

    static {
        additionalOverworldBiomesView = Collections.unmodifiableList(additionalOverworldBiomes);
    }

    public static enum BiomeType {
        DESERT,
        DESERT_LEGACY,
        WARM,
        COOL,
        ICY;

        private BiomeType() {
        }
    }

    private static class TrackedList<E> extends ArrayList<E> {
        private static final long serialVersionUID = 1L;
        private boolean isModded = false;

        @SafeVarargs
        private <T extends E> TrackedList(T... c) {
            super(Arrays.asList(c));
        }

        public E set(int index, E element) {
            this.isModded = true;
            return super.set(index, element);
        }

        public boolean add(E e) {
            this.isModded = true;
            return super.add(e);
        }

        public void add(int index, E element) {
            this.isModded = true;
            super.add(index, element);
        }

        public E remove(int index) {
            this.isModded = true;
            return super.remove(index);
        }

        public boolean remove(Object o) {
            this.isModded = true;
            return super.remove(o);
        }

        public void clear() {
            this.isModded = true;
            super.clear();
        }

        public boolean addAll(Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            this.isModded = true;
            return super.addAll(index, c);
        }

        public boolean removeAll(Collection<?> c) {
            this.isModded = true;
            return super.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            this.isModded = true;
            return super.retainAll(c);
        }

        public boolean isModded() {
            return this.isModded;
        }
    }

    public static class BiomeEntry {
        private final ResourceKey<Biome> key;

        public BiomeEntry(ResourceKey<Biome> key, int weight) {
            this.key = key;
        }

        public ResourceKey<Biome> getKey() {
            return this.key;
        }
    }
}
