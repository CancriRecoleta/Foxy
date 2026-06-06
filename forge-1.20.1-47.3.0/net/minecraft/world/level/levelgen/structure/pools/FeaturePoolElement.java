//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class FeaturePoolElement extends StructurePoolElement {
    public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create((p_210213_) -> {
        return p_210213_.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((p_210215_) -> {
            return p_210215_.feature;
        }), projectionCodec()).apply(p_210213_, FeaturePoolElement::new);
    });
    private final Holder<PlacedFeature> feature;
    private final CompoundTag defaultJigsawNBT;

    protected FeaturePoolElement(Holder<PlacedFeature> p_210209_, StructureTemplatePool.Projection p_210210_) {
        super(p_210210_);
        this.feature = p_210209_;
        this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
    }

    private CompoundTag fillDefaultJigsawNBT() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("name", "minecraft:bottom");
        $$0.putString("final_state", "minecraft:air");
        $$0.putString("pool", "minecraft:empty");
        $$0.putString("target", "minecraft:empty");
        $$0.putString("joint", JointType.ROLLABLE.getSerializedName());
        return $$0;
    }

    public Vec3i getSize(StructureTemplateManager p_227192_, Rotation p_227193_) {
        return Vec3i.ZERO;
    }

    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager p_227199_, BlockPos p_227200_, Rotation p_227201_, RandomSource p_227202_) {
        List<StructureTemplate.StructureBlockInfo> $$4 = Lists.newArrayList();
        $$4.add(new StructureTemplate.StructureBlockInfo(p_227200_, (BlockState)Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)), this.defaultJigsawNBT));
        return $$4;
    }

    public BoundingBox getBoundingBox(StructureTemplateManager p_227195_, BlockPos p_227196_, Rotation p_227197_) {
        Vec3i $$3 = this.getSize(p_227195_, p_227197_);
        return new BoundingBox(p_227196_.getX(), p_227196_.getY(), p_227196_.getZ(), p_227196_.getX() + $$3.getX(), p_227196_.getY() + $$3.getY(), p_227196_.getZ() + $$3.getZ());
    }

    public boolean place(StructureTemplateManager p_227181_, WorldGenLevel p_227182_, StructureManager p_227183_, ChunkGenerator p_227184_, BlockPos p_227185_, BlockPos p_227186_, Rotation p_227187_, BoundingBox p_227188_, RandomSource p_227189_, boolean p_227190_) {
        return ((PlacedFeature)this.feature.value()).place(p_227182_, p_227184_, p_227189_, p_227185_);
    }

    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.FEATURE;
    }

    public String toString() {
        return "Feature[" + this.feature + "]";
    }
}
