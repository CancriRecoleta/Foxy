//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class StoredUserList<K, V extends StoredUserEntry<K>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final File file;
    private final Map<String, V> map = Maps.newHashMap();

    public StoredUserList(File p_11380_) {
        this.file = p_11380_;
    }

    public File getFile() {
        return this.file;
    }

    public void add(V p_11382_) {
        this.map.put(this.getKeyForUser(p_11382_.getUser()), p_11382_);

        try {
            this.save();
        } catch (IOException var3) {
            IOException $$1 = var3;
            LOGGER.warn("Could not save the list after adding a user.", $$1);
        }

    }

    @Nullable
    public V get(K p_11389_) {
        this.removeExpired();
        return (StoredUserEntry)this.map.get(this.getKeyForUser(p_11389_));
    }

    public void remove(K p_11394_) {
        this.map.remove(this.getKeyForUser(p_11394_));

        try {
            this.save();
        } catch (IOException var3) {
            IOException $$1 = var3;
            LOGGER.warn("Could not save the list after removing a user.", $$1);
        }

    }

    public void remove(StoredUserEntry<K> p_11387_) {
        this.remove(p_11387_.getUser());
    }

    public String[] getUserList() {
        return (String[])this.map.keySet().toArray(new String[0]);
    }

    public boolean isEmpty() {
        return this.map.size() < 1;
    }

    protected String getKeyForUser(K p_11384_) {
        return p_11384_.toString();
    }

    protected boolean contains(K p_11397_) {
        return this.map.containsKey(this.getKeyForUser(p_11397_));
    }

    private void removeExpired() {
        List<K> $$0 = Lists.newArrayList();
        Iterator var2 = this.map.values().iterator();

        while(var2.hasNext()) {
            V $$1 = (StoredUserEntry)var2.next();
            if ($$1.hasExpired()) {
                $$0.add($$1.getUser());
            }
        }

        var2 = $$0.iterator();

        while(var2.hasNext()) {
            K $$2 = var2.next();
            this.map.remove(this.getKeyForUser($$2));
        }

    }

    protected abstract StoredUserEntry<K> createEntry(JsonObject var1);

    public Collection<V> getEntries() {
        return this.map.values();
    }

    public void save() throws IOException {
        JsonArray $$0 = new JsonArray();
        Stream var10000 = this.map.values().stream().map((p_11392_) -> {
            JsonObject var10000 = new JsonObject();
            Objects.requireNonNull(p_11392_);
            return (JsonObject)Util.make(var10000, p_11392_::serialize);
        });
        Objects.requireNonNull($$0);
        var10000.forEach($$0::add);
        BufferedWriter $$1 = Files.newWriter(this.file, StandardCharsets.UTF_8);

        try {
            GSON.toJson($$0, $$1);
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

    }

    public void load() throws IOException {
        if (this.file.exists()) {
            BufferedReader $$0 = Files.newReader(this.file, StandardCharsets.UTF_8);

            try {
                JsonArray $$1 = (JsonArray)GSON.fromJson($$0, JsonArray.class);
                this.map.clear();
                Iterator var3 = $$1.iterator();

                while(var3.hasNext()) {
                    JsonElement $$2 = (JsonElement)var3.next();
                    JsonObject $$3 = GsonHelper.convertToJsonObject($$2, "entry");
                    StoredUserEntry<K> $$4 = this.createEntry($$3);
                    if ($$4.getUser() != null) {
                        this.map.put(this.getKeyForUser($$4.getUser()), $$4);
                    }
                }
            } catch (Throwable var8) {
                if ($$0 != null) {
                    try {
                        $$0.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }
                }

                throw var8;
            }

            if ($$0 != null) {
                $$0.close();
            }

        }
    }
}
