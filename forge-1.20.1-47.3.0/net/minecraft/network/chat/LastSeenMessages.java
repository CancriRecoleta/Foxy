//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureUpdater;

public record LastSeenMessages(List<MessageSignature> entries) {
    public static final Codec<LastSeenMessages> CODEC;
    public static LastSeenMessages EMPTY;
    public static final int LAST_SEEN_MESSAGES_MAX_LENGTH = 20;

    public LastSeenMessages(List<MessageSignature> entries) {
        this.entries = entries;
    }

    public void updateSignature(SignatureUpdater.Output p_251665_) throws SignatureException {
        p_251665_.update(Ints.toByteArray(this.entries.size()));
        Iterator var2 = this.entries.iterator();

        while(var2.hasNext()) {
            MessageSignature $$1 = (MessageSignature)var2.next();
            p_251665_.update($$1.bytes());
        }

    }

    public Packed pack(MessageSignatureCache p_253961_) {
        return new Packed(this.entries.stream().map((p_253457_) -> {
            return p_253457_.pack(p_253961_);
        }).toList());
    }

    public List<MessageSignature> entries() {
        return this.entries;
    }

    static {
        CODEC = MessageSignature.CODEC.listOf().xmap(LastSeenMessages::new, LastSeenMessages::entries);
        EMPTY = new LastSeenMessages(List.of());
    }

    public static record Packed(List<MessageSignature.Packed> entries) {
        public static final Packed EMPTY = new Packed(List.of());

        public Packed(FriendlyByteBuf p_249757_) {
            this((List)p_249757_.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 20), MessageSignature.Packed::read));
        }

        public Packed(List<MessageSignature.Packed> entries) {
            this.entries = entries;
        }

        public void write(FriendlyByteBuf p_250725_) {
            p_250725_.writeCollection(this.entries, MessageSignature.Packed::write);
        }

        public Optional<LastSeenMessages> unpack(MessageSignatureCache p_253745_) {
            List<MessageSignature> $$1 = new ArrayList(this.entries.size());
            Iterator var3 = this.entries.iterator();

            while(var3.hasNext()) {
                MessageSignature.Packed $$2 = (MessageSignature.Packed)var3.next();
                Optional<MessageSignature> $$3 = $$2.unpack(p_253745_);
                if ($$3.isEmpty()) {
                    return Optional.empty();
                }

                $$1.add((MessageSignature)$$3.get());
            }

            return Optional.of(new LastSeenMessages($$1));
        }

        public List<MessageSignature.Packed> entries() {
            return this.entries;
        }
    }

    public static record Update(int offset, BitSet acknowledged) {
        public Update(FriendlyByteBuf p_242184_) {
            this(p_242184_.readVarInt(), p_242184_.readFixedBitSet(20));
        }

        public Update(int offset, BitSet acknowledged) {
            this.offset = offset;
            this.acknowledged = acknowledged;
        }

        public void write(FriendlyByteBuf p_242221_) {
            p_242221_.writeVarInt(this.offset);
            p_242221_.writeFixedBitSet(this.acknowledged, 20);
        }

        public int offset() {
            return this.offset;
        }

        public BitSet acknowledged() {
            return this.acknowledged;
        }
    }
}
