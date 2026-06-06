//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record StorageDataSource(ResourceLocation id) implements DataSource {
    public StorageDataSource(ResourceLocation id) {
        this.id = id;
    }

    public Stream<CompoundTag> getData(CommandSourceStack p_237491_) {
        CompoundTag $$1 = p_237491_.getServer().getCommandStorage().get(this.id);
        return Stream.of($$1);
    }

    public String toString() {
        return "storage=" + this.id;
    }

    public ResourceLocation id() {
        return this.id;
    }
}
