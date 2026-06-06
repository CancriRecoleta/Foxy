//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure;

import java.util.Optional;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public abstract class SinglePieceStructure extends Structure {
    private final PieceConstructor constructor;
    private final int width;
    private final int depth;

    protected SinglePieceStructure(PieceConstructor p_226537_, int p_226538_, int p_226539_, Structure.StructureSettings p_226540_) {
        super(p_226540_);
        this.constructor = p_226537_;
        this.width = p_226538_;
        this.depth = p_226539_;
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_226542_) {
        return getLowestY(p_226542_, this.width, this.depth) < p_226542_.chunkGenerator().getSeaLevel() ? Optional.empty() : onTopOfChunkCenter(p_226542_, Types.WORLD_SURFACE_WG, (p_226545_) -> {
            this.generatePieces(p_226545_, p_226542_);
        });
    }

    private void generatePieces(StructurePiecesBuilder p_226547_, Structure.GenerationContext p_226548_) {
        ChunkPos $$2 = p_226548_.chunkPos();
        p_226547_.addPiece(this.constructor.construct(p_226548_.random(), $$2.getMinBlockX(), $$2.getMinBlockZ()));
    }

    @FunctionalInterface
    protected interface PieceConstructor {
        StructurePiece construct(WorldgenRandom var1, int var2, int var3);
    }
}
