//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public enum DragonRespawnAnimation {
    START {
        public void tick(ServerLevel p_64017_, EndDragonFight p_64018_, List<EndCrystal> p_64019_, int p_64020_, BlockPos p_64021_) {
            BlockPos $$5 = new BlockPos(0, 128, 0);
            Iterator var7 = p_64019_.iterator();

            while(var7.hasNext()) {
                EndCrystal $$6 = (EndCrystal)var7.next();
                $$6.setBeamTarget($$5);
            }

            p_64018_.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
        }
    },
    PREPARING_TO_SUMMON_PILLARS {
        public void tick(ServerLevel p_64026_, EndDragonFight p_64027_, List<EndCrystal> p_64028_, int p_64029_, BlockPos p_64030_) {
            if (p_64029_ < 100) {
                if (p_64029_ == 0 || p_64029_ == 50 || p_64029_ == 51 || p_64029_ == 52 || p_64029_ >= 95) {
                    p_64026_.levelEvent(3001, new BlockPos(0, 128, 0), 0);
                }
            } else {
                p_64027_.setRespawnStage(SUMMONING_PILLARS);
            }

        }
    },
    SUMMONING_PILLARS {
        public void tick(ServerLevel p_64035_, EndDragonFight p_64036_, List<EndCrystal> p_64037_, int p_64038_, BlockPos p_64039_) {
            int $$5 = true;
            boolean $$6 = p_64038_ % 40 == 0;
            boolean $$7 = p_64038_ % 40 == 39;
            if ($$6 || $$7) {
                List<SpikeFeature.EndSpike> $$8 = SpikeFeature.getSpikesForLevel(p_64035_);
                int $$9 = p_64038_ / 40;
                if ($$9 < $$8.size()) {
                    SpikeFeature.EndSpike $$10 = (SpikeFeature.EndSpike)$$8.get($$9);
                    if ($$6) {
                        Iterator var12 = p_64037_.iterator();

                        while(var12.hasNext()) {
                            EndCrystal $$11 = (EndCrystal)var12.next();
                            $$11.setBeamTarget(new BlockPos($$10.getCenterX(), $$10.getHeight() + 1, $$10.getCenterZ()));
                        }
                    } else {
                        int $$12 = true;
                        Iterator var16 = BlockPos.betweenClosed(new BlockPos($$10.getCenterX() - 10, $$10.getHeight() - 10, $$10.getCenterZ() - 10), new BlockPos($$10.getCenterX() + 10, $$10.getHeight() + 10, $$10.getCenterZ() + 10)).iterator();

                        while(var16.hasNext()) {
                            BlockPos $$13 = (BlockPos)var16.next();
                            p_64035_.removeBlock($$13, false);
                        }

                        p_64035_.explode((Entity)null, (double)((float)$$10.getCenterX() + 0.5F), (double)$$10.getHeight(), (double)((float)$$10.getCenterZ() + 0.5F), 5.0F, ExplosionInteraction.BLOCK);
                        SpikeConfiguration $$14 = new SpikeConfiguration(true, ImmutableList.of($$10), new BlockPos(0, 128, 0));
                        Feature.END_SPIKE.place($$14, p_64035_, p_64035_.getChunkSource().getGenerator(), RandomSource.create(), new BlockPos($$10.getCenterX(), 45, $$10.getCenterZ()));
                    }
                } else if ($$6) {
                    p_64036_.setRespawnStage(SUMMONING_DRAGON);
                }
            }

        }
    },
    SUMMONING_DRAGON {
        public void tick(ServerLevel p_64044_, EndDragonFight p_64045_, List<EndCrystal> p_64046_, int p_64047_, BlockPos p_64048_) {
            Iterator var6;
            EndCrystal $$6;
            if (p_64047_ >= 100) {
                p_64045_.setRespawnStage(END);
                p_64045_.resetSpikeCrystals();
                var6 = p_64046_.iterator();

                while(var6.hasNext()) {
                    $$6 = (EndCrystal)var6.next();
                    $$6.setBeamTarget((BlockPos)null);
                    p_64044_.explode($$6, $$6.getX(), $$6.getY(), $$6.getZ(), 6.0F, ExplosionInteraction.NONE);
                    $$6.discard();
                }
            } else if (p_64047_ >= 80) {
                p_64044_.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            } else if (p_64047_ == 0) {
                var6 = p_64046_.iterator();

                while(var6.hasNext()) {
                    $$6 = (EndCrystal)var6.next();
                    $$6.setBeamTarget(new BlockPos(0, 128, 0));
                }
            } else if (p_64047_ < 5) {
                p_64044_.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }

        }
    },
    END {
        public void tick(ServerLevel p_64053_, EndDragonFight p_64054_, List<EndCrystal> p_64055_, int p_64056_, BlockPos p_64057_) {
        }
    };

    DragonRespawnAnimation() {
    }

    public abstract void tick(ServerLevel var1, EndDragonFight var2, List<EndCrystal> var3, int var4, BlockPos var5);
}
