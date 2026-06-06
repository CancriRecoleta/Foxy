//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GridLayout extends AbstractLayout {
    private final List<LayoutElement> children;
    private final List<CellInhabitant> cellInhabitants;
    private final LayoutSettings defaultCellSettings;
    private int rowSpacing;
    private int columnSpacing;

    public GridLayout() {
        this(0, 0);
    }

    public GridLayout(int p_265045_, int p_265035_) {
        super(p_265045_, p_265035_, 0, 0);
        this.children = new ArrayList();
        this.cellInhabitants = new ArrayList();
        this.defaultCellSettings = LayoutSettings.defaults();
        this.rowSpacing = 0;
        this.columnSpacing = 0;
    }

    public void arrangeElements() {
        super.arrangeElements();
        int $$0 = 0;
        int $$1 = 0;

        CellInhabitant $$2;
        for(Iterator var3 = this.cellInhabitants.iterator(); var3.hasNext(); $$1 = Math.max($$2.getLastOccupiedColumn(), $$1)) {
            $$2 = (CellInhabitant)var3.next();
            $$0 = Math.max($$2.getLastOccupiedRow(), $$0);
        }

        int[] $$3 = new int[$$1 + 1];
        int[] $$4 = new int[$$0 + 1];
        Iterator var5 = this.cellInhabitants.iterator();

        int $$15;
        int $$17;
        int $$20;
        while(var5.hasNext()) {
            CellInhabitant $$5 = (CellInhabitant)var5.next();
            $$15 = $$5.getHeight() - ($$5.occupiedRows - 1) * this.rowSpacing;
            Divisor $$7 = new Divisor($$15, $$5.occupiedRows);

            for($$17 = $$5.row; $$17 <= $$5.getLastOccupiedRow(); ++$$17) {
                $$4[$$17] = Math.max($$4[$$17], $$7.nextInt());
            }

            $$17 = $$5.getWidth() - ($$5.occupiedColumns - 1) * this.columnSpacing;
            Divisor $$10 = new Divisor($$17, $$5.occupiedColumns);

            for($$20 = $$5.column; $$20 <= $$5.getLastOccupiedColumn(); ++$$20) {
                $$3[$$20] = Math.max($$3[$$20], $$10.nextInt());
            }
        }

        int[] $$12 = new int[$$1 + 1];
        int[] $$13 = new int[$$0 + 1];
        $$12[0] = 0;

        for($$15 = 1; $$15 <= $$1; ++$$15) {
            $$12[$$15] = $$12[$$15 - 1] + $$3[$$15 - 1] + this.columnSpacing;
        }

        $$13[0] = 0;

        for($$15 = 1; $$15 <= $$0; ++$$15) {
            $$13[$$15] = $$13[$$15 - 1] + $$4[$$15 - 1] + this.rowSpacing;
        }

        Iterator var17 = this.cellInhabitants.iterator();

        while(var17.hasNext()) {
            CellInhabitant $$16 = (CellInhabitant)var17.next();
            $$17 = 0;

            int $$19;
            for($$19 = $$16.column; $$19 <= $$16.getLastOccupiedColumn(); ++$$19) {
                $$17 += $$3[$$19];
            }

            $$17 += this.columnSpacing * ($$16.occupiedColumns - 1);
            $$16.setX(this.getX() + $$12[$$16.column], $$17);
            $$19 = 0;

            for($$20 = $$16.row; $$20 <= $$16.getLastOccupiedRow(); ++$$20) {
                $$19 += $$4[$$20];
            }

            $$19 += this.rowSpacing * ($$16.occupiedRows - 1);
            $$16.setY(this.getY() + $$13[$$16.row], $$19);
        }

        this.width = $$12[$$1] + $$3[$$1];
        this.height = $$13[$$0] + $$4[$$0];
    }

    public <T extends LayoutElement> T addChild(T p_265485_, int p_265720_, int p_265679_) {
        return this.addChild(p_265485_, p_265720_, p_265679_, this.newCellSettings());
    }

    public <T extends LayoutElement> T addChild(T p_265061_, int p_265080_, int p_265105_, LayoutSettings p_265057_) {
        return this.addChild(p_265061_, p_265080_, p_265105_, 1, 1, p_265057_);
    }

    public <T extends LayoutElement> T addChild(T p_265590_, int p_265556_, int p_265323_, int p_265531_, int p_265352_) {
        return this.addChild(p_265590_, p_265556_, p_265323_, p_265531_, p_265352_, this.newCellSettings());
    }

    public <T extends LayoutElement> T addChild(T p_265031_, int p_265582_, int p_265782_, int p_265612_, int p_265448_, LayoutSettings p_265579_) {
        if (p_265612_ < 1) {
            throw new IllegalArgumentException("Occupied rows must be at least 1");
        } else if (p_265448_ < 1) {
            throw new IllegalArgumentException("Occupied columns must be at least 1");
        } else {
            this.cellInhabitants.add(new CellInhabitant(p_265031_, p_265582_, p_265782_, p_265612_, p_265448_, p_265579_));
            this.children.add(p_265031_);
            return p_265031_;
        }
    }

    public GridLayout columnSpacing(int p_268135_) {
        this.columnSpacing = p_268135_;
        return this;
    }

    public GridLayout rowSpacing(int p_268237_) {
        this.rowSpacing = p_268237_;
        return this;
    }

    public GridLayout spacing(int p_268351_) {
        return this.columnSpacing(p_268351_).rowSpacing(p_268351_);
    }

    public void visitChildren(Consumer<LayoutElement> p_265389_) {
        this.children.forEach(p_265389_);
    }

    public LayoutSettings newCellSettings() {
        return this.defaultCellSettings.copy();
    }

    public LayoutSettings defaultCellSetting() {
        return this.defaultCellSettings;
    }

    public RowHelper createRowHelper(int p_265327_) {
        return new RowHelper(p_265327_);
    }

    @OnlyIn(Dist.CLIENT)
    static class CellInhabitant extends AbstractLayout.AbstractChildWrapper {
        final int row;
        final int column;
        final int occupiedRows;
        final int occupiedColumns;

        CellInhabitant(LayoutElement p_265063_, int p_265675_, int p_265198_, int p_265625_, int p_265517_, LayoutSettings p_265036_) {
            super(p_265063_, p_265036_.getExposed());
            this.row = p_265675_;
            this.column = p_265198_;
            this.occupiedRows = p_265625_;
            this.occupiedColumns = p_265517_;
        }

        public int getLastOccupiedRow() {
            return this.row + this.occupiedRows - 1;
        }

        public int getLastOccupiedColumn() {
            return this.column + this.occupiedColumns - 1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public final class RowHelper {
        private final int columns;
        private int index;

        RowHelper(int p_265633_) {
            this.columns = p_265633_;
        }

        public <T extends LayoutElement> T addChild(T p_265455_) {
            return this.addChild(p_265455_, 1);
        }

        public <T extends LayoutElement> T addChild(T p_265413_, int p_265491_) {
            return this.addChild(p_265413_, p_265491_, this.defaultCellSetting());
        }

        public <T extends LayoutElement> T addChild(T p_265411_, LayoutSettings p_265755_) {
            return this.addChild(p_265411_, 1, p_265755_);
        }

        public <T extends LayoutElement> T addChild(T p_265200_, int p_265044_, LayoutSettings p_265797_) {
            int $$3 = this.index / this.columns;
            int $$4 = this.index % this.columns;
            if ($$4 + p_265044_ > this.columns) {
                ++$$3;
                $$4 = 0;
                this.index = Mth.roundToward(this.index, this.columns);
            }

            this.index += p_265044_;
            return GridLayout.this.addChild(p_265200_, $$3, $$4, 1, p_265044_, p_265797_);
        }

        public GridLayout getGrid() {
            return GridLayout.this;
        }

        public LayoutSettings newCellSettings() {
            return GridLayout.this.newCellSettings();
        }

        public LayoutSettings defaultCellSetting() {
            return GridLayout.this.defaultCellSetting();
        }
    }
}
