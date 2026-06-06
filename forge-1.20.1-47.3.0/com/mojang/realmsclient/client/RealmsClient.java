//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsClient {
    public static Environment currentEnvironment;
    private static boolean initialized;
    private static final Logger LOGGER;
    private final String sessionId;
    private final String username;
    private final Minecraft minecraft;
    private static final String WORLDS_RESOURCE_PATH = "worlds";
    private static final String INVITES_RESOURCE_PATH = "invites";
    private static final String MCO_RESOURCE_PATH = "mco";
    private static final String SUBSCRIPTION_RESOURCE = "subscriptions";
    private static final String ACTIVITIES_RESOURCE = "activities";
    private static final String OPS_RESOURCE = "ops";
    private static final String REGIONS_RESOURCE = "regions/ping/stat";
    private static final String TRIALS_RESOURCE = "trial";
    private static final String NOTIFICATIONS_RESOURCE = "notifications";
    private static final String PATH_INITIALIZE = "/$WORLD_ID/initialize";
    private static final String PATH_GET_ACTIVTIES = "/$WORLD_ID";
    private static final String PATH_GET_LIVESTATS = "/liveplayerlist";
    private static final String PATH_GET_SUBSCRIPTION = "/$WORLD_ID";
    private static final String PATH_OP = "/$WORLD_ID/$PROFILE_UUID";
    private static final String PATH_PUT_INTO_MINIGAMES_MODE = "/minigames/$MINIGAME_ID/$WORLD_ID";
    private static final String PATH_AVAILABLE = "/available";
    private static final String PATH_TEMPLATES = "/templates/$WORLD_TYPE";
    private static final String PATH_WORLD_JOIN = "/v1/$ID/join/pc";
    private static final String PATH_WORLD_GET = "/$ID";
    private static final String PATH_WORLD_INVITES = "/$WORLD_ID";
    private static final String PATH_WORLD_UNINVITE = "/$WORLD_ID/invite/$UUID";
    private static final String PATH_PENDING_INVITES_COUNT = "/count/pending";
    private static final String PATH_PENDING_INVITES = "/pending";
    private static final String PATH_ACCEPT_INVITE = "/accept/$INVITATION_ID";
    private static final String PATH_REJECT_INVITE = "/reject/$INVITATION_ID";
    private static final String PATH_UNINVITE_MYSELF = "/$WORLD_ID";
    private static final String PATH_WORLD_UPDATE = "/$WORLD_ID";
    private static final String PATH_SLOT = "/$WORLD_ID/slot/$SLOT_ID";
    private static final String PATH_WORLD_OPEN = "/$WORLD_ID/open";
    private static final String PATH_WORLD_CLOSE = "/$WORLD_ID/close";
    private static final String PATH_WORLD_RESET = "/$WORLD_ID/reset";
    private static final String PATH_DELETE_WORLD = "/$WORLD_ID";
    private static final String PATH_WORLD_BACKUPS = "/$WORLD_ID/backups";
    private static final String PATH_WORLD_DOWNLOAD = "/$WORLD_ID/slot/$SLOT_ID/download";
    private static final String PATH_WORLD_UPLOAD = "/$WORLD_ID/backups/upload";
    private static final String PATH_CLIENT_COMPATIBLE = "/client/compatible";
    private static final String PATH_TOS_AGREED = "/tos/agreed";
    private static final String PATH_NEWS = "/v1/news";
    private static final String PATH_MARK_NOTIFICATIONS_SEEN = "/seen";
    private static final String PATH_DISMISS_NOTIFICATIONS = "/dismiss";
    private static final String PATH_STAGE_AVAILABLE = "/stageAvailable";
    private static final GuardedSerializer GSON;

    public static RealmsClient create() {
        Minecraft $$0 = Minecraft.getInstance();
        return create($$0);
    }

    public static RealmsClient create(Minecraft p_239152_) {
        String $$1 = p_239152_.getUser().getName();
        String $$2 = p_239152_.getUser().getSessionId();
        if (!initialized) {
            initialized = true;
            Optional<String> $$3 = Optional.ofNullable(System.getenv("realms.environment")).or(() -> {
                return Optional.ofNullable(System.getProperty("realms.environment"));
            });
            $$3.flatMap(Environment::byName).ifPresent((p_289648_) -> {
                currentEnvironment = p_289648_;
            });
        }

        return new RealmsClient($$2, $$1, p_239152_);
    }

    public static void switchToStage() {
        currentEnvironment = com.mojang.realmsclient.client.RealmsClient.Environment.STAGE;
    }

    public static void switchToProd() {
        currentEnvironment = com.mojang.realmsclient.client.RealmsClient.Environment.PRODUCTION;
    }

    public static void switchToLocal() {
        currentEnvironment = com.mojang.realmsclient.client.RealmsClient.Environment.LOCAL;
    }

    public RealmsClient(String p_87166_, String p_87167_, Minecraft p_87168_) {
        this.sessionId = p_87166_;
        this.username = p_87167_;
        this.minecraft = p_87168_;
        RealmsClientConfig.setProxy(p_87168_.getProxy());
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        String $$0 = this.url("worlds");
        String $$1 = this.execute(Request.get($$0));
        return RealmsServerList.parse($$1);
    }

    public List<RealmsNotification> getNotifications() throws RealmsServiceException {
        String $$0 = this.url("notifications");
        String $$1 = this.execute(Request.get($$0));
        List<RealmsNotification> $$2 = RealmsNotification.parseList($$1);
        return $$2.size() > 1 ? List.of((RealmsNotification)$$2.get(0)) : $$2;
    }

    private static JsonArray uuidListToJsonArray(List<UUID> p_275393_) {
        JsonArray $$1 = new JsonArray();
        Iterator var2 = p_275393_.iterator();

        while(var2.hasNext()) {
            UUID $$2 = (UUID)var2.next();
            if ($$2 != null) {
                $$1.add($$2.toString());
            }
        }

        return $$1;
    }

    public void notificationsSeen(List<UUID> p_275212_) throws RealmsServiceException {
        String $$1 = this.url("notifications/seen");
        this.execute(Request.post($$1, GSON.toJson((JsonElement)uuidListToJsonArray(p_275212_))));
    }

    public void notificationsDismiss(List<UUID> p_275407_) throws RealmsServiceException {
        String $$1 = this.url("notifications/dismiss");
        this.execute(Request.post($$1, GSON.toJson((JsonElement)uuidListToJsonArray(p_275407_))));
    }

    public RealmsServer getOwnWorld(long p_87175_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(p_87175_)));
        String $$2 = this.execute(Request.get($$1));
        return RealmsServer.parse($$2);
    }

    public ServerActivityList getActivity(long p_167279_) throws RealmsServiceException {
        String $$1 = this.url("activities" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_167279_)));
        String $$2 = this.execute(Request.get($$1));
        return ServerActivityList.parse($$2);
    }

    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        String $$0 = this.url("activities/liveplayerlist");
        String $$1 = this.execute(Request.get($$0));
        return RealmsServerPlayerLists.parse($$1);
    }

    public RealmsServerAddress join(long p_87208_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + p_87208_));
        String $$2 = this.execute(Request.get($$1, 5000, 30000));
        return RealmsServerAddress.parse($$2);
    }

    public void initializeWorld(long p_87192_, String p_87193_, String p_87194_) throws RealmsServiceException {
        RealmsDescriptionDto $$3 = new RealmsDescriptionDto(p_87193_, p_87194_);
        String $$4 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(p_87192_)));
        String $$5 = GSON.toJson((ReflectionBasedSerialization)$$3);
        this.execute(Request.post($$4, $$5, 5000, 10000));
    }

    public Boolean mcoEnabled() throws RealmsServiceException {
        String $$0 = this.url("mco/available");
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf($$1);
    }

    public Boolean stageAvailable() throws RealmsServiceException {
        String $$0 = this.url("mco/stageAvailable");
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf($$1);
    }

    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        String $$0 = this.url("mco/client/compatible");
        String $$1 = this.execute(Request.get($$0));

        try {
            CompatibleVersionResponse $$4 = com.mojang.realmsclient.client.RealmsClient.CompatibleVersionResponse.valueOf($$1);
            return $$4;
        } catch (IllegalArgumentException var5) {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + $$1);
        }
    }

    public void uninvite(long p_87184_, String p_87185_) throws RealmsServiceException {
        String $$2 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(p_87184_)).replace("$UUID", p_87185_));
        this.execute(Request.delete($$2));
    }

    public void uninviteMyselfFrom(long p_87223_) throws RealmsServiceException {
        String $$1 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87223_)));
        this.execute(Request.delete($$1));
    }

    public RealmsServer invite(long p_87213_, String p_87214_) throws RealmsServiceException {
        PlayerInfo $$2 = new PlayerInfo();
        $$2.setName(p_87214_);
        String $$3 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87213_)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson((ReflectionBasedSerialization)$$2)));
        return RealmsServer.parse($$4);
    }

    public BackupList backupsFor(long p_87231_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_87231_)));
        String $$2 = this.execute(Request.get($$1));
        return BackupList.parse($$2);
    }

    public void update(long p_87216_, String p_87217_, String p_87218_) throws RealmsServiceException {
        RealmsDescriptionDto $$3 = new RealmsDescriptionDto(p_87217_, p_87218_);
        String $$4 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87216_)));
        this.execute(Request.post($$4, GSON.toJson((ReflectionBasedSerialization)$$3)));
    }

    public void updateSlot(long p_87180_, int p_87181_, RealmsWorldOptions p_87182_) throws RealmsServiceException {
        String $$3 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_87180_)).replace("$SLOT_ID", String.valueOf(p_87181_)));
        String $$4 = p_87182_.toJson();
        this.execute(Request.post($$3, $$4));
    }

    public boolean switchSlot(long p_87177_, int p_87178_) throws RealmsServiceException {
        String $$2 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(p_87177_)).replace("$SLOT_ID", String.valueOf(p_87178_)));
        String $$3 = this.execute(Request.put($$2, ""));
        return Boolean.valueOf($$3);
    }

    public void restoreWorld(long p_87225_, String p_87226_) throws RealmsServiceException {
        String $$2 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(p_87225_)), "backupId=" + p_87226_);
        this.execute(Request.put($$2, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int p_87171_, int p_87172_, RealmsServer.WorldType p_87173_) throws RealmsServiceException {
        String $$3 = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", p_87173_.toString()), String.format(Locale.ROOT, "page=%d&pageSize=%d", p_87171_, p_87172_));
        String $$4 = this.execute(Request.get($$3));
        return WorldTemplatePaginatedList.parse($$4);
    }

    public Boolean putIntoMinigameMode(long p_87233_, String p_87234_) throws RealmsServiceException {
        String $$2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", p_87234_).replace("$WORLD_ID", String.valueOf(p_87233_));
        String $$3 = this.url("worlds" + $$2);
        return Boolean.valueOf(this.execute(Request.put($$3, "")));
    }

    public Ops op(long p_87239_, String p_87240_) throws RealmsServiceException {
        String $$2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_87239_)).replace("$PROFILE_UUID", p_87240_);
        String $$3 = this.url("ops" + $$2);
        return Ops.parse(this.execute(Request.post($$3, "")));
    }

    public Ops deop(long p_87245_, String p_87246_) throws RealmsServiceException {
        String $$2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(p_87245_)).replace("$PROFILE_UUID", p_87246_);
        String $$3 = this.url("ops" + $$2);
        return Ops.parse(this.execute(Request.delete($$3)));
    }

    public Boolean open(long p_87237_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(p_87237_)));
        String $$2 = this.execute(Request.put($$1, ""));
        return Boolean.valueOf($$2);
    }

    public Boolean close(long p_87243_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(p_87243_)));
        String $$2 = this.execute(Request.put($$1, ""));
        return Boolean.valueOf($$2);
    }

    public Boolean resetWorldWithSeed(long p_167276_, WorldGenerationInfo p_167277_) throws RealmsServiceException {
        RealmsWorldResetDto $$2 = new RealmsWorldResetDto(p_167277_.getSeed(), -1L, p_167277_.getLevelType().getDtoIndex(), p_167277_.shouldGenerateStructures());
        String $$3 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_167276_)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson((ReflectionBasedSerialization)$$2), 30000, 80000));
        return Boolean.valueOf($$4);
    }

    public Boolean resetWorldWithTemplate(long p_87251_, String p_87252_) throws RealmsServiceException {
        RealmsWorldResetDto $$2 = new RealmsWorldResetDto((String)null, Long.valueOf(p_87252_), -1, false);
        String $$3 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(p_87251_)));
        String $$4 = this.execute(Request.post($$3, GSON.toJson((ReflectionBasedSerialization)$$2), 30000, 80000));
        return Boolean.valueOf($$4);
    }

    public Subscription subscriptionFor(long p_87249_) throws RealmsServiceException {
        String $$1 = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87249_)));
        String $$2 = this.execute(Request.get($$1));
        return Subscription.parse($$2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        return this.pendingInvites().pendingInvites.size();
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String $$0 = this.url("invites/pending");
        String $$1 = this.execute(Request.get($$0));
        PendingInvitesList $$2 = PendingInvitesList.parse($$1);
        $$2.pendingInvites.removeIf(this::isBlocked);
        return $$2;
    }

    private boolean isBlocked(PendingInvite p_87198_) {
        try {
            UUID $$1 = UUID.fromString(p_87198_.worldOwnerUuid);
            return this.minecraft.getPlayerSocialManager().isBlocked($$1);
        } catch (IllegalArgumentException var3) {
            return false;
        }
    }

    public void acceptInvitation(String p_87202_) throws RealmsServiceException {
        String $$1 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", p_87202_));
        this.execute(Request.put($$1, ""));
    }

    public WorldDownload requestDownloadInfo(long p_87210_, int p_87211_) throws RealmsServiceException {
        String $$2 = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(p_87210_)).replace("$SLOT_ID", String.valueOf(p_87211_)));
        String $$3 = this.execute(Request.get($$2));
        return WorldDownload.parse($$3);
    }

    @Nullable
    public UploadInfo requestUploadInfo(long p_87257_, @Nullable String p_87258_) throws RealmsServiceException {
        String $$2 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(p_87257_)));
        return UploadInfo.parse(this.execute(Request.put($$2, UploadInfo.createRequest(p_87258_))));
    }

    public void rejectInvitation(String p_87220_) throws RealmsServiceException {
        String $$1 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", p_87220_));
        this.execute(Request.put($$1, ""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String $$0 = this.url("mco/tos/agreed");
        this.execute(Request.post($$0, ""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String $$0 = this.url("mco/v1/news");
        String $$1 = this.execute(Request.get($$0, 5000, 10000));
        return RealmsNews.parse($$1);
    }

    public void sendPingResults(PingResult p_87200_) throws RealmsServiceException {
        String $$1 = this.url("regions/ping/stat");
        this.execute(Request.post($$1, GSON.toJson((ReflectionBasedSerialization)p_87200_)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String $$0 = this.url("trial");
        String $$1 = this.execute(Request.get($$0));
        return Boolean.valueOf($$1);
    }

    public void deleteWorld(long p_87255_) throws RealmsServiceException {
        String $$1 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(p_87255_)));
        this.execute(Request.delete($$1));
    }

    private String url(String p_87228_) {
        return this.url(p_87228_, (String)null);
    }

    private String url(String p_87204_, @Nullable String p_87205_) {
        try {
            return (new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + p_87204_, p_87205_, (String)null)).toASCIIString();
        } catch (URISyntaxException var4) {
            URISyntaxException $$2 = var4;
            throw new IllegalArgumentException(p_87204_, $$2);
        }
    }

    private String execute(Request<?> p_87196_) throws RealmsServiceException {
        p_87196_.cookie("sid", this.sessionId);
        p_87196_.cookie("user", this.username);
        p_87196_.cookie("version", SharedConstants.getCurrentVersion().getName());

        try {
            int $$1 = p_87196_.responseCode();
            if ($$1 != 503 && $$1 != 277) {
                String $$3 = p_87196_.text();
                if ($$1 >= 200 && $$1 < 300) {
                    return $$3;
                } else if ($$1 == 401) {
                    String $$4 = p_87196_.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: {}", $$4);
                    throw new RealmsServiceException($$1, $$4);
                } else {
                    RealmsError $$5 = RealmsError.parse($$3);
                    if ($$5 != null) {
                        LOGGER.error("Realms http code: {} -  error code: {} -  message: {} - raw body: {}", new Object[]{$$1, $$5.getErrorCode(), $$5.getErrorMessage(), $$3});
                        throw new RealmsServiceException($$1, $$3, $$5);
                    } else {
                        LOGGER.error("Realms http code: {} - raw body (message failed to parse): {}", $$1, $$3);
                        String $$6 = getHttpCodeDescription($$1);
                        throw new RealmsServiceException($$1, $$6);
                    }
                }
            } else {
                int $$2 = p_87196_.getRetryAfterHeader();
                throw new RetryCallException($$2, $$1);
            }
        } catch (RealmsHttpException var6) {
            RealmsHttpException $$7 = var6;
            throw new RealmsServiceException(500, "Could not connect to Realms: " + $$7.getMessage());
        }
    }

    private static String getHttpCodeDescription(int p_200937_) {
        String var10000;
        switch (p_200937_) {
            case 429 -> var10000 = I18n.get("mco.errorMessage.serviceBusy");
            default -> var10000 = "Unknown error";
        }

        return var10000;
    }

    static {
        currentEnvironment = com.mojang.realmsclient.client.RealmsClient.Environment.PRODUCTION;
        LOGGER = LogUtils.getLogger();
        GSON = new GuardedSerializer();
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Environment {
        PRODUCTION("pc.realms.minecraft.net", "https"),
        STAGE("pc-stage.realms.minecraft.net", "https"),
        LOCAL("localhost:8080", "http");

        public String baseUrl;
        public String protocol;

        private Environment(String p_87286_, String p_87287_) {
            this.baseUrl = p_87286_;
            this.protocol = p_87287_;
        }

        public static Optional<Environment> byName(String p_289688_) {
            Optional var10000;
            switch (p_289688_.toLowerCase(Locale.ROOT)) {
                case "production" -> var10000 = Optional.of(PRODUCTION);
                case "local" -> var10000 = Optional.of(LOCAL);
                case "stage" -> var10000 = Optional.of(STAGE);
                default -> var10000 = Optional.empty();
            }

            return var10000;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum CompatibleVersionResponse {
        COMPATIBLE,
        OUTDATED,
        OTHER;

        private CompatibleVersionResponse() {
        }
    }
}
