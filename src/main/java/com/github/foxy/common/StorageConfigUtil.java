package com.github.foxy.common;

import com.github.foxy.common.config.Serialization;
import com.github.foxy.common.config.compressors.ZSTDCompressor;
import com.github.foxy.common.config.section.SectionSerializationStorage;
import com.github.foxy.common.config.section.SectionStorageConfig;
import com.github.foxy.common.config.storage.other.CompressionStorageAdaptor;
import com.github.foxy.common.config.storage.lmdb.LMDBStorageBackend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StorageConfigUtil {

    public static <T> T getCreateStorageConfig(Class<T> clz, Predicate<T> verifier, Supplier<T> defaultConfig, Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        var json = path.resolve("config.json");
        T config = null;
        if (Files.exists(json)) {
            try {
                config = Serialization.GSON.fromJson(Files.readString(json), clz);
                if (config == null) {
                    Logger.error("Config deserialization null, reverting to default");
                } else {
                    if (!verifier.test(config)) {
                        Logger.error("Config section storage null, reverting to default");
                        config = null;
                    }
                }
            } catch (Exception e) {
                Logger.error("Failed to load the storage configuration file, resetting it to default, this will probably break your save if you used a custom storage config", e);
            }
        }

        if (config == null) {
            config = defaultConfig.get();
        }
        try {
            Files.writeString(json, Serialization.GSON.toJson(config));
        } catch (Exception e) {
            throw new RuntimeException("Failed write the config, aborting!", e);
        }
        if (config == null) {
            throw new IllegalStateException("Config is still null\n");
        }
        return config;
    }

    public static SectionSerializationStorage.Config createDefaultSerializer() {
        //Create the default config. LMDB is the default backend because it ships as an LWJGL module
        //(visible everywhere), unlike the optional RocksDB/Redis backends.
        var baseDB = new LMDBStorageBackend.Config();

        var compressor = new ZSTDCompressor.Config();
        compressor.compressionLevel = 1;

        var compression = new CompressionStorageAdaptor.Config();
        compression.delegate = baseDB;
        compression.compressor = compressor;

        var serializer = new SectionSerializationStorage.Config();
        serializer.storage = compression;

        return serializer;
    }
}
