//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class ServerStatsCounter extends StatsCounter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftServer server;
    private final File file;
    private final Set<Stat<?>> dirty = Sets.newHashSet();

    public ServerStatsCounter(MinecraftServer p_12816_, File p_12817_) {
        this.server = p_12816_;
        this.file = p_12817_;
        if (p_12817_.isFile()) {
            try {
                this.parseLocal(p_12816_.getFixerUpper(), FileUtils.readFileToString(p_12817_));
            } catch (IOException var4) {
                IOException $$2 = var4;
                LOGGER.error("Couldn't read statistics file {}", p_12817_, $$2);
            } catch (JsonParseException var5) {
                JsonParseException $$3 = var5;
                LOGGER.error("Couldn't parse statistics file {}", p_12817_, $$3);
            }
        }

    }

    public void save() {
        try {
            FileUtils.writeStringToFile(this.file, this.toJson());
        } catch (IOException var2) {
            IOException $$0 = var2;
            LOGGER.error("Couldn't save stats", $$0);
        }

    }

    public void setValue(Player p_12827_, Stat<?> p_12828_, int p_12829_) {
        super.setValue(p_12827_, p_12828_, p_12829_);
        this.dirty.add(p_12828_);
    }

    private Set<Stat<?>> getDirty() {
        Set<Stat<?>> $$0 = Sets.newHashSet(this.dirty);
        this.dirty.clear();
        return $$0;
    }

    public void parseLocal(DataFixer p_12833_, String p_12834_) {
        try {
            JsonReader $$2 = new JsonReader(new StringReader(p_12834_));

            label62: {
                try {
                    $$2.setLenient(false);
                    JsonElement $$3 = Streams.parse($$2);
                    if ($$3.isJsonNull()) {
                        LOGGER.error("Unable to parse Stat data from {}", this.file);
                        break label62;
                    }

                    CompoundTag $$4 = fromJson($$3.getAsJsonObject());
                    $$4 = DataFixTypes.STATS.updateToCurrentVersion(p_12833_, $$4, NbtUtils.getDataVersion($$4, 1343));
                    if ($$4.contains("stats", 10)) {
                        CompoundTag $$5 = $$4.getCompound("stats");
                        Iterator var7 = $$5.getAllKeys().iterator();

                        while(var7.hasNext()) {
                            String $$6 = (String)var7.next();
                            if ($$5.contains($$6, 10)) {
                                Util.ifElse(BuiltInRegistries.STAT_TYPE.getOptional(new ResourceLocation($$6)), (p_12844_) -> {
                                    CompoundTag $$3 = $$5.getCompound($$6);
                                    Iterator var5 = $$3.getAllKeys().iterator();

                                    while(var5.hasNext()) {
                                        String $$4 = (String)var5.next();
                                        if ($$3.contains($$4, 99)) {
                                            Util.ifElse(this.getStat(p_12844_, $$4), (p_144252_) -> {
                                                this.stats.put(p_144252_, $$3.getInt($$4));
                                            }, () -> {
                                                LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.file, $$4);
                                            });
                                        } else {
                                            LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", new Object[]{this.file, $$3.get($$4), $$4});
                                        }
                                    }

                                }, () -> {
                                    LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.file, $$6);
                                });
                            }
                        }
                    }
                } catch (Throwable var10) {
                    try {
                        $$2.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }

                    throw var10;
                }

                $$2.close();
                return;
            }

            $$2.close();
        } catch (IOException | JsonParseException var11) {
            Exception $$7 = var11;
            LOGGER.error("Unable to parse Stat data from {}", this.file, $$7);
        }
    }

    private <T> Optional<Stat<T>> getStat(StatType<T> p_12824_, String p_12825_) {
        Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(p_12825_));
        Registry var10001 = p_12824_.getRegistry();
        Objects.requireNonNull(var10001);
        var10000 = var10000.flatMap(var10001::getOptional);
        Objects.requireNonNull(p_12824_);
        return var10000.map(p_12824_::get);
    }

    private static CompoundTag fromJson(JsonObject p_12831_) {
        CompoundTag $$1 = new CompoundTag();
        Iterator var2 = p_12831_.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, JsonElement> $$2 = (Map.Entry)var2.next();
            JsonElement $$3 = (JsonElement)$$2.getValue();
            if ($$3.isJsonObject()) {
                $$1.put((String)$$2.getKey(), fromJson($$3.getAsJsonObject()));
            } else if ($$3.isJsonPrimitive()) {
                JsonPrimitive $$4 = $$3.getAsJsonPrimitive();
                if ($$4.isNumber()) {
                    $$1.putInt((String)$$2.getKey(), $$4.getAsInt());
                }
            }
        }

        return $$1;
    }

    protected String toJson() {
        Map<StatType<?>, JsonObject> $$0 = Maps.newHashMap();
        ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

        while(var2.hasNext()) {
            Object2IntMap.Entry<Stat<?>> $$1 = (Object2IntMap.Entry)var2.next();
            Stat<?> $$2 = (Stat)$$1.getKey();
            ((JsonObject)$$0.computeIfAbsent($$2.getType(), (p_12822_) -> {
                return new JsonObject();
            })).addProperty(getKey($$2).toString(), $$1.getIntValue());
        }

        JsonObject $$3 = new JsonObject();
        Iterator var6 = $$0.entrySet().iterator();

        while(var6.hasNext()) {
            Map.Entry<StatType<?>, JsonObject> $$4 = (Map.Entry)var6.next();
            $$3.add(BuiltInRegistries.STAT_TYPE.getKey((StatType)$$4.getKey()).toString(), (JsonElement)$$4.getValue());
        }

        JsonObject $$5 = new JsonObject();
        $$5.add("stats", $$3);
        $$5.addProperty("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        return $$5.toString();
    }

    private static <T> ResourceLocation getKey(Stat<T> p_12847_) {
        return p_12847_.getType().getRegistry().getKey(p_12847_.getValue());
    }

    public void markAllDirty() {
        this.dirty.addAll(this.stats.keySet());
    }

    public void sendStats(ServerPlayer p_12820_) {
        Object2IntMap<Stat<?>> $$1 = new Object2IntOpenHashMap();
        Iterator var3 = this.getDirty().iterator();

        while(var3.hasNext()) {
            Stat<?> $$2 = (Stat)var3.next();
            $$1.put($$2, this.getValue($$2));
        }

        p_12820_.connection.send(new ClientboundAwardStatsPacket($$1));
    }
}
