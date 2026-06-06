//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

public class CopyNbtFunction extends LootItemConditionalFunction {
    final NbtProvider source;
    final List<CopyOperation> operations;

    CopyNbtFunction(LootItemCondition[] p_165175_, NbtProvider p_165176_, List<CopyOperation> p_165177_) {
        super(p_165175_);
        this.source = p_165176_;
        this.operations = ImmutableList.copyOf(p_165177_);
    }

    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NBT;
    }

    static NbtPathArgument.NbtPath compileNbtPath(String p_80268_) {
        try {
            return (new NbtPathArgument()).parse(new StringReader(p_80268_));
        } catch (CommandSyntaxException var2) {
            CommandSyntaxException $$1 = var2;
            throw new IllegalArgumentException("Failed to parse path " + p_80268_, $$1);
        }
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.source.getReferencedContextParams();
    }

    public ItemStack run(ItemStack p_80250_, LootContext p_80251_) {
        Tag $$2 = this.source.get(p_80251_);
        if ($$2 != null) {
            this.operations.forEach((p_80255_) -> {
                Objects.requireNonNull(p_80250_);
                p_80255_.apply(p_80250_::getOrCreateTag, $$2);
            });
        }

        return p_80250_;
    }

    public static Builder copyData(NbtProvider p_165181_) {
        return new Builder(p_165181_);
    }

    public static Builder copyData(LootContext.EntityTarget p_165179_) {
        return new Builder(ContextNbtProvider.forContextEntity(p_165179_));
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final NbtProvider source;
        private final List<CopyOperation> ops = Lists.newArrayList();

        Builder(NbtProvider p_165183_) {
            this.source = p_165183_;
        }

        public Builder copy(String p_80283_, String p_80284_, MergeStrategy p_80285_) {
            this.ops.add(new CopyOperation(p_80283_, p_80284_, p_80285_));
            return this;
        }

        public Builder copy(String p_80280_, String p_80281_) {
            return this.copy(p_80280_, p_80281_, net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.MergeStrategy.REPLACE);
        }

        protected Builder getThis() {
            return this;
        }

        public LootItemFunction build() {
            return new CopyNbtFunction(this.getConditions(), this.source, this.ops);
        }
    }

    static class CopyOperation {
        private final String sourcePathText;
        private final NbtPathArgument.NbtPath sourcePath;
        private final String targetPathText;
        private final NbtPathArgument.NbtPath targetPath;
        private final MergeStrategy op;

        CopyOperation(String p_80294_, String p_80295_, MergeStrategy p_80296_) {
            this.sourcePathText = p_80294_;
            this.sourcePath = CopyNbtFunction.compileNbtPath(p_80294_);
            this.targetPathText = p_80295_;
            this.targetPath = CopyNbtFunction.compileNbtPath(p_80295_);
            this.op = p_80296_;
        }

        public void apply(Supplier<Tag> p_80306_, Tag p_80307_) {
            try {
                List<Tag> $$2 = this.sourcePath.get(p_80307_);
                if (!$$2.isEmpty()) {
                    this.op.merge((Tag)p_80306_.get(), this.targetPath, $$2);
                }
            } catch (CommandSyntaxException var4) {
            }

        }

        public JsonObject toJson() {
            JsonObject $$0 = new JsonObject();
            $$0.addProperty("source", this.sourcePathText);
            $$0.addProperty("target", this.targetPathText);
            $$0.addProperty("op", this.op.name);
            return $$0;
        }

        public static CopyOperation fromJson(JsonObject p_80304_) {
            String $$1 = GsonHelper.getAsString(p_80304_, "source");
            String $$2 = GsonHelper.getAsString(p_80304_, "target");
            MergeStrategy $$3 = net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.MergeStrategy.getByName(GsonHelper.getAsString(p_80304_, "op"));
            return new CopyOperation($$1, $$2, $$3);
        }
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyNbtFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject p_80399_, CopyNbtFunction p_80400_, JsonSerializationContext p_80401_) {
            super.serialize(p_80399_, (LootItemConditionalFunction)p_80400_, p_80401_);
            p_80399_.add("source", p_80401_.serialize(p_80400_.source));
            JsonArray $$3 = new JsonArray();
            Stream var10000 = p_80400_.operations.stream().map(CopyOperation::toJson);
            Objects.requireNonNull($$3);
            var10000.forEach($$3::add);
            p_80399_.add("ops", $$3);
        }

        public CopyNbtFunction deserialize(JsonObject p_80395_, JsonDeserializationContext p_80396_, LootItemCondition[] p_80397_) {
            NbtProvider $$3 = (NbtProvider)GsonHelper.getAsObject(p_80395_, "source", p_80396_, NbtProvider.class);
            List<CopyOperation> $$4 = Lists.newArrayList();
            JsonArray $$5 = GsonHelper.getAsJsonArray(p_80395_, "ops");
            Iterator var7 = $$5.iterator();

            while(var7.hasNext()) {
                JsonElement $$6 = (JsonElement)var7.next();
                JsonObject $$7 = GsonHelper.convertToJsonObject($$6, "op");
                $$4.add(net.minecraft.world.level.storage.loot.functions.CopyNbtFunction.CopyOperation.fromJson($$7));
            }

            return new CopyNbtFunction(p_80397_, $$3, $$4);
        }
    }

    public static enum MergeStrategy {
        REPLACE("replace") {
            public void merge(Tag p_80362_, NbtPathArgument.NbtPath p_80363_, List<Tag> p_80364_) throws CommandSyntaxException {
                p_80363_.set(p_80362_, (Tag)Iterables.getLast(p_80364_));
            }
        },
        APPEND("append") {
            public void merge(Tag p_80373_, NbtPathArgument.NbtPath p_80374_, List<Tag> p_80375_) throws CommandSyntaxException {
                List<Tag> $$3 = p_80374_.getOrCreate(p_80373_, ListTag::new);
                $$3.forEach((p_80371_) -> {
                    if (p_80371_ instanceof ListTag) {
                        p_80375_.forEach((p_165187_) -> {
                            ((ListTag)p_80371_).add(p_165187_.copy());
                        });
                    }

                });
            }
        },
        MERGE("merge") {
            public void merge(Tag p_80387_, NbtPathArgument.NbtPath p_80388_, List<Tag> p_80389_) throws CommandSyntaxException {
                List<Tag> $$3 = p_80388_.getOrCreate(p_80387_, CompoundTag::new);
                $$3.forEach((p_80385_) -> {
                    if (p_80385_ instanceof CompoundTag) {
                        p_80389_.forEach((p_165190_) -> {
                            if (p_165190_ instanceof CompoundTag) {
                                ((CompoundTag)p_80385_).merge((CompoundTag)p_165190_);
                            }

                        });
                    }

                });
            }
        };

        final String name;

        public abstract void merge(Tag var1, NbtPathArgument.NbtPath var2, List<Tag> var3) throws CommandSyntaxException;

        MergeStrategy(String p_80341_) {
            this.name = p_80341_;
        }

        public static MergeStrategy getByName(String p_80350_) {
            MergeStrategy[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                MergeStrategy $$1 = var1[var3];
                if ($$1.name.equals(p_80350_)) {
                    return $$1;
                }
            }

            throw new IllegalArgumentException("Invalid merge strategy" + p_80350_);
        }
    }
}
