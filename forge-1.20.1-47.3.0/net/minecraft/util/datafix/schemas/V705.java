//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V705 extends NamespacedSchema {
    protected static final Hook.HookFunction ADD_NAMES = new Hook.HookFunction() {
        public <T> T apply(DynamicOps<T> p_18167_, T p_18168_) {
            return V99.addNames(new Dynamic(p_18167_, p_18168_), V704.ITEM_TO_BLOCKENTITY, "minecraft:armor_stand");
        }
    };

    public V705(int p_18075_, Schema p_18076_) {
        super(p_18075_, p_18076_);
    }

    protected static void registerMob(Schema p_18083_, Map<String, Supplier<TypeTemplate>> p_18084_, String p_18085_) {
        p_18083_.register(p_18084_, p_18085_, () -> {
            return V100.equipment(p_18083_);
        });
    }

    protected static void registerThrowableProjectile(Schema p_18094_, Map<String, Supplier<TypeTemplate>> p_18095_, String p_18096_) {
        p_18094_.register(p_18095_, p_18096_, () -> {
            return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18094_));
        });
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_18148_) {
        Map<String, Supplier<TypeTemplate>> $$1 = Maps.newHashMap();
        p_18148_.registerSimple($$1, "minecraft:area_effect_cloud");
        registerMob(p_18148_, $$1, "minecraft:armor_stand");
        p_18148_.register($$1, "minecraft:arrow", (p_18164_) -> {
            return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:bat");
        registerMob(p_18148_, $$1, "minecraft:blaze");
        p_18148_.registerSimple($$1, "minecraft:boat");
        registerMob(p_18148_, $$1, "minecraft:cave_spider");
        p_18148_.register($$1, "minecraft:chest_minecart", (p_18161_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_), "Items", DSL.list(References.ITEM_STACK.in(p_18148_)));
        });
        registerMob(p_18148_, $$1, "minecraft:chicken");
        p_18148_.register($$1, "minecraft:commandblock_minecart", (p_18158_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:cow");
        registerMob(p_18148_, $$1, "minecraft:creeper");
        p_18148_.register($$1, "minecraft:donkey", (p_18155_) -> {
            return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_18148_)), "SaddleItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        p_18148_.registerSimple($$1, "minecraft:dragon_fireball");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:egg");
        registerMob(p_18148_, $$1, "minecraft:elder_guardian");
        p_18148_.registerSimple($$1, "minecraft:ender_crystal");
        registerMob(p_18148_, $$1, "minecraft:ender_dragon");
        p_18148_.register($$1, "minecraft:enderman", (p_18146_) -> {
            return DSL.optionalFields("carried", References.BLOCK_NAME.in(p_18148_), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:endermite");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:ender_pearl");
        p_18148_.registerSimple($$1, "minecraft:eye_of_ender_signal");
        p_18148_.register($$1, "minecraft:falling_block", (p_18143_) -> {
            return DSL.optionalFields("Block", References.BLOCK_NAME.in(p_18148_), "TileEntityData", References.BLOCK_ENTITY.in(p_18148_));
        });
        registerThrowableProjectile(p_18148_, $$1, "minecraft:fireball");
        p_18148_.register($$1, "minecraft:fireworks_rocket", (p_18140_) -> {
            return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(p_18148_));
        });
        p_18148_.register($$1, "minecraft:furnace_minecart", (p_18137_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:ghast");
        registerMob(p_18148_, $$1, "minecraft:giant");
        registerMob(p_18148_, $$1, "minecraft:guardian");
        p_18148_.register($$1, "minecraft:hopper_minecart", (p_18134_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_), "Items", DSL.list(References.ITEM_STACK.in(p_18148_)));
        });
        p_18148_.register($$1, "minecraft:horse", (p_18131_) -> {
            return DSL.optionalFields("ArmorItem", References.ITEM_STACK.in(p_18148_), "SaddleItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:husk");
        p_18148_.register($$1, "minecraft:item", (p_18128_) -> {
            return DSL.optionalFields("Item", References.ITEM_STACK.in(p_18148_));
        });
        p_18148_.register($$1, "minecraft:item_frame", (p_18125_) -> {
            return DSL.optionalFields("Item", References.ITEM_STACK.in(p_18148_));
        });
        p_18148_.registerSimple($$1, "minecraft:leash_knot");
        registerMob(p_18148_, $$1, "minecraft:magma_cube");
        p_18148_.register($$1, "minecraft:minecart", (p_18122_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:mooshroom");
        p_18148_.register($$1, "minecraft:mule", (p_18119_) -> {
            return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_18148_)), "SaddleItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:ocelot");
        p_18148_.registerSimple($$1, "minecraft:painting");
        p_18148_.registerSimple($$1, "minecraft:parrot");
        registerMob(p_18148_, $$1, "minecraft:pig");
        registerMob(p_18148_, $$1, "minecraft:polar_bear");
        p_18148_.register($$1, "minecraft:potion", (p_18116_) -> {
            return DSL.optionalFields("Potion", References.ITEM_STACK.in(p_18148_), "inTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:rabbit");
        registerMob(p_18148_, $$1, "minecraft:sheep");
        registerMob(p_18148_, $$1, "minecraft:shulker");
        p_18148_.registerSimple($$1, "minecraft:shulker_bullet");
        registerMob(p_18148_, $$1, "minecraft:silverfish");
        registerMob(p_18148_, $$1, "minecraft:skeleton");
        p_18148_.register($$1, "minecraft:skeleton_horse", (p_18113_) -> {
            return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:slime");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:small_fireball");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:snowball");
        registerMob(p_18148_, $$1, "minecraft:snowman");
        p_18148_.register($$1, "minecraft:spawner_minecart", (p_18110_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_), References.UNTAGGED_SPAWNER.in(p_18148_));
        });
        p_18148_.register($$1, "minecraft:spectral_arrow", (p_18107_) -> {
            return DSL.optionalFields("inTile", References.BLOCK_NAME.in(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:spider");
        registerMob(p_18148_, $$1, "minecraft:squid");
        registerMob(p_18148_, $$1, "minecraft:stray");
        p_18148_.registerSimple($$1, "minecraft:tnt");
        p_18148_.register($$1, "minecraft:tnt_minecart", (p_18104_) -> {
            return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(p_18148_));
        });
        p_18148_.register($$1, "minecraft:villager", (p_18101_) -> {
            return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(p_18148_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(p_18148_), "buyB", References.ITEM_STACK.in(p_18148_), "sell", References.ITEM_STACK.in(p_18148_)))), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:villager_golem");
        registerMob(p_18148_, $$1, "minecraft:witch");
        registerMob(p_18148_, $$1, "minecraft:wither");
        registerMob(p_18148_, $$1, "minecraft:wither_skeleton");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:wither_skull");
        registerMob(p_18148_, $$1, "minecraft:wolf");
        registerThrowableProjectile(p_18148_, $$1, "minecraft:xp_bottle");
        p_18148_.registerSimple($$1, "minecraft:xp_orb");
        registerMob(p_18148_, $$1, "minecraft:zombie");
        p_18148_.register($$1, "minecraft:zombie_horse", (p_18092_) -> {
            return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        registerMob(p_18148_, $$1, "minecraft:zombie_pigman");
        registerMob(p_18148_, $$1, "minecraft:zombie_villager");
        p_18148_.registerSimple($$1, "minecraft:evocation_fangs");
        registerMob(p_18148_, $$1, "minecraft:evocation_illager");
        p_18148_.registerSimple($$1, "minecraft:illusion_illager");
        p_18148_.register($$1, "minecraft:llama", (p_18081_) -> {
            return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(p_18148_)), "SaddleItem", References.ITEM_STACK.in(p_18148_), "DecorItem", References.ITEM_STACK.in(p_18148_), V100.equipment(p_18148_));
        });
        p_18148_.registerSimple($$1, "minecraft:llama_spit");
        registerMob(p_18148_, $$1, "minecraft:vex");
        registerMob(p_18148_, $$1, "minecraft:vindication_illager");
        return $$1;
    }

    public void registerTypes(Schema p_18150_, Map<String, Supplier<TypeTemplate>> p_18151_, Map<String, Supplier<TypeTemplate>> p_18152_) {
        super.registerTypes(p_18150_, p_18151_, p_18152_);
        p_18150_.registerType(true, References.ENTITY, () -> {
            return DSL.taggedChoiceLazy("id", namespacedString(), p_18151_);
        });
        p_18150_.registerType(true, References.ITEM_STACK, () -> {
            return DSL.hook(DSL.optionalFields("id", References.ITEM_NAME.in(p_18150_), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(p_18150_), "BlockEntityTag", References.BLOCK_ENTITY.in(p_18150_), "CanDestroy", DSL.list(References.BLOCK_NAME.in(p_18150_)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(p_18150_)), "Items", DSL.list(References.ITEM_STACK.in(p_18150_)))), ADD_NAMES, HookFunction.IDENTITY);
        });
    }
}
