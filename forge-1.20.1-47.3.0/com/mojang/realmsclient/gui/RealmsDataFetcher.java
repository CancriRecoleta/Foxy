//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.gui.task.RepeatedDelayStrategy;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsDataFetcher {
    public final DataFetcher dataFetcher;
    public final DataFetcher.Task<List<RealmsNotification>> notificationsTask;
    public final DataFetcher.Task<List<RealmsServer>> serverListUpdateTask;
    public final DataFetcher.Task<RealmsServerPlayerLists> liveStatsTask;
    public final DataFetcher.Task<Integer> pendingInvitesTask;
    public final DataFetcher.Task<Boolean> trialAvailabilityTask;
    public final DataFetcher.Task<RealmsNews> newsTask;
    public final RealmsNewsManager newsManager;

    public RealmsDataFetcher(RealmsClient p_238853_) {
        this.dataFetcher = new DataFetcher(Util.ioPool(), TimeUnit.MILLISECONDS, Util.timeSource);
        this.newsManager = new RealmsNewsManager(new RealmsPersistence());
        this.serverListUpdateTask = this.dataFetcher.createTask("server list", () -> {
            return p_238853_.listWorlds().servers;
        }, Duration.ofSeconds(60L), RepeatedDelayStrategy.CONSTANT);
        DataFetcher var10001 = this.dataFetcher;
        Objects.requireNonNull(p_238853_);
        this.liveStatsTask = var10001.createTask("live stats", p_238853_::getLiveStats, Duration.ofSeconds(10L), RepeatedDelayStrategy.CONSTANT);
        var10001 = this.dataFetcher;
        Objects.requireNonNull(p_238853_);
        this.pendingInvitesTask = var10001.createTask("pending invite count", p_238853_::pendingInvitesCount, Duration.ofSeconds(10L), RepeatedDelayStrategy.exponentialBackoff(360));
        var10001 = this.dataFetcher;
        Objects.requireNonNull(p_238853_);
        this.trialAvailabilityTask = var10001.createTask("trial availablity", p_238853_::trialAvailable, Duration.ofSeconds(60L), RepeatedDelayStrategy.exponentialBackoff(60));
        var10001 = this.dataFetcher;
        Objects.requireNonNull(p_238853_);
        this.newsTask = var10001.createTask("unread news", p_238853_::getNews, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
        var10001 = this.dataFetcher;
        Objects.requireNonNull(p_238853_);
        this.notificationsTask = var10001.createTask("notifications", p_238853_::getNotifications, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
    }
}
