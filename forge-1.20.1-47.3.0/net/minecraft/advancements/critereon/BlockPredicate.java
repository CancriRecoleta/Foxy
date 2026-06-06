//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPredicate {
    public static final BlockPredicate ANY;
    @Nullable
    private final TagKey<Block> tag;
    @Nullable
    private final Set<Block> blocks;
    private final StatePropertiesPredicate properties;
    private final NbtPredicate nbt;

    public BlockPredicate(@Nullable TagKey<Block> p_204023_, @Nullable Set<Block> p_204024_, StatePropertiesPredicate p_204025_, NbtPredicate p_204026_) {
        this.tag = p_204023_;
        this.blocks = p_204024_;
        this.properties = p_204025_;
        this.nbt = p_204026_;
    }

    public boolean matches(ServerLevel p_17915_, BlockPos p_17916_) {
        if (this == ANY) {
            return true;
        } else if (!p_17915_.isLoaded(p_17916_)) {
            return false;
        } else {
            BlockState $$2 = p_17915_.getBlockState(p_17916_);
            if (this.tag != null && !$$2.is(this.tag)) {
                return false;
            } else if (this.blocks != null && !this.blocks.contains($$2.getBlock())) {
                return false;
            } else if (!this.properties.matches($$2)) {
                return false;
            } else {
                if (this.nbt != NbtPredicate.ANY) {
                    BlockEntity $$3 = p_17915_.getBlockEntity(p_17916_);
                    if ($$3 == null || !this.nbt.matches((Tag)$$3.saveWithFullMetadata())) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static BlockPredicate fromJson(@Nullable JsonElement p_17918_) {
        if (p_17918_ != null && !p_17918_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_17918_, "block");
            NbtPredicate $$2 = NbtPredicate.fromJson($$1.get("nbt"));
            Set<Block> $$3 = null;
            JsonArray $$4 = GsonHelper.getAsJsonArray($$1, "blocks", (JsonArray)null);
            if ($$4 != null) {
                ImmutableSet.Builder<Block> $$5 = ImmutableSet.builder();
                Iterator var6 = $$4.iterator();

                while(var6.hasNext()) {
                    JsonElement $$6 = (JsonElement)var6.next();
                    ResourceLocation $$7 = new ResourceLocation(GsonHelper.convertToString($$6, "block"));
                    $$5.add((Block)BuiltInRegistries.BLOCK.getOptional($$7).orElseThrow(() -> {
                        return new JsonSyntaxException("Unknown block id '" + $$7 + "'");
                    }));
                }

                $$3 = $$5.build();
            }

            TagKey<Block> $$8 = null;
            if ($$1.has("tag")) {
                ResourceLocation $$9 = new ResourceLocation(GsonHelper.getAsString($$1, "tag"));
                $$8 = TagKey.create(Registries.BLOCK, $$9);
            }

            StatePropertiesPredicate $$10 = StatePropertiesPredicate.fromJson($$1.get("state"));
            return new BlockPredicate($$8, $$3, $$10, $$2);
        } else {
            return ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = new JsonObject();
            if (this.blocks != null) {
                JsonArray $$1 = new JsonArray();
                Iterator var3 = this.blocks.iterator();

                while(var3.hasNext()) {
                    Block $$2 = (Block)var3.next();
                    $$1.add(BuiltInRegistries.BLOCK.getKey($$2).toString());
                }

                $$0.add("blocks", $$1);
            }

            if (this.tag != null) {
                $$0.addProperty("tag", this.tag.location().toString());
            }

            $$0.add("nbt", this.nbt.serializeToJson());
            $$0.add("state", this.properties.serializeToJson());
            return $$0;
        }
    }

    static {
        ANY = new BlockPredicate((TagKey)null, (Set)null, StatePropertiesPredicate.ANY, NbtPredicate.ANY);
    }

    public static class Builder {
        @Nullable
        private Set<Block> blocks;
        @Nullable
        private TagKey<Block> tag;
        private StatePropertiesPredicate properties;
        private NbtPredicate nbt;

        private Builder() {
            this.properties = StatePropertiesPredicate.ANY;
            this.nbt = NbtPredicate.ANY;
        }

        public static Builder block() {
            return new Builder();
        }

        public Builder of(Block... p_146727_) {
            this.blocks = ImmutableSet.copyOf(p_146727_);
            return this;
        }

        public Builder of(Iterable<Block> p_146723_) {
            this.blocks = ImmutableSet.copyOf(p_146723_);
            return this;
        }

        public Builder of(TagKey<Block> p_204028_) {
            this.tag = p_204028_;
            return this;
        }

        public Builder hasNbt(CompoundTag p_146725_) {
            this.nbt = new NbtPredicate(p_146725_);
            return this;
        }

        public Builder setProperties(StatePropertiesPredicate p_17930_) {
            this.properties = p_17930_;
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.tag, this.blocks, this.properties, this.nbt);
        }
    }
}
