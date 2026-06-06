//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record MessageSignature(byte[] bytes) {
    public static final Codec<MessageSignature> CODEC;
    public static final int BYTES = 256;

    public MessageSignature(byte[] bytes) {
        Preconditions.checkState(bytes.length == 256, "Invalid message signature size");
        this.bytes = bytes;
    }

    public static MessageSignature read(FriendlyByteBuf p_249837_) {
        byte[] $$1 = new byte[256];
        p_249837_.readBytes($$1);
        return new MessageSignature($$1);
    }

    public static void write(FriendlyByteBuf p_250642_, MessageSignature p_249714_) {
        p_250642_.writeBytes(p_249714_.bytes);
    }

    public boolean verify(SignatureValidator p_250998_, SignatureUpdater p_249843_) {
        return p_250998_.validate(p_249843_, this.bytes);
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(this.bytes);
    }

    public boolean equals(Object p_237166_) {
        boolean var10000;
        if (this != p_237166_) {
            label26: {
                if (p_237166_ instanceof MessageSignature) {
                    MessageSignature $$1 = (MessageSignature)p_237166_;
                    if (Arrays.equals(this.bytes, $$1.bytes)) {
                        break label26;
                    }
                }

                var10000 = false;
                return var10000;
            }
        }

        var10000 = true;
        return var10000;
    }

    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    public String toString() {
        return Base64.getEncoder().encodeToString(this.bytes);
    }

    public Packed pack(MessageSignatureCache p_253845_) {
        int $$1 = p_253845_.pack(this);
        return $$1 != -1 ? new Packed($$1) : new Packed(this);
    }

    public byte[] bytes() {
        return this.bytes;
    }

    static {
        CODEC = ExtraCodecs.BASE64_STRING.xmap(MessageSignature::new, MessageSignature::bytes);
    }

    public static record Packed(int id, @Nullable MessageSignature fullSignature) {
        public static final int FULL_SIGNATURE = -1;

        public Packed(MessageSignature p_249705_) {
            this(-1, p_249705_);
        }

        public Packed(int p_250015_) {
            this(p_250015_, (MessageSignature)null);
        }

        public Packed(int id, @Nullable MessageSignature fullSignature) {
            this.id = id;
            this.fullSignature = fullSignature;
        }

        public static Packed read(FriendlyByteBuf p_250810_) {
            int $$1 = p_250810_.readVarInt() - 1;
            return $$1 == -1 ? new Packed(MessageSignature.read(p_250810_)) : new Packed($$1);
        }

        public static void write(FriendlyByteBuf p_251691_, Packed p_252193_) {
            p_251691_.writeVarInt(p_252193_.id() + 1);
            if (p_252193_.fullSignature() != null) {
                MessageSignature.write(p_251691_, p_252193_.fullSignature());
            }

        }

        public Optional<MessageSignature> unpack(MessageSignatureCache p_254423_) {
            return this.fullSignature != null ? Optional.of(this.fullSignature) : Optional.ofNullable(p_254423_.unpack(this.id));
        }

        public int id() {
            return this.id;
        }

        @Nullable
        public MessageSignature fullSignature() {
            return this.fullSignature;
        }
    }
}
