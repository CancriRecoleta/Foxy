//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    final Minecraft minecraft;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int radius = 12;
    @Nullable
    private ChunkData data;

    public ChunkDebugRenderer(Minecraft p_113368_) {
        this.minecraft = p_113368_;
    }

    public void render(PoseStack p_113370_, MultiBufferSource p_113371_, double p_113372_, double p_113373_, double p_113374_) {
        double $$5 = (double)Util.getNanos();
        if ($$5 - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = $$5;
            IntegratedServer $$6 = this.minecraft.getSingleplayerServer();
            if ($$6 != null) {
                this.data = new ChunkData($$6, p_113372_, p_113374_);
            } else {
                this.data = null;
            }
        }

        if (this.data != null) {
            Map<ChunkPos, String> $$7 = (Map)this.data.serverData.getNow((Object)null);
            double $$8 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85;
            Iterator var14 = this.data.clientData.entrySet().iterator();

            while(var14.hasNext()) {
                Map.Entry<ChunkPos, String> $$9 = (Map.Entry)var14.next();
                ChunkPos $$10 = (ChunkPos)$$9.getKey();
                String $$11 = (String)$$9.getValue();
                if ($$7 != null) {
                    $$11 = $$11 + (String)$$7.get($$10);
                }

                String[] $$12 = $$11.split("\n");
                int $$13 = 0;
                String[] var20 = $$12;
                int var21 = $$12.length;

                for(int var22 = 0; var22 < var21; ++var22) {
                    String $$14 = var20[var22];
                    DebugRenderer.renderFloatingText(p_113370_, p_113371_, $$14, (double)SectionPos.sectionToBlockCoord($$10.x, 8), $$8 + (double)$$13, (double)SectionPos.sectionToBlockCoord($$10.z, 8), -1, 0.15F, true, 0.0F, true);
                    $$13 -= 2;
                }
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    private final class ChunkData {
        final Map<ChunkPos, String> clientData;
        final CompletableFuture<Map<ChunkPos, String>> serverData;

        ChunkData(IntegratedServer p_113382_, double p_113383_, double p_113384_) {
            ClientLevel $$3 = ChunkDebugRenderer.this.minecraft.level;
            ResourceKey<Level> $$4 = $$3.dimension();
            int $$5 = SectionPos.posToSectionCoord(p_113383_);
            int $$6 = SectionPos.posToSectionCoord(p_113384_);
            ImmutableMap.Builder<ChunkPos, String> $$7 = ImmutableMap.builder();
            ClientChunkCache $$8 = $$3.getChunkSource();

            for(int $$9 = $$5 - 12; $$9 <= $$5 + 12; ++$$9) {
                for(int $$10 = $$6 - 12; $$10 <= $$6 + 12; ++$$10) {
                    ChunkPos $$11 = new ChunkPos($$9, $$10);
                    String $$12 = "";
                    LevelChunk $$13 = $$8.getChunk($$9, $$10, false);
                    $$12 = $$12 + "Client: ";
                    if ($$13 == null) {
                        $$12 = $$12 + "0n/a\n";
                    } else {
                        $$12 = $$12 + ($$13.isEmpty() ? " E" : "");
                        $$12 = $$12 + "\n";
                    }

                    $$7.put($$11, $$12);
                }
            }

            this.clientData = $$7.build();
            this.serverData = p_113382_.submit(() -> {
                ServerLevel $$4x = p_113382_.getLevel($$4);
                if ($$4x == null) {
                    return ImmutableMap.of();
                } else {
                    ImmutableMap.Builder<ChunkPos, String> $$5x = ImmutableMap.builder();
                    ServerChunkCache $$6x = $$4x.getChunkSource();

                    for(int $$7 = $$5 - 12; $$7 <= $$5 + 12; ++$$7) {
                        for(int $$8 = $$6 - 12; $$8 <= $$6 + 12; ++$$8) {
                            ChunkPos $$9 = new ChunkPos($$7, $$8);
                            String var10002 = $$6x.getChunkDebugData($$9);
                            $$5x.put($$9, "Server: " + var10002);
                        }
                    }

                    return $$5x.build();
                }
            });
        }
    }
}
