//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.ModLoader;

public class SpawnPlacements {
    private static final Map<EntityType<?>, Data> DATA_BY_TYPE = Maps.newHashMap();

    public SpawnPlacements() {
    }

    /** @deprecated */
    @Deprecated
    public static <T extends Mob> void register(EntityType<T> p_21755_, Type p_21756_, Heightmap.Types p_21757_, SpawnPredicate<T> p_21758_) {
        Data spawnplacements$data = (Data)DATA_BY_TYPE.put(p_21755_, new Data(p_21757_, p_21756_, p_21758_));
        if (spawnplacements$data != null) {
            throw new IllegalStateException("Duplicate registration for type " + BuiltInRegistries.ENTITY_TYPE.getKey(p_21755_));
        }
    }

    public static Type getPlacementType(EntityType<?> p_21753_) {
        Data spawnplacements$data = (Data)DATA_BY_TYPE.get(p_21753_);
        return spawnplacements$data == null ? net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS : spawnplacements$data.placement;
    }

    public static Heightmap.Types getHeightmapType(@Nullable EntityType<?> p_21766_) {
        Data spawnplacements$data = (Data)DATA_BY_TYPE.get(p_21766_);
        return spawnplacements$data == null ? Types.MOTION_BLOCKING_NO_LEAVES : spawnplacements$data.heightMap;
    }

    public static <T extends Entity> boolean checkSpawnRules(EntityType<T> p_217075_, ServerLevelAccessor p_217076_, MobSpawnType p_217077_, BlockPos p_217078_, RandomSource p_217079_) {
        Data spawnplacements$data = (Data)DATA_BY_TYPE.get(p_217075_);
        boolean vanillaResult = spawnplacements$data == null || spawnplacements$data.predicate.test(p_217075_, p_217076_, p_217077_, p_217078_, p_217079_);
        return ForgeEventFactory.checkSpawnPlacements(p_217075_, p_217076_, p_217077_, p_217078_, p_217079_, vanillaResult);
    }

    public static void fireSpawnPlacementEvent() {
        Map<EntityType<?>, SpawnPlacementRegisterEvent.MergedSpawnPredicate<?>> map = Maps.newHashMap();
        DATA_BY_TYPE.forEach((type, data) -> {
            map.put(type, new SpawnPlacementRegisterEvent.MergedSpawnPredicate(data.predicate, data.placement, data.heightMap));
        });
        ModLoader.get().postEvent(new SpawnPlacementRegisterEvent(map));
        map.forEach((entityType, merged) -> {
            DATA_BY_TYPE.put(entityType, new Data(merged.getHeightmapType(), merged.getSpawnType(), merged.build()));
        });
    }

