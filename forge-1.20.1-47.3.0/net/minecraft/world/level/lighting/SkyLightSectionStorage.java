//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class SkyLightSectionStorage extends LayerLightSectionStorage<SkyDataLayerStorageMap> {
    protected SkyLightSectionStorage(LightChunkGetter p_75868_) {
        super(LightLayer.SKY, p_75868_, new SkyDataLayerStorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    protected int getLightValue(long p_75880_) {
        return this.getLightValue(p_75880_, false);
    }

    protected int getLightValue(long p_164458_, boolean p_164459_) {
        long $$2 = SectionPos.blockToSection(p_164458_);
        int $$3 = SectionPos.y($$2);
        SkyDataLayerStorageMap $$4 = p_164459_ ? (SkyDataLayerStorageMap)this.updatingSectionData : (SkyDataLayerStorageMap)this.visibleSectionData;
        int $$5 = $$4.topSections.get(SectionPos.getZeroNode($$2));
        if ($$5 != $$4.currentLowestY && $$3 < $$5) {
            DataLayer $$6 = this.getDataLayer($$4, $$2);
            if ($$6 == null) {
                for(p_164458_ = BlockPos.getFlatIndex(p_164458_); $$6 == null; $$6 = this.getDataLayer($$4, $$2)) {
                    ++$$3;
                    if ($$3 >= $$5) {
                        return 15;
                    }

                    $$2 = SectionPos.offset($$2, Direction.UP);
                }
            }

            return $$6.get(SectionPos.sectionRelative(BlockPos.getX(p_164458_)), SectionPos.sectionRelative(BlockPos.getY(p_164458_)), SectionPos.sectionRelative(BlockPos.getZ(p_164458_)));
        } else {
            return p_164459_ && !this.lightOnInSection($$2) ? 0 : 15;
        }
    }

    protected void onNodeAdded(long p_75885_) {
        int $$1 = SectionPos.y(p_75885_);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > $$1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = $$1;
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
        }

        long $$2 = SectionPos.getZeroNode(p_75885_);
        int $$3 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$2);
        if ($$3 < $$1 + 1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$2, $$1 + 1);
        }

    }

    protected void onNodeRemoved(long p_75887_) {
        long $$1 = SectionPos.getZeroNode(p_75887_);
        int $$2 = SectionPos.y(p_75887_);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1) == $$2 + 1) {
            long $$3;
            for($$3 = p_75887_; !this.storingLightForSection($$3) && this.hasLightDataAtOrBelow($$2); $$3 = SectionPos.offset($$3, Direction.DOWN)) {
                --$$2;
            }

            if (this.storingLightForSection($$3)) {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$1, $$2 + 1);
            } else {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove($$1);
            }
        }

    }

    protected DataLayer createDataLayer(long p_75883_) {
        DataLayer $$1 = (DataLayer)this.queuedSections.get(p_75883_);
        if ($$1 != null) {
            return $$1;
        } else {
            int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode(p_75883_));
            if ($$2 != ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY && SectionPos.y(p_75883_) < $$2) {
                DataLayer $$4;
                for(long $$3 = SectionPos.offset(p_75883_, Direction.UP); ($$4 = this.getDataLayer($$3, true)) == null; $$3 = SectionPos.offset($$3, Direction.UP)) {
                }

                return repeatFirstLayer($$4);
            } else {
                return this.lightOnInSection(p_75883_) ? new DataLayer(15) : new DataLayer();
            }
        }
    }

    private static DataLayer repeatFirstLayer(DataLayer p_182513_) {
        if (p_182513_.isDefinitelyHomogenous()) {
            return p_182513_.copy();
        } else {
            byte[] $$1 = p_182513_.getData();
            byte[] $$2 = new byte[2048];

            for(int $$3 = 0; $$3 < 16; ++$$3) {
                System.arraycopy($$1, 0, $$2, $$3 * 128, 128);
            }

            return new DataLayer($$2);
        }
    }

    protected boolean hasLightDataAtOrBelow(int p_278270_) {
        return p_278270_ >= ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected boolean isAboveData(long p_75891_) {
        long $$1 = SectionPos.getZeroNode(p_75891_);
        int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1);
        return $$2 == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(p_75891_) >= $$2;
    }

    protected int getTopSectionY(long p_285094_) {
        return ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(p_285094_);
    }

    protected int getBottomSectionY() {
        return ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected static final class SkyDataLayerStorageMap extends DataLayerStorageMap<SkyDataLayerStorageMap> {
        int currentLowestY;
        final Long2IntOpenHashMap topSections;

        public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> p_75903_, Long2IntOpenHashMap p_75904_, int p_75905_) {
            super(p_75903_);
            this.topSections = p_75904_;
            p_75904_.defaultReturnValue(p_75905_);
            this.currentLowestY = p_75905_;
        }

        public SkyDataLayerStorageMap copy() {
            return new SkyDataLayerStorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }
    }
}
