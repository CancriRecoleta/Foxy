//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PostPass implements AutoCloseable {
    private final EffectInstance effect;
    public final RenderTarget inTarget;
    public final RenderTarget outTarget;
    private final List<IntSupplier> auxAssets = Lists.newArrayList();
    private final List<String> auxNames = Lists.newArrayList();
    private final List<Integer> auxWidths = Lists.newArrayList();
    private final List<Integer> auxHeights = Lists.newArrayList();
    private Matrix4f shaderOrthoMatrix;

    public PostPass(ResourceManager p_110061_, String p_110062_, RenderTarget p_110063_, RenderTarget p_110064_) throws IOException {
        this.effect = new EffectInstance(p_110061_, p_110062_);
        this.inTarget = p_110063_;
        this.outTarget = p_110064_;
    }

    public void close() {
        this.effect.close();
    }

    public final String getName() {
        return this.effect.getName();
    }

    public void addAuxAsset(String p_110070_, IntSupplier p_110071_, int p_110072_, int p_110073_) {
        this.auxNames.add(this.auxNames.size(), p_110070_);
        this.auxAssets.add(this.auxAssets.size(), p_110071_);
        this.auxWidths.add(this.auxWidths.size(), p_110072_);
        this.auxHeights.add(this.auxHeights.size(), p_110073_);
    }

    public void setOrthoMatrix(Matrix4f p_253811_) {
        this.shaderOrthoMatrix = p_253811_;
    }

    public void process(float p_110066_) {
        this.inTarget.unbindWrite();
        float $$1 = (float)this.outTarget.width;
        float $$2 = (float)this.outTarget.height;
        RenderSystem.viewport(0, 0, (int)$$1, (int)$$2);
        EffectInstance var10000 = this.effect;
        RenderTarget var10002 = this.inTarget;
        Objects.requireNonNull(var10002);
        var10000.setSampler("DiffuseSampler", var10002::getColorTextureId);

        for(int $$3 = 0; $$3 < this.auxAssets.size(); ++$$3) {
            this.effect.setSampler((String)this.auxNames.get($$3), (IntSupplier)this.auxAssets.get($$3));
            this.effect.safeGetUniform("AuxSize" + $$3).set((float)(Integer)this.auxWidths.get($$3), (float)(Integer)this.auxHeights.get($$3));
        }

        this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
        this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
        this.effect.safeGetUniform("OutSize").set($$1, $$2);
        this.effect.safeGetUniform("Time").set(p_110066_);
        Minecraft $$4 = Minecraft.getInstance();
        this.effect.safeGetUniform("ScreenSize").set((float)$$4.getWindow().getWidth(), (float)$$4.getWindow().getHeight());
        this.effect.apply();
        this.outTarget.clear(Minecraft.ON_OSX);
        this.outTarget.bindWrite(false);
        RenderSystem.depthFunc(519);
        BufferBuilder $$5 = Tesselator.getInstance().getBuilder();
        $$5.begin(Mode.QUADS, DefaultVertexFormat.POSITION);
        $$5.vertex(0.0, 0.0, 500.0).endVertex();
        $$5.vertex((double)$$1, 0.0, 500.0).endVertex();
        $$5.vertex((double)$$1, (double)$$2, 500.0).endVertex();
        $$5.vertex(0.0, (double)$$2, 500.0).endVertex();
        BufferUploader.draw($$5.end());
        RenderSystem.depthFunc(515);
        this.effect.clear();
        this.outTarget.unbindWrite();
        this.inTarget.unbindRead();
        Iterator var6 = this.auxAssets.iterator();

        while(var6.hasNext()) {
            Object $$6 = var6.next();
            if ($$6 instanceof RenderTarget) {
                ((RenderTarget)$$6).unbindRead();
            }
        }

    }

    public EffectInstance getEffect() {
        return this.effect;
    }
}
