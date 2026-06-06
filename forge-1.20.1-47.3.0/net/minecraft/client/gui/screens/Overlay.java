//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Renderable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Overlay implements Renderable {
    public Overlay() {
    }

    public boolean isPauseScreen() {
        return true;
    }
}
