//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.structures;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class NetherFortressStructure extends Structure {
    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES;
    public static final Codec<NetherFortressStructure> CODEC;

    public NetherFortressStructure(Structure.StructureSettings p_228521_) {
        super(p_228521_);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext p_228523_) {
        ChunkPos $$1 = p_228523_.chunkPos();
        BlockPos $$2 = new BlockPos($$1.getMinBlockX(), 64, $$1.getMinBlockZ());
        return Optional.of(new Structure.GenerationStub($$2, (p_228526_) -> {
            generatePieces(p_228526_, p_228523_);
        }));
    }

    private static void generatePieces(StructurePiecesBuilder p_228528_, Structure.GenerationContext p_228529_) {
        NetherFortressPieces.StartPiece $$2 = new NetherFortressPieces.StartPiece(p_228529_.random(), p_228529_.chunkPos().getBlockX(2), p_228529_.chunkPos().getBlockZ(2));
        p_228528_.addPiece($$2);
        $$2.addChildren($$2, p_228528_, p_228529_.random());
        List<StructurePiece> $$3 = $$2.pendingChildren;

        while(!$$3.isEmpty()) {
            int $$4 = p_228529_.random().nextInt($$3.size());
            StructurePiece $$5 = (StructurePiece)$$3.remove($$4);
            $$5.addChildren($$2, p_228528_, p_228529_.random());
        }

        p_228528_.moveInsideHeights(p_228529_.random(), 48, 70);
    }

    public StructureType<?> type() {
        return StructureType.FORTRESS;
    }

    static {
        FORTRESS_ENEMIES = WeightedRandomList.create((WeightedEntry[])(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)));
        CODEC = simpleCodec(NetherFortressStructure::new);
    }
}
