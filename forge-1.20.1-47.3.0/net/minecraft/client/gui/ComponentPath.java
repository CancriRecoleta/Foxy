//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ComponentPath {
    static ComponentPath leaf(GuiEventListener p_265344_) {
        return new Leaf(p_265344_);
    }

    @Nullable
    static ComponentPath path(ContainerEventHandler p_265254_, @Nullable ComponentPath p_265405_) {
        return p_265405_ == null ? null : new Path(p_265254_, p_265405_);
    }

    static ComponentPath path(GuiEventListener p_265555_, ContainerEventHandler... p_265487_) {
        ComponentPath $$2 = leaf(p_265555_);
        ContainerEventHandler[] var3 = p_265487_;
        int var4 = p_265487_.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ContainerEventHandler $$3 = var3[var5];
            $$2 = path($$3, $$2);
        }

        return $$2;
    }

    GuiEventListener component();

    void applyFocus(boolean var1);

    @OnlyIn(Dist.CLIENT)
    public static record Leaf(GuiEventListener component) implements ComponentPath {
        public Leaf(GuiEventListener component) {
            this.component = component;
        }

        public void applyFocus(boolean p_265248_) {
            this.component.setFocused(p_265248_);
        }

        public GuiEventListener component() {
            return this.component;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record Path(ContainerEventHandler component, ComponentPath childPath) implements ComponentPath {
        public Path(ContainerEventHandler component, ComponentPath childPath) {
            this.component = component;
            this.childPath = childPath;
        }

        public void applyFocus(boolean p_265230_) {
            if (!p_265230_) {
                this.component.setFocused((GuiEventListener)null);
            } else {
                this.component.setFocused(this.childPath.component());
            }

            this.childPath.applyFocus(p_265230_);
        }

        public ContainerEventHandler component() {
            return this.component;
        }

        public ComponentPath childPath() {
            return this.childPath;
        }
    }
}
