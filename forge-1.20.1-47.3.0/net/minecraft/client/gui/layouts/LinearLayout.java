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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LinearLayout extends AbstractLayout {
    private final Orientation orientation;
    private final List<ChildContainer> children;
    private final LayoutSettings defaultChildLayoutSettings;

    public LinearLayout(int p_265093_, int p_265502_, Orientation p_265112_) {
        this(0, 0, p_265093_, p_265502_, p_265112_);
    }

    public LinearLayout(int p_265489_, int p_265500_, int p_265233_, int p_265301_, Orientation p_265341_) {
        super(p_265489_, p_265500_, p_265233_, p_265301_);
        this.children = new ArrayList();
        this.defaultChildLayoutSettings = LayoutSettings.defaults();
        this.orientation = p_265341_;
    }

    public void arrangeElements() {
        super.arrangeElements();
        if (!this.children.isEmpty()) {
            int $$0 = 0;
            int $$1 = this.orientation.getSecondaryLength((LayoutElement)this);

            ChildContainer $$2;
            for(Iterator var3 = this.children.iterator(); var3.hasNext(); $$1 = Math.max($$1, this.orientation.getSecondaryLength($$2))) {
                $$2 = (ChildContainer)var3.next();
                $$0 += this.orientation.getPrimaryLength($$2);
            }

            int $$3 = this.orientation.getPrimaryLength((LayoutElement)this) - $$0;
            int $$4 = this.orientation.getPrimaryPosition(this);
            Iterator<ChildContainer> $$5 = this.children.iterator();
            ChildContainer $$6 = (ChildContainer)$$5.next();
            this.orientation.setPrimaryPosition($$6, $$4);
            $$4 += this.orientation.getPrimaryLength($$6);
            ChildContainer $$8;
            if (this.children.size() >= 2) {
                for(Divisor $$7 = new Divisor($$3, this.children.size() - 1); $$7.hasNext(); $$4 += this.orientation.getPrimaryLength($$8)) {
                    $$4 += $$7.nextInt();
                    $$8 = (ChildContainer)$$5.next();
                    this.orientation.setPrimaryPosition($$8, $$4);
                }
            }

            int $$9 = this.orientation.getSecondaryPosition(this);
            Iterator var13 = this.children.iterator();

            while(var13.hasNext()) {
                ChildContainer $$10 = (ChildContainer)var13.next();
                this.orientation.setSecondaryPosition($$10, $$9, $$1);
            }

            switch (this.orientation) {
                case HORIZONTAL -> this.height = $$1;
                case VERTICAL -> this.width = $$1;
            }

        }
    }

    public void visitChildren(Consumer<LayoutElement> p_265508_) {
        this.children.forEach((p_265178_) -> {
            p_265508_.accept(p_265178_.child);
        });
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public <T extends LayoutElement> T addChild(T p_265140_) {
        return this.addChild(p_265140_, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T p_265475_, LayoutSettings p_265684_) {
        this.children.add(new ChildContainer(p_265475_, p_265684_));
        return p_265475_;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Orientation {
        HORIZONTAL,
        VERTICAL;

        private Orientation() {
        }

        int getPrimaryLength(LayoutElement p_265322_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265322_.getWidth();
                case VERTICAL -> var10000 = p_265322_.getHeight();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        int getPrimaryLength(ChildContainer p_265173_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265173_.getWidth();
                case VERTICAL -> var10000 = p_265173_.getHeight();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        int getSecondaryLength(LayoutElement p_265570_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265570_.getHeight();
                case VERTICAL -> var10000 = p_265570_.getWidth();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        int getSecondaryLength(ChildContainer p_265345_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265345_.getHeight();
                case VERTICAL -> var10000 = p_265345_.getWidth();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        void setPrimaryPosition(ChildContainer p_265660_, int p_265194_) {
            switch (this) {
                case HORIZONTAL -> p_265660_.setX(p_265194_, p_265660_.getWidth());
                case VERTICAL -> p_265660_.setY(p_265194_, p_265660_.getHeight());
            }

        }

        void setSecondaryPosition(ChildContainer p_265536_, int p_265313_, int p_265295_) {
            switch (this) {
                case HORIZONTAL -> p_265536_.setY(p_265313_, p_265295_);
                case VERTICAL -> p_265536_.setX(p_265313_, p_265295_);
            }

        }

        int getPrimaryPosition(LayoutElement p_265209_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265209_.getX();
                case VERTICAL -> var10000 = p_265209_.getY();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        int getSecondaryPosition(LayoutElement p_265676_) {
            int var10000;
            switch (this) {
                case HORIZONTAL -> var10000 = p_265676_.getY();
                case VERTICAL -> var10000 = p_265676_.getX();
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement p_265706_, LayoutSettings p_265131_) {
            super(p_265706_, p_265131_);
        }
    }
}
