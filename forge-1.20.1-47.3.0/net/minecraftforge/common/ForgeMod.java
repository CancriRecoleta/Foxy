//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.DetectedVersion;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.AndCondition.Serializer;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBiomeTagsProvider;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeEntityTypeTagsProvider;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.common.data.ForgeSpriteSourceProvider;
import net.minecraftforge.common.data.VanillaSoundDefinitionsProvider;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.common.world.NoneBiomeModifier;
import net.minecraftforge.common.world.NoneStructureModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType.Properties;
import net.minecraftforge.fml.CrashReportCallables;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsMod;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.filters.VanillaPacketSplitter;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeDeferredRegistriesSetup;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.IdMappingEvent;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.AnyHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

@Mod("forge")
public class ForgeMod {
    public static final String VERSION_CHECK_CAT = "version_checking";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");
    private static final DeferredRegister<Attribute> ATTRIBUTES;
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES;
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS;
    private static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS;
    private static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES;
    private static final RegistryObject<EnumArgument.Info> ENUM_COMMAND_ARGUMENT_TYPE;
    private static final RegistryObject<SingletonArgumentInfo<ModIdArgument>> MODID_COMMAND_ARGUMENT_TYPE;
    public static final RegistryObject<Attribute> SWIM_SPEED;
    public static final RegistryObject<Attribute> NAMETAG_DISTANCE;
    public static final RegistryObject<Attribute> ENTITY_GRAVITY;
    public static final RegistryObject<Attribute> BLOCK_REACH;
    public static final RegistryObject<Attribute> ENTITY_REACH;
    public static final RegistryObject<Attribute> STEP_HEIGHT_ADDITION;
    public static final RegistryObject<Codec<NoneBiomeModifier>> NONE_BIOME_MODIFIER_TYPE;
    public static final RegistryObject<Codec<ForgeBiomeModifiers.AddFeaturesBiomeModifier>> ADD_FEATURES_BIOME_MODIFIER_TYPE;
    public static final RegistryObject<Codec<ForgeBiomeModifiers.RemoveFeaturesBiomeModifier>> REMOVE_FEATURES_BIOME_MODIFIER_TYPE;
    public static final RegistryObject<Codec<ForgeBiomeModifiers.AddSpawnsBiomeModifier>> ADD_SPAWNS_BIOME_MODIFIER_TYPE;
    public static final RegistryObject<Codec<ForgeBiomeModifiers.RemoveSpawnsBiomeModifier>> REMOVE_SPAWNS_BIOME_MODIFIER_TYPE;
    public static final RegistryObject<Codec<NoneStructureModifier>> NONE_STRUCTURE_MODIFIER_TYPE;
    public static final RegistryObject<HolderSetType> ANY_HOLDER_SET;
    public static final RegistryObject<HolderSetType> AND_HOLDER_SET;
    public static final RegistryObject<HolderSetType> OR_HOLDER_SET;
    public static final RegistryObject<HolderSetType> NOT_HOLDER_SET;
    private static final DeferredRegister<FluidType> VANILLA_FLUID_TYPES;
    public static final RegistryObject<FluidType> EMPTY_TYPE;
    public static final RegistryObject<FluidType> WATER_TYPE;
    public static final RegistryObject<FluidType> LAVA_TYPE;
    private static boolean enableMilkFluid;
    public static final RegistryObject<SoundEvent> BUCKET_EMPTY_MILK;
    public static final RegistryObject<SoundEvent> BUCKET_FILL_MILK;
    public static final RegistryObject<FluidType> MILK_TYPE;
    public static final RegistryObject<Fluid> MILK;
    public static final RegistryObject<Fluid> FLOWING_MILK;
    private static ForgeMod INSTANCE;
    public static final PermissionNode<Boolean> USE_SELECTORS_PERMISSION;

    public static ForgeMod getInstance() {
        return INSTANCE;
    }

