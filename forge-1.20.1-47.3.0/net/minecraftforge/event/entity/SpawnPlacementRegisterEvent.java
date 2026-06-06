//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus.Internal;

public class SpawnPlacementRegisterEvent extends Event implements IModBusEvent {
    private final Map<EntityType<?>, MergedSpawnPredicate<?>> map;

    @Internal
    public SpawnPlacementRegisterEvent(Map<EntityType<?>, MergedSpawnPredicate<?>> map) {
        this.map = map;
    }

    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate) {
        this.register(entityType, (SpawnPlacements.Type)null, (Heightmap.Types)null, predicate, net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.OR);
    }

    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
        this.register(entityType, (SpawnPlacements.Type)null, (Heightmap.Types)null, predicate, operation);
    }

    public <T extends Entity> void register(EntityType<T> entityType, @Nullable SpawnPlacements.Type placementType, @Nullable Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
        if (!this.map.containsKey(entityType)) {
            if (placementType == null) {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull placement type! Entity Type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }

            if (heightmap == null) {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull heightmap type! Entity Type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }

            this.map.put(entityType, new MergedSpawnPredicate(predicate, placementType, heightmap));
        } else {
            if (operation != net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.REPLACE && (heightmap != null || placementType != null)) {
                throw new IllegalStateException("Nonnull heightmap types or spawn placement types may only be used with the REPLACE operation. Entity Type: " + ForgeRegistries.ENTITY_TYPES.getKey(entityType));
            }

            ((MergedSpawnPredicate)this.map.get(entityType)).merge(operation, predicate, placementType, heightmap);
        }

    }

    public static enum Operation {
        AND,
        OR,
        REPLACE;

        private Operation() {
        }
    }

    public static class MergedSpawnPredicate<T extends Entity> {
        private final SpawnPlacements.SpawnPredicate<T> originalPredicate;
        private final List<SpawnPlacements.SpawnPredicate<T>> orPredicates;
        private final List<SpawnPlacements.SpawnPredicate<T>> andPredicates;
        @Nullable
        private SpawnPlacements.SpawnPredicate<T> replacementPredicate;
        private SpawnPlacements.Type spawnType;
        private Heightmap.Types heightmapType;

        public MergedSpawnPredicate(SpawnPlacements.SpawnPredicate<T> originalPredicate, SpawnPlacements.Type spawnType, Heightmap.Types heightmapType) {
            this.originalPredicate = originalPredicate;
            this.orPredicates = new ArrayList();
            this.andPredicates = new ArrayList();
            this.replacementPredicate = null;
            this.spawnType = spawnType;
            this.heightmapType = heightmapType;
        }

        public SpawnPlacements.Type getSpawnType() {
            return this.spawnType;
        }

        public Heightmap.Types getHeightmapType() {
            return this.heightmapType;
        }

        private void merge(Operation operation, SpawnPlacements.SpawnPredicate<T> predicate, @Nullable SpawnPlacements.Type spawnType, @Nullable Heightmap.Types heightmapType) {
            if (operation == net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.AND) {
                this.andPredicates.add(predicate);
            } else if (operation == net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.OR) {
                this.orPredicates.add(predicate);
            } else if (operation == net.minecraftforge.event.entity.SpawnPlacementRegisterEvent.Operation.REPLACE) {
                this.replacementPredicate = predicate;
                if (spawnType != null) {
                    this.spawnType = spawnType;
                }

                if (heightmapType != null) {
                    this.heightmapType = heightmapType;
                }
            }

        }

        @Internal
        public SpawnPlacements.SpawnPredicate<T> build() {
            if (this.replacementPredicate != null) {
                return this.replacementPredicate;
            } else {
                SpawnPlacements.SpawnPredicate<T> original = this.originalPredicate;
                SpawnPlacements.SpawnPredicate<T> compiledOrPredicate = (entityType, level, spawnType, pos, random) -> {
                    if (original.test(entityType, level, spawnType, pos, random)) {
                        return true;
                    } else {
                        Iterator var7 = this.orPredicates.iterator();

                        SpawnPlacements.SpawnPredicate predicate;
                        do {
                            if (!var7.hasNext()) {
                                return false;
                            }

                            predicate = (SpawnPlacements.SpawnPredicate)var7.next();
                        } while(!predicate.test(entityType, level, spawnType, pos, random));

                        return true;
                    }
                };
                SpawnPlacements.SpawnPredicate<T> compiledAndPredicate = (entityType, level, spawnType, pos, random) -> {
                    if (!compiledOrPredicate.test(entityType, level, spawnType, pos, random)) {
                        return false;
                    } else {
                        Iterator var7 = this.andPredicates.iterator();

                        SpawnPlacements.SpawnPredicate predicate;
                        do {
                            if (!var7.hasNext()) {
                                return true;
                            }

                            predicate = (SpawnPlacements.SpawnPredicate)var7.next();
                        } while(predicate.test(entityType, level, spawnType, pos, random));

                        return false;
                    }
                };
                return compiledAndPredicate;
            }
        }
    }
}
