//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;

public record ArgumentSignatures(List<Entry> entries) {
    public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
    private static final int MAX_ARGUMENT_COUNT = 8;
    private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

    public ArgumentSignatures(FriendlyByteBuf p_231052_) {
        this((List)p_231052_.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 8), Entry::new));
    }

    public ArgumentSignatures(List<Entry> entries) {
        this.entries = entries;
    }

    @Nullable
    public MessageSignature get(String p_241493_) {
        Iterator var2 = this.entries.iterator();

        Entry $$1;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            $$1 = (Entry)var2.next();
        } while(!$$1.name.equals(p_241493_));

        return $$1.signature;
    }

    public void write(FriendlyByteBuf p_231062_) {
        p_231062_.writeCollection(this.entries, (p_241214_, p_241215_) -> {
            p_241215_.write(p_241214_);
        });
    }

    public static ArgumentSignatures signCommand(SignableCommand<?> p_251621_, Signer p_248653_) {
        List<Entry> $$2 = p_251621_.arguments().stream().map((p_247962_) -> {
            MessageSignature $$2 = p_248653_.sign(p_247962_.value());
            return $$2 != null ? new Entry(p_247962_.name(), $$2) : null;
        }).filter(Objects::nonNull).toList();
        return new ArgumentSignatures($$2);
    }

    public List<Entry> entries() {
        return this.entries;
    }

    public static record Entry(String name, MessageSignature signature) {
        public Entry(FriendlyByteBuf p_241305_) {
            this(p_241305_.readUtf(16), MessageSignature.read(p_241305_));
        }

        public Entry(String name, MessageSignature signature) {
            this.name = name;
            this.signature = signature;
        }

        public void write(FriendlyByteBuf p_241403_) {
            p_241403_.writeUtf(this.name, 16);
            MessageSignature.write(p_241403_, this.signature);
        }

        public String name() {
            return this.name;
        }

        public MessageSignature signature() {
            return this.signature;
        }
    }

    @FunctionalInterface
    public interface Signer {
        @Nullable
        MessageSignature sign(String var1);
    }
}
