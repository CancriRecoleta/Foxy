//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryManager;

class NetworkInitialization {
    NetworkInitialization() {
    }

    public static SimpleChannel getHandshakeChannel() {
        SimpleChannel handshakeChannel = ChannelBuilder.named(NetworkConstants.FML_HANDSHAKE_RESOURCE).clientAcceptedVersions((a) -> {
            return true;
        }).serverAcceptedVersions((a) -> {
            return true;
        }).networkProtocolVersion(() -> {
            return "FML3";
        }).simpleChannel();
        handshakeChannel.messageBuilder(HandshakeMessages.C2SAcknowledge.class, 99, NetworkDirection.LOGIN_TO_SERVER).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.C2SAcknowledge::decode).encoder(HandshakeMessages.C2SAcknowledge::encode).consumerNetworkThread(HandshakeHandler.indexFirst(HandshakeHandler::handleClientAck)).add();
        handshakeChannel.messageBuilder(HandshakeMessages.S2CModData.class, 5, NetworkDirection.LOGIN_TO_CLIENT).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.S2CModData::decode).encoder(HandshakeMessages.S2CModData::encode).markAsLoginPacket().noResponse().consumerNetworkThread(HandshakeHandler.biConsumerFor(HandshakeHandler::handleModData)).add();
        handshakeChannel.messageBuilder(HandshakeMessages.S2CModList.class, 1, NetworkDirection.LOGIN_TO_CLIENT).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.S2CModList::decode).encoder(HandshakeMessages.S2CModList::encode).markAsLoginPacket().consumerNetworkThread(HandshakeHandler.biConsumerFor(HandshakeHandler::handleServerModListOnClient)).add();
        handshakeChannel.messageBuilder(HandshakeMessages.C2SModListReply.class, 2, NetworkDirection.LOGIN_TO_SERVER).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.C2SModListReply::decode).encoder(HandshakeMessages.C2SModListReply::encode).consumerNetworkThread(HandshakeHandler.indexFirst(HandshakeHandler::handleClientModListOnServer)).add();
        handshakeChannel.messageBuilder(HandshakeMessages.S2CRegistry.class, 3, NetworkDirection.LOGIN_TO_CLIENT).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.S2CRegistry::decode).encoder(HandshakeMessages.S2CRegistry::encode).buildLoginPacketList(RegistryManager::generateRegistryPackets).consumerNetworkThread(HandshakeHandler.biConsumerFor(HandshakeHandler::handleRegistryMessage)).add();
        SimpleChannel.MessageBuilder var10000 = handshakeChannel.messageBuilder(HandshakeMessages.S2CConfigData.class, 4, NetworkDirection.LOGIN_TO_CLIENT).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.S2CConfigData::decode).encoder(HandshakeMessages.S2CConfigData::encode);
        ConfigSync var10001 = ConfigSync.INSTANCE;
        Objects.requireNonNull(var10001);
        var10000.buildLoginPacketList(var10001::syncConfigs).consumerNetworkThread(HandshakeHandler.biConsumerFor(HandshakeHandler::handleConfigSync)).add();
        handshakeChannel.messageBuilder(HandshakeMessages.S2CChannelMismatchData.class, 6, NetworkDirection.LOGIN_TO_CLIENT).loginIndex(HandshakeMessages.LoginIndexedMessage::getLoginIndex, HandshakeMessages.LoginIndexedMessage::setLoginIndex).decoder(HandshakeMessages.S2CChannelMismatchData::decode).encoder(HandshakeMessages.S2CChannelMismatchData::encode).consumerNetworkThread(HandshakeHandler.biConsumerFor(HandshakeHandler::handleModMismatchData)).add();
        return handshakeChannel;
    }

    public static SimpleChannel getPlayChannel() {
        SimpleChannel playChannel = ChannelBuilder.named(NetworkConstants.FML_PLAY_RESOURCE).clientAcceptedVersions((a) -> {
            return true;
        }).serverAcceptedVersions((a) -> {
            return true;
        }).networkProtocolVersion(() -> {
            return "FML3";
        }).simpleChannel();
        playChannel.messageBuilder(PlayMessages.SpawnEntity.class, 0).decoder(PlayMessages.SpawnEntity::decode).encoder(PlayMessages.SpawnEntity::encode).consumerNetworkThread(PlayMessages.SpawnEntity::handle).add();
        playChannel.messageBuilder(PlayMessages.OpenContainer.class, 1).decoder(PlayMessages.OpenContainer::decode).encoder(PlayMessages.OpenContainer::encode).consumerNetworkThread(PlayMessages.OpenContainer::handle).add();
        return playChannel;
    }

    public static List<EventNetworkChannel> buildMCRegistrationChannels() {
        EventNetworkChannel mcRegChannel = ChannelBuilder.named(NetworkConstants.MC_REGISTER_RESOURCE).clientAcceptedVersions((a) -> {
            return true;
        }).serverAcceptedVersions((a) -> {
            return true;
        }).networkProtocolVersion(() -> {
            return "FML3";
        }).eventNetworkChannel();
        MCRegisterPacketHandler var10001 = MCRegisterPacketHandler.INSTANCE;
        Objects.requireNonNull(var10001);
        mcRegChannel.addListener(var10001::registerListener);
        EventNetworkChannel mcUnregChannel = ChannelBuilder.named(NetworkConstants.MC_UNREGISTER_RESOURCE).clientAcceptedVersions((a) -> {
            return true;
        }).serverAcceptedVersions((a) -> {
            return true;
        }).networkProtocolVersion(() -> {
            return "FML3";
        }).eventNetworkChannel();
        var10001 = MCRegisterPacketHandler.INSTANCE;
        Objects.requireNonNull(var10001);
        mcUnregChannel.addListener(var10001::unregisterListener);
        return Arrays.asList(mcRegChannel, mcUnregChannel);
    }
}
