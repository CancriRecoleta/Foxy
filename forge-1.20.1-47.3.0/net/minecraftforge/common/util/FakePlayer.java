//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class FakePlayer extends ServerPlayer {
    public FakePlayer(ServerLevel level, GameProfile name) {
        super(level.getServer(), level, name);
        this.connection = new FakePlayerNetHandler(level.getServer(), this);
    }

    public void displayClientMessage(Component chatComponent, boolean actionBar) {
    }

    public void awardStat(Stat stat, int amount) {
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    public boolean canHarmPlayer(Player player) {
        return false;
    }

    public void die(DamageSource source) {
    }

    public void tick() {
    }

    public void updateOptions(ServerboundClientInformationPacket packet) {
    }

    public @Nullable MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @ParametersAreNonnullByDefault
    private static class FakePlayerNetHandler extends ServerGamePacketListenerImpl {
        private static final Connection DUMMY_CONNECTION;

        public FakePlayerNetHandler(MinecraftServer server, ServerPlayer player) {
            super(server, DUMMY_CONNECTION, player);
        }

        public void tick() {
        }

        public void resetPosition() {
        }

        public void disconnect(Component message) {
        }

        public void handlePlayerInput(ServerboundPlayerInputPacket packet) {
        }

        public void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {
        }

        public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {
        }

        public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {
        }

        public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {
        }

        public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {
        }

        public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {
        }

        public void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {
        }

        public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {
        }

        public void handlePickItem(ServerboundPickItemPacket packet) {
        }

        public void handleRenameItem(ServerboundRenameItemPacket packet) {
        }

        public void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {
        }

        public void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {
        }

        public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {
        }

        public void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {
        }

        public void handleSelectTrade(ServerboundSelectTradePacket packet) {
        }

        public void handleEditBook(ServerboundEditBookPacket packet) {
        }

        public void handleEntityTagQuery(ServerboundEntityTagQuery packet) {
        }

        public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery packet) {
        }

        public void handleMovePlayer(ServerboundMovePlayerPacket packet) {
        }

        public void teleport(double x, double y, double z, float yaw, float pitch) {
        }

        public void handlePlayerAction(ServerboundPlayerActionPacket packet) {
        }

        public void handleUseItemOn(ServerboundUseItemOnPacket packet) {
        }

        public void handleUseItem(ServerboundUseItemPacket packet) {
        }

        public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {
        }

        public void handleResourcePackResponse(ServerboundResourcePackPacket packet) {
        }

        public void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {
        }

        public void onDisconnect(Component message) {
        }

        public void send(Packet<?> packet) {
        }

        public void send(Packet<?> packet, @Nullable PacketSendListener sendListener) {
        }

        public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {
        }

        public void handleChat(ServerboundChatPacket packet) {
        }

        public void handleAnimate(ServerboundSwingPacket packet) {
        }

        public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {
        }

        public void handleInteract(ServerboundInteractPacket packet) {
        }

        public void handleClientCommand(ServerboundClientCommandPacket packet) {
        }

        public void handleContainerClose(ServerboundContainerClosePacket packet) {
        }

        public void handleContainerClick(ServerboundContainerClickPacket packet) {
        }

        public void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {
        }

        public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {
        }

        public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {
        }

        public void handleSignUpdate(ServerboundSignUpdatePacket packet) {
        }

        public void handleKeepAlive(ServerboundKeepAlivePacket packet) {
        }

        public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {
        }

        public void handleClientInformation(ServerboundClientInformationPacket packet) {
        }

        public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
        }

        public void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {
        }

        public void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {
        }

        public void teleport(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> relativeSet) {
        }

        public void ackBlockChangesUpTo(int sequence) {
        }

        public void handleChatCommand(ServerboundChatCommandPacket packet) {
        }

        public void handleChatAck(ServerboundChatAckPacket packet) {
        }

        public void addPendingMessage(PlayerChatMessage message) {
        }

        public void sendPlayerChatMessage(PlayerChatMessage message, ChatType.Bound boundChatType) {
        }

        public void sendDisguisedChatMessage(Component content, ChatType.Bound boundChatType) {
        }

        public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket packet) {
        }

        static {
            DUMMY_CONNECTION = new Connection(PacketFlow.CLIENTBOUND);
        }
    }
}
