//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.SignatureUpdater;

public record SignedMessageBody(String content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {
    public static final MapCodec<SignedMessageBody> MAP_CODEC = RecordCodecBuilder.mapCodec((p_253722_) -> {
        return p_253722_.group(Codec.STRING.fieldOf("content").forGetter(SignedMessageBody::content), ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(SignedMessageBody::timeStamp), Codec.LONG.fieldOf("salt").forGetter(SignedMessageBody::salt), LastSeenMessages.CODEC.optionalFieldOf("last_seen", LastSeenMessages.EMPTY).forGetter(SignedMessageBody::lastSeen)).apply(p_253722_, SignedMessageBody::new);
    });

    public SignedMessageBody(String content, Instant timeStamp, long salt, LastSeenMessages lastSeen) {
        this.content = content;
        this.timeStamp = timeStamp;
        this.salt = salt;
        this.lastSeen = lastSeen;
    }

    public static SignedMessageBody unsigned(String p_249884_) {
        return new SignedMessageBody(p_249884_, Instant.now(), 0L, LastSeenMessages.EMPTY);
    }

    public void updateSignature(SignatureUpdater.Output p_249654_) throws SignatureException {
        p_249654_.update(Longs.toByteArray(this.salt));
        p_249654_.update(Longs.toByteArray(this.timeStamp.getEpochSecond()));
        byte[] $$1 = this.content.getBytes(StandardCharsets.UTF_8);
        p_249654_.update(Ints.toByteArray($$1.length));
        p_249654_.update($$1);
        this.lastSeen.updateSignature(p_249654_);
    }

    public Packed pack(MessageSignatureCache p_253671_) {
        return new Packed(this.content, this.timeStamp, this.salt, this.lastSeen.pack(p_253671_));
    }

    public String content() {
        return this.content;
    }

    public Instant timeStamp() {
        return this.timeStamp;
    }

    public long salt() {
        return this.salt;
    }

    public LastSeenMessages lastSeen() {
        return this.lastSeen;
    }

    public static record Packed(String content, Instant timeStamp, long salt, LastSeenMessages.Packed lastSeen) {
        public Packed(FriendlyByteBuf p_251620_) {
            this(p_251620_.readUtf(256), p_251620_.readInstant(), p_251620_.readLong(), new LastSeenMessages.Packed(p_251620_));
        }

        public Packed(String content, Instant timeStamp, long salt, LastSeenMessages.Packed lastSeen) {
            this.content = content;
            this.timeStamp = timeStamp;
            this.salt = salt;
            this.lastSeen = lastSeen;
        }

        public void write(FriendlyByteBuf p_250247_) {
            p_250247_.writeUtf(this.content, 256);
            p_250247_.writeInstant(this.timeStamp);
            p_250247_.writeLong(this.salt);
            this.lastSeen.write(p_250247_);
        }

        public Optional<SignedMessageBody> unpack(MessageSignatureCache p_253919_) {
            return this.lastSeen.unpack(p_253919_).map((p_249065_) -> {
                return new SignedMessageBody(this.content, this.timeStamp, this.salt, p_249065_);
            });
        }

        public String content() {
            return this.content;
        }

        public Instant timeStamp() {
            return this.timeStamp;
        }

        public long salt() {
            return this.salt;
        }

        public LastSeenMessages.Packed lastSeen() {
            return this.lastSeen;
        }
    }
}
