//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LoggedChatMessage extends LoggedChatEvent {
    static Player player(GameProfile p_261832_, PlayerChatMessage p_261491_, ChatTrustLevel p_262141_) {
        return new Player(p_261832_, p_261491_, p_262141_);
    }

    static System system(Component p_242325_, Instant p_242334_) {
        return new System(p_242325_, p_242334_);
    }

    Component toContentComponent();

    default Component toNarrationComponent() {
        return this.toContentComponent();
    }

    boolean canReport(UUID var1);

    @OnlyIn(Dist.CLIENT)
    public static record Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) implements LoggedChatMessage {
        public static final Codec<Player> CODEC = RecordCodecBuilder.create((p_261382_) -> {
            return p_261382_.group(ExtraCodecs.GAME_PROFILE.fieldOf("profile").forGetter(Player::profile), PlayerChatMessage.MAP_CODEC.forGetter(Player::message), ChatTrustLevel.CODEC.optionalFieldOf("trust_level", ChatTrustLevel.SECURE).forGetter(Player::trustLevel)).apply(p_261382_, Player::new);
        });
        private static final DateTimeFormatter TIME_FORMATTER;

        public Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) {
            this.profile = profile;
            this.message = message;
            this.trustLevel = trustLevel;
        }

        public Component toContentComponent() {
            if (!this.message.filterMask().isEmpty()) {
                Component $$0 = this.message.filterMask().applyWithFormatting(this.message.signedContent());
                return (Component)($$0 != null ? $$0 : Component.empty());
            } else {
                return this.message.decoratedContent();
            }
        }

        public Component toNarrationComponent() {
            Component $$0 = this.toContentComponent();
            Component $$1 = this.getTimeComponent();
            return Component.translatable("gui.chatSelection.message.narrate", this.profile.getName(), $$0, $$1);
        }

        public Component toHeadingComponent() {
            Component $$0 = this.getTimeComponent();
            return Component.translatable("gui.chatSelection.heading", this.profile.getName(), $$0);
        }

        private Component getTimeComponent() {
            LocalDateTime $$0 = LocalDateTime.ofInstant(this.message.timeStamp(), ZoneOffset.systemDefault());
            return Component.literal($$0.format(TIME_FORMATTER)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
        }

        public boolean canReport(UUID p_242210_) {
            return this.message.hasSignatureFrom(p_242210_);
        }

        public UUID profileId() {
            return this.profile.getId();
        }

        public LoggedChatEvent.Type type() {
            return net.minecraft.client.multiplayer.chat.LoggedChatEvent.Type.PLAYER;
        }

        public GameProfile profile() {
            return this.profile;
        }

        public PlayerChatMessage message() {
            return this.message;
        }

        public ChatTrustLevel trustLevel() {
            return this.trustLevel;
        }

        static {
            TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record System(Component message, Instant timeStamp) implements LoggedChatMessage {
        public static final Codec<System> CODEC = RecordCodecBuilder.create((p_253996_) -> {
            return p_253996_.group(ExtraCodecs.COMPONENT.fieldOf("message").forGetter(System::message), ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(System::timeStamp)).apply(p_253996_, System::new);
        });

        public System(Component message, Instant timeStamp) {
            this.message = message;
            this.timeStamp = timeStamp;
        }

        public Component toContentComponent() {
            return this.message;
        }

        public boolean canReport(UUID p_242173_) {
            return false;
        }

        public LoggedChatEvent.Type type() {
            return net.minecraft.client.multiplayer.chat.LoggedChatEvent.Type.SYSTEM;
        }

        public Component message() {
            return this.message;
        }

        public Instant timeStamp() {
            return this.timeStamp;
        }
    }
}
