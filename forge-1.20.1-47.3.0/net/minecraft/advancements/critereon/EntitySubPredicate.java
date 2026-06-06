//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.MushroomCow.MushroomType;
import net.minecraft.world.entity.animal.TropicalFish.Pattern;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public interface EntitySubPredicate {
    EntitySubPredicate ANY = new EntitySubPredicate() {
        public boolean matches(Entity p_218841_, ServerLevel p_218842_, @Nullable Vec3 p_218843_) {
            return true;
        }

        public JsonObject serializeCustomData() {
            return new JsonObject();
        }

        public Type type() {
            return net.minecraft.advancements.critereon.EntitySubPredicate.Types.ANY;
        }
    };

    static EntitySubPredicate fromJson(@Nullable JsonElement p_218836_) {
        if (p_218836_ != null && !p_218836_.isJsonNull()) {
            JsonObject $$1 = GsonHelper.convertToJsonObject(p_218836_, "type_specific");
            String $$2 = GsonHelper.getAsString($$1, "type", (String)null);
            if ($$2 == null) {
                return ANY;
            } else {
                Type $$3 = (Type)net.minecraft.advancements.critereon.EntitySubPredicate.Types.TYPES.get($$2);
                if ($$3 == null) {
                    throw new JsonSyntaxException("Unknown sub-predicate type: " + $$2);
                } else {
                    return $$3.deserialize($$1);
                }
            }
        } else {
            return ANY;
        }
    }

    boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3);

    JsonObject serializeCustomData();

    default JsonElement serialize() {
        if (this.type() == net.minecraft.advancements.critereon.EntitySubPredicate.Types.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject $$0 = this.serializeCustomData();
            String $$1 = (String)net.minecraft.advancements.critereon.EntitySubPredicate.Types.TYPES.inverse().get(this.type());
            $$0.addProperty("type", $$1);
            return $$0;
        }
    }

    Type type();

    static EntitySubPredicate variant(CatVariant p_218832_) {
        return net.minecraft.advancements.critereon.EntitySubPredicate.Types.CAT.createPredicate(p_218832_);
    }

    static EntitySubPredicate variant(FrogVariant p_218834_) {
        return net.minecraft.advancements.critereon.EntitySubPredicate.Types.FROG.createPredicate(p_218834_);
    }

    public static final class Types {
        public static final Type ANY = (p_218860_) -> {
            return EntitySubPredicate.ANY;
        };
        public static final Type LIGHTNING = LighthingBoltPredicate::fromJson;
        public static final Type FISHING_HOOK = FishingHookPredicate::fromJson;
        public static final Type PLAYER = PlayerPredicate::fromJson;
        public static final Type SLIME = SlimePredicate::fromJson;
        public static final EntityVariantPredicate<CatVariant> CAT;
        public static final EntityVariantPredicate<FrogVariant> FROG;
        public static final EntityVariantPredicate<Axolotl.Variant> AXOLOTL;
        public static final EntityVariantPredicate<Boat.Type> BOAT;
        public static final EntityVariantPredicate<Fox.Type> FOX;
        public static final EntityVariantPredicate<MushroomCow.MushroomType> MOOSHROOM;
        public static final EntityVariantPredicate<Holder<PaintingVariant>> PAINTING;
        public static final EntityVariantPredicate<Rabbit.Variant> RABBIT;
        public static final EntityVariantPredicate<Variant> HORSE;
        public static final EntityVariantPredicate<Llama.Variant> LLAMA;
        public static final EntityVariantPredicate<VillagerType> VILLAGER;
        public static final EntityVariantPredicate<Parrot.Variant> PARROT;
        public static final EntityVariantPredicate<TropicalFish.Pattern> TROPICAL_FISH;
        public static final BiMap<String, Type> TYPES;

        public Types() {
        }

        static {
            CAT = EntityVariantPredicate.create(BuiltInRegistries.CAT_VARIANT, (p_218862_) -> {
                Optional var10000;
                if (p_218862_ instanceof Cat $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            FROG = EntityVariantPredicate.create(BuiltInRegistries.FROG_VARIANT, (p_218858_) -> {
                Optional var10000;
                if (p_218858_ instanceof Frog $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            AXOLOTL = EntityVariantPredicate.create(net.minecraft.world.entity.animal.axolotl.Axolotl.Variant.CODEC, (p_262508_) -> {
                Optional var10000;
                if (p_262508_ instanceof Axolotl $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            BOAT = EntityVariantPredicate.create((Codec)net.minecraft.world.entity.vehicle.Boat.Type.CODEC, (p_262507_) -> {
                Optional var10000;
                if (p_262507_ instanceof Boat $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            FOX = EntityVariantPredicate.create((Codec)net.minecraft.world.entity.animal.Fox.Type.CODEC, (p_262510_) -> {
                Optional var10000;
                if (p_262510_ instanceof Fox $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            MOOSHROOM = EntityVariantPredicate.create((Codec)MushroomType.CODEC, (p_262513_) -> {
                Optional var10000;
                if (p_262513_ instanceof MushroomCow $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            PAINTING = EntityVariantPredicate.create(BuiltInRegistries.PAINTING_VARIANT.holderByNameCodec(), (p_262509_) -> {
                Optional var10000;
                if (p_262509_ instanceof Painting $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            RABBIT = EntityVariantPredicate.create(net.minecraft.world.entity.animal.Rabbit.Variant.CODEC, (p_262511_) -> {
                Optional var10000;
                if (p_262511_ instanceof Rabbit $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            HORSE = EntityVariantPredicate.create(Variant.CODEC, (p_262516_) -> {
                Optional var10000;
                if (p_262516_ instanceof Horse $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            LLAMA = EntityVariantPredicate.create(net.minecraft.world.entity.animal.horse.Llama.Variant.CODEC, (p_262515_) -> {
                Optional var10000;
                if (p_262515_ instanceof Llama $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            VILLAGER = EntityVariantPredicate.create(BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), (p_262512_) -> {
                Optional var10000;
                if (p_262512_ instanceof VillagerDataHolder $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            PARROT = EntityVariantPredicate.create(net.minecraft.world.entity.animal.Parrot.Variant.CODEC, (p_262506_) -> {
                Optional var10000;
                if (p_262506_ instanceof Parrot $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            TROPICAL_FISH = EntityVariantPredicate.create(Pattern.CODEC, (p_262517_) -> {
                Optional var10000;
                if (p_262517_ instanceof TropicalFish $$1) {
                    var10000 = Optional.of($$1.getVariant());
                } else {
                    var10000 = Optional.empty();
                }

                return var10000;
            });
            TYPES = ImmutableBiMap.builder().put("any", ANY).put("lightning", LIGHTNING).put("fishing_hook", FISHING_HOOK).put("player", PLAYER).put("slime", SLIME).put("cat", CAT.type()).put("frog", FROG.type()).put("axolotl", AXOLOTL.type()).put("boat", BOAT.type()).put("fox", FOX.type()).put("mooshroom", MOOSHROOM.type()).put("painting", PAINTING.type()).put("rabbit", RABBIT.type()).put("horse", HORSE.type()).put("llama", LLAMA.type()).put("villager", VILLAGER.type()).put("parrot", PARROT.type()).put("tropical_fish", TROPICAL_FISH.type()).buildOrThrow();
        }
    }

    public interface Type {
        EntitySubPredicate deserialize(JsonObject var1);
    }
}
