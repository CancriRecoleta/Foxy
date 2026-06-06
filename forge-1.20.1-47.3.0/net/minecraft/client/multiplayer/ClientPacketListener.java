//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ServerData.ServerPackStatus;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.BeeDebugRenderer;
import net.minecraft.client.renderer.debug.BrainDebugRenderer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.chat.SignedMessageChain.Encoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Crypt.SaltSupplier;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientPacketListener implements TickablePacketListener, ClientGamePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
    private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable("multiplayer.unsecureserver.toast.title");
    private static final Component UNSERURE_SERVER_TOAST = Component.translatable("multiplayer.unsecureserver.toast");
    private static final Component INVALID_PACKET = Component.translatable("multiplayer.disconnect.invalid_packet");
    private static final Component CHAT_VALIDATION_FAILED_ERROR = Component.translatable("multiplayer.disconnect.chat_validation_failed");
    private static final int PENDING_OFFSET_THRESHOLD = 64;
    private final Connection connection;
    private final List<DeferredPacket> deferredPackets = new ArrayList();
    @Nullable
    private final ServerData serverData;
    private final GameProfile localGameProfile;
    private final Screen callbackScreen;
    private final Minecraft minecraft;
    private ClientLevel level;
    private ClientLevel.ClientLevelData levelData;
    private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
    private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet();
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
    private int serverChunkRadius = 3;
    private int serverSimulationDistance = 3;
    private final RandomSource random = RandomSource.createThreadSafe();
    public CommandDispatcher<SharedSuggestionProvider> commands = new CommandDispatcher();
    private final RecipeManager recipeManager = new RecipeManager();
    private final UUID id = UUID.randomUUID();
    private Set<ResourceKey<Level>> levels;
    private LayeredRegistryAccess<ClientRegistryLayer> registryAccess = ClientRegistryLayer.createRegistryAccess();
    private FeatureFlagSet enabledFeatures;
    private final WorldSessionTelemetryManager telemetryManager;
    @Nullable
    private LocalChatSession chatSession;
    private SignedMessageChain.Encoder signedMessageEncoder;
    private LastSeenMessagesTracker lastSeenMessages;
    private MessageSignatureCache messageSignatureCache;

    public ClientPacketListener(Minecraft p_253924_, Screen p_254239_, Connection p_253614_, @Nullable ServerData p_254072_, GameProfile p_254079_, WorldSessionTelemetryManager p_262115_) {
        this.enabledFeatures = FeatureFlags.DEFAULT_FLAGS;
        this.signedMessageEncoder = Encoder.UNSIGNED;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        this.minecraft = p_253924_;
        this.callbackScreen = p_254239_;
        this.connection = p_253614_;
        this.serverData = p_254072_;
        this.localGameProfile = p_254079_;
        this.advancements = new ClientAdvancements(p_253924_, p_262115_);
        this.suggestionsProvider = new ClientSuggestionProvider(this, p_253924_);
        this.telemetryManager = p_262115_;
    }

    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public void close() {
        this.level = null;
        this.telemetryManager.onDisconnect();
    }

    public RecipeManager getRecipeManager() {
        return this.recipeManager;
    }

    public void handleLogin(ClientboundLoginPacket p_105030_) {
        PacketUtils.ensureRunningOnSameThread(p_105030_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        this.registryAccess = this.registryAccess.replaceFrom(ClientRegistryLayer.REMOTE, (RegistryAccess.Frozen[])(p_105030_.registryHolder()));
        if (!this.connection.isMemoryConnection()) {
            this.registryAccess.compositeAccess().registries().forEach((p_205542_) -> {
                p_205542_.value().resetTags();
            });
        }

        List<ResourceKey<Level>> list = Lists.newArrayList(p_105030_.levels());
        Collections.shuffle(list);
        this.levels = Sets.newLinkedHashSet(list);
        ResourceKey<Level> resourcekey = p_105030_.dimension();
        Holder<DimensionType> holder = this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(p_105030_.dimensionType());
        this.serverChunkRadius = p_105030_.chunkRadius();
        this.serverSimulationDistance = p_105030_.simulationDistance();
        boolean flag = p_105030_.isDebug();
        boolean flag1 = p_105030_.isFlat();
        ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(Difficulty.NORMAL, p_105030_.hardcore(), flag1);
        this.levelData = clientlevel$clientleveldata;
        int var10007 = this.serverChunkRadius;
        int var10008 = this.serverSimulationDistance;
        Minecraft var10009 = this.minecraft;
        Objects.requireNonNull(var10009);
        this.level = new ClientLevel(this, clientlevel$clientleveldata, resourcekey, holder, var10007, var10008, var10009::getProfiler, this.minecraft.levelRenderer, flag, p_105030_.seed());
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0F);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }

        this.minecraft.debugRenderer.clear();
        this.minecraft.player.resetPos();
        ForgeHooksClient.firePlayerLogin(this.minecraft.gameMode, this.minecraft.player, this.minecraft.getConnection().connection);
        int i = p_105030_.playerId();
        this.minecraft.player.setId(i);
        this.level.addPlayer(i, this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.cameraEntity = this.minecraft.player;
        this.minecraft.setScreen(new ReceivingLevelScreen());
        this.minecraft.player.setReducedDebugInfo(p_105030_.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen(p_105030_.showDeathScreen());
        this.minecraft.player.setLastDeathLocation(p_105030_.lastDeathLocation());
        this.minecraft.player.setPortalCooldown(p_105030_.portalCooldown());
        this.minecraft.gameMode.setLocalMode(p_105030_.gameType(), p_105030_.previousGameType());
        this.minecraft.options.setServerRenderDistance(p_105030_.chunkRadius());
        NetworkHooks.sendMCRegistryPackets(this.connection, "PLAY_TO_SERVER");
        this.minecraft.options.broadcastOptions();
        this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
        this.chatSession = null;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        if (this.connection.isEncrypted()) {
            this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync((p_253341_) -> {
                p_253341_.ifPresent(this::setKeyPair);
            }, this.minecraft);
        }

        this.telemetryManager.onPlayerInfoReceived(p_105030_.gameType(), p_105030_.hardcore());
        this.minecraft.quickPlayLog().log(this.minecraft);
    }

    public void handleAddEntity(ClientboundAddEntityPacket p_104958_) {
        PacketUtils.ensureRunningOnSameThread(p_104958_, this, (BlockableEventLoop)this.minecraft);
        EntityType<?> entitytype = p_104958_.getType();
        Entity entity = entitytype.create(this.level);
        if (entity != null) {
            entity.recreateFromPacket(p_104958_);
            int i = p_104958_.getId();
            this.level.putNonPlayerEntity(i, entity);
            this.postAddEntitySoundInstance(entity);
        } else {
            LOGGER.warn("Skipping Entity with id {}", entitytype);
        }

    }

    private void postAddEntitySoundInstance(Entity p_233664_) {
        if (p_233664_ instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)p_233664_));
        } else if (p_233664_ instanceof Bee) {
            boolean flag = ((Bee)p_233664_).isAngry();
            Object beesoundinstance;
            if (flag) {
                beesoundinstance = new BeeAggressiveSoundInstance((Bee)p_233664_);
            } else {
                beesoundinstance = new BeeFlyingSoundInstance((Bee)p_233664_);
            }

            this.minecraft.getSoundManager().queueTickingSound((TickableSoundInstance)beesoundinstance);
        }

    }

    public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket p_104960_) {
        PacketUtils.ensureRunningOnSameThread(p_104960_, this, (BlockableEventLoop)this.minecraft);
        double d0 = p_104960_.getX();
        double d1 = p_104960_.getY();
        double d2 = p_104960_.getZ();
        Entity entity = new ExperienceOrb(this.level, d0, d1, d2, p_104960_.getValue());
        entity.syncPacketPositionCodec(d0, d1, d2);
        entity.setYRot(0.0F);
        entity.setXRot(0.0F);
        entity.setId(p_104960_.getId());
        this.level.putNonPlayerEntity(p_104960_.getId(), entity);
    }

    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket p_105092_) {
        PacketUtils.ensureRunningOnSameThread(p_105092_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105092_.getId());
        if (entity != null) {
            entity.lerpMotion((double)p_105092_.getXa() / 8000.0, (double)p_105092_.getYa() / 8000.0, (double)p_105092_.getZa() / 8000.0);
        }

    }

    public void handleSetEntityData(ClientboundSetEntityDataPacket p_105088_) {
        PacketUtils.ensureRunningOnSameThread(p_105088_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105088_.id());
        if (entity != null) {
            entity.getEntityData().assignValues(p_105088_.packedItems());
        }

    }

    public void handleAddPlayer(ClientboundAddPlayerPacket p_104966_) {
        PacketUtils.ensureRunningOnSameThread(p_104966_, this, (BlockableEventLoop)this.minecraft);
        PlayerInfo playerinfo = this.getPlayerInfo(p_104966_.getPlayerId());
        if (playerinfo == null) {
            LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", p_104966_.getPlayerId());
        } else {
            double d0 = p_104966_.getX();
            double d1 = p_104966_.getY();
            double d2 = p_104966_.getZ();
            float f = (float)(p_104966_.getyRot() * 360) / 256.0F;
            float f1 = (float)(p_104966_.getxRot() * 360) / 256.0F;
            int i = p_104966_.getEntityId();
            RemotePlayer remoteplayer = new RemotePlayer(this.minecraft.level, playerinfo.getProfile());
            remoteplayer.setId(i);
            remoteplayer.syncPacketPositionCodec(d0, d1, d2);
            remoteplayer.absMoveTo(d0, d1, d2, f, f1);
            remoteplayer.setOldPosAndRot();
            this.level.addPlayer(i, remoteplayer);
        }

    }

    public void handleTeleportEntity(ClientboundTeleportEntityPacket p_105124_) {
        PacketUtils.ensureRunningOnSameThread(p_105124_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105124_.getId());
        if (entity != null) {
            double d0 = p_105124_.getX();
            double d1 = p_105124_.getY();
            double d2 = p_105124_.getZ();
            entity.syncPacketPositionCodec(d0, d1, d2);
            if (!entity.isControlledByLocalInstance()) {
                float f = (float)(p_105124_.getyRot() * 360) / 256.0F;
                float f1 = (float)(p_105124_.getxRot() * 360) / 256.0F;
                entity.lerpTo(d0, d1, d2, f, f1, 3, true);
                entity.setOnGround(p_105124_.isOnGround());
            }
        }

    }

    public void handleSetCarriedItem(ClientboundSetCarriedItemPacket p_105078_) {
        PacketUtils.ensureRunningOnSameThread(p_105078_, this, (BlockableEventLoop)this.minecraft);
        if (Inventory.isHotbarSlot(p_105078_.getSlot())) {
            this.minecraft.player.getInventory().selected = p_105078_.getSlot();
        }

    }

    public void handleMoveEntity(ClientboundMoveEntityPacket p_105036_) {
        PacketUtils.ensureRunningOnSameThread(p_105036_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = p_105036_.getEntity(this.level);
        if (entity != null && !entity.isControlledByLocalInstance()) {
            if (p_105036_.hasPosition()) {
                VecDeltaCodec vecdeltacodec = entity.getPositionCodec();
                Vec3 vec3 = vecdeltacodec.decode((long)p_105036_.getXa(), (long)p_105036_.getYa(), (long)p_105036_.getZa());
                vecdeltacodec.setBase(vec3);
                float f = p_105036_.hasRotation() ? (float)(p_105036_.getyRot() * 360) / 256.0F : entity.getYRot();
                float f1 = p_105036_.hasRotation() ? (float)(p_105036_.getxRot() * 360) / 256.0F : entity.getXRot();
                entity.lerpTo(vec3.x(), vec3.y(), vec3.z(), f, f1, 3, false);
            } else if (p_105036_.hasRotation()) {
                float f2 = (float)(p_105036_.getyRot() * 360) / 256.0F;
                float f3 = (float)(p_105036_.getxRot() * 360) / 256.0F;
                entity.lerpTo(entity.getX(), entity.getY(), entity.getZ(), f2, f3, 3, false);
            }

            entity.setOnGround(p_105036_.isOnGround());
        }

    }

    public void handleRotateMob(ClientboundRotateHeadPacket p_105068_) {
        PacketUtils.ensureRunningOnSameThread(p_105068_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = p_105068_.getEntity(this.level);
        if (entity != null) {
            float f = (float)(p_105068_.getYHeadRot() * 360) / 256.0F;
            entity.lerpHeadTo(f, 3);
        }

    }

    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket p_182633_) {
        PacketUtils.ensureRunningOnSameThread(p_182633_, this, (BlockableEventLoop)this.minecraft);
        p_182633_.getEntityIds().forEach((p_205521_) -> {
            this.level.removeEntity(p_205521_, RemovalReason.DISCARDED);
        });
    }

    public void handleMovePlayer(ClientboundPlayerPositionPacket p_105056_) {
        PacketUtils.ensureRunningOnSameThread(p_105056_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        Vec3 vec3 = player.getDeltaMovement();
        boolean flag = p_105056_.getRelativeArguments().contains(RelativeMovement.X);
        boolean flag1 = p_105056_.getRelativeArguments().contains(RelativeMovement.Y);
        boolean flag2 = p_105056_.getRelativeArguments().contains(RelativeMovement.Z);
        double d0;
        double d1;
        if (flag) {
            d0 = vec3.x();
            d1 = player.getX() + p_105056_.getX();
            player.xOld += p_105056_.getX();
            player.xo += p_105056_.getX();
        } else {
            d0 = 0.0;
            d1 = p_105056_.getX();
            player.xOld = d1;
            player.xo = d1;
        }

        double d2;
        double d3;
        if (flag1) {
            d2 = vec3.y();
            d3 = player.getY() + p_105056_.getY();
            player.yOld += p_105056_.getY();
            player.yo += p_105056_.getY();
        } else {
            d2 = 0.0;
            d3 = p_105056_.getY();
            player.yOld = d3;
            player.yo = d3;
        }

        double d4;
        double d5;
        if (flag2) {
            d4 = vec3.z();
            d5 = player.getZ() + p_105056_.getZ();
            player.zOld += p_105056_.getZ();
            player.zo += p_105056_.getZ();
        } else {
            d4 = 0.0;
            d5 = p_105056_.getZ();
            player.zOld = d5;
            player.zo = d5;
        }

        player.setPos(d1, d3, d5);
        player.setDeltaMovement(d0, d2, d4);
        float f = p_105056_.getYRot();
        float f1 = p_105056_.getXRot();
        if (p_105056_.getRelativeArguments().contains(RelativeMovement.X_ROT)) {
            player.setXRot(player.getXRot() + f1);
            player.xRotO += f1;
        } else {
            player.setXRot(f1);
            player.xRotO = f1;
        }

        if (p_105056_.getRelativeArguments().contains(RelativeMovement.Y_ROT)) {
            player.setYRot(player.getYRot() + f);
            player.yRotO += f;
        } else {
            player.setYRot(f);
            player.yRotO = f;
        }

        this.connection.send(new ServerboundAcceptTeleportationPacket(p_105056_.getId()));
        this.connection.send(new ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false));
    }

    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket p_105070_) {
        PacketUtils.ensureRunningOnSameThread(p_105070_, this, (BlockableEventLoop)this.minecraft);
        p_105070_.runUpdates((p_284633_, p_284634_) -> {
            this.level.setServerVerifiedBlockState(p_284633_, p_284634_, 19);
        });
    }

    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket p_194241_) {
        PacketUtils.ensureRunningOnSameThread(p_194241_, this, (BlockableEventLoop)this.minecraft);
        int i = p_194241_.getX();
        int j = p_194241_.getZ();
        this.updateLevelChunk(i, j, p_194241_.getChunkData());
        ClientboundLightUpdatePacketData clientboundlightupdatepacketdata = p_194241_.getLightData();
        this.level.queueLightUpdate(() -> {
            this.applyLightData(i, j, clientboundlightupdatepacketdata);
            LevelChunk levelchunk = this.level.getChunkSource().getChunk(i, j, false);
            if (levelchunk != null) {
                this.enableChunkLight(levelchunk, i, j);
            }

        });
    }

    public void handleChunksBiomes(ClientboundChunksBiomesPacket p_275437_) {
        PacketUtils.ensureRunningOnSameThread(p_275437_, this, (BlockableEventLoop)this.minecraft);
        Iterator var2 = p_275437_.chunkBiomeData().iterator();

        ClientboundChunksBiomesPacket.ChunkBiomeData clientboundchunksbiomespacket$chunkbiomedata2;
        while(var2.hasNext()) {
            clientboundchunksbiomespacket$chunkbiomedata2 = (ClientboundChunksBiomesPacket.ChunkBiomeData)var2.next();
            this.level.getChunkSource().replaceBiomes(clientboundchunksbiomespacket$chunkbiomedata2.pos().x, clientboundchunksbiomespacket$chunkbiomedata2.pos().z, clientboundchunksbiomespacket$chunkbiomedata2.getReadBuffer());
        }

        var2 = p_275437_.chunkBiomeData().iterator();

        while(var2.hasNext()) {
            clientboundchunksbiomespacket$chunkbiomedata2 = (ClientboundChunksBiomesPacket.ChunkBiomeData)var2.next();
            this.level.onChunkLoaded(new ChunkPos(clientboundchunksbiomespacket$chunkbiomedata2.pos().x, clientboundchunksbiomespacket$chunkbiomedata2.pos().z));
        }

        var2 = p_275437_.chunkBiomeData().iterator();

        while(var2.hasNext()) {
            clientboundchunksbiomespacket$chunkbiomedata2 = (ClientboundChunksBiomesPacket.ChunkBiomeData)var2.next();

            for(int i = -1; i <= 1; ++i) {
                for(int j = -1; j <= 1; ++j) {
                    for(int k = this.level.getMinSection(); k < this.level.getMaxSection(); ++k) {
                        this.minecraft.levelRenderer.setSectionDirty(clientboundchunksbiomespacket$chunkbiomedata2.pos().x + i, k, clientboundchunksbiomespacket$chunkbiomedata2.pos().z + j);
                    }
                }
            }
        }

    }

    private void updateLevelChunk(int p_194199_, int p_194200_, ClientboundLevelChunkPacketData p_194201_) {
        this.level.getChunkSource().replaceWithPacketData(p_194199_, p_194200_, p_194201_.getReadBuffer(), p_194201_.getHeightmaps(), p_194201_.getBlockEntitiesTagsConsumer(p_194199_, p_194200_));
    }

    private void enableChunkLight(LevelChunk p_194213_, int p_194214_, int p_194215_) {
        LevelLightEngine levellightengine = this.level.getChunkSource().getLightEngine();
        LevelChunkSection[] alevelchunksection = p_194213_.getSections();
        ChunkPos chunkpos = p_194213_.getPos();

        for(int i = 0; i < alevelchunksection.length; ++i) {
            LevelChunkSection levelchunksection = alevelchunksection[i];
            int j = this.level.getSectionYFromSectionIndex(i);
            levellightengine.updateSectionStatus(SectionPos.of(chunkpos, j), levelchunksection.hasOnlyAir());
            this.level.setSectionDirtyWithNeighbors(p_194214_, j, p_194215_);
        }

    }

    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket p_105014_) {
        PacketUtils.ensureRunningOnSameThread(p_105014_, this, (BlockableEventLoop)this.minecraft);
        int i = p_105014_.getX();
        int j = p_105014_.getZ();
        ClientChunkCache clientchunkcache = this.level.getChunkSource();
        clientchunkcache.drop(i, j);
        this.queueLightRemoval(p_105014_);
    }

    private void queueLightRemoval(ClientboundForgetLevelChunkPacket p_194253_) {
        ChunkPos chunkpos = new ChunkPos(p_194253_.getX(), p_194253_.getZ());
        this.level.queueLightUpdate(() -> {
            LevelLightEngine levellightengine = this.level.getLightEngine();
            levellightengine.setLightEnabled(chunkpos, false);

            int j;
            for(j = levellightengine.getMinLightSection(); j < levellightengine.getMaxLightSection(); ++j) {
                SectionPos sectionpos = SectionPos.of(chunkpos, j);
                levellightengine.queueSectionData(LightLayer.BLOCK, sectionpos, (DataLayer)null);
                levellightengine.queueSectionData(LightLayer.SKY, sectionpos, (DataLayer)null);
            }

            for(j = this.level.getMinSection(); j < this.level.getMaxSection(); ++j) {
                levellightengine.updateSectionStatus(SectionPos.of(chunkpos, j), true);
            }

        });
    }

    public void handleBlockUpdate(ClientboundBlockUpdatePacket p_104980_) {
        PacketUtils.ensureRunningOnSameThread(p_104980_, this, (BlockableEventLoop)this.minecraft);
        this.level.setServerVerifiedBlockState(p_104980_.getPos(), p_104980_.getBlockState(), 19);
    }

    public void handleDisconnect(ClientboundDisconnectPacket p_105008_) {
        this.connection.disconnect(p_105008_.getReason());
    }

    public void onDisconnect(Component p_104954_) {
        this.minecraft.clearLevel();
        this.telemetryManager.onDisconnect();
        if (this.callbackScreen != null) {
            if (this.callbackScreen instanceof RealmsScreen) {
                this.minecraft.setScreen(new DisconnectedRealmsScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, p_104954_));
            } else {
                this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, GENERIC_DISCONNECT_MESSAGE, p_104954_));
            }
        } else {
            this.minecraft.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), GENERIC_DISCONNECT_MESSAGE, p_104954_));
        }

    }

    public void send(Packet<?> p_104956_) {
        this.connection.send(p_104956_);
    }

    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket p_105122_) {
        PacketUtils.ensureRunningOnSameThread(p_105122_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105122_.getItemId());
        LivingEntity livingentity = (LivingEntity)this.level.getEntity(p_105122_.getPlayerId());
        if (livingentity == null) {
            livingentity = this.minecraft.player;
        }

        if (entity != null) {
            if (entity instanceof ExperienceOrb) {
                this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F, false);
            } else {
                this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F, false);
            }

            this.minecraft.particleEngine.add(new ItemPickupParticle(this.minecraft.getEntityRenderDispatcher(), this.minecraft.renderBuffers(), this.level, entity, (Entity)livingentity));
            if (entity instanceof ItemEntity) {
                ItemEntity itementity = (ItemEntity)entity;
                ItemStack itemstack = itementity.getItem();
                if (!itemstack.isEmpty()) {
                    itemstack.shrink(p_105122_.getAmount());
                }

                if (itemstack.isEmpty()) {
                    this.level.removeEntity(p_105122_.getItemId(), RemovalReason.DISCARDED);
                }
            } else if (!(entity instanceof ExperienceOrb)) {
                this.level.removeEntity(p_105122_.getItemId(), RemovalReason.DISCARDED);
            }
        }

    }

    public void handleSystemChat(ClientboundSystemChatPacket p_233708_) {
        PacketUtils.ensureRunningOnSameThread(p_233708_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.getChatListener().handleSystemMessage(p_233708_.content(), p_233708_.overlay());
    }

    public void handlePlayerChat(ClientboundPlayerChatPacket p_233702_) {
        PacketUtils.ensureRunningOnSameThread(p_233702_, this, (BlockableEventLoop)this.minecraft);
        Optional<SignedMessageBody> optional = p_233702_.body().unpack(this.messageSignatureCache);
        Optional<ChatType.Bound> optional1 = p_233702_.chatType().resolve(this.registryAccess.compositeAccess());
        if (!optional.isEmpty() && !optional1.isEmpty()) {
            UUID uuid = p_233702_.sender();
            PlayerInfo playerinfo = this.getPlayerInfo(uuid);
            if (playerinfo == null) {
                this.connection.disconnect(CHAT_VALIDATION_FAILED_ERROR);
            } else {
                RemoteChatSession remotechatsession = playerinfo.getChatSession();
                SignedMessageLink signedmessagelink;
                if (remotechatsession != null) {
                    signedmessagelink = new SignedMessageLink(p_233702_.index(), uuid, remotechatsession.sessionId());
                } else {
                    signedmessagelink = SignedMessageLink.unsigned(uuid);
                }

                PlayerChatMessage playerchatmessage = new PlayerChatMessage(signedmessagelink, p_233702_.signature(), (SignedMessageBody)optional.get(), p_233702_.unsignedContent(), p_233702_.filterMask());
                if (!playerinfo.getMessageValidator().updateAndValidate(playerchatmessage)) {
                    this.connection.disconnect(CHAT_VALIDATION_FAILED_ERROR);
                } else {
                    this.minecraft.getChatListener().handlePlayerChatMessage(playerchatmessage, playerinfo.getProfile(), (ChatType.Bound)optional1.get());
                    this.messageSignatureCache.push(playerchatmessage);
                }
            }
        } else {
            this.connection.disconnect(INVALID_PACKET);
        }

    }

    public void handleDisguisedChat(ClientboundDisguisedChatPacket p_251920_) {
        PacketUtils.ensureRunningOnSameThread(p_251920_, this, (BlockableEventLoop)this.minecraft);
        Optional<ChatType.Bound> optional = p_251920_.chatType().resolve(this.registryAccess.compositeAccess());
        if (optional.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
        } else {
            this.minecraft.getChatListener().handleDisguisedChatMessage(p_251920_.message(), (ChatType.Bound)optional.get());
        }

    }

    public void handleDeleteChat(ClientboundDeleteChatPacket p_241325_) {
        PacketUtils.ensureRunningOnSameThread(p_241325_, this, (BlockableEventLoop)this.minecraft);
        Optional<MessageSignature> optional = p_241325_.messageSignature().unpack(this.messageSignatureCache);
        if (optional.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
        } else {
            this.lastSeenMessages.ignorePending((MessageSignature)optional.get());
            if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue((MessageSignature)optional.get())) {
                this.minecraft.gui.getChat().deleteMessage((MessageSignature)optional.get());
            }
        }

    }

    public void handleAnimate(ClientboundAnimatePacket p_104968_) {
        PacketUtils.ensureRunningOnSameThread(p_104968_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_104968_.getId());
        if (entity != null) {
            LivingEntity livingentity1;
            if (p_104968_.getAction() == 0) {
                livingentity1 = (LivingEntity)entity;
                livingentity1.swing(InteractionHand.MAIN_HAND);
            } else if (p_104968_.getAction() == 3) {
                livingentity1 = (LivingEntity)entity;
                livingentity1.swing(InteractionHand.OFF_HAND);
            } else if (p_104968_.getAction() == 2) {
                Player player = (Player)entity;
                player.stopSleepInBed(false, false);
            } else if (p_104968_.getAction() == 4) {
                this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
            } else if (p_104968_.getAction() == 5) {
                this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
            }
        }

    }

    public void handleHurtAnimation(ClientboundHurtAnimationPacket p_265581_) {
        PacketUtils.ensureRunningOnSameThread(p_265581_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_265581_.id());
        if (entity != null) {
            entity.animateHurt(p_265581_.yaw());
        }

    }

    public void handleSetTime(ClientboundSetTimePacket p_105108_) {
        PacketUtils.ensureRunningOnSameThread(p_105108_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.level.setGameTime(p_105108_.getGameTime());
        this.minecraft.level.setDayTime(p_105108_.getDayTime());
        this.telemetryManager.setTime(p_105108_.getGameTime());
    }

    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket p_105084_) {
        PacketUtils.ensureRunningOnSameThread(p_105084_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.level.setDefaultSpawnPos(p_105084_.getPos(), p_105084_.getAngle());
        Screen screen = this.minecraft.screen;
        if (screen instanceof ReceivingLevelScreen receivinglevelscreen) {
            receivinglevelscreen.loadingPacketsReceived();
        }

    }

    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket p_105102_) {
        PacketUtils.ensureRunningOnSameThread(p_105102_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105102_.getVehicle());
        if (entity == null) {
            LOGGER.warn("Received passengers for unknown entity");
        } else {
            boolean flag = entity.hasIndirectPassenger(this.minecraft.player);
            entity.ejectPassengers();
            int[] var4 = p_105102_.getPassengers();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                int i = var4[var6];
                Entity entity1 = this.level.getEntity(i);
                if (entity1 != null) {
                    entity1.startRiding(entity, true);
                    if (entity1 == this.minecraft.player && !flag) {
                        if (entity instanceof Boat) {
                            this.minecraft.player.yRotO = entity.getYRot();
                            this.minecraft.player.setYRot(entity.getYRot());
                            this.minecraft.player.setYHeadRot(entity.getYRot());
                        }

                        Component component = Component.translatable("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage());
                        this.minecraft.gui.setOverlayMessage(component, false);
                        this.minecraft.getNarrator().sayNow((Component)component);
                    }
                }
            }
        }

    }

    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket p_105090_) {
        PacketUtils.ensureRunningOnSameThread(p_105090_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105090_.getSourceId());
        if (entity instanceof Mob) {
            ((Mob)entity).setDelayedLeashHolderId(p_105090_.getDestId());
        }

    }

    private static ItemStack findTotem(Player p_104928_) {
        InteractionHand[] var1 = InteractionHand.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            InteractionHand interactionhand = var1[var3];
            ItemStack itemstack = p_104928_.getItemInHand(interactionhand);
            if (itemstack.is(Items.TOTEM_OF_UNDYING)) {
                return itemstack;
            }
        }

        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    public void handleEntityEvent(ClientboundEntityEventPacket p_105010_) {
        PacketUtils.ensureRunningOnSameThread(p_105010_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = p_105010_.getEntity(this.level);
        if (entity != null) {
            switch (p_105010_.getEventId()) {
                case 21:
                    this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)entity));
                    break;
                case 35:
                    int i = true;
                    this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                    if (entity == this.minecraft.player) {
                        this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
                    }
                    break;
                case 63:
                    this.minecraft.getSoundManager().play(new SnifferSoundInstance((Sniffer)entity));
                    break;
                default:
                    entity.handleEntityEvent(p_105010_.getEventId());
            }
        }

    }

    public void handleDamageEvent(ClientboundDamageEventPacket p_270800_) {
        PacketUtils.ensureRunningOnSameThread(p_270800_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_270800_.entityId());
        if (entity != null) {
            entity.handleDamageEvent(p_270800_.getSource(this.level));
        }

    }

    public void handleSetHealth(ClientboundSetHealthPacket p_105098_) {
        PacketUtils.ensureRunningOnSameThread(p_105098_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.player.hurtTo(p_105098_.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel(p_105098_.getFood());
        this.minecraft.player.getFoodData().setSaturation(p_105098_.getSaturation());
    }

    public void handleSetExperience(ClientboundSetExperiencePacket p_105096_) {
        PacketUtils.ensureRunningOnSameThread(p_105096_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.player.setExperienceValues(p_105096_.getExperienceProgress(), p_105096_.getTotalExperience(), p_105096_.getExperienceLevel());
    }

    public void handleRespawn(ClientboundRespawnPacket p_105066_) {
        PacketUtils.ensureRunningOnSameThread(p_105066_, this, (BlockableEventLoop)this.minecraft);
        ResourceKey<Level> resourcekey = p_105066_.getDimension();
        Holder<DimensionType> holder = this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(p_105066_.getDimensionType());
        LocalPlayer localplayer = this.minecraft.player;
        int i = localplayer.getId();
        if (resourcekey != localplayer.level().dimension()) {
            Scoreboard scoreboard = this.level.getScoreboard();
            Map<String, MapItemSavedData> map = this.level.getAllMapData();
            boolean flag = p_105066_.isDebug();
            boolean flag1 = p_105066_.isFlat();
            ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(this.levelData.getDifficulty(), this.levelData.isHardcore(), flag1);
            this.levelData = clientlevel$clientleveldata;
            int var10007 = this.serverChunkRadius;
            int var10008 = this.serverSimulationDistance;
            Minecraft var10009 = this.minecraft;
            Objects.requireNonNull(var10009);
            this.level = new ClientLevel(this, clientlevel$clientleveldata, resourcekey, holder, var10007, var10008, var10009::getProfiler, this.minecraft.levelRenderer, flag, p_105066_.getSeed());
            this.level.setScoreboard(scoreboard);
            this.level.addMapData(map);
            this.minecraft.setLevel(this.level);
            this.minecraft.setScreen(new ReceivingLevelScreen());
        }

        String s = localplayer.getServerBrand();
        this.minecraft.cameraEntity = null;
        if (localplayer.hasContainerOpen()) {
            localplayer.closeContainer();
        }

        LocalPlayer localplayer1;
        if (p_105066_.shouldKeep((byte)2)) {
            localplayer1 = this.minecraft.gameMode.createPlayer(this.level, localplayer.getStats(), localplayer.getRecipeBook(), localplayer.isShiftKeyDown(), localplayer.isSprinting());
        } else {
            localplayer1 = this.minecraft.gameMode.createPlayer(this.level, localplayer.getStats(), localplayer.getRecipeBook());
        }

        localplayer1.setId(i);
        this.minecraft.player = localplayer1;
        if (resourcekey != localplayer.level().dimension()) {
            this.minecraft.getMusicManager().stopPlaying();
        }

        this.minecraft.cameraEntity = localplayer1;
        if (p_105066_.shouldKeep((byte)2)) {
            List<SynchedEntityData.DataValue<?>> list = localplayer.getEntityData().getNonDefaultValues();
            if (list != null) {
                localplayer1.getEntityData().assignValues(list);
            }
        }

        if (p_105066_.shouldKeep((byte)1)) {
            localplayer1.getAttributes().assignValues(localplayer.getAttributes());
        }

        localplayer1.updateSyncFields(localplayer);
        localplayer1.resetPos();
        localplayer1.setServerBrand(s);
        ForgeHooksClient.firePlayerRespawn(this.minecraft.gameMode, localplayer, localplayer1, localplayer1.connection.connection);
        this.level.addPlayer(i, localplayer1);
        localplayer1.setYRot(-180.0F);
        localplayer1.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(localplayer1);
        localplayer1.setReducedDebugInfo(localplayer.isReducedDebugInfo());
        localplayer1.setShowDeathScreen(localplayer.shouldShowDeathScreen());
        localplayer1.setLastDeathLocation(p_105066_.getLastDeathLocation());
        localplayer1.setPortalCooldown(p_105066_.getPortalCooldown());
        localplayer1.spinningEffectIntensity = localplayer.spinningEffectIntensity;
        localplayer1.oSpinningEffectIntensity = localplayer.oSpinningEffectIntensity;
        if (this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof DeathScreen.TitleConfirmScreen) {
            this.minecraft.setScreen((Screen)null);
        }

        this.minecraft.gameMode.setLocalMode(p_105066_.getPlayerGameType(), p_105066_.getPreviousPlayerGameType());
    }

    public void handleExplosion(ClientboundExplodePacket p_105012_) {
        PacketUtils.ensureRunningOnSameThread(p_105012_, this, (BlockableEventLoop)this.minecraft);
        Explosion explosion = new Explosion(this.minecraft.level, (Entity)null, p_105012_.getX(), p_105012_.getY(), p_105012_.getZ(), p_105012_.getPower(), p_105012_.getToBlow());
        explosion.finalizeExplosion(true);
        this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add((double)p_105012_.getKnockbackX(), (double)p_105012_.getKnockbackY(), (double)p_105012_.getKnockbackZ()));
    }

    public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket p_105018_) {
        PacketUtils.ensureRunningOnSameThread(p_105018_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105018_.getEntityId());
        if (entity instanceof AbstractHorse) {
            LocalPlayer localplayer = this.minecraft.player;
            AbstractHorse abstracthorse = (AbstractHorse)entity;
            SimpleContainer simplecontainer = new SimpleContainer(p_105018_.getSize());
            HorseInventoryMenu horseinventorymenu = new HorseInventoryMenu(p_105018_.getContainerId(), localplayer.getInventory(), simplecontainer, abstracthorse);
            localplayer.containerMenu = horseinventorymenu;
            this.minecraft.setScreen(new HorseInventoryScreen(horseinventorymenu, localplayer.getInventory(), abstracthorse));
        }

    }

    public void handleOpenScreen(ClientboundOpenScreenPacket p_105042_) {
        PacketUtils.ensureRunningOnSameThread(p_105042_, this, (BlockableEventLoop)this.minecraft);
        MenuScreens.create(p_105042_.getType(), this.minecraft, p_105042_.getContainerId(), p_105042_.getTitle());
    }

    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket p_105000_) {
        PacketUtils.ensureRunningOnSameThread(p_105000_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        ItemStack itemstack = p_105000_.getItem();
        int i = p_105000_.getSlot();
        this.minecraft.getTutorial().onGetItem(itemstack);
        if (p_105000_.getContainerId() == -1) {
            if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
                player.containerMenu.setCarried(itemstack);
            }
        } else if (p_105000_.getContainerId() == -2) {
            player.getInventory().setItem(i, itemstack);
        } else {
            boolean flag = false;
            Screen screen = this.minecraft.screen;
            if (screen instanceof CreativeModeInventoryScreen) {
                CreativeModeInventoryScreen creativemodeinventoryscreen = (CreativeModeInventoryScreen)screen;
                flag = !creativemodeinventoryscreen.isInventoryOpen();
            }

            if (p_105000_.getContainerId() == 0 && InventoryMenu.isHotbarSlot(i)) {
                if (!itemstack.isEmpty()) {
                    ItemStack itemstack1 = player.inventoryMenu.getSlot(i).getItem();
                    if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                        itemstack.setPopTime(5);
                    }
                }

                player.inventoryMenu.setItem(i, p_105000_.getStateId(), itemstack);
            } else if (p_105000_.getContainerId() == player.containerMenu.containerId && (p_105000_.getContainerId() != 0 || !flag)) {
                player.containerMenu.setItem(i, p_105000_.getStateId(), itemstack);
            }
        }

    }

    public void handleContainerContent(ClientboundContainerSetContentPacket p_104996_) {
        PacketUtils.ensureRunningOnSameThread(p_104996_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        if (p_104996_.getContainerId() == 0) {
            player.inventoryMenu.initializeContents(p_104996_.getStateId(), p_104996_.getItems(), p_104996_.getCarriedItem());
        } else if (p_104996_.getContainerId() == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(p_104996_.getStateId(), p_104996_.getItems(), p_104996_.getCarriedItem());
        }

    }

    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket p_105044_) {
        PacketUtils.ensureRunningOnSameThread(p_105044_, this, (BlockableEventLoop)this.minecraft);
        BlockPos blockpos = p_105044_.getPos();
        BlockEntity $$3 = this.level.getBlockEntity(blockpos);
        if ($$3 instanceof SignBlockEntity signblockentity) {
            this.minecraft.player.openTextEdit(signblockentity, p_105044_.isFrontText());
        } else {
            BlockState blockstate = this.level.getBlockState(blockpos);
            SignBlockEntity signblockentity1 = new SignBlockEntity(blockpos, blockstate);
            signblockentity1.setLevel(this.level);
            this.minecraft.player.openTextEdit(signblockentity1, p_105044_.isFrontText());
        }

    }

    public void handleBlockEntityData(ClientboundBlockEntityDataPacket p_104976_) {
        PacketUtils.ensureRunningOnSameThread(p_104976_, this, (BlockableEventLoop)this.minecraft);
        BlockPos blockpos = p_104976_.getPos();
        this.minecraft.level.getBlockEntity(blockpos, p_104976_.getType()).ifPresent((p_205557_) -> {
            p_205557_.onDataPacket(this.connection, p_104976_);
            if (p_205557_ instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }

        });
    }

    public void handleContainerSetData(ClientboundContainerSetDataPacket p_104998_) {
        PacketUtils.ensureRunningOnSameThread(p_104998_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        if (player.containerMenu != null && player.containerMenu.containerId == p_104998_.getContainerId()) {
            player.containerMenu.setData(p_104998_.getId(), p_104998_.getValue());
        }

    }

    public void handleSetEquipment(ClientboundSetEquipmentPacket p_105094_) {
        PacketUtils.ensureRunningOnSameThread(p_105094_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105094_.getEntity());
        if (entity != null) {
            p_105094_.getSlots().forEach((p_205528_) -> {
                entity.setItemSlot((EquipmentSlot)p_205528_.getFirst(), (ItemStack)p_205528_.getSecond());
            });
        }

    }

    public void handleContainerClose(ClientboundContainerClosePacket p_104994_) {
        PacketUtils.ensureRunningOnSameThread(p_104994_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.player.clientSideCloseContainer();
    }

    public void handleBlockEvent(ClientboundBlockEventPacket p_104978_) {
        PacketUtils.ensureRunningOnSameThread(p_104978_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.level.blockEvent(p_104978_.getPos(), p_104978_.getBlock(), p_104978_.getB0(), p_104978_.getB1());
    }

    public void handleBlockDestruction(ClientboundBlockDestructionPacket p_104974_) {
        PacketUtils.ensureRunningOnSameThread(p_104974_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.level.destroyBlockProgress(p_104974_.getId(), p_104974_.getPos(), p_104974_.getProgress());
    }

    public void handleGameEvent(ClientboundGameEventPacket p_105016_) {
        PacketUtils.ensureRunningOnSameThread(p_105016_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        ClientboundGameEventPacket.Type clientboundgameeventpacket$type = p_105016_.getEvent();
        float f = p_105016_.getParam();
        int i = Mth.floor(f + 0.5F);
        if (clientboundgameeventpacket$type == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
            player.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid"), false);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
            this.minecraft.gameMode.setLocalMode(GameType.byId(i));
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.WIN_GAME) {
            if (i == 0) {
                this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN)));
                this.minecraft.setScreen(new ReceivingLevelScreen());
            } else if (i == 1) {
                this.minecraft.setScreen(new WinScreen(true, () -> {
                    this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN)));
                    this.minecraft.setScreen((Screen)null);
                }));
            }
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.DEMO_EVENT) {
            Options options = this.minecraft.options;
            if (f == 0.0F) {
                this.minecraft.setScreen(new DemoIntroScreen());
            } else if (f == 101.0F) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.movement", options.keyUp.getTranslatedKeyMessage(), options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage()));
            } else if (f == 102.0F) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.jump", options.keyJump.getTranslatedKeyMessage()));
            } else if (f == 103.0F) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.help.inventory", options.keyInventory.getTranslatedKeyMessage()));
            } else if (f == 104.0F) {
                this.minecraft.gui.getChat().addMessage(Component.translatable("demo.day.6", options.keyScreenshot.getTranslatedKeyMessage()));
            }
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.ARROW_HIT_PLAYER) {
            this.level.playSound(player, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
            this.level.setRainLevel(f);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            this.level.setThunderLevel(f);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.PUFFER_FISH_STING) {
            this.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
            this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            if (i == 1) {
                this.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
            }
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
            this.minecraft.player.setShowDeathScreen(f == 0.0F);
        }

    }

    public void handleMapItemData(ClientboundMapItemDataPacket p_105032_) {
        PacketUtils.ensureRunningOnSameThread(p_105032_, this, (BlockableEventLoop)this.minecraft);
        MapRenderer maprenderer = this.minecraft.gameRenderer.getMapRenderer();
        int i = p_105032_.getMapId();
        String s = MapItem.makeKey(i);
        MapItemSavedData mapitemsaveddata = this.minecraft.level.getMapData(s);
        if (mapitemsaveddata == null) {
            mapitemsaveddata = MapItemSavedData.createForClient(p_105032_.getScale(), p_105032_.isLocked(), this.minecraft.level.dimension());
            this.minecraft.level.overrideMapData(s, mapitemsaveddata);
        }

        p_105032_.applyToMap(mapitemsaveddata);
        maprenderer.update(i, mapitemsaveddata);
    }

    public void handleLevelEvent(ClientboundLevelEventPacket p_105024_) {
        PacketUtils.ensureRunningOnSameThread(p_105024_, this, (BlockableEventLoop)this.minecraft);
        if (p_105024_.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent(p_105024_.getType(), p_105024_.getPos(), p_105024_.getData());
        } else {
            this.minecraft.level.levelEvent(p_105024_.getType(), p_105024_.getPos(), p_105024_.getData());
        }

    }

    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket p_105126_) {
        PacketUtils.ensureRunningOnSameThread(p_105126_, this, (BlockableEventLoop)this.minecraft);
        this.advancements.update(p_105126_);
    }

    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket p_105072_) {
        PacketUtils.ensureRunningOnSameThread(p_105072_, this, (BlockableEventLoop)this.minecraft);
        ResourceLocation resourcelocation = p_105072_.getTab();
        if (resourcelocation == null) {
            this.advancements.setSelectedTab((Advancement)null, false);
        } else {
            Advancement advancement = this.advancements.getAdvancements().get(resourcelocation);
            this.advancements.setSelectedTab(advancement, false);
        }

    }

    public void handleCommands(ClientboundCommandsPacket p_104990_) {
        PacketUtils.ensureRunningOnSameThread(p_104990_, this, (BlockableEventLoop)this.minecraft);
        CommandBuildContext context = CommandBuildContext.simple(this.registryAccess.compositeAccess(), this.enabledFeatures);
        this.commands = new CommandDispatcher(p_104990_.getRoot(context));
        this.commands = ClientCommandHandler.mergeServerCommands(this.commands, context);
    }

    public void handleStopSoundEvent(ClientboundStopSoundPacket p_105116_) {
        PacketUtils.ensureRunningOnSameThread(p_105116_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.getSoundManager().stop(p_105116_.getName(), p_105116_.getSource());
    }

    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket p_104988_) {
        PacketUtils.ensureRunningOnSameThread(p_104988_, this, (BlockableEventLoop)this.minecraft);
        this.suggestionsProvider.completeCustomSuggestions(p_104988_.getId(), p_104988_.getSuggestions());
    }

    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket p_105132_) {
        PacketUtils.ensureRunningOnSameThread(p_105132_, this, (BlockableEventLoop)this.minecraft);
        this.recipeManager.replaceRecipes(p_105132_.getRecipes());
        ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();
        clientrecipebook.setupCollections(this.recipeManager.getRecipes(), this.minecraft.level.registryAccess());
        this.minecraft.populateSearchTree(SearchRegistry.RECIPE_COLLECTIONS, clientrecipebook.getCollections());
        ForgeHooksClient.onRecipesUpdated(this.recipeManager);
    }

    public void handleLookAt(ClientboundPlayerLookAtPacket p_105054_) {
        PacketUtils.ensureRunningOnSameThread(p_105054_, this, (BlockableEventLoop)this.minecraft);
        Vec3 vec3 = p_105054_.getPosition(this.level);
        if (vec3 != null) {
            this.minecraft.player.lookAt(p_105054_.getFromAnchor(), vec3);
        }

    }

    public void handleTagQueryPacket(ClientboundTagQueryPacket p_105120_) {
        PacketUtils.ensureRunningOnSameThread(p_105120_, this, (BlockableEventLoop)this.minecraft);
        if (!this.debugQueryHandler.handleResponse(p_105120_.getTransactionId(), p_105120_.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", p_105120_.getTransactionId());
        }

    }

    public void handleAwardStats(ClientboundAwardStatsPacket p_104970_) {
        PacketUtils.ensureRunningOnSameThread(p_104970_, this, (BlockableEventLoop)this.minecraft);
        Iterator var2 = p_104970_.getStats().entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<Stat<?>, Integer> entry = (Map.Entry)var2.next();
            Stat<?> stat = (Stat)entry.getKey();
            int i = (Integer)entry.getValue();
            this.minecraft.player.getStats().setValue(this.minecraft.player, stat, i);
        }

        if (this.minecraft.screen instanceof StatsUpdateListener) {
            ((StatsUpdateListener)this.minecraft.screen).onStatsUpdated();
        }

    }

    public void handleAddOrRemoveRecipes(ClientboundRecipePacket p_105058_) {
        ClientRecipeBook clientrecipebook;
        PacketUtils.ensureRunningOnSameThread(p_105058_, this, (BlockableEventLoop)this.minecraft);
        clientrecipebook = this.minecraft.player.getRecipeBook();
        clientrecipebook.setBookSettings(p_105058_.getBookSettings());
        ClientboundRecipePacket.State clientboundrecipepacket$state = p_105058_.getState();
        Optional var10000;
        Iterator var4;
        ResourceLocation resourcelocation2;
        label45:
        switch (clientboundrecipepacket$state) {
            case REMOVE:
                var4 = p_105058_.getRecipes().iterator();

                while(true) {
                    if (!var4.hasNext()) {
                        break label45;
                    }

                    resourcelocation2 = (ResourceLocation)var4.next();
                    var10000 = this.recipeManager.byKey(resourcelocation2);
                    Objects.requireNonNull(clientrecipebook);
                    var10000.ifPresent(clientrecipebook::remove);
                }
            case INIT:
                var4 = p_105058_.getRecipes().iterator();

                while(var4.hasNext()) {
                    resourcelocation2 = (ResourceLocation)var4.next();
                    var10000 = this.recipeManager.byKey(resourcelocation2);
                    Objects.requireNonNull(clientrecipebook);
                    var10000.ifPresent(clientrecipebook::add);
                }

                var4 = p_105058_.getHighlights().iterator();

                while(true) {
                    if (!var4.hasNext()) {
                        break label45;
                    }

                    resourcelocation2 = (ResourceLocation)var4.next();
                    var10000 = this.recipeManager.byKey(resourcelocation2);
                    Objects.requireNonNull(clientrecipebook);
                    var10000.ifPresent(clientrecipebook::addHighlight);
                }
            case ADD:
                var4 = p_105058_.getRecipes().iterator();

                while(var4.hasNext()) {
                    resourcelocation2 = (ResourceLocation)var4.next();
                    this.recipeManager.byKey(resourcelocation2).ifPresent((p_272314_) -> {
                        clientrecipebook.add(p_272314_);
                        clientrecipebook.addHighlight(p_272314_);
                        if (p_272314_.showNotification()) {
                            RecipeToast.addOrUpdate(this.minecraft.getToasts(), p_272314_);
                        }

                    });
                }
        }

        clientrecipebook.getCollections().forEach((p_205540_) -> {
            p_205540_.updateKnownRecipes(clientrecipebook);
        });
        if (this.minecraft.screen instanceof RecipeUpdateListener) {
            ((RecipeUpdateListener)this.minecraft.screen).recipesUpdated();
        }

    }

    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket p_105130_) {
        PacketUtils.ensureRunningOnSameThread(p_105130_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105130_.getEntityId());
        if (entity instanceof LivingEntity) {
            MobEffect mobeffect = p_105130_.getEffect();
            if (mobeffect != null) {
                MobEffectInstance mobeffectinstance = new MobEffectInstance(mobeffect, p_105130_.getEffectDurationTicks(), p_105130_.getEffectAmplifier(), p_105130_.isEffectAmbient(), p_105130_.isEffectVisible(), p_105130_.effectShowsIcon(), (MobEffectInstance)null, Optional.ofNullable(p_105130_.getFactorData()));
                ((LivingEntity)entity).forceAddEffect(mobeffectinstance, (Entity)null);
            }
        }

    }

    public void handleUpdateTags(ClientboundUpdateTagsPacket p_105134_) {
        PacketUtils.ensureRunningOnSameThread(p_105134_, this, (BlockableEventLoop)this.minecraft);
        p_105134_.getTags().forEach(this::updateTagsForRegistry);
        if (!this.connection.isMemoryConnection()) {
            Blocks.rebuildCache();
        }

        CreativeModeTabs.allTabs().stream().filter(CreativeModeTab::hasSearchBar).forEach(CreativeModeTab::rebuildSearchTree);
        MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent(this.registryAccess.compositeAccess(), true, this.connection.isMemoryConnection()));
    }

    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket p_251591_) {
        PacketUtils.ensureRunningOnSameThread(p_251591_, this, (BlockableEventLoop)this.minecraft);
        this.enabledFeatures = FeatureFlags.REGISTRY.fromNames(p_251591_.features());
    }

    private <T> void updateTagsForRegistry(ResourceKey<? extends Registry<? extends T>> p_205561_, TagNetworkSerialization.NetworkPayload p_205562_) {
        if (!p_205562_.isEmpty()) {
            Registry<T> registry = (Registry)this.registryAccess.compositeAccess().registry(p_205561_).orElseThrow(() -> {
                return new IllegalStateException("Unknown registry " + p_205561_);
            });
            Map<TagKey<T>, List<Holder<T>>> map = new HashMap();
            Objects.requireNonNull(map);
            TagNetworkSerialization.deserializeTagsFromNetwork(p_205561_, registry, p_205562_, map::put);
            registry.bindTags(map);
        }

    }

    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket p_171771_) {
    }

    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket p_171773_) {
    }

    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket p_171775_) {
        PacketUtils.ensureRunningOnSameThread(p_171775_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_171775_.getPlayerId());
        if (entity == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.setScreen(new DeathScreen(p_171775_.getMessage(), this.level.getLevelData().isHardcore()));
            } else {
                this.minecraft.player.respawn();
            }
        }

    }

    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket p_104984_) {
        PacketUtils.ensureRunningOnSameThread(p_104984_, this, (BlockableEventLoop)this.minecraft);
        this.levelData.setDifficulty(p_104984_.getDifficulty());
        this.levelData.setDifficultyLocked(p_104984_.isLocked());
    }

    public void handleSetCamera(ClientboundSetCameraPacket p_105076_) {
        PacketUtils.ensureRunningOnSameThread(p_105076_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = p_105076_.getEntity(this.level);
        if (entity != null) {
            this.minecraft.setCameraEntity(entity);
        }

    }

    public void handleInitializeBorder(ClientboundInitializeBorderPacket p_171767_) {
        PacketUtils.ensureRunningOnSameThread(p_171767_, this, (BlockableEventLoop)this.minecraft);
        WorldBorder worldborder = this.level.getWorldBorder();
        worldborder.setCenter(p_171767_.getNewCenterX(), p_171767_.getNewCenterZ());
        long i = p_171767_.getLerpTime();
        if (i > 0L) {
            worldborder.lerpSizeBetween(p_171767_.getOldSize(), p_171767_.getNewSize(), i);
        } else {
            worldborder.setSize(p_171767_.getNewSize());
        }

        worldborder.setAbsoluteMaxSize(p_171767_.getNewAbsoluteMaxSize());
        worldborder.setWarningBlocks(p_171767_.getWarningBlocks());
        worldborder.setWarningTime(p_171767_.getWarningTime());
    }

    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket p_171781_) {
        PacketUtils.ensureRunningOnSameThread(p_171781_, this, (BlockableEventLoop)this.minecraft);
        this.level.getWorldBorder().setCenter(p_171781_.getNewCenterX(), p_171781_.getNewCenterZ());
    }

    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket p_171783_) {
        PacketUtils.ensureRunningOnSameThread(p_171783_, this, (BlockableEventLoop)this.minecraft);
        this.level.getWorldBorder().lerpSizeBetween(p_171783_.getOldSize(), p_171783_.getNewSize(), p_171783_.getLerpTime());
    }

    public void handleSetBorderSize(ClientboundSetBorderSizePacket p_171785_) {
        PacketUtils.ensureRunningOnSameThread(p_171785_, this, (BlockableEventLoop)this.minecraft);
        this.level.getWorldBorder().setSize(p_171785_.getSize());
    }

    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket p_171789_) {
        PacketUtils.ensureRunningOnSameThread(p_171789_, this, (BlockableEventLoop)this.minecraft);
        this.level.getWorldBorder().setWarningBlocks(p_171789_.getWarningBlocks());
    }

    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket p_171787_) {
        PacketUtils.ensureRunningOnSameThread(p_171787_, this, (BlockableEventLoop)this.minecraft);
        this.level.getWorldBorder().setWarningTime(p_171787_.getWarningDelay());
    }

    public void handleTitlesClear(ClientboundClearTitlesPacket p_171765_) {
        PacketUtils.ensureRunningOnSameThread(p_171765_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.clear();
        if (p_171765_.shouldResetTimes()) {
            this.minecraft.gui.resetTitleTimes();
        }

    }

    public void handleServerData(ClientboundServerDataPacket p_233704_) {
        PacketUtils.ensureRunningOnSameThread(p_233704_, this, (BlockableEventLoop)this.minecraft);
        if (this.serverData != null) {
            this.serverData.motd = p_233704_.getMotd();
            Optional var10000 = p_233704_.getIconBytes();
            ServerData var10001 = this.serverData;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::setIconBytes);
            this.serverData.setEnforcesSecureChat(p_233704_.enforcesSecureChat());
            ServerList.saveSingleServer(this.serverData);
            if (!p_233704_.enforcesSecureChat()) {
                SystemToast systemtoast = SystemToast.multiline(this.minecraft, SystemToastIds.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST);
                this.minecraft.getToasts().addToast(systemtoast);
            }
        }

    }

    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket p_240832_) {
        PacketUtils.ensureRunningOnSameThread(p_240832_, this, (BlockableEventLoop)this.minecraft);
        this.suggestionsProvider.modifyCustomCompletions(p_240832_.action(), p_240832_.entries());
    }

    public void setActionBarText(ClientboundSetActionBarTextPacket p_171779_) {
        PacketUtils.ensureRunningOnSameThread(p_171779_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.setOverlayMessage(p_171779_.getText(), false);
    }

    public void setTitleText(ClientboundSetTitleTextPacket p_171793_) {
        PacketUtils.ensureRunningOnSameThread(p_171793_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.setTitle(p_171793_.getText());
    }

    public void setSubtitleText(ClientboundSetSubtitleTextPacket p_171791_) {
        PacketUtils.ensureRunningOnSameThread(p_171791_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.setSubtitle(p_171791_.getText());
    }

    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket p_171795_) {
        PacketUtils.ensureRunningOnSameThread(p_171795_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.setTimes(p_171795_.getFadeIn(), p_171795_.getStay(), p_171795_.getFadeOut());
    }

    public void handleTabListCustomisation(ClientboundTabListPacket p_105118_) {
        PacketUtils.ensureRunningOnSameThread(p_105118_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.getTabList().setHeader(p_105118_.getHeader().getString().isEmpty() ? null : p_105118_.getHeader());
        this.minecraft.gui.getTabList().setFooter(p_105118_.getFooter().getString().isEmpty() ? null : p_105118_.getFooter());
    }

    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket p_105062_) {
        PacketUtils.ensureRunningOnSameThread(p_105062_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = p_105062_.getEntity(this.level);
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).removeEffectNoUpdate(p_105062_.getEffect());
        }

    }

    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket p_248731_) {
        PacketUtils.ensureRunningOnSameThread(p_248731_, this, (BlockableEventLoop)this.minecraft);
        Iterator var2 = p_248731_.profileIds().iterator();

        while(var2.hasNext()) {
            UUID uuid = (UUID)var2.next();
            this.minecraft.getPlayerSocialManager().removePlayer(uuid);
            PlayerInfo playerinfo = (PlayerInfo)this.playerInfoMap.remove(uuid);
            if (playerinfo != null) {
                this.listedPlayers.remove(playerinfo);
            }
        }

    }

    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket p_250115_) {
        PacketUtils.ensureRunningOnSameThread(p_250115_, this, (BlockableEventLoop)this.minecraft);
        Iterator var2 = p_250115_.newEntries().iterator();

        ClientboundPlayerInfoUpdatePacket.Entry clientboundplayerinfoupdatepacket$entry1;
        PlayerInfo playerinfo1;
        while(var2.hasNext()) {
            clientboundplayerinfoupdatepacket$entry1 = (ClientboundPlayerInfoUpdatePacket.Entry)var2.next();
            playerinfo1 = new PlayerInfo(clientboundplayerinfoupdatepacket$entry1.profile(), this.enforcesSecureChat());
            if (this.playerInfoMap.putIfAbsent(clientboundplayerinfoupdatepacket$entry1.profileId(), playerinfo1) == null) {
                this.minecraft.getPlayerSocialManager().addPlayer(playerinfo1);
            }
        }

        var2 = p_250115_.entries().iterator();

        while(true) {
            while(var2.hasNext()) {
                clientboundplayerinfoupdatepacket$entry1 = (ClientboundPlayerInfoUpdatePacket.Entry)var2.next();
                playerinfo1 = (PlayerInfo)this.playerInfoMap.get(clientboundplayerinfoupdatepacket$entry1.profileId());
                if (playerinfo1 == null) {
                    LOGGER.warn("Ignoring player info update for unknown player {}", clientboundplayerinfoupdatepacket$entry1.profileId());
                } else {
                    Iterator var5 = p_250115_.actions().iterator();

                    while(var5.hasNext()) {
                        ClientboundPlayerInfoUpdatePacket.Action clientboundplayerinfoupdatepacket$action = (ClientboundPlayerInfoUpdatePacket.Action)var5.next();
                        this.applyPlayerInfoUpdate(clientboundplayerinfoupdatepacket$action, clientboundplayerinfoupdatepacket$entry1, playerinfo1);
                    }
                }
            }

            return;
        }
    }

    private void applyPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket.Action p_248954_, ClientboundPlayerInfoUpdatePacket.Entry p_251310_, PlayerInfo p_251146_) {
        switch (p_248954_) {
            case INITIALIZE_CHAT:
                this.initializeChatSession(p_251310_, p_251146_);
                break;
            case UPDATE_GAME_MODE:
                if (p_251146_.getGameMode() != p_251310_.gameMode() && this.minecraft.player != null && this.minecraft.player.getUUID().equals(p_251310_.profileId())) {
                    this.minecraft.player.onGameModeChanged(p_251310_.gameMode());
                }

                p_251146_.setGameMode(p_251310_.gameMode());
                break;
            case UPDATE_LISTED:
                if (p_251310_.listed()) {
                    this.listedPlayers.add(p_251146_);
                } else {
                    this.listedPlayers.remove(p_251146_);
                }
                break;
            case UPDATE_LATENCY:
                p_251146_.setLatency(p_251310_.latency());
                break;
            case UPDATE_DISPLAY_NAME:
                p_251146_.setTabListDisplayName(p_251310_.displayName());
        }

    }

    private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry p_248806_, PlayerInfo p_251136_) {
        GameProfile gameprofile = p_251136_.getProfile();
        SignatureValidator signaturevalidator = this.minecraft.getProfileKeySignatureValidator();
        if (signaturevalidator == null) {
            LOGGER.warn("Ignoring chat session from {} due to missing Services public key", gameprofile.getName());
            p_251136_.clearChatSession(this.enforcesSecureChat());
        } else {
            RemoteChatSession.Data remotechatsession$data = p_248806_.chatSession();
            if (remotechatsession$data != null) {
                try {
                    RemoteChatSession remotechatsession = remotechatsession$data.validate(gameprofile, signaturevalidator, ProfilePublicKey.EXPIRY_GRACE_PERIOD);
                    p_251136_.setChatSession(remotechatsession);
                } catch (ProfilePublicKey.ValidationException var7) {
                    ProfilePublicKey.ValidationException profilepublickey$validationexception = var7;
                    LOGGER.error("Failed to validate profile key for player: '{}'", gameprofile.getName(), profilepublickey$validationexception);
                    p_251136_.clearChatSession(this.enforcesSecureChat());
                }
            } else {
                p_251136_.clearChatSession(this.enforcesSecureChat());
            }
        }

    }

    private boolean enforcesSecureChat() {
        return this.serverData != null && this.serverData.enforcesSecureChat();
    }

    public void handleKeepAlive(ClientboundKeepAlivePacket p_105020_) {
        this.sendWhen(new ServerboundKeepAlivePacket(p_105020_.getId()), () -> {
            return !RenderSystem.isFrozenAtPollEvents();
        }, Duration.ofMinutes(1L));
    }

    private void sendWhen(Packet<ServerGamePacketListener> p_270433_, BooleanSupplier p_270843_, Duration p_270497_) {
        if (p_270843_.getAsBoolean()) {
            this.send(p_270433_);
        } else {
            this.deferredPackets.add(new DeferredPacket(p_270433_, p_270843_, Util.getMillis() + p_270497_.toMillis()));
        }

    }

    private void sendDeferredPackets() {
        Iterator<DeferredPacket> iterator = this.deferredPackets.iterator();

        while(iterator.hasNext()) {
            DeferredPacket clientpacketlistener$deferredpacket = (DeferredPacket)iterator.next();
            if (clientpacketlistener$deferredpacket.sendCondition().getAsBoolean()) {
                this.send(clientpacketlistener$deferredpacket.packet);
                iterator.remove();
            } else if (clientpacketlistener$deferredpacket.expirationTime() <= Util.getMillis()) {
                iterator.remove();
            }
        }

    }

    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket p_105048_) {
        PacketUtils.ensureRunningOnSameThread(p_105048_, this, (BlockableEventLoop)this.minecraft);
        Player player = this.minecraft.player;
        player.getAbilities().flying = p_105048_.isFlying();
        player.getAbilities().instabuild = p_105048_.canInstabuild();
        player.getAbilities().invulnerable = p_105048_.isInvulnerable();
        player.getAbilities().mayfly = p_105048_.canFly();
        player.getAbilities().setFlyingSpeed(p_105048_.getFlyingSpeed());
        player.getAbilities().setWalkingSpeed(p_105048_.getWalkingSpeed());
    }

    public void handleSoundEvent(ClientboundSoundPacket p_105114_) {
        PacketUtils.ensureRunningOnSameThread(p_105114_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.level.playSeededSound(this.minecraft.player, p_105114_.getX(), p_105114_.getY(), p_105114_.getZ(), p_105114_.getSound(), p_105114_.getSource(), p_105114_.getVolume(), p_105114_.getPitch(), p_105114_.getSeed());
    }

    public void handleSoundEntityEvent(ClientboundSoundEntityPacket p_105112_) {
        PacketUtils.ensureRunningOnSameThread(p_105112_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105112_.getId());
        if (entity != null) {
            this.minecraft.level.playSeededSound(this.minecraft.player, entity, p_105112_.getSound(), p_105112_.getSource(), p_105112_.getVolume(), p_105112_.getPitch(), p_105112_.getSeed());
        }

    }

    public void handleResourcePack(ClientboundResourcePackPacket p_105064_) {
        URL url = parseResourcePackUrl(p_105064_.getUrl());
        if (url == null) {
            this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
        } else {
            String s = p_105064_.getHash();
            boolean flag = p_105064_.isRequired();
            if (this.serverData != null && this.serverData.getResourcePackStatus() == ServerPackStatus.ENABLED) {
                this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.ACCEPTED);
                this.downloadCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack(url, s, true));
            } else if (this.serverData == null || this.serverData.getResourcePackStatus() == ServerPackStatus.PROMPT || flag && this.serverData.getResourcePackStatus() == ServerPackStatus.DISABLED) {
                this.minecraft.execute(() -> {
                    this.minecraft.setScreen(new ConfirmScreen((p_233690_) -> {
                        this.minecraft.setScreen((Screen)null);
                        if (p_233690_) {
                            if (this.serverData != null) {
                                this.serverData.setResourcePackStatus(ServerPackStatus.ENABLED);
                            }

                            this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.ACCEPTED);
                            this.downloadCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack(url, s, true));
                        } else {
                            this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.DECLINED);
                            if (flag) {
                                this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                            } else if (this.serverData != null) {
                                this.serverData.setResourcePackStatus(ServerPackStatus.DISABLED);
                            }
                        }

                        if (this.serverData != null) {
                            ServerList.saveSingleServer(this.serverData);
                        }

                    }, flag ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"), preparePackPrompt(flag ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD) : Component.translatable("multiplayer.texturePrompt.line2"), p_105064_.getPrompt()), flag ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES, (Component)(flag ? Component.translatable("menu.disconnect") : CommonComponents.GUI_NO)));
                });
            } else {
                this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.DECLINED);
                if (flag) {
                    this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                }
            }
        }

    }

    private static Component preparePackPrompt(Component p_171760_, @Nullable Component p_171761_) {
        return (Component)(p_171761_ == null ? p_171760_ : Component.translatable("multiplayer.texturePrompt.serverPrompt", p_171760_, p_171761_));
    }

    @Nullable
    private static URL parseResourcePackUrl(String p_233710_) {
        try {
            URL url = new URL(p_233710_);
            String s = url.getProtocol();
            return !"http".equals(s) && !"https".equals(s) ? null : url;
        } catch (MalformedURLException var3) {
            return null;
        }
    }

    private void downloadCallback(CompletableFuture<?> p_104952_) {
        p_104952_.thenRun(() -> {
            this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
        }).exceptionally((p_233680_) -> {
            this.send(net.minecraft.network.protocol.game.ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
            return null;
        });
    }

    private void send(ServerboundResourcePackPacket.Action p_105136_) {
        this.connection.send(new ServerboundResourcePackPacket(p_105136_));
    }

    public void handleBossUpdate(ClientboundBossEventPacket p_104982_) {
        PacketUtils.ensureRunningOnSameThread(p_104982_, this, (BlockableEventLoop)this.minecraft);
        this.minecraft.gui.getBossOverlay().update(p_104982_);
    }

    public void handleItemCooldown(ClientboundCooldownPacket p_105002_) {
        PacketUtils.ensureRunningOnSameThread(p_105002_, this, (BlockableEventLoop)this.minecraft);
        if (p_105002_.getDuration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown(p_105002_.getItem());
        } else {
            this.minecraft.player.getCooldowns().addCooldown(p_105002_.getItem(), p_105002_.getDuration());
        }

    }

    public void handleMoveVehicle(ClientboundMoveVehiclePacket p_105038_) {
        PacketUtils.ensureRunningOnSameThread(p_105038_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.minecraft.player.getRootVehicle();
        if (entity != this.minecraft.player && entity.isControlledByLocalInstance()) {
            entity.absMoveTo(p_105038_.getX(), p_105038_.getY(), p_105038_.getZ(), p_105038_.getYRot(), p_105038_.getXRot());
            this.connection.send(new ServerboundMoveVehiclePacket(entity));
        }

    }

    public void handleOpenBook(ClientboundOpenBookPacket p_105040_) {
        PacketUtils.ensureRunningOnSameThread(p_105040_, this, (BlockableEventLoop)this.minecraft);
        ItemStack itemstack = this.minecraft.player.getItemInHand(p_105040_.getHand());
        if (itemstack.is(Items.WRITTEN_BOOK)) {
            this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(itemstack)));
        }

    }

    public void handleCustomPayload(ClientboundCustomPayloadPacket p_105004_) {
        if (!NetworkHooks.onCustomPayload(p_105004_, this.connection)) {
            PacketUtils.ensureRunningOnSameThread(p_105004_, this, (BlockableEventLoop)this.minecraft);
            ResourceLocation resourcelocation = p_105004_.getIdentifier();
            FriendlyByteBuf friendlybytebuf = null;

            try {
                friendlybytebuf = p_105004_.getData();
                if (ClientboundCustomPayloadPacket.BRAND.equals(resourcelocation)) {
                    String s = friendlybytebuf.readUtf();
                    this.minecraft.player.setServerBrand(s);
                    this.telemetryManager.onServerBrandReceived(s);
                } else {
                    int j2;
                    if (ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals(resourcelocation)) {
                        j2 = friendlybytebuf.readInt();
                        float f = friendlybytebuf.readFloat();
                        Path path = Path.createFromStream(friendlybytebuf);
                        this.minecraft.debugRenderer.pathfindingRenderer.addPath(j2, path, f);
                    } else if (ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(resourcelocation)) {
                        long l1 = friendlybytebuf.readVarLong();
                        BlockPos blockpos8 = friendlybytebuf.readBlockPos();
                        ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(l1, blockpos8);
                    } else {
                        ArrayList list2;
                        int j6;
                        int j5;
                        if (ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(resourcelocation)) {
                            DimensionType dimensiontype = (DimensionType)this.registryAccess.compositeAccess().registryOrThrow(Registries.DIMENSION_TYPE).get(friendlybytebuf.readResourceLocation());
                            BoundingBox boundingbox = new BoundingBox(friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt());
                            j5 = friendlybytebuf.readInt();
                            list2 = Lists.newArrayList();
                            List<Boolean> list1 = Lists.newArrayList();

                            for(j6 = 0; j6 < j5; ++j6) {
                                list2.add(new BoundingBox(friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt(), friendlybytebuf.readInt()));
                                list1.add(friendlybytebuf.readBoolean());
                            }

                            this.minecraft.debugRenderer.structureRenderer.addBoundingBox(boundingbox, list2, list1, dimensiontype);
                        } else if (ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(resourcelocation)) {
                            ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(friendlybytebuf.readBlockPos(), friendlybytebuf.readFloat(), friendlybytebuf.readFloat(), friendlybytebuf.readFloat(), friendlybytebuf.readFloat(), friendlybytebuf.readFloat());
                        } else {
                            int k3;
                            if (ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(resourcelocation)) {
                                j2 = friendlybytebuf.readInt();

                                for(k3 = 0; k3 < j2; ++k3) {
                                    this.minecraft.debugRenderer.villageSectionsDebugRenderer.setVillageSection(friendlybytebuf.readSectionPos());
                                }

                                k3 = friendlybytebuf.readInt();

                                for(j5 = 0; j5 < k3; ++j5) {
                                    this.minecraft.debugRenderer.villageSectionsDebugRenderer.setNotVillageSection(friendlybytebuf.readSectionPos());
                                }
                            } else {
                                BlockPos blockpos7;
                                String s10;
                                if (ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(resourcelocation)) {
                                    blockpos7 = friendlybytebuf.readBlockPos();
                                    s10 = friendlybytebuf.readUtf();
                                    j5 = friendlybytebuf.readInt();
                                    BrainDebugRenderer.PoiInfo braindebugrenderer$poiinfo = new BrainDebugRenderer.PoiInfo(blockpos7, s10, j5);
                                    this.minecraft.debugRenderer.brainDebugRenderer.addPoi(braindebugrenderer$poiinfo);
                                } else if (ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(resourcelocation)) {
                                    blockpos7 = friendlybytebuf.readBlockPos();
                                    this.minecraft.debugRenderer.brainDebugRenderer.removePoi(blockpos7);
                                } else if (ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(resourcelocation)) {
                                    blockpos7 = friendlybytebuf.readBlockPos();
                                    k3 = friendlybytebuf.readInt();
                                    this.minecraft.debugRenderer.brainDebugRenderer.setFreeTicketCount(blockpos7, k3);
                                } else if (ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(resourcelocation)) {
                                    blockpos7 = friendlybytebuf.readBlockPos();
                                    k3 = friendlybytebuf.readInt();
                                    j5 = friendlybytebuf.readInt();
                                    list2 = Lists.newArrayList();

                                    for(int i6 = 0; i6 < j5; ++i6) {
                                        j6 = friendlybytebuf.readInt();
                                        boolean flag = friendlybytebuf.readBoolean();
                                        String s1 = friendlybytebuf.readUtf(255);
                                        list2.add(new GoalSelectorDebugRenderer.DebugGoal(blockpos7, j6, s1, flag));
                                    }

                                    this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(k3, list2);
                                } else if (ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(resourcelocation)) {
                                    j2 = friendlybytebuf.readInt();
                                    Collection<BlockPos> collection = Lists.newArrayList();

                                    for(j5 = 0; j5 < j2; ++j5) {
                                        collection.add(friendlybytebuf.readBlockPos());
                                    }

                                    this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(collection);
                                } else {
                                    int k6;
                                    int l6;
                                    double d1;
                                    double d3;
                                    double d5;
                                    PositionImpl position1;
                                    UUID uuid1;
                                    if (ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(resourcelocation)) {
                                        d1 = friendlybytebuf.readDouble();
                                        d3 = friendlybytebuf.readDouble();
                                        d5 = friendlybytebuf.readDouble();
                                        position1 = new PositionImpl(d1, d3, d5);
                                        uuid1 = friendlybytebuf.readUUID();
                                        k6 = friendlybytebuf.readInt();
                                        String s2 = friendlybytebuf.readUtf();
                                        String s3 = friendlybytebuf.readUtf();
                                        l6 = friendlybytebuf.readInt();
                                        float f1 = friendlybytebuf.readFloat();
                                        float f2 = friendlybytebuf.readFloat();
                                        String s4 = friendlybytebuf.readUtf();
                                        Path path1 = (Path)friendlybytebuf.readNullable(Path::createFromStream);
                                        boolean flag1 = friendlybytebuf.readBoolean();
                                        int l = friendlybytebuf.readInt();
                                        BrainDebugRenderer.BrainDump braindebugrenderer$braindump = new BrainDebugRenderer.BrainDump(uuid1, k6, s2, s3, l6, f1, f2, position1, s4, path1, flag1, l);
                                        int i1 = friendlybytebuf.readVarInt();

                                        int i8;
                                        for(i8 = 0; i8 < i1; ++i8) {
                                            String s5 = friendlybytebuf.readUtf();
                                            braindebugrenderer$braindump.activities.add(s5);
                                        }

                                        i8 = friendlybytebuf.readVarInt();

                                        int k8;
                                        for(k8 = 0; k8 < i8; ++k8) {
                                            String s6 = friendlybytebuf.readUtf();
                                            braindebugrenderer$braindump.behaviors.add(s6);
                                        }

                                        k8 = friendlybytebuf.readVarInt();

                                        int i9;
                                        for(i9 = 0; i9 < k8; ++i9) {
                                            String s7 = friendlybytebuf.readUtf();
                                            braindebugrenderer$braindump.memories.add(s7);
                                        }

                                        i9 = friendlybytebuf.readVarInt();

                                        int k9;
                                        for(k9 = 0; k9 < i9; ++k9) {
                                            BlockPos blockpos = friendlybytebuf.readBlockPos();
                                            braindebugrenderer$braindump.pois.add(blockpos);
                                        }

                                        k9 = friendlybytebuf.readVarInt();

                                        int i10;
                                        for(i10 = 0; i10 < k9; ++i10) {
                                            BlockPos blockpos1 = friendlybytebuf.readBlockPos();
                                            braindebugrenderer$braindump.potentialPois.add(blockpos1);
                                        }

                                        i10 = friendlybytebuf.readVarInt();

                                        for(int j10 = 0; j10 < i10; ++j10) {
                                            String s8 = friendlybytebuf.readUtf();
                                            braindebugrenderer$braindump.gossips.add(s8);
                                        }

                                        this.minecraft.debugRenderer.brainDebugRenderer.addOrUpdateBrainDump(braindebugrenderer$braindump);
                                    } else if (ClientboundCustomPayloadPacket.DEBUG_BEE.equals(resourcelocation)) {
                                        d1 = friendlybytebuf.readDouble();
                                        d3 = friendlybytebuf.readDouble();
                                        d5 = friendlybytebuf.readDouble();
                                        position1 = new PositionImpl(d1, d3, d5);
                                        uuid1 = friendlybytebuf.readUUID();
                                        k6 = friendlybytebuf.readInt();
                                        BlockPos blockpos9 = (BlockPos)friendlybytebuf.readNullable(FriendlyByteBuf::readBlockPos);
                                        BlockPos blockpos10 = (BlockPos)friendlybytebuf.readNullable(FriendlyByteBuf::readBlockPos);
                                        l6 = friendlybytebuf.readInt();
                                        Path path2 = (Path)friendlybytebuf.readNullable(Path::createFromStream);
                                        BeeDebugRenderer.BeeInfo beedebugrenderer$beeinfo = new BeeDebugRenderer.BeeInfo(uuid1, k6, position1, path2, blockpos9, blockpos10, l6);
                                        int i7 = friendlybytebuf.readVarInt();

                                        int k7;
                                        for(k7 = 0; k7 < i7; ++k7) {
                                            String s12 = friendlybytebuf.readUtf();
                                            beedebugrenderer$beeinfo.goals.add(s12);
                                        }

                                        k7 = friendlybytebuf.readVarInt();

                                        for(int l7 = 0; l7 < k7; ++l7) {
                                            BlockPos blockpos11 = friendlybytebuf.readBlockPos();
                                            beedebugrenderer$beeinfo.blacklistedHives.add(blockpos11);
                                        }

                                        this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateBeeInfo(beedebugrenderer$beeinfo);
                                    } else {
                                        int l5;
                                        if (ClientboundCustomPayloadPacket.DEBUG_HIVE.equals(resourcelocation)) {
                                            blockpos7 = friendlybytebuf.readBlockPos();
                                            s10 = friendlybytebuf.readUtf();
                                            j5 = friendlybytebuf.readInt();
                                            l5 = friendlybytebuf.readInt();
                                            boolean flag2 = friendlybytebuf.readBoolean();
                                            BeeDebugRenderer.HiveInfo beedebugrenderer$hiveinfo = new BeeDebugRenderer.HiveInfo(blockpos7, s10, j5, l5, flag2, this.level.getGameTime());
                                            this.minecraft.debugRenderer.beeDebugRenderer.addOrUpdateHiveInfo(beedebugrenderer$hiveinfo);
                                        } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_CLEAR.equals(resourcelocation)) {
                                            this.minecraft.debugRenderer.gameTestDebugRenderer.clear();
                                        } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(resourcelocation)) {
                                            blockpos7 = friendlybytebuf.readBlockPos();
                                            k3 = friendlybytebuf.readInt();
                                            String s11 = friendlybytebuf.readUtf();
                                            l5 = friendlybytebuf.readInt();
                                            this.minecraft.debugRenderer.gameTestDebugRenderer.addMarker(blockpos7, k3, s11, l5);
                                        } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT.equals(resourcelocation)) {
                                            GameEvent gameevent = (GameEvent)BuiltInRegistries.GAME_EVENT.get(new ResourceLocation(friendlybytebuf.readUtf()));
                                            Vec3 vec3 = new Vec3(friendlybytebuf.readDouble(), friendlybytebuf.readDouble(), friendlybytebuf.readDouble());
                                            this.minecraft.debugRenderer.gameEventListenerRenderer.trackGameEvent(gameevent, vec3);
                                        } else if (ClientboundCustomPayloadPacket.DEBUG_GAME_EVENT_LISTENER.equals(resourcelocation)) {
                                            ResourceLocation resourcelocation1 = friendlybytebuf.readResourceLocation();
                                            PositionSource positionsource = ((PositionSourceType)BuiltInRegistries.POSITION_SOURCE_TYPE.getOptional(resourcelocation1).orElseThrow(() -> {
                                                return new IllegalArgumentException("Unknown position source type " + resourcelocation1);
                                            })).read(friendlybytebuf);
                                            j5 = friendlybytebuf.readVarInt();
                                            this.minecraft.debugRenderer.gameEventListenerRenderer.trackListener(positionsource, j5);
                                        } else {
                                            LOGGER.warn("Unknown custom packet identifier: {}", resourcelocation);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } finally {
                if (friendlybytebuf != null) {
                    friendlybytebuf.release();
                }

            }

        }
    }

    public void handleAddObjective(ClientboundSetObjectivePacket p_105100_) {
        PacketUtils.ensureRunningOnSameThread(p_105100_, this, (BlockableEventLoop)this.minecraft);
        Scoreboard scoreboard = this.level.getScoreboard();
        String s = p_105100_.getObjectiveName();
        if (p_105100_.getMethod() == 0) {
            scoreboard.addObjective(s, ObjectiveCriteria.DUMMY, p_105100_.getDisplayName(), p_105100_.getRenderType());
        } else if (scoreboard.hasObjective(s)) {
            Objective objective = scoreboard.getObjective(s);
            if (p_105100_.getMethod() == 1) {
                scoreboard.removeObjective(objective);
            } else if (p_105100_.getMethod() == 2) {
                objective.setRenderType(p_105100_.getRenderType());
                objective.setDisplayName(p_105100_.getDisplayName());
            }
        }

    }

    public void handleSetScore(ClientboundSetScorePacket p_105106_) {
        PacketUtils.ensureRunningOnSameThread(p_105106_, this, (BlockableEventLoop)this.minecraft);
        Scoreboard scoreboard = this.level.getScoreboard();
        String s = p_105106_.getObjectiveName();
        switch (p_105106_.getMethod()) {
            case CHANGE:
                Objective objective = scoreboard.getOrCreateObjective(s);
                Score score = scoreboard.getOrCreatePlayerScore(p_105106_.getOwner(), objective);
                score.setScore(p_105106_.getScore());
                break;
            case REMOVE:
                scoreboard.resetPlayerScore(p_105106_.getOwner(), scoreboard.getObjective(s));
        }

    }

    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket p_105086_) {
        PacketUtils.ensureRunningOnSameThread(p_105086_, this, (BlockableEventLoop)this.minecraft);
        Scoreboard scoreboard = this.level.getScoreboard();
        String s = p_105086_.getObjectiveName();
        Objective objective = s == null ? null : scoreboard.getOrCreateObjective(s);
        scoreboard.setDisplayObjective(p_105086_.getSlot(), objective);
    }

    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket p_105104_) {
        PacketUtils.ensureRunningOnSameThread(p_105104_, this, (BlockableEventLoop)this.minecraft);
        Scoreboard scoreboard = this.level.getScoreboard();
        ClientboundSetPlayerTeamPacket.Action clientboundsetplayerteampacket$action = p_105104_.getTeamAction();
        PlayerTeam playerteam;
        if (clientboundsetplayerteampacket$action == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
            playerteam = scoreboard.addPlayerTeam(p_105104_.getName());
        } else {
            playerteam = scoreboard.getPlayerTeam(p_105104_.getName());
            if (playerteam == null) {
                LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{p_105104_.getName(), p_105104_.getTeamAction(), p_105104_.getPlayerAction()});
                return;
            }
        }

        Optional<ClientboundSetPlayerTeamPacket.Parameters> optional = p_105104_.getParameters();
        optional.ifPresent((p_233670_) -> {
            playerteam.setDisplayName(p_233670_.getDisplayName());
            playerteam.setColor(p_233670_.getColor());
            playerteam.unpackOptions(p_233670_.getOptions());
            Team.Visibility team$visibility = Visibility.byName(p_233670_.getNametagVisibility());
            if (team$visibility != null) {
                playerteam.setNameTagVisibility(team$visibility);
            }

            Team.CollisionRule team$collisionrule = CollisionRule.byName(p_233670_.getCollisionRule());
            if (team$collisionrule != null) {
                playerteam.setCollisionRule(team$collisionrule);
            }

            playerteam.setPlayerPrefix(p_233670_.getPlayerPrefix());
            playerteam.setPlayerSuffix(p_233670_.getPlayerSuffix());
        });
        ClientboundSetPlayerTeamPacket.Action clientboundsetplayerteampacket$action1 = p_105104_.getPlayerAction();
        Iterator var7;
        String s1;
        if (clientboundsetplayerteampacket$action1 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
            var7 = p_105104_.getPlayers().iterator();

            while(var7.hasNext()) {
                s1 = (String)var7.next();
                scoreboard.addPlayerToTeam(s1, playerteam);
            }
        } else if (clientboundsetplayerteampacket$action1 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            var7 = p_105104_.getPlayers().iterator();

            while(var7.hasNext()) {
                s1 = (String)var7.next();
                scoreboard.removePlayerFromTeam(s1, playerteam);
            }
        }

        if (clientboundsetplayerteampacket$action == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            scoreboard.removePlayerTeam(playerteam);
        }

    }

    public void handleParticleEvent(ClientboundLevelParticlesPacket p_105026_) {
        PacketUtils.ensureRunningOnSameThread(p_105026_, this, (BlockableEventLoop)this.minecraft);
        if (p_105026_.getCount() == 0) {
            double d0 = (double)(p_105026_.getMaxSpeed() * p_105026_.getXDist());
            double d2 = (double)(p_105026_.getMaxSpeed() * p_105026_.getYDist());
            double d4 = (double)(p_105026_.getMaxSpeed() * p_105026_.getZDist());

            try {
                this.level.addParticle(p_105026_.getParticle(), p_105026_.isOverrideLimiter(), p_105026_.getX(), p_105026_.getY(), p_105026_.getZ(), d0, d2, d4);
            } catch (Throwable var17) {
                LOGGER.warn("Could not spawn particle effect {}", p_105026_.getParticle());
            }
        } else {
            for(int i = 0; i < p_105026_.getCount(); ++i) {
                double d1 = this.random.nextGaussian() * (double)p_105026_.getXDist();
                double d3 = this.random.nextGaussian() * (double)p_105026_.getYDist();
                double d5 = this.random.nextGaussian() * (double)p_105026_.getZDist();
                double d6 = this.random.nextGaussian() * (double)p_105026_.getMaxSpeed();
                double d7 = this.random.nextGaussian() * (double)p_105026_.getMaxSpeed();
                double d8 = this.random.nextGaussian() * (double)p_105026_.getMaxSpeed();

                try {
                    this.level.addParticle(p_105026_.getParticle(), p_105026_.isOverrideLimiter(), p_105026_.getX() + d1, p_105026_.getY() + d3, p_105026_.getZ() + d5, d6, d7, d8);
                } catch (Throwable var16) {
                    LOGGER.warn("Could not spawn particle effect {}", p_105026_.getParticle());
                    return;
                }
            }
        }

    }

    public void handlePing(ClientboundPingPacket p_171769_) {
        PacketUtils.ensureRunningOnSameThread(p_171769_, this, (BlockableEventLoop)this.minecraft);
        this.send((Packet)(new ServerboundPongPacket(p_171769_.getId())));
    }

    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket p_105128_) {
        PacketUtils.ensureRunningOnSameThread(p_105128_, this, (BlockableEventLoop)this.minecraft);
        Entity entity = this.level.getEntity(p_105128_.getEntityId());
        if (entity != null) {
            if (!(entity instanceof LivingEntity)) {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            } else {
                AttributeMap attributemap = ((LivingEntity)entity).getAttributes();
                Iterator var4 = p_105128_.getValues().iterator();

                while(true) {
                    while(var4.hasNext()) {
                        ClientboundUpdateAttributesPacket.AttributeSnapshot clientboundupdateattributespacket$attributesnapshot = (ClientboundUpdateAttributesPacket.AttributeSnapshot)var4.next();
                        AttributeInstance attributeinstance = attributemap.getInstance(clientboundupdateattributespacket$attributesnapshot.getAttribute());
                        if (attributeinstance == null) {
                            LOGGER.warn("Entity {} does not have attribute {}", entity, BuiltInRegistries.ATTRIBUTE.getKey(clientboundupdateattributespacket$attributesnapshot.getAttribute()));
                        } else {
                            attributeinstance.setBaseValue(clientboundupdateattributespacket$attributesnapshot.getBase());
                            attributeinstance.removeModifiers();
                            Iterator var7 = clientboundupdateattributespacket$attributesnapshot.getModifiers().iterator();

                            while(var7.hasNext()) {
                                AttributeModifier attributemodifier = (AttributeModifier)var7.next();
                                attributeinstance.addTransientModifier(attributemodifier);
                            }
                        }
                    }

                    return;
                }
            }
        }
    }

    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket p_105046_) {
        PacketUtils.ensureRunningOnSameThread(p_105046_, this, (BlockableEventLoop)this.minecraft);
        AbstractContainerMenu abstractcontainermenu = this.minecraft.player.containerMenu;
        if (abstractcontainermenu.containerId == p_105046_.getContainerId()) {
            this.recipeManager.byKey(p_105046_.getRecipe()).ifPresent((p_233667_) -> {
                if (this.minecraft.screen instanceof RecipeUpdateListener) {
                    RecipeBookComponent recipebookcomponent = ((RecipeUpdateListener)this.minecraft.screen).getRecipeBookComponent();
                    recipebookcomponent.setupGhostRecipe(p_233667_, abstractcontainermenu.slots);
                }

            });
        }

    }

    public void handleLightUpdatePacket(ClientboundLightUpdatePacket p_194243_) {
        PacketUtils.ensureRunningOnSameThread(p_194243_, this, (BlockableEventLoop)this.minecraft);
        int i = p_194243_.getX();
        int j = p_194243_.getZ();
        ClientboundLightUpdatePacketData clientboundlightupdatepacketdata = p_194243_.getLightData();
        this.level.queueLightUpdate(() -> {
            this.applyLightData(i, j, clientboundlightupdatepacketdata);
        });
    }

    private void applyLightData(int p_194249_, int p_194250_, ClientboundLightUpdatePacketData p_194251_) {
        LevelLightEngine levellightengine = this.level.getChunkSource().getLightEngine();
        BitSet bitset = p_194251_.getSkyYMask();
        BitSet bitset1 = p_194251_.getEmptySkyYMask();
        Iterator<byte[]> iterator = p_194251_.getSkyUpdates().iterator();
        this.readSectionList(p_194249_, p_194250_, levellightengine, LightLayer.SKY, bitset, bitset1, iterator);
        BitSet bitset2 = p_194251_.getBlockYMask();
        BitSet bitset3 = p_194251_.getEmptyBlockYMask();
        Iterator<byte[]> iterator1 = p_194251_.getBlockUpdates().iterator();
        this.readSectionList(p_194249_, p_194250_, levellightengine, LightLayer.BLOCK, bitset2, bitset3, iterator1);
        levellightengine.setLightEnabled(new ChunkPos(p_194249_, p_194250_), true);
    }

    public void handleMerchantOffers(ClientboundMerchantOffersPacket p_105034_) {
        PacketUtils.ensureRunningOnSameThread(p_105034_, this, (BlockableEventLoop)this.minecraft);
        AbstractContainerMenu abstractcontainermenu = this.minecraft.player.containerMenu;
        if (p_105034_.getContainerId() == abstractcontainermenu.containerId && abstractcontainermenu instanceof MerchantMenu merchantmenu) {
            merchantmenu.setOffers(new MerchantOffers(p_105034_.getOffers().createTag()));
            merchantmenu.setXp(p_105034_.getVillagerXp());
            merchantmenu.setMerchantLevel(p_105034_.getVillagerLevel());
            merchantmenu.setShowProgressBar(p_105034_.showProgress());
            merchantmenu.setCanRestock(p_105034_.canRestock());
        }

    }

    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket p_105082_) {
        PacketUtils.ensureRunningOnSameThread(p_105082_, this, (BlockableEventLoop)this.minecraft);
        this.serverChunkRadius = p_105082_.getRadius();
        this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
        this.level.getChunkSource().updateViewRadius(p_105082_.getRadius());
    }

    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket p_194245_) {
        PacketUtils.ensureRunningOnSameThread(p_194245_, this, (BlockableEventLoop)this.minecraft);
        this.serverSimulationDistance = p_194245_.simulationDistance();
        this.level.setServerSimulationDistance(this.serverSimulationDistance);
    }

    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket p_105080_) {
        PacketUtils.ensureRunningOnSameThread(p_105080_, this, (BlockableEventLoop)this.minecraft);
        this.level.getChunkSource().updateViewCenter(p_105080_.getX(), p_105080_.getZ());
    }

    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket p_233698_) {
        PacketUtils.ensureRunningOnSameThread(p_233698_, this, (BlockableEventLoop)this.minecraft);
        this.level.handleBlockChangedAck(p_233698_.sequence());
    }

    public void handleBundlePacket(ClientboundBundlePacket p_265195_) {
        PacketUtils.ensureRunningOnSameThread(p_265195_, this, (BlockableEventLoop)this.minecraft);
        Iterator var2 = p_265195_.subPackets().iterator();

        while(var2.hasNext()) {
            Packet<ClientGamePacketListener> packet = (Packet)var2.next();
            packet.handle(this);
        }

    }

    private void readSectionList(int p_171735_, int p_171736_, LevelLightEngine p_171737_, LightLayer p_171738_, BitSet p_171739_, BitSet p_171740_, Iterator<byte[]> p_171741_) {
        for(int i = 0; i < p_171737_.getLightSectionCount(); ++i) {
            int j = p_171737_.getMinLightSection() + i;
            boolean flag = p_171739_.get(i);
            boolean flag1 = p_171740_.get(i);
            if (flag || flag1) {
                p_171737_.queueSectionData(p_171738_, SectionPos.of(p_171735_, j, p_171736_), flag ? new DataLayer((byte[])((byte[])p_171741_.next()).clone()) : new DataLayer());
                this.level.setSectionDirtyWithNeighbors(p_171735_, j, p_171736_);
            }
        }

    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    public Collection<PlayerInfo> getListedOnlinePlayers() {
        return this.listedPlayers;
    }

    public Collection<PlayerInfo> getOnlinePlayers() {
        return this.playerInfoMap.values();
    }

    public Collection<UUID> getOnlinePlayerIds() {
        return this.playerInfoMap.keySet();
    }

    @Nullable
    public PlayerInfo getPlayerInfo(UUID p_104950_) {
        return (PlayerInfo)this.playerInfoMap.get(p_104950_);
    }

    @Nullable
    public PlayerInfo getPlayerInfo(String p_104939_) {
        Iterator var2 = this.playerInfoMap.values().iterator();

        PlayerInfo playerinfo;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            playerinfo = (PlayerInfo)var2.next();
        } while(!playerinfo.getProfile().getName().equals(p_104939_));

        return playerinfo;
    }

    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }

    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }

    public CommandDispatcher<SharedSuggestionProvider> getCommands() {
        return this.commands;
    }

    public ClientLevel getLevel() {
        return this.level;
    }

    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }

    public UUID getId() {
        return this.id;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess.compositeAccess();
    }

    public void markMessageAsProcessed(PlayerChatMessage p_242356_, boolean p_242455_) {
        MessageSignature messagesignature = p_242356_.signature();
        if (messagesignature != null && this.lastSeenMessages.addPending(messagesignature, p_242455_) && this.lastSeenMessages.offset() > 64) {
            this.sendChatAcknowledgement();
        }

    }

    private void sendChatAcknowledgement() {
        int i = this.lastSeenMessages.getAndClearOffset();
        if (i > 0) {
            this.send((Packet)(new ServerboundChatAckPacket(i)));
        }

    }

    public void sendChat(String p_249888_) {
        p_249888_ = ForgeHooksClient.onClientSendMessage(p_249888_);
        if (!p_249888_.isEmpty()) {
            Instant instant = Instant.now();
            long i = SaltSupplier.getLong();
            LastSeenMessagesTracker.Update lastseenmessagestracker$update = this.lastSeenMessages.generateAndApplyUpdate();
            MessageSignature messagesignature = this.signedMessageEncoder.pack(new SignedMessageBody(p_249888_, instant, i, lastseenmessagestracker$update.lastSeen()));
            this.send((Packet)(new ServerboundChatPacket(p_249888_, instant, i, messagesignature, lastseenmessagestracker$update.update())));
        }
    }

    public void sendCommand(String p_250092_) {
        if (!ClientCommandHandler.runCommand(p_250092_)) {
            Instant instant = Instant.now();
            long i = SaltSupplier.getLong();
            LastSeenMessagesTracker.Update lastseenmessagestracker$update = this.lastSeenMessages.generateAndApplyUpdate();
            ArgumentSignatures argumentsignatures = ArgumentSignatures.signCommand(SignableCommand.of(this.parseCommand(p_250092_)), (p_247875_) -> {
                SignedMessageBody signedmessagebody = new SignedMessageBody(p_247875_, instant, i, lastseenmessagestracker$update.lastSeen());
                return this.signedMessageEncoder.pack(signedmessagebody);
            });
            this.send((Packet)(new ServerboundChatCommandPacket(p_250092_, instant, i, argumentsignatures, lastseenmessagestracker$update.update())));
        }
    }

    public boolean sendUnsignedCommand(String p_251509_) {
        if (SignableCommand.of(this.parseCommand(p_251509_)).arguments().isEmpty()) {
            LastSeenMessagesTracker.Update lastseenmessagestracker$update = this.lastSeenMessages.generateAndApplyUpdate();
            this.send((Packet)(new ServerboundChatCommandPacket(p_251509_, Instant.now(), 0L, ArgumentSignatures.EMPTY, lastseenmessagestracker$update.update())));
            return true;
        } else {
            return false;
        }
    }

    private ParseResults<SharedSuggestionProvider> parseCommand(String p_249982_) {
        return this.commands.parse(p_249982_, this.suggestionsProvider);
    }

    public void tick() {
        if (this.connection.isEncrypted()) {
            ProfileKeyPairManager profilekeypairmanager = this.minecraft.getProfileKeyPairManager();
            if (profilekeypairmanager.shouldRefreshKeyPair()) {
                profilekeypairmanager.prepareKeyPair().thenAcceptAsync((p_253339_) -> {
                    p_253339_.ifPresent(this::setKeyPair);
                }, this.minecraft);
            }
        }

        this.sendDeferredPackets();
        this.telemetryManager.tick();
    }

    public void setKeyPair(ProfileKeyPair p_261475_) {
        if (this.localGameProfile.getId().equals(this.minecraft.getUser().getProfileId()) && (this.chatSession == null || !this.chatSession.keyPair().equals(p_261475_))) {
            this.chatSession = LocalChatSession.create(p_261475_);
            this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.getId());
            this.send((Packet)(new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData())));
        }

    }

    @Nullable
    public ServerData getServerData() {
        return this.serverData;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean isFeatureEnabled(FeatureFlagSet p_250605_) {
        return p_250605_.isSubsetOf(this.enabledFeatures());
    }

    @OnlyIn(Dist.CLIENT)
    static record DeferredPacket(Packet<ServerGamePacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
        DeferredPacket(Packet<ServerGamePacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
            this.packet = packet;
            this.sendCondition = sendCondition;
            this.expirationTime = expirationTime;
        }

        public Packet<ServerGamePacketListener> packet() {
            return this.packet;
        }

        public BooleanSupplier sendCondition() {
            return this.sendCondition;
        }

        public long expirationTime() {
            return this.expirationTime;
        }
    }
}
