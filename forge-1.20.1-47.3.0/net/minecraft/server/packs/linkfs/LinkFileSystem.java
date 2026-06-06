//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.linkfs;

import com.google.common.base.Splitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class LinkFileSystem extends FileSystem {
    private static final Set<String> VIEWS = Set.of("basic");
    public static final String PATH_SEPARATOR = "/";
    private static final Splitter PATH_SPLITTER = Splitter.on('/');
    private final FileStore store;
    private final FileSystemProvider provider = new LinkFSProvider();
    private final LinkFSPath root;

    LinkFileSystem(String p_251238_, DirectoryEntry p_248738_) {
        this.store = new LinkFSFileStore(p_251238_);
        this.root = buildPath(p_248738_, this, "", (LinkFSPath)null);
    }

    private static LinkFSPath buildPath(DirectoryEntry p_250914_, LinkFileSystem p_248904_, String p_248935_, @Nullable LinkFSPath p_250296_) {
        Object2ObjectOpenHashMap<String, LinkFSPath> $$4 = new Object2ObjectOpenHashMap();
        LinkFSPath $$5 = new LinkFSPath(p_248904_, p_248935_, p_250296_, new PathContents.DirectoryContents($$4));
        p_250914_.files.forEach((p_249491_, p_250850_) -> {
            $$4.put(p_249491_, new LinkFSPath(p_248904_, p_249491_, $$5, new PathContents.FileContents(p_250850_)));
        });
        p_250914_.children.forEach((p_251592_, p_251728_) -> {
            $$4.put(p_251592_, buildPath(p_251728_, p_248904_, p_251592_, $$5));
        });
        $$4.trim();
        return $$5;
    }

    public FileSystemProvider provider() {
        return this.provider;
    }

    public void close() {
    }

    public boolean isOpen() {
        return true;
    }

    public boolean isReadOnly() {
        return true;
    }

    public String getSeparator() {
        return "/";
    }

    public Iterable<Path> getRootDirectories() {
        return List.of(this.root);
    }

    public Iterable<FileStore> getFileStores() {
        return List.of(this.store);
    }

    public Set<String> supportedFileAttributeViews() {
        return VIEWS;
    }

    public Path getPath(String p_250018_, String... p_252159_) {
        Stream<String> $$2 = Stream.of(p_250018_);
        if (p_252159_.length > 0) {
            $$2 = Stream.concat($$2, Stream.of(p_252159_));
        }

        String $$3 = (String)$$2.collect(Collectors.joining("/"));
        if ($$3.equals("/")) {
            return this.root;
        } else {
            LinkFSPath $$6;
            Iterator var6;
            String $$7;
            if ($$3.startsWith("/")) {
                $$6 = this.root;

                for(var6 = PATH_SPLITTER.split($$3.substring(1)).iterator(); var6.hasNext(); $$6 = $$6.resolveName($$7)) {
                    $$7 = (String)var6.next();
                    if ($$7.isEmpty()) {
                        throw new IllegalArgumentException("Empty paths not allowed");
                    }
                }

                return $$6;
            } else {
                $$6 = null;

                for(var6 = PATH_SPLITTER.split($$3).iterator(); var6.hasNext(); $$6 = new LinkFSPath(this, $$7, $$6, PathContents.RELATIVE)) {
                    $$7 = (String)var6.next();
                    if ($$7.isEmpty()) {
                        throw new IllegalArgumentException("Empty paths not allowed");
                    }
                }

                if ($$6 == null) {
                    throw new IllegalArgumentException("Empty paths not allowed");
                } else {
                    return $$6;
                }
            }
        }
    }

    public PathMatcher getPathMatcher(String p_250757_) {
        throw new UnsupportedOperationException();
    }

    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException();
    }

    public WatchService newWatchService() {
        throw new UnsupportedOperationException();
    }

    public FileStore store() {
        return this.store;
    }

    public LinkFSPath rootPath() {
        return this.root;
    }

    public static Builder builder() {
        return new Builder();
    }

    static record DirectoryEntry(Map<String, DirectoryEntry> children, Map<String, Path> files) {
        public DirectoryEntry() {
            this(new HashMap(), new HashMap());
        }

        private DirectoryEntry(Map<String, DirectoryEntry> children, Map<String, Path> files) {
            this.children = children;
            this.files = files;
        }

        public Map<String, DirectoryEntry> children() {
            return this.children;
        }

        public Map<String, Path> files() {
            return this.files;
        }
    }

    public static class Builder {
        private final DirectoryEntry root = new DirectoryEntry();

        public Builder() {
        }

        public Builder put(List<String> p_249758_, String p_251234_, Path p_248766_) {
            DirectoryEntry $$3 = this.root;

            String $$4;
            for(Iterator var5 = p_249758_.iterator(); var5.hasNext(); $$3 = (DirectoryEntry)$$3.children.computeIfAbsent($$4, (p_249671_) -> {
                return new DirectoryEntry();
            })) {
                $$4 = (String)var5.next();
            }

            $$3.files.put(p_251234_, p_248766_);
            return this;
        }

        public Builder put(List<String> p_250158_, Path p_250483_) {
            if (p_250158_.isEmpty()) {
                throw new IllegalArgumentException("Path can't be empty");
            } else {
                int $$2 = p_250158_.size() - 1;
                return this.put(p_250158_.subList(0, $$2), (String)p_250158_.get($$2), p_250483_);
            }
        }

        public FileSystem build(String p_251975_) {
            return new LinkFileSystem(p_251975_, this.root);
        }
    }
}
