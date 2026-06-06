//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

@OnlyIn(Dist.CLIENT)
public class ToastComponent {
    private static final int SLOT_COUNT = 5;
    private static final int NO_SPACE = -1;
    final Minecraft minecraft;
    private final List<ToastInstance<?>> visible = new ArrayList();
    private final BitSet occupiedSlots = new BitSet(5);
    private final Deque<Toast> queued = Queues.newArrayDeque();

    public ToastComponent(Minecraft p_94918_) {
        this.minecraft = p_94918_;
    }

    public void render(GuiGraphics p_283249_) {
        if (!this.minecraft.options.hideGui) {
            int i = p_283249_.guiWidth();
            this.visible.removeIf((p_280780_) -> {
                if (p_280780_ != null && p_280780_.render(i, p_283249_)) {
                    this.occupiedSlots.clear(p_280780_.index, p_280780_.index + p_280780_.slotCount);
                    return true;
                } else {
                    return false;
                }
            });
            if (!this.queued.isEmpty() && this.freeSlots() > 0) {
                this.queued.removeIf((p_243239_) -> {
                    int j = p_243239_.slotCount();
                    int k = this.findFreeIndex(j);
                    if (k != -1) {
                        this.visible.add(new ToastInstance(p_243239_, k, j));
                        this.occupiedSlots.set(k, k + j);
                        return true;
                    } else {
                        return false;
                    }
                });
            }
        }

    }

    private int findFreeIndex(int p_243272_) {
        if (this.freeSlots() >= p_243272_) {
            int i = 0;

            for(int j = 0; j < 5; ++j) {
                if (this.occupiedSlots.get(j)) {
                    i = 0;
                } else {
                    ++i;
                    if (i == p_243272_) {
                        return j + 1 - i;
                    }
                }
            }
        }

        return -1;
    }

    private int freeSlots() {
        return 5 - this.occupiedSlots.cardinality();
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> p_94927_, Object p_94928_) {
        Iterator var3 = this.visible.iterator();

        ToastInstance toastinstance;
        do {
            if (!var3.hasNext()) {
                var3 = this.queued.iterator();

                Toast toast;
                do {
                    if (!var3.hasNext()) {
                        return (Toast)null;
                    }

                    toast = (Toast)var3.next();
                } while(!p_94927_.isAssignableFrom(toast.getClass()) || !toast.getToken().equals(p_94928_));

                return toast;
            }

            toastinstance = (ToastInstance)var3.next();
        } while(toastinstance == null || !p_94927_.isAssignableFrom(toastinstance.getToast().getClass()) || !toastinstance.getToast().getToken().equals(p_94928_));

        return toastinstance.getToast();
    }

    public void clear() {
        this.occupiedSlots.clear();
        this.visible.clear();
        this.queued.clear();
    }

    public void addToast(Toast p_94923_) {
        if (!ForgeHooksClient.onToastAdd(p_94923_)) {
            this.queued.add(p_94923_);
        }
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public double getNotificationDisplayTimeMultiplier() {
        return (Double)this.minecraft.options.notificationDisplayTime().get();
    }

    @OnlyIn(Dist.CLIENT)
    class ToastInstance<T extends Toast> {
        private static final long ANIMATION_TIME = 600L;
        private final T toast;
        final int index;
        final int slotCount;
        private long animationTime = -1L;
        private long visibleTime = -1L;
        private Toast.Visibility visibility;

        ToastInstance(T p_243319_, int p_243300_, int p_243224_) {
            this.visibility = Visibility.SHOW;
            this.toast = p_243319_;
            this.index = p_243300_;
            this.slotCount = p_243224_;
        }

        public T getToast() {
            return this.toast;
        }

        private float getVisibility(long p_94948_) {
            float f = Mth.clamp((float)(p_94948_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
            f *= f;
            return this.visibility == Visibility.HIDE ? 1.0F - f : f;
        }

        public boolean render(int p_282887_, GuiGraphics p_283668_) {
            long i = Util.getMillis();
            if (this.animationTime == -1L) {
                this.animationTime = i;
                this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
            }

            if (this.visibility == Visibility.SHOW && i - this.animationTime <= 600L) {
                this.visibleTime = i;
            }

            p_283668_.pose().pushPose();
            p_283668_.pose().translate((float)p_282887_ - (float)this.toast.width() * this.getVisibility(i), (float)(this.index * 32), 800.0F);
            Toast.Visibility toast$visibility = this.toast.render(p_283668_, ToastComponent.this, i - this.visibleTime);
            p_283668_.pose().popPose();
            if (toast$visibility != this.visibility) {
                this.animationTime = i - (long)((int)((1.0F - this.getVisibility(i)) * 600.0F));
                this.visibility = toast$visibility;
                this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
            }

            return this.visibility == Visibility.HIDE && i - this.animationTime > 600L;
        }
    }
}
