//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyValueCondition implements Condition {
    private static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
    private final String key;
    private final String value;

    public KeyValueCondition(String p_111939_, String p_111940_) {
        this.key = p_111939_;
        this.value = p_111940_;
    }

    public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> p_111960_) {
        Property<?> $$1 = p_111960_.getProperty(this.key);
        if ($$1 == null) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", this.key, p_111960_.getOwner()));
        } else {
            String $$2 = this.value;
            boolean $$3 = !$$2.isEmpty() && $$2.charAt(0) == '!';
            if ($$3) {
                $$2 = $$2.substring(1);
            }

            List<String> $$4 = PIPE_SPLITTER.splitToList($$2);
            if ($$4.isEmpty()) {
                throw new RuntimeException(String.format(Locale.ROOT, "Empty value '%s' for property '%s' on '%s'", this.value, this.key, p_111960_.getOwner()));
            } else {
                Predicate $$7;
                if ($$4.size() == 1) {
                    $$7 = this.getBlockStatePredicate(p_111960_, $$1, $$2);
                } else {
                    List<Predicate<BlockState>> $$6 = (List)$$4.stream().map((p_111958_) -> {
                        return this.getBlockStatePredicate(p_111960_, $$1, p_111958_);
                    }).collect(Collectors.toList());
                    $$7 = (p_111954_) -> {
                        return $$6.stream().anyMatch((p_173509_) -> {
                            return p_173509_.test(p_111954_);
                        });
                    };
                }

                return $$3 ? $$7.negate() : $$7;
            }
        }
    }

    private Predicate<BlockState> getBlockStatePredicate(StateDefinition<Block, BlockState> p_111945_, Property<?> p_111946_, String p_111947_) {
        Optional<?> $$3 = p_111946_.getValue(p_111947_);
        if (!$$3.isPresent()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", p_111947_, this.key, p_111945_.getOwner(), this.value));
        } else {
            return (p_111951_) -> {
                return p_111951_.getValue(p_111946_).equals($$3.get());
            };
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
    }
}
