//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.extensions;

import java.util.Collection;
import net.minecraft.server.packs.PackResources;
import org.jetbrains.annotations.Nullable;

public interface IForgePackResources {
    default boolean isHidden() {
        return false;
    }

    default @Nullable Collection<PackResources> getChildren() {
        return null;
    }
}
