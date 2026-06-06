//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public record ServerboundClientInformationPacket(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) implements Packet<ServerGamePacketListener> {
    public static final int MAX_LANGUAGE_LENGTH = 16;

    public ServerboundClientInformationPacket(FriendlyByteBuf p_179560_) {
        this(p_179560_.readUtf(16), p_179560_.readByte(), (ChatVisiblity)p_179560_.readEnum(ChatVisiblity.class), p_179560_.readBoolean(), p_179560_.readUnsignedByte(), (HumanoidArm)p_179560_.readEnum(HumanoidArm.class), p_179560_.readBoolean(), p_179560_.readBoolean());
    }

    public ServerboundClientInformationPacket(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand, boolean textFilteringEnabled, boolean allowsListing) {
        this.language = language;
        this.viewDistance = viewDistance;
        this.chatVisibility = chatVisibility;
        this.chatColors = chatColors;
        this.modelCustomisation = modelCustomisation;
        this.mainHand = mainHand;
        this.textFilteringEnabled = textFilteringEnabled;
        this.allowsListing = allowsListing;
    }

    public void write(FriendlyByteBuf p_133884_) {
        p_133884_.writeUtf(this.language);
        p_133884_.writeByte(this.viewDistance);
        p_133884_.writeEnum(this.chatVisibility);
        p_133884_.writeBoolean(this.chatColors);
        p_133884_.writeByte(this.modelCustomisation);
        p_133884_.writeEnum(this.mainHand);
        p_133884_.writeBoolean(this.textFilteringEnabled);
        p_133884_.writeBoolean(this.allowsListing);
    }

    public void handle(ServerGamePacketListener p_133882_) {
        p_133882_.handleClientInformation(this);
    }

    public String language() {
        return this.language;
    }

    public int viewDistance() {
        return this.viewDistance;
    }

    public ChatVisiblity chatVisibility() {
        return this.chatVisibility;
    }

    public boolean chatColors() {
        return this.chatColors;
    }

    public int modelCustomisation() {
        return this.modelCustomisation;
    }

    public HumanoidArm mainHand() {
        return this.mainHand;
    }

    public boolean textFilteringEnabled() {
        return this.textFilteringEnabled;
    }

    public boolean allowsListing() {
        return this.allowsListing;
    }
}
