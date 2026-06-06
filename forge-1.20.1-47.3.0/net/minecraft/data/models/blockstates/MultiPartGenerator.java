//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPartGenerator implements BlockStateGenerator {
    private final Block block;
    private final List<Entry> parts = Lists.newArrayList();

    private MultiPartGenerator(Block p_125202_) {
        this.block = p_125202_;
    }

    public Block getBlock() {
        return this.block;
    }

    public static MultiPartGenerator multiPart(Block p_125205_) {
        return new MultiPartGenerator(p_125205_);
    }

    public MultiPartGenerator with(List<Variant> p_125221_) {
        this.parts.add(new Entry(p_125221_));
        return this;
    }

    public MultiPartGenerator with(Variant p_125219_) {
        return this.with((List)ImmutableList.of(p_125219_));
    }

    public MultiPartGenerator with(Condition p_125213_, List<Variant> p_125214_) {
        this.parts.add(new ConditionalEntry(p_125213_, p_125214_));
        return this;
    }

    public MultiPartGenerator with(Condition p_125216_, Variant... p_125217_) {
        return this.with(p_125216_, (List)ImmutableList.copyOf(p_125217_));
    }

    public MultiPartGenerator with(Condition p_125210_, Variant p_125211_) {
        return this.with(p_125210_, (List)ImmutableList.of(p_125211_));
    }

    public JsonElement get() {
        StateDefinition<Block, BlockState> $$0 = this.block.getStateDefinition();
        this.parts.forEach((p_125208_) -> {
            p_125208_.validate($$0);
        });
        JsonArray $$1 = new JsonArray();
        Stream var10000 = this.parts.stream().map(Entry::get);
        Objects.requireNonNull($$1);
        var10000.forEach($$1::add);
        JsonObject $$2 = new JsonObject();
        $$2.add("multipart", $$1);
        return $$2;
    }

    private static class Entry implements Supplier<JsonElement> {
        private final List<Variant> variants;

        Entry(List<Variant> p_125238_) {
            this.variants = p_125238_;
        }

        public void validate(StateDefinition<?, ?> p_125243_) {
        }

        public void decorate(JsonObject p_125244_) {
        }

        public JsonElement get() {
            JsonObject $$0 = new JsonObject();
            this.decorate($$0);
            $$0.add("apply", Variant.convertList(this.variants));
            return $$0;
        }
    }

    static class ConditionalEntry extends Entry {
        private final Condition condition;

        ConditionalEntry(Condition p_125226_, List<Variant> p_125227_) {
            super(p_125227_);
            this.condition = p_125226_;
        }

        public void validate(StateDefinition<?, ?> p_125233_) {
            this.condition.validate(p_125233_);
        }

        public void decorate(JsonObject p_125235_) {
            p_125235_.add("when", (JsonElement)this.condition.get());
        }
    }
}
