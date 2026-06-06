//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraft.util.Crypt;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureValidator;

public record ProfilePublicKey(Data data) {
    public static final Component EXPIRED_PROFILE_PUBLIC_KEY = Component.translatable("multiplayer.disconnect.expired_public_key");
    private static final Component INVALID_SIGNATURE = Component.translatable("multiplayer.disconnect.invalid_public_key_signature.new");
    public static final Duration EXPIRY_GRACE_PERIOD = Duration.ofHours(8L);
    public static final Codec<ProfilePublicKey> TRUSTED_CODEC;

    public ProfilePublicKey(Data data) {
        this.data = data;
    }

    public static ProfilePublicKey createValidated(SignatureValidator p_243373_, UUID p_243390_, Data p_243374_, Duration p_243387_) throws ValidationException {
        if (p_243374_.hasExpired(p_243387_)) {
            throw new ValidationException(EXPIRED_PROFILE_PUBLIC_KEY);
        } else if (!p_243374_.validateSignature(p_243373_, p_243390_)) {
            throw new ValidationException(INVALID_SIGNATURE);
        } else {
            return new ProfilePublicKey(p_243374_);
        }
    }

    public SignatureValidator createSignatureValidator() {
        return SignatureValidator.from(this.data.key, "SHA256withRSA");
    }

    public Data data() {
        return this.data;
    }

    static {
        TRUSTED_CODEC = net.minecraft.world.entity.player.ProfilePublicKey.Data.CODEC.xmap(ProfilePublicKey::new, ProfilePublicKey::data);
    }

    public static record Data(Instant expiresAt, PublicKey key, byte[] keySignature) {
        private static final int MAX_KEY_SIGNATURE_SIZE = 4096;
        public static final Codec<Data> CODEC = RecordCodecBuilder.create((p_219814_) -> {
            return p_219814_.group(ExtraCodecs.INSTANT_ISO8601.fieldOf("expires_at").forGetter(Data::expiresAt), Crypt.PUBLIC_KEY_CODEC.fieldOf("key").forGetter(Data::key), ExtraCodecs.BASE64_STRING.fieldOf("signature_v2").forGetter(Data::keySignature)).apply(p_219814_, Data::new);
        });

        public Data(FriendlyByteBuf p_219809_) {
            this(p_219809_.readInstant(), p_219809_.readPublicKey(), p_219809_.readByteArray(4096));
        }

        public Data(Instant expiresAt, PublicKey key, byte[] keySignature) {
            this.expiresAt = expiresAt;
            this.key = key;
            this.keySignature = keySignature;
        }

        public void write(FriendlyByteBuf p_219816_) {
            p_219816_.writeInstant(this.expiresAt);
            p_219816_.writePublicKey(this.key);
            p_219816_.writeByteArray(this.keySignature);
        }

        boolean validateSignature(SignatureValidator p_240296_, UUID p_240297_) {
            return p_240296_.validate(this.signedPayload(p_240297_), this.keySignature);
        }

        private byte[] signedPayload(UUID p_240267_) {
            byte[] $$1 = this.key.getEncoded();
            byte[] $$2 = new byte[24 + $$1.length];
            ByteBuffer $$3 = ByteBuffer.wrap($$2).order(ByteOrder.BIG_ENDIAN);
            $$3.putLong(p_240267_.getMostSignificantBits()).putLong(p_240267_.getLeastSignificantBits()).putLong(this.expiresAt.toEpochMilli()).put($$1);
            return $$2;
        }

        public boolean hasExpired() {
            return this.expiresAt.isBefore(Instant.now());
        }

        public boolean hasExpired(Duration p_243376_) {
            return this.expiresAt.plus(p_243376_).isBefore(Instant.now());
        }

        public boolean equals(Object p_219822_) {
            if (!(p_219822_ instanceof Data $$1)) {
                return false;
            } else {
                return this.expiresAt.equals($$1.expiresAt) && this.key.equals($$1.key) && Arrays.equals(this.keySignature, $$1.keySignature);
            }
        }

        public Instant expiresAt() {
            return this.expiresAt;
        }

        public PublicKey key() {
            return this.key;
        }

        public byte[] keySignature() {
            return this.keySignature;
        }
    }

    public static class ValidationException extends ThrowingComponent {
        public ValidationException(Component p_243378_) {
            super(p_243378_);
        }
    }
}
