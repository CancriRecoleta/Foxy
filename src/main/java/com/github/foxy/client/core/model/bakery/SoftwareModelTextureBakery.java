package com.github.foxy.client.core.model.bakery;

import com.github.foxy.client.compat.ChunkSectionLayer;
import com.github.foxy.client.core.model.ModelFactory;
import com.github.foxy.common.util.UnsafeUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11C.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glFinish;
import static org.lwjgl.opengl.GL11C.glFlush;
import static org.lwjgl.opengl.GL11C.glGetTexImage;
import static org.lwjgl.opengl.GL11C.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11C.glPixelStorei;
import static org.lwjgl.opengl.GL12C.GL_PACK_IMAGE_HEIGHT;
import static org.lwjgl.opengl.GL12C.GL_PACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL12C.GL_PACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL13C.GL_PACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

/**
 * Bakes block-state face textures into a {@link SoftwareRasterizer}'s framebuffer.
 *
 * <h2>Pipeline</h2>
 * <ol>
 *   <li>{@link #setupTexture()} reads the vanilla block atlas off the GL texture and
 *       hands it to the rasteriser as the active sampler texture (so quads can read
 *       their texels off-thread without GL).</li>
 *   <li>{@link #renderToOutput} chooses a render layer (cutout / translucent / solid /
 *       tripwire) for the block state, walks its quads via the vanilla baked model
 *       (or the fluid renderer for liquid blocks), and rasterises them into the six
 *       canonical face views.</li>
 *   <li>Each rasterised framebuffer is memcpy'd into the caller's {@code outputBuffer}
 *       at a fixed face-stride.</li>
 * </ol>
 *
 * <h2>Six views</h2>
 * <p>The {@link #VIEWS} matrices are pre-built at class init by composing translate-
 * to-centre + axis-aligned rotation + axis flip + ortho projection. They map the
 * unit cube {@code [0, 1]^3} into clip space such that the indicated face fills
 * the framebuffer. View-index ordering matches {@link Direction#from3DDataValue}:
 * 0=DOWN, 1=UP, 2=NORTH, 3=SOUTH, 4=WEST, 5=EAST.</p>
 *
 * <h2>Cleanroom note</h2>
 * <p>Same algorithm as upstream Voxy. The cleanroom rewrite drops several unused
 * imports ({@code NativeImage}, {@code IOException}, {@code Path},
 * {@code AtomicInteger}), simplifies the {@code meta |= true ? 2 : 0} expression,
 * extracts a {@link #shouldFaceCull} helper, and adds full English javadoc.</p>
 */
public class SoftwareModelTextureBakery {

    /** Face count, per the canonical {@link Direction#from3DDataValue} ordering. */
    private static final int FACE_COUNT = 6;

    /** Pre-baked one-per-face view+projection matrices. */
    private static final Matrix4f[] VIEWS = new Matrix4f[FACE_COUNT];

    /** Bytes one face's framebuffer occupies in the output buffer. */
    private static final long SINGLE_FACE_OUTPUT_SIZE =
            (long) ModelFactory.MODEL_TEXTURE_SIZE * ModelFactory.MODEL_TEXTURE_SIZE * 8;

    private final ReuseVertexConsumer vc = new ReuseVertexConsumer();
    private final SoftwareRasterizer rasterizer = new SoftwareRasterizer(ModelFactory.MODEL_TEXTURE_SIZE);

    public SoftwareModelTextureBakery() {}

    /**
     * Reads the vanilla block atlas into a heap {@code int[]} and registers it with
     * the rasteriser. Must run on the GL thread because of the
     * {@code glGetTexImage} call.
     */
    public void setupTexture() {
        var tex = Minecraft.getInstance().getTextureManager().getTexture(
                ResourceLocation.tryBuild("minecraft", "textures/atlas/blocks.png"));
        int targetMipLevel = 0;

        glBindTexture(GL_TEXTURE_2D, tex.getId());
        int width = glGetTexLevelParameteri(GL_TEXTURE_2D, targetMipLevel, GL_TEXTURE_WIDTH);
        int height = glGetTexLevelParameteri(GL_TEXTURE_2D, targetMipLevel, GL_TEXTURE_HEIGHT);

        // Doing the readback ourselves bypasses Blaze3D quirks and is shorter than
        // routing through NativeImage; cleared GL pixel-store state guarantees a
        // packed RGBA8 row layout regardless of caller-side state.
        var texture = new int[width * height];
        glFlush();
        glFinish();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindBuffer(GL_PIXEL_PACK_BUFFER, 0);
        glPixelStorei(GL_PACK_ROW_LENGTH, width);
        glPixelStorei(GL_PACK_IMAGE_HEIGHT, 0);
        glPixelStorei(GL_PACK_SKIP_ROWS, 0);
        glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
        glPixelStorei(GL_PACK_ALIGNMENT, 4);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);

