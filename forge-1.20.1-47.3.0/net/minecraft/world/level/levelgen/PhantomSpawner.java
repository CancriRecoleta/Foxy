//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.minecraftforge.eventbus.api.Event.Result;

public class PhantomSpawner implements CustomSpawner {
    private int nextTick;

    public PhantomSpawner() {
    }

    public int tick(ServerLevel p_64576_, boolean p_64577_, boolean p_64578_) {
        if (!p_64577_) {
            return 0;
        } else if (!p_64576_.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) {
            return 0;
        } else {
            RandomSource randomsource = p_64576_.random;
            --this.nextTick;
            if (this.nextTick > 0) {
                return 0;
            } else {
                this.nextTick += (60 + randomsource.nextInt(60)) * 20;
                if (p_64576_.getSkyDarken() < 5 && p_64576_.dimensionType().hasSkyLight()) {
                    return 0;
                } else {
                    int i = 0;
                    Iterator var6 = p_64576_.players().iterator();

                    while(true) {
                        DifficultyInstance difficultyinstance;
                        PlayerSpawnPhantomsEvent event;
                        BlockPos blockpos1;
                        BlockState blockstate;
                        FluidState fluidstate;
                        do {
                            BlockPos blockpos;
                            int j;
                            do {
                                ServerPlayer serverplayer;
                                do {
                                    do {
                                        do {
                                            do {
                                                if (!var6.hasNext()) {
                                                    return i;
                                                }

                                                serverplayer = (ServerPlayer)var6.next();
                                            } while(serverplayer.isSpectator());

                                            blockpos = serverplayer.blockPosition();
                                            difficultyinstance = p_64576_.getCurrentDifficultyAt(blockpos);
                                            event = new PlayerSpawnPhantomsEvent(serverplayer, 1 + randomsource.nextInt(difficultyinstance.getDifficulty().getId() + 1));
                                            MinecraftForge.EVENT_BUS.post(event);
                                        } while(event.getResult() == Result.DENY);
                                    } while(event.getResult() != Result.ALLOW && p_64576_.dimensionType().hasSkyLight() && (blockpos.getY() < p_64576_.getSeaLevel() || !p_64576_.canSeeSky(blockpos)));
                                } while(!difficultyinstance.isHarderThan(randomsource.nextFloat() * 3.0F));

                                ServerStatsCounter serverstatscounter = serverplayer.getStats();
                                j = Mth.clamp(serverstatscounter.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                                int k = true;
                            } while(event.getResult() != Result.ALLOW && randomsource.nextInt(j) < 72000);

                            blockpos1 = blockpos.above(20 + randomsource.nextInt(15)).east(-10 + randomsource.nextInt(21)).south(-10 + randomsource.nextInt(21));
                            blockstate = p_64576_.getBlockState(blockpos1);
                            fluidstate = p_64576_.getFluidState(blockpos1);
                        } while(!NaturalSpawner.isValidEmptySpawnBlock(p_64576_, blockpos1, blockstate, fluidstate, EntityType.PHANTOM));

                        SpawnGroupData spawngroupdata = null;
                        int l = event.getPhantomsToSpawn();

                        for(int i1 = 0; i1 < l; ++i1) {
                            Phantom phantom = (Phantom)EntityType.PHANTOM.create(p_64576_);
                            if (phantom != null) {
                                phantom.moveTo(blockpos1, 0.0F, 0.0F);
                                spawngroupdata = phantom.finalizeSpawn(p_64576_, difficultyinstance, MobSpawnType.NATURAL, spawngroupdata, (CompoundTag)null);
                                p_64576_.addFreshEntityWithPassengers(phantom);
                                ++i;
                            }
                        }
                    }
                }
            }
        }
    }
}
