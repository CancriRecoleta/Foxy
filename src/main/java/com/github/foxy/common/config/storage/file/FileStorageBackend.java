package com.github.foxy.common.config.storage.file;

import com.github.foxy.common.Logger;
import com.github.foxy.common.config.storage.StorageBackend;
import com.github.foxy.common.util.MemoryBuffer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.function.LongConsumer;
import java.util.stream.Stream;

/**
 * {@link StorageBackend} that persists each section payload and each id mapping to a
 * dedicated file under a root directory.
 *
 * <h2>Layout</h2>
 * <pre>
 *   &lt;root&gt;/sections/&lt;ss&gt;/&lt;keyHex&gt;.dat
 *   &lt;root&gt;/mappings/&lt;ii&gt;/&lt;idHex&gt;.dat
 * </pre>
 * where {@code &lt;ss&gt;} is the low byte of the section key as two lowercase hex
 * digits (256 shard subdirectories total, ~average 400 files each on a typical world)
 * and {@code &lt;ii&gt;} is the same low byte of the mapping id. The shard prefix keeps
 * directory entry counts manageable on filesystems with O(n) directory scans (ext4
 * without dir_index, NTFS without 8.3-name disable, etc.).
 *
 * <h2>Atomicity</h2>
 * Every write goes to a sibling {@code .tmp} file and is then moved into place with
 * {@link StandardCopyOption#ATOMIC_MOVE}. A torn write therefore at worst leaves a
 * stray {@code .tmp} that the next startup can sweep; it never corrupts a stored entry.
 *
 * <h2>Suitability</h2>
 * No native dependencies, trivially correct, and adequate for development /
 * single-player imports. For larger worlds (10&sup7; sections) packed-region or
 * RocksDB / LMDB backends are more I/O efficient; this implementation is meant to be
 * the default choice until those land.
 */
public final class FileStorageBackend extends StorageBackend {

    private static final String SECTION_EXT = ".dat";
    private static final String TMP_EXT = ".tmp";

    private final Path root;
    private final Path sectionsRoot;
    private final Path mappingsRoot;

