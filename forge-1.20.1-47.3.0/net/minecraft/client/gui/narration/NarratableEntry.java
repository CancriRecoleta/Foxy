//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.narration;

import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface NarratableEntry extends TabOrderedElement, NarrationSupplier {
    NarrationPriority narrationPriority();

    default boolean isActive() {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum NarrationPriority {
        NONE,
        HOVERED,
        FOCUSED;

        private NarrationPriority() {
        }

        public boolean isTerminal() {
            return this == FOCUSED;
        }
    }
}