    public static void enableMilkFluid() {
        enableMilkFluid = true;
    }

    public ForgeMod() {
        LOGGER.info(FORGEMOD, "Forge mod loading, version {}, for MC {} with MCP {}", ForgeVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
        ForgeSnapshotsMod.logStartupWarning();
        INSTANCE = this;
        MinecraftForge.initialize();
        CrashReportCallables.registerCrashCallable("Crash Report UUID", () -> {
            UUID uuid = UUID.randomUUID();
            LOGGER.fatal("Preparing crash report with UUID {}", uuid);
            return uuid.toString();
        });
        LOGGER.debug(FORGEMOD, "Loading Network data for FML net version: {}", NetworkConstants.init());
        CrashReportCallables.registerCrashCallable("FML", ForgeVersion::getSpec);
        CrashReportCallables.registerCrashCallable("Forge", () -> {
            String var10000 = ForgeVersion.getGroup();
            return var10000 + ":" + ForgeVersion.getVersion();
        });
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener((event) -> {
            event.dataPackRegistry(Keys.BIOME_MODIFIERS, BiomeModifier.DIRECT_CODEC);
            event.dataPackRegistry(Keys.STRUCTURE_MODIFIERS, StructureModifier.DIRECT_CODEC);
        });
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(this::registerFluids);
        modEventBus.addListener(this::registerVanillaDisplayContexts);
        modEventBus.addListener(this::registerRecipeSerializers);
        modEventBus.addListener(this::registerLootData);
        modEventBus.register(this);
        ATTRIBUTES.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        STRUCTURE_MODIFIER_SERIALIZERS.register(modEventBus);
        HOLDER_SET_TYPES.register(modEventBus);
        VANILLA_FLUID_TYPES.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
        MinecraftForge.EVENT_BUS.addListener(this::missingSoundMapping);
        ModLoadingContext.get().registerConfig(Type.CLIENT, ForgeConfig.clientSpec);
        ModLoadingContext.get().registerConfig(Type.SERVER, ForgeConfig.serverSpec);
        ModLoadingContext.get().registerConfig(Type.COMMON, ForgeConfig.commonSpec);
        modEventBus.register(ForgeConfig.class);
        ForgeDeferredRegistriesSetup.setup(modEventBus);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> {
            return new IExtensionPoint.DisplayTest(() -> {
                return "ANY";
            }, (remote, isServer) -> {
                return true;
            });
        });
        StartupMessageManager.addModMessage("Forge version " + ForgeVersion.getVersion());
        MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        MinecraftForge.EVENT_BUS.addListener(this::mappingChanged);
        MinecraftForge.EVENT_BUS.addListener(this::registerPermissionNodes);
        ForgeRegistries.ITEMS.tags().addOptionalTagDefaults(Items.ENCHANTING_FUELS, Set.of(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)net.minecraft.world.item.Items.LAPIS_LAZULI)));
        addAlias(ForgeRegistries.ATTRIBUTES, new ResourceLocation("forge", "reach_distance"), new ResourceLocation("forge", "block_reach"));
        addAlias(ForgeRegistries.ATTRIBUTES, new ResourceLocation("forge", "attack_range"), new ResourceLocation("forge", "entity_reach"));
    }

    public void preInit(FMLCommonSetupEvent evt) {
        VersionChecker.startVersionCheck();
        VanillaPacketSplitter.register();
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
    }

    public void serverStopping(ServerStoppingEvent evt) {
        WorldWorkerManager.clear();
    }

    public void mappingChanged(IdMappingEvent evt) {
        Ingredient.invalidateAll();
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackMetadataGenerator var10002 = new PackMetadataGenerator(packOutput);
        MetadataSectionType var10003 = PackMetadataSection.TYPE;
        MutableComponent var10006 = Component.translatable("pack.forge.description");
        int var10007 = DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES);
        Stream var10008 = Arrays.stream(PackType.values());
        Function var10009 = Function.identity();
        WorldVersion var10010 = DetectedVersion.BUILT_IN;
        Objects.requireNonNull(var10010);
        gen.addProvider(true, (DataProvider)var10002.add(var10003, new PackMetadataSection(var10006, var10007, (Map)var10008.collect(Collectors.toMap(var10009, var10010::getPackVersion)))));
        ForgeBlockTagsProvider blockTags = new ForgeBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), (DataProvider)blockTags);
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper)));
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeEntityTypeTagsProvider(packOutput, lookupProvider, existingFileHelper)));
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeFluidTagsProvider(packOutput, lookupProvider, existingFileHelper)));
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeRecipeProvider(packOutput)));
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeLootTableProvider(packOutput)));
        gen.addProvider(event.includeServer(), (DataProvider)(new ForgeBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper)));
        gen.addProvider(event.includeClient(), (DataProvider)(new ForgeSpriteSourceProvider(packOutput, existingFileHelper)));
        gen.addProvider(event.includeClient(), (DataProvider)(new VanillaSoundDefinitionsProvider(packOutput, existingFileHelper)));
    }

    public void missingSoundMapping(MissingMappingsEvent event) {
        if (event.getKey() == Keys.SOUND_EVENTS) {
            List<String> removedSounds = Arrays.asList("entity.parrot.imitate.panda", "entity.parrot.imitate.zombie_pigman", "entity.parrot.imitate.enderman", "entity.parrot.imitate.polar_bear", "entity.parrot.imitate.wolf");
            Iterator var3 = event.getAllMappings(Keys.SOUND_EVENTS).iterator();

            while(var3.hasNext()) {
                MissingMappingsEvent.Mapping<SoundEvent> mapping = (MissingMappingsEvent.Mapping)var3.next();
                ResourceLocation regName = mapping.getKey();
                if (regName != null && regName.getNamespace().equals("minecraft")) {
                    String path = regName.getPath();
                    if (removedSounds.stream().anyMatch((s) -> {
                        return s.equals(path);
                    })) {
                        LOGGER.info("Ignoring removed minecraft sound {}", regName);
                        mapping.ignore();
                    }
                }
            }

        }
    }

    public void registerFluids(RegisterEvent event) {
        if (enableMilkFluid) {
            event.register(Keys.SOUND_EVENTS, (helper) -> {
                helper.register((ResourceLocation)BUCKET_EMPTY_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_EMPTY_MILK.getId()));
                helper.register((ResourceLocation)BUCKET_FILL_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_FILL_MILK.getId()));
            });
            event.register(Keys.FLUID_TYPES, (helper) -> {
                helper.register((ResourceLocation)MILK_TYPE.getId(), new FluidType(Properties.create().density(1024).viscosity(1024).sound(SoundActions.BUCKET_FILL, (SoundEvent)BUCKET_FILL_MILK.get()).sound(SoundActions.BUCKET_EMPTY, (SoundEvent)BUCKET_EMPTY_MILK.get())) {
                    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                        consumer.accept(new IClientFluidTypeExtensions() {
                            private static final ResourceLocation MILK_STILL = new ResourceLocation("forge", "block/milk_still");
                            private static final ResourceLocation MILK_FLOW = new ResourceLocation("forge", "block/milk_flowing");

                            public ResourceLocation getStillTexture() {
                                return MILK_STILL;
                            }

                            public ResourceLocation getFlowingTexture() {
                                return MILK_FLOW;
                            }
                        });
                    }
                });
            });
            event.register(Keys.FLUIDS, (helper) -> {
                ForgeFlowingFluid.Properties properties = (new ForgeFlowingFluid.Properties(MILK_TYPE, MILK, FLOWING_MILK)).bucket(() -> {
                    return net.minecraft.world.item.Items.MILK_BUCKET;
                });
                helper.register((ResourceLocation)MILK.getId(), new ForgeFlowingFluid.Source(properties));
                helper.register((ResourceLocation)FLOWING_MILK.getId(), new ForgeFlowingFluid.Flowing(properties));
            });
        }

    }

    public void registerVanillaDisplayContexts(RegisterEvent event) {
        if (event.getRegistryKey().equals(Keys.DISPLAY_CONTEXTS)) {
            IForgeRegistryInternal<ItemDisplayContext> forgeRegistry = (IForgeRegistryInternal)event.getForgeRegistry();
            if (forgeRegistry == null) {
                throw new IllegalStateException("Item display context was not a forge registry, wtf???");
            }

            Arrays.stream(ItemDisplayContext.values()).filter(Predicate.not(ItemDisplayContext::isModded)).forEach((ctx) -> {
                forgeRegistry.register(ctx.getId(), new ResourceLocation("minecraft", ctx.getSerializedName()), ctx);
            });
        }

    }

    public void registerRecipeSerializers(RegisterEvent event) {
        if (event.getRegistryKey().equals(Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.FalseCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.ItemExistsCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.ModLoadedCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.NotCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.OrCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.TrueCondition.Serializer.INSTANCE);
            CraftingHelper.register(net.minecraftforge.common.crafting.conditions.TagEmptyCondition.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "compound"), net.minecraftforge.common.crafting.CompoundIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "nbt"), net.minecraftforge.common.crafting.StrictNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "partial_nbt"), net.minecraftforge.common.crafting.PartialNBTIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "difference"), net.minecraftforge.common.crafting.DifferenceIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("forge", "intersection"), net.minecraftforge.common.crafting.IntersectionIngredient.Serializer.INSTANCE);
            CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
            event.register(Keys.RECIPE_SERIALIZERS, new ResourceLocation("forge", "conditional"), ConditionalRecipe.Serializer::new);
        }

    }

    public void registerLootData(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.LOOT_CONDITION_TYPE)) {
            event.register(Registries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:loot_table_id"), () -> {
                return LootTableIdCondition.LOOT_TABLE_ID;
            });
            event.register(Registries.LOOT_CONDITION_TYPE, new ResourceLocation("forge:can_tool_perform_action"), () -> {
                return CanToolPerformAction.LOOT_CONDITION_TYPE;
            });
        }
    }

    public void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        event.addNodes(USE_SELECTORS_PERMISSION);
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20"
    )
    private static <T> void addAlias(IForgeRegistry<T> registry, ResourceLocation from, ResourceLocation to) {
        ForgeRegistry<T> fReg = (ForgeRegistry)registry;
        fReg.addAlias(from, to);
    }

    static {
        ATTRIBUTES = DeferredRegister.create(Keys.ATTRIBUTES, "forge");
        COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, "forge");
        BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, "forge");
        STRUCTURE_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.STRUCTURE_MODIFIER_SERIALIZERS, "forge");
        HOLDER_SET_TYPES = DeferredRegister.create(Keys.HOLDER_SET_TYPES, "forge");
        ENUM_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("enum", () -> {
            return (EnumArgument.Info)ArgumentTypeInfos.registerByClass(EnumArgument.class, new EnumArgument.Info());
        });
        MODID_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("modid", () -> {
            return (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
        });
        SWIM_SPEED = ATTRIBUTES.register("swim_speed", () -> {
            return (new RangedAttribute("forge.swim_speed", 1.0, 0.0, 1024.0)).setSyncable(true);
        });
        NAMETAG_DISTANCE = ATTRIBUTES.register("nametag_distance", () -> {
            return (new RangedAttribute("forge.name_tag_distance", 64.0, 0.0, 64.0)).setSyncable(true);
        });
        ENTITY_GRAVITY = ATTRIBUTES.register("entity_gravity", () -> {
            return (new RangedAttribute("forge.entity_gravity", 0.08, -8.0, 8.0)).setSyncable(true);
        });
        BLOCK_REACH = ATTRIBUTES.register("block_reach", () -> {
            return (new RangedAttribute("forge.block_reach", 4.5, 0.0, 1024.0)).setSyncable(true);
        });
        ENTITY_REACH = ATTRIBUTES.register("entity_reach", () -> {
            return (new RangedAttribute("forge.entity_reach", 3.0, 0.0, 1024.0)).setSyncable(true);
        });
        STEP_HEIGHT_ADDITION = ATTRIBUTES.register("step_height_addition", () -> {
            return (new RangedAttribute("forge.step_height", 0.0, -512.0, 512.0)).setSyncable(true);
        });
        NONE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("none", () -> {
            return Codec.unit(NoneBiomeModifier.INSTANCE);
        });
        ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_features", () -> {
            return RecordCodecBuilder.create((builder) -> {
                return builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::biomes), PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::features), Decoration.CODEC.fieldOf("step").forGetter(ForgeBiomeModifiers.AddFeaturesBiomeModifier::step)).apply(builder, ForgeBiomeModifiers.AddFeaturesBiomeModifier::new);
            });
        });
        REMOVE_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_features", () -> {
            return RecordCodecBuilder.create((builder) -> {
                return builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::biomes), PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::features), (new ExtraCodecs.EitherCodec(Decoration.CODEC.listOf(), Decoration.CODEC)).xmap((either) -> {
                    return (Set)either.map(Set::copyOf, Set::of);
                }, (set) -> {
                    return set.size() == 1 ? Either.right(((GenerationStep.Decoration[])set.toArray((x$0) -> {
                        return new GenerationStep.Decoration[x$0];
                    }))[0]) : Either.left(List.copyOf(set));
                }).optionalFieldOf("steps", EnumSet.allOf(GenerationStep.Decoration.class)).forGetter(ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::steps)).apply(builder, ForgeBiomeModifiers.RemoveFeaturesBiomeModifier::new);
            });
        });
        ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () -> {
            return RecordCodecBuilder.create((builder) -> {
                return builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(ForgeBiomeModifiers.AddSpawnsBiomeModifier::biomes), (new ExtraCodecs.EitherCodec(SpawnerData.CODEC.listOf(), SpawnerData.CODEC)).xmap((either) -> {
                    return (List)either.map(Function.identity(), List::of);
                }, (list) -> {
                    return list.size() == 1 ? Either.right((MobSpawnSettings.SpawnerData)list.get(0)) : Either.left(list);
                }).fieldOf("spawners").forGetter(ForgeBiomeModifiers.AddSpawnsBiomeModifier::spawners)).apply(builder, ForgeBiomeModifiers.AddSpawnsBiomeModifier::new);
            });
        });
        REMOVE_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_spawns", () -> {
            return RecordCodecBuilder.create((builder) -> {
                return builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::biomes), RegistryCodecs.homogeneousList(Keys.ENTITY_TYPES).fieldOf("entity_types").forGetter(ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::entityTypes)).apply(builder, ForgeBiomeModifiers.RemoveSpawnsBiomeModifier::new);
            });
        });
        NONE_STRUCTURE_MODIFIER_TYPE = STRUCTURE_MODIFIER_SERIALIZERS.register("none", () -> {
            return Codec.unit(NoneStructureModifier.INSTANCE);
        });
        ANY_HOLDER_SET = HOLDER_SET_TYPES.register("any", () -> {
            return AnyHolderSet::codec;
        });
        AND_HOLDER_SET = HOLDER_SET_TYPES.register("and", () -> {
            return AndHolderSet::codec;
        });
        OR_HOLDER_SET = HOLDER_SET_TYPES.register("or", () -> {
            return OrHolderSet::codec;
        });
        NOT_HOLDER_SET = HOLDER_SET_TYPES.register("not", () -> {
            return NotHolderSet::codec;
        });
        VANILLA_FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, "minecraft");
        EMPTY_TYPE = VANILLA_FLUID_TYPES.register("empty", () -> {
            return new FluidType(Properties.create().descriptionId("block.minecraft.air").motionScale(1.0).canPushEntity(false).canSwim(false).canDrown(false).fallDistanceModifier(1.0F).pathType((BlockPathTypes)null).adjacentPathType((BlockPathTypes)null).density(0).temperature(0).viscosity(0)) {
                public void setItemMovement(ItemEntity entity) {
                    if (!entity.isNoGravity()) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, -0.04, 0.0));
                    }

                }
            };
        });
        WATER_TYPE = VANILLA_FLUID_TYPES.register("water", () -> {
            return new FluidType(Properties.create().descriptionId("block.minecraft.water").fallDistanceModifier(0.0F).canExtinguish(true).canConvertToSource(true).supportsBoating(true).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY).sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH).canHydrate(true)) {
                public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
                    return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
                }

                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
                        private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
                        private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
                        private static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");

                        public ResourceLocation getStillTexture() {
                            return WATER_STILL;
                        }

                        public ResourceLocation getFlowingTexture() {
                            return WATER_FLOW;
                        }

                        public @Nullable ResourceLocation getOverlayTexture() {
                            return WATER_OVERLAY;
                        }

                        public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                            return UNDERWATER_LOCATION;
                        }

                        public int getTintColor() {
                            return -12618012;
                        }

                        public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                            return BiomeColors.getAverageWaterColor(getter, pos) | -16777216;
                        }
                    });
                }
            };
        });
        LAVA_TYPE = VANILLA_FLUID_TYPES.register("lava", () -> {
            return new FluidType(Properties.create().descriptionId("block.minecraft.lava").canSwim(false).canDrown(false).pathType(BlockPathTypes.LAVA).adjacentPathType((BlockPathTypes)null).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA).lightLevel(15).density(3000).viscosity(6000).temperature(1300)) {
                public double motionScale(Entity entity) {
                    return entity.level().dimensionType().ultraWarm() ? 0.007 : 0.0023333333333333335;
                }

                public void setItemMovement(ItemEntity entity) {
                    Vec3 vec3 = entity.getDeltaMovement();
                    entity.setDeltaMovement(vec3.x * 0.949999988079071, vec3.y + (double)(vec3.y < 0.05999999865889549 ? 5.0E-4F : 0.0F), vec3.z * 0.949999988079071);
                }

                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation LAVA_STILL = new ResourceLocation("block/lava_still");
                        private static final ResourceLocation LAVA_FLOW = new ResourceLocation("block/lava_flow");

                        public ResourceLocation getStillTexture() {
                            return LAVA_STILL;
                        }

                        public ResourceLocation getFlowingTexture() {
                            return LAVA_FLOW;
                        }
                    });
                }
            };
        });
        enableMilkFluid = false;
        BUCKET_EMPTY_MILK = RegistryObject.create(new ResourceLocation("item.bucket.empty_milk"), ForgeRegistries.SOUND_EVENTS);
        BUCKET_FILL_MILK = RegistryObject.create(new ResourceLocation("item.bucket.fill_milk"), ForgeRegistries.SOUND_EVENTS);
        MILK_TYPE = RegistryObject.createOptional(new ResourceLocation("milk"), Keys.FLUID_TYPES.location(), "minecraft");
        MILK = RegistryObject.create(new ResourceLocation("milk"), ForgeRegistries.FLUIDS);
        FLOWING_MILK = RegistryObject.create(new ResourceLocation("flowing_milk"), ForgeRegistries.FLUIDS);
        USE_SELECTORS_PERMISSION = new PermissionNode("forge", "use_entity_selectors", PermissionTypes.BOOLEAN, (player, uuid, contexts) -> {
            return player != null && player.hasPermissions(2);
        }, new PermissionDynamicContextKey[0]);
    }
}
