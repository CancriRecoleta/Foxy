//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<Long, Map<BlockPos, Integer>> lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

    NeighborsUpdateRenderer(Minecraft p_113595_) {
        this.minecraft = p_113595_;
    }

    public void addUpdate(long p_113597_, BlockPos p_113598_) {
        Map<BlockPos, Integer> $$2 = (Map)this.lastUpdate.computeIfAbsent(p_113597_, (p_113606_) -> {
            return Maps.newHashMap();
        });
        int $$3 = (Integer)$$2.getOrDefault(p_113598_, 0);
        $$2.put(p_113598_, $$3 + 1);
    }

    public void render(PoseStack p_113600_, MultiBufferSource p_113601_, double p_113602_, double p_113603_, double p_113604_) {
        long $$5 = this.minecraft.level.getGameTime();
        int $$6 = true;
        double $$7 = 0.0025;
        Set<BlockPos> $$8 = Sets.newHashSet();
        Map<BlockPos, Integer> $$9 = Maps.newHashMap();
        VertexConsumer $$10 = p_113601_.getBuffer(RenderType.lines());
        Iterator<Map.Entry<Long, Map<BlockPos, Integer>>> $$11 = this.lastUpdate.entrySet().iterator();

        while(true) {
            Map.Entry $$20;
            while($$11.hasNext()) {
                $$20 = (Map.Entry)$$11.next();
                Long $$13 = (Long)$$20.getKey();
                Map<BlockPos, Integer> $$14 = (Map)$$20.getValue();
                long $$15 = $$5 - $$13;
                if ($$15 > 200L) {
                    $$11.remove();
                } else {
                    Iterator var23 = $$14.entrySet().iterator();

                    while(var23.hasNext()) {
                        Map.Entry<BlockPos, Integer> $$16 = (Map.Entry)var23.next();
                        BlockPos $$17 = (BlockPos)$$16.getKey();
                        Integer $$18 = (Integer)$$16.getValue();
                        if ($$8.add($$17)) {
                            AABB $$19 = (new AABB(BlockPos.ZERO)).inflate(0.002).deflate(0.0025 * (double)$$15).move((double)$$17.getX(), (double)$$17.getY(), (double)$$17.getZ()).move(-p_113602_, -p_113603_, -p_113604_);
                            LevelRenderer.renderLineBox(p_113600_, $$10, $$19.minX, $$19.minY, $$19.minZ, $$19.maxX, $$19.maxY, $$19.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
                            $$9.put($$17, $$18);
                        }
                    }
                }
            }

            $$11 = $$9.entrySet().iterator();

            while($$11.hasNext()) {
                $$20 = (Map.Entry)$$11.next();
                BlockPos $$21 = (BlockPos)$$20.getKey();
                Integer $$22 = (Integer)$$20.getValue();
                DebugRenderer.renderFloatingText(p_113600_, p_113601_, String.valueOf($$22), $$21.getX(), $$21.getY(), $$21.getZ(), -1);
            }

            return;
        }
    }
}
