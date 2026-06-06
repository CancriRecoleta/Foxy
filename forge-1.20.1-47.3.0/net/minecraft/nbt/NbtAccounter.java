//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

public class NbtAccounter {
    public static final NbtAccounter UNLIMITED = new NbtAccounter(0L) {
        public void accountBytes(long p_128927_) {
        }
    };
    private final long quota;
    private long usage;

    public NbtAccounter(long p_128922_) {
        this.quota = p_128922_;
    }

    public void accountBytes(long p_263515_) {
        this.usage += p_263515_;
        if (this.usage > this.quota) {
            throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
        }
    }

    public String readUTF(String data) {
        this.accountBytes(2L);
        if (data == null) {
            return data;
        } else {
            int len = data.length();
            int utflen = 0;

            for(int i = 0; i < len; ++i) {
                int c = data.charAt(i);
                if (c >= 1 && c <= 127) {
                    ++utflen;
                } else if (c > 2047) {
                    utflen += 3;
                } else {
                    utflen += 2;
                }
            }

            this.accountBytes((long)utflen);
            return data;
        }
    }

    @VisibleForTesting
    public long getUsage() {
        return this.usage;
    }
}
