//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public class Ping {
    public Ping() {
    }

    public static List<RegionPingResult> ping(Region... p_87131_) {
        Region[] var1 = p_87131_;
        int var2 = p_87131_.length;

        int var3;
        for(var3 = 0; var3 < var2; ++var3) {
            Region $$1 = var1[var3];
            ping($$1.endpoint);
        }

        List<RegionPingResult> $$2 = Lists.newArrayList();
        Region[] var7 = p_87131_;
        var3 = p_87131_.length;

        for(int var8 = 0; var8 < var3; ++var8) {
            Region $$3 = var7[var8];
            $$2.add(new RegionPingResult($$3.name, ping($$3.endpoint)));
        }

        $$2.sort(Comparator.comparingInt(RegionPingResult::ping));
        return $$2;
    }

    private static int ping(String p_87127_) {
        int $$1 = true;
        long $$2 = 0L;
        Socket $$3 = null;

        for(int $$4 = 0; $$4 < 5; ++$$4) {
            try {
                SocketAddress $$5 = new InetSocketAddress(p_87127_, 80);
                $$3 = new Socket();
                long $$6 = now();
                $$3.connect($$5, 700);
                $$2 += now() - $$6;
            } catch (Exception var12) {
                $$2 += 700L;
            } finally {
                IOUtils.closeQuietly($$3);
            }
        }

        return (int)((double)$$2 / 5.0);
    }

    private static long now() {
        return Util.getMillis();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return ping(com.mojang.realmsclient.client.Ping.Region.values());
    }

    @OnlyIn(Dist.CLIENT)
    static enum Region {
        US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
        US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
        US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
        EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
        AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
        AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
        AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
        SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

        final String name;
        final String endpoint;

        private Region(String p_87148_, String p_87149_) {
            this.name = p_87148_;
            this.endpoint = p_87149_;
        }
    }
}