    static {
        register(EntityType.AXOLOTL, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, Axolotl::checkAxolotlSpawnRules);
        register(EntityType.COD, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.DOLPHIN, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.DROWNED, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, Drowned::checkDrownedSpawnRules);
        register(EntityType.GUARDIAN, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
        register(EntityType.PUFFERFISH, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.SALMON, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.SQUID, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        register(EntityType.TROPICAL_FISH, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, TropicalFish::checkTropicalFishSpawnRules);
        register(EntityType.BAT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Bat::checkBatSpawnRules);
        register(EntityType.BLAZE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        register(EntityType.CAVE_SPIDER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.CHICKEN, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.COW, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.CREEPER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.DONKEY, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.ENDERMAN, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.ENDERMITE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Endermite::checkEndermiteSpawnRules);
        register(EntityType.ENDER_DRAGON, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.FROG, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Frog::checkFrogSpawnRules);
        register(EntityType.GHAST, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Ghast::checkGhastSpawnRules);
        register(EntityType.GIANT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.GLOW_SQUID, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, GlowSquid::checkGlowSquideSpawnRules);
        register(EntityType.GOAT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Goat::checkGoatSpawnRules);
        register(EntityType.HORSE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.HUSK, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Husk::checkHuskSpawnRules);
        register(EntityType.IRON_GOLEM, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.LLAMA, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.MAGMA_CUBE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, MagmaCube::checkMagmaCubeSpawnRules);
        register(EntityType.MOOSHROOM, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, MushroomCow::checkMushroomSpawnRules);
        register(EntityType.MULE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.OCELOT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING, Ocelot::checkOcelotSpawnRules);
        register(EntityType.PARROT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING, Parrot::checkParrotSpawnRules);
        register(EntityType.PIG, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.HOGLIN, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Hoglin::checkHoglinSpawnRules);
        register(EntityType.PIGLIN, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Piglin::checkPiglinSpawnRules);
        register(EntityType.PILLAGER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, PatrollingMonster::checkPatrollingMonsterSpawnRules);
        register(EntityType.POLAR_BEAR, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, PolarBear::checkPolarBearSpawnRules);
        register(EntityType.RABBIT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Rabbit::checkRabbitSpawnRules);
        register(EntityType.SHEEP, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.SILVERFISH, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Silverfish::checkSilverfishSpawnRules);
        register(EntityType.SKELETON, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.SKELETON_HORSE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.SLIME, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Slime::checkSlimeSpawnRules);
        register(EntityType.SNOW_GOLEM, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.SPIDER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.STRAY, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Stray::checkStraySpawnRules);
        register(EntityType.STRIDER, net.minecraft.world.entity.SpawnPlacements.Type.IN_LAVA, Types.MOTION_BLOCKING_NO_LEAVES, Strider::checkStriderSpawnRules);
        register(EntityType.TURTLE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Turtle::checkTurtleSpawnRules);
        register(EntityType.VILLAGER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.WITCH, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WITHER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WITHER_SKELETON, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WOLF, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Wolf::checkWolfSpawnRules);
        register(EntityType.ZOMBIE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.ZOMBIE_HORSE, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.ZOMBIFIED_PIGLIN, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglin::checkZombifiedPiglinSpawnRules);
        register(EntityType.ZOMBIE_VILLAGER, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.CAT, net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.ELDER_GUARDIAN, net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, Guardian::checkGuardianSpawnRules);
        register(EntityType.EVOKER, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.FOX, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Fox::checkFoxSpawnRules);
        register(EntityType.ILLUSIONER, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.PANDA, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.PHANTOM, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.RAVAGER, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.SHULKER, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.TRADER_LLAMA, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        register(EntityType.VEX, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.VINDICATOR, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        register(EntityType.WANDERING_TRADER, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(EntityType.WARDEN, net.minecraft.world.entity.SpawnPlacements.Type.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    static class Data {
        final Heightmap.Types heightMap;
        final Type placement;
        final SpawnPredicate<?> predicate;

        public Data(Heightmap.Types p_21771_, Type p_21772_, SpawnPredicate<?> p_21773_) {
            this.heightMap = p_21771_;
            this.placement = p_21772_;
            this.predicate = p_21773_;
        }
    }

    public static enum Type implements IExtensibleEnum {
        ON_GROUND,
        IN_WATER,
        NO_RESTRICTIONS,
        IN_LAVA;

        private TriPredicate<LevelReader, BlockPos, EntityType<?>> predicate;

        public static Type create(String name, TriPredicate<LevelReader, BlockPos, EntityType<? extends Mob>> predicate) {
            throw new IllegalStateException("Enum not extended");
        }

        private Type() {
            this((TriPredicate)null);
        }

        private Type(TriPredicate predicate) {
            this.predicate = predicate;
        }

        public boolean canSpawnAt(LevelReader world, BlockPos pos, EntityType<?> type) {
            if (this == NO_RESTRICTIONS) {
                return true;
            } else {
                return this.predicate == null ? NaturalSpawner.canSpawnAtBody(this, world, pos, type) : this.predicate.test(world, pos, type);
            }
        }
    }

    @FunctionalInterface
    public interface SpawnPredicate<T extends Entity> {
        boolean test(EntityType<T> var1, ServerLevelAccessor var2, MobSpawnType var3, BlockPos var4, RandomSource var5);
    }
}
