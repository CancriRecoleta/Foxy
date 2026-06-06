//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementSubProvider {
    static Advancement createPlaceholder(String p_267076_) {
        return Builder.advancement().build(new ResourceLocation(p_267076_));
    }

    void generate(HolderLookup.Provider var1, Consumer<Advancement> var2);
}
