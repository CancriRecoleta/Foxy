//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public record EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) implements DataSource {
    public EntityDataSource(String p_237330_) {
        this(p_237330_, compileSelector(p_237330_));
    }

    public EntityDataSource(String selectorPattern, @Nullable EntitySelector compiledSelector) {
        this.selectorPattern = selectorPattern;
        this.compiledSelector = compiledSelector;
    }

    @Nullable
    private static EntitySelector compileSelector(String p_237336_) {
        try {
            EntitySelectorParser $$1 = new EntitySelectorParser(new StringReader(p_237336_));
            return $$1.parse();
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public Stream<CompoundTag> getData(CommandSourceStack p_237341_) throws CommandSyntaxException {
        if (this.compiledSelector != null) {
            List<? extends Entity> $$1 = this.compiledSelector.findEntities(p_237341_);
            return $$1.stream().map(NbtPredicate::getEntityTagToCompare);
        } else {
            return Stream.empty();
        }
    }

    public String toString() {
        return "entity=" + this.selectorPattern;
    }

    public boolean equals(Object p_237339_) {
        if (this == p_237339_) {
            return true;
        } else {
            boolean var10000;
            if (p_237339_ instanceof EntityDataSource) {
                EntityDataSource $$1 = (EntityDataSource)p_237339_;
                if (this.selectorPattern.equals($$1.selectorPattern)) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        return this.selectorPattern.hashCode();
    }

    public String selectorPattern() {
        return this.selectorPattern;
    }

    @Nullable
    public EntitySelector compiledSelector() {
        return this.compiledSelector;
    }
}
