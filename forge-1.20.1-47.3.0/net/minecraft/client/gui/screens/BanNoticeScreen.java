//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.report.BanReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class BanNoticeScreen {
    private static final Component TEMPORARY_BAN_TITLE;
    private static final Component PERMANENT_BAN_TITLE;

    public BanNoticeScreen() {
    }

    public static ConfirmLinkScreen create(BooleanConsumer p_239968_, BanDetails p_239969_) {
        return new ConfirmLinkScreen(p_239968_, getBannedTitle(p_239969_), getBannedScreenText(p_239969_), "https://aka.ms/mcjavamoderation", CommonComponents.GUI_ACKNOWLEDGE, true);
    }

    private static Component getBannedTitle(BanDetails p_239953_) {
        return isTemporaryBan(p_239953_) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
    }

    private static Component getBannedScreenText(BanDetails p_239138_) {
        return Component.translatable("gui.banned.description", getBanReasonText(p_239138_), getBanStatusText(p_239138_), Component.literal("https://aka.ms/mcjavamoderation"));
    }

    private static Component getBanReasonText(BanDetails p_239534_) {
        String $$1 = p_239534_.reason();
        String $$2 = p_239534_.reasonMessage();
        if (StringUtils.isNumeric($$1)) {
            int $$3 = Integer.parseInt($$1);
            BanReason $$4 = BanReason.byId($$3);
            MutableComponent $$7;
            if ($$4 != null) {
                $$7 = ComponentUtils.mergeStyles($$4.title().copy(), Style.EMPTY.withBold(true));
            } else if ($$2 != null) {
                $$7 = Component.translatable("gui.banned.description.reason_id_message", $$3, $$2).withStyle(ChatFormatting.BOLD);
            } else {
                $$7 = Component.translatable("gui.banned.description.reason_id", $$3).withStyle(ChatFormatting.BOLD);
            }

            return Component.translatable("gui.banned.description.reason", $$7);
        } else {
            return Component.translatable("gui.banned.description.unknownreason");
        }
    }

    private static Component getBanStatusText(BanDetails p_239319_) {
        if (isTemporaryBan(p_239319_)) {
            Component $$1 = getBanDurationText(p_239319_);
            return Component.translatable("gui.banned.description.temporary", Component.translatable("gui.banned.description.temporary.duration", $$1).withStyle(ChatFormatting.BOLD));
        } else {
            return Component.translatable("gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
        }
    }

    private static Component getBanDurationText(BanDetails p_239880_) {
        Duration $$1 = Duration.between(Instant.now(), p_239880_.expires());
        long $$2 = $$1.toHours();
        if ($$2 > 72L) {
            return CommonComponents.days($$1.toDays());
        } else {
            return $$2 < 1L ? CommonComponents.minutes($$1.toMinutes()) : CommonComponents.hours($$1.toHours());
        }
    }

    private static boolean isTemporaryBan(BanDetails p_239501_) {
        return p_239501_.expires() != null;
    }

    static {
        TEMPORARY_BAN_TITLE = Component.translatable("gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
        PERMANENT_BAN_TITLE = Component.translatable("gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);
    }
}
