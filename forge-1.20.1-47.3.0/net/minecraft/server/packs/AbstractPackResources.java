//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final boolean isBuiltin;

    protected AbstractPackResources(String p_255888_, boolean p_256392_) {
        this.name = p_255888_;
        this.isBuiltin = p_256392_;
    }

    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10213_) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource(new String[]{"pack.mcmeta"});
        if (iosupplier == null) {
            return null;
        } else {
            InputStream inputstream = (InputStream)iosupplier.get();

            Object var4;
            try {
                var4 = getMetadataFromStream(p_10213_, inputstream);
            } catch (Throwable var7) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (inputstream != null) {
                inputstream.close();
            }

            return var4;
        }
    }

    @Nullable
    public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> p_10215_, InputStream p_10216_) {
        JsonObject jsonobject;
        Exception exception;
        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(p_10216_, StandardCharsets.UTF_8));

            try {
                jsonobject = GsonHelper.parse((Reader)bufferedreader);
            } catch (Throwable var8) {
                try {
                    bufferedreader.close();
                } catch (Throwable var6) {
                    var8.addSuppressed(var6);
                }

                throw var8;
            }

            bufferedreader.close();
        } catch (Exception var9) {
            exception = var9;
            LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception);
            return null;
        }

        if (!jsonobject.has(p_10215_.getMetadataSectionName())) {
            return null;
        } else {
            try {
                return p_10215_.fromJson(GsonHelper.getAsJsonObject(jsonobject, p_10215_.getMetadataSectionName()));
            } catch (Exception var7) {
                exception = var7;
                LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception);
                return null;
            }
        }
    }

    public String packId() {
        return this.name;
    }

    public boolean isBuiltin() {
        return this.isBuiltin;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s: %s", this.getClass().getName(), this.name);
    }
}
