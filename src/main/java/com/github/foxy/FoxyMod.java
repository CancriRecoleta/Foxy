package com.github.foxy;

import com.github.foxy.commonImpl.FoxyCommon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

// Forge entrypoint for Foxy. The heavy lifting lives in FoxyCommon (common) and FoxyClient (client);
// this class just bootstraps them. Client wiring is dispatched through DistExecutor so none of the
// client/render classes are loaded on a dedicated server.
@Mod(FoxyMod.MODID)
public class FoxyMod {
    public static final String MODID = "foxy";

    public FoxyMod() {
        // Run FoxyCommon's static initializer now that the mod list and environment are available.
        FoxyCommon.bootstrap();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> com.github.foxy.client.FoxyClient::bootstrapClient);
    }
}
