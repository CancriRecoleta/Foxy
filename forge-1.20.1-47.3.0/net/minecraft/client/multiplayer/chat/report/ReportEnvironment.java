//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ReportEnvironment(String clientVersion, @Nullable Server server) {
    public ReportEnvironment(String clientVersion, @Nullable Server server) {
        this.clientVersion = clientVersion;
        this.server = server;
    }

    public static ReportEnvironment local() {
        return create((Server)null);
    }

    public static ReportEnvironment thirdParty(String p_238999_) {
        return create(new Server.ThirdParty(p_238999_));
    }

    public static ReportEnvironment realm(RealmsServer p_239765_) {
        return create(new Server.Realm(p_239765_));
    }

    public static ReportEnvironment create(@Nullable Server p_239956_) {
        return new ReportEnvironment(getClientVersion(), p_239956_);
    }

    public AbuseReportRequest.ClientInfo clientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
    }

    @Nullable
    public AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo() {
        Server var2 = this.server;
        if (var2 instanceof Server.ThirdParty $$0) {
            return new AbuseReportRequest.ThirdPartyServerInfo($$0.ip);
        } else {
            return null;
        }
    }

    @Nullable
    public AbuseReportRequest.RealmInfo realmInfo() {
        Server var2 = this.server;
        if (var2 instanceof Server.Realm $$0) {
            return new AbuseReportRequest.RealmInfo(String.valueOf($$0.realmId()), $$0.slotId());
        } else {
            return null;
        }
    }

    private static String getClientVersion() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append("1.20.1");
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$0.append(" (modded)");
        }

        return $$0.toString();
    }

    public String clientVersion() {
        return this.clientVersion;
    }

    @Nullable
    public Server server() {
        return this.server;
    }

    @OnlyIn(Dist.CLIENT)
    public interface Server {
        @OnlyIn(Dist.CLIENT)
        public static record Realm(long realmId, int slotId) implements Server {
            public Realm(RealmsServer p_239068_) {
                this(p_239068_.id, p_239068_.activeSlot);
            }

            public Realm(long realmId, int slotId) {
                this.realmId = realmId;
                this.slotId = slotId;
            }

            public long realmId() {
                return this.realmId;
            }

            public int slotId() {
                return this.slotId;
            }
        }

        @OnlyIn(Dist.CLIENT)
        public static record ThirdParty(String ip) implements Server {
            public ThirdParty(String ip) {
                this.ip = ip;
            }

            public String ip() {
                return this.ip;
            }
        }
    }
}
