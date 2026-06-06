//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float PROBABILITY_OF_GOLD_GONE = 0.3F;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_NETHERRACK = 0.07F;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_LAVA = 0.2F;
    private final VerticalPlacement verticalPlacement;
    private final Properties properties;

    public RuinedPortalPiece(StructureTemplateManager p_229105_, BlockPos p_229106_, VerticalPlacement p_229107_, Properties p_229108_, ResourceLocation p_229109_, StructureTemplate p_229110_, Rotation p_229111_, Mirror p_229112_, BlockPos p_229113_) {
        super(StructurePieceType.RUINED_PORTAL, 0, p_229105_, p_229109_, p_229109_.toString(), makeSettings(p_229112_, p_229111_, p_229107_, p_229113_, p_229108_), p_229106_);
        this.verticalPlacement = p_229107_;
        this.properties = p_229108_;
    }

    public RuinedPortalPiece(StructureTemplateManager p_229115_, CompoundTag p_229116_) {
        super(StructurePieceType.RUINED_PORTAL, p_229116_, p_229115_, (p_229188_) -> {
            return makeSettings(p_229115_, p_229116_, p_229188_);
        });
        this.verticalPlacement = net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.byName(p_229116_.getString("VerticalPlacement"));
        DataResult var10001 = net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.Properties.CODEC.parse(new Dynamic(NbtOps.INSTANCE, p_229116_.get("Properties")));
        Logger var10003 = LOGGER;
        Objects.requireNonNull(var10003);
        this.properties = (Properties)var10001.getOrThrow(true, var10003::error);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext p_229158_, CompoundTag p_229159_) {
        super.addAdditionalSaveData(p_229158_, p_229159_);
        p_229159_.putString("Rotation", this.placeSettings.getRotation().name());
        p_229159_.putString("Mirror", this.placeSettings.getMirror().name());
        p_229159_.putString("VerticalPlacement", this.verticalPlacement.getName());
        DataResult var10000 = net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.Properties.CODEC.encodeStart(NbtOps.INSTANCE, this.properties);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((p_229177_) -> {
            p_229159_.put("Properties", p_229177_);
        });
    }

    private static StructurePlaceSettings makeSettings(StructureTemplateManager p_229166_, CompoundTag p_229167_, ResourceLocation p_229168_) {
        StructureTemplate $$3 = p_229166_.getOrCreate(p_229168_);
        BlockPos $$4 = new BlockPos($$3.getSize().getX() / 2, 0, $$3.getSize().getZ() / 2);
        Mirror var10000 = Mirror.valueOf(p_229167_.getString("Mirror"));
        Rotation var10001 = Rotation.valueOf(p_229167_.getString("Rotation"));
        VerticalPlacement var10002 = net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.byName(p_229167_.getString("VerticalPlacement"));
        DataResult var10004 = net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.Properties.CODEC.parse(new Dynamic(NbtOps.INSTANCE, p_229167_.get("Properties")));
        Logger var10006 = LOGGER;
        Objects.requireNonNull(var10006);
        return makeSettings(var10000, var10001, var10002, $$4, (Properties)var10004.getOrThrow(true, var10006::error));
    }

    private static StructurePlaceSettings makeSettings(Mirror p_229152_, Rotation p_229153_, VerticalPlacement p_229154_, BlockPos p_229155_, Properties p_229156_) {
        BlockIgnoreProcessor $$5 = p_229156_.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
        List<ProcessorRule> $$6 = Lists.newArrayList();
        $$6.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
        $$6.add(getLavaProcessorRule(p_229154_, p_229156_));
        if (!p_229156_.cold) {
            $$6.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
        }

        StructurePlaceSettings $$7 = (new StructurePlaceSettings()).setRotation(p_229153_).setMirror(p_229152_).setRotationPivot(p_229155_).addProcessor($$5).addProcessor(new RuleProcessor($$6)).addProcessor(new BlockAgeProcessor(p_229156_.mossiness)).addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)).addProcessor(new LavaSubmergedBlockProcessor());
        if (p_229156_.replaceWithBlackstone) {
            $$7.addProcessor(BlackstoneReplaceProcessor.INSTANCE);
        }

        return $$7;
    }

    private static ProcessorRule getLavaProcessorRule(VerticalPlacement p_229163_, Properties p_229164_) {
        if (p_229163_ == net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR) {
            return getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        } else {
            return p_229164_.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
        }
    }

    public void postProcess(WorldGenLevel p_229137_, StructureManager p_229138_, ChunkGenerator p_229139_, RandomSource p_229140_, BoundingBox p_229141_, ChunkPos p_229142_, BlockPos p_229143_) {
        BoundingBox $$7 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (p_229141_.isInside($$7.getCenter())) {
            p_229141_.encapsulate($$7);
            super.postProcess(p_229137_, p_229138_, p_229139_, p_229140_, p_229141_, p_229142_, p_229143_);
            this.spreadNetherrack(p_229140_, p_229137_);
            this.addNetherrackDripColumnsBelowPortal(p_229140_, p_229137_);
            if (this.properties.vines || this.properties.overgrown) {
                BlockPos.betweenClosedStream(this.getBoundingBox()).forEach((p_229127_) -> {
                    if (this.properties.vines) {
                        this.maybeAddVines(p_229140_, p_229137_, p_229127_);
                    }

                    if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(p_229140_, p_229137_, p_229127_);
                    }

                });
            }

        }
    }

    protected void handleDataMarker(String p_229170_, BlockPos p_229171_, ServerLevelAccessor p_229172_, RandomSource p_229173_, BoundingBox p_229174_) {
    }

    private void maybeAddVines(RandomSource p_229121_, LevelAccessor p_229122_, BlockPos p_229123_) {
        BlockState $$3 = p_229122_.getBlockState(p_229123_);
        if (!$$3.isAir() && !$$3.is(Blocks.VINE)) {
            Direction $$4 = getRandomHorizontalDirection(p_229121_);
            BlockPos $$5 = p_229123_.relative($$4);
            BlockState $$6 = p_229122_.getBlockState($$5);
            if ($$6.isAir()) {
                if (Block.isFaceFull($$3.getCollisionShape(p_229122_, p_229123_), $$4)) {
                    BooleanProperty $$7 = VineBlock.getPropertyForFace($$4.getOpposite());
                    p_229122_.setBlock($$5, (BlockState)Blocks.VINE.defaultBlockState().setValue($$7, true), 3);
                }
            }
        }
    }

    private void maybeAddLeavesAbove(RandomSource p_229182_, LevelAccessor p_229183_, BlockPos p_229184_) {
        if (p_229182_.nextFloat() < 0.5F && p_229183_.getBlockState(p_229184_).is(Blocks.NETHERRACK) && p_229183_.getBlockState(p_229184_.above()).isAir()) {
            p_229183_.setBlock(p_229184_.above(), (BlockState)Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
        }

    }

    private void addNetherrackDripColumnsBelowPortal(RandomSource p_229118_, LevelAccessor p_229119_) {
        for(int $$2 = this.boundingBox.minX() + 1; $$2 < this.boundingBox.maxX(); ++$$2) {
            for(int $$3 = this.boundingBox.minZ() + 1; $$3 < this.boundingBox.maxZ(); ++$$3) {
                BlockPos $$4 = new BlockPos($$2, this.boundingBox.minY(), $$3);
                if (p_229119_.getBlockState($$4).is(Blocks.NETHERRACK)) {
                    this.addNetherrackDripColumn(p_229118_, p_229119_, $$4.below());
                }
            }
        }

    }

    private void addNetherrackDripColumn(RandomSource p_229190_, LevelAccessor p_229191_, BlockPos p_229192_) {
        BlockPos.MutableBlockPos $$3 = p_229192_.mutable();
        this.placeNetherrackOrMagma(p_229190_, p_229191_, $$3);
        int $$4 = 8;

        while($$4 > 0 && p_229190_.nextFloat() < 0.5F) {
            $$3.move(Direction.DOWN);
            --$$4;
            this.placeNetherrackOrMagma(p_229190_, p_229191_, $$3);
        }

    }

    private void spreadNetherrack(RandomSource p_229179_, LevelAccessor p_229180_) {
        boolean $$2 = this.verticalPlacement == net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR;
        BlockPos $$3 = this.boundingBox.getCenter();
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        float[] $$6 = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
        int $$7 = $$6.length;
        int $$8 = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
        int $$9 = p_229179_.nextInt(Math.max(1, 8 - $$8 / 2));
        int $$10 = true;
        BlockPos.MutableBlockPos $$11 = BlockPos.ZERO.mutable();

        for(int $$12 = $$4 - $$7; $$12 <= $$4 + $$7; ++$$12) {
            for(int $$13 = $$5 - $$7; $$13 <= $$5 + $$7; ++$$13) {
                int $$14 = Math.abs($$12 - $$4) + Math.abs($$13 - $$5);
                int $$15 = Math.max(0, $$14 + $$9);
                if ($$15 < $$7) {
                    float $$16 = $$6[$$15];
                    if (p_229179_.nextDouble() < (double)$$16) {
                        int $$17 = getSurfaceY(p_229180_, $$12, $$13, this.verticalPlacement);
                        int $$18 = $$2 ? $$17 : Math.min(this.boundingBox.minY(), $$17);
                        $$11.set($$12, $$18, $$13);
                        if (Math.abs($$18 - this.boundingBox.minY()) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(p_229180_, $$11)) {
                            this.placeNetherrackOrMagma(p_229179_, p_229180_, $$11);
                            if (this.properties.overgrown) {
                                this.maybeAddLeavesAbove(p_229179_, p_229180_, $$11);
                            }

                            this.addNetherrackDripColumn(p_229179_, p_229180_, $$11.below());
                        }
                    }
                }
            }
        }

    }

    private boolean canBlockBeReplacedByNetherrackOrMagma(LevelAccessor p_229134_, BlockPos p_229135_) {
        BlockState $$2 = p_229134_.getBlockState(p_229135_);
        return !$$2.is(Blocks.AIR) && !$$2.is(Blocks.OBSIDIAN) && !$$2.is(BlockTags.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.IN_NETHER || !$$2.is(Blocks.LAVA));
    }

    private void placeNetherrackOrMagma(RandomSource p_229194_, LevelAccessor p_229195_, BlockPos p_229196_) {
        if (!this.properties.cold && p_229194_.nextFloat() < 0.07F) {
            p_229195_.setBlock(p_229196_, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
        } else {
            p_229195_.setBlock(p_229196_, Blocks.NETHERRACK.defaultBlockState(), 3);
        }

    }

    private static int getSurfaceY(LevelAccessor p_229129_, int p_229130_, int p_229131_, VerticalPlacement p_229132_) {
        return p_229129_.getHeight(getHeightMapType(p_229132_), p_229130_, p_229131_) - 1;
    }

    public static Heightmap.Types getHeightMapType(VerticalPlacement p_229161_) {
        return p_229161_ == net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR ? Types.OCEAN_FLOOR_WG : Types.WORLD_SURFACE_WG;
    }

    private static ProcessorRule getBlockReplaceRule(Block p_229145_, float p_229146_, Block p_229147_) {
        return new ProcessorRule(new RandomBlockMatchTest(p_229145_, p_229146_), AlwaysTrueTest.INSTANCE, p_229147_.defaultBlockState());
    }

    private static ProcessorRule getBlockReplaceRule(Block p_229149_, Block p_229150_) {
        return new ProcessorRule(new BlockMatchTest(p_229149_), AlwaysTrueTest.INSTANCE, p_229150_.defaultBlockState());
    }

    public static enum VerticalPlacement implements StringRepresentable {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        public static final StringRepresentable.EnumCodec<VerticalPlacement> CODEC = StringRepresentable.fromEnum(VerticalPlacement::values);
        private final String name;

        private VerticalPlacement(String p_229240_) {
            this.name = p_229240_;
        }

        public String getName() {
            return this.name;
        }

        public static VerticalPlacement byName(String p_229243_) {
            return (VerticalPlacement)CODEC.byName(p_229243_);
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create((p_229214_) -> {
            return p_229214_.group(Codec.BOOL.fieldOf("cold").forGetter((p_229226_) -> {
                return p_229226_.cold;
            }), Codec.FLOAT.fieldOf("mossiness").forGetter((p_229224_) -> {
                return p_229224_.mossiness;
            }), Codec.BOOL.fieldOf("air_pocket").forGetter((p_229222_) -> {
                return p_229222_.airPocket;
            }), Codec.BOOL.fieldOf("overgrown").forGetter((p_229220_) -> {
                return p_229220_.overgrown;
            }), Codec.BOOL.fieldOf("vines").forGetter((p_229218_) -> {
                return p_229218_.vines;
            }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((p_229216_) -> {
                return p_229216_.replaceWithBlackstone;
            })).apply(p_229214_, Properties::new);
        });
        public boolean cold;
        public float mossiness;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public Properties() {
        }

        public Properties(boolean p_229207_, float p_229208_, boolean p_229209_, boolean p_229210_, boolean p_229211_, boolean p_229212_) {
            this.cold = p_229207_;
            this.mossiness = p_229208_;
            this.airPocket = p_229209_;
            this.overgrown = p_229210_;
            this.vines = p_229211_;
            this.replaceWithBlackstone = p_229212_;
        }
    }
}