    /**
     * @param root absolute path to the storage root; created lazily if it doesn't exist
     */
    public FileStorageBackend(Path root) {
        this.root = root;
        this.sectionsRoot = root.resolve("sections");
        this.mappingsRoot = root.resolve("mappings");
        try {
            Files.createDirectories(this.sectionsRoot);
            Files.createDirectories(this.mappingsRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create FileStorageBackend root: " + root, e);
        }
    }

    // ---- key / id encoding -------------------------------------------------------------

    private static String shardOf(long v) {
        // Single-byte hash from the LSB; section ids encode (lvl, x, y, z) so the LSB
        // already mixes spatially-close keys across shards.
        return String.format("%02x", (int) (v & 0xFFL));
    }

    private static String shardOf(int v) {
        return String.format("%02x", v & 0xFF);
    }

    private Path sectionPath(long key) {
        String hex = String.format("%016x", key);
        return this.sectionsRoot.resolve(shardOf(key)).resolve(hex + SECTION_EXT);
    }

    private Path mappingPath(int id) {
        String hex = String.format("%08x", id);
        return this.mappingsRoot.resolve(shardOf(id)).resolve(hex + SECTION_EXT);
    }

    private static long parseSectionKey(Path path) {
        String name = path.getFileName().toString();
        if (!name.endsWith(SECTION_EXT)) return 0L;
        String hex = name.substring(0, name.length() - SECTION_EXT.length());
        try {
            return Long.parseUnsignedLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static int parseMappingId(Path path) {
        String name = path.getFileName().toString();
        if (!name.endsWith(SECTION_EXT)) return 0;
        String hex = name.substring(0, name.length() - SECTION_EXT.length());
        try {
            return Integer.parseUnsignedInt(hex, 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ---- I/O primitives ---------------------------------------------------------------

    /**
     * Atomic write: stream {@code size} bytes from {@code src} into a sibling temp file,
     * then move it onto {@code target}. The parent directory is created lazily.
     */
    private static void atomicWrite(Path target, long src, long size) throws IOException {
        Files.createDirectories(target.getParent());
        Path tmp = target.resolveSibling(target.getFileName().toString() + TMP_EXT);
        try (FileChannel ch = FileChannel.open(tmp,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer bb = MemoryUtil.memByteBuffer(src, (int) Math.min(size, Integer.MAX_VALUE));
            while (bb.hasRemaining()) ch.write(bb);
        }
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFailed) {
            // Some filesystems (rare; mostly Windows shares) can't honour ATOMIC_MOVE
            // across attribute boundaries. Fall back to non-atomic replace; we accept
            // the (very narrow) torn-write window because the alternative is a hard fail.
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ---- section data ------------------------------------------------------------------

    @Override
    public MemoryBuffer getSectionData(long key, MemoryBuffer scratch) {
        Path path = sectionPath(key);
        if (!Files.exists(path)) return null;
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            long size = ch.size();
            if (size > scratch.size) {
                throw new IOException("Section payload (" + size + " bytes) exceeds scratch buffer ("
                        + scratch.size + " bytes)");
            }
            ByteBuffer bb = MemoryUtil.memByteBuffer(scratch.address, (int) size);
            while (bb.hasRemaining()) {
                int n = ch.read(bb);
                if (n < 0) break;
            }
            return MemoryBuffer.wrap(scratch.address, size);
        } catch (IOException e) {
            Logger.error("FileStorageBackend: read failed for " + path, e);
            return null;
        }
    }

    @Override
    public void setSectionData(long key, MemoryBuffer data) {
        Path path = sectionPath(key);
        try {
            atomicWrite(path, data.address, data.size);
        } catch (IOException e) {
            Logger.error("FileStorageBackend: write failed for " + path, e);
        }
    }

    @Override
    public void deleteSectionData(long key) {
        Path path = sectionPath(key);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            Logger.warn("FileStorageBackend: delete failed for " + path + ": " + e.getMessage());
        }
    }

    // ---- id mappings -------------------------------------------------------------------

    @Override
    public void putIdMapping(int id, ByteBuffer data) {
        Path path = mappingPath(id);
        try {
            int origPos = data.position();
            // Bridge an arbitrary ByteBuffer (heap or direct) to atomicWrite's
            // address+size contract: copy into a transient native buffer if needed.
            if (data.isDirect()) {
                long src = MemoryUtil.memAddress(data) + origPos;
                atomicWrite(path, src, data.remaining());
            } else {
                ByteBuffer direct = MemoryUtil.memAlloc(data.remaining());
                try {
                    direct.put(data);
                    direct.flip();
                    atomicWrite(path, MemoryUtil.memAddress(direct), direct.remaining());
                } finally {
                    MemoryUtil.memFree(direct);
                }
            }
            data.position(origPos);
        } catch (IOException e) {
            Logger.error("FileStorageBackend: mapping write failed for " + path, e);
        }
    }

    @Override
    public Int2ObjectOpenHashMap<byte[]> getIdMappingsData() {
        var out = new Int2ObjectOpenHashMap<byte[]>();
        if (!Files.isDirectory(this.mappingsRoot)) return out;
        try (Stream<Path> stream = Files.walk(this.mappingsRoot, 2)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(SECTION_EXT))
                    .forEach(p -> {
                        try {
                            int id = parseMappingId(p);
                            byte[] bytes = Files.readAllBytes(p);
                            out.put(id, bytes);
                        } catch (IOException e) {
                            Logger.warn("FileStorageBackend: skipping unreadable mapping " + p);
                        }
                    });
        } catch (IOException e) {
            Logger.error("FileStorageBackend: mapping enumeration failed", e);
        }
        return out;
    }

    // ---- iteration ---------------------------------------------------------------------

    @Override
    public void iteratePositions(int level, LongConsumer callback) {
        if (!Files.isDirectory(this.sectionsRoot)) return;
        try (Stream<Path> stream = Files.walk(this.sectionsRoot, 2)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(SECTION_EXT))
                    .forEach(p -> {
                        long key = parseSectionKey(p);
                        if (level == -1 || ((key >>> 60) & 0xFL) == level) {
                            callback.accept(key);
                        }
                    });
        } catch (IOException e) {
            Logger.error("FileStorageBackend: position enumeration failed", e);
        }
    }

    // ---- lifecycle ---------------------------------------------------------------------

    @Override public void flush() {
        // Each setSectionData / putIdMapping is its own atomic FileChannel close + move,
        // so there is no in-process buffer to flush. We deliberately do not fsync the
        // parent directories here; the OS metadata journal handles that on the typical
        // platforms (ext4, NTFS, APFS).
    }

    @Override public void close() { /* no resources to release */ }
}
