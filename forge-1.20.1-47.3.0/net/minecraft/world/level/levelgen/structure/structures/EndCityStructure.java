//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class EndCityStructure extends Structure {
    public static final Codec<EndCityStructure> CODEC = simpleCodec(EndCityStructure::new);

    public EndCityStructure(Structure.StructureSettings p_227526_) {
        super(p_227526_);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_227528_) {
        Rotation $$1 = Rotation.getRandom(p_227528_.random());
        BlockPos $$2 = this.getLowestYIn5by5BoxOffset7Blocks(p_227528_, $$1);
        return $$2.getY() < 60 ? Optional.empty() : Optional.of(new Structure.GenerationStub($$2, (p_227538_) -> {
            this.generatePieces(p_227538_, $$2, $$1, p_227528_);
        }));
    }

    private void generatePieces(StructurePiecesBuilder p_227530_, BlockPos p_227531_, Rotation p_227532_, Structure.GenerationContext p_227533_) {
        List<StructurePiece> $$4 = Lists.newArrayList();
        EndCityPieces.startHouseTower(p_227533_.structureTemplateManager(), p_227531_, p_227532_, $$4, p_227533_.random());
        Objects.requireNonNull(p_227530_);
        $$4.forEach(p_227530_::addPiece);
    }

    public StructureType<?> type() {
        return StructureType.END_CITY;
    }
}
