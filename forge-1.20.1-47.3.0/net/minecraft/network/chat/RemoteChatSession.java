//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
    public RemoteChatSession(UUID sessionId, ProfilePublicKey profilePublicKey) {
        this.sessionId = sessionId;
        this.profilePublicKey = profilePublicKey;
    }

    public SignedMessageValidator createMessageValidator() {
        return new SignedMessageValidator.KeyBased(this.profilePublicKey.createSignatureValidator());
    }

    public SignedMessageChain.Decoder createMessageDecoder(UUID p_249107_) {
        return (new SignedMessageChain(p_249107_, this.sessionId)).decoder(this.profilePublicKey);
    }

    public Data asData() {
        return new Data(this.sessionId, this.profilePublicKey.data());
    }

    public boolean hasExpired() {
        return this.profilePublicKey.data().hasExpired();
    }

    public UUID sessionId() {
        return this.sessionId;
    }

    public ProfilePublicKey profilePublicKey() {
        return this.profilePublicKey;
    }

    public static record Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
        public Data(UUID sessionId, ProfilePublicKey.Data profilePublicKey) {
            this.sessionId = sessionId;
            this.profilePublicKey = profilePublicKey;
        }

        public static Data read(FriendlyByteBuf p_252181_) {
            return new Data(p_252181_.readUUID(), new ProfilePublicKey.Data(p_252181_));
        }

        public static void write(FriendlyByteBuf p_248910_, Data p_250537_) {
            p_248910_.writeUUID(p_250537_.sessionId);
            p_250537_.profilePublicKey.write(p_248910_);
        }

        public RemoteChatSession validate(GameProfile p_251231_, SignatureValidator p_248970_, Duration p_251179_) throws ProfilePublicKey.ValidationException {
            return new RemoteChatSession(this.sessionId, ProfilePublicKey.createValidated(p_248970_, p_251231_.getId(), this.profilePublicKey, p_251179_));
        }

        public UUID sessionId() {
            return this.sessionId;
        }

        public ProfilePublicKey.Data profilePublicKey() {
            return this.profilePublicKey;
        }
    }
}
