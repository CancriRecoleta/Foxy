package com.github.foxy.client.iris;

import com.github.foxy.client.config.FoxyConfig;
import com.github.foxy.client.core.IGetFoxyRenderSystem;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.function.Supplier;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

public class FoxyUniforms {

    public static Matrix4f getViewProjection() {//This is 1 frame late ;-; cries, since the update occurs _before_ the Foxy render pipeline
        var getVrs = (IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer;
        if (getVrs == null || getVrs.Foxy$getRenderSystem() == null) {
            return new Matrix4f();
        }
        var vrs = getVrs.Foxy$getRenderSystem();
        return new Matrix4f(vrs.getViewport().MVP);
    }

    public static Matrix4f getModelView() {//This is 1 frame late ;-; cries, since the update occurs _before_ the Foxy render pipeline
        var getVrs = (IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer;
        if (getVrs == null || getVrs.Foxy$getRenderSystem() == null) {
            return new Matrix4f();
        }
        var vrs = getVrs.Foxy$getRenderSystem();
        return new Matrix4f(vrs.getViewport().modelView);
    }

    public static Matrix4f getProjection() {//This is 1 frame late ;-; cries, since the update occurs _before_ the Foxy render pipeline
        var getVrs = (IGetFoxyRenderSystem) Minecraft.getInstance().levelRenderer;
        if (getVrs == null || getVrs.Foxy$getRenderSystem() == null) {
            return new Matrix4f();
        }
        var vrs = getVrs.Foxy$getRenderSystem();
        var mat = vrs.getViewport().projection;
        if (mat == null) {
            return new Matrix4f();
        }
        return new Matrix4f(mat);
    }

    public static void addUniforms(UniformHolder uniforms) {
        uniforms
                .uniform1i(PER_FRAME, "vxRenderDistance", ()->Math.round(FoxyConfig.CONFIG.sectionRenderDistance*32))//In chunks
                .uniformMatrix(PER_FRAME, "vxViewProj", FoxyUniforms::getViewProjection)
                .uniformMatrix(PER_FRAME, "vxViewProjInv", new Inverted(FoxyUniforms::getViewProjection))
                .uniformMatrix(PER_FRAME, "vxViewProjPrev", new PreviousMat(FoxyUniforms::getViewProjection))
                .uniformMatrix(PER_FRAME, "vxModelView", FoxyUniforms::getModelView)
                .uniformMatrix(PER_FRAME, "vxModelViewInv", new Inverted(FoxyUniforms::getModelView))
                .uniformMatrix(PER_FRAME, "vxModelViewPrev", new PreviousMat(FoxyUniforms::getModelView))
                .uniformMatrix(PER_FRAME, "vxProj", FoxyUniforms::getProjection)
                .uniformMatrix(PER_FRAME, "vxProjInv", new Inverted(FoxyUniforms::getProjection))
                .uniformMatrix(PER_FRAME, "vxProjPrev", new PreviousMat(FoxyUniforms::getProjection));

        /*
        if (IrisShaderPatch.IMPERSONATE_DISTANT_HORIZONS) {
            uniforms
                    .uniform1f(PER_FRAME, "dhNearPlane", ()->16)//Presently hardcoded in Foxy
                    .uniform1f(PER_FRAME, "dhFarPlane", ()->16*3000)//Presently hardcoded in Foxy

                    .uniform1i(PER_FRAME, "dhRenderDistance", ()->Math.round(FoxyConfig.CONFIG.sectionRenderDistance*32*16))//In blocks
                    .uniformMatrix(PER_FRAME, "dhProjection", FoxyUniforms::getProjection)
                    .uniformMatrix(PER_FRAME, "dhProjectionInverse", new Inverted(FoxyUniforms::getProjection))
                    .uniformMatrix(PER_FRAME, "dhPreviousProjection", new PreviousMat(FoxyUniforms::getProjection));
        }*/
    }




    private record Inverted(Supplier<Matrix4fc> parent) implements Supplier<Matrix4fc> {
        private Inverted(Supplier<Matrix4fc> parent) {
            this.parent = parent;
        }

        public Matrix4fc get() {
            Matrix4f copy = new Matrix4f(this.parent.get());
            copy.invert();
            return copy;
        }

        public Supplier<Matrix4fc> parent() {
            return this.parent;
        }
    }

    private static class PreviousMat implements Supplier<Matrix4fc> {
        private final Supplier<Matrix4fc> parent;
        private Matrix4f previous;

        PreviousMat(Supplier<Matrix4fc> parent) {
            this.parent = parent;
            this.previous = new Matrix4f();
        }

        public Matrix4fc get() {
            Matrix4f previous = this.previous;
            this.previous = new Matrix4f(this.parent.get());
            return previous;
        }
    }
}
