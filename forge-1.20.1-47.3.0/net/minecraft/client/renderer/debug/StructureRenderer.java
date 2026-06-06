//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
    private final Minecraft minecraft;
    private final Map<DimensionType, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, BoundingBox>> postPiecesBoxes = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> startPiecesMap = Maps.newIdentityHashMap();
    private static final int MAX_RENDER_DIST = 500;

    public StructureRenderer(Minecraft p_113680_) {
        this.minecraft = p_113680_;
    }

    public void render(PoseStack p_113688_, MultiBufferSource p_113689_, double p_113690_, double p_113691_, double p_113692_) {
        Camera $$5 = this.minecraft.gameRenderer.getMainCamera();
        LevelAccessor $$6 = this.minecraft.level;
        DimensionType $$7 = $$6.dimensionType();
        BlockPos $$8 = BlockPos.containing($$5.getPosition().x, 0.0, $$5.getPosition().z);
        VertexConsumer $$9 = p_113689_.getBuffer(RenderType.lines());
        Iterator var14;
        if (this.postMainBoxes.containsKey($$7)) {
            var14 = ((Map)this.postMainBoxes.get($$7)).values().iterator();

            while(var14.hasNext()) {
                BoundingBox $$10 = (BoundingBox)var14.next();
                if ($$8.closerThan($$10.getCenter(), 500.0)) {
                    LevelRenderer.renderLineBox(p_113688_, $$9, (double)$$10.minX() - p_113690_, (double)$$10.minY() - p_113691_, (double)$$10.minZ() - p_113692_, (double)($$10.maxX() + 1) - p_113690_, (double)($$10.maxY() + 1) - p_113691_, (double)($$10.maxZ() + 1) - p_113692_, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }

        if (this.postPiecesBoxes.containsKey($$7)) {
            var14 = ((Map)this.postPiecesBoxes.get($$7)).entrySet().iterator();

            while(var14.hasNext()) {
                Map.Entry<String, BoundingBox> $$11 = (Map.Entry)var14.next();
                String $$12 = (String)$$11.getKey();
                BoundingBox $$13 = (BoundingBox)$$11.getValue();
                Boolean $$14 = (Boolean)((Map)this.startPiecesMap.get($$7)).get($$12);
                if ($$8.closerThan($$13.getCenter(), 500.0)) {
                    if ($$14) {
                        LevelRenderer.renderLineBox(p_113688_, $$9, (double)$$13.minX() - p_113690_, (double)$$13.minY() - p_113691_, (double)$$13.minZ() - p_113692_, (double)($$13.maxX() + 1) - p_113690_, (double)($$13.maxY() + 1) - p_113691_, (double)($$13.maxZ() + 1) - p_113692_, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
                    } else {
                        LevelRenderer.renderLineBox(p_113688_, $$9, (double)$$13.minX() - p_113690_, (double)$$13.minY() - p_113691_, (double)$$13.minZ() - p_113692_, (double)($$13.maxX() + 1) - p_113690_, (double)($$13.maxY() + 1) - p_113691_, (double)($$13.maxZ() + 1) - p_113692_, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            }
        }

    }

    public void addBoundingBox(BoundingBox p_113683_, List<BoundingBox> p_113684_, List<Boolean> p_113685_, DimensionType p_113686_) {
        if (!this.postMainBoxes.containsKey(p_113686_)) {
            this.postMainBoxes.put(p_113686_, Maps.newHashMap());
        }

        if (!this.postPiecesBoxes.containsKey(p_113686_)) {
            this.postPiecesBoxes.put(p_113686_, Maps.newHashMap());
            this.startPiecesMap.put(p_113686_, Maps.newHashMap());
        }

        ((Map)this.postMainBoxes.get(p_113686_)).put(p_113683_.toString(), p_113683_);

        for(int $$4 = 0; $$4 < p_113684_.size(); ++$$4) {
            BoundingBox $$5 = (BoundingBox)p_113684_.get($$4);
            Boolean $$6 = (Boolean)p_113685_.get($$4);
            ((Map)this.postPiecesBoxes.get(p_113686_)).put($$5.toString(), $$5);
            ((Map)this.startPiecesMap.get(p_113686_)).put($$5.toString(), $$6);
        }

    }

    public void clear() {
        this.postMainBoxes.clear();
        this.postPiecesBoxes.clear();
        this.startPiecesMap.clear();
    }
}
