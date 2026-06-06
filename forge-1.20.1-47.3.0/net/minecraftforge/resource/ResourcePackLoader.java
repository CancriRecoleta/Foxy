//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.resource;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;

public class ResourcePackLoader {
    private static Map<IModFile, PathPackResources> modResourcePacks;

    public ResourcePackLoader() {
    }

    public static Optional<PathPackResources> getPackFor(String modId) {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).map(IModFileInfo::getFile).map((mf) -> {
            return (PathPackResources)modResourcePacks.get(mf);
        });
    }

    /** @deprecated */
    @Deprecated
    public static void loadResourcePacks(PackRepository resourcePacks, BiFunction<Map<IModFile, ? extends PathPackResources>, BiConsumer<? super PathPackResources, Pack>, ? extends RepositorySource> packFinder) {
        loadResourcePacks(resourcePacks, (map) -> {
            return (RepositorySource)packFinder.apply(map, (rp, p) -> {
            });
        });
    }

    public static void loadResourcePacks(PackRepository resourcePacks, Function<Map<IModFile, ? extends PathPackResources>, ? extends RepositorySource> packFinder) {
        modResourcePacks = (Map)ModList.get().getModFiles().stream().filter((mf) -> {
            return mf.requiredLanguageLoaders().stream().noneMatch((ls) -> {
                return ls.languageName().equals("minecraft");
            });
        }).map((mf) -> {
            return Pair.of(mf, createPackForMod(mf));
        }).collect(Collectors.toMap((p) -> {
            return ((IModFileInfo)p.getFirst()).getFile();
        }, Pair::getSecond, (u, v) -> {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Duplicate key %s", u));
        }, LinkedHashMap::new));
        resourcePacks.addPackFinder((RepositorySource)packFinder.apply(modResourcePacks));
    }

    public static @NotNull PathPackResources createPackForMod(final IModFileInfo mf) {
        return new PathPackResources(mf.getFile().getFileName(), true, mf.getFile().getFilePath()) {
            private final IModFile modFile = mf.getFile();

            protected @NotNull Path resolve(String... paths) {
                return this.modFile.findResource(paths);
            }
        };
    }

    public static List<String> getPackNames() {
        return (List)ModList.get().applyForEachModFile((mf) -> {
            List var10000 = mf.getModInfos();
            return "mod:" + ((IModInfo)var10000.get(0)).getModId();
        }).filter((n) -> {
            return !n.equals("mod:minecraft");
        }).collect(Collectors.toList());
    }

    public static <V> Comparator<Map.Entry<String, V>> getSorter() {
        List<String> order = new ArrayList();
        order.add("vanilla");
        order.add("mod_resources");
        Stream var10000 = ModList.get().getModFiles().stream().filter((mf) -> {
            return mf.requiredLanguageLoaders().stream().noneMatch((ls) -> {
                return ls.languageName().equals("minecraft");
            });
        }).map((e) -> {
            return ((IModInfo)e.getMods().get(0)).getModId();
        }).map((e) -> {
            return "mod:" + e;
        });
        Objects.requireNonNull(order);
        var10000.forEach(order::add);
        Object2IntMap<String> order_f = new Object2IntOpenHashMap(order.size());

        for(int x = 0; x < order.size(); ++x) {
            order_f.put((String)order.get(x), x);
        }

        return (e1, e2) -> {
            String s1 = (String)e1.getKey();
            String s2 = (String)e2.getKey();
            int i1 = order_f.getOrDefault(s1, -1);
            int i2 = order_f.getOrDefault(s2, -1);
            if (i1 == i2 && i1 == -1) {
                return s1.compareTo(s2);
            } else if (i1 == -1) {
                return 1;
            } else {
                return i2 == -1 ? -1 : i2 - i1;
            }
        };
    }
}
