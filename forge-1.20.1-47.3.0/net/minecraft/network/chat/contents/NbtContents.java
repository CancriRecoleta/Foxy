//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class NbtContents implements ComponentContents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean interpreting;
    private final Optional<Component> separator;
    private final String nbtPathPattern;
    private final DataSource dataSource;
    @Nullable
    protected final NbtPathArgument.NbtPath compiledNbtPath;

    public NbtContents(String p_237395_, boolean p_237396_, Optional<Component> p_237397_, DataSource p_237398_) {
        this(p_237395_, compileNbtPath(p_237395_), p_237396_, p_237397_, p_237398_);
    }

    private NbtContents(String p_237389_, @Nullable NbtPathArgument.NbtPath p_237390_, boolean p_237391_, Optional<Component> p_237392_, DataSource p_237393_) {
        this.nbtPathPattern = p_237389_;
        this.compiledNbtPath = p_237390_;
        this.interpreting = p_237391_;
        this.separator = p_237392_;
        this.dataSource = p_237393_;
    }

    @Nullable
    private static NbtPathArgument.NbtPath compileNbtPath(String p_237410_) {
        try {
            return (new NbtPathArgument()).parse(new StringReader(p_237410_));
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public String getNbtPath() {
        return this.nbtPathPattern;
    }

    public boolean isInterpreting() {
        return this.interpreting;
    }

    public Optional<Component> getSeparator() {
        return this.separator;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public boolean equals(Object p_237430_) {
        if (this == p_237430_) {
            return true;
        } else {
            boolean var10000;
            if (p_237430_ instanceof NbtContents) {
                NbtContents $$1 = (NbtContents)p_237430_;
                if (this.dataSource.equals($$1.dataSource) && this.separator.equals($$1.separator) && this.interpreting == $$1.interpreting && this.nbtPathPattern.equals($$1.nbtPathPattern)) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        int $$0 = this.interpreting ? 1 : 0;
        $$0 = 31 * $$0 + this.separator.hashCode();
        $$0 = 31 * $$0 + this.nbtPathPattern.hashCode();
        $$0 = 31 * $$0 + this.dataSource.hashCode();
        return $$0;
    }

    public String toString() {
        return "nbt{" + this.dataSource + ", interpreting=" + this.interpreting + ", separator=" + this.separator + "}";
    }

    public MutableComponent resolve(@Nullable CommandSourceStack p_237401_, @Nullable Entity p_237402_, int p_237403_) throws CommandSyntaxException {
        if (p_237401_ != null && this.compiledNbtPath != null) {
            Stream<String> $$3 = this.dataSource.getData(p_237401_).flatMap((p_237417_) -> {
                try {
                    return this.compiledNbtPath.get(p_237417_).stream();
                } catch (CommandSyntaxException var3) {
                    return Stream.empty();
                }
            }).map(Tag::getAsString);
            if (this.interpreting) {
                Component $$4 = (Component)DataFixUtils.orElse(ComponentUtils.updateForEntity(p_237401_, this.separator, p_237402_, p_237403_), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR);
                return (MutableComponent)$$3.flatMap((p_237408_) -> {
                    try {
                        MutableComponent $$4 = Serializer.fromJson(p_237408_);
                        return Stream.of(ComponentUtils.updateForEntity(p_237401_, (Component)$$4, p_237402_, p_237403_));
                    } catch (Exception var5) {
                        Exception $$5 = var5;
                        LOGGER.warn("Failed to parse component: {}", p_237408_, $$5);
                        return Stream.of();
                    }
                }).reduce((p_237420_, p_237421_) -> {
                    return p_237420_.append($$4).append((Component)p_237421_);
                }).orElseGet(Component::empty);
            } else {
                return (MutableComponent)ComponentUtils.updateForEntity(p_237401_, this.separator, p_237402_, p_237403_).map((p_237415_) -> {
                    return (MutableComponent)$$3.map(Component::literal).reduce((p_237424_, p_237425_) -> {
                        return p_237424_.append((Component)p_237415_).append((Component)p_237425_);
                    }).orElseGet(Component::empty);
                }).orElseGet(() -> {
                    return Component.literal((String)$$3.collect(Collectors.joining(", ")));
                });
            }
        } else {
            return Component.empty();
        }
    }
}
