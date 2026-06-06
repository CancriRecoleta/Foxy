//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RandomizedIntStateProvider extends BlockStateProvider {
    public static final Codec<RandomizedIntStateProvider> CODEC = RecordCodecBuilder.create((p_161576_) -> {
        return p_161576_.group(BlockStateProvider.CODEC.fieldOf("source").forGetter((p_161592_) -> {
            return p_161592_.source;
        }), Codec.STRING.fieldOf("property").forGetter((p_161590_) -> {
            return p_161590_.propertyName;
        }), IntProvider.CODEC.fieldOf("values").forGetter((p_161578_) -> {
            return p_161578_.values;
        })).apply(p_161576_, RandomizedIntStateProvider::new);
    });
    private final BlockStateProvider source;
    private final String propertyName;
    @Nullable
    private IntegerProperty property;
    private final IntProvider values;

    public RandomizedIntStateProvider(BlockStateProvider p_161562_, IntegerProperty p_161563_, IntProvider p_161564_) {
        this.source = p_161562_;
        this.property = p_161563_;
        this.propertyName = p_161563_.getName();
        this.values = p_161564_;
        Collection<Integer> $$3 = p_161563_.getPossibleValues();

        for(int $$4 = p_161564_.getMinValue(); $$4 <= p_161564_.getMaxValue(); ++$$4) {
            if (!$$3.contains($$4)) {
                String var10002 = p_161563_.getName();
                throw new IllegalArgumentException("Property value out of range: " + var10002 + ": " + $$4);
            }
        }

    }

    public RandomizedIntStateProvider(BlockStateProvider p_161566_, String p_161567_, IntProvider p_161568_) {
        this.source = p_161566_;
        this.propertyName = p_161567_;
        this.values = p_161568_;
    }

    protected BlockStateProviderType<?> type() {
        return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
    }

    public BlockState getState(RandomSource p_225919_, BlockPos p_225920_) {
        BlockState $$2 = this.source.getState(p_225919_, p_225920_);
        if (this.property == null || !$$2.hasProperty(this.property)) {
            this.property = findProperty($$2, this.propertyName);
        }

        return (BlockState)$$2.setValue(this.property, this.values.sample(p_225919_));
    }

    private static IntegerProperty findProperty(BlockState p_161571_, String p_161572_) {
        Collection<Property<?>> $$2 = p_161571_.getProperties();
        Optional<IntegerProperty> $$3 = $$2.stream().filter((p_161583_) -> {
            return p_161583_.getName().equals(p_161572_);
        }).filter((p_161588_) -> {
            return p_161588_ instanceof IntegerProperty;
        }).map((p_161574_) -> {
            return (IntegerProperty)p_161574_;
        }).findAny();
        return (IntegerProperty)$$3.orElseThrow(() -> {
            return new IllegalArgumentException("Illegal property: " + p_161572_);
        });
    }
}
