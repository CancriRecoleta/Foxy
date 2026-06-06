//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ObjectSelectionList<E extends Entry<E>> extends AbstractSelectionList<E> {
    private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

    public ObjectSelectionList(Minecraft p_94442_, int p_94443_, int p_94444_, int p_94445_, int p_94446_, int p_94447_) {
        super(p_94442_, p_94443_, p_94444_, p_94445_, p_94446_, p_94447_);
    }

    @Nullable
    public ComponentPath nextFocusPath(FocusNavigationEvent p_265150_) {
        if (this.getItemCount() == 0) {
            return null;
        } else if (this.isFocused() && p_265150_ instanceof FocusNavigationEvent.ArrowNavigation) {
            FocusNavigationEvent.ArrowNavigation $$1 = (FocusNavigationEvent.ArrowNavigation)p_265150_;
            E $$2 = (Entry)this.nextEntry($$1.direction());
            return $$2 != null ? ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf($$2)) : null;
        } else if (!this.isFocused()) {
            E $$3 = (Entry)this.getSelected();
            if ($$3 == null) {
                $$3 = (Entry)this.nextEntry(p_265150_.getVerticalDirectionForInitialFocus());
            }

            return $$3 == null ? null : ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf($$3));
        } else {
            return null;
        }
    }

    public void updateNarration(NarrationElementOutput p_169042_) {
        E $$1 = (Entry)this.getHovered();
        if ($$1 != null) {
            this.narrateListElementPosition(p_169042_.nest(), $$1);
            $$1.updateNarration(p_169042_);
        } else {
            E $$2 = (Entry)this.getSelected();
            if ($$2 != null) {
                this.narrateListElementPosition(p_169042_.nest(), $$2);
                $$2.updateNarration(p_169042_);
            }
        }

        if (this.isFocused()) {
            p_169042_.add(NarratedElementType.USAGE, USAGE_NARRATION);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry<E extends Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
        public Entry() {
        }

        public abstract Component getNarration();

        public void updateNarration(NarrationElementOutput p_169044_) {
            p_169044_.add(NarratedElementType.TITLE, this.getNarration());
        }
    }
}
