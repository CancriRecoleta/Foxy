//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.models.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
    public static final Provider CUBE;
    public static final Provider CUBE_MIRRORED;
    public static final Provider COLUMN;
    public static final Provider COLUMN_HORIZONTAL;
    public static final Provider CUBE_TOP_BOTTOM;
    public static final Provider CUBE_TOP;
    public static final Provider ORIENTABLE_ONLY_TOP;
    public static final Provider ORIENTABLE;
    public static final Provider CARPET;
    public static final Provider FLOWERBED_1;
    public static final Provider FLOWERBED_2;
    public static final Provider FLOWERBED_3;
    public static final Provider FLOWERBED_4;
    public static final Provider GLAZED_TERRACOTTA;
    public static final Provider CORAL_FAN;
    public static final Provider PARTICLE_ONLY;
    public static final Provider ANVIL;
    public static final Provider LEAVES;
    public static final Provider LANTERN;
    public static final Provider HANGING_LANTERN;
    public static final Provider SEAGRASS;
    public static final Provider COLUMN_ALT;
    public static final Provider COLUMN_HORIZONTAL_ALT;
    public static final Provider TOP_BOTTOM_WITH_WALL;
    public static final Provider COLUMN_WITH_WALL;
    private final TextureMapping mapping;
    private final ModelTemplate template;

    private TexturedModel(TextureMapping p_125930_, ModelTemplate p_125931_) {
        this.mapping = p_125930_;
        this.template = p_125931_;
    }

    public ModelTemplate getTemplate() {
        return this.template;
    }

    public TextureMapping getMapping() {
        return this.mapping;
    }

    public TexturedModel updateTextures(Consumer<TextureMapping> p_125941_) {
        p_125941_.accept(this.mapping);
        return this;
    }

    public ResourceLocation create(Block p_125938_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125939_) {
        return this.template.create(p_125938_, this.mapping, p_125939_);
    }

    public ResourceLocation createWithSuffix(Block p_125934_, String p_125935_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125936_) {
        return this.template.createWithSuffix(p_125934_, p_125935_, this.mapping, p_125936_);
    }

    private static Provider createDefault(Function<Block, TextureMapping> p_125943_, ModelTemplate p_125944_) {
        return (p_125948_) -> {
            return new TexturedModel((TextureMapping)p_125943_.apply(p_125948_), p_125944_);
        };
    }

    public static TexturedModel createAllSame(ResourceLocation p_125950_) {
        return new TexturedModel(TextureMapping.cube(p_125950_), ModelTemplates.CUBE_ALL);
    }

    static {
        CUBE = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
        CUBE_MIRRORED = createDefault(TextureMapping::cube, ModelTemplates.CUBE_MIRRORED_ALL);
        COLUMN = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN);
        COLUMN_HORIZONTAL = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
        CUBE_TOP_BOTTOM = createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP);
        CUBE_TOP = createDefault(TextureMapping::cubeTop, ModelTemplates.CUBE_TOP);
        ORIENTABLE_ONLY_TOP = createDefault(TextureMapping::orientableCubeOnlyTop, ModelTemplates.CUBE_ORIENTABLE);
        ORIENTABLE = createDefault(TextureMapping::orientableCube, ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
        CARPET = createDefault(TextureMapping::wool, ModelTemplates.CARPET);
        FLOWERBED_1 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_1);
        FLOWERBED_2 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_2);
        FLOWERBED_3 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_3);
        FLOWERBED_4 = createDefault(TextureMapping::flowerbed, ModelTemplates.FLOWERBED_4);
        GLAZED_TERRACOTTA = createDefault(TextureMapping::pattern, ModelTemplates.GLAZED_TERRACOTTA);
        CORAL_FAN = createDefault(TextureMapping::fan, ModelTemplates.CORAL_FAN);
        PARTICLE_ONLY = createDefault(TextureMapping::particle, ModelTemplates.PARTICLE_ONLY);
        ANVIL = createDefault(TextureMapping::top, ModelTemplates.ANVIL);
        LEAVES = createDefault(TextureMapping::cube, ModelTemplates.LEAVES);
        LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.LANTERN);
        HANGING_LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.HANGING_LANTERN);
        SEAGRASS = createDefault(TextureMapping::defaultTexture, ModelTemplates.SEAGRASS);
        COLUMN_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN);
        COLUMN_HORIZONTAL_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
        TOP_BOTTOM_WITH_WALL = createDefault(TextureMapping::cubeBottomTopWithWall, ModelTemplates.CUBE_BOTTOM_TOP);
        COLUMN_WITH_WALL = createDefault(TextureMapping::columnWithWall, ModelTemplates.CUBE_COLUMN);
    }

    @FunctionalInterface
    public interface Provider {
        TexturedModel get(Block var1);

        default ResourceLocation create(Block p_125957_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125958_) {
            return this.get(p_125957_).create(p_125957_, p_125958_);
        }

        default ResourceLocation createWithSuffix(Block p_125953_, String p_125954_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125955_) {
            return this.get(p_125953_).createWithSuffix(p_125953_, p_125954_, p_125955_);
        }

        default Provider updateTexture(Consumer<TextureMapping> p_125960_) {
            return (p_125963_) -> {
                return this.get(p_125963_).updateTextures(p_125960_);
            };
        }
    }
}
