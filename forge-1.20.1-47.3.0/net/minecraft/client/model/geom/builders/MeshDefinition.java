//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.model.geom.builders;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.PartPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MeshDefinition {
    private final PartDefinition root;

    public MeshDefinition() {
        this.root = new PartDefinition(ImmutableList.of(), PartPose.ZERO);
    }

    public PartDefinition getRoot() {
        return this.root;
    }
}
