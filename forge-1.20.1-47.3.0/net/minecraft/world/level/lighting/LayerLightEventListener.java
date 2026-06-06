//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;

public interface LayerLightEventListener extends LightEventListener {
    @Nullable
    DataLayer getDataLayerData(SectionPos var1);

    int getLightValue(BlockPos var1);

    public static enum DummyLightLayerEventListener implements LayerLightEventListener {
        INSTANCE;

        private DummyLightLayerEventListener() {
        }

        @Nullable
        public DataLayer getDataLayerData(SectionPos p_75718_) {
            return null;
        }

        public int getLightValue(BlockPos p_75723_) {
            return 0;
        }

        public void checkBlock(BlockPos p_164434_) {
        }

        public boolean hasLightWork() {
            return false;
        }

        public int runLightUpdates() {
            return 0;
        }

        public void updateSectionStatus(SectionPos p_75720_, boolean p_75721_) {
        }

        public void setLightEnabled(ChunkPos p_164431_, boolean p_164432_) {
        }

        public void propagateLightSources(ChunkPos p_285209_) {
        }
    }
}
