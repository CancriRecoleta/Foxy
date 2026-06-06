//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.players;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import org.slf4j.Logger;

public class GameProfileCache {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int GAMEPROFILES_MRU_LIMIT = 1000;
    private static final int GAMEPROFILES_EXPIRATION_MONTHS = 1;
    private static boolean usesAuthentication;
    private final Map<String, GameProfileInfo> profilesByName = Maps.newConcurrentMap();
    private final Map<UUID, GameProfileInfo> profilesByUUID = Maps.newConcurrentMap();
    private final Map<String, CompletableFuture<Optional<GameProfile>>> requests = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepository;
    private final Gson gson = (new GsonBuilder()).create();
    private final File file;
    private final AtomicLong operationCount = new AtomicLong();
    @Nullable
    private Executor executor;

    public GameProfileCache(GameProfileRepository p_10974_, File p_10975_) {
        this.profileRepository = p_10974_;
        this.file = p_10975_;
        Lists.reverse(this.load()).forEach(this::safeAdd);
    }

    private void safeAdd(GameProfileInfo p_10980_) {
        GameProfile $$1 = p_10980_.getProfile();
        p_10980_.setLastAccess(this.getNextOperation());
        String $$2 = $$1.getName();
        if ($$2 != null) {
            this.profilesByName.put($$2.toLowerCase(Locale.ROOT), p_10980_);
        }

        UUID $$3 = $$1.getId();
        if ($$3 != null) {
            this.profilesByUUID.put($$3, p_10980_);
        }

    }

