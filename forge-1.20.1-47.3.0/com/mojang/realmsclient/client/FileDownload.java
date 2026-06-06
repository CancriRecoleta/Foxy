//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
    static final Logger LOGGER = LogUtils.getLogger();
    volatile boolean cancelled;
    volatile boolean finished;
    volatile boolean error;
    volatile boolean extracting;
    @Nullable
    private volatile File tempFile;
    volatile File resourcePackPath;
    @Nullable
    private volatile HttpGet request;
    @Nullable
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public FileDownload() {
    }

    public long contentLength(String p_86990_) {
        CloseableHttpClient $$1 = null;
        HttpGet $$2 = null;

        long var5;
        try {
            $$2 = new HttpGet(p_86990_);
            $$1 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse $$3 = $$1.execute($$2);
            var5 = Long.parseLong($$3.getFirstHeader("Content-Length").getValue());
            return var5;
        } catch (Throwable var16) {
            LOGGER.error("Unable to get content length for download");
            var5 = 0L;
        } finally {
            if ($$2 != null) {
                $$2.releaseConnection();
            }

            if ($$1 != null) {
                try {
                    $$1.close();
                } catch (IOException var15) {
                    IOException $$7 = var15;
                    LOGGER.error("Could not close http client", $$7);
                }
            }

        }

        return var5;
    }

    public void download(WorldDownload p_86983_, String p_86984_, RealmsDownloadLatestWorldScreen.DownloadStatus p_86985_, LevelStorageSource p_86986_) {
        if (this.currentThread == null) {
            this.currentThread = new Thread(() -> {
                CloseableHttpClient $$4 = null;
                boolean var90 = false;

                Exception $$26;
                FileOutputStream $$23;
                DownloadCountingOutputStream $$25;
                CloseableHttpResponse $$22;
                ResourcePackProgressListener $$24;
                label1399: {
                    label1400: {
                        try {
                            var90 = true;
                            this.tempFile = File.createTempFile("backup", ".tar.gz");
                            this.request = new HttpGet(p_86983_.downloadLink);
                            $$4 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                            $$22 = $$4.execute(this.request);
                            p_86985_.totalBytes = Long.parseLong($$22.getFirstHeader("Content-Length").getValue());
                            if ($$22.getStatusLine().getStatusCode() == 200) {
                                $$23 = new FileOutputStream(this.tempFile);
                                ProgressListener $$13 = new ProgressListener(p_86984_.trim(), this.tempFile, p_86986_, p_86985_);
                                $$25 = new DownloadCountingOutputStream($$23);
                                $$25.setListener($$13);
                                IOUtils.copy($$22.getEntity().getContent(), $$25);
                                var90 = false;
                                break label1400;
                            }

                            this.error = true;
                            this.request.abort();
                            var90 = false;
                        } catch (Exception var103) {
                            $$26 = var103;
                            LOGGER.error("Caught exception while downloading: {}", $$26.getMessage());
                            this.error = true;
                            var90 = false;
                            break label1399;
                        } finally {
                            if (var90) {
                                this.request.releaseConnection();
                                if (this.tempFile != null) {
                                    this.tempFile.delete();
                                }

                                if (!this.error) {
                                    if (!p_86983_.resourcePackUrl.isEmpty() && !p_86983_.resourcePackHash.isEmpty()) {
                                        try {
                                            this.tempFile = File.createTempFile("resources", ".tar.gz");
                                            this.request = new HttpGet(p_86983_.resourcePackUrl);
                                            HttpResponse $$28 = $$4.execute(this.request);
                                            p_86985_.totalBytes = Long.parseLong($$28.getFirstHeader("Content-Length").getValue());
                                            if ($$28.getStatusLine().getStatusCode() != 200) {
                                                this.error = true;
                                                this.request.abort();
                                                return;
                                            }

                                            OutputStream $$29 = new FileOutputStream(this.tempFile);
                                            ResourcePackProgressListener $$30 = new ResourcePackProgressListener(this.tempFile, p_86985_, p_86983_);
                                            DownloadCountingOutputStream $$31 = new DownloadCountingOutputStream($$29);
                                            $$31.setListener($$30);
                                            IOUtils.copy($$28.getEntity().getContent(), $$31);
                                        } catch (Exception var95) {
                                            Exception $$32 = var95;
                                            LOGGER.error("Caught exception while downloading: {}", $$32.getMessage());
                                            this.error = true;
                                        } finally {
                                            this.request.releaseConnection();
                                            if (this.tempFile != null) {
                                                this.tempFile.delete();
                                            }

                                        }
                                    } else {
                                        this.finished = true;
                                    }
                                }

                                if ($$4 != null) {
                                    try {
                                        $$4.close();
                                    } catch (IOException var91) {
                                        LOGGER.error("Failed to close Realms download client");
                                    }
                                }

                            }
                        }

                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                            this.tempFile.delete();
                        }

                        if (!this.error) {
                            if (!p_86983_.resourcePackUrl.isEmpty() && !p_86983_.resourcePackHash.isEmpty()) {
                                try {
                                    this.tempFile = File.createTempFile("resources", ".tar.gz");
                                    this.request = new HttpGet(p_86983_.resourcePackUrl);
                                    HttpResponse $$6 = $$4.execute(this.request);
                                    p_86985_.totalBytes = Long.parseLong($$6.getFirstHeader("Content-Length").getValue());
                                    if ($$6.getStatusLine().getStatusCode() != 200) {
                                        this.error = true;
                                        this.request.abort();
                                        return;
                                    }

                                    OutputStream $$7 = new FileOutputStream(this.tempFile);
                                    ResourcePackProgressListener $$8 = new ResourcePackProgressListener(this.tempFile, p_86985_, p_86983_);
                                    DownloadCountingOutputStream $$9 = new DownloadCountingOutputStream($$7);
                                    $$9.setListener($$8);
                                    IOUtils.copy($$6.getEntity().getContent(), $$9);
                                } catch (Exception var97) {
                                    Exception $$10 = var97;
                                    LOGGER.error("Caught exception while downloading: {}", $$10.getMessage());
                                    this.error = true;
                                } finally {
                                    this.request.releaseConnection();
                                    if (this.tempFile != null) {
                                        this.tempFile.delete();
                                    }

                                }
                            } else {
                                this.finished = true;
                            }
                        }

                        if ($$4 != null) {
                            try {
                                $$4.close();
                            } catch (IOException var92) {
                                LOGGER.error("Failed to close Realms download client");
                            }
                        }

                        return;
                    }

                    this.request.releaseConnection();
                    if (this.tempFile != null) {
                        this.tempFile.delete();
                    }

                    if (!this.error) {
                        if (!p_86983_.resourcePackUrl.isEmpty() && !p_86983_.resourcePackHash.isEmpty()) {
                            label1344: {
                                try {
                                    this.tempFile = File.createTempFile("resources", ".tar.gz");
                                    this.request = new HttpGet(p_86983_.resourcePackUrl);
                                    $$22 = $$4.execute(this.request);
                                    p_86985_.totalBytes = Long.parseLong($$22.getFirstHeader("Content-Length").getValue());
                                    if ($$22.getStatusLine().getStatusCode() == 200) {
                                        $$23 = new FileOutputStream(this.tempFile);
                                        $$24 = new ResourcePackProgressListener(this.tempFile, p_86985_, p_86983_);
                                        $$25 = new DownloadCountingOutputStream($$23);
                                        $$25.setListener($$24);
                                        IOUtils.copy($$22.getEntity().getContent(), $$25);
                                        break label1344;
                                    }

                                    this.error = true;
                                    this.request.abort();
                                } catch (Exception var101) {
                                    $$26 = var101;
                                    LOGGER.error("Caught exception while downloading: {}", $$26.getMessage());
                                    this.error = true;
                                    break label1344;
                                } finally {
                                    this.request.releaseConnection();
                                    if (this.tempFile != null) {
                                        this.tempFile.delete();
                                    }

                                }

                                return;
                            }
                        } else {
                            this.finished = true;
                        }
                    }

                    if ($$4 != null) {
                        try {
                            $$4.close();
                        } catch (IOException var94) {
                            LOGGER.error("Failed to close Realms download client");
                        }

                        return;
                    }

                    return;
                }

                this.request.releaseConnection();
                if (this.tempFile != null) {
                    this.tempFile.delete();
                }

                if (!this.error) {
                    if (!p_86983_.resourcePackUrl.isEmpty() && !p_86983_.resourcePackHash.isEmpty()) {
                        try {
                            this.tempFile = File.createTempFile("resources", ".tar.gz");
                            this.request = new HttpGet(p_86983_.resourcePackUrl);
                            $$22 = $$4.execute(this.request);
                            p_86985_.totalBytes = Long.parseLong($$22.getFirstHeader("Content-Length").getValue());
                            if ($$22.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.request.abort();
                                return;
                            }

                            $$23 = new FileOutputStream(this.tempFile);
                            $$24 = new ResourcePackProgressListener(this.tempFile, p_86985_, p_86983_);
                            $$25 = new DownloadCountingOutputStream($$23);
                            $$25.setListener($$24);
                            IOUtils.copy($$22.getEntity().getContent(), $$25);
                        } catch (Exception var99) {
                            $$26 = var99;
                            LOGGER.error("Caught exception while downloading: {}", $$26.getMessage());
                            this.error = true;
                        } finally {
                            this.request.releaseConnection();
                            if (this.tempFile != null) {
                                this.tempFile.delete();
                            }

                        }
                    } else {
                        this.finished = true;
                    }
                }

                if ($$4 != null) {
                    try {
                        $$4.close();
                    } catch (IOException var93) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }

            });
            this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
            this.currentThread.start();
        }
    }

    public void cancel() {
        if (this.request != null) {
            this.request.abort();
        }

        if (this.tempFile != null) {
            this.tempFile.delete();
        }

        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String p_87002_) {
        p_87002_ = p_87002_.replaceAll("[\\./\"]", "_");
        String[] var1 = INVALID_FILE_NAMES;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String $$1 = var1[var3];
            if (p_87002_.equalsIgnoreCase($$1)) {
                p_87002_ = "_" + p_87002_ + "_";
            }
        }

        return p_87002_;
    }

    void untarGzipArchive(String p_86992_, @Nullable File p_86993_, LevelStorageSource p_86994_) throws IOException {
        Pattern $$3 = Pattern.compile(".*-([0-9]+)$");
        int $$4 = 1;
        char[] var7 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            char $$5 = var7[var9];
            p_86992_ = p_86992_.replace($$5, '_');
        }

        if (StringUtils.isEmpty(p_86992_)) {
            p_86992_ = "Realm";
        }

        p_86992_ = findAvailableFolderName(p_86992_);

        try {
            Iterator var54 = p_86994_.findLevelCandidates().iterator();

            while(var54.hasNext()) {
                LevelStorageSource.LevelDirectory $$6 = (LevelStorageSource.LevelDirectory)var54.next();
                String $$7 = $$6.directoryName();
                if ($$7.toLowerCase(Locale.ROOT).startsWith(p_86992_.toLowerCase(Locale.ROOT))) {
                    Matcher $$8 = $$3.matcher($$7);
                    if ($$8.matches()) {
                        int $$9 = Integer.parseInt($$8.group(1));
                        if ($$9 > $$4) {
                            $$4 = $$9;
                        }
                    } else {
                        ++$$4;
                    }
                }
            }
        } catch (Exception var52) {
            LOGGER.error("Error getting level list", var52);
            this.error = true;
            return;
        }

        String $$13;
        if (p_86994_.isNewLevelIdAcceptable(p_86992_) && $$4 <= 1) {
            $$13 = p_86992_;
        } else {
            $$13 = p_86992_ + ($$4 == 1 ? "" : "-" + $$4);
            if (!p_86994_.isNewLevelIdAcceptable($$13)) {
                boolean $$12 = false;

                while(!$$12) {
                    ++$$4;
                    $$13 = p_86992_ + ($$4 == 1 ? "" : "-" + $$4);
                    if (p_86994_.isNewLevelIdAcceptable($$13)) {
                        $$12 = true;
                    }
                }
            }
        }

        TarArchiveInputStream $$14 = null;
        File $$15 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
        boolean var35 = false;

        Path $$25;
        LevelStorageSource.LevelStorageAccess $$24;
        label463: {
            try {
                var35 = true;
                $$15.mkdir();
                $$14 = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_86993_))));

                for(TarArchiveEntry $$16 = $$14.getNextTarEntry(); $$16 != null; $$16 = $$14.getNextTarEntry()) {
                    File $$17 = new File($$15, $$16.getName().replace("world", $$13));
                    if ($$16.isDirectory()) {
                        $$17.mkdirs();
                    } else {
                        $$17.createNewFile();
                        FileOutputStream $$18 = new FileOutputStream($$17);

                        try {
                            IOUtils.copy($$14, $$18);
                        } catch (Throwable var40) {
                            try {
                                $$18.close();
                            } catch (Throwable var38) {
                                var40.addSuppressed(var38);
                            }

                            throw var40;
                        }

                        $$18.close();
                    }
                }

                var35 = false;
                break label463;
            } catch (Exception var50) {
                LOGGER.error("Error extracting world", var50);
                this.error = true;
                var35 = false;
            } finally {
                if (var35) {
                    if ($$14 != null) {
                        $$14.close();
                    }

                    if (p_86993_ != null) {
                        p_86993_.delete();
                    }

                    try {
                        LevelStorageSource.LevelStorageAccess $$28 = p_86994_.validateAndCreateAccess($$13);

                        try {
                            $$28.renameLevel($$13.trim());
                            Path $$29 = $$28.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                            deletePlayerTag($$29.toFile());
                        } catch (Throwable var41) {
                            if ($$28 != null) {
                                try {
                                    $$28.close();
                                } catch (Throwable var36) {
                                    var41.addSuppressed(var36);
                                }
                            }

                            throw var41;
                        }

                        if ($$28 != null) {
                            $$28.close();
                        }
                    } catch (IOException var42) {
                        LOGGER.error("Failed to rename unpacked realms level {}", $$13, var42);
                    } catch (ContentValidationException var43) {
                        ContentValidationException $$31 = var43;
                        LOGGER.warn("{}", $$31.getMessage());
                    }

                    this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
                }
            }

            if ($$14 != null) {
                $$14.close();
            }

            if (p_86993_ != null) {
                p_86993_.delete();
            }

            try {
                $$24 = p_86994_.validateAndCreateAccess($$13);

                try {
                    $$24.renameLevel($$13.trim());
                    $$25 = $$24.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                    deletePlayerTag($$25.toFile());
                } catch (Throwable var44) {
                    if ($$24 != null) {
                        try {
                            $$24.close();
                        } catch (Throwable var37) {
                            var44.addSuppressed(var37);
                        }
                    }

                    throw var44;
                }

                if ($$24 != null) {
                    $$24.close();
                }
            } catch (IOException var45) {
                LOGGER.error("Failed to rename unpacked realms level {}", $$13, var45);
            } catch (ContentValidationException var46) {
                LOGGER.warn("{}", var46.getMessage());
            }

            this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
            return;
        }

        if ($$14 != null) {
            $$14.close();
        }

        if (p_86993_ != null) {
            p_86993_.delete();
        }

        try {
            $$24 = p_86994_.validateAndCreateAccess($$13);

            try {
                $$24.renameLevel($$13.trim());
                $$25 = $$24.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                deletePlayerTag($$25.toFile());
            } catch (Throwable var47) {
                if ($$24 != null) {
                    try {
                        $$24.close();
                    } catch (Throwable var39) {
                        var47.addSuppressed(var39);
                    }
                }

                throw var47;
            }

            if ($$24 != null) {
                $$24.close();
            }
        } catch (IOException var48) {
            LOGGER.error("Failed to rename unpacked realms level {}", $$13, var48);
        } catch (ContentValidationException var49) {
            LOGGER.warn("{}", var49.getMessage());
        }

        this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
    }

    private static void deletePlayerTag(File p_86988_) {
        if (p_86988_.exists()) {
            try {
                CompoundTag $$1 = NbtIo.readCompressed(p_86988_);
                CompoundTag $$2 = $$1.getCompound("Data");
                $$2.remove("Player");
                NbtIo.writeCompressed($$1, p_86988_);
            } catch (Exception var3) {
                Exception $$3 = var3;
                $$3.printStackTrace();
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    class ResourcePackProgressListener implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        ResourcePackProgressListener(File p_87046_, RealmsDownloadLatestWorldScreen.DownloadStatus p_87047_, WorldDownload p_87048_) {
            this.tempFile = p_87046_;
            this.downloadStatus = p_87047_;
            this.worldDownload = p_87048_;
        }

        public void actionPerformed(ActionEvent p_87056_) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)p_87056_.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String $$1 = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
                    if ($$1.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        FileDownload.LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", this.worldDownload.resourcePackHash, $$1);
                        FileUtils.deleteQuietly(this.tempFile);
                        FileDownload.this.error = true;
                    }
                } catch (IOException var3) {
                    IOException $$2 = var3;
                    FileDownload.LOGGER.error("Error copying resourcepack file: {}", $$2.getMessage());
                    FileDownload.this.error = true;
                }
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class DownloadCountingOutputStream extends CountingOutputStream {
        @Nullable
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream p_193509_) {
            super(p_193509_);
        }

        public void setListener(ActionListener p_87017_) {
            this.listener = p_87017_;
        }

        protected void afterWrite(int p_87019_) throws IOException {
            super.afterWrite(p_87019_);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
            }

        }
    }

    @OnlyIn(Dist.CLIENT)
    private class ProgressListener implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        ProgressListener(String p_87027_, File p_87028_, LevelStorageSource p_87029_, RealmsDownloadLatestWorldScreen.DownloadStatus p_87030_) {
            this.worldName = p_87027_;
            this.tempFile = p_87028_;
            this.levelStorageSource = p_87029_;
            this.downloadStatus = p_87030_;
        }

        public void actionPerformed(ActionEvent p_87039_) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)p_87039_.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                } catch (IOException var3) {
                    IOException $$1 = var3;
                    FileDownload.LOGGER.error("Error extracting archive", $$1);
                    FileDownload.this.error = true;
                }
            }

        }
    }
}
