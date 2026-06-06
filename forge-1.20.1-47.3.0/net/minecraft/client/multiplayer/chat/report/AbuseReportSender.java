//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.datafixers.util.Unit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface AbuseReportSender {
    static AbuseReportSender create(ReportEnvironment p_239536_, UserApiService p_239537_) {
        return new Services(p_239536_, p_239537_);
    }

    CompletableFuture<Unit> send(UUID var1, AbuseReport var2);

    boolean isEnabled();

    default AbuseReportLimits reportLimits() {
        return AbuseReportLimits.DEFAULTS;
    }

    @OnlyIn(Dist.CLIENT)
    public static record Services(ReportEnvironment environment, UserApiService userApiService) implements AbuseReportSender {
        private static final Component SERVICE_UNAVAILABLE_TEXT = Component.translatable("gui.abuseReport.send.service_unavailable");
        private static final Component HTTP_ERROR_TEXT = Component.translatable("gui.abuseReport.send.http_error");
        private static final Component JSON_ERROR_TEXT = Component.translatable("gui.abuseReport.send.json_error");

        public Services(ReportEnvironment environment, UserApiService userApiService) {
            this.environment = environment;
            this.userApiService = userApiService;
        }

        public CompletableFuture<Unit> send(UUID p_239470_, AbuseReport p_239471_) {
            return CompletableFuture.supplyAsync(() -> {
                AbuseReportRequest $$2 = new AbuseReportRequest(1, p_239470_, p_239471_, this.environment.clientInfo(), this.environment.thirdPartyServerInfo(), this.environment.realmInfo());

                Component $$6;
                try {
                    this.userApiService.reportAbuse($$2);
                    return Unit.INSTANCE;
                } catch (MinecraftClientHttpException var6) {
                    MinecraftClientHttpException $$3 = var6;
                    $$6 = this.getHttpErrorDescription($$3);
                    throw new CompletionException(new SendException($$6, $$3));
                } catch (MinecraftClientException var7) {
                    MinecraftClientException $$5 = var7;
                    $$6 = this.getErrorDescription($$5);
                    throw new CompletionException(new SendException($$6, $$5));
                }
            }, Util.ioPool());
        }

        public boolean isEnabled() {
            return this.userApiService.canSendReports();
        }

        private Component getHttpErrorDescription(MinecraftClientHttpException p_239705_) {
            return Component.translatable("gui.abuseReport.send.error_message", p_239705_.getMessage());
        }

        private Component getErrorDescription(MinecraftClientException p_240068_) {
            Component var10000;
            switch (p_240068_.getType()) {
                case SERVICE_UNAVAILABLE -> var10000 = SERVICE_UNAVAILABLE_TEXT;
                case HTTP_ERROR -> var10000 = HTTP_ERROR_TEXT;
                case JSON_ERROR -> var10000 = JSON_ERROR_TEXT;
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }

        public AbuseReportLimits reportLimits() {
            return this.userApiService.getAbuseReportLimits();
        }

        public ReportEnvironment environment() {
            return this.environment;
        }

        public UserApiService userApiService() {
            return this.userApiService;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SendException extends ThrowingComponent {
        public SendException(Component p_239646_, Throwable p_239647_) {
            super(p_239646_, p_239647_);
        }
    }
}
