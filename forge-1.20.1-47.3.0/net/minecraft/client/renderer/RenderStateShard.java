//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.LogicOp;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public abstract class RenderStateShard {
    private static final float VIEW_SCALE_Z_EPSILON = 0.99975586F;
    public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
    protected final String name;
    protected Runnable setupState;
    private final Runnable clearState;
    protected static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });
    protected static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard LIGHTNING_TRANSPARENCY = new TransparencyStateShard("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard GLINT_TRANSPARENCY = new TransparencyStateShard("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard CRUMBLING_TRANSPARENCY = new TransparencyStateShard("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.DST_COLOR, DestFactor.SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final ShaderStateShard NO_SHADER = new ShaderStateShard();
    protected static final ShaderStateShard POSITION_COLOR_LIGHTMAP_SHADER = new ShaderStateShard(GameRenderer::getPositionColorLightmapShader);
    protected static final ShaderStateShard POSITION_SHADER = new ShaderStateShard(GameRenderer::getPositionShader);
    protected static final ShaderStateShard POSITION_COLOR_TEX_SHADER = new ShaderStateShard(GameRenderer::getPositionColorTexShader);
    protected static final ShaderStateShard POSITION_TEX_SHADER = new ShaderStateShard(GameRenderer::getPositionTexShader);
    protected static final ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_SHADER = new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader);
    protected static final ShaderStateShard POSITION_COLOR_SHADER = new ShaderStateShard(GameRenderer::getPositionColorShader);
    protected static final ShaderStateShard RENDERTYPE_SOLID_SHADER = new ShaderStateShard(GameRenderer::getRendertypeSolidShader);
    protected static final ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCutoutMippedShader);
    protected static final ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentShader);
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentMovingBlockShader);
    protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentNoCrumblingShader);
    protected static final ShaderStateShard RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorCutoutNoCullShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullZOffsetShader);
    protected static final ShaderStateShard RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeItemEntityTranslucentCullShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentCullShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntitySmoothCutoutShader);
    protected static final ShaderStateShard RENDERTYPE_BEACON_BEAM_SHADER = new ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_DECAL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityDecalShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_NO_OUTLINE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityNoOutlineShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_SHADOW_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityShadowShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_ALPHA_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityAlphaShader);
    protected static final ShaderStateShard RENDERTYPE_EYES_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEyesShader);
    protected static final ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
    protected static final ShaderStateShard RENDERTYPE_LEASH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLeashShader);
    protected static final ShaderStateShard RENDERTYPE_WATER_MASK_SHADER = new ShaderStateShard(GameRenderer::getRendertypeWaterMaskShader);
    protected static final ShaderStateShard RENDERTYPE_OUTLINE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeOutlineShader);
    protected static final ShaderStateShard RENDERTYPE_ARMOR_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorGlintShader);
    protected static final ShaderStateShard RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorEntityGlintShader);
    protected static final ShaderStateShard RENDERTYPE_GLINT_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintTranslucentShader);
    protected static final ShaderStateShard RENDERTYPE_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintShader);
    protected static final ShaderStateShard RENDERTYPE_GLINT_DIRECT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintDirectShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityGlintShader);
    protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityGlintDirectShader);
    protected static final ShaderStateShard RENDERTYPE_CRUMBLING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCrumblingShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_BACKGROUND_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextBackgroundShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextIntensityShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_SEE_THROUGH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextSeeThroughShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextBackgroundSeeThroughShader);
    protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextIntensitySeeThroughShader);
    protected static final ShaderStateShard RENDERTYPE_LIGHTNING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLightningShader);
    protected static final ShaderStateShard RENDERTYPE_TRIPWIRE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTripwireShader);
    protected static final ShaderStateShard RENDERTYPE_END_PORTAL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEndPortalShader);
    protected static final ShaderStateShard RENDERTYPE_END_GATEWAY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEndGatewayShader);
    protected static final ShaderStateShard RENDERTYPE_LINES_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLinesShader);
    protected static final ShaderStateShard RENDERTYPE_GUI_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGuiShader);
    protected static final ShaderStateShard RENDERTYPE_GUI_OVERLAY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGuiOverlayShader);
    protected static final ShaderStateShard RENDERTYPE_GUI_TEXT_HIGHLIGHT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGuiTextHighlightShader);
    protected static final ShaderStateShard RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGuiGhostRecipeOverlayShader);
    protected static final TextureStateShard BLOCK_SHEET_MIPPED;
    protected static final TextureStateShard BLOCK_SHEET;
    protected static final EmptyTextureStateShard NO_TEXTURE;
    protected static final TexturingStateShard DEFAULT_TEXTURING;
    protected static final TexturingStateShard GLINT_TEXTURING;
    protected static final TexturingStateShard ENTITY_GLINT_TEXTURING;
    protected static final LightmapStateShard LIGHTMAP;
    protected static final LightmapStateShard NO_LIGHTMAP;
    protected static final OverlayStateShard OVERLAY;
    protected static final OverlayStateShard NO_OVERLAY;
    protected static final CullStateShard CULL;
    protected static final CullStateShard NO_CULL;
    protected static final DepthTestStateShard NO_DEPTH_TEST;
    protected static final DepthTestStateShard EQUAL_DEPTH_TEST;
    protected static final DepthTestStateShard LEQUAL_DEPTH_TEST;
    protected static final DepthTestStateShard GREATER_DEPTH_TEST;
    protected static final WriteMaskStateShard COLOR_DEPTH_WRITE;
    protected static final WriteMaskStateShard COLOR_WRITE;
    protected static final WriteMaskStateShard DEPTH_WRITE;
    protected static final LayeringStateShard NO_LAYERING;
    protected static final LayeringStateShard POLYGON_OFFSET_LAYERING;
    protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING;
    protected static final OutputStateShard MAIN_TARGET;
    protected static final OutputStateShard OUTLINE_TARGET;
    protected static final OutputStateShard TRANSLUCENT_TARGET;
    protected static final OutputStateShard PARTICLES_TARGET;
    protected static final OutputStateShard WEATHER_TARGET;
    protected static final OutputStateShard CLOUDS_TARGET;
    protected static final OutputStateShard ITEM_ENTITY_TARGET;
    protected static final LineStateShard DEFAULT_LINE;
    protected static final ColorLogicStateShard NO_COLOR_LOGIC;
    protected static final ColorLogicStateShard OR_REVERSE_COLOR_LOGIC;

    public RenderStateShard(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        this.name = p_110161_;
        this.setupState = p_110162_;
        this.clearState = p_110163_;
    }

    public void setupRenderState() {
        this.setupState.run();
    }

    public void clearRenderState() {
        this.clearState.run();
    }

    public String toString() {
        return this.name;
    }

    private static void setupGlintTexturing(float p_110187_) {
        long $$1 = (long)((double)Util.getMillis() * (Double)Minecraft.getInstance().options.glintSpeed().get() * 8.0);
        float $$2 = (float)($$1 % 110000L) / 110000.0F;
        float $$3 = (float)($$1 % 30000L) / 30000.0F;
        Matrix4f $$4 = (new Matrix4f()).translation(-$$2, $$3, 0.0F);
        $$4.rotateZ(0.17453292F).scale(p_110187_);
        RenderSystem.setTextureMatrix($$4);
    }

    static {
        BLOCK_SHEET_MIPPED = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
        BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
        NO_TEXTURE = new EmptyTextureStateShard();
        DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {
        }, () -> {
        });
        GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> {
            setupGlintTexturing(8.0F);
        }, () -> {
            RenderSystem.resetTextureMatrix();
        });
        ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> {
            setupGlintTexturing(0.16F);
        }, () -> {
            RenderSystem.resetTextureMatrix();
        });
        LIGHTMAP = new LightmapStateShard(true);
        NO_LIGHTMAP = new LightmapStateShard(false);
        OVERLAY = new OverlayStateShard(true);
        NO_OVERLAY = new OverlayStateShard(false);
        CULL = new CullStateShard(true);
        NO_CULL = new CullStateShard(false);
        NO_DEPTH_TEST = new DepthTestStateShard("always", 519);
        EQUAL_DEPTH_TEST = new DepthTestStateShard("==", 514);
        LEQUAL_DEPTH_TEST = new DepthTestStateShard("<=", 515);
        GREATER_DEPTH_TEST = new DepthTestStateShard(">", 516);
        COLOR_DEPTH_WRITE = new WriteMaskStateShard(true, true);
        COLOR_WRITE = new WriteMaskStateShard(true, false);
        DEPTH_WRITE = new WriteMaskStateShard(false, true);
        NO_LAYERING = new LayeringStateShard("no_layering", () -> {
        }, () -> {
        });
        POLYGON_OFFSET_LAYERING = new LayeringStateShard("polygon_offset_layering", () -> {
            RenderSystem.polygonOffset(-1.0F, -10.0F);
            RenderSystem.enablePolygonOffset();
        }, () -> {
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
        });
        VIEW_OFFSET_Z_LAYERING = new LayeringStateShard("view_offset_z_layering", () -> {
            PoseStack $$0 = RenderSystem.getModelViewStack();
            $$0.pushPose();
            $$0.scale(0.99975586F, 0.99975586F, 0.99975586F);
            RenderSystem.applyModelViewMatrix();
        }, () -> {
            PoseStack $$0 = RenderSystem.getModelViewStack();
            $$0.popPose();
            RenderSystem.applyModelViewMatrix();
        });
        MAIN_TARGET = new OutputStateShard("main_target", () -> {
        }, () -> {
        });
        OUTLINE_TARGET = new OutputStateShard("outline_target", () -> {
            Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
        }, () -> {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        });
        TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
            }

        }, () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

        });
        PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
            }

        }, () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

        });
        WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getWeatherTarget().bindWrite(false);
            }

        }, () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

        });
        CLOUDS_TARGET = new OutputStateShard("clouds_target", () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getCloudsTarget().bindWrite(false);
            }

        }, () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

        });
        ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
            }

        }, () -> {
            if (Minecraft.useShaderTransparency()) {
                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
            }

        });
        DEFAULT_LINE = new LineStateShard(OptionalDouble.of(1.0));
        NO_COLOR_LOGIC = new ColorLogicStateShard("no_color_logic", () -> {
            RenderSystem.disableColorLogicOp();
        }, () -> {
        });
        OR_REVERSE_COLOR_LOGIC = new ColorLogicStateShard("or_reverse", () -> {
            RenderSystem.enableColorLogicOp();
            RenderSystem.logicOp(LogicOp.OR_REVERSE);
        }, () -> {
            RenderSystem.disableColorLogicOp();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static class TransparencyStateShard extends RenderStateShard {
        public TransparencyStateShard(String p_110353_, Runnable p_110354_, Runnable p_110355_) {
            super(p_110353_, p_110354_, p_110355_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class ShaderStateShard extends RenderStateShard {
        private final Optional<Supplier<ShaderInstance>> shader;

        public ShaderStateShard(Supplier<ShaderInstance> p_173139_) {
            super("shader", () -> {
                RenderSystem.setShader(p_173139_);
            }, () -> {
            });
            this.shader = Optional.of(p_173139_);
        }

        public ShaderStateShard() {
            super("shader", () -> {
                RenderSystem.setShader(() -> {
                    return null;
                });
            }, () -> {
            });
            this.shader = Optional.empty();
        }

        public String toString() {
            return this.name + "[" + this.shader + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TextureStateShard extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> texture;
        protected boolean blur;
        protected boolean mipmap;

        public TextureStateShard(ResourceLocation p_110333_, boolean p_110334_, boolean p_110335_) {
            super(() -> {
                TextureManager $$3 = Minecraft.getInstance().getTextureManager();
                $$3.getTexture(p_110333_).setFilter(p_110334_, p_110335_);
                RenderSystem.setShaderTexture(0, p_110333_);
            }, () -> {
            });
            this.texture = Optional.of(p_110333_);
            this.blur = p_110334_;
            this.mipmap = p_110335_;
        }

        public String toString() {
            return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class EmptyTextureStateShard extends RenderStateShard {
        public EmptyTextureStateShard(Runnable p_173117_, Runnable p_173118_) {
            super("texture", p_173117_, p_173118_);
        }

        EmptyTextureStateShard() {
            super("texture", () -> {
            }, () -> {
            });
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return Optional.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TexturingStateShard extends RenderStateShard {
        public TexturingStateShard(String p_110349_, Runnable p_110350_, Runnable p_110351_) {
            super(p_110349_, p_110350_, p_110351_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LightmapStateShard extends BooleanStateShard {
        public LightmapStateShard(boolean p_110271_) {
            super("lightmap", () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                }

            }, () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
                }

            }, p_110271_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OverlayStateShard extends BooleanStateShard {
        public OverlayStateShard(boolean p_110304_) {
            super("overlay", () -> {
                if (p_110304_) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
                }

            }, () -> {
                if (p_110304_) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
                }

            }, p_110304_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class CullStateShard extends BooleanStateShard {
        public CullStateShard(boolean p_110238_) {
            super("cull", () -> {
                if (!p_110238_) {
                    RenderSystem.disableCull();
                }

            }, () -> {
                if (!p_110238_) {
                    RenderSystem.enableCull();
                }

            }, p_110238_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class DepthTestStateShard extends RenderStateShard {
        private final String functionName;

        public DepthTestStateShard(String p_110246_, int p_110247_) {
            super("depth_test", () -> {
                if (p_110247_ != 519) {
                    RenderSystem.enableDepthTest();
                    RenderSystem.depthFunc(p_110247_);
                }

            }, () -> {
                if (p_110247_ != 519) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.depthFunc(515);
                }

            });
            this.functionName = p_110246_;
        }

        public String toString() {
            return this.name + "[" + this.functionName + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WriteMaskStateShard extends RenderStateShard {
        private final boolean writeColor;
        private final boolean writeDepth;

        public WriteMaskStateShard(boolean p_110359_, boolean p_110360_) {
            super("write_mask_state", () -> {
                if (!p_110360_) {
                    RenderSystem.depthMask(p_110360_);
                }

                if (!p_110359_) {
                    RenderSystem.colorMask(p_110359_, p_110359_, p_110359_, p_110359_);
                }

            }, () -> {
                if (!p_110360_) {
                    RenderSystem.depthMask(true);
                }

                if (!p_110359_) {
                    RenderSystem.colorMask(true, true, true, true);
                }

            });
            this.writeColor = p_110359_;
            this.writeDepth = p_110360_;
        }

        public String toString() {
            return this.name + "[writeColor=" + this.writeColor + ", writeDepth=" + this.writeDepth + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LayeringStateShard extends RenderStateShard {
        public LayeringStateShard(String p_110267_, Runnable p_110268_, Runnable p_110269_) {
            super(p_110267_, p_110268_, p_110269_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OutputStateShard extends RenderStateShard {
        public OutputStateShard(String p_110300_, Runnable p_110301_, Runnable p_110302_) {
            super(p_110300_, p_110301_, p_110302_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class LineStateShard extends RenderStateShard {
        private final OptionalDouble width;

        public LineStateShard(OptionalDouble p_110278_) {
            super("line_width", () -> {
                if (!Objects.equals(p_110278_, OptionalDouble.of(1.0))) {
                    if (p_110278_.isPresent()) {
                        RenderSystem.lineWidth((float)p_110278_.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
                    }
                }

            }, () -> {
                if (!Objects.equals(p_110278_, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0F);
                }

            });
            this.width = p_110278_;
        }

        public String toString() {
            String var10000 = this.name;
            return var10000 + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class ColorLogicStateShard extends RenderStateShard {
        public ColorLogicStateShard(String p_286784_, Runnable p_286884_, Runnable p_286375_) {
            super(p_286784_, p_286884_, p_286375_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class BooleanStateShard extends RenderStateShard {
        private final boolean enabled;

        public BooleanStateShard(String p_110229_, Runnable p_110230_, Runnable p_110231_, boolean p_110232_) {
            super(p_110229_, p_110230_, p_110231_);
            this.enabled = p_110232_;
        }

        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class OffsetTexturingStateShard extends TexturingStateShard {
        public OffsetTexturingStateShard(float p_110290_, float p_110291_) {
            super("offset_texturing", () -> {
                RenderSystem.setTextureMatrix((new Matrix4f()).translation(p_110290_, p_110291_, 0.0F));
            }, () -> {
                RenderSystem.resetTextureMatrix();
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MultiTextureStateShard extends EmptyTextureStateShard {
        private final Optional<ResourceLocation> cutoutTexture;

        MultiTextureStateShard(ImmutableList<Triple<ResourceLocation, Boolean, Boolean>> p_173123_) {
            super(() -> {
                int $$1 = 0;
                UnmodifiableIterator var2 = p_173123_.iterator();

                while(var2.hasNext()) {
                    Triple<ResourceLocation, Boolean, Boolean> $$2 = (Triple)var2.next();
                    TextureManager $$3 = Minecraft.getInstance().getTextureManager();
                    $$3.getTexture((ResourceLocation)$$2.getLeft()).setFilter((Boolean)$$2.getMiddle(), (Boolean)$$2.getRight());
                    RenderSystem.setShaderTexture($$1++, (ResourceLocation)$$2.getLeft());
                }

            }, () -> {
            });
            this.cutoutTexture = p_173123_.stream().findFirst().map(Triple::getLeft);
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return this.cutoutTexture;
        }

        public static Builder builder() {
            return new Builder();
        }

        @OnlyIn(Dist.CLIENT)
        public static final class Builder {
            private final ImmutableList.Builder<Triple<ResourceLocation, Boolean, Boolean>> builder = new ImmutableList.Builder();

            public Builder() {
            }

            public Builder add(ResourceLocation p_173133_, boolean p_173134_, boolean p_173135_) {
                this.builder.add(Triple.of(p_173133_, p_173134_, p_173135_));
                return this;
            }

            public MultiTextureStateShard build() {
                return new MultiTextureStateShard(this.builder.build());
            }
        }
    }
}
