//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ContextNbtProvider implements NbtProvider {
    private static final String BLOCK_ENTITY_ID = "block_entity";
    private static final Getter BLOCK_ENTITY_PROVIDER = new Getter() {
        public Tag get(LootContext p_165582_) {
            BlockEntity blockentity = (BlockEntity)p_165582_.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            return blockentity != null ? blockentity.saveWithFullMetadata() : null;
        }

        public String getId() {
            return "block_entity";
        }

        public Set<LootContextParam<?>> getReferencedContextParams() {
            return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
        }
    };
    public static final ContextNbtProvider BLOCK_ENTITY;
    final Getter getter;

    private static Getter forEntity(final LootContext.EntityTarget p_165578_) {
        return new Getter() {
            @Nullable
            public Tag get(LootContext p_165589_) {
                Entity entity = (Entity)p_165589_.getParamOrNull(p_165578_.getParam());
                return entity != null ? NbtPredicate.getEntityTagToCompare(entity) : null;
            }

            public String getId() {
                return p_165578_.getName();
            }

            public Set<LootContextParam<?>> getReferencedContextParams() {
                return ImmutableSet.of(p_165578_.getParam());
            }
        };
    }

    private ContextNbtProvider(Getter p_165568_) {
        this.getter = p_165568_;
    }

    public LootNbtProviderType getType() {
        return NbtProviders.CONTEXT;
    }

    @Nullable
    public Tag get(LootContext p_165573_) {
        return this.getter.get(p_165573_);
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.getter.getReferencedContextParams();
    }

    public static NbtProvider forContextEntity(LootContext.EntityTarget p_165571_) {
        return new ContextNbtProvider(forEntity(p_165571_));
    }

    static ContextNbtProvider createFromContext(String p_165575_) {
        if (p_165575_.equals("block_entity")) {
            return new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
        } else {
            LootContext.EntityTarget lootcontext$entitytarget = EntityTarget.getByName(p_165575_);
            return new ContextNbtProvider(forEntity(lootcontext$entitytarget));
        }
    }

    static {
        BLOCK_ENTITY = new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
    }

    interface Getter {
        @Nullable
        Tag get(LootContext var1);

        String getId();

        Set<LootContextParam<?>> getReferencedContextParams();
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ContextNbtProvider> {
        public Serializer() {
        }

        public void serialize(JsonObject p_165610_, ContextNbtProvider p_165611_, JsonSerializationContext p_165612_) {
            p_165610_.addProperty("target", p_165611_.getter.getId());
        }

        public ContextNbtProvider deserialize(JsonObject p_165618_, JsonDeserializationContext p_165619_) {
            String s = GsonHelper.getAsString(p_165618_, "target");
            return ContextNbtProvider.createFromContext(s);
        }
    }

    public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ContextNbtProvider> {
        public InlineSerializer() {
        }

        public JsonElement serialize(ContextNbtProvider p_165597_, JsonSerializationContext p_165598_) {
            return new JsonPrimitive(p_165597_.getter.getId());
        }

        public ContextNbtProvider deserialize(JsonElement p_165603_, JsonDeserializationContext p_165604_) {
            String s = p_165603_.getAsString();
            return ContextNbtProvider.createFromContext(s);
        }
    }
}
