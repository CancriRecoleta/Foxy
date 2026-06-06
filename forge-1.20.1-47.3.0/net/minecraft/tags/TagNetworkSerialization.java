//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.Holder.Kind;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;

public class TagNetworkSerialization {
    public TagNetworkSerialization() {
    }

    public static Map<ResourceKey<? extends Registry<?>>, NetworkPayload> serializeTagsToNetwork(LayeredRegistryAccess<RegistryLayer> p_251774_) {
        return (Map)RegistrySynchronization.networkSafeRegistries(p_251774_).map((p_203949_) -> {
            return Pair.of(p_203949_.key(), serializeToNetwork(p_203949_.value()));
        }).filter((p_203941_) -> {
            return !((NetworkPayload)p_203941_.getSecond()).isEmpty();
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> NetworkPayload serializeToNetwork(Registry<T> p_203943_) {
        Map<ResourceLocation, IntList> $$1 = new HashMap();
        p_203943_.getTags().forEach((p_203947_) -> {
            HolderSet<T> $$3 = (HolderSet)p_203947_.getSecond();
            IntList $$4 = new IntArrayList($$3.size());
            Iterator var5 = $$3.iterator();

            while(var5.hasNext()) {
                Holder<T> $$5 = (Holder)var5.next();
                if ($$5.kind() != Kind.REFERENCE) {
                    throw new IllegalStateException("Can't serialize unregistered value " + $$5);
                }

                $$4.add(p_203943_.getId($$5.value()));
            }

            $$1.put(((TagKey)p_203947_.getFirst()).location(), $$4);
        });
        return new NetworkPayload($$1);
    }

    public static <T> void deserializeTagsFromNetwork(ResourceKey<? extends Registry<T>> p_203953_, Registry<T> p_203954_, NetworkPayload p_203955_, TagOutput<T> p_203956_) {
        p_203955_.tags.forEach((p_248278_, p_248279_) -> {
            TagKey<T> $$5 = TagKey.create(p_203953_, p_248278_);
            IntStream var10000 = p_248279_.intStream();
            Objects.requireNonNull(p_203954_);
            List<Holder<T>> $$6 = (List)var10000.mapToObj(p_203954_::getHolder).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
            p_203956_.accept($$5, $$6);
        });
    }

    public static final class NetworkPayload {
        final Map<ResourceLocation, IntList> tags;

        NetworkPayload(Map<ResourceLocation, IntList> p_203965_) {
            this.tags = p_203965_;
        }

        public void write(FriendlyByteBuf p_203968_) {
            p_203968_.writeMap(this.tags, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeIntIdList);
        }

        public static NetworkPayload read(FriendlyByteBuf p_203970_) {
            return new NetworkPayload(p_203970_.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readIntIdList));
        }

        public boolean isEmpty() {
            return this.tags.isEmpty();
        }
    }

    @FunctionalInterface
    public interface TagOutput<T> {
        void accept(TagKey<T> var1, List<Holder<T>> var2);
    }
}
