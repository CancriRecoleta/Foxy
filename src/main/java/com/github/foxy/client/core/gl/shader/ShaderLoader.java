package com.github.foxy.client.core.gl.shader;

import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loader and {@code #import} resolver for Foxy's GLSL assets.
 *
 * <h2>Layout on disk</h2>
 * <p>Sources live under {@code assets/<namespace>/shaders/<path>} inside the mod jar
 * (matching vanilla resource conventions). The loader reaches them via classpath
 * {@code getResourceAsStream}, which works regardless of Minecraft's resource manager
 * lifecycle &mdash; shaders can be loaded at any time, not just after a resource-pack
 * reload.</p>
 *
 * <h2>{@code #import}</h2>
 * <pre>
 *   #import &lt;foxy:lod/common.glsl&gt;
 * </pre>
 * Imports are resolved recursively; the imported file's {@code #version} line is
 * stripped (the root file's version wins, and the loader prepends a single
 * {@code #version 460 core} line for the final output). Cycles will trigger a stack
 * overflow &mdash; intentional, since GLSL has no header guards and treating the case
 * as user error matches upstream behaviour.
 *
 * <p>Anything that doesn't match the {@code #import &lt;ns:path&gt;} or {@code #version}
 * patterns is passed through verbatim.</p>
 */
public final class ShaderLoader {
    private ShaderLoader() {}

    private static final Pattern IMPORT_PATTERN = Pattern.compile("#import <(?<namespace>[^:>]+):(?<path>[^>]+)>");

    /**
     * Loads {@code id} (e.g. {@code "foxy:lod/sectionMesh.vert"}), resolves its
     * {@code #import} graph, and returns the assembled GLSL source ready for
     * {@code glShaderSource}.
     */
    public static String parse(String id) {
        ResourceLocation root = ResourceLocation.tryParse(id);
        if (root == null) {
            throw new IllegalArgumentException("Invalid shader resource id: " + id);
        }
        StringBuilder sb = new StringBuilder("#version 460 core\n");
        for (String line : parseRoot(root)) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /** Recursively expand the imports of {@code id} into a flat line list. */
    private static List<String> parseRoot(ResourceLocation id) {
        var out = new ArrayList<String>();
        for (String line : loadAsset(id).lines().toList()) {
            if (line.startsWith("#version")) {
                // Stripped: the top-level entry point will emit a single #version 460 line.
                continue;
            }
            if (line.startsWith("#import")) {
                Matcher m = IMPORT_PATTERN.matcher(line);
                if (!m.matches()) {
                    throw new IllegalArgumentException("Malformed #import directive: " + line);
                }
                ResourceLocation imported = ResourceLocation.tryBuild(m.group("namespace"), m.group("path"));
                if (imported == null) {
                    throw new IllegalArgumentException("Invalid #import target in: " + line);
                }
                out.addAll(parseRoot(imported));
                continue;
            }
            out.add(line);
        }
        return out;
    }

    private static String loadAsset(ResourceLocation id) {
        String path = "/assets/" + id.getNamespace() + "/shaders/" + id.getPath();
        try (InputStream in = ShaderLoader.class.getResourceAsStream(path)) {
            if (in == null) throw new RuntimeException("Shader asset not found: " + path);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shader asset " + path, e);
        }
    }
}
