//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SpawnArmorTrimsCommand {
    private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM = (Map)Util.make(Maps.newHashMap(), (p_266706_) -> {
        p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.HEAD), Items.CHAINMAIL_HELMET);
        p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.CHEST), Items.CHAINMAIL_CHESTPLATE);
        p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.LEGS), Items.CHAINMAIL_LEGGINGS);
        p_266706_.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.FEET), Items.CHAINMAIL_BOOTS);
        p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.HEAD), Items.IRON_HELMET);
        p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.CHEST), Items.IRON_CHESTPLATE);
        p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.LEGS), Items.IRON_LEGGINGS);
        p_266706_.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.FEET), Items.IRON_BOOTS);
        p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.HEAD), Items.GOLDEN_HELMET);
        p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.CHEST), Items.GOLDEN_CHESTPLATE);
        p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.LEGS), Items.GOLDEN_LEGGINGS);
        p_266706_.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.FEET), Items.GOLDEN_BOOTS);
        p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD), Items.NETHERITE_HELMET);
        p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST), Items.NETHERITE_CHESTPLATE);
        p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS), Items.NETHERITE_LEGGINGS);
        p_266706_.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.FEET), Items.NETHERITE_BOOTS);
        p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD), Items.DIAMOND_HELMET);
        p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE);
        p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS);
        p_266706_.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
        p_266706_.put(Pair.of(ArmorMaterials.TURTLE, EquipmentSlot.HEAD), Items.TURTLE_HELMET);
    });
    private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS;
    private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS;
    private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER;
    private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER;

    public SpawnArmorTrimsCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_266758_) {
        p_266758_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawn_armor_trims").requires((p_277270_) -> {
            return p_277270_.hasPermission(2);
        })).executes((p_267005_) -> {
            return spawnArmorTrims((CommandSourceStack)p_267005_.getSource(), ((CommandSourceStack)p_267005_.getSource()).getPlayerOrException());
        }));
    }

    private static int spawnArmorTrims(CommandSourceStack p_266993_, Player p_266983_) {
        Level $$2 = p_266983_.level();
        NonNullList<ArmorTrim> $$3 = NonNullList.create();
        Registry<TrimPattern> $$4 = $$2.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
        Registry<TrimMaterial> $$5 = $$2.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
        $$4.stream().sorted(Comparator.comparing((p_266941_) -> {
            return TRIM_PATTERN_ORDER.applyAsInt((ResourceKey)$$4.getResourceKey(p_266941_).orElse((Object)null));
        })).forEachOrdered((p_266759_) -> {
            $$5.stream().sorted(Comparator.comparing((p_267239_) -> {
                return TRIM_MATERIAL_ORDER.applyAsInt((ResourceKey)$$5.getResourceKey(p_267239_).orElse((Object)null));
            })).forEachOrdered((p_267162_) -> {
                $$3.add(new ArmorTrim($$5.wrapAsHolder(p_267162_), $$4.wrapAsHolder(p_266759_)));
            });
        });
        BlockPos $$6 = p_266983_.blockPosition().relative((Direction)p_266983_.getDirection(), 5);
        int $$7 = ArmorMaterials.values().length - 1;
        double $$8 = 3.0;
        int $$9 = 0;
        int $$10 = 0;

        for(Iterator var12 = $$3.iterator(); var12.hasNext(); ++$$9) {
            ArmorTrim $$11 = (ArmorTrim)var12.next();
            ArmorMaterials[] var14 = ArmorMaterials.values();
            int var15 = var14.length;

            for(int var16 = 0; var16 < var15; ++var16) {
                ArmorMaterial $$12 = var14[var16];
                if ($$12 != ArmorMaterials.LEATHER) {
                    double $$13 = (double)$$6.getX() + 0.5 - (double)($$9 % $$5.size()) * 3.0;
                    double $$14 = (double)$$6.getY() + 0.5 + (double)($$10 % $$7) * 3.0;
                    double $$15 = (double)$$6.getZ() + 0.5 + (double)($$9 / $$5.size() * 10);
                    ArmorStand $$16 = new ArmorStand($$2, $$13, $$14, $$15);
                    $$16.setYRot(180.0F);
                    $$16.setNoGravity(true);
                    EquipmentSlot[] var25 = EquipmentSlot.values();
                    int var26 = var25.length;

                    for(int var27 = 0; var27 < var26; ++var27) {
                        EquipmentSlot $$17 = var25[var27];
                        Item $$18 = (Item)MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of($$12, $$17));
                        if ($$18 != null) {
                            ItemStack $$19 = new ItemStack($$18);
                            ArmorTrim.setTrim($$2.registryAccess(), $$19, $$11);
                            $$16.setItemSlot($$17, $$19);
                            if ($$18 instanceof ArmorItem) {
                                ArmorItem $$20 = (ArmorItem)$$18;
                                if ($$20.getMaterial() == ArmorMaterials.TURTLE) {
                                    $$16.setCustomName(((TrimPattern)$$11.pattern().value()).copyWithStyle($$11.material()).copy().append(" ").append(((TrimMaterial)$$11.material().value()).description()));
                                    $$16.setCustomNameVisible(true);
                                    continue;
                                }
                            }

                            $$16.setInvisible(true);
                        }
                    }

                    $$2.addFreshEntity($$16);
                    ++$$10;
                }
            }
        }

        p_266993_.sendSuccess(() -> {
            return Component.literal("Armorstands with trimmed armor spawned around you");
        }, true);
        return 1;
    }

    static {
        VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE, TrimPatterns.WAYFINDER, TrimPatterns.SHAPER, TrimPatterns.SILENCE, TrimPatterns.RAISER, TrimPatterns.HOST);
        VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
        TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
        TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);
    }
}
