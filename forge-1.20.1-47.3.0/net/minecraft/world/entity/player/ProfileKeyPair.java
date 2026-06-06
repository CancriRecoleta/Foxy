//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.PrivateKey;
import java.time.Instant;
import net.minecraft.util.Crypt;
import net.minecraft.util.ExtraCodecs;

public record ProfileKeyPair(PrivateKey privateKey, ProfilePublicKey publicKey, Instant refreshedAfter) {
    public static final Codec<ProfileKeyPair> CODEC = RecordCodecBuilder.create((p_219772_) -> {
        return p_219772_.group(Crypt.PRIVATE_KEY_CODEC.fieldOf("private_key").forGetter(ProfileKeyPair::privateKey), ProfilePublicKey.TRUSTED_CODEC.fieldOf("public_key").forGetter(ProfileKeyPair::publicKey), ExtraCodecs.INSTANT_ISO8601.fieldOf("refreshed_after").forGetter(ProfileKeyPair::refreshedAfter)).apply(p_219772_, ProfileKeyPair::new);
    });

    public ProfileKeyPair(PrivateKey privateKey, ProfilePublicKey publicKey, Instant refreshedAfter) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.refreshedAfter = refreshedAfter;
    }

    public boolean dueRefresh() {
        return this.refreshedAfter.isBefore(Instant.now());
    }

    public PrivateKey privateKey() {
        return this.privateKey;
    }

    public ProfilePublicKey publicKey() {
        return this.publicKey;
    }

    public Instant refreshedAfter() {
        return this.refreshedAfter;
    }
}
