//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private SignedMessageLink nextLink;

    public SignedMessageChain(UUID p_250050_, UUID p_249127_) {
        this.nextLink = SignedMessageLink.root(p_250050_, p_249127_);
    }

    public Encoder encoder(Signer p_248636_) {
        return (p_248067_) -> {
            SignedMessageLink $$2 = this.advanceLink();
            return $$2 == null ? null : new MessageSignature(p_248636_.sign((p_248065_) -> {
                PlayerChatMessage.updateSignature(p_248065_, $$2, p_248067_);
            }));
        };
    }

    public Decoder decoder(ProfilePublicKey p_249122_) {
        SignatureValidator $$1 = p_249122_.createSignatureValidator();
        return (p_248061_, p_248062_) -> {
            SignedMessageLink $$4 = this.advanceLink();
            if ($$4 == null) {
                throw new DecodeException(Component.translatable("chat.disabled.chain_broken"), false);
            } else if (p_249122_.data().hasExpired()) {
                throw new DecodeException(Component.translatable("chat.disabled.expiredProfileKey"), false);
            } else {
                PlayerChatMessage $$5 = new PlayerChatMessage($$4, p_248061_, p_248062_, (Component)null, FilterMask.PASS_THROUGH);
                if (!$$5.verify($$1)) {
                    throw new DecodeException(Component.translatable("multiplayer.disconnect.unsigned_chat"), true);
                } else {
                    if ($$5.hasExpiredServer(Instant.now())) {
                        LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", p_248062_.content());
                    }

                    return $$5;
                }
            }
        };
    }

    @Nullable
    private SignedMessageLink advanceLink() {
        SignedMessageLink $$0 = this.nextLink;
        if ($$0 != null) {
            this.nextLink = $$0.advance();
        }

        return $$0;
    }

    @FunctionalInterface
    public interface Encoder {
        Encoder UNSIGNED = (p_250548_) -> {
            return null;
        };

        @Nullable
        MessageSignature pack(SignedMessageBody var1);
    }

    @FunctionalInterface
    public interface Decoder {
        Decoder REJECT_ALL = (p_253466_, p_253467_) -> {
            throw new DecodeException(Component.translatable("chat.disabled.missingProfileKey"), false);
        };

        static Decoder unsigned(UUID p_251747_) {
            return (p_248069_, p_248070_) -> {
                return PlayerChatMessage.unsigned(p_251747_, p_248070_.content());
            };
        }

        PlayerChatMessage unpack(@Nullable MessageSignature var1, SignedMessageBody var2) throws DecodeException;
    }

    public static class DecodeException extends ThrowingComponent {
        private final boolean shouldDisconnect;

        public DecodeException(Component p_249149_, boolean p_250401_) {
            super(p_249149_);
            this.shouldDisconnect = p_250401_;
        }

        public boolean shouldDisconnect() {
            return this.shouldDisconnect;
        }
    }
}
