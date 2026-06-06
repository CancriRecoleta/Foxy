//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;

public class LocalMobCapCalculator {
    private final Long2ObjectMap<List<ServerPlayer>> playersNearChunk = new Long2ObjectOpenHashMap();
    private final Map<ServerPlayer, MobCounts> playerMobCounts = Maps.newHashMap();
    private final ChunkMap chunkMap;

    public LocalMobCapCalculator(ChunkMap p_186501_) {
        this.chunkMap = p_186501_;
    }

    private List<ServerPlayer> getPlayersNear(ChunkPos p_186508_) {
        return (List)this.playersNearChunk.computeIfAbsent(p_186508_.toLong(), (p_186511_) -> {
            return this.chunkMap.getPlayersCloseForSpawning(p_186508_);
        });
    }

    public void addMob(ChunkPos p_186513_, MobCategory p_186514_) {
        Iterator var3 = this.getPlayersNear(p_186513_).iterator();

        while(var3.hasNext()) {
            ServerPlayer $$2 = (ServerPlayer)var3.next();
            ((MobCounts)this.playerMobCounts.computeIfAbsent($$2, (p_186503_) -> {
                return new MobCounts();
            })).add(p_186514_);
        }

    }

    public boolean canSpawn(MobCategory p_186505_, ChunkPos p_186506_) {
        Iterator var3 = this.getPlayersNear(p_186506_).iterator();

        MobCounts $$3;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            ServerPlayer $$2 = (ServerPlayer)var3.next();
            $$3 = (MobCounts)this.playerMobCounts.get($$2);
        } while($$3 != null && !$$3.canSpawn(p_186505_));

        return true;
    }

    static class MobCounts {
        private final Object2IntMap<MobCategory> counts = new Object2IntOpenHashMap(MobCategory.values().length);

        MobCounts() {
        }

        public void add(MobCategory p_186518_) {
            this.counts.computeInt(p_186518_, (p_186520_, p_186521_) -> {
                return p_186521_ == null ? 1 : p_186521_ + 1;
            });
        }

        public boolean canSpawn(MobCategory p_186523_) {
            return this.counts.getOrDefault(p_186523_, 0) < p_186523_.getMaxInstancesPerChunk();
        }
    }
}
