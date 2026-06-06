//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class TextFilterClient implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
    private static final ThreadFactory THREAD_FACTORY = (p_10148_) -> {
        Thread $$1 = new Thread(p_10148_);
        $$1.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
        return $$1;
    };
    private static final String DEFAULT_ENDPOINT = "v1/chat";
    private final URL chatEndpoint;
    private final MessageEncoder chatEncoder;
    final URL joinEndpoint;
    final JoinOrLeaveEncoder joinEncoder;
    final URL leaveEndpoint;
    final JoinOrLeaveEncoder leaveEncoder;
    private final String authKey;
    final IgnoreStrategy chatIgnoreStrategy;
    final ExecutorService workerPool;

    private TextFilterClient(URL p_215275_, MessageEncoder p_215276_, URL p_215277_, JoinOrLeaveEncoder p_215278_, URL p_215279_, JoinOrLeaveEncoder p_215280_, String p_215281_, IgnoreStrategy p_215282_, int p_215283_) {
        this.authKey = p_215281_;
        this.chatIgnoreStrategy = p_215282_;
        this.chatEndpoint = p_215275_;
        this.chatEncoder = p_215276_;
        this.joinEndpoint = p_215277_;
        this.joinEncoder = p_215278_;
        this.leaveEndpoint = p_215279_;
        this.leaveEncoder = p_215280_;
        this.workerPool = Executors.newFixedThreadPool(p_215283_, THREAD_FACTORY);
    }

    private static URL getEndpoint(URI p_212246_, @Nullable JsonObject p_212247_, String p_212248_, String p_212249_) throws MalformedURLException {
        String $$4 = getEndpointFromConfig(p_212247_, p_212248_, p_212249_);
        return p_212246_.resolve("/" + $$4).toURL();
    }

    private static String getEndpointFromConfig(@Nullable JsonObject p_215295_, String p_215296_, String p_215297_) {
        return p_215295_ != null ? GsonHelper.getAsString(p_215295_, p_215296_, p_215297_) : p_215297_;
    }

    @Nullable
    public static TextFilterClient createFromConfig(String p_143737_) {
        if (Strings.isNullOrEmpty(p_143737_)) {
            return null;
        } else {
            try {
                JsonObject $$1 = GsonHelper.parse(p_143737_);
                URI $$2 = new URI(GsonHelper.getAsString($$1, "apiServer"));
                String $$3 = GsonHelper.getAsString($$1, "apiKey");
                if ($$3.isEmpty()) {
                    throw new IllegalArgumentException("Missing API key");
                } else {
                    int $$4 = GsonHelper.getAsInt($$1, "ruleId", 1);
                    String $$5 = GsonHelper.getAsString($$1, "serverId", "");
                    String $$6 = GsonHelper.getAsString($$1, "roomId", "Java:Chat");
                    int $$7 = GsonHelper.getAsInt($$1, "hashesToDrop", -1);
                    int $$8 = GsonHelper.getAsInt($$1, "maxConcurrentRequests", 7);
                    JsonObject $$9 = GsonHelper.getAsJsonObject($$1, "endpoints", (JsonObject)null);
                    String $$10 = getEndpointFromConfig($$9, "chat", "v1/chat");
                    boolean $$11 = $$10.equals("v1/chat");
                    URL $$12 = $$2.resolve("/" + $$10).toURL();
                    URL $$13 = getEndpoint($$2, $$9, "join", "v1/join");
                    URL $$14 = getEndpoint($$2, $$9, "leave", "v1/leave");
                    JoinOrLeaveEncoder $$15 = (p_215310_) -> {
                        JsonObject $$3 = new JsonObject();
                        $$3.addProperty("server", $$5);
                        $$3.addProperty("room", $$6);
                        $$3.addProperty("user_id", p_215310_.getId().toString());
                        $$3.addProperty("user_display_name", p_215310_.getName());
                        return $$3;
                    };
                    MessageEncoder $$18;
                    if ($$11) {
                        $$18 = (p_238214_, p_238215_) -> {
                            JsonObject $$5x = new JsonObject();
                            $$5x.addProperty("rule", $$4);
                            $$5x.addProperty("server", $$5);
                            $$5x.addProperty("room", $$6);
                            $$5x.addProperty("player", p_238214_.getId().toString());
                            $$5x.addProperty("player_display_name", p_238214_.getName());
                            $$5x.addProperty("text", p_238215_);
                            $$5x.addProperty("language", "*");
                            return $$5x;
                        };
                    } else {
                        String $$17 = String.valueOf($$4);
                        $$18 = (p_238220_, p_238221_) -> {
                            JsonObject $$5x = new JsonObject();
                            $$5x.addProperty("rule_id", $$17);
                            $$5x.addProperty("category", $$5);
                            $$5x.addProperty("subcategory", $$6);
                            $$5x.addProperty("user_id", p_238220_.getId().toString());
                            $$5x.addProperty("user_display_name", p_238220_.getName());
                            $$5x.addProperty("text", p_238221_);
                            $$5x.addProperty("language", "*");
                            return $$5x;
                        };
                    }

                    IgnoreStrategy $$19 = net.minecraft.server.network.TextFilterClient.IgnoreStrategy.select($$7);
                    String $$20 = Base64.getEncoder().encodeToString($$3.getBytes(StandardCharsets.US_ASCII));
                    return new TextFilterClient($$12, $$18, $$13, $$15, $$14, $$15, $$20, $$19, $$8);
                }
            } catch (Exception var19) {
                Exception $$21 = var19;
                LOGGER.warn("Failed to parse chat filter config {}", p_143737_, $$21);
                return null;
            }
        }
    }

    void processJoinOrLeave(GameProfile p_215303_, URL p_215304_, JoinOrLeaveEncoder p_215305_, Executor p_215306_) {
        p_215306_.execute(() -> {
            JsonObject $$3 = p_215305_.encode(p_215303_);

            try {
                this.processRequest($$3, p_215304_);
            } catch (Exception var6) {
                Exception $$4 = var6;
                LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{p_215304_, p_215303_, $$4});
            }

        });
    }

    CompletableFuture<FilteredText> requestMessageProcessing(GameProfile p_10137_, String p_10138_, IgnoreStrategy p_10139_, Executor p_10140_) {
        return p_10138_.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
            JsonObject $$3 = this.chatEncoder.encode(p_10137_, p_10138_);

            try {
                JsonObject $$4 = this.processRequestResponse($$3, this.chatEndpoint);
                boolean $$5 = GsonHelper.getAsBoolean($$4, "response", false);
                if ($$5) {
                    return FilteredText.passThrough(p_10138_);
                } else {
                    String $$6 = GsonHelper.getAsString($$4, "hashed", (String)null);
                    if ($$6 == null) {
                        return FilteredText.fullyFiltered(p_10138_);
                    } else {
                        JsonArray $$7 = GsonHelper.getAsJsonArray($$4, "hashes");
                        FilterMask $$8 = this.parseMask(p_10138_, $$7, p_10139_);
                        return new FilteredText(p_10138_, $$8);
                    }
                }
            } catch (Exception var10) {
                Exception $$9 = var10;
                LOGGER.warn("Failed to validate message '{}'", p_10138_, $$9);
                return FilteredText.fullyFiltered(p_10138_);
            }
        }, p_10140_);
    }

    private FilterMask parseMask(String p_243283_, JsonArray p_243222_, IgnoreStrategy p_243237_) {
        if (p_243222_.isEmpty()) {
            return FilterMask.PASS_THROUGH;
        } else if (p_243237_.shouldIgnore(p_243283_, p_243222_.size())) {
            return FilterMask.FULLY_FILTERED;
        } else {
            FilterMask $$3 = new FilterMask(p_243283_.length());

            for(int $$4 = 0; $$4 < p_243222_.size(); ++$$4) {
                $$3.setFiltered(p_243222_.get($$4).getAsInt());
            }

            return $$3;
        }
    }

    public void close() {
        this.workerPool.shutdownNow();
    }

    private void drainStream(InputStream p_10146_) throws IOException {
        byte[] $$1 = new byte[1024];

        while(p_10146_.read($$1) != -1) {
        }

    }

    private JsonObject processRequestResponse(JsonObject p_10128_, URL p_10129_) throws IOException {
        HttpURLConnection $$2 = this.makeRequest(p_10128_, p_10129_);
        InputStream $$3 = $$2.getInputStream();

        JsonObject var5;
        label90: {
            try {
                if ($$2.getResponseCode() == 204) {
                    var5 = new JsonObject();
                    break label90;
                }

                try {
                    var5 = Streams.parse(new JsonReader(new InputStreamReader($$3, StandardCharsets.UTF_8))).getAsJsonObject();
                } finally {
                    this.drainStream($$3);
                }
            } catch (Throwable var12) {
                if ($$3 != null) {
                    try {
                        $$3.close();
                    } catch (Throwable var10) {
                        var12.addSuppressed(var10);
                    }
                }

                throw var12;
            }

            if ($$3 != null) {
                $$3.close();
            }

            return var5;
        }

        if ($$3 != null) {
            $$3.close();
        }

        return var5;
    }

    private void processRequest(JsonObject p_10152_, URL p_10153_) throws IOException {
        HttpURLConnection $$2 = this.makeRequest(p_10152_, p_10153_);
        InputStream $$3 = $$2.getInputStream();

        try {
            this.drainStream($$3);
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

    }

    private HttpURLConnection makeRequest(JsonObject p_10157_, URL p_10158_) throws IOException {
        HttpURLConnection $$2 = (HttpURLConnection)p_10158_.openConnection();
        $$2.setConnectTimeout(15000);
        $$2.setReadTimeout(2000);
        $$2.setUseCaches(false);
        $$2.setDoOutput(true);
        $$2.setDoInput(true);
        $$2.setRequestMethod("POST");
        $$2.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        $$2.setRequestProperty("Accept", "application/json");
        $$2.setRequestProperty("Authorization", "Basic " + this.authKey);
        $$2.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
        OutputStreamWriter $$3 = new OutputStreamWriter($$2.getOutputStream(), StandardCharsets.UTF_8);

        try {
            JsonWriter $$4 = new JsonWriter($$3);

            try {
                Streams.write(p_10157_, $$4);
            } catch (Throwable var10) {
                try {
                    $$4.close();
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }

                throw var10;
            }

            $$4.close();
        } catch (Throwable var11) {
            try {
                $$3.close();
            } catch (Throwable var8) {
                var11.addSuppressed(var8);
            }

            throw var11;
        }

        $$3.close();
        int $$5 = $$2.getResponseCode();
        if ($$5 >= 200 && $$5 < 300) {
            return $$2;
        } else {
            throw new RequestFailedException("" + $$5 + " " + $$2.getResponseMessage());
        }
    }

    public TextFilter createContext(GameProfile p_10135_) {
        return new PlayerContext(p_10135_);
    }

    @FunctionalInterface
    public interface IgnoreStrategy {
        IgnoreStrategy NEVER_IGNORE = (p_10169_, p_10170_) -> {
            return false;
        };
        IgnoreStrategy IGNORE_FULLY_FILTERED = (p_10166_, p_10167_) -> {
            return p_10166_.length() == p_10167_;
        };

        static IgnoreStrategy ignoreOverThreshold(int p_143739_) {
            return (p_143742_, p_143743_) -> {
                return p_143743_ >= p_143739_;
            };
        }

        static IgnoreStrategy select(int p_143745_) {
            IgnoreStrategy var10000;
            switch (p_143745_) {
                case -1 -> var10000 = NEVER_IGNORE;
                case 0 -> var10000 = IGNORE_FULLY_FILTERED;
                default -> var10000 = ignoreOverThreshold(p_143745_);
            }

            return var10000;
        }

        boolean shouldIgnore(String var1, int var2);
    }

    @FunctionalInterface
    interface MessageEncoder {
        JsonObject encode(GameProfile var1, String var2);
    }

    @FunctionalInterface
    private interface JoinOrLeaveEncoder {
        JsonObject encode(GameProfile var1);
    }

    public static class RequestFailedException extends RuntimeException {
        RequestFailedException(String p_10199_) {
            super(p_10199_);
        }
    }

    class PlayerContext implements TextFilter {
        private final GameProfile profile;
        private final Executor streamExecutor;

        PlayerContext(GameProfile p_10179_) {
            this.profile = p_10179_;
            ProcessorMailbox<Runnable> $$1 = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + p_10179_.getName());
            Objects.requireNonNull($$1);
            this.streamExecutor = $$1::tell;
        }

        public void join() {
            TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, TextFilterClient.this.joinEncoder, this.streamExecutor);
        }

        public void leave() {
            TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, TextFilterClient.this.leaveEncoder, this.streamExecutor);
        }

        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> p_10190_) {
            List<CompletableFuture<FilteredText>> $$1 = (List)p_10190_.stream().map((p_10195_) -> {
                return TextFilterClient.this.requestMessageProcessing(this.profile, p_10195_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
            }).collect(ImmutableList.toImmutableList());
            return Util.sequenceFailFast($$1).exceptionally((p_143747_) -> {
                return ImmutableList.of();
            });
        }

        public CompletableFuture<FilteredText> processStreamMessage(String p_10186_) {
            return TextFilterClient.this.requestMessageProcessing(this.profile, p_10186_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
        }
    }
}
