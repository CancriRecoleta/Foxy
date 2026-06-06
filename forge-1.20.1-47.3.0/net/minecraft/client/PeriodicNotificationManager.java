//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PeriodicNotificationManager extends SimplePreparableReloadListener<Map<String, List<Notification>>> implements AutoCloseable {
    private static final Codec<Map<String, List<Notification>>> CODEC;
    private static final Logger LOGGER;
    private final ResourceLocation notifications;
    private final Object2BooleanFunction<String> selector;
    @Nullable
    private java.util.Timer timer;
    @Nullable
    private NotificationTask notificationTask;

    public PeriodicNotificationManager(ResourceLocation p_205293_, Object2BooleanFunction<String> p_205294_) {
        this.notifications = p_205293_;
        this.selector = p_205294_;
    }

    protected Map<String, List<Notification>> prepare(ResourceManager p_205300_, ProfilerFiller p_205301_) {
        try {
            Reader $$2 = p_205300_.openAsReader(this.notifications);

            Map var4;
            try {
                var4 = (Map)CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader($$2)).result().orElseThrow();
            } catch (Throwable var7) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return var4;
        } catch (Exception var8) {
            Exception $$3 = var8;
            LOGGER.warn("Failed to load {}", this.notifications, $$3);
            return ImmutableMap.of();
        }
    }

    protected void apply(Map<String, List<Notification>> p_205318_, ResourceManager p_205319_, ProfilerFiller p_205320_) {
        List<Notification> $$3 = (List)p_205318_.entrySet().stream().filter((p_205316_) -> {
            return (Boolean)this.selector.apply((String)p_205316_.getKey());
        }).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if ($$3.isEmpty()) {
            this.stopTimer();
        } else if ($$3.stream().anyMatch((p_205326_) -> {
            return p_205326_.period == 0L;
        })) {
            Util.logAndPauseIfInIde("A periodic notification in " + this.notifications + " has a period of zero minutes");
            this.stopTimer();
        } else {
            long $$4 = this.calculateInitialDelay($$3);
            long $$5 = this.calculateOptimalPeriod($$3, $$4);
            if (this.timer == null) {
                this.timer = new java.util.Timer();
            }

            if (this.notificationTask == null) {
                this.notificationTask = new NotificationTask($$3, $$4, $$5);
            } else {
                this.notificationTask = this.notificationTask.reset($$3, $$5);
            }

            this.timer.scheduleAtFixedRate(this.notificationTask, TimeUnit.MINUTES.toMillis($$4), TimeUnit.MINUTES.toMillis($$5));
        }
    }

    public void close() {
        this.stopTimer();
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }

    }

    private long calculateOptimalPeriod(List<Notification> p_205313_, long p_205314_) {
        return p_205313_.stream().mapToLong((p_205298_) -> {
            long $$2 = p_205298_.delay - p_205314_;
            return LongMath.gcd($$2, p_205298_.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> {
            return new IllegalStateException("Empty notifications from: " + this.notifications);
        });
    }

    private long calculateInitialDelay(List<Notification> p_205311_) {
        return p_205311_.stream().mapToLong((p_205305_) -> {
            return p_205305_.delay;
        }).min().orElse(0L);
    }

    static {
        CODEC = Codec.unboundedMap(Codec.STRING, RecordCodecBuilder.create((p_205303_) -> {
            return p_205303_.group(Codec.LONG.optionalFieldOf("delay", 0L).forGetter(Notification::delay), Codec.LONG.fieldOf("period").forGetter(Notification::period), Codec.STRING.fieldOf("title").forGetter(Notification::title), Codec.STRING.fieldOf("message").forGetter(Notification::message)).apply(p_205303_, Notification::new);
        }).listOf());
        LOGGER = LogUtils.getLogger();
    }

    @OnlyIn(Dist.CLIENT)
    static class NotificationTask extends TimerTask {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final List<Notification> notifications;
        private final long period;
        private final AtomicLong elapsed;

        public NotificationTask(List<Notification> p_205350_, long p_205351_, long p_205352_) {
            this.notifications = p_205350_;
            this.period = p_205352_;
            this.elapsed = new AtomicLong(p_205351_);
        }

        public NotificationTask reset(List<Notification> p_205357_, long p_205358_) {
            this.cancel();
            return new NotificationTask(p_205357_, this.elapsed.get(), p_205358_);
        }

        public void run() {
            long $$0 = this.elapsed.getAndAdd(this.period);
            long $$1 = this.elapsed.get();
            Iterator var5 = this.notifications.iterator();

            while(var5.hasNext()) {
                Notification $$2 = (Notification)var5.next();
                if ($$0 >= $$2.delay) {
                    long $$3 = $$0 / $$2.period;
                    long $$4 = $$1 / $$2.period;
                    if ($$3 != $$4) {
                        this.minecraft.execute(() -> {
                            SystemToast.add(Minecraft.getInstance().getToasts(), SystemToastIds.PERIODIC_NOTIFICATION, Component.translatable($$2.title, $$3), Component.translatable($$2.message, $$3));
                        });
                        return;
                    }
                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record Notification(long delay, long period, String title, String message) {
        public Notification(long delay, long period, String title, String message) {
            this.delay = delay != 0L ? delay : period;
            this.period = period;
            this.title = title;
            this.message = message;
        }

        public long delay() {
            return this.delay;
        }

        public long period() {
            return this.period;
        }

        public String title() {
            return this.title;
        }

        public String message() {
            return this.message;
        }
    }
}
