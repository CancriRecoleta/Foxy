//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class StructureManager {
    private final LevelAccessor level;
    private final WorldOptions worldOptions;
    private final StructureCheck structureCheck;

    public StructureManager(LevelAccessor p_249675_, WorldOptions p_248820_, StructureCheck p_249103_) {
        this.level = p_249675_;
        this.worldOptions = p_248820_;
        this.structureCheck = p_249103_;
    }

    public StructureManager forWorldGenRegion(WorldGenRegion p_220469_) {
        if (p_220469_.getLevel() != this.level) {
            ServerLevel var10002 = p_220469_.getLevel();
            throw new IllegalStateException("Using invalid structure manager (source level: " + var10002 + ", region: " + p_220469_);
        } else {
            return new StructureManager(p_220469_, this.worldOptions, this.structureCheck);
        }
    }

    public List<StructureStart> startsForStructure(ChunkPos p_220478_, Predicate<Structure> p_220479_) {
        Map<Structure, LongSet> $$2 = this.level.getChunk(p_220478_.x, p_220478_.z, ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
        ImmutableList.Builder<StructureStart> $$3 = ImmutableList.builder();
        Iterator var5 = $$2.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<Structure, LongSet> $$4 = (Map.Entry)var5.next();
            Structure $$5 = (Structure)$$4.getKey();
            if (p_220479_.test($$5)) {
                LongSet var10002 = (LongSet)$$4.getValue();
                Objects.requireNonNull($$3);
                this.fillStartsForStructure($$5, var10002, $$3::add);
            }
        }

        return $$3.build();
    }

    public List<StructureStart> startsForStructure(SectionPos p_220505_, Structure p_220506_) {
        LongSet $$2 = this.level.getChunk(p_220505_.x(), p_220505_.z(), ChunkStatus.STRUCTURE_REFERENCES).getReferencesForStructure(p_220506_);
        ImmutableList.Builder<StructureStart> $$3 = ImmutableList.builder();
        Objects.requireNonNull($$3);
        this.fillStartsForStructure(p_220506_, $$2, $$3::add);
        return $$3.build();
    }

    public void fillStartsForStructure(Structure p_220481_, LongSet p_220482_, Consumer<StructureStart> p_220483_) {
        LongIterator var4 = p_220482_.iterator();

        while(var4.hasNext()) {
            long $$3 = (Long)var4.next();
            SectionPos $$4 = SectionPos.of(new ChunkPos($$3), this.level.getMinSection());
            StructureStart $$5 = this.getStartForStructure($$4, p_220481_, this.level.getChunk($$4.x(), $$4.z(), ChunkStatus.STRUCTURE_STARTS));
            if ($$5 != null && $$5.isValid()) {
                p_220483_.accept($$5);
            }
        }

    }

    @Nullable
    public StructureStart getStartForStructure(SectionPos p_220513_, Structure p_220514_, StructureAccess p_220515_) {
        return p_220515_.getStartForStructure(p_220514_);
    }

    public void setStartForStructure(SectionPos p_220517_, Structure p_220518_, StructureStart p_220519_, StructureAccess p_220520_) {
        p_220520_.setStartForStructure(p_220518_, p_220519_);
    }

    public void addReferenceForStructure(SectionPos p_220508_, Structure p_220509_, long p_220510_, StructureAccess p_220511_) {
        p_220511_.addReferenceForStructure(p_220509_, p_220510_);
    }

    public boolean shouldGenerateStructures() {
        return this.worldOptions.generateStructures();
    }

    public StructureStart getStructureAt(BlockPos p_220495_, Structure p_220496_) {
        Iterator var3 = this.startsForStructure(SectionPos.of(p_220495_), p_220496_).iterator();

        StructureStart $$2;
        do {
            if (!var3.hasNext()) {
                return StructureStart.INVALID_START;
            }

            $$2 = (StructureStart)var3.next();
        } while(!$$2.getBoundingBox().isInside(p_220495_));

        return $$2;
    }

    public StructureStart getStructureWithPieceAt(BlockPos p_220489_, ResourceKey<Structure> p_220490_) {
        Structure $$2 = (Structure)this.registryAccess().registryOrThrow(Registries.STRUCTURE).get(p_220490_);
        return $$2 == null ? StructureStart.INVALID_START : this.getStructureWithPieceAt(p_220489_, $$2);
    }

    public StructureStart getStructureWithPieceAt(BlockPos p_220492_, TagKey<Structure> p_220493_) {
        Registry<Structure> $$2 = this.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Iterator var4 = this.startsForStructure(new ChunkPos(p_220492_), (p_258967_) -> {
            return (Boolean)$$2.getHolder($$2.getId(p_258967_)).map((p_248425_) -> {
                return p_248425_.is(p_220493_);
            }).orElse(false);
        }).iterator();

        StructureStart $$3;
        do {
            if (!var4.hasNext()) {
                return StructureStart.INVALID_START;
            }

            $$3 = (StructureStart)var4.next();
        } while(!this.structureHasPieceAt(p_220492_, $$3));

        return $$3;
    }

    public StructureStart getStructureWithPieceAt(BlockPos p_220525_, Structure p_220526_) {
        Iterator var3 = this.startsForStructure(SectionPos.of(p_220525_), p_220526_).iterator();

        StructureStart $$2;
        do {
            if (!var3.hasNext()) {
                return StructureStart.INVALID_START;
            }

            $$2 = (StructureStart)var3.next();
        } while(!this.structureHasPieceAt(p_220525_, $$2));

        return $$2;
    }

    public boolean structureHasPieceAt(BlockPos p_220498_, StructureStart p_220499_) {
        Iterator var3 = p_220499_.getPieces().iterator();

        StructurePiece $$2;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            $$2 = (StructurePiece)var3.next();
        } while(!$$2.getBoundingBox().isInside(p_220498_));

        return true;
    }

    public boolean hasAnyStructureAt(BlockPos p_220487_) {
        SectionPos $$1 = SectionPos.of(p_220487_);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).hasAnyStructureReferences();
    }

    public Map<Structure, LongSet> getAllStructuresAt(BlockPos p_220523_) {
        SectionPos $$1 = SectionPos.of(p_220523_);
        return this.level.getChunk($$1.x(), $$1.z(), ChunkStatus.STRUCTURE_REFERENCES).getAllReferences();
    }

    public StructureCheckResult checkStructurePresence(ChunkPos p_220474_, Structure p_220475_, boolean p_220476_) {
        return this.structureCheck.checkStart(p_220474_, p_220475_, p_220476_);
    }

    public void addReference(StructureStart p_220485_) {
        p_220485_.addReference();
        this.structureCheck.incrementReference(p_220485_.getChunkPos(), p_220485_.getStructure());
    }

    public RegistryAccess registryAccess() {
        return this.level.registryAccess();
    }
}
