//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.File;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class HotbarManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int NUM_HOTBAR_GROUPS = 9;
    private final File optionsFile;
    private final DataFixer fixerUpper;
    private final Hotbar[] hotbars = new Hotbar[9];
    private boolean loaded;

    public HotbarManager(File p_90803_, DataFixer p_90804_) {
        this.optionsFile = new File(p_90803_, "hotbar.nbt");
        this.fixerUpper = p_90804_;

        for(int $$2 = 0; $$2 < 9; ++$$2) {
            this.hotbars[$$2] = new Hotbar();
        }

    }

    private void load() {
        try {
            CompoundTag $$0 = NbtIo.read(this.optionsFile);
            if ($$0 == null) {
                return;
            }

            int $$1 = NbtUtils.getDataVersion($$0, 1343);
            $$0 = DataFixTypes.HOTBAR.updateToCurrentVersion(this.fixerUpper, $$0, $$1);

            for(int $$2 = 0; $$2 < 9; ++$$2) {
                this.hotbars[$$2].fromTag($$0.getList(String.valueOf($$2), 10));
            }
        } catch (Exception var4) {
            Exception $$3 = var4;
            LOGGER.error("Failed to load creative mode options", $$3);
        }

    }

    public void save() {
        try {
            CompoundTag $$0 = NbtUtils.addCurrentDataVersion(new CompoundTag());

            for(int $$1 = 0; $$1 < 9; ++$$1) {
                $$0.put(String.valueOf($$1), this.get($$1).createTag());
            }

            NbtIo.write($$0, this.optionsFile);
        } catch (Exception var3) {
            Exception $$2 = var3;
            LOGGER.error("Failed to save creative mode options", $$2);
        }

    }

    public Hotbar get(int p_90807_) {
        if (!this.loaded) {
            this.load();
            this.loaded = true;
        }

        return this.hotbars[p_90807_];
    }
}
