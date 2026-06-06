//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ParametersAreNonnullByDefault
public class ForgeChunkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TicketType<TicketOwner<BlockPos>> BLOCK = TicketType.create("forge:block", Comparator.comparing((info) -> {
        return info;
    }));
    private static final TicketType<TicketOwner<BlockPos>> BLOCK_TICKING = TicketType.create("forge:block_ticking", Comparator.comparing((info) -> {
        return info;
    }));
    private static final TicketType<TicketOwner<UUID>> ENTITY = TicketType.create("forge:entity", Comparator.comparing((info) -> {
        return info;
    }));
    private static final TicketType<TicketOwner<UUID>> ENTITY_TICKING = TicketType.create("forge:entity_ticking", Comparator.comparing((info) -> {
        return info;
    }));
    private static final Map<String, LoadingValidationCallback> callbacks = new HashMap();

    public ForgeChunkManager() {
    }

    public static void setForcedChunkLoadingCallback(String modId, LoadingValidationCallback callback) {
        if (ModList.get().isLoaded(modId)) {
            callbacks.put(modId, callback);
        } else {
            LOGGER.warn("A mod attempted to set the forced chunk validation loading callback for an unloaded mod of id: {}", modId);
        }

    }

    public static boolean hasForcedChunks(ServerLevel level) {
        ForcedChunksSavedData data = (ForcedChunksSavedData)level.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
        if (data == null) {
            return false;
        } else {
            return !data.getChunks().isEmpty() || !data.getBlockForcedChunks().isEmpty() || !data.getEntityForcedChunks().isEmpty();
        }
    }

    public static boolean forceChunk(ServerLevel level, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking, ticking ? BLOCK_TICKING : BLOCK, ForcedChunksSavedData::getBlockForcedChunks);
    }

    public static boolean forceChunk(ServerLevel level, String modId, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner.getUUID(), chunkX, chunkZ, add, ticking);
    }

    public static boolean forceChunk(ServerLevel level, String modId, UUID owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking, ticking ? ENTITY_TICKING : ENTITY, ForcedChunksSavedData::getEntityForcedChunks);
    }

    private static <T extends Comparable<? super T>> boolean forceChunk(ServerLevel level, String modId, T owner, int chunkX, int chunkZ, boolean add, boolean ticking, TicketType<TicketOwner<T>> type, Function<ForcedChunksSavedData, TicketTracker<T>> ticketGetter) {
        if (!ModList.get().isLoaded(modId)) {
            LOGGER.warn("A mod attempted to force a chunk for an unloaded mod of id: {}", modId);
            return false;
        } else {
            ForcedChunksSavedData saveData = (ForcedChunksSavedData)level.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks");
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);
            long chunk = pos.toLong();
            TicketTracker<T> tickets = (TicketTracker)ticketGetter.apply(saveData);
            TicketOwner<T> ticketOwner = new TicketOwner(modId, owner);
            boolean success;
            if (add) {
                success = tickets.add(ticketOwner, chunk, ticking);
                if (success) {
                    level.getChunk(chunkX, chunkZ);
                }
            } else {
                success = tickets.remove(ticketOwner, chunk, ticking);
            }

            if (success) {
                saveData.setDirty(true);
                forceChunk(level, pos, type, ticketOwner, add, ticking);
            }

            return success;
        }
    }

    private static <T extends Comparable<? super T>> void forceChunk(ServerLevel level, ChunkPos pos, TicketType<TicketOwner<T>> type, TicketOwner<T> owner, boolean add, boolean ticking) {
        if (add) {
            level.getChunkSource().addRegionTicket(type, pos, 2, owner, ticking);
        } else {
            level.getChunkSource().removeRegionTicket(type, pos, 2, owner, ticking);
        }

    }

    public static void reinstatePersistentChunks(ServerLevel level, ForcedChunksSavedData saveData) {
        if (!callbacks.isEmpty()) {
            Map<String, Map<BlockPos, Pair<LongSet, LongSet>>> blockTickets = gatherTicketsByModId(saveData.getBlockForcedChunks());
            Map<String, Map<UUID, Pair<LongSet, LongSet>>> entityTickets = gatherTicketsByModId(saveData.getEntityForcedChunks());
            Iterator var4 = callbacks.entrySet().iterator();

            label31:
            while(true) {
                Map.Entry entry;
                String modId;
                boolean hasBlockTicket;
                boolean hasEntityTicket;
                do {
                    if (!var4.hasNext()) {
                        break label31;
                    }

                    entry = (Map.Entry)var4.next();
                    modId = (String)entry.getKey();
                    hasBlockTicket = blockTickets.containsKey(modId);
                    hasEntityTicket = entityTickets.containsKey(modId);
                } while(!hasBlockTicket && !hasEntityTicket);

                Map<BlockPos, Pair<LongSet, LongSet>> ownedBlockTickets = hasBlockTicket ? Collections.unmodifiableMap((Map)blockTickets.get(modId)) : Collections.emptyMap();
                Map<UUID, Pair<LongSet, LongSet>> ownedEntityTickets = hasEntityTicket ? Collections.unmodifiableMap((Map)entityTickets.get(modId)) : Collections.emptyMap();
                ((LoadingValidationCallback)entry.getValue()).validateTickets(level, new TicketHelper(saveData, modId, ownedBlockTickets, ownedEntityTickets));
            }
        }

        reinstatePersistentChunks(level, BLOCK, saveData.getBlockForcedChunks().chunks, false);
        reinstatePersistentChunks(level, BLOCK_TICKING, saveData.getBlockForcedChunks().tickingChunks, true);
        reinstatePersistentChunks(level, ENTITY, saveData.getEntityForcedChunks().chunks, false);
        reinstatePersistentChunks(level, ENTITY_TICKING, saveData.getEntityForcedChunks().tickingChunks, true);
    }

    private static <T extends Comparable<? super T>> Map<String, Map<T, Pair<LongSet, LongSet>>> gatherTicketsByModId(TicketTracker<T> tickets) {
        Map<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks = new HashMap();
        gatherTicketsByModId(tickets.chunks, Pair::getFirst, modSortedOwnedChunks);
        gatherTicketsByModId(tickets.tickingChunks, Pair::getSecond, modSortedOwnedChunks);
        return modSortedOwnedChunks;
    }

    private static <T extends Comparable<? super T>> void gatherTicketsByModId(Map<TicketOwner<T>, LongSet> tickets, Function<Pair<LongSet, LongSet>, LongSet> typeGetter, Map<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks) {
        Iterator var3 = tickets.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<TicketOwner<T>, LongSet> entry = (Map.Entry)var3.next();
            Pair<LongSet, LongSet> pair = (Pair)((Map)modSortedOwnedChunks.computeIfAbsent(((TicketOwner)entry.getKey()).modId, (modId) -> {
                return new HashMap();
            })).computeIfAbsent(((TicketOwner)entry.getKey()).owner, (owner) -> {
                return new Pair(new LongOpenHashSet(), new LongOpenHashSet());
            });
            ((LongSet)typeGetter.apply(pair)).addAll((LongCollection)entry.getValue());
        }

    }

    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerLevel level, TicketType<TicketOwner<T>> type, Map<TicketOwner<T>, LongSet> tickets, boolean ticking) {
        Iterator var4 = tickets.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<TicketOwner<T>, LongSet> entry = (Map.Entry)var4.next();
            LongIterator var6 = ((LongSet)entry.getValue()).iterator();

            while(var6.hasNext()) {
                long chunk = (Long)var6.next();
                forceChunk(level, new ChunkPos(chunk), type, (TicketOwner)entry.getKey(), true, ticking);
            }
        }

    }

    public static void writeForgeForcedChunks(CompoundTag nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks) {
        if (!blockForcedChunks.isEmpty() || !entityForcedChunks.isEmpty()) {
            Map<String, Long2ObjectMap<CompoundTag>> forcedEntries = new HashMap();
            writeForcedChunkOwners(forcedEntries, (TicketTracker)blockForcedChunks, "Blocks", 10, (pos, forcedBlocks) -> {
                forcedBlocks.add(NbtUtils.writeBlockPos(pos));
            });
            writeForcedChunkOwners(forcedEntries, (TicketTracker)entityForcedChunks, "Entities", 11, (uuid, forcedEntities) -> {
                forcedEntities.add(NbtUtils.createUUID(uuid));
            });
            ListTag forcedChunks = new ListTag();
            Iterator var5 = forcedEntries.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, Long2ObjectMap<CompoundTag>> entry = (Map.Entry)var5.next();
                CompoundTag forcedEntry = new CompoundTag();
                forcedEntry.putString("Mod", (String)entry.getKey());
                ListTag modForced = new ListTag();
                modForced.addAll(((Long2ObjectMap)entry.getValue()).values());
                forcedEntry.put("ModForced", modForced);
                forcedChunks.add(forcedEntry);
            }

            nbt.put("ForgeForced", forcedChunks);
        }

    }

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundTag>> forcedEntries, TicketTracker<T> tracker, String listKey, int listType, BiConsumer<T, ListTag> ownerWriter) {
        writeForcedChunkOwners(forcedEntries, tracker.chunks, listKey, listType, ownerWriter);
        writeForcedChunkOwners(forcedEntries, tracker.tickingChunks, "Ticking" + listKey, listType, ownerWriter);
    }

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundTag>> forcedEntries, Map<TicketOwner<T>, LongSet> forcedChunks, String listKey, int listType, BiConsumer<T, ListTag> ownerWriter) {
        Iterator var5 = forcedChunks.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<TicketOwner<T>, LongSet> entry = (Map.Entry)var5.next();
            Long2ObjectMap<CompoundTag> modForced = (Long2ObjectMap)forcedEntries.computeIfAbsent(((TicketOwner)entry.getKey()).modId, (modId) -> {
                return new Long2ObjectOpenHashMap();
            });
            LongIterator var8 = ((LongSet)entry.getValue()).iterator();

            while(var8.hasNext()) {
                long chunk = (Long)var8.next();
                CompoundTag modEntry = (CompoundTag)modForced.computeIfAbsent(chunk, (chunkPos) -> {
                    CompoundTag baseEntry = new CompoundTag();
                    baseEntry.putLong("Chunk", chunkPos);
                    return baseEntry;
                });
                ListTag ownerList = modEntry.getList(listKey, listType);
                ownerWriter.accept(((TicketOwner)entry.getKey()).owner, ownerList);
                modEntry.put(listKey, ownerList);
            }
        }

    }

    public static void readForgeForcedChunks(CompoundTag nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks) {
        ListTag forcedChunks = nbt.getList("ForgeForced", 10);

        for(int i = 0; i < forcedChunks.size(); ++i) {
            CompoundTag forcedEntry = forcedChunks.getCompound(i);
            String modId = forcedEntry.getString("Mod");
            if (ModList.get().isLoaded(modId)) {
                ListTag modForced = forcedEntry.getList("ModForced", 10);

                for(int j = 0; j < modForced.size(); ++j) {
                    CompoundTag modEntry = modForced.getCompound(j);
                    long chunkPos = modEntry.getLong("Chunk");
                    readBlockForcedChunks(modId, chunkPos, modEntry, "Blocks", blockForcedChunks.chunks);
                    readBlockForcedChunks(modId, chunkPos, modEntry, "TickingBlocks", blockForcedChunks.tickingChunks);
                    readEntityForcedChunks(modId, chunkPos, modEntry, "Entities", entityForcedChunks.chunks);
                    readEntityForcedChunks(modId, chunkPos, modEntry, "TickingEntities", entityForcedChunks.tickingChunks);
                }
            } else {
                LOGGER.warn("Found chunk loading data for mod {} which is currently not available or active - it will be removed from the level save.", modId);
            }
        }

    }

    private static void readBlockForcedChunks(String modId, long chunkPos, CompoundTag modEntry, String key, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks) {
        ListTag forcedBlocks = modEntry.getList(key, 10);

        for(int k = 0; k < forcedBlocks.size(); ++k) {
            ((LongSet)blockForcedChunks.computeIfAbsent(new TicketOwner(modId, NbtUtils.readBlockPos(forcedBlocks.getCompound(k))), (owner) -> {
                return new LongOpenHashSet();
            })).add(chunkPos);
        }

    }

    private static void readEntityForcedChunks(String modId, long chunkPos, CompoundTag modEntry, String key, Map<TicketOwner<UUID>, LongSet> entityForcedChunks) {
        ListTag forcedEntities = modEntry.getList(key, 11);
        Iterator var7 = forcedEntities.iterator();

        while(var7.hasNext()) {
            Tag uuid = (Tag)var7.next();
            ((LongSet)entityForcedChunks.computeIfAbsent(new TicketOwner(modId, NbtUtils.loadUUID(uuid)), (owner) -> {
                return new LongOpenHashSet();
            })).add(chunkPos);
        }

    }

    public static class TicketTracker<T extends Comparable<? super T>> {
        private final Map<TicketOwner<T>, LongSet> chunks = new HashMap();
        private final Map<TicketOwner<T>, LongSet> tickingChunks = new HashMap();

        public TicketTracker() {
        }

        public Map<TicketOwner<T>, LongSet> getChunks() {
            return Collections.unmodifiableMap(this.chunks);
        }

        public Map<TicketOwner<T>, LongSet> getTickingChunks() {
            return Collections.unmodifiableMap(this.tickingChunks);
        }

        public boolean isEmpty() {
            return this.chunks.isEmpty() && this.tickingChunks.isEmpty();
        }

        private Map<TicketOwner<T>, LongSet> getTickets(boolean ticking) {
            return ticking ? this.tickingChunks : this.chunks;
        }

        private boolean remove(TicketOwner<T> owner, long chunk, boolean ticking) {
            Map<TicketOwner<T>, LongSet> tickets = this.getTickets(ticking);
            if (tickets.containsKey(owner)) {
                LongSet ticketChunks = (LongSet)tickets.get(owner);
                if (ticketChunks.remove(chunk)) {
                    if (ticketChunks.isEmpty()) {
                        tickets.remove(owner);
                    }

                    return true;
                }
            }

            return false;
        }

        private boolean add(TicketOwner<T> owner, long chunk, boolean ticking) {
            return ((LongSet)this.getTickets(ticking).computeIfAbsent(owner, (o) -> {
                return new LongOpenHashSet();
            })).add(chunk);
        }
    }

    public static class TicketOwner<T extends Comparable<? super T>> implements Comparable<TicketOwner<T>> {
        private final String modId;
        private final T owner;

        private TicketOwner(String modId, T owner) {
            this.modId = modId;
            this.owner = owner;
        }

        public int compareTo(TicketOwner<T> other) {
            int res = this.modId.compareTo(other.modId);
            return res == 0 ? this.owner.compareTo(other.owner) : res;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                TicketOwner<?> that = (TicketOwner)o;
                return Objects.equals(this.modId, that.modId) && Objects.equals(this.owner, that.owner);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.modId, this.owner});
        }
    }

    @FunctionalInterface
    public interface LoadingValidationCallback {
        void validateTickets(ServerLevel var1, TicketHelper var2);
    }

    public static class TicketHelper {
        private final Map<BlockPos, Pair<LongSet, LongSet>> blockTickets;
        private final Map<UUID, Pair<LongSet, LongSet>> entityTickets;
        private final ForcedChunksSavedData saveData;
        private final String modId;

        private TicketHelper(ForcedChunksSavedData saveData, String modId, Map<BlockPos, Pair<LongSet, LongSet>> blockTickets, Map<UUID, Pair<LongSet, LongSet>> entityTickets) {
            this.saveData = saveData;
            this.modId = modId;
            this.blockTickets = blockTickets;
            this.entityTickets = entityTickets;
        }

        public Map<BlockPos, Pair<LongSet, LongSet>> getBlockTickets() {
            return this.blockTickets;
        }

        public Map<UUID, Pair<LongSet, LongSet>> getEntityTickets() {
            return this.entityTickets;
        }

        public void removeAllTickets(BlockPos owner) {
            this.removeAllTickets(this.saveData.getBlockForcedChunks(), owner);
        }

        public void removeAllTickets(UUID owner) {
            this.removeAllTickets(this.saveData.getEntityForcedChunks(), owner);
        }

        private <T extends Comparable<? super T>> void removeAllTickets(TicketTracker<T> tickets, T owner) {
            TicketOwner<T> ticketOwner = new TicketOwner(this.modId, owner);
            if (tickets.chunks.containsKey(ticketOwner) || tickets.tickingChunks.containsKey(ticketOwner)) {
                tickets.chunks.remove(ticketOwner);
                tickets.tickingChunks.remove(ticketOwner);
                this.saveData.setDirty(true);
            }

        }

        public void removeTicket(BlockPos owner, long chunk, boolean ticking) {
            this.removeTicket(this.saveData.getBlockForcedChunks(), owner, chunk, ticking);
        }

        public void removeTicket(UUID owner, long chunk, boolean ticking) {
            this.removeTicket(this.saveData.getEntityForcedChunks(), owner, chunk, ticking);
        }

        private <T extends Comparable<? super T>> void removeTicket(TicketTracker<T> tickets, T owner, long chunk, boolean ticking) {
            if (tickets.remove(new TicketOwner(this.modId, owner), chunk, ticking)) {
                this.saveData.setDirty(true);
            }

        }
    }
}
