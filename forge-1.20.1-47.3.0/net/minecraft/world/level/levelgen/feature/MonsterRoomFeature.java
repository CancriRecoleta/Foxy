//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.DungeonHooks;
import org.slf4j.Logger;

public class MonsterRoomFeature extends Feature<NoneFeatureConfiguration> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityType<?>[] MOBS;
    private static final BlockState AIR;

    public MonsterRoomFeature(Codec<NoneFeatureConfiguration> p_66345_) {
        super(p_66345_);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160066_) {
        Predicate<BlockState> predicate = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);
        BlockPos blockpos = p_160066_.origin();
        RandomSource randomsource = p_160066_.random();
        WorldGenLevel worldgenlevel = p_160066_.level();
        int i = true;
        int j = randomsource.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int i1 = true;
        int j1 = true;
        int k1 = randomsource.nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i2 = k1 + 1;
        int j2 = 0;

        int k3;
        int i4;
        int k4;
        BlockPos blockpos3;
        for(k3 = k; k3 <= l; ++k3) {
            for(i4 = -1; i4 <= 4; ++i4) {
                for(k4 = l1; k4 <= i2; ++k4) {
                    blockpos3 = blockpos.offset(k3, i4, k4);
                    boolean flag = worldgenlevel.getBlockState(blockpos3).isSolid();
                    if (i4 == -1 && !flag) {
                        return false;
                    }

                    if (i4 == 4 && !flag) {
                        return false;
                    }

                    if ((k3 == k || k3 == l || k4 == l1 || k4 == i2) && i4 == 0 && worldgenlevel.isEmptyBlock(blockpos3) && worldgenlevel.isEmptyBlock(blockpos3.above())) {
                        ++j2;
                    }
                }
            }
        }

        if (j2 >= 1 && j2 <= 5) {
            for(k3 = k; k3 <= l; ++k3) {
                for(i4 = 3; i4 >= -1; --i4) {
                    for(k4 = l1; k4 <= i2; ++k4) {
                        blockpos3 = blockpos.offset(k3, i4, k4);
                        BlockState blockstate = worldgenlevel.getBlockState(blockpos3);
                        if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2) {
                            if (!blockstate.is(Blocks.CHEST) && !blockstate.is(Blocks.SPAWNER)) {
                                this.safeSetBlock(worldgenlevel, blockpos3, AIR, predicate);
                            }
                        } else if (blockpos3.getY() >= worldgenlevel.getMinBuildHeight() && !worldgenlevel.getBlockState(blockpos3.below()).isSolid()) {
                            worldgenlevel.setBlock(blockpos3, AIR, 2);
                        } else if (blockstate.isSolid() && !blockstate.is(Blocks.CHEST)) {
                            if (i4 == -1 && randomsource.nextInt(4) != 0) {
                                this.safeSetBlock(worldgenlevel, blockpos3, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
                            } else {
                                this.safeSetBlock(worldgenlevel, blockpos3, Blocks.COBBLESTONE.defaultBlockState(), predicate);
                            }
                        }
                    }
                }
            }

            for(k3 = 0; k3 < 2; ++k3) {
                for(i4 = 0; i4 < 3; ++i4) {
                    k4 = blockpos.getX() + randomsource.nextInt(j * 2 + 1) - j;
                    int i5 = blockpos.getY();
                    int j5 = blockpos.getZ() + randomsource.nextInt(k1 * 2 + 1) - k1;
                    BlockPos blockpos2 = new BlockPos(k4, i5, j5);
                    if (worldgenlevel.isEmptyBlock(blockpos2)) {
                        int j3 = 0;
                        Iterator var23 = Plane.HORIZONTAL.iterator();

                        while(var23.hasNext()) {
                            Direction direction = (Direction)var23.next();
                            if (worldgenlevel.getBlockState(blockpos2.relative(direction)).isSolid()) {
                                ++j3;
                            }
                        }

                        if (j3 == 1) {
                            this.safeSetBlock(worldgenlevel, blockpos2, StructurePiece.reorient(worldgenlevel, blockpos2, Blocks.CHEST.defaultBlockState()), predicate);
                            RandomizableContainerBlockEntity.setLootTable(worldgenlevel, randomsource, blockpos2, BuiltInLootTables.SIMPLE_DUNGEON);
                            break;
                        }
                    }
                }
            }

            this.safeSetBlock(worldgenlevel, blockpos, Blocks.SPAWNER.defaultBlockState(), predicate);
            BlockEntity blockentity = worldgenlevel.getBlockEntity(blockpos);
            if (blockentity instanceof SpawnerBlockEntity) {
                SpawnerBlockEntity spawnerblockentity = (SpawnerBlockEntity)blockentity;
                spawnerblockentity.setEntityId(this.randomEntityId(randomsource), randomsource);
            } else {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", new Object[]{blockpos.getX(), blockpos.getY(), blockpos.getZ()});
            }

            return true;
        } else {
            return false;
        }
    }

    private EntityType<?> randomEntityId(RandomSource p_225154_) {
        return DungeonHooks.getRandomDungeonMob(p_225154_);
    }

    static {
        MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
        AIR = Blocks.CAVE_AIR.defaultBlockState();
    }
}
