package com.github.foxy.client.core.gl;

import com.github.foxy.client.core.gl.shader.ShaderType;
import com.github.foxy.common.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GLCapabilities;

import java.util.Locale;

import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.opengl.GL11C.GL_VENDOR;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL32.glGetInteger64;
import static org.lwjgl.opengl.GL43C.GL_MAX_SHADER_STORAGE_BLOCK_SIZE;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT;
import static org.lwjgl.opengl.GL43C.glGetInteger;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX;
import static org.lwjgl.opengl.NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX;

/**
 * GL feature detection collected once after the context is current.
 *
 * <p>Foxy's renderer requires a few specific GL 4.5+ features (compute shaders, indirect
 * draw with parameter buffers, persistent-mapped buffers, SSBOs, KHR debug labelling).
 * This class probes the active context and exposes per-feature booleans plus a few
 * tuning constants ({@link #ssboMaxSize}, {@link #ssboBindingAlignment}).</p>
 *
 * <h2>Lifecycle</h2>
 * {@link #INSTANCE} is allocated lazily on first call to {@link #init()}, which must
 * happen on the GL thread after the context exists (i.e. in or after Forge's
 * {@code FMLClientSetupEvent}).
 *
 * <h2>Cleanroom note</h2>
 * Upstream Voxy probes a known-broken AMD depth-sampler quirk by uploading a real
 * compute shader and checking its output. The Foxy port skips that for now &mdash; it
 * requires more GL state setup than is appropriate at startup, and the renderer can
 * gate its own depth-sampling shader on the same vendor check at the call site.
 */
public final class Capabilities {

    public static volatile Capabilities INSTANCE;

    /** Initialises the singleton from the active GL context. Idempotent. */
    public static synchronized Capabilities init() {
        if (INSTANCE == null) INSTANCE = new Capabilities();
        return INSTANCE;
    }

    /** Singleton accessor; throws when {@link #init()} hasn't been called yet. */
    public static Capabilities get() {
        Capabilities c = INSTANCE;
        if (c == null) throw new IllegalStateException("Capabilities.init() not yet called");
        return c;
    }

    // ---- driver / vendor ---------------------------------------------------------------

    /** {@code glGetString(GL_VENDOR)}, lower-cased. */
    public final String vendor;
    /** {@code glGetString(GL_VERSION)}, lower-cased. */
    public final String version;
    /** {@code true} when the driver string contains {@code "mesa"}. */
    public final boolean isMesa;
    /** {@code true} when the vendor string contains {@code "intel"}. */
    public final boolean isIntel;
    /** {@code true} when the vendor string contains {@code "nvidia"}. */
    public final boolean isNvidia;
    /** {@code true} when the vendor string contains {@code "amd"} or {@code "radeon"}. */
    public final boolean isAmd;

    // ---- core / extension features ----------------------------------------------------

    /** Whether compute shaders are available (required by Foxy renderer). */
    public final boolean compute;
    /** Whether indirect draws can read their counts from a parameter buffer ({@code GL_ARB_indirect_parameters}). */
    public final boolean indirectParameters;
    /** Whether {@code GL_ARB_sparse_buffer} is exposed. */
    public final boolean sparseBuffer;
    /** Whether {@code GL_NV_representative_fragment_test} is available. */
    public final boolean repFragTest;
    /** Whether {@code GL_NV_mesh_shader} is available. */
    public final boolean meshShaders;
    /** Whether {@code GL_NV_fragment_shader_barycentric} is available. */
    public final boolean nvBarycentric;

    /** Whether GLSL 64-bit integers compile (probed by trial compile). */
    public final boolean int64;
    /** Whether KHR subgroup operations compile (probed by trial compile). */
    public final boolean subgroup;

    // ---- tuning constants -------------------------------------------------------------

    /** Maximum bindable SSBO size in bytes. */
    public final long ssboMaxSize;
    /** Required SSBO binding offset alignment, in bytes. */
    public final int ssboBindingAlignment;

    // ---- memory query (NVX_gpu_memory_info) -------------------------------------------

    /** Whether {@code GL_NVX_gpu_memory_info} can be queried. */
    public final boolean canQueryGpuMemory;
    /** Bytes of dedicated GPU memory (only valid when {@link #canQueryGpuMemory}). */
    public final long totalDedicatedMemory;
    /** Bytes of dynamic GPU memory (only valid when {@link #canQueryGpuMemory}). */
    public final long totalDynamicMemory;

