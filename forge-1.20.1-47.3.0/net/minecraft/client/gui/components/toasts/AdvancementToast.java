//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components.toasts;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementToast implements Toast {
    public static final int DISPLAY_TIME = 5000;
    private final Advancement advancement;
    private boolean playedSound;

    public AdvancementToast(Advancement p_94798_) {
        this.advancement = p_94798_;
    }

    public Toast.Visibility render(GuiGraphics p_281813_, ToastComponent p_282243_, long p_282604_) {
        DisplayInfo $$3 = this.advancement.getDisplay();
        p_281813_.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        if ($$3 != null) {
            List<FormattedCharSequence> $$4 = p_282243_.getMinecraft().font.split($$3.getTitle(), 125);
            int $$5 = $$3.getFrame() == FrameType.CHALLENGE ? 16746751 : 16776960;
            if ($$4.size() == 1) {
                p_281813_.drawString(p_282243_.getMinecraft().font, (Component)$$3.getFrame().getDisplayName(), 30, 7, $$5 | -16777216, false);
                p_281813_.drawString(p_282243_.getMinecraft().font, (FormattedCharSequence)((FormattedCharSequence)$$4.get(0)), 30, 18, -1, false);
            } else {
                int $$6 = true;
                float $$7 = 300.0F;
                int $$9;
                if (p_282604_ < 1500L) {
                    $$9 = Mth.floor(Mth.clamp((float)(1500L - p_282604_) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    p_281813_.drawString(p_282243_.getMinecraft().font, (Component)$$3.getFrame().getDisplayName(), 30, 11, $$5 | $$9, false);
                } else {
                    $$9 = Mth.floor(Mth.clamp((float)(p_282604_ - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int var10000 = this.height() / 2;
                    int var10001 = $$4.size();
                    Objects.requireNonNull(p_282243_.getMinecraft().font);
                    int $$10 = var10000 - var10001 * 9 / 2;

                    for(Iterator var12 = $$4.iterator(); var12.hasNext(); $$10 += 9) {
                        FormattedCharSequence $$11 = (FormattedCharSequence)var12.next();
                        p_281813_.drawString(p_282243_.getMinecraft().font, (FormattedCharSequence)$$11, 30, $$10, 16777215 | $$9, false);
                        Objects.requireNonNull(p_282243_.getMinecraft().font);
                    }
                }
            }

            if (!this.playedSound && p_282604_ > 0L) {
                this.playedSound = true;
                if ($$3.getFrame() == FrameType.CHALLENGE) {
                    p_282243_.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
                }
            }

            p_281813_.renderFakeItem($$3.getIcon(), 8, 8);
            return (double)p_282604_ >= 5000.0 * p_282243_.getNotificationDisplayTimeMultiplier() ? net.minecraft.client.gui.components.toasts.Toast.Visibility.HIDE : net.minecraft.client.gui.components.toasts.Toast.Visibility.SHOW;
        } else {
            return net.minecraft.client.gui.components.toasts.Toast.Visibility.HIDE;
        }
    }
}
