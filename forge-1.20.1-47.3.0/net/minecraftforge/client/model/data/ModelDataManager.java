//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@EventBusSubscriber(
    modid = "forge",
    bus = Bus.FORGE,
    value = {Dist.CLIENT}
)
@Internal
public class ModelDataManager {
    private final Level level;
    private final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap();
    private final Map<ChunkPos, Map<BlockPos, ModelData>> modelDataCache = new ConcurrentHashMap();

    public ModelDataManager(Level level) {
        this.level = level;
    }

    public void requestRefresh(@NotNull BlockEntity blockEntity) {
        Preconditions.checkNotNull(blockEntity, "Block entity must not be null");
        ((Set)this.needModelDataRefresh.computeIfAbsent(new ChunkPos(blockEntity.getBlockPos()), ($) -> {
            return Collections.synchronizedSet(new HashSet());
        })).add(blockEntity.getBlockPos());
    }

    private void refreshAt(ChunkPos chunk) {
        Set<BlockPos> needUpdate = (Set)this.needModelDataRefresh.remove(chunk);
        if (needUpdate != null) {
            Map<BlockPos, ModelData> data = (Map)this.modelDataCache.computeIfAbsent(chunk, ($) -> {
                return new ConcurrentHashMap();
            });
            Iterator var4 = needUpdate.iterator();

            while(true) {
                while(var4.hasNext()) {
                    BlockPos pos = (BlockPos)var4.next();
                    BlockEntity toUpdate = this.level.getBlockEntity(pos);
                    if (toUpdate != null && !toUpdate.isRemoved()) {
                        data.put(pos, toUpdate.getModelData());
                    } else {
                        data.remove(pos);
                    }
                }

                return;
            }
        }
    }

    public @Nullable ModelData getAt(BlockPos pos) {
        return (ModelData)this.getAt(new ChunkPos(pos)).get(pos);
    }

    public Map<BlockPos, ModelData> getAt(ChunkPos pos) {
        Preconditions.checkArgument(this.level.isClientSide, "Cannot request model data for server level");
        this.refreshAt(pos);
        return (Map)this.modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        LevelAccessor level = event.getChunk().getWorldForge();
        if (level != null) {
            ModelDataManager modelDataManager = level.getModelDataManager();
            if (modelDataManager != null) {
                ChunkPos chunk = event.getChunk().getPos();
                modelDataManager.needModelDataRefresh.remove(chunk);
                modelDataManager.modelDataCache.remove(chunk);
            }
        }
    }
}