    // ---- known driver quirks ----------------------------------------------------------

    /**
     * Whether the driver exhibits the AMD broken-depth-sampler bug. The probe is
     * deferred to the call site that actually wants to sample depth from a compute
     * shader; this field is always {@code false} on the Foxy port and the renderer
     * is expected to test on demand.
     */
    public final boolean hasBrokenDepthSampler;

    private Capabilities() {
        GLCapabilities cap = GL.getCapabilities();

        this.version = safeGetString(GL_VERSION).toLowerCase(Locale.ROOT);
        this.vendor = safeGetString(GL_VENDOR).toLowerCase(Locale.ROOT);
        this.isMesa = this.version.contains("mesa");
        this.isIntel = this.vendor.contains("intel");
        this.isNvidia = this.vendor.contains("nvidia");
        this.isAmd = this.vendor.contains("amd") || this.vendor.contains("radeon");

        this.compute = cap.glDispatchComputeIndirect != 0;
        this.indirectParameters = cap.glMultiDrawElementsIndirectCountARB != 0;
        this.sparseBuffer = cap.GL_ARB_sparse_buffer;
        this.repFragTest = cap.GL_NV_representative_fragment_test;
        this.meshShaders = cap.GL_NV_mesh_shader;
        this.nvBarycentric = cap.GL_NV_fragment_shader_barycentric;

        // Probing 64-bit-int and subgroup support by extension flags is unreliable on
        // some drivers 閳?they advertise the extensions but reject the actual GLSL
        // tokens. Compile a tiny stub for each and trust the result.
        this.int64 = compilesOk(ShaderType.COMPUTE, """
                #version 430
                #extension GL_ARB_gpu_shader_int64 : require
                layout(local_size_x=32) in;
                void main() { uint64_t a = 1234; }
                """);
        this.subgroup = cap.GL_KHR_shader_subgroup && compilesOk(ShaderType.COMPUTE, """
                #version 430
                #extension GL_KHR_shader_subgroup_basic : require
                #extension GL_KHR_shader_subgroup_arithmetic : require
                layout(local_size_x=32) in;
                void main() { uint a = subgroupExclusiveAdd(gl_LocalInvocationIndex); }
                """);

        this.ssboMaxSize = glGetInteger64(GL_MAX_SHADER_STORAGE_BLOCK_SIZE);
        this.ssboBindingAlignment = glGetInteger(GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT);

        this.canQueryGpuMemory = cap.GL_NVX_gpu_memory_info;
        if (this.canQueryGpuMemory) {
            // NVX returns kilobytes; convert to bytes for callers.
            this.totalDedicatedMemory = glGetInteger64(GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX) * 1024L;
            this.totalDynamicMemory = (glGetInteger64(GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) * 1024L)
                    - this.totalDedicatedMemory;
        } else {
            this.totalDedicatedMemory = -1L;
            this.totalDynamicMemory = -1L;
        }

        // Cleanroom port keeps this disabled; see field doc.
        this.hasBrokenDepthSampler = false;

        Logger.info("Foxy GL capabilities: vendor='" + this.vendor + "', version='" + this.version
                + "', compute=" + this.compute + ", indirectParameters=" + this.indirectParameters
                + ", subgroup=" + this.subgroup + ", int64=" + this.int64);
    }

    /** Returns the current free dedicated VRAM in bytes; throws when querying isn't supported. */
    public long getFreeDedicatedGpuMemory() {
        if (!this.canQueryGpuMemory) {
            throw new IllegalStateException("Cannot query GPU memory; NVX_gpu_memory_info not available");
        }
        return glGetInteger64(GL_GPU_MEMORY_INFO_CURRENT_AVAILABLE_VIDMEM_NVX) * 1024L;
    }

    private static String safeGetString(int e) {
        String s = glGetString(e);
        return s == null ? "" : s;
    }

    /**
     * Tries to compile {@code src} as a {@code type} shader; returns whether the GL
     * driver accepted it. The shader is deleted before return regardless of result.
     */
    private static boolean compilesOk(ShaderType type, String src) {
        int shader = GL20C.glCreateShader(type.gl);
        try {
            GL20C.glShaderSource(shader, src);
            GL20C.glCompileShader(shader);
            return GL20C.glGetShaderi(shader, GL20C.GL_COMPILE_STATUS) == GL20C.GL_TRUE;
        } finally {
            GL20C.glDeleteShader(shader);
        }
    }
}