        this.rasterizer.setSamplerTexture(texture, width, height);
    }

    /**
     * Computes the per-vertex meta byte for {@code layer}: bit 0 = alpha-discard
     * enabled, bit 1 = depth test enabled (always; the rasteriser uses depth even
     * for solid layers).
     */
    public static int getMetaFromLayer(ChunkSectionLayer layer) {
        boolean hasDiscard = layer == ChunkSectionLayer.CUTOUT
                || layer == ChunkSectionLayer.TRANSLUCENT
                || layer == ChunkSectionLayer.TRIPWIRE;
        int meta = hasDiscard ? 1 : 0;
        meta |= 2; // depth test always enabled
        return meta;
    }

    private void bakeBlockModel(BlockState state, ChunkSectionLayer layer) {
        if (state.getRenderShape() == RenderShape.INVISIBLE) return;
        var model = Minecraft.getInstance()
                .getModelManager()
                .getBlockModelShaper()
                .getBlockModel(state);

        int meta = getMetaFromLayer(layer);
        var random = new SingleThreadedRandomSource(42L);
        // null direction emits "unculled" quads (e.g. cross models, fluids interiors).
        for (Direction direction : new Direction[]{
                Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
                Direction.WEST, Direction.EAST, null}) {
            random.setSeed(42L);
            var quads = model.getQuads(state, direction, random);
            for (var quad : quads) {
                this.vc.quad(quad, meta | (quad.isTinted() ? 4 : 0));
            }
        }
    }

    private void bakeFluidState(BlockState state, ChunkSectionLayer layer, int face) {
        // Always set the per-vertex tint flag for fluids; non-tinted fluids end up
        // implicitly culled later for lacking a colour provider.
        int metadata = getMetaFromLayer(layer) | 4;
        this.vc.setDefaultMeta(metadata);
        Minecraft.getInstance().getBlockRenderer().renderLiquid(BlockPos.ZERO,
                new SingleBlockFluidView(state, face), this.vc, state, state.getFluidState());
        this.vc.setDefaultMeta(0);
    }

    /**
     * Stub world used to anchor the fluid renderer at the origin. Returns air for
     * every position outside the unit block (so neighbour-aware fluid mesh code
     * sees an isolated cube).
     */
    private static final class SingleBlockFluidView implements BlockAndTintGetter {
        private final BlockState state;
        private final int face;

        SingleBlockFluidView(BlockState state, int face) {
            this.state = state;
            this.face = face;
        }

        @Override public float getShade(Direction direction, boolean shaded) { return 0; }
        @Override public LevelLightEngine getLightEngine() { return null; }
        @Override public int getBrightness(LightLayer type, BlockPos pos) { return 0; }
        @Override public int getBlockTint(BlockPos pos, ColorResolver colorResolver) { return 0; }
        @Nullable @Override public BlockEntity getBlockEntity(BlockPos pos) { return null; }
        @Override public int getHeight() { return 0; }
        @Override public int getMinBuildHeight() { return 0; }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return shouldReturnAirForFluid(pos, this.face)
                    ? Blocks.AIR.defaultBlockState()
                    : this.state;
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return shouldReturnAirForFluid(pos, this.face)
                    ? Blocks.AIR.defaultBlockState().getFluidState()
                    : this.state.getFluidState();
        }
    }

    /**
     * Returns true when the synthetic fluid world should report air at {@code pos}
     * for the face being rasterised. The criterion is "the block lies on the
     * positive side of the face's outward normal" — i.e. behind the camera.
     */
    private static boolean shouldReturnAirForFluid(BlockPos pos, int face) {
        var normal = Direction.from3DDataValue(face).getNormal();
        int dot = normal.getX() * pos.getX() + normal.getY() * pos.getY() + normal.getZ() * pos.getZ();
        return dot >= 1;
    }

    /** Tears down the vertex consumer's off-heap buffer. */
    public void free() {
        this.vc.free();
    }

    /** Cull the back face for views 1, 2, 4 (UP, NORTH, WEST) per the upstream convention. */
    private static boolean shouldFaceCull(int face) {
        return face == 1 || face == 2 || face == 4;
    }

    /**
     * Rasterises every face of {@code state} into {@code outputBuffer}.
     *
     * @return bit 0 = at least one quad was shaded; bit 1 = at least one face used a
     *         darkened texture
     */
    public int renderToOutput(BlockState state, long outputBuffer) {
        // Zero the entire face block.
        MemoryUtil.memSet(outputBuffer, 0, ModelFactory.MODEL_TEXTURE_SIZE
                * ModelFactory.MODEL_TEXTURE_SIZE * 8 * FACE_COUNT);

        boolean isBlock = !(state.getBlock() instanceof LiquidBlock);
        ChunkSectionLayer layer;
        if (state.getBlock() instanceof LiquidBlock) {
            layer = ChunkSectionLayer.from(ItemBlockRenderTypes.getRenderLayer(state.getFluidState()));
        } else if (state.getBlock() instanceof LeavesBlock) {
            // Leaves get rendered into the solid layer so distant LODs aren't transparent.
            layer = ChunkSectionLayer.SOLID;
        } else {
            layer = ChunkSectionLayer.from(ItemBlockRenderTypes.getChunkRenderType(state));
        }

        // TODO upstream: support BlockEntity-backed renders via BakedBlockEntityModel.

        this.rasterizer.setBlending(layer == ChunkSectionLayer.TRANSLUCENT);

        boolean isAnyShaded = false;
        boolean isAnyDarkened = false;

        if (isBlock) {
            this.vc.reset();
            bakeBlockModel(state, layer);
            isAnyShaded |= this.vc.anyShaded;
            isAnyDarkened |= this.vc.anyDarkendTex;
            if (!this.vc.isEmpty()) {
                for (int face = 0; face < VIEWS.length; face++) {
                    this.rasterizer.setFaceCull(shouldFaceCull(face));
                    this.rasterizer.clear();
                    this.rasterizer.raster(VIEWS[face], this.vc);
                    UnsafeUtil.memcpy(this.rasterizer.getRawFramebuffer(),
                            outputBuffer + SINGLE_FACE_OUTPUT_SIZE * face);
                }
            }
        } else {
            // Fluid path: re-bake per face because the synthetic neighbour state changes.
            for (int face = 0; face < VIEWS.length; face++) {
                this.vc.reset();
                bakeFluidState(state, layer, face);
                if (this.vc.isEmpty()) continue;
                isAnyShaded |= this.vc.anyShaded;
                isAnyDarkened |= this.vc.anyDarkendTex;

                this.rasterizer.setFaceCull(shouldFaceCull(face));
                this.rasterizer.clear();
                this.rasterizer.raster(VIEWS[face], this.vc);
                UnsafeUtil.memcpy(this.rasterizer.getRawFramebuffer(),
                        outputBuffer + SINGLE_FACE_OUTPUT_SIZE * face);
            }
        }

        return (isAnyShaded ? 1 : 0) | (isAnyDarkened ? 2 : 0);
    }

    // ---- view matrix bootstrap -------------------------------------------------------

    static {
        // Face index, pitch, yaw, roll, flip mask (bit 0 = flip-x, bit 1 = flip-y, bit 2 = flip-z).
        addView(0, -90,   0,   0, 0b000); // DOWN
        addView(1,  90,   0,   0, 0b100); // UP
        addView(2,   0, 180,   0, 0b001); // NORTH
        addView(3,   0,   0,   0, 0b000); // SOUTH
        addView(4,   0,  90, 270, 0b100); // WEST
        addView(5,   0, 270, 270, 0b000); // EAST
    }

    private static void addView(int i, float pitch, float yaw, float rotation, int flip) {
        var stack = new PoseStack();
        stack.translate(0.5f, 0.5f, 0.5f);
        stack.mulPose(quatFromAxis(new Vector3f(0, 0, 1), rotation));
        stack.mulPose(quatFromAxis(new Vector3f(1, 0, 0), pitch));
        stack.mulPose(quatFromAxis(new Vector3f(0, 1, 0), yaw));
        stack.last().pose().mul(new Matrix4f().scale(
                1 - 2 * (flip & 1),
                1 - (flip & 2),
                1 - ((flip >> 1) & 2)));
        stack.translate(-0.5f, -0.5f, -0.5f);

        // Compose with an orthographic projection mapping [0,1] -> [-1,+1] in xy and
        // inverted z; the result transforms cube-local coords directly to clip space.
        Matrix4f ortho = new Matrix4f().set(
                2,  0,  0, 0,
                0,  2,  0, 0,
                0,  0, -2, 0,
                -1, -1, 1, 1);
        VIEWS[i] = ortho.mul(new Matrix4f(stack.last().pose()));
    }

    /** Axis-angle quaternion identical in math to upstream's hand-rolled construction. */
    private static Quaternionf quatFromAxis(Vector3f axis, float angleDegrees) {
        float angle = (float) Math.toRadians(angleDegrees);
        float halfAngle = angle / 2.0f;
        float sin = (float) Math.sin(halfAngle);
        float invLen = (float) (1 / Math.sqrt(axis.lengthSquared()));
        return new Quaternionf(
                axis.x * invLen * sin,
                axis.y * invLen * sin,
                axis.z * invLen * sin,
                Math.cos(halfAngle));
    }
}
