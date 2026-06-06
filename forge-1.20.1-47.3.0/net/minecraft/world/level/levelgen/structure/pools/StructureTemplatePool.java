//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

public class StructureTemplatePool {
    private static final int SIZE_UNSET = Integer.MIN_VALUE;
    private static final MutableObject<Codec<Holder<StructureTemplatePool>>> CODEC_REFERENCE = new MutableObject();
    public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create((p_255606_) -> {
        MutableObject var10001 = CODEC_REFERENCE;
        Objects.requireNonNull(var10001);
        return p_255606_.group(ExtraCodecs.lazyInitializedCodec(var10001::getValue).fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements").forGetter((p_210579_) -> {
            return p_210579_.rawTemplates;
        })).apply(p_255606_, StructureTemplatePool::new);
    });
    public static final Codec<Holder<StructureTemplatePool>> CODEC;
    private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final ObjectArrayList<StructurePoolElement> templates;
    private final Holder<StructureTemplatePool> fallback;
    private int maxSize = Integer.MIN_VALUE;

    public StructureTemplatePool(Holder<StructureTemplatePool> p_255747_, List<Pair<StructurePoolElement, Integer>> p_255919_) {
        this.rawTemplates = p_255919_;
        this.templates = new ObjectArrayList();
        Iterator var3 = p_255919_.iterator();

        while(var3.hasNext()) {
            Pair<StructurePoolElement, Integer> $$2 = (Pair)var3.next();
            StructurePoolElement $$3 = (StructurePoolElement)$$2.getFirst();

            for(int $$4 = 0; $$4 < (Integer)$$2.getSecond(); ++$$4) {
                this.templates.add($$3);
            }
        }

        this.fallback = p_255747_;
    }

    public StructureTemplatePool(Holder<StructureTemplatePool> p_255795_, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> p_256083_, Projection p_255642_) {
        this.rawTemplates = Lists.newArrayList();
        this.templates = new ObjectArrayList();
        Iterator var4 = p_256083_.iterator();

        while(var4.hasNext()) {
            Pair<Function<Projection, ? extends StructurePoolElement>, Integer> $$3 = (Pair)var4.next();
            StructurePoolElement $$4 = (StructurePoolElement)((Function)$$3.getFirst()).apply(p_255642_);
            this.rawTemplates.add(Pair.of($$4, (Integer)$$3.getSecond()));

            for(int $$5 = 0; $$5 < (Integer)$$3.getSecond(); ++$$5) {
                this.templates.add($$4);
            }
        }

        this.fallback = p_255795_;
    }

    public int getMaxSize(StructureTemplateManager p_227358_) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().filter((p_210577_) -> {
                return p_210577_ != EmptyPoolElement.INSTANCE;
            }).mapToInt((p_227361_) -> {
                return p_227361_.getBoundingBox(p_227358_, BlockPos.ZERO, Rotation.NONE).getYSpan();
            }).max().orElse(0);
        }

        return this.maxSize;
    }

    public Holder<StructureTemplatePool> getFallback() {
        return this.fallback;
    }

    public StructurePoolElement getRandomTemplate(RandomSource p_227356_) {
        return (StructurePoolElement)this.templates.get(p_227356_.nextInt(this.templates.size()));
    }

    public List<StructurePoolElement> getShuffledTemplates(RandomSource p_227363_) {
        return Util.shuffledCopy(this.templates, p_227363_);
    }

    public int size() {
        return this.templates.size();
    }

    static {
        RegistryFileCodec var10000 = RegistryFileCodec.create(Registries.TEMPLATE_POOL, DIRECT_CODEC);
        MutableObject var10001 = CODEC_REFERENCE;
        Objects.requireNonNull(var10001);
        CODEC = (Codec)Util.make(var10000, var10001::setValue);
    }

    public static enum Projection implements StringRepresentable {
        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityProcessor(Types.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", ImmutableList.of());

        public static final StringRepresentable.EnumCodec<Projection> CODEC = StringRepresentable.fromEnum(Projection::values);
        private final String name;
        private final ImmutableList<StructureProcessor> processors;

        private Projection(String p_210602_, ImmutableList p_210603_) {
            this.name = p_210602_;
            this.processors = p_210603_;
        }

        public String getName() {
            return this.name;
        }

        public static Projection byName(String p_210608_) {
            return (Projection)CODEC.byName(p_210608_);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        public String getSerializedName() {
            return this.name;
        }
    }
}
