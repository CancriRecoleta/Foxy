//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MultiBufferSource {
    static BufferSource immediate(BufferBuilder p_109899_) {
        return immediateWithBuffers(ImmutableMap.of(), p_109899_);
    }

    static BufferSource immediateWithBuffers(Map<RenderType, BufferBuilder> p_109901_, BufferBuilder p_109902_) {
        return new BufferSource(p_109902_, p_109901_);
    }

    VertexConsumer getBuffer(RenderType var1);

    @OnlyIn(Dist.CLIENT)
    public static class BufferSource implements MultiBufferSource {
        protected final BufferBuilder builder;
        protected final Map<RenderType, BufferBuilder> fixedBuffers;
        protected Optional<RenderType> lastState = Optional.empty();
        protected final Set<BufferBuilder> startedBuffers = Sets.newHashSet();

        protected BufferSource(BufferBuilder p_109909_, Map<RenderType, BufferBuilder> p_109910_) {
            this.builder = p_109909_;
            this.fixedBuffers = p_109910_;
        }

        public VertexConsumer getBuffer(RenderType p_109919_) {
            Optional<RenderType> $$1 = p_109919_.asOptional();
            BufferBuilder $$2 = this.getBuilderRaw(p_109919_);
            if (!Objects.equals(this.lastState, $$1) || !p_109919_.canConsolidateConsecutiveGeometry()) {
                if (this.lastState.isPresent()) {
                    RenderType $$3 = (RenderType)this.lastState.get();
                    if (!this.fixedBuffers.containsKey($$3)) {
                        this.endBatch($$3);
                    }
                }

                if (this.startedBuffers.add($$2)) {
                    $$2.begin(p_109919_.mode(), p_109919_.format());
                }

                this.lastState = $$1;
            }

            return $$2;
        }

        private BufferBuilder getBuilderRaw(RenderType p_109915_) {
            return (BufferBuilder)this.fixedBuffers.getOrDefault(p_109915_, this.builder);
        }

        public void endLastBatch() {
            if (this.lastState.isPresent()) {
                RenderType $$0 = (RenderType)this.lastState.get();
                if (!this.fixedBuffers.containsKey($$0)) {
                    this.endBatch($$0);
                }

                this.lastState = Optional.empty();
            }

        }

        public void endBatch() {
            this.lastState.ifPresent((p_109917_) -> {
                VertexConsumer $$1 = this.getBuffer(p_109917_);
                if ($$1 == this.builder) {
                    this.endBatch(p_109917_);
                }

            });
            Iterator var1 = this.fixedBuffers.keySet().iterator();

            while(var1.hasNext()) {
                RenderType $$0 = (RenderType)var1.next();
                this.endBatch($$0);
            }

        }

        public void endBatch(RenderType p_109913_) {
            BufferBuilder $$1 = this.getBuilderRaw(p_109913_);
            boolean $$2 = Objects.equals(this.lastState, p_109913_.asOptional());
            if ($$2 || $$1 != this.builder) {
                if (this.startedBuffers.remove($$1)) {
                    p_109913_.end($$1, RenderSystem.getVertexSorting());
                    if ($$2) {
                        this.lastState = Optional.empty();
                    }

                }
            }
        }
    }
}
