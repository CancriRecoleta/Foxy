package com.github.foxy.client.core.gl.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL43C.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.NVMeshShader.GL_MESH_SHADER_NV;
import static org.lwjgl.opengl.NVMeshShader.GL_TASK_SHADER_NV;

/**
 * Tag for the kind of GLSL stage a shader source string declares.
 *
 * <p>The {@link #gl} value is the {@code GL_*_SHADER} constant accepted by
 * {@code glCreateShader}; the {@code MESH} / {@code TASK} entries depend on
 * {@link org.lwjgl.opengl.NVMeshShader NV_mesh_shader} which only newer NVIDIA drivers
 * expose.</p>
 */
public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER),
    COMPUTE(GL_COMPUTE_SHADER),
    MESH(GL_MESH_SHADER_NV),
    TASK(GL_TASK_SHADER_NV);

    /** {@code glCreateShader} stage enum. */
    public final int gl;

    ShaderType(int glEnum) { this.gl = glEnum; }
}
