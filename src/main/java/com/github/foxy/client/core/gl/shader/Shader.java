package com.github.foxy.client.core.gl.shader;

import com.github.foxy.client.core.gl.Capabilities;
import com.github.foxy.client.core.gl.GlDebug;
import com.github.foxy.client.core.gl.GlObject;
import com.github.foxy.common.Logger;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * GL shader-program wrapper plus a fluent {@link Builder} for assembling and
 * compiling them.
 *
 * <h2>Build pipeline</h2>
 * <ol>
 *   <li>Caller chooses {@link #make(IShaderProcessor...)} for plain {@code Shader}s
 *       or {@link #makeAuto(IShaderProcessor...)} for {@link AutoBindingShader}s.</li>
 *   <li>Adds sources via {@link Builder#add Builder.add} (loads from
 *       {@code assets/foxy/shaders/...} via {@link ShaderLoader}) or
 *       {@link Builder#addSource Builder.addSource} (raw string).</li>
 *   <li>Optionally injects {@code #define}s ({@link Builder#define}) and
 *       text replacements ({@link Builder#replace}).</li>
 *   <li>{@link Builder#compile()} pushes the compose-then-apply chain through every
 *       {@link IShaderProcessor}, runs {@code glCreateShader} / {@code glCompileShader}
 *       per stage, links them together, and returns the wrapper.</li>
 * </ol>
 *
 * <h2>Auto-defines</h2>
 * The builder unconditionally adds {@code IS_INTEL} (when {@link Capabilities#isIntel}) and
 * {@code IS_WINDOWS} (when running on Windows) so shader assets can branch on common
 * driver-specific workarounds without the caller spelling them out at every call site.
 *
 * <h2>Diagnostics</h2>
 * On a compile failure the offending source is dumped to
 * {@code <gamedir>/foxy-shader-dump.txt} alongside the GL info log, then a
 * {@link RuntimeException} is thrown so the loader hits the fail-fast path.
 */
public class Shader extends GlObject {
    private final int id;

    Shader(int program) { this.id = program; }

    /** Underlying GL program name. */
    public int id() { return this.id; }

    /** {@code glUseProgram(id)}. */
    public void bind() { glUseProgram(this.id); }

    @Override
    public void free() {
        free0();
        glDeleteProgram(this.id);
    }

    /** Optional KHR-debug program label. */
    public Shader name(String label) {
        GlDebug.nameProgram(label, this.id);
        return this;
    }

    /** Starts a builder that produces a plain {@link Shader}. */
    public static Builder<Shader> make(IShaderProcessor... processors) {
        return makeInternal((b, prog) -> new Shader(prog), processors);
    }

    /** Starts a builder that produces an {@link AutoBindingShader}. */
    public static Builder<AutoBindingShader> makeAuto(IShaderProcessor... processors) {
        return makeInternal(AutoBindingShader::new, processors);
    }

    /** Composes processors right-to-left so the first one runs on the raw source. */
    static <T extends Shader> Builder<T> makeInternal(Builder.IShaderObjectConstructor<T> ctor,
                                                      IShaderProcessor[] processors) {
        IShaderProcessor chain = (type, source) -> source;
        for (IShaderProcessor processor : processors) {
            IShaderProcessor prev = chain;
            chain = (type, source) -> prev.process(type, processor.process(type, source));
        }
        return new Builder<>(ctor, chain);
    }

    /**
     * Mutable accumulator for one shader program. Methods are chainable and the
     * builder is single-use: {@link #compile()} consumes its state.
     */
    public static class Builder<T extends Shader> {
        /** Constructor adapter: lets {@link AutoBindingShader} read the builder's defines. */
        protected interface IShaderObjectConstructor<J extends Shader> {
            J make(Builder<J> builder, int program);
        }

        final Map<String, String> defines = new HashMap<>();
        final Map<String, String> replacements = new LinkedHashMap<>();
        private final Map<ShaderType, String> sources = new HashMap<>();
        private final IShaderProcessor processor;
        private final IShaderObjectConstructor<T> constructor;

        Builder(IShaderObjectConstructor<T> constructor, IShaderProcessor processor) {
            this.constructor = constructor;
            this.processor = processor;
        }

        /** Returns a deep copy; useful when the same source skeleton is compiled with different defines. */
        @Override
        public Builder<T> clone() {
            var c = new Builder<>(this.constructor, this.processor);
            c.defines.putAll(this.defines);
            c.sources.putAll(this.sources);
            c.replacements.putAll(this.replacements);
            return c;
        }

        /** {@code #define name}. */
        public Builder<T> define(String name) { this.defines.put(name, ""); return this; }

        /** Conditionally {@code #define name}. */
        public Builder<T> defineIf(String name, boolean condition) {
            if (condition) this.defines.put(name, "");
            return this;
        }

        /** Conditionally {@code #define name value}. */
        public Builder<T> defineIf(String name, boolean condition, int value) {
            if (condition) this.defines.put(name, Integer.toString(value));
            return this;
        }

        /** {@code #define name value}. */
        public Builder<T> define(String name, int value) {
            this.defines.put(name, Integer.toString(value));
            return this;
        }

        /** {@code #define name value} with a GLSL float-suffix. */
        public Builder<T> define(String name, float value) {
            this.defines.put(name, Float.toString(value) + "f");
            return this;
        }

        /** {@code #define name value} with the literal text supplied by the caller. */
        public Builder<T> define(String name, String value) {
            this.defines.put(name, value);
            return this;
        }

        /** Na鑼倂e string-replace applied after defines but before {@code glShaderSource}. */
        public Builder<T> replace(String token, String replacement) {
            this.replacements.put(token, replacement);
            return this;
        }

        /** Loads {@code id} via {@link ShaderLoader} and adds it as the {@code type} stage. */
        public Builder<T> add(ShaderType type, String id) {
            this.addSource(type, ShaderLoader.parse(id));
            return this;
        }

        /** Adds a raw source string as the {@code type} stage; passes it through the processor chain. */
        public Builder<T> addSource(ShaderType type, String source) {
            this.sources.put(type, this.processor.process(type, source));
            return this;
        }

        /** Hook for caller-supplied configuration; useful when conditionally adding stages. */
        public Builder<T> apply(Consumer<Builder<T>> applyer) {
            applyer.accept(this);
            return this;
        }

        /** Compiles every stage, links them, and wraps the program with the configured constructor. */
        public T compile() {
            // Auto-defines: unconditionally bake the host driver / OS into the source so
            // assets can write `#ifdef IS_INTEL` / `#ifdef IS_WINDOWS` for known quirks.
            this.defineIf("IS_INTEL", Capabilities.get().isIntel);
            this.defineIf("IS_WINDOWS", System.getProperty("os.name", "").startsWith("Windows"));
            return this.constructor.make(this, compileToProgram());
        }

        private int compileToProgram() {
            int program = GL20C.glCreateProgram();
            int[] shaders = new int[this.sources.size()];

            String defs = this.defines.entrySet().stream()
                    .map(e -> "#define " + e.getKey() + " " + e.getValue() + "\n")
                    .collect(Collectors.joining());

            int i = 0;
            for (var entry : this.sources.entrySet()) {
                String src = entry.getValue();
                // Inject defines after the first newline (which holds the #version line).
                int firstNewline = src.indexOf('\n');
                src = src.substring(0, firstNewline + 1) + defs + src.substring(firstNewline + 1);
                for (var rep : this.replacements.entrySet()) {
                    src = src.replace(rep.getKey(), rep.getValue());
                }
                shaders[i++] = createShader(entry.getKey(), src);
            }

            for (int s : shaders) GL20C.glAttachShader(program, s);
            GL20C.glLinkProgram(program);
            for (int s : shaders) {
                GL20C.glDetachShader(program, s);
                GL20C.glDeleteShader(s);
            }
            verifyLinked(program);
            return program;
        }

        private static void verifyLinked(int program) {
            String log = GL20C.glGetProgramInfoLog(program);
            if (!log.isEmpty()) Logger.error(log);
            if (GL20C.glGetProgrami(program, GL20C.GL_LINK_STATUS) != GL20C.GL_TRUE) {
                throw new RuntimeException("Shader program link failed; see log");
            }
        }

        /** Compiles one stage; on failure dumps the offending source to disk and throws. */
        private static int createShader(ShaderType type, String src) {
            int shader = GL20C.glCreateShader(type.gl);
            // Sodium's well-known {@code nglShaderSource} workaround: hand the driver an
            // explicit pointer to a NUL-terminated UTF-8 string instead of relying on
            // LWJGL to translate the Java String, which has tripped some Mesa drivers.
            long ptr = MemoryUtil.memAddress(MemoryUtil.memUTF8(src, true));
            try (var stack = MemoryStack.stackPush()) {
                GL20C.nglShaderSource(shader, 1, stack.pointers(ptr).address0(), 0L);
            } finally {
                MemoryUtil.nmemFree(ptr);
            }
            GL20C.glCompileShader(shader);

            String log = GL20C.glGetShaderInfoLog(shader);
            if (!log.isEmpty()) Logger.warn(log);

            if (GL20C.glGetShaderi(shader, GL20C.GL_COMPILE_STATUS) != GL20C.GL_TRUE) {
                GL20C.glDeleteShader(shader);
                dumpFailedShader(src);
                throw new RuntimeException("Shader compile failed (" + type.name() + "); source dumped");
            }
            return shader;
        }

        /** Best-effort dump to {@code <gamedir>/foxy-shader-dump.txt} for offline inspection. */
        private static void dumpFailedShader(String src) {
            try {
                Path dump = FMLPaths.GAMEDIR.get().resolve("foxy-shader-dump.txt");
                Files.writeString(dump, src);
                Logger.error("Foxy: failed shader dumped to " + dump);
            } catch (IOException e) {
                Logger.error("Foxy: failed to write shader dump", e);
            }
        }

        // ---- forwarding accessors used by AutoBindingShader.Builder ----------------------

        /** Read-only view of the accumulated defines; consumed by {@link AutoBindingShader}. */
        Map<String, String> definesView() { return this.defines; }

        /** Empty source list query, used in tests / introspection. */
        List<ShaderType> stages() { return new ArrayList<>(this.sources.keySet()); }
    }
}
