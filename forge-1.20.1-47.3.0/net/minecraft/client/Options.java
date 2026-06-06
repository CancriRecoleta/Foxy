//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.OptionInstance.UnitDouble;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Options {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>() {
    };
    public static final int RENDER_DISTANCE_TINY = 2;
    public static final int RENDER_DISTANCE_SHORT = 4;
    public static final int RENDER_DISTANCE_NORMAL = 8;
    public static final int RENDER_DISTANCE_FAR = 12;
    public static final int RENDER_DISTANCE_REALLY_FAR = 16;
    public static final int RENDER_DISTANCE_EXTREME = 32;
    private static final Splitter OPTION_SPLITTER = Splitter.on(':').limit(2);
    private static final float DEFAULT_VOLUME = 1.0F;
    public static final String DEFAULT_SOUND_DEVICE = "";
    private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = Component.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
    private final OptionInstance<Boolean> darkMojangStudiosBackground;
    private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
    private final OptionInstance<Boolean> hideLightningFlash;
    private final OptionInstance<Double> sensitivity;
    private final OptionInstance<Integer> renderDistance;
    private final OptionInstance<Integer> simulationDistance;
    private int serverRenderDistance;
    private final OptionInstance<Double> entityDistanceScaling;
    public static final int UNLIMITED_FRAMERATE_CUTOFF = 260;
    private final OptionInstance<Integer> framerateLimit;
    private final OptionInstance<CloudStatus> cloudStatus;
    private static final Component GRAPHICS_TOOLTIP_FAST = Component.translatable("options.graphics.fast.tooltip");
    private static final Component GRAPHICS_TOOLTIP_FABULOUS;
    private static final Component GRAPHICS_TOOLTIP_FANCY;
    private final OptionInstance<GraphicsStatus> graphicsMode;
    private final OptionInstance<Boolean> ambientOcclusion;
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE;
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED;
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY;
    private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates;
    public List<String> resourcePacks;
    public List<String> incompatibleResourcePacks;
    private final OptionInstance<ChatVisiblity> chatVisibility;
    private final OptionInstance<Double> chatOpacity;
    private final OptionInstance<Double> chatLineSpacing;
    private final OptionInstance<Double> textBackgroundOpacity;
    private final OptionInstance<Double> panoramaSpeed;
    private static final Component ACCESSIBILITY_TOOLTIP_CONTRAST_MODE;
    private final OptionInstance<Boolean> highContrast;
    @Nullable
    public String fullscreenVideoModeString;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus;
    private final Set<PlayerModelPart> modelParts;
    private final OptionInstance<HumanoidArm> mainHand;
    public int overrideWidth;
    public int overrideHeight;
    private final OptionInstance<Double> chatScale;
    private final OptionInstance<Double> chatWidth;
    private final OptionInstance<Double> chatHeightUnfocused;
    private final OptionInstance<Double> chatHeightFocused;
    private final OptionInstance<Double> chatDelay;
    private static final Component ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME;
    private final OptionInstance<Double> notificationDisplayTime;
    private final OptionInstance<Integer> mipmapLevels;
    public boolean useNativeTransport;
    private final OptionInstance<AttackIndicatorStatus> attackIndicator;
    public TutorialSteps tutorialStep;
    public boolean joinedFirstServer;
    public boolean hideBundleTutorial;
    private final OptionInstance<Integer> biomeBlendRadius;
    private final OptionInstance<Double> mouseWheelSensitivity;
    private final OptionInstance<Boolean> rawMouseInput;
    public int glDebugVerbosity;
    private final OptionInstance<Boolean> autoJump;
    private final OptionInstance<Boolean> operatorItemsTab;
    private final OptionInstance<Boolean> autoSuggestions;
    private final OptionInstance<Boolean> chatColors;
    private final OptionInstance<Boolean> chatLinks;
    private final OptionInstance<Boolean> chatLinksPrompt;
    private final OptionInstance<Boolean> enableVsync;
    private final OptionInstance<Boolean> entityShadows;
    private final OptionInstance<Boolean> forceUnicodeFont;
    private final OptionInstance<Boolean> invertYMouse;
    private final OptionInstance<Boolean> discreteMouseScroll;
    private final OptionInstance<Boolean> realmsNotifications;
    private static final Component ALLOW_SERVER_LISTING_TOOLTIP;
    private final OptionInstance<Boolean> allowServerListing;
    private final OptionInstance<Boolean> reducedDebugInfo;
    private final Map<SoundSource, OptionInstance<Double>> soundSourceVolumes;
    private final OptionInstance<Boolean> showSubtitles;
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON;
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF;
    private final OptionInstance<Boolean> directionalAudio;
    private final OptionInstance<Boolean> backgroundForChatOnly;
    private final OptionInstance<Boolean> touchscreen;
    private final OptionInstance<Boolean> fullscreen;
    private final OptionInstance<Boolean> bobView;
    private static final Component MOVEMENT_TOGGLE;
    private static final Component MOVEMENT_HOLD;
    private final OptionInstance<Boolean> toggleCrouch;
    private final OptionInstance<Boolean> toggleSprint;
    public boolean skipMultiplayerWarning;
    public boolean skipRealms32bitWarning;
    private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES;
    private final OptionInstance<Boolean> hideMatchedNames;
    private final OptionInstance<Boolean> showAutosaveIndicator;
    private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE;
    private final OptionInstance<Boolean> onlyShowSecureChat;
    public final KeyMapping keyUp;
    public final KeyMapping keyLeft;
    public final KeyMapping keyDown;
    public final KeyMapping keyRight;
    public final KeyMapping keyJump;
    public final KeyMapping keyShift;
    public final KeyMapping keySprint;
    public final KeyMapping keyInventory;
    public final KeyMapping keySwapOffhand;
    public final KeyMapping keyDrop;
    public final KeyMapping keyUse;
    public final KeyMapping keyAttack;
    public final KeyMapping keyPickItem;
    public final KeyMapping keyChat;
    public final KeyMapping keyPlayerList;
    public final KeyMapping keyCommand;
    public final KeyMapping keySocialInteractions;
    public final KeyMapping keyScreenshot;
    public final KeyMapping keyTogglePerspective;
    public final KeyMapping keySmoothCamera;
    public final KeyMapping keyFullscreen;
    public final KeyMapping keySpectatorOutlines;
    public final KeyMapping keyAdvancements;
    public final KeyMapping[] keyHotbarSlots;
    public final KeyMapping keySaveHotbarActivator;
    public final KeyMapping keyLoadHotbarActivator;
    public KeyMapping[] keyMappings;
    protected Minecraft minecraft;
    private final File optionsFile;
    public boolean hideGui;
    private CameraType cameraType;
    public boolean renderDebug;
    public boolean renderDebugCharts;
    public boolean renderFpsChart;
    public String lastMpIp;
    public boolean smoothCamera;
    private final OptionInstance<Integer> fov;
    private static final Component TELEMETRY_TOOLTIP;
    private final OptionInstance<Boolean> telemetryOptInExtra;
    private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT;
    private final OptionInstance<Double> screenEffectScale;
    private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT;
    private final OptionInstance<Double> fovEffectScale;
    private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT;
    private final OptionInstance<Double> darknessEffectScale;
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_SPEED;
    private final OptionInstance<Double> glintSpeed;
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH;
    private final OptionInstance<Double> glintStrength;
    private static final Component ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH;
    private final OptionInstance<Double> damageTiltStrength;
    private final OptionInstance<Double> gamma;
    public static final int AUTO_GUI_SCALE = 0;
    private static final int MAX_GUI_SCALE_INCLUSIVE = 2147483646;
    private final OptionInstance<Integer> guiScale;
    private final OptionInstance<ParticleStatus> particles;
    private final OptionInstance<NarratorStatus> narrator;
    public String languageCode;
    private final OptionInstance<String> soundDevice;
    public boolean onboardAccessibility;
    public boolean syncWrites;

    public OptionInstance<Boolean> darkMojangStudiosBackground() {
        return this.darkMojangStudiosBackground;
    }

    public OptionInstance<Boolean> hideLightningFlash() {
        return this.hideLightningFlash;
    }

    public OptionInstance<Double> sensitivity() {
        return this.sensitivity;
    }

    public OptionInstance<Integer> renderDistance() {
        return this.renderDistance;
    }

    public OptionInstance<Integer> simulationDistance() {
        return this.simulationDistance;
    }

    public OptionInstance<Double> entityDistanceScaling() {
        return this.entityDistanceScaling;
    }

    public OptionInstance<Integer> framerateLimit() {
        return this.framerateLimit;
    }

    public OptionInstance<CloudStatus> cloudStatus() {
        return this.cloudStatus;
    }

    public OptionInstance<GraphicsStatus> graphicsMode() {
        return this.graphicsMode;
    }

    public OptionInstance<Boolean> ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
        return this.prioritizeChunkUpdates;
    }

    public void updateResourcePacks(PackRepository p_275268_) {
        List<String> list = ImmutableList.copyOf(this.resourcePacks);
        this.resourcePacks.clear();
        this.incompatibleResourcePacks.clear();
        Iterator var3 = p_275268_.getSelectedPacks().iterator();

        while(var3.hasNext()) {
            Pack pack = (Pack)var3.next();
            if (!pack.isFixedPosition()) {
                this.resourcePacks.add(pack.getId());
                if (!pack.getCompatibility().isCompatible()) {
                    this.incompatibleResourcePacks.add(pack.getId());
                }
            }
        }

        this.save();
        List<String> list1 = ImmutableList.copyOf(this.resourcePacks);
        if (!list1.equals(list)) {
            this.minecraft.reloadResourcePacks();
        }

    }

    public OptionInstance<ChatVisiblity> chatVisibility() {
        return this.chatVisibility;
    }

    public OptionInstance<Double> chatOpacity() {
        return this.chatOpacity;
    }

    public OptionInstance<Double> chatLineSpacing() {
        return this.chatLineSpacing;
    }

    public OptionInstance<Double> textBackgroundOpacity() {
        return this.textBackgroundOpacity;
    }

    public OptionInstance<Double> panoramaSpeed() {
        return this.panoramaSpeed;
    }

    public OptionInstance<Boolean> highContrast() {
        return this.highContrast;
    }

    public OptionInstance<HumanoidArm> mainHand() {
        return this.mainHand;
    }

    public OptionInstance<Double> chatScale() {
        return this.chatScale;
    }

    public OptionInstance<Double> chatWidth() {
        return this.chatWidth;
    }

    public OptionInstance<Double> chatHeightUnfocused() {
        return this.chatHeightUnfocused;
    }

    public OptionInstance<Double> chatHeightFocused() {
        return this.chatHeightFocused;
    }

    public OptionInstance<Double> chatDelay() {
        return this.chatDelay;
    }

    public OptionInstance<Double> notificationDisplayTime() {
        return this.notificationDisplayTime;
    }

    public OptionInstance<Integer> mipmapLevels() {
        return this.mipmapLevels;
    }

    public OptionInstance<AttackIndicatorStatus> attackIndicator() {
        return this.attackIndicator;
    }

    public OptionInstance<Integer> biomeBlendRadius() {
        return this.biomeBlendRadius;
    }

    private static double logMouse(int p_231966_) {
        return Math.pow(10.0, (double)p_231966_ / 100.0);
    }

    private static int unlogMouse(double p_231840_) {
        return Mth.floor(Math.log10(p_231840_) * 100.0);
    }

    public OptionInstance<Double> mouseWheelSensitivity() {
        return this.mouseWheelSensitivity;
    }

    public OptionInstance<Boolean> rawMouseInput() {
        return this.rawMouseInput;
    }

    public OptionInstance<Boolean> autoJump() {
        return this.autoJump;
    }

    public OptionInstance<Boolean> operatorItemsTab() {
        return this.operatorItemsTab;
    }

    public OptionInstance<Boolean> autoSuggestions() {
        return this.autoSuggestions;
    }

    public OptionInstance<Boolean> chatColors() {
        return this.chatColors;
    }

    public OptionInstance<Boolean> chatLinks() {
        return this.chatLinks;
    }

    public OptionInstance<Boolean> chatLinksPrompt() {
        return this.chatLinksPrompt;
    }

    public OptionInstance<Boolean> enableVsync() {
        return this.enableVsync;
    }

    public OptionInstance<Boolean> entityShadows() {
        return this.entityShadows;
    }

    public OptionInstance<Boolean> forceUnicodeFont() {
        return this.forceUnicodeFont;
    }

    public OptionInstance<Boolean> invertYMouse() {
        return this.invertYMouse;
    }

    public OptionInstance<Boolean> discreteMouseScroll() {
        return this.discreteMouseScroll;
    }

    public OptionInstance<Boolean> realmsNotifications() {
        return this.realmsNotifications;
    }

    public OptionInstance<Boolean> allowServerListing() {
        return this.allowServerListing;
    }

    public OptionInstance<Boolean> reducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public final float getSoundSourceVolume(SoundSource p_92148_) {
        return ((Double)this.getSoundSourceOptionInstance(p_92148_).get()).floatValue();
    }

    public final OptionInstance<Double> getSoundSourceOptionInstance(SoundSource p_251574_) {
        return (OptionInstance)Objects.requireNonNull((OptionInstance)this.soundSourceVolumes.get(p_251574_));
    }

    private OptionInstance<Double> createSoundSliderOptionInstance(String p_250353_, SoundSource p_249262_) {
        return new OptionInstance(p_250353_, OptionInstance.noTooltip(), (p_262709_, p_262710_) -> {
            return p_262710_ == 0.0 ? genericValueLabel(p_262709_, CommonComponents.OPTION_OFF) : percentValueLabel(p_262709_, p_262710_);
        }, UnitDouble.INSTANCE, 1.0, (p_247768_) -> {
            Minecraft.getInstance().getSoundManager().updateSourceVolume(p_249262_, p_247768_.floatValue());
        });
    }

    public OptionInstance<Boolean> showSubtitles() {
        return this.showSubtitles;
    }

    public OptionInstance<Boolean> directionalAudio() {
        return this.directionalAudio;
    }

    public OptionInstance<Boolean> backgroundForChatOnly() {
        return this.backgroundForChatOnly;
    }

    public OptionInstance<Boolean> touchscreen() {
        return this.touchscreen;
    }

    public OptionInstance<Boolean> fullscreen() {
        return this.fullscreen;
    }

    public OptionInstance<Boolean> bobView() {
        return this.bobView;
    }

    public OptionInstance<Boolean> toggleCrouch() {
        return this.toggleCrouch;
    }

    public OptionInstance<Boolean> toggleSprint() {
        return this.toggleSprint;
    }

    public OptionInstance<Boolean> hideMatchedNames() {
        return this.hideMatchedNames;
    }

    public OptionInstance<Boolean> showAutosaveIndicator() {
        return this.showAutosaveIndicator;
    }

    public OptionInstance<Boolean> onlyShowSecureChat() {
        return this.onlyShowSecureChat;
    }

    public OptionInstance<Integer> fov() {
        return this.fov;
    }

    public OptionInstance<Boolean> telemetryOptInExtra() {
        return this.telemetryOptInExtra;
    }

    public OptionInstance<Double> screenEffectScale() {
        return this.screenEffectScale;
    }

    public OptionInstance<Double> fovEffectScale() {
        return this.fovEffectScale;
    }

    public OptionInstance<Double> darknessEffectScale() {
        return this.darknessEffectScale;
    }

    public OptionInstance<Double> glintSpeed() {
        return this.glintSpeed;
    }

    public OptionInstance<Double> glintStrength() {
        return this.glintStrength;
    }

    public OptionInstance<Double> damageTiltStrength() {
        return this.damageTiltStrength;
    }

    public OptionInstance<Double> gamma() {
        return this.gamma;
    }

    public OptionInstance<Integer> guiScale() {
        return this.guiScale;
    }

    public OptionInstance<ParticleStatus> particles() {
        return this.particles;
    }

    public OptionInstance<NarratorStatus> narrator() {
        return this.narrator;
    }

    public OptionInstance<String> soundDevice() {
        return this.soundDevice;
    }

    public Options(Minecraft p_92138_, File p_92139_) {
        this.darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
        this.hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
        this.sensitivity = new OptionInstance("options.sensitivity", OptionInstance.noTooltip(), (p_232096_, p_232097_) -> {
            if (p_232097_ == 0.0) {
                return genericValueLabel(p_232096_, Component.translatable("options.sensitivity.min"));
            } else {
                return p_232097_ == 1.0 ? genericValueLabel(p_232096_, Component.translatable("options.sensitivity.max")) : percentValueLabel(p_232096_, 2.0 * p_232097_);
            }
        }, UnitDouble.INSTANCE, 0.5, (p_232115_) -> {
        });
        this.serverRenderDistance = 0;
        this.entityDistanceScaling = new OptionInstance("options.entityDistanceScaling", OptionInstance.noTooltip(), Options::percentValueLabel, (new OptionInstance.IntRange(2, 20)).xmap((p_232020_) -> {
            return (double)p_232020_ / 4.0;
        }, (p_232112_) -> {
            return (int)(p_232112_ * 4.0);
        }), Codec.doubleRange(0.5, 5.0), 1.0, (p_265235_) -> {
        });
        this.framerateLimit = new OptionInstance("options.framerateLimit", OptionInstance.noTooltip(), (p_232048_, p_232049_) -> {
            return p_232049_ == 260 ? genericValueLabel(p_232048_, Component.translatable("options.framerateLimit.max")) : genericValueLabel(p_232048_, Component.translatable("options.framerate", p_232049_));
        }, (new OptionInstance.IntRange(1, 26)).xmap((p_232003_) -> {
            return p_232003_ * 10;
        }, (p_232094_) -> {
            return p_232094_ / 10;
        }), Codec.intRange(10, 260), 120, (p_232086_) -> {
            Minecraft.getInstance().getWindow().setFramerateLimit(p_232086_);
        });
        this.cloudStatus = new OptionInstance("options.renderClouds", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(CloudStatus.values()), Codec.either(Codec.BOOL, Codec.STRING).xmap((p_231939_) -> {
            return (CloudStatus)p_231939_.map((p_232082_) -> {
                return p_232082_ ? CloudStatus.FANCY : CloudStatus.OFF;
            }, (p_232043_) -> {
                CloudStatus cloudstatus;
                switch (p_232043_) {
                    case "true" -> cloudstatus = CloudStatus.FANCY;
                    case "fast" -> cloudstatus = CloudStatus.FAST;
                    default -> cloudstatus = CloudStatus.OFF;
                }

                return cloudstatus;
            });
        }, (p_231941_) -> {
            String s;
            switch (p_231941_) {
                case FANCY -> s = "true";
                case FAST -> s = "fast";
                case OFF -> s = "false";
                default -> throw new IncompatibleClassChangeError();
            }

            return Either.right(s);
        })), CloudStatus.FANCY, (p_231854_) -> {
            if (Minecraft.useShaderTransparency()) {
                RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getCloudsTarget();
                if (rendertarget != null) {
                    rendertarget.clear(Minecraft.ON_OSX);
                }
            }

        });
        this.graphicsMode = new OptionInstance("options.graphics", (p_258117_) -> {
            Tooltip tooltip;
            switch (p_258117_) {
                case FANCY -> tooltip = Tooltip.create(GRAPHICS_TOOLTIP_FANCY);
                case FAST -> tooltip = Tooltip.create(GRAPHICS_TOOLTIP_FAST);
                case FABULOUS -> tooltip = Tooltip.create(GRAPHICS_TOOLTIP_FABULOUS);
                default -> throw new IncompatibleClassChangeError();
            }

            return tooltip;
        }, (p_231904_, p_231905_) -> {
            MutableComponent mutablecomponent = Component.translatable(p_231905_.getKey());
            return p_231905_ == GraphicsStatus.FABULOUS ? mutablecomponent.withStyle(ChatFormatting.ITALIC) : mutablecomponent;
        }, new OptionInstance.AltEnum(Arrays.asList(GraphicsStatus.values()), (List)Stream.of(GraphicsStatus.values()).filter((p_231943_) -> {
            return p_231943_ != GraphicsStatus.FABULOUS;
        }).collect(Collectors.toList()), () -> {
            return Minecraft.getInstance().isRunning() && Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous();
        }, (p_231862_, p_231863_) -> {
            Minecraft minecraft = Minecraft.getInstance();
            GpuWarnlistManager gpuwarnlistmanager = minecraft.getGpuWarnlistManager();
            if (p_231863_ == GraphicsStatus.FABULOUS && gpuwarnlistmanager.willShowWarning()) {
                gpuwarnlistmanager.showWarning();
            } else {
                p_231862_.set(p_231863_);
                minecraft.levelRenderer.allChanged();
            }

        }, Codec.INT.xmap(GraphicsStatus::byId, GraphicsStatus::getId)), GraphicsStatus.FANCY, (p_268192_) -> {
        });
        this.ambientOcclusion = OptionInstance.createBoolean("options.ao", true, (p_263512_) -> {
            Minecraft.getInstance().levelRenderer.allChanged();
        });
        this.prioritizeChunkUpdates = new OptionInstance("options.prioritizeChunkUpdates", (p_258118_) -> {
            Tooltip tooltip;
            switch (p_258118_) {
                case NONE -> tooltip = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NONE);
                case PLAYER_AFFECTED -> tooltip = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
                case NEARBY -> tooltip = Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
                default -> throw new IncompatibleClassChangeError();
            }

            return tooltip;
        }, OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(PrioritizeChunkUpdates.values()), Codec.INT.xmap(PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId)), PrioritizeChunkUpdates.NONE, (p_268073_) -> {
        });
        this.resourcePacks = Lists.newArrayList();
        this.incompatibleResourcePacks = Lists.newArrayList();
        this.chatVisibility = new OptionInstance("options.chat.visibility", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(ChatVisiblity.values()), Codec.INT.xmap(ChatVisiblity::byId, ChatVisiblity::getId)), ChatVisiblity.FULL, (p_268018_) -> {
        });
        this.chatOpacity = new OptionInstance("options.chat.opacity", OptionInstance.noTooltip(), (p_232088_, p_232089_) -> {
            return percentValueLabel(p_232088_, p_232089_ * 0.9 + 0.1);
        }, UnitDouble.INSTANCE, 1.0, (p_232106_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.chatLineSpacing = new OptionInstance("options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, UnitDouble.INSTANCE, 0.0, (p_232103_) -> {
        });
        this.textBackgroundOpacity = new OptionInstance("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, UnitDouble.INSTANCE, 0.5, (p_232100_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.panoramaSpeed = new OptionInstance("options.accessibility.panorama_speed", OptionInstance.noTooltip(), Options::percentValueLabel, UnitDouble.INSTANCE, 1.0, (p_232109_) -> {
        });
        this.highContrast = OptionInstance.createBoolean("options.accessibility.high_contrast", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_CONTRAST_MODE), false, (p_275860_) -> {
            PackRepository packrepository = Minecraft.getInstance().getResourcePackRepository();
            boolean flag2 = packrepository.getSelectedIds().contains("high_contrast");
            if (!flag2 && p_275860_) {
                if (packrepository.addPack("high_contrast")) {
                    this.updateResourcePacks(packrepository);
                }
            } else if (flag2 && !p_275860_ && packrepository.removePack("high_contrast")) {
                this.updateResourcePacks(packrepository);
            }

        });
        this.pauseOnLostFocus = true;
        this.modelParts = EnumSet.allOf(PlayerModelPart.class);
        this.mainHand = new OptionInstance("options.mainHand", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(HumanoidArm.values()), Codec.STRING.xmap((p_232028_) -> {
            return "left".equals(p_232028_) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        }, (p_231937_) -> {
            return p_231937_ == HumanoidArm.LEFT ? "left" : "right";
        })), HumanoidArm.RIGHT, (p_231842_) -> {
            this.broadcastOptions();
        });
        this.chatScale = new OptionInstance("options.chat.scale", OptionInstance.noTooltip(), (p_232078_, p_232079_) -> {
            return (Component)(p_232079_ == 0.0 ? CommonComponents.optionStatus(p_232078_, false) : percentValueLabel(p_232078_, p_232079_));
        }, UnitDouble.INSTANCE, 1.0, (p_232092_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.chatWidth = new OptionInstance("options.chat.width", OptionInstance.noTooltip(), (p_232068_, p_232069_) -> {
            return pixelValueLabel(p_232068_, ChatComponent.getWidth(p_232069_));
        }, UnitDouble.INSTANCE, 1.0, (p_232084_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.chatHeightUnfocused = new OptionInstance("options.chat.height.unfocused", OptionInstance.noTooltip(), (p_232058_, p_232059_) -> {
            return pixelValueLabel(p_232058_, ChatComponent.getHeight(p_232059_));
        }, UnitDouble.INSTANCE, ChatComponent.defaultUnfocusedPct(), (p_232074_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.chatHeightFocused = new OptionInstance("options.chat.height.focused", OptionInstance.noTooltip(), (p_232045_, p_232046_) -> {
            return pixelValueLabel(p_232045_, ChatComponent.getHeight(p_232046_));
        }, UnitDouble.INSTANCE, 1.0, (p_232064_) -> {
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        this.chatDelay = new OptionInstance("options.chat.delay_instant", OptionInstance.noTooltip(), (p_232030_, p_232031_) -> {
            return p_232031_ <= 0.0 ? Component.translatable("options.chat.delay_none") : Component.translatable("options.chat.delay", String.format(Locale.ROOT, "%.1f", p_232031_));
        }, (new OptionInstance.IntRange(0, 60)).xmap((p_231986_) -> {
            return (double)p_231986_ / 10.0;
        }, (p_232054_) -> {
            return (int)(p_232054_ * 10.0);
        }), Codec.doubleRange(0.0, 6.0), 0.0, (p_232039_) -> {
            Minecraft.getInstance().getChatListener().setMessageDelay(p_232039_);
        });
        this.notificationDisplayTime = new OptionInstance("options.notifications.display_time", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME), (p_264664_, p_270572_) -> {
            return genericValueLabel(p_264664_, Component.translatable("options.multiplier", p_270572_));
        }, (new OptionInstance.IntRange(5, 100)).xmap((p_264666_) -> {
            return (double)p_264666_ / 10.0;
        }, (p_264667_) -> {
            return (int)(p_264667_ * 10.0);
        }), Codec.doubleRange(0.5, 10.0), 1.0, (p_268049_) -> {
        });
        this.mipmapLevels = new OptionInstance("options.mipmapLevels", OptionInstance.noTooltip(), (p_232033_, p_232034_) -> {
            return (Component)(p_232034_ == 0 ? CommonComponents.optionStatus(p_232033_, false) : genericValueLabel(p_232033_, p_232034_));
        }, new OptionInstance.IntRange(0, 4), 4, (p_268254_) -> {
        });
        this.useNativeTransport = true;
        this.attackIndicator = new OptionInstance("options.attackIndicator", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(AttackIndicatorStatus.values()), Codec.INT.xmap(AttackIndicatorStatus::byId, AttackIndicatorStatus::getId)), AttackIndicatorStatus.CROSSHAIR, (p_268185_) -> {
        });
        this.tutorialStep = TutorialSteps.MOVEMENT;
        this.joinedFirstServer = false;
        this.hideBundleTutorial = false;
        this.biomeBlendRadius = new OptionInstance("options.biomeBlendRadius", OptionInstance.noTooltip(), (p_232016_, p_232017_) -> {
            int i = p_232017_ * 2 + 1;
            return genericValueLabel(p_232016_, Component.translatable("options.biomeBlendRadius." + i));
        }, new OptionInstance.IntRange(0, 7), 2, (p_232026_) -> {
            Minecraft.getInstance().levelRenderer.allChanged();
        });
        this.mouseWheelSensitivity = new OptionInstance("options.mouseWheelSensitivity", OptionInstance.noTooltip(), (p_232013_, p_232014_) -> {
            return genericValueLabel(p_232013_, Component.literal(String.format(Locale.ROOT, "%.2f", p_232014_)));
        }, (new OptionInstance.IntRange(-200, 100)).xmap(Options::logMouse, Options::unlogMouse), Codec.doubleRange(logMouse(-200), logMouse(100)), logMouse(0), (p_268246_) -> {
        });
        this.rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, (p_232062_) -> {
            Window window = Minecraft.getInstance().getWindow();
            if (window != null) {
                window.updateRawMouseInput(p_232062_);
            }

        });
        this.glDebugVerbosity = 1;
        this.autoJump = OptionInstance.createBoolean("options.autoJump", false);
        this.operatorItemsTab = OptionInstance.createBoolean("options.operatorItemsTab", false);
        this.autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
        this.chatColors = OptionInstance.createBoolean("options.chat.color", true);
        this.chatLinks = OptionInstance.createBoolean("options.chat.links", true);
        this.chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
        this.enableVsync = OptionInstance.createBoolean("options.vsync", true, (p_232052_) -> {
            if (Minecraft.getInstance().getWindow() != null) {
                Minecraft.getInstance().getWindow().updateVsync(p_232052_);
            }

        });
        this.entityShadows = OptionInstance.createBoolean("options.entityShadows", true);
        this.forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, (p_232037_) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getWindow() != null) {
                minecraft.selectMainFont(p_232037_);
                minecraft.resizeDisplay();
            }

        });
        this.invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
        this.discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
        this.realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", true);
        this.allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, (p_232022_) -> {
            this.broadcastOptions();
        });
        this.reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
        this.soundSourceVolumes = (Map)Util.make(new EnumMap(SoundSource.class), (p_247766_) -> {
            SoundSource[] var2 = SoundSource.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                SoundSource soundsource = var2[var4];
                p_247766_.put(soundsource, this.createSoundSliderOptionInstance("soundCategory." + soundsource.getName(), soundsource));
            }

        });
        this.showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
        this.directionalAudio = OptionInstance.createBoolean("options.directionalAudio", (p_231858_) -> {
            return p_231858_ ? Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_ON) : Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_OFF);
        }, false, (p_275545_) -> {
            SoundManager soundmanager = Minecraft.getInstance().getSoundManager();
            soundmanager.reload();
            soundmanager.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
        });
        this.backgroundForChatOnly = new OptionInstance("options.accessibility.text_background", OptionInstance.noTooltip(), (p_231976_, p_231977_) -> {
            return p_231977_ ? Component.translatable("options.accessibility.text_background.chat") : Component.translatable("options.accessibility.text_background.everywhere");
        }, OptionInstance.BOOLEAN_VALUES, true, (p_275545_) -> {
        });
        this.touchscreen = OptionInstance.createBoolean("options.touchscreen", false);
        this.fullscreen = OptionInstance.createBoolean("options.fullscreen", false, (p_231970_) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getWindow() != null && minecraft.getWindow().isFullscreen() != p_231970_) {
                minecraft.getWindow().toggleFullScreen();
                this.fullscreen().set(minecraft.getWindow().isFullscreen());
            }

        });
        this.bobView = OptionInstance.createBoolean("options.viewBobbing", true);
        this.toggleCrouch = new OptionInstance("key.sneak", OptionInstance.noTooltip(), (p_231956_, p_231957_) -> {
            return p_231957_ ? MOVEMENT_TOGGLE : MOVEMENT_HOLD;
        }, OptionInstance.BOOLEAN_VALUES, false, (p_231970_) -> {
        });
        this.toggleSprint = new OptionInstance("key.sprint", OptionInstance.noTooltip(), (p_231956_, p_231957_) -> {
            return p_231957_ ? MOVEMENT_TOGGLE : MOVEMENT_HOLD;
        }, OptionInstance.BOOLEAN_VALUES, false, (p_261689_) -> {
        });
        this.hideMatchedNames = OptionInstance.createBoolean("options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true);
        this.showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
        this.onlyShowSecureChat = OptionInstance.createBoolean("options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false);
        this.keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
        this.keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
        this.keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
        this.keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
        this.keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
        OptionInstance var10006 = this.toggleCrouch;
        Objects.requireNonNull(var10006);
        this.keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", var10006::get);
        var10006 = this.toggleSprint;
        Objects.requireNonNull(var10006);
        this.keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", var10006::get);
        this.keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
        this.keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
        this.keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
        this.keyUse = new KeyMapping("key.use", Type.MOUSE, 1, "key.categories.gameplay");
        this.keyAttack = new KeyMapping("key.attack", Type.MOUSE, 0, "key.categories.gameplay");
        this.keyPickItem = new KeyMapping("key.pickItem", Type.MOUSE, 2, "key.categories.gameplay");
        this.keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
        this.keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
        this.keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
        this.keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
        this.keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
        this.keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
        this.keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        this.keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
        this.keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
        this.keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
        this.keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
        this.keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
        this.keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
        this.keyMappings = (KeyMapping[])ArrayUtils.addAll(new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, this.keyHotbarSlots);
        this.cameraType = CameraType.FIRST_PERSON;
        this.lastMpIp = "";
        this.fov = new OptionInstance("options.fov", OptionInstance.noTooltip(), (p_231999_, p_232000_) -> {
            Component component;
            switch (p_232000_) {
                case 70 -> component = genericValueLabel(p_231999_, Component.translatable("options.fov.min"));
                case 110 -> component = genericValueLabel(p_231999_, Component.translatable("options.fov.max"));
                default -> component = genericValueLabel(p_231999_, p_232000_);
            }

            return component;
        }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap((p_232007_) -> {
            return (int)(p_232007_ * 40.0 + 70.0);
        }, (p_232009_) -> {
            return ((double)p_232009_ - 70.0) / 40.0;
        }), 70, (p_231951_) -> {
            Minecraft.getInstance().levelRenderer.needsUpdate();
        });
        this.telemetryOptInExtra = OptionInstance.createBoolean("options.telemetry.button", OptionInstance.cachedConstantTooltip(TELEMETRY_TOOLTIP), (p_261356_, p_261357_) -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.allowsTelemetry()) {
                return Component.translatable("options.telemetry.state.none");
            } else {
                return p_261357_ && minecraft.extraTelemetryAvailable() ? Component.translatable("options.telemetry.state.all") : Component.translatable("options.telemetry.state.minimal");
            }
        }, false, (p_268147_) -> {
        });
        this.screenEffectScale = new OptionInstance("options.screenEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT), (p_231996_, p_231997_) -> {
            return p_231997_ == 0.0 ? genericValueLabel(p_231996_, CommonComponents.OPTION_OFF) : percentValueLabel(p_231996_, p_231997_);
        }, UnitDouble.INSTANCE, 1.0, (p_231949_) -> {
        });
        this.fovEffectScale = new OptionInstance("options.fovEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT), (p_231996_, p_231997_) -> {
            return p_231997_ == 0.0 ? genericValueLabel(p_231996_, CommonComponents.OPTION_OFF) : percentValueLabel(p_231996_, p_231997_);
        }, UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), Codec.doubleRange(0.0, 1.0), 1.0, (p_231949_) -> {
        });
        this.darknessEffectScale = new OptionInstance("options.darknessEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT), (p_231979_, p_231980_) -> {
            return p_231980_ == 0.0 ? genericValueLabel(p_231979_, CommonComponents.OPTION_OFF) : percentValueLabel(p_231979_, p_231980_);
        }, UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), 1.0, (p_231877_) -> {
        });
        this.glintSpeed = new OptionInstance("options.glintSpeed", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_SPEED), (p_231959_, p_231960_) -> {
            return p_231960_ == 0.0 ? genericValueLabel(p_231959_, CommonComponents.OPTION_OFF) : percentValueLabel(p_231959_, p_231960_);
        }, UnitDouble.INSTANCE, 0.5, (p_265799_) -> {
        });
        this.glintStrength = new OptionInstance("options.glintStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH), (p_267835_, p_267836_) -> {
            return p_267836_ == 0.0 ? genericValueLabel(p_267835_, CommonComponents.OPTION_OFF) : percentValueLabel(p_267835_, p_267836_);
        }, UnitDouble.INSTANCE, 0.75, RenderSystem::setShaderGlintAlpha);
        this.damageTiltStrength = new OptionInstance("options.damageTiltStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH), (p_269609_, p_269610_) -> {
            return p_269610_ == 0.0 ? genericValueLabel(p_269609_, CommonComponents.OPTION_OFF) : percentValueLabel(p_269609_, p_269610_);
        }, UnitDouble.INSTANCE, 1.0, (p_268127_) -> {
        });
        this.gamma = new OptionInstance("options.gamma", OptionInstance.noTooltip(), (p_269609_, p_269610_) -> {
            int i = (int)(p_269610_ * 100.0);
            if (i == 0) {
                return genericValueLabel(p_269609_, Component.translatable("options.gamma.min"));
            } else if (i == 50) {
                return genericValueLabel(p_269609_, Component.translatable("options.gamma.default"));
            } else {
                return i == 100 ? genericValueLabel(p_269609_, Component.translatable("options.gamma.max")) : genericValueLabel(p_269609_, i);
            }
        }, UnitDouble.INSTANCE, 0.5, (p_268127_) -> {
        });
        this.guiScale = new OptionInstance("options.guiScale", OptionInstance.noTooltip(), (p_231982_, p_231983_) -> {
            return p_231983_ == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString(p_231983_));
        }, new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
            Minecraft minecraft = Minecraft.getInstance();
            return !minecraft.isRunning() ? 2147483646 : minecraft.getWindow().calculateScale(0, minecraft.isEnforceUnicode());
        }, 2147483646), 0, (p_270071_) -> {
        });
        this.particles = new OptionInstance("options.particles", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList(ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)), ParticleStatus.ALL, (p_269611_) -> {
        });
        this.narrator = new OptionInstance("options.narrator", OptionInstance.noTooltip(), (p_231907_, p_231908_) -> {
            return (Component)(this.minecraft.getNarrator().isActive() ? p_231908_.getName() : Component.translatable("options.narrator.notavailable"));
        }, new OptionInstance.Enum(Arrays.asList(NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)), NarratorStatus.OFF, (p_231860_) -> {
            this.minecraft.getNarrator().updateNarratorStatus(p_231860_);
        });
        this.languageCode = "en_us";
        this.soundDevice = new OptionInstance("options.audioDevice", OptionInstance.noTooltip(), (p_231919_, p_231920_) -> {
            if ("".equals(p_231920_)) {
                return Component.translatable("options.audioDevice.default");
            } else {
                return p_231920_.startsWith("OpenAL Soft on ") ? Component.literal(p_231920_.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH)) : Component.literal(p_231920_);
            }
        }, new OptionInstance.LazyEnum(() -> {
            return Stream.concat(Stream.of(""), Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList();
        }, (p_232011_) -> {
            return Minecraft.getInstance().isRunning() && (p_232011_ == null || !p_232011_.isEmpty()) && !Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains(p_232011_) ? Optional.empty() : Optional.of(p_232011_);
        }, Codec.STRING), "", (p_275584_) -> {
            SoundManager soundmanager = Minecraft.getInstance().getSoundManager();
            soundmanager.reload();
            soundmanager.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
        });
        this.onboardAccessibility = true;
        this.setForgeKeybindProperties();
        this.minecraft = p_92138_;
        this.optionsFile = new File(p_92139_, "options.txt");
        boolean flag = p_92138_.is64Bit();
        boolean flag1 = flag && Runtime.getRuntime().maxMemory() >= 1000000000L;
        this.renderDistance = new OptionInstance("options.renderDistance", OptionInstance.noTooltip(), (p_231962_, p_268036_) -> {
            return genericValueLabel(p_231962_, Component.translatable("options.chunks", p_268036_));
        }, new OptionInstance.IntRange(2, flag1 ? 32 : 16), flag ? 12 : 8, (p_231992_) -> {
            Minecraft.getInstance().levelRenderer.needsUpdate();
        });
        this.simulationDistance = new OptionInstance("options.simulationDistance", OptionInstance.noTooltip(), (p_231916_, p_270801_) -> {
            return genericValueLabel(p_231916_, Component.translatable("options.chunks", p_270801_));
        }, new OptionInstance.IntRange(5, flag1 ? 32 : 16), flag ? 12 : 8, (p_268325_) -> {
        });
        this.syncWrites = Util.getPlatform() == OS.WINDOWS;
        this.load();
    }

    public float getBackgroundOpacity(float p_92142_) {
        return (Boolean)this.backgroundForChatOnly.get() ? p_92142_ : ((Double)this.textBackgroundOpacity().get()).floatValue();
    }

    public int getBackgroundColor(float p_92171_) {
        return (int)(this.getBackgroundOpacity(p_92171_) * 255.0F) << 24 & -16777216;
    }

    public int getBackgroundColor(int p_92144_) {
        return (Boolean)this.backgroundForChatOnly.get() ? p_92144_ : (int)((Double)this.textBackgroundOpacity.get() * 255.0) << 24 & -16777216;
    }

    public void setKey(KeyMapping p_92160_, InputConstants.Key p_92161_) {
        p_92160_.setKey(p_92161_);
        this.save();
    }

    private void processOptions(FieldAccess p_168428_) {
        p_168428_.process("autoJump", this.autoJump);
        p_168428_.process("operatorItemsTab", this.operatorItemsTab);
        p_168428_.process("autoSuggestions", this.autoSuggestions);
        p_168428_.process("chatColors", this.chatColors);
        p_168428_.process("chatLinks", this.chatLinks);
        p_168428_.process("chatLinksPrompt", this.chatLinksPrompt);
        p_168428_.process("enableVsync", this.enableVsync);
        p_168428_.process("entityShadows", this.entityShadows);
        p_168428_.process("forceUnicodeFont", this.forceUnicodeFont);
        p_168428_.process("discrete_mouse_scroll", this.discreteMouseScroll);
        p_168428_.process("invertYMouse", this.invertYMouse);
        p_168428_.process("realmsNotifications", this.realmsNotifications);
        p_168428_.process("reducedDebugInfo", this.reducedDebugInfo);
        p_168428_.process("showSubtitles", this.showSubtitles);
        p_168428_.process("directionalAudio", this.directionalAudio);
        p_168428_.process("touchscreen", this.touchscreen);
        p_168428_.process("fullscreen", this.fullscreen);
        p_168428_.process("bobView", this.bobView);
        p_168428_.process("toggleCrouch", this.toggleCrouch);
        p_168428_.process("toggleSprint", this.toggleSprint);
        p_168428_.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
        p_168428_.process("hideLightningFlashes", this.hideLightningFlash);
        p_168428_.process("mouseSensitivity", this.sensitivity);
        p_168428_.process("fov", this.fov);
        p_168428_.process("screenEffectScale", this.screenEffectScale);
        p_168428_.process("fovEffectScale", this.fovEffectScale);
        p_168428_.process("darknessEffectScale", this.darknessEffectScale);
        p_168428_.process("glintSpeed", this.glintSpeed);
        p_168428_.process("glintStrength", this.glintStrength);
        p_168428_.process("damageTiltStrength", this.damageTiltStrength);
        p_168428_.process("highContrast", this.highContrast);
        p_168428_.process("gamma", this.gamma);
        p_168428_.process("renderDistance", this.renderDistance);
        p_168428_.process("simulationDistance", this.simulationDistance);
        p_168428_.process("entityDistanceScaling", this.entityDistanceScaling);
        p_168428_.process("guiScale", this.guiScale);
        p_168428_.process("particles", this.particles);
        p_168428_.process("maxFps", this.framerateLimit);
        p_168428_.process("graphicsMode", this.graphicsMode);
        p_168428_.process("ao", this.ambientOcclusion);
        p_168428_.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
        p_168428_.process("biomeBlendRadius", this.biomeBlendRadius);
        p_168428_.process("renderClouds", this.cloudStatus);
        List var10003 = this.resourcePacks;
        Function var10004 = Options::readPackList;
        Gson var10005 = GSON;
        Objects.requireNonNull(var10005);
        this.resourcePacks = (List)p_168428_.process("resourcePacks", var10003, var10004, var10005::toJson);
        var10003 = this.incompatibleResourcePacks;
        var10004 = Options::readPackList;
        var10005 = GSON;
        Objects.requireNonNull(var10005);
        this.incompatibleResourcePacks = (List)p_168428_.process("incompatibleResourcePacks", var10003, var10004, var10005::toJson);
        this.lastMpIp = p_168428_.process("lastServer", this.lastMpIp);
        this.languageCode = p_168428_.process("lang", this.languageCode);
        p_168428_.process("soundDevice", this.soundDevice);
        p_168428_.process("chatVisibility", this.chatVisibility);
        p_168428_.process("chatOpacity", this.chatOpacity);
        p_168428_.process("chatLineSpacing", this.chatLineSpacing);
        p_168428_.process("textBackgroundOpacity", this.textBackgroundOpacity);
        p_168428_.process("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = p_168428_.process("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = p_168428_.process("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = p_168428_.process("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = p_168428_.process("overrideWidth", this.overrideWidth);
        this.overrideHeight = p_168428_.process("overrideHeight", this.overrideHeight);
        p_168428_.process("chatHeightFocused", this.chatHeightFocused);
        p_168428_.process("chatDelay", this.chatDelay);
        p_168428_.process("chatHeightUnfocused", this.chatHeightUnfocused);
        p_168428_.process("chatScale", this.chatScale);
        p_168428_.process("chatWidth", this.chatWidth);
        p_168428_.process("notificationDisplayTime", this.notificationDisplayTime);
        p_168428_.process("mipmapLevels", this.mipmapLevels);
        this.useNativeTransport = p_168428_.process("useNativeTransport", this.useNativeTransport);
        p_168428_.process("mainHand", this.mainHand);
        p_168428_.process("attackIndicator", this.attackIndicator);
        p_168428_.process("narrator", this.narrator);
        this.tutorialStep = (TutorialSteps)p_168428_.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
        p_168428_.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
        p_168428_.process("rawMouseInput", this.rawMouseInput);
        this.glDebugVerbosity = p_168428_.process("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = p_168428_.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
        this.skipRealms32bitWarning = p_168428_.process("skipRealms32bitWarning", this.skipRealms32bitWarning);
        p_168428_.process("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = p_168428_.process("joinedFirstServer", this.joinedFirstServer);
        this.hideBundleTutorial = p_168428_.process("hideBundleTutorial", this.hideBundleTutorial);
        this.syncWrites = p_168428_.process("syncChunkWrites", this.syncWrites);
        p_168428_.process("showAutosaveIndicator", this.showAutosaveIndicator);
        p_168428_.process("allowServerListing", this.allowServerListing);
        p_168428_.process("onlyShowSecureChat", this.onlyShowSecureChat);
        p_168428_.process("panoramaScrollSpeed", this.panoramaSpeed);
        p_168428_.process("telemetryOptInExtra", this.telemetryOptInExtra);
        this.onboardAccessibility = p_168428_.process("onboardAccessibility", this.onboardAccessibility);
        ForgeSnapshotsMod.processOptions(p_168428_);
        this.processOptionsForge(p_168428_);
    }

    private void processOptionsForge(FieldAccess p_168428_) {
        KeyMapping[] var2 = this.keyMappings;
        int var3 = var2.length;

        int var4;
        for(var4 = 0; var4 < var3; ++var4) {
            KeyMapping keymapping = var2[var4];
            String var10000 = keymapping.saveString();
            String s = var10000 + (keymapping.getKeyModifier() != KeyModifier.NONE ? ":" + keymapping.getKeyModifier() : "");
            String s1 = p_168428_.process("key_" + keymapping.getName(), s);
            if (!s.equals(s1)) {
                if (s1.indexOf(58) != -1) {
                    String[] pts = s1.split(":");
                    keymapping.setKeyModifierAndCode(KeyModifier.valueFromString(pts[1]), InputConstants.getKey(pts[0]));
                } else {
                    keymapping.setKeyModifierAndCode(KeyModifier.NONE, InputConstants.getKey(s1));
                }
            }
        }

        SoundSource[] var9 = SoundSource.values();
        var3 = var9.length;

        for(var4 = 0; var4 < var3; ++var4) {
            SoundSource soundsource = var9[var4];
            p_168428_.process("soundCategory_" + soundsource.getName(), (OptionInstance)this.soundSourceVolumes.get(soundsource));
        }

        PlayerModelPart[] var10 = PlayerModelPart.values();
        var3 = var10.length;

        for(var4 = 0; var4 < var3; ++var4) {
            PlayerModelPart playermodelpart = var10[var4];
            boolean flag = this.modelParts.contains(playermodelpart);
            boolean flag1 = p_168428_.process("modelPart_" + playermodelpart.getId(), flag);
            if (flag1 != flag) {
                this.setModelPart(playermodelpart, flag1);
            }
        }

    }

    public void load() {
        this.load(false);
    }

    public void load(boolean limited) {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }

            CompoundTag compoundtag = new CompoundTag();
            BufferedReader bufferedreader = Files.newReader(this.optionsFile, Charsets.UTF_8);

            try {
                bufferedreader.lines().forEach((p_231896_) -> {
                    try {
                        Iterator<String> iterator = OPTION_SPLITTER.split(p_231896_).iterator();
                        compoundtag.putString((String)iterator.next(), (String)iterator.next());
                    } catch (Exception var3) {
                        LOGGER.warn("Skipping bad option: {}", p_231896_);
                    }

                });
            } catch (Throwable var7) {
                if (bufferedreader != null) {
                    try {
                        bufferedreader.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (bufferedreader != null) {
                bufferedreader.close();
            }

            final CompoundTag compoundtag1 = this.dataFix(compoundtag);
            if (!compoundtag1.contains("graphicsMode") && compoundtag1.contains("fancyGraphics")) {
                if (isTrue(compoundtag1.getString("fancyGraphics"))) {
                    this.graphicsMode.set(GraphicsStatus.FANCY);
                } else {
                    this.graphicsMode.set(GraphicsStatus.FAST);
                }
            }

            Consumer<FieldAccess> processor = limited ? this::processOptionsForge : this::processOptions;
            processor.accept(new FieldAccess() {
                @Nullable
                private String getValueOrNull(String p_168459_) {
                    return compoundtag1.contains(p_168459_) ? compoundtag1.getString(p_168459_) : null;
                }

                public <T> void process(String p_232125_, OptionInstance<T> p_232126_) {
                    String s = this.getValueOrNull(p_232125_);
                    if (s != null) {
                        JsonReader jsonreader = new JsonReader(new StringReader(s.isEmpty() ? "\"\"" : s));
                        JsonElement jsonelement = JsonParser.parseReader(jsonreader);
                        DataResult<T> dataresult = p_232126_.codec().parse(JsonOps.INSTANCE, jsonelement);
                        dataresult.error().ifPresent((p_232130_) -> {
                            Options.LOGGER.error("Error parsing option value " + s + " for option " + p_232126_ + ": " + p_232130_.message());
                        });
                        Optional var10000 = dataresult.result();
                        Objects.requireNonNull(p_232126_);
                        var10000.ifPresent(p_232126_::set);
                    }

                }

                public int process(String p_168467_, int p_168468_) {
                    String s = this.getValueOrNull(p_168467_);
                    if (s != null) {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException var5) {
                            NumberFormatException numberformatexception = var5;
                            Options.LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{p_168467_, s, numberformatexception});
                        }
                    }

                    return p_168468_;
                }

                public boolean process(String p_168483_, boolean p_168484_) {
                    String s = this.getValueOrNull(p_168483_);
                    return s != null ? Options.isTrue(s) : p_168484_;
                }

                public String process(String p_168480_, String p_168481_) {
                    return (String)MoreObjects.firstNonNull(this.getValueOrNull(p_168480_), p_168481_);
                }

                public float process(String p_168464_, float p_168465_) {
                    String s = this.getValueOrNull(p_168464_);
                    if (s != null) {
                        if (Options.isTrue(s)) {
                            return 1.0F;
                        }

                        if (Options.isFalse(s)) {
                            return 0.0F;
                        }

                        try {
                            return Float.parseFloat(s);
                        } catch (NumberFormatException var5) {
                            NumberFormatException numberformatexception = var5;
                            Options.LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{p_168464_, s, numberformatexception});
                        }
                    }

                    return p_168465_;
                }

                public <T> T process(String p_168470_, T p_168471_, Function<String, T> p_168472_, Function<T, String> p_168473_) {
                    String s = this.getValueOrNull(p_168470_);
                    return s == null ? p_168471_ : p_168472_.apply(s);
                }
            });
            if (compoundtag1.contains("fullscreenResolution")) {
                this.fullscreenVideoModeString = compoundtag1.getString("fullscreenResolution");
            }

            if (this.minecraft.getWindow() != null) {
                this.minecraft.getWindow().setFramerateLimit((Integer)this.framerateLimit.get());
            }

            KeyMapping.resetMapping();
        } catch (Exception var8) {
            Exception exception = var8;
            LOGGER.error("Failed to load options", exception);
        }

    }

    static boolean isTrue(String p_168436_) {
        return "true".equals(p_168436_);
    }

    static boolean isFalse(String p_168441_) {
        return "false".equals(p_168441_);
    }

    private CompoundTag dataFix(CompoundTag p_92165_) {
        int i = 0;

        try {
            i = Integer.parseInt(p_92165_.getString("version"));
        } catch (RuntimeException var4) {
        }

        return DataFixTypes.OPTIONS.updateToCurrentVersion(this.minecraft.getFixerUpper(), p_92165_, i);
    }

    public void save() {
        try {
            final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));

            try {
                printwriter.println("version:" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());
                this.processOptions(new FieldAccess() {
                    public void writePrefix(String p_168491_) {
                        printwriter.print(p_168491_);
                        printwriter.print(':');
                    }

                    public <T> void process(String p_232135_, OptionInstance<T> p_232136_) {
                        DataResult<JsonElement> dataresult = p_232136_.codec().encodeStart(JsonOps.INSTANCE, p_232136_.get());
                        dataresult.error().ifPresent((p_232133_) -> {
                            Options.LOGGER.error("Error saving option " + p_232136_ + ": " + p_232133_);
                        });
                        dataresult.result().ifPresent((p_232140_) -> {
                            this.writePrefix(p_232135_);
                            printwriter.println(Options.GSON.toJson(p_232140_));
                        });
                    }

                    public int process(String p_168499_, int p_168500_) {
                        this.writePrefix(p_168499_);
                        printwriter.println(p_168500_);
                        return p_168500_;
                    }

                    public boolean process(String p_168515_, boolean p_168516_) {
                        this.writePrefix(p_168515_);
                        printwriter.println(p_168516_);
                        return p_168516_;
                    }

                    public String process(String p_168512_, String p_168513_) {
                        this.writePrefix(p_168512_);
                        printwriter.println(p_168513_);
                        return p_168513_;
                    }

                    public float process(String p_168496_, float p_168497_) {
                        this.writePrefix(p_168496_);
                        printwriter.println(p_168497_);
                        return p_168497_;
                    }

                    public <T> T process(String p_168502_, T p_168503_, Function<String, T> p_168504_, Function<T, String> p_168505_) {
                        this.writePrefix(p_168502_);
                        printwriter.println((String)p_168505_.apply(p_168503_));
                        return p_168503_;
                    }
                });
                if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
                    printwriter.println("fullscreenResolution:" + ((VideoMode)this.minecraft.getWindow().getPreferredFullscreenVideoMode().get()).write());
                }
            } catch (Throwable var5) {
                try {
                    printwriter.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }

            printwriter.close();
        } catch (Exception var6) {
            Exception exception = var6;
            LOGGER.error("Failed to save options", exception);
        }

        this.broadcastOptions();
    }

    public void broadcastOptions() {
        if (!ClientModLoader.isLoading()) {
            if (this.minecraft.player != null) {
                int i = 0;

                PlayerModelPart playermodelpart;
                for(Iterator var2 = this.modelParts.iterator(); var2.hasNext(); i |= playermodelpart.getMask()) {
                    playermodelpart = (PlayerModelPart)var2.next();
                }

                this.minecraft.player.connection.send((Packet)(new ServerboundClientInformationPacket(this.languageCode, (Integer)this.renderDistance.get(), (ChatVisiblity)this.chatVisibility.get(), (Boolean)this.chatColors.get(), i, (HumanoidArm)this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), (Boolean)this.allowServerListing.get())));
            }

        }
    }

    private void setModelPart(PlayerModelPart p_92155_, boolean p_92156_) {
        if (p_92156_) {
            this.modelParts.add(p_92155_);
        } else {
            this.modelParts.remove(p_92155_);
        }

    }

    public boolean isModelPartEnabled(PlayerModelPart p_168417_) {
        return this.modelParts.contains(p_168417_);
    }

    public void toggleModelPart(PlayerModelPart p_168419_, boolean p_168420_) {
        this.setModelPart(p_168419_, p_168420_);
        this.broadcastOptions();
    }

    public CloudStatus getCloudsType() {
        return this.getEffectiveRenderDistance() >= 4 ? (CloudStatus)this.cloudStatus.get() : CloudStatus.OFF;
    }

    public boolean useNativeTransport() {
        return this.useNativeTransport;
    }

    public void loadSelectedResourcePacks(PackRepository p_92146_) {
        Set<String> set = Sets.newLinkedHashSet();
        Iterator<String> iterator = this.resourcePacks.iterator();

        while(true) {
            while(iterator.hasNext()) {
                String s = (String)iterator.next();
                Pack pack = p_92146_.getPack(s);
                if (pack == null && !s.startsWith("file/")) {
                    pack = p_92146_.getPack("file/" + s);
                }

                if (pack == null) {
                    LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", s);
                    iterator.remove();
                } else if (!pack.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains(s)) {
                    LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", s);
                    iterator.remove();
                } else if (pack.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains(s)) {
                    LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", s);
                    this.incompatibleResourcePacks.remove(s);
                } else {
                    set.add(pack.getId());
                }
            }

            p_92146_.setSelected(set);
            return;
        }
    }

    private void setForgeKeybindProperties() {
        KeyConflictContext inGame = KeyConflictContext.IN_GAME;
        this.keyUp.setKeyConflictContext(inGame);
        this.keyLeft.setKeyConflictContext(inGame);
        this.keyDown.setKeyConflictContext(inGame);
        this.keyRight.setKeyConflictContext(inGame);
        this.keyJump.setKeyConflictContext(inGame);
        this.keyShift.setKeyConflictContext(inGame);
        this.keySprint.setKeyConflictContext(inGame);
        this.keyAttack.setKeyConflictContext(inGame);
        this.keyChat.setKeyConflictContext(inGame);
        this.keyPlayerList.setKeyConflictContext(inGame);
        this.keyCommand.setKeyConflictContext(inGame);
        this.keyTogglePerspective.setKeyConflictContext(inGame);
        this.keySmoothCamera.setKeyConflictContext(inGame);
    }

    public CameraType getCameraType() {
        return this.cameraType;
    }

    public void setCameraType(CameraType p_92158_) {
        this.cameraType = p_92158_;
    }

    private static List<String> readPackList(String p_168443_) {
        List<String> list = (List)GsonHelper.fromNullableJson(GSON, p_168443_, RESOURCE_PACK_TYPE);
        return (List)(list != null ? list : Lists.newArrayList());
    }

    public File getFile() {
        return this.optionsFile;
    }

    public String dumpOptionsForReport() {
        Stream<Pair<String, Object>> stream = Stream.builder().add(Pair.of("ao", this.ambientOcclusion.get())).add(Pair.of("biomeBlendRadius", this.biomeBlendRadius.get())).add(Pair.of("enableVsync", this.enableVsync.get())).add(Pair.of("entityDistanceScaling", this.entityDistanceScaling.get())).add(Pair.of("entityShadows", this.entityShadows.get())).add(Pair.of("forceUnicodeFont", this.forceUnicodeFont.get())).add(Pair.of("fov", this.fov.get())).add(Pair.of("fovEffectScale", this.fovEffectScale.get())).add(Pair.of("darknessEffectScale", this.darknessEffectScale.get())).add(Pair.of("glintSpeed", this.glintSpeed.get())).add(Pair.of("glintStrength", this.glintStrength.get())).add(Pair.of("prioritizeChunkUpdates", this.prioritizeChunkUpdates.get())).add(Pair.of("fullscreen", this.fullscreen.get())).add(Pair.of("fullscreenResolution", String.valueOf(this.fullscreenVideoModeString))).add(Pair.of("gamma", this.gamma.get())).add(Pair.of("glDebugVerbosity", this.glDebugVerbosity)).add(Pair.of("graphicsMode", this.graphicsMode.get())).add(Pair.of("guiScale", this.guiScale.get())).add(Pair.of("maxFps", this.framerateLimit.get())).add(Pair.of("mipmapLevels", this.mipmapLevels.get())).add(Pair.of("narrator", this.narrator.get())).add(Pair.of("overrideHeight", this.overrideHeight)).add(Pair.of("overrideWidth", this.overrideWidth)).add(Pair.of("particles", this.particles.get())).add(Pair.of("reducedDebugInfo", this.reducedDebugInfo.get())).add(Pair.of("renderClouds", this.cloudStatus.get())).add(Pair.of("renderDistance", this.renderDistance.get())).add(Pair.of("simulationDistance", this.simulationDistance.get())).add(Pair.of("resourcePacks", this.resourcePacks)).add(Pair.of("screenEffectScale", this.screenEffectScale.get())).add(Pair.of("syncChunkWrites", this.syncWrites)).add(Pair.of("useNativeTransport", this.useNativeTransport)).add(Pair.of("soundDevice", this.soundDevice.get())).build();
        return (String)stream.map((p_231848_) -> {
            String var10000 = (String)p_231848_.getFirst();
            return var10000 + ": " + p_231848_.getSecond();
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    public void setServerRenderDistance(int p_193771_) {
        this.serverRenderDistance = p_193771_;
    }

    public int getEffectiveRenderDistance() {
        return this.serverRenderDistance > 0 ? Math.min((Integer)this.renderDistance.get(), this.serverRenderDistance) : (Integer)this.renderDistance.get();
    }

    private static Component pixelValueLabel(Component p_231953_, int p_231954_) {
        return Component.translatable("options.pixel_value", p_231953_, p_231954_);
    }

    private static Component percentValueLabel(Component p_231898_, double p_231899_) {
        return Component.translatable("options.percent_value", p_231898_, (int)(p_231899_ * 100.0));
    }

    public static Component genericValueLabel(Component p_231922_, Component p_231923_) {
        return Component.translatable("options.generic_value", p_231922_, p_231923_);
    }

    public static Component genericValueLabel(Component p_231901_, int p_231902_) {
        return genericValueLabel(p_231901_, Component.literal(Integer.toString(p_231902_)));
    }

    static {
        GRAPHICS_TOOLTIP_FABULOUS = Component.translatable("options.graphics.fabulous.tooltip", Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC));
        GRAPHICS_TOOLTIP_FANCY = Component.translatable("options.graphics.fancy.tooltip");
        PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
        PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
        PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
        ACCESSIBILITY_TOOLTIP_CONTRAST_MODE = Component.translatable("options.accessibility.high_contrast.tooltip");
        ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME = Component.translatable("options.notifications.display_time.tooltip");
        ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
        DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
        DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
        MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
        MOVEMENT_HOLD = Component.translatable("options.key.hold");
        CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
        CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
        TELEMETRY_TOOLTIP = Component.translatable("options.telemetry.button.tooltip", Component.translatable("options.telemetry.state.minimal"), Component.translatable("options.telemetry.state.all"));
        ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
        ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
        ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
        ACCESSIBILITY_TOOLTIP_GLINT_SPEED = Component.translatable("options.glintSpeed.tooltip");
        ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH = Component.translatable("options.glintStrength.tooltip");
        ACCESSIBILITY_TOOLTIP_DAMAGE_TILT_STRENGTH = Component.translatable("options.damageTiltStrength.tooltip");
    }

    @OnlyIn(Dist.CLIENT)
    public interface FieldAccess {
        <T> void process(String var1, OptionInstance<T> var2);

        int process(String var1, int var2);

        boolean process(String var1, boolean var2);

        String process(String var1, String var2);

        float process(String var1, float var2);

        <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
    }
}
