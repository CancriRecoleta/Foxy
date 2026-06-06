//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.validation;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class PathAllowList implements PathMatcher {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String COMMENT_PREFIX = "#";
    private final List<ConfigEntry> entries;
    private final Map<String, PathMatcher> compiledPaths = new ConcurrentHashMap();

    public PathAllowList(List<ConfigEntry> p_289956_) {
        this.entries = p_289956_;
    }

    public PathMatcher getForFileSystem(FileSystem p_289975_) {
        return (PathMatcher)this.compiledPaths.computeIfAbsent(p_289975_.provider().getScheme(), (p_289958_) -> {
            List $$4;
            try {
                $$4 = this.entries.stream().map((p_289937_) -> {
                    return p_289937_.compile(p_289975_);
                }).toList();
            } catch (Exception var5) {
                Exception $$3 = var5;
                LOGGER.error("Failed to compile file pattern list", $$3);
                return (p_289987_) -> {
                    return false;
                };
            }

            PathMatcher var10000;
            switch ($$4.size()) {
                case 0 -> var10000 = (p_289982_) -> {
    return false;
};
                case 1 -> var10000 = (PathMatcher)$$4.get(0);
                default -> var10000 = (p_289927_) -> {
    Iterator var2 = $$4.iterator();

    PathMatcher $$2;
    do {
        if (!var2.hasNext()) {
            return false;
        }

        $$2 = (PathMatcher)var2.next();
    } while(!$$2.matches(p_289927_));

    return true;
};
            }

            return var10000;
        });
    }

    public boolean matches(Path p_289964_) {
        return this.getForFileSystem(p_289964_.getFileSystem()).matches(p_289964_);
    }

    public static PathAllowList readPlain(BufferedReader p_289921_) {
        return new PathAllowList(p_289921_.lines().flatMap((p_289962_) -> {
            return net.minecraft.world.level.validation.PathAllowList.ConfigEntry.parse(p_289962_).stream();
        }).toList());
    }

    public static record ConfigEntry(EntryType type, String pattern) {
        public ConfigEntry(EntryType type, String pattern) {
            this.type = type;
            this.pattern = pattern;
        }

        public PathMatcher compile(FileSystem p_289936_) {
            return this.type().compile(p_289936_, this.pattern);
        }

        static Optional<ConfigEntry> parse(String p_289947_) {
            if (!p_289947_.isBlank() && !p_289947_.startsWith("#")) {
                if (!p_289947_.startsWith("[")) {
                    return Optional.of(new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.PREFIX, p_289947_));
                } else {
                    int $$1 = p_289947_.indexOf(93, 1);
                    if ($$1 == -1) {
                        throw new IllegalArgumentException("Unterminated type in line '" + p_289947_ + "'");
                    } else {
                        String $$2 = p_289947_.substring(1, $$1);
                        String $$3 = p_289947_.substring($$1 + 1);
                        Optional var10000;
                        switch ($$2) {
                            case "glob":
                            case "regex":
                                var10000 = Optional.of(new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.FILESYSTEM, $$2 + ":" + $$3));
                                break;
                            case "prefix":
                                var10000 = Optional.of(new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.PREFIX, $$3));
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported definition type in line '" + p_289947_ + "'");
                        }

                        return var10000;
                    }
                }
            } else {
                return Optional.empty();
            }
        }

        static ConfigEntry glob(String p_289983_) {
            return new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.FILESYSTEM, "glob:" + p_289983_);
        }

        static ConfigEntry regex(String p_289944_) {
            return new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.FILESYSTEM, "regex:" + p_289944_);
        }

        static ConfigEntry prefix(String p_289918_) {
            return new ConfigEntry(net.minecraft.world.level.validation.PathAllowList.EntryType.PREFIX, p_289918_);
        }

        public EntryType type() {
            return this.type;
        }

        public String pattern() {
            return this.pattern;
        }
    }

    @FunctionalInterface
    public interface EntryType {
        EntryType FILESYSTEM = FileSystem::getPathMatcher;
        EntryType PREFIX = (p_289949_, p_289938_) -> {
            return (p_289955_) -> {
                return p_289955_.toString().startsWith(p_289938_);
            };
        };

        PathMatcher compile(FileSystem var1, String var2);
    }
}
