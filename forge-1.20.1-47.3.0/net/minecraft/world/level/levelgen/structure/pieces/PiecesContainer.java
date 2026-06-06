//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.slf4j.Logger;

public record PiecesContainer(List<StructurePiece> pieces) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
    private static final Map<ResourceLocation, ResourceLocation> RENAMES;

    public PiecesContainer(List<StructurePiece> pieces) {
        this.pieces = List.copyOf(pieces);
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public boolean isInsidePiece(BlockPos p_192752_) {
        Iterator var2 = this.pieces.iterator();

        StructurePiece $$1;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            $$1 = (StructurePiece)var2.next();
        } while(!$$1.getBoundingBox().isInside(p_192752_));

        return true;
    }

    public Tag save(StructurePieceSerializationContext p_192750_) {
        ListTag $$1 = new ListTag();
        Iterator var3 = this.pieces.iterator();

        while(var3.hasNext()) {
            StructurePiece $$2 = (StructurePiece)var3.next();
            $$1.add($$2.createTag(p_192750_));
        }

        return $$1;
    }

    public static PiecesContainer load(ListTag p_192754_, StructurePieceSerializationContext p_192755_) {
        List<StructurePiece> $$2 = Lists.newArrayList();

        for(int $$3 = 0; $$3 < p_192754_.size(); ++$$3) {
            CompoundTag $$4 = p_192754_.getCompound($$3);
            String $$5 = $$4.getString("id").toLowerCase(Locale.ROOT);
            ResourceLocation $$6 = new ResourceLocation($$5);
            ResourceLocation $$7 = (ResourceLocation)RENAMES.getOrDefault($$6, $$6);
            StructurePieceType $$8 = (StructurePieceType)BuiltInRegistries.STRUCTURE_PIECE.get($$7);
            if ($$8 == null) {
                LOGGER.error("Unknown structure piece id: {}", $$7);
            } else {
                try {
                    StructurePiece $$9 = $$8.load(p_192755_, $$4);
                    $$2.add($$9);
                } catch (Exception var10) {
                    Exception $$10 = var10;
                    LOGGER.error("Exception loading structure piece with id {}", $$7, $$10);
                }
            }
        }

        return new PiecesContainer($$2);
    }

    public BoundingBox calculateBoundingBox() {
        return StructurePiece.createBoundingBox(this.pieces.stream());
    }

    public List<StructurePiece> pieces() {
        return this.pieces;
    }

    static {
        RENAMES = ImmutableMap.builder().put(new ResourceLocation("nvi"), JIGSAW_RENAME).put(new ResourceLocation("pcp"), JIGSAW_RENAME).put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME).put(new ResourceLocation("runtime"), JIGSAW_RENAME).build();
    }
}
