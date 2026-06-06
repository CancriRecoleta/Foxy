//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanMonumentStructure extends Structure {
    public static final Codec<OceanMonumentStructure> CODEC = simpleCodec(OceanMonumentStructure::new);

    public OceanMonumentStructure(Structure.StructureSettings p_228955_) {
        super(p_228955_);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_228964_) {
        int $$1 = p_228964_.chunkPos().getBlockX(9);
        int $$2 = p_228964_.chunkPos().getBlockZ(9);
        Set<Holder<Biome>> $$3 = p_228964_.biomeSource().getBiomesWithin($$1, p_228964_.chunkGenerator().getSeaLevel(), $$2, 29, p_228964_.randomState().sampler());
        Iterator var5 = $$3.iterator();

        Holder $$4;
        do {
            if (!var5.hasNext()) {
                return onTopOfChunkCenter(p_228964_, Types.OCEAN_FLOOR_WG, (p_228967_) -> {
                    generatePieces(p_228967_, p_228964_);
                });
            }

            $$4 = (Holder)var5.next();
        } while($$4.is(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING));

        return Optional.empty();
    }

    private static StructurePiece createTopPiece(ChunkPos p_228961_, WorldgenRandom p_228962_) {
        int $$2 = p_228961_.getMinBlockX() - 29;
        int $$3 = p_228961_.getMinBlockZ() - 29;
        Direction $$4 = Plane.HORIZONTAL.getRandomDirection(p_228962_);
        return new OceanMonumentPieces.MonumentBuilding(p_228962_, $$2, $$3, $$4);
    }

    private static void generatePieces(StructurePiecesBuilder p_228969_, Structure.GenerationContext p_228970_) {
        p_228969_.addPiece(createTopPiece(p_228970_.chunkPos(), p_228970_.random()));
    }

    public static PiecesContainer regeneratePiecesAfterLoad(ChunkPos p_228957_, long p_228958_, PiecesContainer p_228959_) {
        if (p_228959_.isEmpty()) {
            return p_228959_;
        } else {
            WorldgenRandom $$3 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            $$3.setLargeFeatureSeed(p_228958_, p_228957_.x, p_228957_.z);
            StructurePiece $$4 = (StructurePiece)p_228959_.pieces().get(0);
            BoundingBox $$5 = $$4.getBoundingBox();
            int $$6 = $$5.minX();
            int $$7 = $$5.minZ();
            Direction $$8 = Plane.HORIZONTAL.getRandomDirection($$3);
            Direction $$9 = (Direction)Objects.requireNonNullElse($$4.getOrientation(), $$8);
            StructurePiece $$10 = new OceanMonumentPieces.MonumentBuilding($$3, $$6, $$7, $$9);
            StructurePiecesBuilder $$11 = new StructurePiecesBuilder();
            $$11.addPiece($$10);
            return $$11.build();
        }
    }

    public StructureType<?> type() {
        return StructureType.OCEAN_MONUMENT;
    }
}
