//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ListeningExecutorService DOWNLOAD_EXECUTOR;

    private HttpUtil() {
    }

    public static CompletableFuture<?> downloadTo(File p_216226_, URL p_216227_, Map<String, String> p_216228_, int p_216229_, @Nullable ProgressListener p_216230_, Proxy p_216231_) {
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection $$6 = null;
            InputStream $$7 = null;
            OutputStream $$8 = null;
            if (p_216230_ != null) {
                p_216230_.progressStart(Component.translatable("resourcepack.downloading"));
                p_216230_.progressStage(Component.translatable("resourcepack.requesting"));
            }

            try {
                byte[] $$9 = new byte[4096];
                $$6 = (HttpURLConnection)p_216227_.openConnection(p_216231_);
                $$6.setInstanceFollowRedirects(true);
                float $$10 = 0.0F;
                float $$11 = (float)p_216228_.entrySet().size();
                Iterator var12 = p_216228_.entrySet().iterator();

                while(var12.hasNext()) {
                    Map.Entry<String, String> $$12 = (Map.Entry)var12.next();
                    $$6.setRequestProperty((String)$$12.getKey(), (String)$$12.getValue());
                    if (p_216230_ != null) {
                        p_216230_.progressStagePercentage((int)(++$$10 / $$11 * 100.0F));
                    }
                }

                $$7 = $$6.getInputStream();
                $$11 = (float)$$6.getContentLength();
                int $$13 = $$6.getContentLength();
                if (p_216230_ != null) {
                    p_216230_.progressStage(Component.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", $$11 / 1000.0F / 1000.0F)));
                }

                if (p_216226_.exists()) {
                    long $$14 = p_216226_.length();
                    if ($$14 == (long)$$13) {
                        if (p_216230_ != null) {
                            p_216230_.stop();
                        }

                        Object var15 = null;
                        return var15;
                    }

                    LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", new Object[]{p_216226_, $$13, $$14});
                    FileUtils.deleteQuietly(p_216226_);
                } else if (p_216226_.getParentFile() != null) {
                    p_216226_.getParentFile().mkdirs();
                }

                $$8 = new DataOutputStream(new FileOutputStream(p_216226_));
                if (p_216229_ > 0 && $$11 > (float)p_216229_) {
                    if (p_216230_ != null) {
                        p_216230_.stop();
                    }

                    throw new IOException("Filesize is bigger than maximum allowed (file is " + $$10 + ", limit is " + p_216229_ + ")");
                } else {
                    int $$15;
                    while(($$15 = $$7.read($$9)) >= 0) {
                        $$10 += (float)$$15;
                        if (p_216230_ != null) {
                            p_216230_.progressStagePercentage((int)($$10 / $$11 * 100.0F));
                        }

                        if (p_216229_ > 0 && $$10 > (float)p_216229_) {
                            if (p_216230_ != null) {
                                p_216230_.stop();
                            }

                            throw new IOException("Filesize was bigger than maximum allowed (got >= " + $$10 + ", limit was " + p_216229_ + ")");
                        }

                        if (Thread.interrupted()) {
                            LOGGER.error("INTERRUPTED");
                            if (p_216230_ != null) {
                                p_216230_.stop();
                            }

                            Object var14 = null;
                            return var14;
                        }

                        $$8.write($$9, 0, $$15);
                    }

                    if (p_216230_ != null) {
                        p_216230_.stop();
                    }

                    return null;
                }
            } catch (Throwable var21) {
                Throwable $$16 = var21;
                LOGGER.error("Failed to download file", $$16);
                if ($$6 != null) {
                    InputStream $$17 = $$6.getErrorStream();

                    try {
                        LOGGER.error("HTTP response error: {}", IOUtils.toString($$17, StandardCharsets.UTF_8));
                    } catch (IOException var20) {
                        LOGGER.error("Failed to read response from server");
                    }
                }

                if (p_216230_ != null) {
                    p_216230_.stop();
                }

                return null;
            } finally {
                IOUtils.closeQuietly($$7);
                IOUtils.closeQuietly($$8);
            }
        }, DOWNLOAD_EXECUTOR);
    }

    public static int getAvailablePort() {
        try {
            ServerSocket $$0 = new ServerSocket(0);

            int var1;
            try {
                var1 = $$0.getLocalPort();
            } catch (Throwable var4) {
                try {
                    $$0.close();
                } catch (Throwable var3) {
                    var4.addSuppressed(var3);
                }

                throw var4;
            }

            $$0.close();
            return var1;
        } catch (IOException var5) {
            return 25564;
        }
    }

    public static boolean isPortAvailable(int p_259872_) {
        if (p_259872_ >= 0 && p_259872_ <= 65535) {
            try {
                ServerSocket $$1 = new ServerSocket(p_259872_);

                boolean var2;
                try {
                    var2 = $$1.getLocalPort() == p_259872_;
                } catch (Throwable var5) {
                    try {
                        $$1.close();
                    } catch (Throwable var4) {
                        var5.addSuppressed(var4);
                    }

                    throw var5;
                }

                $$1.close();
                return var2;
            } catch (IOException var6) {
                return false;
            }
        } else {
            return false;
        }
    }

    static {
        DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));
    }
}
