//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;

public class StructurePiecesBuilder implements StructurePieceAccessor {
    private final List<StructurePiece> pieces = Lists.newArrayList();

    public StructurePiecesBuilder() {
    }

    public void addPiece(StructurePiece p_192791_) {
        this.pieces.add(p_192791_);
    }

    @Nullable
    public StructurePiece findCollisionPiece(BoundingBox p_192789_) {
        return StructurePiece.findCollisionPiece(this.pieces, p_192789_);
    }

    /** @deprecated */
    @Deprecated
    public void offsetPiecesVertically(int p_192782_) {
        Iterator var2 = this.pieces.iterator();

        while(var2.hasNext()) {
            StructurePiece $$1 = (StructurePiece)var2.next();
            $$1.move(0, p_192782_, 0);
        }

    }

    /** @deprecated */
    @Deprecated
    public int moveBelowSeaLevel(int p_226966_, int p_226967_, RandomSource p_226968_, int p_226969_) {
        int $$4 = p_226966_ - p_226969_;
        BoundingBox $$5 = this.getBoundingBox();
        int $$6 = $$5.getYSpan() + p_226967_ + 1;
        if ($$6 < $$4) {
            $$6 += p_226968_.nextInt($$4 - $$6);
        }

        int $$7 = $$6 - $$5.maxY();
        this.offsetPiecesVertically($$7);
        return $$7;
    }

    /** @deprecated */
    public void moveInsideHeights(RandomSource p_226971_, int p_226972_, int p_226973_) {
        BoundingBox $$3 = this.getBoundingBox();
        int $$4 = p_226973_ - p_226972_ + 1 - $$3.getYSpan();
        int $$6;
        if ($$4 > 1) {
            $$6 = p_226972_ + p_226971_.nextInt($$4);
        } else {
            $$6 = p_226972_;
        }

        int $$7 = $$6 - $$3.minY();
        this.offsetPiecesVertically($$7);
    }

    public PiecesContainer build() {
        return new PiecesContainer(this.pieces);
    }

    public void clear() {
        this.pieces.clear();
    }

    public boolean isEmpty() {
        return this.pieces.isEmpty();
    }

    public BoundingBox getBoundingBox() {
        return StructurePiece.createBoundingBox(this.pieces.stream());
    }
}
