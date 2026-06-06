//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.font.providers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UnihexProvider implements GlyphProvider {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int GLYPH_HEIGHT = 16;
    private static final int DIGITS_PER_BYTE = 2;
    private static final int DIGITS_FOR_WIDTH_8 = 32;
    private static final int DIGITS_FOR_WIDTH_16 = 64;
    private static final int DIGITS_FOR_WIDTH_24 = 96;
    private static final int DIGITS_FOR_WIDTH_32 = 128;
    private final CodepointMap<Glyph> glyphs;

    UnihexProvider(CodepointMap<Glyph> p_285457_) {
        this.glyphs = p_285457_;
    }

    @Nullable
    public GlyphInfo getGlyph(int p_285239_) {
        return (GlyphInfo)this.glyphs.get(p_285239_);
    }

    public IntSet getSupportedGlyphs() {
        return this.glyphs.keySet();
    }

    @VisibleForTesting
    static void unpackBitsToBytes(IntBuffer p_285211_, int p_285508_, int p_285312_, int p_285412_) {
        int $$4 = 32 - p_285312_ - 1;
        int $$5 = 32 - p_285412_ - 1;

        for(int $$6 = $$4; $$6 >= $$5; --$$6) {
            if ($$6 < 32 && $$6 >= 0) {
                boolean $$7 = (p_285508_ >> $$6 & 1) != 0;
                p_285211_.put($$7 ? -1 : 0);
            } else {
                p_285211_.put(0);
            }
        }

    }

    static void unpackBitsToBytes(IntBuffer p_285283_, LineData p_285485_, int p_284940_, int p_284950_) {
        for(int $$4 = 0; $$4 < 16; ++$$4) {
            int $$5 = p_285485_.line($$4);
            unpackBitsToBytes(p_285283_, $$5, p_284940_, p_284950_);
        }

    }

    @VisibleForTesting
    static void readFromStream(InputStream p_285315_, ReaderOutput p_285353_) throws IOException {
        int $$2 = 0;
        ByteList $$3 = new ByteArrayList(128);

        while(true) {
            boolean $$4 = copyUntil(p_285315_, $$3, 58);
            int $$5 = $$3.size();
            if ($$5 == 0 && !$$4) {
                return;
            }

            if (!$$4 || $$5 != 4 && $$5 != 5 && $$5 != 6) {
                throw new IllegalArgumentException("Invalid entry at line " + $$2 + ": expected 4, 5 or 6 hex digits followed by a colon");
            }

            int $$6 = 0;

            int $$8;
            for($$8 = 0; $$8 < $$5; ++$$8) {
                $$6 = $$6 << 4 | decodeHex($$2, $$3.getByte($$8));
            }

            $$3.clear();
            copyUntil(p_285315_, $$3, 10);
            $$8 = $$3.size();
            LineData var10000;
            switch ($$8) {
                case 32 -> var10000 = net.minecraft.client.gui.font.providers.UnihexProvider.ByteContents.read($$2, $$3);
                case 64 -> var10000 = net.minecraft.client.gui.font.providers.UnihexProvider.ShortContents.read($$2, $$3);
                case 96 -> var10000 = net.minecraft.client.gui.font.providers.UnihexProvider.IntContents.read24($$2, $$3);
                case 128 -> var10000 = net.minecraft.client.gui.font.providers.UnihexProvider.IntContents.read32($$2, $$3);
                default -> throw new IllegalArgumentException("Invalid entry at line " + $$2 + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
            }

            LineData $$9 = var10000;
            p_285353_.accept($$6, $$9);
            ++$$2;
            $$3.clear();
        }
    }

    static int decodeHex(int p_285205_, ByteList p_285268_, int p_285345_) {
        return decodeHex(p_285205_, p_285268_.getByte(p_285345_));
    }

    private static int decodeHex(int p_284952_, byte p_285036_) {
        byte var10000;
        switch (p_285036_) {
            case 48:
                var10000 = 0;
                break;
            case 49:
                var10000 = 1;
                break;
            case 50:
                var10000 = 2;
                break;
            case 51:
                var10000 = 3;
                break;
            case 52:
                var10000 = 4;
                break;
            case 53:
                var10000 = 5;
                break;
            case 54:
                var10000 = 6;
                break;
            case 55:
                var10000 = 7;
                break;
            case 56:
                var10000 = 8;
                break;
            case 57:
                var10000 = 9;
                break;
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            default:
                throw new IllegalArgumentException("Invalid entry at line " + p_284952_ + ": expected hex digit, got " + (char)p_285036_);
            case 65:
                var10000 = 10;
                break;
            case 66:
                var10000 = 11;
                break;
            case 67:
                var10000 = 12;
                break;
            case 68:
                var10000 = 13;
                break;
            case 69:
                var10000 = 14;
                break;
            case 70:
                var10000 = 15;
        }

        return var10000;
    }

    private static boolean copyUntil(InputStream p_284994_, ByteList p_285351_, int p_285177_) throws IOException {
        while(true) {
            int $$3 = p_284994_.read();
            if ($$3 == -1) {
                return false;
            }

            if ($$3 == p_285177_) {
                return true;
            }

            p_285351_.add((byte)$$3);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface LineData {
        int line(int var1);

        int bitWidth();

        default int mask() {
            int $$0 = 0;

            for(int $$1 = 0; $$1 < 16; ++$$1) {
                $$0 |= this.line($$1);
            }

            return $$0;
        }

        default int calculateWidth() {
            int $$0 = this.mask();
            int $$1 = this.bitWidth();
            int $$4;
            int $$5;
            if ($$0 == 0) {
                $$4 = 0;
                $$5 = $$1;
            } else {
                $$4 = Integer.numberOfLeadingZeros($$0);
                $$5 = 32 - Integer.numberOfTrailingZeros($$0) - 1;
            }

            return net.minecraft.client.gui.font.providers.UnihexProvider.Dimensions.pack($$4, $$5);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record ByteContents(byte[] contents) implements LineData {
        private ByteContents(byte[] contents) {
            this.contents = contents;
        }

        public int line(int p_285203_) {
            return this.contents[p_285203_] << 24;
        }

        static LineData read(int p_285080_, ByteList p_285481_) {
            byte[] $$2 = new byte[16];
            int $$3 = 0;

            for(int $$4 = 0; $$4 < 16; ++$$4) {
                int $$5 = UnihexProvider.decodeHex(p_285080_, p_285481_, $$3++);
                int $$6 = UnihexProvider.decodeHex(p_285080_, p_285481_, $$3++);
                byte $$7 = (byte)($$5 << 4 | $$6);
                $$2[$$4] = $$7;
            }

            return new ByteContents($$2);
        }

        public int bitWidth() {
            return 8;
        }

        public byte[] contents() {
            return this.contents;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static record ShortContents(short[] contents) implements LineData {
        private ShortContents(short[] contents) {
            this.contents = contents;
        }

        public int line(int p_285158_) {
            return this.contents[p_285158_] << 16;
        }

        static LineData read(int p_285528_, ByteList p_284958_) {
            short[] $$2 = new short[16];
            int $$3 = 0;

            for(int $$4 = 0; $$4 < 16; ++$$4) {
                int $$5 = UnihexProvider.decodeHex(p_285528_, p_284958_, $$3++);
                int $$6 = UnihexProvider.decodeHex(p_285528_, p_284958_, $$3++);
                int $$7 = UnihexProvider.decodeHex(p_285528_, p_284958_, $$3++);
                int $$8 = UnihexProvider.decodeHex(p_285528_, p_284958_, $$3++);
                short $$9 = (short)($$5 << 12 | $$6 << 8 | $$7 << 4 | $$8);
                $$2[$$4] = $$9;
            }

            return new ShortContents($$2);
        }

        public int bitWidth() {
            return 16;
        }

        public short[] contents() {
            return this.contents;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record IntContents(int[] contents, int bitWidth) implements LineData {
        private static final int SIZE_24 = 24;

        private IntContents(int[] contents, int bitWidth) {
            this.contents = contents;
            this.bitWidth = bitWidth;
        }

        public int line(int p_285172_) {
            return this.contents[p_285172_];
        }

        static LineData read24(int p_285362_, ByteList p_285123_) {
            int[] $$2 = new int[16];
            int $$3 = 0;
            int $$4 = 0;

            for(int $$5 = 0; $$5 < 16; ++$$5) {
                int $$6 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$7 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$8 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$9 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$10 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$11 = UnihexProvider.decodeHex(p_285362_, p_285123_, $$4++);
                int $$12 = $$6 << 20 | $$7 << 16 | $$8 << 12 | $$9 << 8 | $$10 << 4 | $$11;
                $$2[$$5] = $$12 << 8;
                $$3 |= $$12;
            }

            return new IntContents($$2, 24);
        }

        public static LineData read32(int p_285222_, ByteList p_285346_) {
            int[] $$2 = new int[16];
            int $$3 = 0;
            int $$4 = 0;

            for(int $$5 = 0; $$5 < 16; ++$$5) {
                int $$6 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$7 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$8 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$9 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$10 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$11 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$12 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$13 = UnihexProvider.decodeHex(p_285222_, p_285346_, $$4++);
                int $$14 = $$6 << 28 | $$7 << 24 | $$8 << 20 | $$9 << 16 | $$10 << 12 | $$11 << 8 | $$12 << 4 | $$13;
                $$2[$$5] = $$14;
                $$3 |= $$14;
            }

            return new IntContents($$2, 32);
        }

        public int[] contents() {
            return this.contents;
        }

        public int bitWidth() {
            return this.bitWidth;
        }
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface ReaderOutput {
        void accept(int var1, LineData var2);
    }

    @OnlyIn(Dist.CLIENT)
    private static record Glyph(LineData contents, int left, int right) implements GlyphInfo {
        Glyph(LineData contents, int left, int right) {
            this.contents = contents;
            this.left = left;
            this.right = right;
        }

        public int width() {
            return this.right - this.left + 1;
        }

        public float getAdvance() {
            return (float)(this.width() / 2 + 1);
        }

        public float getShadowOffset() {
            return 0.5F;
        }

        public float getBoldOffset() {
            return 0.5F;
        }

        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> p_285377_) {
            return (BakedGlyph)p_285377_.apply(new SheetGlyphInfo() {
                public float getOversample() {
                    return 2.0F;
                }

                public int getPixelWidth() {
                    return Glyph.this.width();
                }

                public int getPixelHeight() {
                    return 16;
                }

                public void upload(int p_285473_, int p_285510_) {
                    IntBuffer $$2 = MemoryUtil.memAllocInt(Glyph.this.width() * 16);
                    UnihexProvider.unpackBitsToBytes($$2, Glyph.this.contents, Glyph.this.left, Glyph.this.right);
                    $$2.rewind();
                    GlStateManager.upload(0, p_285473_, p_285510_, Glyph.this.width(), 16, Format.RGBA, $$2, MemoryUtil::memFree);
                }

                public boolean isColored() {
                    return true;
                }
            });
        }

        public LineData contents() {
            return this.contents;
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Definition implements GlyphProviderDefinition {
        public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec((p_286579_) -> {
            return p_286579_.group(ResourceLocation.CODEC.fieldOf("hex_file").forGetter((p_286591_) -> {
                return p_286591_.hexFile;
            }), net.minecraft.client.gui.font.providers.UnihexProvider.OverrideRange.CODEC.listOf().fieldOf("size_overrides").forGetter((p_286528_) -> {
                return p_286528_.sizeOverrides;
            })).apply(p_286579_, Definition::new);
        });
        private final ResourceLocation hexFile;
        private final List<OverrideRange> sizeOverrides;

        private Definition(ResourceLocation p_286378_, List<OverrideRange> p_286770_) {
            this.hexFile = p_286378_;
            this.sizeOverrides = p_286770_;
        }

        public GlyphProviderType type() {
            return GlyphProviderType.UNIHEX;
        }

        public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
            return Either.left(this::load);
        }

        private GlyphProvider load(ResourceManager p_286472_) throws IOException {
            InputStream $$1 = p_286472_.open(this.hexFile);

            UnihexProvider var3;
            try {
                var3 = this.loadData($$1);
            } catch (Throwable var6) {
                if ($$1 != null) {
                    try {
                        $$1.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if ($$1 != null) {
                $$1.close();
            }

            return var3;
        }

        private UnihexProvider loadData(InputStream p_286795_) throws IOException {
            CodepointMap<LineData> $$1 = new CodepointMap((p_286908_) -> {
                return new LineData[p_286908_];
            }, (p_286615_) -> {
                return new LineData[p_286615_][];
            });
            Objects.requireNonNull($$1);
            ReaderOutput $$2 = $$1::put;
            ZipInputStream $$3 = new ZipInputStream(p_286795_);

            UnihexProvider var17;
            try {
                ZipEntry $$4;
                while(($$4 = $$3.getNextEntry()) != null) {
                    String $$5 = $$4.getName();
                    if ($$5.endsWith(".hex")) {
                        UnihexProvider.LOGGER.info("Found {}, loading", $$5);
                        UnihexProvider.readFromStream(new FastBufferedInputStream($$3), $$2);
                    }
                }

                CodepointMap<Glyph> $$6 = new CodepointMap((p_286831_) -> {
                    return new Glyph[p_286831_];
                }, (p_286340_) -> {
                    return new Glyph[p_286340_][];
                });
                Iterator var7 = this.sizeOverrides.iterator();

                label40:
                while(true) {
                    if (var7.hasNext()) {
                        OverrideRange $$7 = (OverrideRange)var7.next();
                        int $$8 = $$7.from;
                        int $$9 = $$7.to;
                        Dimensions $$10 = $$7.dimensions;
                        int $$11 = $$8;

                        while(true) {
                            if ($$11 > $$9) {
                                continue label40;
                            }

                            LineData $$12 = (LineData)$$1.remove($$11);
                            if ($$12 != null) {
                                $$6.put($$11, new Glyph($$12, $$10.left, $$10.right));
                            }

                            ++$$11;
                        }
                    }

                    $$1.forEach((p_286721_, p_286722_) -> {
                        int $$3 = p_286722_.calculateWidth();
                        int $$4 = net.minecraft.client.gui.font.providers.UnihexProvider.Dimensions.left($$3);
                        int $$5 = net.minecraft.client.gui.font.providers.UnihexProvider.Dimensions.right($$3);
                        $$6.put(p_286721_, new Glyph(p_286722_, $$4, $$5));
                    });
                    var17 = new UnihexProvider($$6);
                    break;
                }
            } catch (Throwable var15) {
                try {
                    $$3.close();
                } catch (Throwable var14) {
                    var15.addSuppressed(var14);
                }

                throw var15;
            }

            $$3.close();
            return var17;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record Dimensions(int left, int right) {
        public static final MapCodec<Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec((p_285497_) -> {
            return p_285497_.group(Codec.INT.fieldOf("left").forGetter(Dimensions::left), Codec.INT.fieldOf("right").forGetter(Dimensions::right)).apply(p_285497_, Dimensions::new);
        });
        public static final Codec<Dimensions> CODEC;

        public Dimensions(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public int pack() {
            return pack(this.left, this.right);
        }

        public static int pack(int p_285339_, int p_285120_) {
            return (p_285339_ & 255) << 8 | p_285120_ & 255;
        }

        public static int left(int p_285195_) {
            return (byte)(p_285195_ >> 8);
        }

        public static int right(int p_285419_) {
            return (byte)p_285419_;
        }

        public int left() {
            return this.left;
        }

        public int right() {
            return this.right;
        }

        static {
            CODEC = MAP_CODEC.codec();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static record OverrideRange(int from, int to, Dimensions dimensions) {
        private static final Codec<OverrideRange> RAW_CODEC = RecordCodecBuilder.create((p_285088_) -> {
            return p_285088_.group(ExtraCodecs.CODEPOINT.fieldOf("from").forGetter(OverrideRange::from), ExtraCodecs.CODEPOINT.fieldOf("to").forGetter(OverrideRange::to), net.minecraft.client.gui.font.providers.UnihexProvider.Dimensions.MAP_CODEC.forGetter(OverrideRange::dimensions)).apply(p_285088_, OverrideRange::new);
        });
        public static final Codec<OverrideRange> CODEC;

        private OverrideRange(int from, int to, Dimensions dimensions) {
            this.from = from;
            this.to = to;
            this.dimensions = dimensions;
        }

        public int from() {
            return this.from;
        }

        public int to() {
            return this.to;
        }

        public Dimensions dimensions() {
            return this.dimensions;
        }

        static {
            CODEC = ExtraCodecs.validate(RAW_CODEC, (p_285215_) -> {
                return p_285215_.from >= p_285215_.to ? DataResult.error(() -> {
                    return "Invalid range: [" + p_285215_.from + ";" + p_285215_.to + "]";
                }) : DataResult.success(p_285215_);
            });
        }
    }
}
