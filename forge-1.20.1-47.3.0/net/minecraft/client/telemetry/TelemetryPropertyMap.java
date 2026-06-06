//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.telemetry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TelemetryPropertyMap {
    final Map<TelemetryProperty<?>, Object> entries;

    TelemetryPropertyMap(Map<TelemetryProperty<?>, Object> p_262135_) {
        this.entries = p_262135_;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Codec<TelemetryPropertyMap> createCodec(final List<TelemetryProperty<?>> p_262139_) {
        return (new MapCodec<TelemetryPropertyMap>() {
            public <T> RecordBuilder<T> encode(TelemetryPropertyMap p_261525_, DynamicOps<T> p_262068_, RecordBuilder<T> p_261850_) {
                RecordBuilder<T> $$3 = p_261850_;

                TelemetryProperty $$4;
                for(Iterator var5 = p_262139_.iterator(); var5.hasNext(); $$3 = this.encodeProperty(p_261525_, $$3, $$4)) {
                    $$4 = (TelemetryProperty)var5.next();
                }

                return $$3;
            }

            private <T, V> RecordBuilder<T> encodeProperty(TelemetryPropertyMap p_262128_, RecordBuilder<T> p_261947_, TelemetryProperty<V> p_261911_) {
                V $$3 = p_262128_.get(p_261911_);
                return $$3 != null ? p_261947_.add(p_261911_.id(), $$3, p_261911_.codec()) : p_261947_;
            }

            public <T> DataResult<TelemetryPropertyMap> decode(DynamicOps<T> p_261767_, MapLike<T> p_262176_) {
                DataResult<Builder> $$2 = DataResult.success(new Builder());

                TelemetryProperty $$3;
                for(Iterator var4 = p_262139_.iterator(); var4.hasNext(); $$2 = this.decodeProperty($$2, p_261767_, p_262176_, $$3)) {
                    $$3 = (TelemetryProperty)var4.next();
                }

                return $$2.map(Builder::build);
            }

            private <T, V> DataResult<Builder> decodeProperty(DataResult<Builder> p_261892_, DynamicOps<T> p_261859_, MapLike<T> p_261668_, TelemetryProperty<V> p_261627_) {
                T $$4 = p_261668_.get(p_261627_.id());
                if ($$4 != null) {
                    DataResult<V> $$5 = p_261627_.codec().parse(p_261859_, $$4);
                    return p_261892_.apply2stable((p_262028_, p_261796_) -> {
                        return p_262028_.put(p_261627_, p_261796_);
                    }, $$5);
                } else {
                    return p_261892_;
                }
            }

            public <T> Stream<T> keys(DynamicOps<T> p_261746_) {
                Stream var10000 = p_262139_.stream().map(TelemetryProperty::id);
                Objects.requireNonNull(p_261746_);
                return var10000.map(p_261746_::createString);
            }
        }).codec();
    }

    @Nullable
    public <T> T get(TelemetryProperty<T> p_261667_) {
        return this.entries.get(p_261667_);
    }

    public String toString() {
        return this.entries.toString();
    }

    public Set<TelemetryProperty<?>> propertySet() {
        return this.entries.keySet();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        private final Map<TelemetryProperty<?>, Object> entries = new Reference2ObjectOpenHashMap();

        Builder() {
        }

        public <T> Builder put(TelemetryProperty<T> p_261681_, T p_262093_) {
            this.entries.put(p_261681_, p_262093_);
            return this;
        }

        public <T> Builder putIfNotNull(TelemetryProperty<T> p_286534_, @Nullable T p_286699_) {
            if (p_286699_ != null) {
                this.entries.put(p_286534_, p_286699_);
            }

            return this;
        }

        public Builder putAll(TelemetryPropertyMap p_261779_) {
            this.entries.putAll(p_261779_.entries);
            return this;
        }

        public TelemetryPropertyMap build() {
            return new TelemetryPropertyMap(this.entries);
        }
    }
}
