//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PanoramaRenderer {
    private final Minecraft minecraft;
    private final CubeMap cubeMap;
    private float spin;
    private float bob;

    public PanoramaRenderer(CubeMap p_110002_) {
        this.cubeMap = p_110002_;
        this.minecraft = Minecraft.getInstance();
    }

    public void render(float p_110004_, float p_110005_) {
        float $$2 = (float)((double)p_110004_ * (Double)this.minecraft.options.panoramaSpeed().get());
        this.spin = wrap(this.spin + $$2 * 0.1F, 360.0F);
        this.bob = wrap(this.bob + $$2 * 0.001F, 6.2831855F);
        this.cubeMap.render(this.minecraft, 10.0F, -this.spin, p_110005_);
    }

    private static float wrap(float p_249058_, float p_249548_) {
        return p_249058_ > p_249548_ ? p_249058_ - p_249548_ : p_249058_;
    }
}
