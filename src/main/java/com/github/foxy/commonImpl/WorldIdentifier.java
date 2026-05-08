package com.github.foxy.commonImpl;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Stable identifier for one Foxy-managed world.
 *
 * <p>Combines a caller-supplied {@code namespace} (typically the singleplayer save
 * folder name or a sanitized multiplayer server hostname) with the dimension's
 * {@link ResourceKey}. The pair is enough to disambiguate every world a single client
 * is likely to have data for at a given time; an optional {@code biomeSeed} is folded
 * in too so that two saves with the same name but different generation seeds never
 * collide.</p>
 *
 * <h2>Hashing</h2>
 * The {@link #getWorldId()} accessor returns the first 32 hex characters of an SHA-256
 * digest of {@code namespace + "|" + levelKey.location() + "|" + biomeSeed}. The 128-bit
 * truncation is plenty for collision-free lookup across realistic world counts and
 * keeps storage paths short.
 *
 * <h2>Cleanroom note</h2>
 * Upstream Voxy injects a {@code voxy$getIdentifier()} method onto {@code Level} via a
 * mixin. The cleanroom port avoids that coupling by making the identifier a value object
 * the caller constructs explicitly when entering a world; see
 * {@link com.github.foxy.commonImpl.FoxyInstance FoxyInstance} for the concrete client
 * wiring.
 */
public final class WorldIdentifier {

    /** Disambiguator for the world: SP save folder name, or MP server identity. */
    private final String namespace;

    /** Dimension this identifier addresses. */
    private final ResourceKey<Level> levelKey;

    /** World-generation seed (mixed in to differentiate same-named saves with different seeds). */
    private final long biomeSeed;

    private final String worldId;

    /**
     * @param namespace per-host namespace, never null; the SP save folder name or MP
     *                  server identity
     * @param levelKey  dimension key (e.g. {@code minecraft:overworld})
     * @param biomeSeed world generation seed; may be {@code 0} when unknown / irrelevant
     */
    public WorldIdentifier(String namespace, ResourceKey<Level> levelKey, long biomeSeed) {
        this.namespace = Objects.requireNonNull(namespace, "namespace");
        this.levelKey = Objects.requireNonNull(levelKey, "levelKey");
        this.biomeSeed = biomeSeed;
        this.worldId = computeWorldId(namespace, levelKey, biomeSeed);
    }

    /** Convenience constructor with no biome seed. */
    public WorldIdentifier(String namespace, ResourceKey<Level> levelKey) {
        this(namespace, levelKey, 0L);
    }

    /** The caller-supplied namespace string. */
    public String namespace() { return this.namespace; }

    /** The dimension key. */
    public ResourceKey<Level> levelKey() { return this.levelKey; }

    /** The biome-seed seed. */
    public long biomeSeed() { return this.biomeSeed; }

    /**
     * Returns the directory-friendly 32-hex-char hash of this identifier. Stable across
     * runs; safe to use as part of a filesystem path.
     */
    public String getWorldId() { return this.worldId; }

    public long getLongHash() { return Integer.toUnsignedLong(this.worldId.hashCode()); }

    public static WorldIdentifier of(Level level) {
        var instance = FoxyInstance.current();
        if (instance != null) {
            var active = instance.identifier();
            return active.levelKey().equals(level.dimension())
                    ? active
                    : new WorldIdentifier(active.namespace(), level.dimension(), active.biomeSeed());
        }
        return new WorldIdentifier("default", level.dimension(), 0L);
    }

    public com.github.foxy.common.world.WorldEngine getOrCreateEngine() {
        var instance = FoxyInstance.current();
        return instance == null ? null : instance.getOrCreateEngine(this);
    }

    private static String computeWorldId(String namespace, ResourceKey<Level> levelKey, long biomeSeed) {
        String input = namespace + "|" + levelKey.location().toString() + "|" + biomeSeed;
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            // 16 bytes -> 32 hex chars; plenty of entropy and short enough for paths.
            StringBuilder sb = new StringBuilder(32);
            for (int i = 0; i < 16; i++) {
                int b = digest[i] & 0xFF;
                if (b < 0x10) sb.append('0');
                sb.append(Integer.toHexString(b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is mandatory in any compliant JRE; reaching here means a broken JVM.
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldIdentifier other)) return false;
        return this.biomeSeed == other.biomeSeed
                && this.namespace.equals(other.namespace)
                && this.levelKey.equals(other.levelKey);
    }

    @Override
    public int hashCode() {
        // The hex worldId already encodes all three fields; reuse its hash.
        return this.worldId.hashCode();
    }

    @Override
    public String toString() {
        return "WorldIdentifier[" + this.namespace + ", " + this.levelKey.location()
                + ", seed=" + this.biomeSeed + ", id=" + this.worldId + "]";
    }

    public static final class GsonAdapter extends TypeAdapter<WorldIdentifier> {
        public static final GsonAdapter INSTANCE = new GsonAdapter();

        @Override
        public void write(JsonWriter out, WorldIdentifier value) throws IOException {
            out.beginObject();
            out.name("namespace").value(value.namespace);
            out.name("dimension").value(value.levelKey.location().toString());
            out.name("biomeSeed").value(value.biomeSeed);
            out.endObject();
        }

        @Override
        public WorldIdentifier read(JsonReader in) throws IOException {
            String namespace = "default";
            ResourceLocation dimension = Level.OVERWORLD.location();
            long biomeSeed = 0L;
            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "namespace" -> namespace = in.nextString();
                    case "dimension" -> {
                        // tryParse over the deprecated raw-string constructor; on a
                        // malformed value we fall back to the OVERWORLD default.
                        ResourceLocation parsed = ResourceLocation.tryParse(in.nextString());
                        if (parsed != null) dimension = parsed;
                    }
                    case "biomeSeed" -> biomeSeed = in.nextLong();
                    default -> in.skipValue();
                }
            }
            in.endObject();
            return new WorldIdentifier(namespace, ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimension), biomeSeed);
        }
    }
}
