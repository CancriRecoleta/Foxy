//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt implements DataProvider {
    @Nullable
    private static final Path DUMP_SNBT_TO = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final Iterable<Path> inputFolders;
    private final List<Filter> filters = Lists.newArrayList();

    public SnbtToNbt(PackOutput p_249104_, Iterable<Path> p_249523_) {
        this.output = p_249104_;
        this.inputFolders = p_249523_;
    }

    public SnbtToNbt addFilter(Filter p_126476_) {
        this.filters.add(p_126476_);
        return this;
    }

    private CompoundTag applyFilters(String p_126461_, CompoundTag p_126462_) {
        CompoundTag $$2 = p_126462_;

        Filter $$3;
        for(Iterator var4 = this.filters.iterator(); var4.hasNext(); $$2 = $$3.apply(p_126461_, $$2)) {
            $$3 = (Filter)var4.next();
        }

        return $$2;
    }

    public CompletableFuture<?> run(CachedOutput p_254336_) {
        Path $$1 = this.output.getOutputFolder();
        List<CompletableFuture<?>> $$2 = Lists.newArrayList();
        Iterator var4 = this.inputFolders.iterator();

        while(var4.hasNext()) {
            Path $$3 = (Path)var4.next();
            $$2.add(CompletableFuture.supplyAsync(() -> {
                try {
                    Stream<Path> $$3x = Files.walk($$3);

                    CompletableFuture var5;
                    try {
                        var5 = CompletableFuture.allOf((CompletableFuture[])$$3x.filter((p_126464_) -> {
                            return p_126464_.toString().endsWith(".snbt");
                        }).map((p_253432_) -> {
                            return CompletableFuture.runAsync(() -> {
                                TaskResult $$4 = this.readStructure(p_253432_, this.getName($$3, p_253432_));
                                this.storeStructureIfChanged(p_254336_, $$4, $$1);
                            }, Util.backgroundExecutor());
                        }).toArray((p_253433_) -> {
                            return new CompletableFuture[p_253433_];
                        }));
                    } catch (Throwable var8) {
                        if ($$3x != null) {
                            try {
                                $$3x.close();
                            } catch (Throwable var7) {
                                var8.addSuppressed(var7);
                            }
                        }

                        throw var8;
                    }

                    if ($$3x != null) {
                        $$3x.close();
                    }

                    return var5;
                } catch (Exception var9) {
                    Exception $$4 = var9;
                    throw new RuntimeException("Failed to read structure input directory, aborting", $$4);
                }
            }, Util.backgroundExecutor()).thenCompose((p_253441_) -> {
                return p_253441_;
            }));
        }

        return Util.sequenceFailFast($$2);
    }

    public final String getName() {
        return "SNBT -> NBT";
    }

    private String getName(Path p_126469_, Path p_126470_) {
        String $$2 = p_126469_.relativize(p_126470_).toString().replaceAll("\\\\", "/");
        return $$2.substring(0, $$2.length() - ".snbt".length());
    }

    private TaskResult readStructure(Path p_126466_, String p_126467_) {
        try {
            BufferedReader $$2 = Files.newBufferedReader(p_126466_);

            TaskResult var11;
            try {
                String $$3 = IOUtils.toString($$2);
                CompoundTag $$4 = this.applyFilters(p_126467_, NbtUtils.snbtToStructure($$3));
                ByteArrayOutputStream $$5 = new ByteArrayOutputStream();
                HashingOutputStream $$6 = new HashingOutputStream(Hashing.sha1(), $$5);
                NbtIo.writeCompressed($$4, (OutputStream)$$6);
                byte[] $$7 = $$5.toByteArray();
                HashCode $$8 = $$6.hash();
                String $$10;
                if (DUMP_SNBT_TO != null) {
                    $$10 = NbtUtils.structureToSnbt($$4);
                } else {
                    $$10 = null;
                }

                var11 = new TaskResult(p_126467_, $$7, $$10, $$8);
            } catch (Throwable var13) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                    }
                }

                throw var13;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return var11;
        } catch (Throwable var14) {
            Throwable $$11 = var14;
            throw new StructureConversionException(p_126466_, $$11);
        }
    }

    private void storeStructureIfChanged(CachedOutput p_236394_, TaskResult p_236395_, Path p_236396_) {
        Path $$5;
        IOException $$6;
        if (p_236395_.snbtPayload != null) {
            $$5 = DUMP_SNBT_TO.resolve(p_236395_.name + ".snbt");

            try {
                NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, $$5, p_236395_.snbtPayload);
            } catch (IOException var7) {
                $$6 = var7;
                LOGGER.error("Couldn't write structure SNBT {} at {}", new Object[]{p_236395_.name, $$5, $$6});
            }
        }

        $$5 = p_236396_.resolve(p_236395_.name + ".nbt");

        try {
            p_236394_.writeIfNeeded($$5, p_236395_.payload, p_236395_.hash);
        } catch (IOException var6) {
            $$6 = var6;
            LOGGER.error("Couldn't write structure {} at {}", new Object[]{p_236395_.name, $$5, $$6});
        }

    }

    @FunctionalInterface
    public interface Filter {
        CompoundTag apply(String var1, CompoundTag var2);
    }

    static record TaskResult(String name, byte[] payload, @Nullable String snbtPayload, HashCode hash) {
        TaskResult(String name, byte[] payload, @Nullable String snbtPayload, HashCode hash) {
            this.name = name;
            this.payload = payload;
            this.snbtPayload = snbtPayload;
            this.hash = hash;
        }

        public String name() {
            return this.name;
        }

        public byte[] payload() {
            return this.payload;
        }

        @Nullable
        public String snbtPayload() {
            return this.snbtPayload;
        }

        public HashCode hash() {
            return this.hash;
        }
    }

    private static class StructureConversionException extends RuntimeException {
        public StructureConversionException(Path p_176820_, Throwable p_176821_) {
            super(p_176820_.toAbsolutePath().toString(), p_176821_);
        }
    }
}
