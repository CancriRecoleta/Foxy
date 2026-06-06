//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator implements BlockStateGenerator {
    private final Block block;
    private final List<Variant> baseVariants;
    private final Set<Property<?>> seenProperties = Sets.newHashSet();
    private final List<PropertyDispatch> declaredPropertySets = Lists.newArrayList();

    private MultiVariantGenerator(Block p_125251_, List<Variant> p_125252_) {
        this.block = p_125251_;
        this.baseVariants = p_125252_;
    }

    public MultiVariantGenerator with(PropertyDispatch p_125272_) {
        p_125272_.getDefinedProperties().forEach((p_125263_) -> {
            if (this.block.getStateDefinition().getProperty(p_125263_.getName()) != p_125263_) {
                throw new IllegalStateException("Property " + p_125263_ + " is not defined for block " + this.block);
            } else if (!this.seenProperties.add(p_125263_)) {
                throw new IllegalStateException("Values of property " + p_125263_ + " already defined for block " + this.block);
            }
        });
        this.declaredPropertySets.add(p_125272_);
        return this;
    }

    public JsonElement get() {
        Stream<Pair<Selector, List<Variant>>> $$0 = Stream.of(Pair.of(Selector.empty(), this.baseVariants));

        Map $$2;
        for(Iterator var2 = this.declaredPropertySets.iterator(); var2.hasNext(); $$0 = $$0.flatMap((p_125289_) -> {
            return $$2.entrySet().stream().map((p_176309_) -> {
                Selector $$2 = ((Selector)p_125289_.getFirst()).extend((Selector)p_176309_.getKey());
                List<Variant> $$3 = mergeVariants((List)p_125289_.getSecond(), (List)p_176309_.getValue());
                return Pair.of($$2, $$3);
            });
        })) {
            PropertyDispatch $$1 = (PropertyDispatch)var2.next();
            $$2 = $$1.getEntries();
        }

        Map<String, JsonElement> $$3 = new TreeMap();
        $$0.forEach((p_125285_) -> {
            $$3.put(((Selector)p_125285_.getFirst()).getKey(), Variant.convertList((List)p_125285_.getSecond()));
        });
        JsonObject $$4 = new JsonObject();
        $$4.add("variants", (JsonElement)Util.make(new JsonObject(), (p_125282_) -> {
            Objects.requireNonNull(p_125282_);
            $$3.forEach(p_125282_::add);
        }));
        return $$4;
    }

    private static List<Variant> mergeVariants(List<Variant> p_125278_, List<Variant> p_125279_) {
        ImmutableList.Builder<Variant> $$2 = ImmutableList.builder();
        p_125278_.forEach((p_125276_) -> {
            p_125279_.forEach((p_176306_) -> {
                $$2.add(Variant.merge(p_125276_, p_176306_));
            });
        });
        return $$2.build();
    }

    public Block getBlock() {
        return this.block;
    }

    public static MultiVariantGenerator multiVariant(Block p_125255_) {
        return new MultiVariantGenerator(p_125255_, ImmutableList.of(Variant.variant()));
    }

    public static MultiVariantGenerator multiVariant(Block p_125257_, Variant p_125258_) {
        return new MultiVariantGenerator(p_125257_, ImmutableList.of(p_125258_));
    }

    public static MultiVariantGenerator multiVariant(Block p_125260_, Variant... p_125261_) {
        return new MultiVariantGenerator(p_125260_, ImmutableList.copyOf(p_125261_));
    }
}
