//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import net.minecraft.obfuscate.DontObfuscate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.internal.BrandingControl;

@OnlyIn(Dist.CLIENT)
public class ClientBrandRetriever {
    public static final String VANILLA_NAME = "vanilla";

    public ClientBrandRetriever() {
    }

    @DontObfuscate
    public static String getClientModName() {
        return BrandingControl.getClientBranding();
    }
}
