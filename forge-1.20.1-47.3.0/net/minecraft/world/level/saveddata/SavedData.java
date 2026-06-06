//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.saveddata;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.slf4j.Logger;

public abstract class SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean dirty;

    public SavedData() {
    }

    public abstract CompoundTag save(CompoundTag var1);

    public void setDirty() {
        this.setDirty(true);
    }

    public void setDirty(boolean p_77761_) {
        this.dirty = p_77761_;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void save(File p_77758_) {
        if (this.isDirty()) {
            CompoundTag $$1 = new CompoundTag();
            $$1.put("data", this.save(new CompoundTag()));
            NbtUtils.addCurrentDataVersion($$1);

            try {
                NbtIo.writeCompressed($$1, p_77758_);
            } catch (IOException var4) {
                IOException $$2 = var4;
                LOGGER.error("Could not save data {}", this, $$2);
            }

            this.setDirty(false);
        }
    }
}
