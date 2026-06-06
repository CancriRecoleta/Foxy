//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PartDefinition {
    private final List<CubeDefinition> cubes;
    private final PartPose partPose;
    private final Map<String, PartDefinition> children = Maps.newHashMap();

    PartDefinition(List<CubeDefinition> p_171581_, PartPose p_171582_) {
        this.cubes = p_171581_;
        this.partPose = p_171582_;
    }

    public PartDefinition addOrReplaceChild(String p_171600_, CubeListBuilder p_171601_, PartPose p_171602_) {
        PartDefinition $$3 = new PartDefinition(p_171601_.getCubes(), p_171602_);
        PartDefinition $$4 = (PartDefinition)this.children.put(p_171600_, $$3);
        if ($$4 != null) {
            $$3.children.putAll($$4.children);
        }

        return $$3;
    }

    public ModelPart bake(int p_171584_, int p_171585_) {
        Object2ObjectArrayMap<String, ModelPart> $$2 = (Object2ObjectArrayMap)this.children.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (p_171593_) -> {
            return ((PartDefinition)p_171593_.getValue()).bake(p_171584_, p_171585_);
        }, (p_171595_, p_171596_) -> {
            return p_171595_;
        }, Object2ObjectArrayMap::new));
        List<ModelPart.Cube> $$3 = (List)this.cubes.stream().map((p_171589_) -> {
            return p_171589_.bake(p_171584_, p_171585_);
        }).collect(ImmutableList.toImmutableList());
        ModelPart $$4 = new ModelPart($$3, $$2);
        $$4.setInitialPose(this.partPose);
        $$4.loadPose(this.partPose);
        return $$4;
    }

    public PartDefinition getChild(String p_171598_) {
        return (PartDefinition)this.children.get(p_171598_);
    }
}
