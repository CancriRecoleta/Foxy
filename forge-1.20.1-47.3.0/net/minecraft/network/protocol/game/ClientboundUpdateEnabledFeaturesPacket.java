//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientGamePacketListener> {
    public ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf p_250545_) {
        this((Set)p_250545_.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
    }

    public ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) {
        this.features = features;
    }

    public void write(FriendlyByteBuf p_251972_) {
        p_251972_.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle(ClientGamePacketListener p_250317_) {
        p_250317_.handleEnabledFeatures(this);
    }

    public Set<ResourceLocation> features() {
        return this.features;
    }
}
