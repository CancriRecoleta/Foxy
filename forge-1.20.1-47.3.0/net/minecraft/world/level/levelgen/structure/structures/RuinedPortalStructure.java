//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece.VerticalPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RuinedPortalStructure extends Structure {
    private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
    private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
    private static final float PROBABILITY_OF_GIANT_PORTAL = 0.05F;
    private static final int MIN_Y_INDEX = 15;
    private final List<Setup> setups;
    public static final Codec<RuinedPortalStructure> CODEC = RecordCodecBuilder.create((p_229304_) -> {
        return p_229304_.group(settingsCodec(p_229304_), ExtraCodecs.nonEmptyList(net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure.Setup.CODEC.listOf()).fieldOf("setups").forGetter((p_229299_) -> {
            return p_229299_.setups;
        })).apply(p_229304_, RuinedPortalStructure::new);
    });

    public RuinedPortalStructure(Structure.StructureSettings p_229260_, List<Setup> p_229261_) {
        super(p_229260_);
        this.setups = p_229261_;
    }

    public RuinedPortalStructure(Structure.StructureSettings p_229257_, Setup p_229258_) {
        this(p_229257_, List.of(p_229258_));
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_229285_) {
        RuinedPortalPiece.Properties $$1 = new RuinedPortalPiece.Properties();
        WorldgenRandom $$2 = p_229285_.random();
        Setup $$3 = null;
        if (this.setups.size() > 1) {
            float $$4 = 0.0F;

            Setup $$5;
            for(Iterator var6 = this.setups.iterator(); var6.hasNext(); $$4 += $$5.weight()) {
                $$5 = (Setup)var6.next();
            }

            float $$6 = $$2.nextFloat();
            Iterator var23 = this.setups.iterator();

            while(var23.hasNext()) {
                Setup $$7 = (Setup)var23.next();
                $$6 -= $$7.weight() / $$4;
                if ($$6 < 0.0F) {
                    $$3 = $$7;
                    break;
                }
            }
        } else {
            $$3 = (Setup)this.setups.get(0);
        }

        if ($$3 == null) {
            throw new IllegalStateException();
        } else {
            Setup $$8 = $$3;
            $$1.airPocket = sample($$2, $$8.airPocketProbability());
            $$1.mossiness = $$8.mossiness();
            $$1.overgrown = $$8.overgrown();
            $$1.vines = $$8.vines();
            $$1.replaceWithBlackstone = $$8.replaceWithBlackstone();
            ResourceLocation $$10;
            if ($$2.nextFloat() < 0.05F) {
                $$10 = new ResourceLocation(STRUCTURE_LOCATION_GIANT_PORTALS[$$2.nextInt(STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
            } else {
                $$10 = new ResourceLocation(STRUCTURE_LOCATION_PORTALS[$$2.nextInt(STRUCTURE_LOCATION_PORTALS.length)]);
            }

            StructureTemplate $$11 = p_229285_.structureTemplateManager().getOrCreate($$10);
            Rotation $$12 = (Rotation)Util.getRandom((Object[])Rotation.values(), $$2);
            Mirror $$13 = $$2.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
            BlockPos $$14 = new BlockPos($$11.getSize().getX() / 2, 0, $$11.getSize().getZ() / 2);
            ChunkGenerator $$15 = p_229285_.chunkGenerator();
            LevelHeightAccessor $$16 = p_229285_.heightAccessor();
            RandomState $$17 = p_229285_.randomState();
            BlockPos $$18 = p_229285_.chunkPos().getWorldPosition();
            BoundingBox $$19 = $$11.getBoundingBox($$18, $$12, $$14, $$13);
            BlockPos $$20 = $$19.getCenter();
            int $$21 = $$15.getBaseHeight($$20.getX(), $$20.getZ(), RuinedPortalPiece.getHeightMapType($$8.placement()), $$16, $$17) - 1;
            int $$22 = findSuitableY($$2, $$15, $$8.placement(), $$1.airPocket, $$21, $$19.getYSpan(), $$19, $$16, $$17);
            BlockPos $$23 = new BlockPos($$18.getX(), $$22, $$18.getZ());
            return Optional.of(new Structure.GenerationStub($$23, (p_229297_) -> {
                if ($$8.canBeCold()) {
                    $$1.cold = isCold($$23, p_229285_.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock($$23.getX()), QuartPos.fromBlock($$23.getY()), QuartPos.fromBlock($$23.getZ()), $$17.sampler()));
                }

                p_229297_.addPiece(new RuinedPortalPiece(p_229285_.structureTemplateManager(), $$23, $$8.placement(), $$1, $$10, $$11, $$12, $$13, $$14));
            }));
        }
    }

    private static boolean sample(WorldgenRandom p_229282_, float p_229283_) {
        if (p_229283_ == 0.0F) {
            return false;
        } else if (p_229283_ == 1.0F) {
            return true;
        } else {
            return p_229282_.nextFloat() < p_229283_;
        }
    }

    private static boolean isCold(BlockPos p_229301_, Holder<Biome> p_229302_) {
        return ((Biome)p_229302_.value()).coldEnoughToSnow(p_229301_);
    }

    private static int findSuitableY(RandomSource p_229267_, ChunkGenerator p_229268_, RuinedPortalPiece.VerticalPlacement p_229269_, boolean p_229270_, int p_229271_, int p_229272_, BoundingBox p_229273_, LevelHeightAccessor p_229274_, RandomState p_229275_) {
        int $$9 = p_229274_.getMinBuildHeight() + 15;
        int $$18;
        if (p_229269_ == VerticalPlacement.IN_NETHER) {
            if (p_229270_) {
                $$18 = Mth.randomBetweenInclusive(p_229267_, 32, 100);
            } else if (p_229267_.nextFloat() < 0.5F) {
                $$18 = Mth.randomBetweenInclusive(p_229267_, 27, 29);
            } else {
                $$18 = Mth.randomBetweenInclusive(p_229267_, 29, 100);
            }
        } else {
            int $$15;
            if (p_229269_ == VerticalPlacement.IN_MOUNTAIN) {
                $$15 = p_229271_ - p_229272_;
                $$18 = getRandomWithinInterval(p_229267_, 70, $$15);
            } else if (p_229269_ == VerticalPlacement.UNDERGROUND) {
                $$15 = p_229271_ - p_229272_;
                $$18 = getRandomWithinInterval(p_229267_, $$9, $$15);
            } else if (p_229269_ == VerticalPlacement.PARTLY_BURIED) {
                $$18 = p_229271_ - p_229272_ + Mth.randomBetweenInclusive(p_229267_, 2, 8);
            } else {
                $$18 = p_229271_;
            }
        }

        List<BlockPos> $$19 = ImmutableList.of(new BlockPos(p_229273_.minX(), 0, p_229273_.minZ()), new BlockPos(p_229273_.maxX(), 0, p_229273_.minZ()), new BlockPos(p_229273_.minX(), 0, p_229273_.maxZ()), new BlockPos(p_229273_.maxX(), 0, p_229273_.maxZ()));
        List<NoiseColumn> $$20 = (List)$$19.stream().map((p_229280_) -> {
            return p_229268_.getBaseColumn(p_229280_.getX(), p_229280_.getZ(), p_229274_, p_229275_);
        }).collect(Collectors.toList());
        Heightmap.Types $$21 = p_229269_ == VerticalPlacement.ON_OCEAN_FLOOR ? Types.OCEAN_FLOOR_WG : Types.WORLD_SURFACE_WG;

        int $$22;
        for($$22 = $$18; $$22 > $$9; --$$22) {
            int $$23 = 0;
            Iterator var16 = $$20.iterator();

            while(var16.hasNext()) {
                NoiseColumn $$24 = (NoiseColumn)var16.next();
                BlockState $$25 = $$24.getBlock($$22);
                if ($$21.isOpaque().test($$25)) {
                    ++$$23;
                    if ($$23 == 3) {
                        return $$22;
                    }
                }
            }
        }

        return $$22;
    }

    private static int getRandomWithinInterval(RandomSource p_229263_, int p_229264_, int p_229265_) {
        return p_229264_ < p_229265_ ? Mth.randomBetweenInclusive(p_229263_, p_229264_, p_229265_) : p_229265_;
    }

    public StructureType<?> type() {
        return StructureType.RUINED_PORTAL;
    }

    public static record Setup(RuinedPortalPiece.VerticalPlacement placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {
        public static final Codec<Setup> CODEC = RecordCodecBuilder.create((p_229327_) -> {
            return p_229327_.group(VerticalPlacement.CODEC.fieldOf("placement").forGetter(Setup::placement), Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(Setup::airPocketProbability), Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(Setup::mossiness), Codec.BOOL.fieldOf("overgrown").forGetter(Setup::overgrown), Codec.BOOL.fieldOf("vines").forGetter(Setup::vines), Codec.BOOL.fieldOf("can_be_cold").forGetter(Setup::canBeCold), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(Setup::replaceWithBlackstone), ExtraCodecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(Setup::weight)).apply(p_229327_, Setup::new);
        });

        public Setup(RuinedPortalPiece.VerticalPlacement placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {
            this.placement = placement;
            this.airPocketProbability = airPocketProbability;
            this.mossiness = mossiness;
            this.overgrown = overgrown;
            this.vines = vines;
            this.canBeCold = canBeCold;
            this.replaceWithBlackstone = replaceWithBlackstone;
            this.weight = weight;
        }

        public RuinedPortalPiece.VerticalPlacement placement() {
            return this.placement;
        }

        public float airPocketProbability() {
            return this.airPocketProbability;
        }

        public float mossiness() {
            return this.mossiness;
        }

        public boolean overgrown() {
            return this.overgrown;
        }

        public boolean vines() {
            return this.vines;
        }

        public boolean canBeCold() {
            return this.canBeCold;
        }

        public boolean replaceWithBlackstone() {
            return this.replaceWithBlackstone;
        }

        public float weight() {
            return this.weight;
        }
    }
}
