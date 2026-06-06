//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleKeyMapping extends KeyMapping {
    private final BooleanSupplier needsToggle;

    public ToggleKeyMapping(String p_92529_, int p_92530_, String p_92531_, BooleanSupplier p_92532_) {
        super(p_92529_, Type.KEYSYM, p_92530_, p_92531_);
        this.needsToggle = p_92532_;
    }

    public void setDown(boolean p_92534_) {
        if (this.needsToggle.getAsBoolean()) {
            if (p_92534_ && this.isConflictContextAndModifierActive()) {
                super.setDown(!this.isDown());
            }
        } else {
            super.setDown(p_92534_);
        }

    }

    public boolean isDown() {
        return this.isDown && (this.isConflictContextAndModifierActive() || this.needsToggle.getAsBoolean());
    }

    protected void reset() {
        super.setDown(false);
    }
}
