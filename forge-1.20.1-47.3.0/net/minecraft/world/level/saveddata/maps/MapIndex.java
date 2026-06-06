//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
    public static final String FILE_NAME = "idcounts";
    private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

    public MapIndex() {
        this.usedAuxIds.defaultReturnValue(-1);
    }

    public static MapIndex load(CompoundTag p_164763_) {
        MapIndex $$1 = new MapIndex();
        Iterator var2 = p_164763_.getAllKeys().iterator();

        while(var2.hasNext()) {
            String $$2 = (String)var2.next();
            if (p_164763_.contains($$2, 99)) {
                $$1.usedAuxIds.put($$2, p_164763_.getInt($$2));
            }
        }

        return $$1;
    }

    public CompoundTag save(CompoundTag p_77884_) {
        ObjectIterator var2 = this.usedAuxIds.object2IntEntrySet().iterator();

        while(var2.hasNext()) {
            Object2IntMap.Entry<String> $$1 = (Object2IntMap.Entry)var2.next();
            p_77884_.putInt((String)$$1.getKey(), $$1.getIntValue());
        }

        return p_77884_;
    }

    public int getFreeAuxValueForMap() {
        int $$0 = this.usedAuxIds.getInt("map") + 1;
        this.usedAuxIds.put("map", $$0);
        this.setDirty();
        return $$0;
    }
}
