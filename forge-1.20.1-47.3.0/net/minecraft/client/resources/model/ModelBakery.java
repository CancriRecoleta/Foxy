//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
    public static final Material FIRE_0;
    public static final Material FIRE_1;
    public static final Material LAVA_FLOW;
    public static final Material WATER_FLOW;
    public static final Material WATER_OVERLAY;
    public static final Material BANNER_BASE;
    public static final Material SHIELD_BASE;
    public static final Material NO_PATTERN_SHIELD;
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES;
    public static final List<ResourceLocation> BREAKING_LOCATIONS;
    public static final List<RenderType> DESTROY_TYPES;
    static final int SINGLETON_MODEL_GROUP = -1;
    private static final int INVISIBLE_MODEL_GROUP = 0;
    private static final Logger LOGGER;
    private static final String BUILTIN_SLASH = "builtin/";
    private static final String BUILTIN_SLASH_GENERATED = "builtin/generated";
    private static final String BUILTIN_BLOCK_ENTITY = "builtin/entity";
    private static final String MISSING_MODEL_NAME = "missing";
    public static final ModelResourceLocation MISSING_MODEL_LOCATION;
    public static final FileToIdConverter BLOCKSTATE_LISTER;
    public static final FileToIdConverter MODEL_LISTER;
    @VisibleForTesting
    public static final String MISSING_MODEL_MESH;
    private static final Map<String, String> BUILTIN_MODELS;
    private static final Splitter COMMA_SPLITTER;
    private static final Splitter EQUAL_SPLITTER;
    public static final BlockModel GENERATION_MARKER;
    public static final BlockModel BLOCK_ENTITY_MARKER;
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION;
    static final ItemModelGenerator ITEM_MODEL_GENERATOR;
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS;
    private final BlockColors blockColors;
    private final Map<ResourceLocation, BlockModel> modelResources;
    private final Map<ResourceLocation, List<LoadedJson>> blockStateResources;
    private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
    private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
    private final Map<ResourceLocation, UnbakedModel> unbakedCache = Maps.newHashMap();
    final Map<BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
    private final Map<ResourceLocation, UnbakedModel> topLevelModels = Maps.newHashMap();
    private final Map<ResourceLocation, BakedModel> bakedTopLevelModels = Maps.newHashMap();
    private int nextModelGroup = 1;
    private final Object2IntMap<BlockState> modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (p_119309_) -> {
        p_119309_.defaultReturnValue(-1);
    });

    public ModelBakery(BlockColors p_249183_, ProfilerFiller p_252014_, Map<ResourceLocation, BlockModel> p_251087_, Map<ResourceLocation, List<LoadedJson>> p_250416_) {
        this.blockColors = p_249183_;
        this.modelResources = p_251087_;
        this.blockStateResources = p_250416_;
        p_252014_.push("missing_model");

        try {
            this.unbakedCache.put(MISSING_MODEL_LOCATION, this.loadBlockModel(MISSING_MODEL_LOCATION));
            this.loadTopLevel(MISSING_MODEL_LOCATION);
        } catch (IOException var9) {
            IOException ioexception = var9;
            LOGGER.error("Error loading missing model, should never happen :(", ioexception);
            throw new RuntimeException(ioexception);
        }

        p_252014_.popPush("static_definitions");
        STATIC_DEFINITIONS.forEach((p_119347_, p_119348_) -> {
            p_119348_.getPossibleStates().forEach((p_174905_) -> {
                this.loadTopLevel(BlockModelShaper.stateToModelLocation(p_119347_, p_174905_));
            });
        });
        p_252014_.popPush("blocks");
        Iterator var10 = BuiltInRegistries.BLOCK.iterator();

        while(var10.hasNext()) {
            Block block = (Block)var10.next();
            block.getStateDefinition().getPossibleStates().forEach((p_119264_) -> {
                this.loadTopLevel(BlockModelShaper.stateToModelLocation(p_119264_));
            });
        }

        p_252014_.popPush("items");
        var10 = BuiltInRegistries.ITEM.keySet().iterator();

        while(var10.hasNext()) {
            ResourceLocation resourcelocation = (ResourceLocation)var10.next();
            this.loadTopLevel(new ModelResourceLocation(resourcelocation, "inventory"));
        }

        p_252014_.popPush("special");
        this.loadTopLevel(ItemRenderer.TRIDENT_IN_HAND_MODEL);
        this.loadTopLevel(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
        Set<ResourceLocation> additionalModels = Sets.newHashSet();
        ForgeHooksClient.onRegisterAdditionalModels(additionalModels);
        Iterator var13 = additionalModels.iterator();

        while(var13.hasNext()) {
            ResourceLocation rl = (ResourceLocation)var13.next();
            UnbakedModel unbakedmodel = this.getModel(rl);
            this.unbakedCache.put(rl, unbakedmodel);
            this.topLevelModels.put(rl, unbakedmodel);
        }

        this.topLevelModels.values().forEach((p_247954_) -> {
            p_247954_.resolveParents(this::getModel);
        });
        p_252014_.pop();
    }

    public void bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> p_248669_) {
        this.topLevelModels.keySet().forEach((p_247958_) -> {
            BakedModel bakedmodel = null;

            try {
                bakedmodel = (new ModelBakerImpl(p_248669_, p_247958_)).bake(p_247958_, BlockModelRotation.X0_Y0);
            } catch (Exception var5) {
                Exception exception = var5;
                LOGGER.warn("Unable to bake model: '{}': {}", p_247958_, exception);
            }

            if (bakedmodel != null) {
                this.bakedTopLevelModels.put(p_247958_, bakedmodel);
            }

        });
    }

    private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> p_119274_, String p_119275_) {
        Map<Property<?>, Comparable<?>> map = Maps.newHashMap();
        Iterator var3 = COMMA_SPLITTER.split(p_119275_).iterator();

        while(true) {
            while(true) {
                Iterator iterator;
                do {
                    if (!var3.hasNext()) {
                        Block block = (Block)p_119274_.getOwner();
                        return (p_119262_) -> {
                            if (p_119262_ != null && p_119262_.is(block)) {
                                Iterator var3 = map.entrySet().iterator();

                                Map.Entry entry;
                                do {
                                    if (!var3.hasNext()) {
                                        return true;
                                    }

                                    entry = (Map.Entry)var3.next();
                                } while(Objects.equals(p_119262_.getValue((Property)entry.getKey()), entry.getValue()));

                                return false;
                            } else {
                                return false;
                            }
                        };
                    }

                    String s = (String)var3.next();
                    iterator = EQUAL_SPLITTER.split(s).iterator();
                } while(!iterator.hasNext());

                String s1 = (String)iterator.next();
                Property<?> property = p_119274_.getProperty(s1);
                if (property != null && iterator.hasNext()) {
                    String s2 = (String)iterator.next();
                    Comparable<?> comparable = getValueHelper(property, s2);
                    if (comparable == null) {
                        throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + property.getPossibleValues());
                    }

                    map.put(property, comparable);
                } else if (!s1.isEmpty()) {
                    throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
                }
            }
        }
    }

    @Nullable
    static <T extends Comparable<T>> T getValueHelper(Property<T> p_119277_, String p_119278_) {
        return (Comparable)p_119277_.getValue(p_119278_).orElse((Comparable)null);
    }

    public UnbakedModel getModel(ResourceLocation p_119342_) {
        if (this.unbakedCache.containsKey(p_119342_)) {
            return (UnbakedModel)this.unbakedCache.get(p_119342_);
        } else if (this.loadingStack.contains(p_119342_)) {
            throw new IllegalStateException("Circular reference while loading " + p_119342_);
        } else {
            this.loadingStack.add(p_119342_);
            UnbakedModel unbakedmodel = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);

            while(!this.loadingStack.isEmpty()) {
                ResourceLocation resourcelocation = (ResourceLocation)this.loadingStack.iterator().next();

                try {
                    if (!this.unbakedCache.containsKey(resourcelocation)) {
                        this.loadModel(resourcelocation);
                    }
                } catch (BlockStateDefinitionException var9) {
                    BlockStateDefinitionException modelbakery$blockstatedefinitionexception = var9;
                    LOGGER.warn(modelbakery$blockstatedefinitionexception.getMessage());
                    this.unbakedCache.put(resourcelocation, unbakedmodel);
                } catch (Exception var10) {
                    Exception exception = var10;
                    LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", new Object[]{resourcelocation, p_119342_, exception});
                    this.unbakedCache.put(resourcelocation, unbakedmodel);
                } finally {
                    this.loadingStack.remove(resourcelocation);
                }
            }

            return (UnbakedModel)this.unbakedCache.getOrDefault(p_119342_, unbakedmodel);
        }
    }

    private void loadModel(ResourceLocation p_119363_) throws Exception {
        if (p_119363_ instanceof ModelResourceLocation modelresourcelocation) {
            ResourceLocation resourcelocation;
            if (Objects.equals(modelresourcelocation.getVariant(), "inventory")) {
                resourcelocation = p_119363_.withPrefix("item/");
                BlockModel blockmodel = this.loadBlockModel(resourcelocation);
                this.cacheAndQueueDependencies(modelresourcelocation, blockmodel);
                this.unbakedCache.put(resourcelocation, blockmodel);
            } else {
                resourcelocation = new ResourceLocation(p_119363_.getNamespace(), p_119363_.getPath());
                StateDefinition<Block, BlockState> statedefinition = (StateDefinition)Optional.ofNullable((StateDefinition)STATIC_DEFINITIONS.get(resourcelocation)).orElseGet(() -> {
                    return ((Block)BuiltInRegistries.BLOCK.get(resourcelocation)).getStateDefinition();
                });
                this.context.setDefinition(statedefinition);
                List<Property<?>> list = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)statedefinition.getOwner()));
                ImmutableList<BlockState> immutablelist = statedefinition.getPossibleStates();
                Map<ModelResourceLocation, BlockState> map = Maps.newHashMap();
                immutablelist.forEach((p_119330_) -> {
                    map.put(BlockModelShaper.stateToModelLocation(resourcelocation, p_119330_), p_119330_);
                });
                Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>> map1 = Maps.newHashMap();
                ResourceLocation resourcelocation1 = BLOCKSTATE_LISTER.idToFile(p_119363_);
                UnbakedModel unbakedmodel = (UnbakedModel)this.unbakedCache.get(MISSING_MODEL_LOCATION);
                ModelGroupKey modelbakery$modelgroupkey = new ModelGroupKey(ImmutableList.of(unbakedmodel), ImmutableList.of());
                Pair pair = Pair.of(unbakedmodel, () -> {
                    return modelbakery$modelgroupkey;
                });
                boolean var23 = false;

                try {
                    var23 = true;
                    Iterator var29 = ((List)this.blockStateResources.getOrDefault(resourcelocation1, List.of())).stream().map((p_247956_) -> {
                        try {
                            return Pair.of(p_247956_.source, BlockModelDefinition.fromJsonElement(this.context, p_247956_.data));
                        } catch (Exception var4) {
                            Exception exception1 = var4;
                            throw new BlockStateDefinitionException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resourcelocation1, p_247956_.source, exception1.getMessage()));
                        }
                    }).toList().iterator();

                    while(true) {
                        if (!var29.hasNext()) {
                            var23 = false;
                            break;
                        }

                        Pair<String, BlockModelDefinition> pair1 = (Pair)var29.next();
                        BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)pair1.getSecond();
                        Map<BlockState, Pair<UnbakedModel, Supplier<ModelGroupKey>>> map2 = Maps.newIdentityHashMap();
                        MultiPart multipart;
                        if (blockmodeldefinition.isMultiPart()) {
                            multipart = blockmodeldefinition.getMultiPart();
                            immutablelist.forEach((p_119326_) -> {
                                map2.put(p_119326_, Pair.of(multipart, () -> {
                                    return net.minecraft.client.resources.model.ModelBakery.ModelGroupKey.create(p_119326_, (MultiPart)multipart, list);
                                }));
                            });
                        } else {
                            multipart = null;
                        }

                        blockmodeldefinition.getVariants().forEach((p_119289_, p_119290_) -> {
                            try {
                                immutablelist.stream().filter(predicate(statedefinition, p_119289_)).forEach((p_174902_) -> {
                                    Pair<UnbakedModel, Supplier<ModelGroupKey>> pair2 = (Pair)map2.put(p_174902_, Pair.of(p_119290_, () -> {
                                        return net.minecraft.client.resources.model.ModelBakery.ModelGroupKey.create(p_174902_, (UnbakedModel)p_119290_, list);
                                    }));
                                    if (pair2 != null && pair2.getFirst() != multipart) {
                                        map2.put(p_174902_, pair);
                                        Optional var10002 = blockmodeldefinition.getVariants().entrySet().stream().filter((p_174892_) -> {
                                            return p_174892_.getValue() == pair2.getFirst();
                                        }).findFirst();
                                        throw new RuntimeException("Overlapping definition with: " + (String)((Map.Entry)var10002.get()).getKey());
                                    }
                                });
                            } catch (Exception var12) {
                                Exception exception1 = var12;
                                LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", new Object[]{resourcelocation1, pair1.getFirst(), p_119289_, exception1.getMessage()});
                            }

                        });
                        map1.putAll(map2);
                    }
                } catch (BlockStateDefinitionException var24) {
                    BlockStateDefinitionException modelbakery$blockstatedefinitionexception = var24;
                    throw modelbakery$blockstatedefinitionexception;
                } catch (Exception var25) {
                    Exception exception = var25;
                    throw new BlockStateDefinitionException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s': %s", resourcelocation1, exception));
                } finally {
                    if (var23) {
                        HashMap map3 = Maps.newHashMap();
                        map.forEach((p_119336_, p_119337_) -> {
                            Pair<UnbakedModel, Supplier<ModelGroupKey>> pair2 = (Pair)map1.get(p_119337_);
                            if (pair2 == null) {
                                LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_119336_);
                                pair2 = pair;
                            }

                            this.cacheAndQueueDependencies(p_119336_, (UnbakedModel)pair2.getFirst());

                            try {
                                ModelGroupKey modelbakery$modelgroupkey1 = (ModelGroupKey)((Supplier)pair2.getSecond()).get();
                                ((Set)map3.computeIfAbsent(modelbakery$modelgroupkey1, (p_174894_) -> {
                                    return Sets.newIdentityHashSet();
                                })).add(p_119337_);
                            } catch (Exception var9) {
                                Exception exception1 = var9;
                                LOGGER.warn("Exception evaluating model definition: '{}'", p_119336_, exception1);
                            }

                        });
                        map3.forEach((p_284640_, p_284641_) -> {
                            Iterator<BlockState> iterator = p_284641_.iterator();

                            while(iterator.hasNext()) {
                                BlockState blockstate = (BlockState)iterator.next();
                                if (blockstate.getRenderShape() != RenderShape.MODEL) {
                                    iterator.remove();
                                    this.modelGroups.put(blockstate, 0);
                                }
                            }

                            if (p_284641_.size() > 1) {
                                this.registerModelGroup(p_284641_);
                            }

                        });
                    }
                }

                Map<ModelGroupKey, Set<BlockState>> map3 = Maps.newHashMap();
                map.forEach((p_119336_, p_119337_) -> {
                    Pair<UnbakedModel, Supplier<ModelGroupKey>> pair2 = (Pair)map1.get(p_119337_);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_119336_);
                        pair2 = pair;
                    }

                    this.cacheAndQueueDependencies(p_119336_, (UnbakedModel)pair2.getFirst());

                    try {
                        ModelGroupKey modelbakery$modelgroupkey1 = (ModelGroupKey)((Supplier)pair2.getSecond()).get();
                        ((Set)map3.computeIfAbsent(modelbakery$modelgroupkey1, (p_174894_) -> {
                            return Sets.newIdentityHashSet();
                        })).add(p_119337_);
                    } catch (Exception var9) {
                        Exception exception1 = var9;
                        LOGGER.warn("Exception evaluating model definition: '{}'", p_119336_, exception1);
                    }

                });
                map3.forEach((p_284640_, p_284641_) -> {
                    Iterator<BlockState> iterator = p_284641_.iterator();

                    while(iterator.hasNext()) {
                        BlockState blockstate = (BlockState)iterator.next();
                        if (blockstate.getRenderShape() != RenderShape.MODEL) {
                            iterator.remove();
                            this.modelGroups.put(blockstate, 0);
                        }
                    }

                    if (p_284641_.size() > 1) {
                        this.registerModelGroup(p_284641_);
                    }

                });
            }
        } else {
            this.cacheAndQueueDependencies(p_119363_, this.loadBlockModel(p_119363_));
        }

    }

    private void cacheAndQueueDependencies(ResourceLocation p_119353_, UnbakedModel p_119354_) {
        this.unbakedCache.put(p_119353_, p_119354_);
        this.loadingStack.addAll(p_119354_.getDependencies());
    }

    private void loadTopLevel(ModelResourceLocation p_119307_) {
        UnbakedModel unbakedmodel = this.getModel(p_119307_);
        this.unbakedCache.put(p_119307_, unbakedmodel);
        this.topLevelModels.put(p_119307_, unbakedmodel);
    }

    private void registerModelGroup(Iterable<BlockState> p_119311_) {
        int i = this.nextModelGroup++;
        p_119311_.forEach((p_119256_) -> {
            this.modelGroups.put(p_119256_, i);
        });
    }

    protected BlockModel loadBlockModel(ResourceLocation p_119365_) throws IOException {
        String s = p_119365_.getPath();
        if ("builtin/generated".equals(s)) {
            return GENERATION_MARKER;
        } else if ("builtin/entity".equals(s)) {
            return BLOCK_ENTITY_MARKER;
        } else if (s.startsWith("builtin/")) {
            String s1 = s.substring("builtin/".length());
            String s2 = (String)BUILTIN_MODELS.get(s1);
            if (s2 == null) {
                throw new FileNotFoundException(p_119365_.toString());
            } else {
                Reader reader = new StringReader(s2);
                BlockModel blockmodel1 = BlockModel.fromStream(reader);
                blockmodel1.name = p_119365_.toString();
                return blockmodel1;
            }
        } else {
            ResourceLocation resourcelocation = MODEL_LISTER.idToFile(p_119365_);
            BlockModel blockmodel = (BlockModel)this.modelResources.get(resourcelocation);
            if (blockmodel == null) {
                throw new FileNotFoundException(resourcelocation.toString());
            } else {
                blockmodel.name = p_119365_.toString();
                return blockmodel;
            }
        }
    }

    public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }

    public Object2IntMap<BlockState> getModelGroups() {
        return this.modelGroups;
    }

    static {
        FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
        FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
        LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
        WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
        WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
        BANNER_BASE = new Material(Sheets.BANNER_SHEET, new ResourceLocation("entity/banner_base"));
        SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, new ResourceLocation("entity/shield_base"));
        NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, new ResourceLocation("entity/shield_base_nopattern"));
        DESTROY_STAGES = (List)IntStream.range(0, 10).mapToObj((p_119253_) -> {
            return new ResourceLocation("block/destroy_stage_" + p_119253_);
        }).collect(Collectors.toList());
        BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map((p_119371_) -> {
            return new ResourceLocation("textures/" + p_119371_.getPath() + ".png");
        }).collect(Collectors.toList());
        DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
        LOGGER = LogUtils.getLogger();
        MISSING_MODEL_LOCATION = ModelResourceLocation.vanilla("builtin/missing", "missing");
        BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
        MODEL_LISTER = FileToIdConverter.json("models");
        MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureAtlasSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureAtlasSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
        BUILTIN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
        COMMA_SPLITTER = Splitter.on(',');
        EQUAL_SPLITTER = Splitter.on('=').limit(2);
        GENERATION_MARKER = (BlockModel)Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), (p_119359_) -> {
            p_119359_.name = "generation marker";
        });
        BLOCK_ENTITY_MARKER = (BlockModel)Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), (p_119297_) -> {
            p_119297_.name = "block entity marker";
        });
        ITEM_FRAME_FAKE_DEFINITION = (new StateDefinition.Builder(Blocks.AIR)).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
        ITEM_MODEL_GENERATOR = new ItemModelGenerator();
        STATIC_DEFINITIONS = ImmutableMap.of(new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION, new ResourceLocation("glow_item_frame"), ITEM_FRAME_FAKE_DEFINITION);
    }

    @OnlyIn(Dist.CLIENT)
    static class BlockStateDefinitionException extends RuntimeException {
        public BlockStateDefinitionException(String p_119373_) {
            super(p_119373_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ModelGroupKey {
        private final List<UnbakedModel> models;
        private final List<Object> coloringValues;

        public ModelGroupKey(List<UnbakedModel> p_119377_, List<Object> p_119378_) {
            this.models = p_119377_;
            this.coloringValues = p_119378_;
        }

        public boolean equals(Object p_119395_) {
            if (this == p_119395_) {
                return true;
            } else if (!(p_119395_ instanceof ModelGroupKey)) {
                return false;
            } else {
                ModelGroupKey modelbakery$modelgroupkey = (ModelGroupKey)p_119395_;
                return Objects.equals(this.models, modelbakery$modelgroupkey.models) && Objects.equals(this.coloringValues, modelbakery$modelgroupkey.coloringValues);
            }
        }

        public int hashCode() {
            return 31 * this.models.hashCode() + this.coloringValues.hashCode();
        }

        public static ModelGroupKey create(BlockState p_119380_, MultiPart p_119381_, Collection<Property<?>> p_119382_) {
            StateDefinition<Block, BlockState> statedefinition = p_119380_.getBlock().getStateDefinition();
            List<UnbakedModel> list = (List)p_119381_.getSelectors().stream().filter((p_119393_) -> {
                return p_119393_.getPredicate(statedefinition).test(p_119380_);
            }).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
            List<Object> list1 = getColoringValues(p_119380_, p_119382_);
            return new ModelGroupKey(list, list1);
        }

        public static ModelGroupKey create(BlockState p_119384_, UnbakedModel p_119385_, Collection<Property<?>> p_119386_) {
            List<Object> list = getColoringValues(p_119384_, p_119386_);
            return new ModelGroupKey(ImmutableList.of(p_119385_), list);
        }

        private static List<Object> getColoringValues(BlockState p_119388_, Collection<Property<?>> p_119389_) {
            Stream var10000 = p_119389_.stream();
            Objects.requireNonNull(p_119388_);
            return (List)var10000.map(p_119388_::getValue).collect(ImmutableList.toImmutableList());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static record LoadedJson(String source, JsonElement data) {
        public LoadedJson(String source, JsonElement data) {
            this.source = source;
            this.data = data;
        }

        public String source() {
            return this.source;
        }

        public JsonElement data() {
            return this.data;
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ModelBakerImpl implements ModelBaker {
        private final Function<Material, TextureAtlasSprite> modelTextureGetter;

        ModelBakerImpl(BiFunction<ResourceLocation, Material, TextureAtlasSprite> p_249651_, ResourceLocation p_251408_) {
            this.modelTextureGetter = (p_250859_) -> {
                return (TextureAtlasSprite)p_249651_.apply(p_251408_, p_250859_);
            };
        }

        public UnbakedModel getModel(ResourceLocation p_248568_) {
            return ModelBakery.this.getModel(p_248568_);
        }

        public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
            return this.modelTextureGetter;
        }

        public BakedModel bake(ResourceLocation p_252176_, ModelState p_249765_) {
            return this.bake(p_252176_, p_249765_, this.modelTextureGetter);
        }

        public BakedModel bake(ResourceLocation p_252176_, ModelState p_249765_, Function<Material, TextureAtlasSprite> sprites) {
            BakedCacheKey modelbakery$bakedcachekey = new BakedCacheKey(p_252176_, p_249765_.getRotation(), p_249765_.isUvLocked());
            BakedModel bakedmodel = (BakedModel)ModelBakery.this.bakedCache.get(modelbakery$bakedcachekey);
            if (bakedmodel != null) {
                return bakedmodel;
            } else {
                UnbakedModel unbakedmodel = this.getModel(p_252176_);
                if (unbakedmodel instanceof BlockModel) {
                    BlockModel blockmodel = (BlockModel)unbakedmodel;
                    if (blockmodel.getRootModel() == ModelBakery.GENERATION_MARKER) {
                        return ModelBakery.ITEM_MODEL_GENERATOR.generateBlockModel(sprites, blockmodel).bake(this, blockmodel, sprites, p_249765_, p_252176_, false);
                    }
                }

                BakedModel bakedmodel1 = unbakedmodel.bake(this, sprites, p_249765_, p_252176_);
                ModelBakery.this.bakedCache.put(modelbakery$bakedcachekey, bakedmodel1);
                return bakedmodel1;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
        BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
            this.id = id;
            this.transformation = transformation;
            this.isUvLocked = isUvLocked;
        }

        public ResourceLocation id() {
            return this.id;
        }

        public Transformation transformation() {
            return this.transformation;
        }

        public boolean isUvLocked() {
            return this.isUvLocked;
        }
    }
}