    private static Optional<GameProfile> lookupGameProfile(GameProfileRepository p_10994_, String p_10995_) {
        final AtomicReference<GameProfile> $$2 = new AtomicReference();
        ProfileLookupCallback $$3 = new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(GameProfile p_11017_) {
                $$2.set(p_11017_);
            }

            public void onProfileLookupFailed(GameProfile p_11014_, Exception p_11015_) {
                $$2.set((Object)null);
            }
        };
        p_10994_.findProfilesByNames(new String[]{p_10995_}, Agent.MINECRAFT, $$3);
        GameProfile $$4 = (GameProfile)$$2.get();
        if (!usesAuthentication() && $$4 == null) {
            UUID $$5 = UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID)null, p_10995_));
            return Optional.of(new GameProfile($$5, p_10995_));
        } else {
            return Optional.ofNullable($$4);
        }
    }

    public static void setUsesAuthentication(boolean p_11005_) {
        usesAuthentication = p_11005_;
    }

    private static boolean usesAuthentication() {
        return usesAuthentication;
    }

    public void add(GameProfile p_10992_) {
        Calendar $$1 = Calendar.getInstance();
        $$1.setTime(new Date());
        $$1.add(2, 1);
        Date $$2 = $$1.getTime();
        GameProfileInfo $$3 = new GameProfileInfo(p_10992_, $$2);
        this.safeAdd($$3);
        this.save();
    }

    private long getNextOperation() {
        return this.operationCount.incrementAndGet();
    }

    public Optional<GameProfile> get(String p_10997_) {
        String $$1 = p_10997_.toLowerCase(Locale.ROOT);
        GameProfileInfo $$2 = (GameProfileInfo)this.profilesByName.get($$1);
        boolean $$3 = false;
        if ($$2 != null && (new Date()).getTime() >= $$2.expirationDate.getTime()) {
            this.profilesByUUID.remove($$2.getProfile().getId());
            this.profilesByName.remove($$2.getProfile().getName().toLowerCase(Locale.ROOT));
            $$3 = true;
            $$2 = null;
        }

        Optional $$5;
        if ($$2 != null) {
            $$2.setLastAccess(this.getNextOperation());
            $$5 = Optional.of($$2.getProfile());
        } else {
            $$5 = lookupGameProfile(this.profileRepository, $$1);
            if ($$5.isPresent()) {
                this.add((GameProfile)$$5.get());
                $$3 = false;
            }
        }

        if ($$3) {
            this.save();
        }

        return $$5;
    }

    public void getAsync(String p_143968_, Consumer<Optional<GameProfile>> p_143969_) {
        if (this.executor == null) {
            throw new IllegalStateException("No executor");
        } else {
            CompletableFuture<Optional<GameProfile>> $$2 = (CompletableFuture)this.requests.get(p_143968_);
            if ($$2 != null) {
                this.requests.put(p_143968_, $$2.whenCompleteAsync((p_143984_, p_143985_) -> {
                    p_143969_.accept(p_143984_);
                }, this.executor));
            } else {
                this.requests.put(p_143968_, CompletableFuture.supplyAsync(() -> {
                    return this.get(p_143968_);
                }, Util.backgroundExecutor()).whenCompleteAsync((p_143965_, p_143966_) -> {
                    this.requests.remove(p_143968_);
                }, this.executor).whenCompleteAsync((p_143978_, p_143979_) -> {
                    p_143969_.accept(p_143978_);
                }, this.executor));
            }

        }
    }

    public Optional<GameProfile> get(UUID p_11003_) {
        GameProfileInfo $$1 = (GameProfileInfo)this.profilesByUUID.get(p_11003_);
        if ($$1 == null) {
            return Optional.empty();
        } else {
            $$1.setLastAccess(this.getNextOperation());
            return Optional.of($$1.getProfile());
        }
    }

    public void setExecutor(Executor p_143975_) {
        this.executor = p_143975_;
    }

    public void clearExecutor() {
        this.executor = null;
    }

    private static DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    }

    public List<GameProfileInfo> load() {
        List<GameProfileInfo> $$0 = Lists.newArrayList();

        try {
            Reader $$1 = Files.newReader(this.file, StandardCharsets.UTF_8);

            label54: {
                ArrayList var4;
                try {
                    JsonArray $$2 = (JsonArray)this.gson.fromJson($$1, JsonArray.class);
                    if ($$2 != null) {
                        DateFormat $$3 = createDateFormat();
                        $$2.forEach((p_143973_) -> {
                            Optional var10000 = readGameProfile(p_143973_, $$3);
                            Objects.requireNonNull($$0);
                            var10000.ifPresent($$0::add);
                        });
                        break label54;
                    }

                    var4 = $$0;
                } catch (Throwable var6) {
                    if ($$1 != null) {
                        try {
                            $$1.close();
                        } catch (Throwable var5) {
                            var6.addSuppressed(var5);
                        }
                    }

                    throw var6;
                }

                if ($$1 != null) {
                    $$1.close();
                }

                return var4;
            }

            if ($$1 != null) {
                $$1.close();
            }
        } catch (FileNotFoundException var7) {
        } catch (JsonParseException | IOException var8) {
            Exception $$4 = var8;
            LOGGER.warn("Failed to load profile cache {}", this.file, $$4);
        }

        return $$0;
    }

    public void save() {
        JsonArray $$0 = new JsonArray();
        DateFormat $$1 = createDateFormat();
        this.getTopMRUProfiles(1000).forEach((p_143962_) -> {
            $$0.add(writeGameProfile(p_143962_, $$1));
        });
        String $$2 = this.gson.toJson($$0);

        try {
            Writer $$3 = Files.newWriter(this.file, StandardCharsets.UTF_8);

            try {
                $$3.write($$2);
            } catch (Throwable var8) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if ($$3 != null) {
                $$3.close();
            }
        } catch (IOException var9) {
        }

    }

    private Stream<GameProfileInfo> getTopMRUProfiles(int p_10978_) {
        return ImmutableList.copyOf(this.profilesByUUID.values()).stream().sorted(Comparator.comparing(GameProfileInfo::getLastAccess).reversed()).limit((long)p_10978_);
    }

    private static JsonElement writeGameProfile(GameProfileInfo p_10982_, DateFormat p_10983_) {
        JsonObject $$2 = new JsonObject();
        $$2.addProperty("name", p_10982_.getProfile().getName());
        UUID $$3 = p_10982_.getProfile().getId();
        $$2.addProperty("uuid", $$3 == null ? "" : $$3.toString());
        $$2.addProperty("expiresOn", p_10983_.format(p_10982_.getExpirationDate()));
        return $$2;
    }

    private static Optional<GameProfileInfo> readGameProfile(JsonElement p_10989_, DateFormat p_10990_) {
        if (p_10989_.isJsonObject()) {
            JsonObject $$2 = p_10989_.getAsJsonObject();
            JsonElement $$3 = $$2.get("name");
            JsonElement $$4 = $$2.get("uuid");
            JsonElement $$5 = $$2.get("expiresOn");
            if ($$3 != null && $$4 != null) {
                String $$6 = $$4.getAsString();
                String $$7 = $$3.getAsString();
                Date $$8 = null;
                if ($$5 != null) {
                    try {
                        $$8 = p_10990_.parse($$5.getAsString());
                    } catch (ParseException var12) {
                    }
                }

                if ($$7 != null && $$6 != null && $$8 != null) {
                    UUID $$11;
                    try {
                        $$11 = UUID.fromString($$6);
                    } catch (Throwable var11) {
                        return Optional.empty();
                    }

                    return Optional.of(new GameProfileInfo(new GameProfile($$11, $$7), $$8));
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    static class GameProfileInfo {
        private final GameProfile profile;
        final Date expirationDate;
        private volatile long lastAccess;

        GameProfileInfo(GameProfile p_11022_, Date p_11023_) {
            this.profile = p_11022_;
            this.expirationDate = p_11023_;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccess(long p_11030_) {
            this.lastAccess = p_11030_;
        }

        public long getLastAccess() {
            return this.lastAccess;
        }
    }
}
