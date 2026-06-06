//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;

public class CommandStorage {
    private static final String ID_PREFIX = "command_storage_";
    private final Map<String, Container> namespaces = Maps.newHashMap();
    private final DimensionDataStorage storage;

    public CommandStorage(DimensionDataStorage p_78035_) {
        this.storage = p_78035_;
    }

    private Container newStorage(String p_164836_) {
        Container $$1 = new Container();
        this.namespaces.put(p_164836_, $$1);
        return $$1;
    }

    public CompoundTag get(ResourceLocation p_78045_) {
        String $$1 = p_78045_.getNamespace();
        Container $$2 = (Container)this.storage.get((p_164844_) -> {
            return this.newStorage($$1).load(p_164844_);
        }, createId($$1));
        return $$2 != null ? $$2.get(p_78045_.getPath()) : new CompoundTag();
    }

    public void set(ResourceLocation p_78047_, CompoundTag p_78048_) {
        String $$2 = p_78047_.getNamespace();
        ((Container)this.storage.computeIfAbsent((p_164839_) -> {
            return this.newStorage($$2).load(p_164839_);
        }, () -> {
            return this.newStorage($$2);
        }, createId($$2))).put(p_78047_.getPath(), p_78048_);
    }

    public Stream<ResourceLocation> keys() {
        return this.namespaces.entrySet().stream().flatMap((p_164841_) -> {
            return ((Container)p_164841_.getValue()).getKeys((String)p_164841_.getKey());
        });
    }

    private static String createId(String p_78038_) {
        return "command_storage_" + p_78038_;
    }

    private static class Container extends SavedData {
        private static final String TAG_CONTENTS = "contents";
        private final Map<String, CompoundTag> storage = Maps.newHashMap();

        Container() {
        }

        Container load(CompoundTag p_164850_) {
            CompoundTag $$1 = p_164850_.getCompound("contents");
            Iterator var3 = $$1.getAllKeys().iterator();

            while(var3.hasNext()) {
                String $$2 = (String)var3.next();
                this.storage.put($$2, $$1.getCompound($$2));
            }

            return this;
        }

        public CompoundTag save(CompoundTag p_78075_) {
            CompoundTag $$1 = new CompoundTag();
            this.storage.forEach((p_78070_, p_78071_) -> {
                $$1.put(p_78070_, p_78071_.copy());
            });
            p_78075_.put("contents", $$1);
            return p_78075_;
        }

        public CompoundTag get(String p_78059_) {
            CompoundTag $$1 = (CompoundTag)this.storage.get(p_78059_);
            return $$1 != null ? $$1 : new CompoundTag();
        }

        public void put(String p_78064_, CompoundTag p_78065_) {
            if (p_78065_.isEmpty()) {
                this.storage.remove(p_78064_);
            } else {
                this.storage.put(p_78064_, p_78065_);
            }

            this.setDirty();
        }

        public Stream<ResourceLocation> getKeys(String p_78073_) {
            return this.storage.keySet().stream().map((p_78062_) -> {
                return new ResourceLocation(p_78073_, p_78062_);
            });
        }
    }
}
