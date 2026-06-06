//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.model.data.ModelDataManager;
import org.jetbrains.annotations.Nullable;

public interface IForgeBlockGetter {
    private BlockGetter self() {
        return (BlockGetter)this;
    }

    default @Nullable BlockEntity getExistingBlockEntity(BlockPos pos) {
        if (this instanceof Level level) {
            return !level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())) ? null : level.getChunk(pos).getExistingBlockEntity(pos);
        } else if (this instanceof LevelChunk chunk) {
            return (BlockEntity)chunk.getBlockEntities().get(pos);
        } else if (this instanceof ImposterProtoChunk chunk) {
            return chunk.getWrapped().getExistingBlockEntity(pos);
        } else {
            return this.self().getBlockEntity(pos);
        }
    }

    default @Nullable ModelDataManager getModelDataManager() {
        return null;
    }
}
