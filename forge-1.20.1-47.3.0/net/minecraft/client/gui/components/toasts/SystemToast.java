//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemToast implements Toast {
    private static final int MAX_LINE_SIZE = 200;
    private static final int LINE_SPACING = 12;
    private static final int MARGIN = 10;
    private final SystemToastIds id;
    private Component title;
    private List<FormattedCharSequence> messageLines;
    private long lastChanged;
    private boolean changed;
    private final int width;

    public SystemToast(SystemToastIds p_94832_, Component p_94833_, @Nullable Component p_94834_) {
        this(p_94832_, p_94833_, nullToEmpty(p_94834_), Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width((FormattedText)p_94833_), p_94834_ == null ? 0 : Minecraft.getInstance().font.width((FormattedText)p_94834_))));
    }

    public static SystemToast multiline(Minecraft p_94848_, SystemToastIds p_94849_, Component p_94850_, Component p_94851_) {
        Font $$4 = p_94848_.font;
        List<FormattedCharSequence> $$5 = $$4.split(p_94851_, 200);
        Stream var10001 = $$5.stream();
        Objects.requireNonNull($$4);
        int $$6 = Math.max(200, var10001.mapToInt($$4::width).max().orElse(200));
        return new SystemToast(p_94849_, p_94850_, $$5, $$6 + 30);
    }

    private SystemToast(SystemToastIds p_94827_, Component p_94828_, List<FormattedCharSequence> p_94829_, int p_94830_) {
        this.id = p_94827_;
        this.title = p_94828_;
        this.messageLines = p_94829_;
        this.width = p_94830_;
    }

    private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component p_94861_) {
        return p_94861_ == null ? ImmutableList.of() : ImmutableList.of(p_94861_.getVisualOrderText());
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return 20 + Math.max(this.messageLines.size(), 1) * 12;
    }

    public Toast.Visibility render(GuiGraphics p_281624_, ToastComponent p_282333_, long p_282762_) {
        if (this.changed) {
            this.lastChanged = p_282762_;
            this.changed = false;
        }

        int $$3 = this.width();
        int $$8;
        if ($$3 == 160 && this.messageLines.size() <= 1) {
            p_281624_.blit(TEXTURE, 0, 0, 0, 64, $$3, this.height());
        } else {
            $$8 = this.height();
            int $$5 = true;
            int $$6 = Math.min(4, $$8 - 28);
            this.renderBackgroundRow(p_281624_, p_282333_, $$3, 0, 0, 28);

            for(int $$7 = 28; $$7 < $$8 - $$6; $$7 += 10) {
                this.renderBackgroundRow(p_281624_, p_282333_, $$3, 16, $$7, Math.min(16, $$8 - $$7 - $$6));
            }

            this.renderBackgroundRow(p_281624_, p_282333_, $$3, 32 - $$6, $$8 - $$6, $$6);
        }

        if (this.messageLines == null) {
            p_281624_.drawString(p_282333_.getMinecraft().font, (Component)this.title, 18, 12, -256, false);
        } else {
            p_281624_.drawString(p_282333_.getMinecraft().font, (Component)this.title, 18, 7, -256, false);

            for($$8 = 0; $$8 < this.messageLines.size(); ++$$8) {
                p_281624_.drawString(p_282333_.getMinecraft().font, (FormattedCharSequence)((FormattedCharSequence)this.messageLines.get($$8)), 18, 18 + $$8 * 12, -1, false);
            }
        }

        return (double)(p_282762_ - this.lastChanged) < (double)this.id.displayTime * p_282333_.getNotificationDisplayTimeMultiplier() ? net.minecraft.client.gui.components.toasts.Toast.Visibility.SHOW : net.minecraft.client.gui.components.toasts.Toast.Visibility.HIDE;
    }

    private void renderBackgroundRow(GuiGraphics p_281840_, ToastComponent p_281283_, int p_281750_, int p_282371_, int p_283613_, int p_282880_) {
        int $$6 = p_282371_ == 0 ? 20 : 5;
        int $$7 = Math.min(60, p_281750_ - $$6);
        p_281840_.blit(TEXTURE, 0, p_283613_, 0, 64 + p_282371_, $$6, p_282880_);

        for(int $$8 = $$6; $$8 < p_281750_ - $$7; $$8 += 64) {
            p_281840_.blit(TEXTURE, $$8, p_283613_, 32, 64 + p_282371_, Math.min(64, p_281750_ - $$8 - $$7), p_282880_);
        }

        p_281840_.blit(TEXTURE, p_281750_ - $$7, p_283613_, 160 - $$7, 64 + p_282371_, $$7, p_282880_);
    }

    public void reset(Component p_94863_, @Nullable Component p_94864_) {
        this.title = p_94863_;
        this.messageLines = nullToEmpty(p_94864_);
        this.changed = true;
    }

    public SystemToastIds getToken() {
        return this.id;
    }

    public static void add(ToastComponent p_94856_, SystemToastIds p_94857_, Component p_94858_, @Nullable Component p_94859_) {
        p_94856_.addToast(new SystemToast(p_94857_, p_94858_, p_94859_));
    }

    public static void addOrUpdate(ToastComponent p_94870_, SystemToastIds p_94871_, Component p_94872_, @Nullable Component p_94873_) {
        SystemToast $$4 = (SystemToast)p_94870_.getToast(SystemToast.class, p_94871_);
        if ($$4 == null) {
            add(p_94870_, p_94871_, p_94872_, p_94873_);
        } else {
            $$4.reset(p_94872_, p_94873_);
        }

    }

    public static void onWorldAccessFailure(Minecraft p_94853_, String p_94854_) {
        add(p_94853_.getToasts(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal(p_94854_));
    }

    public static void onWorldDeleteFailure(Minecraft p_94867_, String p_94868_) {
        add(p_94867_.getToasts(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal(p_94868_));
    }

    public static void onPackCopyFailure(Minecraft p_94876_, String p_94877_) {
        add(p_94876_.getToasts(), net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal(p_94877_));
    }

    @OnlyIn(Dist.CLIENT)
    public static enum SystemToastIds {
        TUTORIAL_HINT,
        NARRATOR_TOGGLE,
        WORLD_BACKUP,
        PACK_LOAD_FAILURE,
        WORLD_ACCESS_FAILURE,
        PACK_COPY_FAILURE,
        PERIODIC_NOTIFICATION,
        UNSECURE_SERVER_WARNING(10000L);

        final long displayTime;

        private SystemToastIds(long p_232551_) {
            this.displayTime = p_232551_;
        }

        private SystemToastIds() {
            this(5000L);
        }
    }
}
