//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURE_DIRECTORY_NAME = "structures";
    private static final String TEST_STRUCTURES_DIR = "gameteststructures";
    private static final String STRUCTURE_FILE_EXTENSION = ".nbt";
    private static final String STRUCTURE_TEXT_FILE_EXTENSION = ".snbt";
    private final Map<ResourceLocation, Optional<StructureTemplate>> structureRepository = Maps.newConcurrentMap();
    private final DataFixer fixerUpper;
    private ResourceManager resourceManager;
    private final Path generatedDir;
    private final List<Source> sources;
    private final HolderGetter<Block> blockLookup;
    private static final FileToIdConverter LISTER = new FileToIdConverter("structures", ".nbt");

    public StructureTemplateManager(ResourceManager p_249872_, LevelStorageSource.LevelStorageAccess p_249864_, DataFixer p_249868_, HolderGetter<Block> p_256126_) {
        this.resourceManager = p_249872_;
        this.fixerUpper = p_249868_;
        this.generatedDir = p_249864_.getLevelPath(LevelResource.GENERATED_DIR).normalize();
        this.blockLookup = p_256126_;
        ImmutableList.Builder<Source> $$4 = ImmutableList.builder();
        $$4.add(new Source(this::loadFromGenerated, this::listGenerated));
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            $$4.add(new Source(this::loadFromTestStructures, this::listTestStructures));
        }

        $$4.add(new Source(this::loadFromResource, this::listResources));
        this.sources = $$4.build();
    }

    public StructureTemplate getOrCreate(ResourceLocation p_230360_) {
        Optional<StructureTemplate> $$1 = this.get(p_230360_);
        if ($$1.isPresent()) {
            return (StructureTemplate)$$1.get();
        } else {
            StructureTemplate $$2 = new StructureTemplate();
            this.structureRepository.put(p_230360_, Optional.of($$2));
            return $$2;
        }
    }

    public Optional<StructureTemplate> get(ResourceLocation p_230408_) {
        return (Optional)this.structureRepository.computeIfAbsent(p_230408_, this::tryLoad);
    }

    public Stream<ResourceLocation> listTemplates() {
        return this.sources.stream().flatMap((p_230376_) -> {
            return (Stream)p_230376_.lister().get();
        }).distinct();
    }

    private Optional<StructureTemplate> tryLoad(ResourceLocation p_230426_) {
        Iterator var2 = this.sources.iterator();

        while(var2.hasNext()) {
            Source $$1 = (Source)var2.next();

            try {
                Optional<StructureTemplate> $$2 = (Optional)$$1.loader().apply(p_230426_);
                if ($$2.isPresent()) {
                    return $$2;
                }
            } catch (Exception var5) {
            }
        }

        return Optional.empty();
    }

    public void onResourceManagerReload(ResourceManager p_230371_) {
        this.resourceManager = p_230371_;
        this.structureRepository.clear();
    }

    private Optional<StructureTemplate> loadFromResource(ResourceLocation p_230428_) {
        ResourceLocation $$1 = LISTER.idToFile(p_230428_);
        return this.load(() -> {
            return this.resourceManager.open($$1);
        }, (p_230366_) -> {
            LOGGER.error("Couldn't load structure {}", p_230428_, p_230366_);
        });
    }

    private Stream<ResourceLocation> listResources() {
        Stream var10000 = LISTER.listMatchingResources(this.resourceManager).keySet().stream();
        FileToIdConverter var10001 = LISTER;
        Objects.requireNonNull(var10001);
        return var10000.map(var10001::fileToId);
    }

    private Optional<StructureTemplate> loadFromTestStructures(ResourceLocation p_230430_) {
        return this.loadFromSnbt(p_230430_, Paths.get("gameteststructures"));
    }

    private Stream<ResourceLocation> listTestStructures() {
        return this.listFolderContents(Paths.get("gameteststructures"), "minecraft", ".snbt");
    }

    private Optional<StructureTemplate> loadFromGenerated(ResourceLocation p_230432_) {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Optional.empty();
        } else {
            Path $$1 = createAndValidatePathToStructure(this.generatedDir, p_230432_, ".nbt");
            return this.load(() -> {
                return new FileInputStream($$1.toFile());
            }, (p_230400_) -> {
                LOGGER.error("Couldn't load structure from {}", $$1, p_230400_);
            });
        }
    }

    private Stream<ResourceLocation> listGenerated() {
        if (!Files.isDirectory(this.generatedDir, new LinkOption[0])) {
            return Stream.empty();
        } else {
            try {
                return Files.list(this.generatedDir).filter((p_230419_) -> {
                    return Files.isDirectory(p_230419_, new LinkOption[0]);
                }).flatMap((p_230410_) -> {
                    return this.listGeneratedInNamespace(p_230410_);
                });
            } catch (IOException var2) {
                return Stream.empty();
            }
        }
    }

    private Stream<ResourceLocation> listGeneratedInNamespace(Path p_230389_) {
        Path $$1 = p_230389_.resolve("structures");
        return this.listFolderContents($$1, p_230389_.getFileName().toString(), ".nbt");
    }

    private Stream<ResourceLocation> listFolderContents(Path p_230395_, String p_230396_, String p_230397_) {
        if (!Files.isDirectory(p_230395_, new LinkOption[0])) {
            return Stream.empty();
        } else {
            int $$3 = p_230397_.length();
            Function<String, String> $$4 = (p_230358_) -> {
                return p_230358_.substring(0, p_230358_.length() - $$3);
            };

            try {
                return Files.walk(p_230395_).filter((p_230381_) -> {
                    return p_230381_.toString().endsWith(p_230397_);
                }).mapMulti((p_230386_, p_230387_) -> {
                    try {
                        p_230387_.accept(new ResourceLocation(p_230396_, (String)$$4.apply(this.relativize(p_230395_, p_230386_))));
                    } catch (ResourceLocationException var7) {
                        ResourceLocationException $$5 = var7;
                        LOGGER.error("Invalid location while listing pack contents", $$5);
                    }

                });
            } catch (IOException var7) {
                IOException $$5 = var7;
                LOGGER.error("Failed to list folder contents", $$5);
                return Stream.empty();
            }
        }
    }

    private String relativize(Path p_230402_, Path p_230403_) {
        return p_230402_.relativize(p_230403_).toString().replace(File.separator, "/");
    }

    private Optional<StructureTemplate> loadFromSnbt(ResourceLocation p_230368_, Path p_230369_) {
        if (!Files.isDirectory(p_230369_, new LinkOption[0])) {
            return Optional.empty();
        } else {
            Path $$2 = FileUtil.createPathToResource(p_230369_, p_230368_.getPath(), ".snbt");

            try {
                BufferedReader $$3 = Files.newBufferedReader($$2);

                Optional var6;
                try {
                    String $$4 = IOUtils.toString($$3);
                    var6 = Optional.of(this.readStructure(NbtUtils.snbtToStructure($$4)));
                } catch (Throwable var8) {
                    if ($$3 != null) {
                        try {
                            $$3.close();
                        } catch (Throwable var7) {
                            var8.addSuppressed(var7);
                        }
                    }

                    throw var8;
                }

                if ($$3 != null) {
                    $$3.close();
                }

                return var6;
            } catch (NoSuchFileException var9) {
                return Optional.empty();
            } catch (CommandSyntaxException | IOException var10) {
                Exception $$6 = var10;
                LOGGER.error("Couldn't load structure from {}", $$2, $$6);
                return Optional.empty();
            }
        }
    }

    private Optional<StructureTemplate> load(InputStreamOpener p_230373_, Consumer<Throwable> p_230374_) {
        try {
            InputStream $$2 = p_230373_.open();

            Optional var4;
            try {
                var4 = Optional.of(this.readStructure($$2));
            } catch (Throwable var7) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return var4;
        } catch (FileNotFoundException var8) {
            return Optional.empty();
        } catch (Throwable var9) {
            Throwable $$4 = var9;
            p_230374_.accept($$4);
            return Optional.empty();
        }
    }

    private StructureTemplate readStructure(InputStream p_230378_) throws IOException {
        CompoundTag $$1 = NbtIo.readCompressed(p_230378_);
        return this.readStructure($$1);
    }

    public StructureTemplate readStructure(CompoundTag p_230405_) {
        StructureTemplate $$1 = new StructureTemplate();
        int $$2 = NbtUtils.getDataVersion(p_230405_, 500);
        $$1.load(this.blockLookup, DataFixTypes.STRUCTURE.updateToCurrentVersion(this.fixerUpper, p_230405_, $$2));
        return $$1;
    }

    public boolean save(ResourceLocation p_230417_) {
        Optional<StructureTemplate> $$1 = (Optional)this.structureRepository.get(p_230417_);
        if (!$$1.isPresent()) {
            return false;
        } else {
            StructureTemplate $$2 = (StructureTemplate)$$1.get();
            Path $$3 = createAndValidatePathToStructure(this.generatedDir, p_230417_, ".nbt");
            Path $$4 = $$3.getParent();
            if ($$4 == null) {
                return false;
            } else {
                try {
                    Files.createDirectories(Files.exists($$4, new LinkOption[0]) ? $$4.toRealPath() : $$4);
                } catch (IOException var13) {
                    LOGGER.error("Failed to create parent directory: {}", $$4);
                    return false;
                }

                CompoundTag $$6 = $$2.save(new CompoundTag());

                try {
                    OutputStream $$7 = new FileOutputStream($$3.toFile());

                    try {
                        NbtIo.writeCompressed($$6, (OutputStream)$$7);
                    } catch (Throwable var11) {
                        try {
                            $$7.close();
                        } catch (Throwable var10) {
                            var11.addSuppressed(var10);
                        }

                        throw var11;
                    }

                    $$7.close();
                    return true;
                } catch (Throwable var12) {
                    return false;
                }
            }
        }
    }

    public Path getPathToGeneratedStructure(ResourceLocation p_230362_, String p_230363_) {
        return createPathToStructure(this.generatedDir, p_230362_, p_230363_);
    }

    public static Path createPathToStructure(Path p_230391_, ResourceLocation p_230392_, String p_230393_) {
        try {
            Path $$3 = p_230391_.resolve(p_230392_.getNamespace());
            Path $$4 = $$3.resolve("structures");
            return FileUtil.createPathToResource($$4, p_230392_.getPath(), p_230393_);
        } catch (InvalidPathException var5) {
            InvalidPathException $$5 = var5;
            throw new ResourceLocationException("Invalid resource path: " + p_230392_, $$5);
        }
    }

    private static Path createAndValidatePathToStructure(Path p_230412_, ResourceLocation p_230413_, String p_230414_) {
        if (p_230413_.getPath().contains("//")) {
            throw new ResourceLocationException("Invalid resource path: " + p_230413_);
        } else {
            Path $$3 = createPathToStructure(p_230412_, p_230413_, p_230414_);
            if ($$3.startsWith(p_230412_) && FileUtil.isPathNormalized($$3) && FileUtil.isPathPortable($$3)) {
                return $$3;
            } else {
                throw new ResourceLocationException("Invalid resource path: " + $$3);
            }
        }
    }

    public void remove(ResourceLocation p_230422_) {
        this.structureRepository.remove(p_230422_);
    }

    static record Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
        Source(Function<ResourceLocation, Optional<StructureTemplate>> loader, Supplier<Stream<ResourceLocation>> lister) {
            this.loader = loader;
            this.lister = lister;
        }

        public Function<ResourceLocation, Optional<StructureTemplate>> loader() {
            return this.loader;
        }

        public Supplier<Stream<ResourceLocation>> lister() {
            return this.lister;
        }
    }

    @FunctionalInterface
    interface InputStreamOpener {
        InputStream open() throws IOException;
    }
}
