//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
    private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final int MAX_FILE_NAME = 255;
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
    private static final Pattern STRICT_PATH_SEGMENT_CHECK = Pattern.compile("[-._a-z0-9]+");

    public FileUtil() {
    }

    public static String findAvailableName(Path p_133731_, String p_133732_, String p_133733_) throws IOException {
        char[] var3 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
        int $$5 = var3.length;

        for(int var5 = 0; var5 < $$5; ++var5) {
            char $$3 = var3[var5];
            p_133732_ = p_133732_.replace($$3, '_');
        }

        p_133732_ = p_133732_.replaceAll("[./\"]", "_");
        if (RESERVED_WINDOWS_FILENAMES.matcher(p_133732_).matches()) {
            p_133732_ = "_" + p_133732_ + "_";
        }

        Matcher $$4 = COPY_COUNTER_PATTERN.matcher(p_133732_);
        $$5 = 0;
        if ($$4.matches()) {
            p_133732_ = $$4.group("name");
            $$5 = Integer.parseInt($$4.group("count"));
        }

        if (p_133732_.length() > 255 - p_133733_.length()) {
            p_133732_ = p_133732_.substring(0, 255 - p_133733_.length());
        }

        while(true) {
            String $$6 = p_133732_;
            if ($$5 != 0) {
                String $$7 = " (" + $$5 + ")";
                int $$8 = 255 - $$7.length();
                if (p_133732_.length() > $$8) {
                    $$6 = p_133732_.substring(0, $$8);
                }

                $$6 = $$6 + $$7;
            }

            $$6 = $$6 + p_133733_;
            Path $$9 = p_133731_.resolve($$6);

            try {
                Path $$10 = Files.createDirectory($$9);
                Files.deleteIfExists($$10);
                return p_133731_.relativize($$10).toString();
            } catch (FileAlreadyExistsException var8) {
                ++$$5;
            }
        }
    }

    public static boolean isPathNormalized(Path p_133729_) {
        Path $$1 = p_133729_.normalize();
        return $$1.equals(p_133729_);
    }

    public static boolean isPathPortable(Path p_133735_) {
        Iterator var1 = p_133735_.iterator();

        Path $$1;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            $$1 = (Path)var1.next();
        } while(!RESERVED_WINDOWS_FILENAMES.matcher($$1.toString()).matches());

        return false;
    }

    public static Path createPathToResource(Path p_133737_, String p_133738_, String p_133739_) {
        String $$3 = p_133738_ + p_133739_;
        Path $$4 = Paths.get($$3);
        if ($$4.endsWith(p_133739_)) {
            throw new InvalidPathException($$3, "empty resource name");
        } else {
            return p_133737_.resolve($$4);
        }
    }

    public static String getFullResourcePath(String p_179923_) {
        return FilenameUtils.getFullPath(p_179923_).replace(File.separator, "/");
    }

    public static String normalizeResourcePath(String p_179925_) {
        return FilenameUtils.normalize(p_179925_).replace(File.separator, "/");
    }

    public static DataResult<List<String>> decomposePath(String p_248866_) {
        int $$1 = p_248866_.indexOf(47);
        if ($$1 == -1) {
            DataResult var10000;
            switch (p_248866_) {
                case "":
                case ".":
                case "..":
                    var10000 = DataResult.error(() -> {
                        return "Invalid path '" + p_248866_ + "'";
                    });
                    break;
                default:
                    var10000 = !isValidStrictPathSegment(p_248866_) ? DataResult.error(() -> {
                        return "Invalid path '" + p_248866_ + "'";
                    }) : DataResult.success(List.of(p_248866_));
            }

            return var10000;
        } else {
            List<String> $$2 = new ArrayList();
            int $$3 = 0;
            boolean $$4 = false;

            while(true) {
                switch (p_248866_.substring($$3, $$1)) {
                    case "":
                    case ".":
                    case "..":
                        return DataResult.error(() -> {
                            return "Invalid segment '" + $$5 + "' in path '" + p_248866_ + "'";
                        });
                }

                if (!isValidStrictPathSegment($$5)) {
                    return DataResult.error(() -> {
                        return "Invalid segment '" + $$5 + "' in path '" + p_248866_ + "'";
                    });
                }

                $$2.add($$5);
                if ($$4) {
                    return DataResult.success($$2);
                }

                $$3 = $$1 + 1;
                $$1 = p_248866_.indexOf(47, $$3);
                if ($$1 == -1) {
                    $$1 = p_248866_.length();
                    $$4 = true;
                }
            }
        }
    }

    public static Path resolvePath(Path p_251522_, List<String> p_251495_) {
        int $$2 = p_251495_.size();
        Path var10000;
        switch ($$2) {
            case 0:
                var10000 = p_251522_;
                break;
            case 1:
                var10000 = p_251522_.resolve((String)p_251495_.get(0));
                break;
            default:
                String[] $$3 = new String[$$2 - 1];

                for(int $$4 = 1; $$4 < $$2; ++$$4) {
                    $$3[$$4 - 1] = (String)p_251495_.get($$4);
                }

                var10000 = p_251522_.resolve(p_251522_.getFileSystem().getPath((String)p_251495_.get(0), $$3));
        }

        return var10000;
    }

    public static boolean isValidStrictPathSegment(String p_249814_) {
        return STRICT_PATH_SEGMENT_CHECK.matcher(p_249814_).matches();
    }

    public static void validatePath(String... p_249502_) {
        if (p_249502_.length == 0) {
            throw new IllegalArgumentException("Path must have at least one element");
        } else {
            String[] var1 = p_249502_;
            int var2 = p_249502_.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String $$1 = var1[var3];
                if ($$1.equals("..") || $$1.equals(".") || !isValidStrictPathSegment($$1)) {
                    throw new IllegalArgumentException("Illegal segment " + $$1 + " in path " + Arrays.toString(p_249502_));
                }
            }

        }
    }

    public static void createDirectoriesSafe(Path p_259902_) throws IOException {
        Files.createDirectories(Files.exists(p_259902_, new LinkOption[0]) ? p_259902_.toRealPath() : p_259902_);
    }
}
