//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.providers.nbt;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface NbtProvider {
    @Nullable
    Tag get(LootContext var1);

    Set<LootContextParam<?>> getReferencedContextParams();

    LootNbtProviderType getType();
}
