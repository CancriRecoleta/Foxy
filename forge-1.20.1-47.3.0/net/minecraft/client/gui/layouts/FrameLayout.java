//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.layouts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrameLayout extends AbstractLayout {
    private final List<ChildContainer> children;
    private int minWidth;
    private int minHeight;
    private final LayoutSettings defaultChildLayoutSettings;

    public FrameLayout() {
        this(0, 0, 0, 0);
    }

    public FrameLayout(int p_270073_, int p_270705_) {
        this(0, 0, p_270073_, p_270705_);
    }

    public FrameLayout(int p_265719_, int p_265042_, int p_265587_, int p_265682_) {
        super(p_265719_, p_265042_, p_265587_, p_265682_);
        this.children = new ArrayList();
        this.defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);
        this.setMinDimensions(p_265587_, p_265682_);
    }

    public FrameLayout setMinDimensions(int p_265169_, int p_265616_) {
        return this.setMinWidth(p_265169_).setMinHeight(p_265616_);
    }

    public FrameLayout setMinHeight(int p_265646_) {
        this.minHeight = p_265646_;
        return this;
    }

    public FrameLayout setMinWidth(int p_265764_) {
        this.minWidth = p_265764_;
        return this;
    }

    public LayoutSettings newChildLayoutSettings() {
        return this.defaultChildLayoutSettings.copy();
    }

    public LayoutSettings defaultChildLayoutSetting() {
        return this.defaultChildLayoutSettings;
    }

    public void arrangeElements() {
        super.arrangeElements();
        int $$0 = this.minWidth;
        int $$1 = this.minHeight;

        Iterator var3;
        ChildContainer $$3;
        for(var3 = this.children.iterator(); var3.hasNext(); $$1 = Math.max($$1, $$3.getHeight())) {
            $$3 = (ChildContainer)var3.next();
            $$0 = Math.max($$0, $$3.getWidth());
        }

        var3 = this.children.iterator();

        while(var3.hasNext()) {
            $$3 = (ChildContainer)var3.next();
            $$3.setX(this.getX(), $$0);
            $$3.setY(this.getY(), $$1);
        }

        this.width = $$0;
        this.height = $$1;
    }

    public <T extends LayoutElement> T addChild(T p_265071_) {
        return this.addChild(p_265071_, this.newChildLayoutSettings());
    }

    public <T extends LayoutElement> T addChild(T p_265386_, LayoutSettings p_265532_) {
        this.children.add(new ChildContainer(p_265386_, p_265532_));
        return p_265386_;
    }

    public void visitChildren(Consumer<LayoutElement> p_265070_) {
        this.children.forEach((p_265653_) -> {
            p_265070_.accept(p_265653_.child);
        });
    }

    public static void centerInRectangle(LayoutElement p_265197_, int p_265518_, int p_265334_, int p_265540_, int p_265632_) {
        alignInRectangle(p_265197_, p_265518_, p_265334_, p_265540_, p_265632_, 0.5F, 0.5F);
    }

    public static void centerInRectangle(LayoutElement p_268229_, ScreenRectangle p_268113_) {
        centerInRectangle(p_268229_, p_268113_.position().x(), p_268113_.position().y(), p_268113_.width(), p_268113_.height());
    }

    public static void alignInRectangle(LayoutElement p_275320_, ScreenRectangle p_275389_, float p_275607_, float p_275662_) {
        alignInRectangle(p_275320_, p_275389_.left(), p_275389_.top(), p_275389_.width(), p_275389_.height(), p_275607_, p_275662_);
    }

    public static void alignInRectangle(LayoutElement p_265662_, int p_265497_, int p_265030_, int p_265535_, int p_265427_, float p_265271_, float p_265365_) {
        int var10002 = p_265662_.getWidth();
        Objects.requireNonNull(p_265662_);
        alignInDimension(p_265497_, p_265535_, var10002, p_265662_::setX, p_265271_);
        var10002 = p_265662_.getHeight();
        Objects.requireNonNull(p_265662_);
        alignInDimension(p_265030_, p_265427_, var10002, p_265662_::setY, p_265365_);
    }

    public static void alignInDimension(int p_265164_, int p_265100_, int p_265351_, Consumer<Integer> p_265614_, float p_265428_) {
        int $$5 = (int)Mth.lerp(p_265428_, 0.0F, (float)(p_265100_ - p_265351_));
        p_265614_.accept(p_265164_ + $$5);
    }

    @OnlyIn(Dist.CLIENT)
    static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
        protected ChildContainer(LayoutElement p_265667_, LayoutSettings p_265430_) {
            super(p_265667_, p_265430_);
        }
    }
}
