//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public record BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) implements DataSource {
    public BlockDataSource(String p_237312_) {
        this(p_237312_, compilePos(p_237312_));
    }

    public BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) {
        this.posPattern = posPattern;
        this.compiledPos = compiledPos;
    }

    @Nullable
    private static Coordinates compilePos(String p_237318_) {
        try {
            return BlockPosArgument.blockPos().parse(new StringReader(p_237318_));
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public Stream<CompoundTag> getData(CommandSourceStack p_237323_) {
        if (this.compiledPos != null) {
            ServerLevel $$1 = p_237323_.getLevel();
            BlockPos $$2 = this.compiledPos.getBlockPos(p_237323_);
            if ($$1.isLoaded($$2)) {
                BlockEntity $$3 = $$1.getBlockEntity($$2);
                if ($$3 != null) {
                    return Stream.of($$3.saveWithFullMetadata());
                }
            }
        }

        return Stream.empty();
    }

    public String toString() {
        return "block=" + this.posPattern;
    }

    public boolean equals(Object p_237321_) {
        if (this == p_237321_) {
            return true;
        } else {
            boolean var10000;
            if (p_237321_ instanceof BlockDataSource) {
                BlockDataSource $$1 = (BlockDataSource)p_237321_;
                if (this.posPattern.equals($$1.posPattern)) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        return this.posPattern.hashCode();
    }

    public String posPattern() {
        return this.posPattern;
    }

    @Nullable
    public Coordinates compiledPos() {
        return this.compiledPos;
    }
}
