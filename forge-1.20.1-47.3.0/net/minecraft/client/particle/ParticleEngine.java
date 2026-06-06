//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ParticleEngine implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
    private static final ResourceLocation PARTICLES_ATLAS_INFO = new ResourceLocation("particles");
    private static final int MAX_PARTICLES_PER_LAYER = 16384;
    private static final List<ParticleRenderType> RENDER_ORDER;
    protected ClientLevel level;
    private final Map<ParticleRenderType, Queue<Particle>> particles;
    private final Queue<TrackingEmitter> trackingEmitters;
    private final TextureManager textureManager;
    private final RandomSource random;
    private final Map<ResourceLocation, ParticleProvider<?>> providers;
    private final Queue<Particle> particlesToAdd;
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets;
    private final TextureAtlas textureAtlas;
    private final Object2IntOpenHashMap<ParticleGroup> trackedParticleCounts;

    public ParticleEngine(ClientLevel p_107299_, TextureManager p_107300_) {
        this.particles = Maps.newTreeMap(ForgeHooksClient.makeParticleRenderTypeComparator(RENDER_ORDER));
        this.trackingEmitters = Queues.newArrayDeque();
        this.random = RandomSource.create();
        this.providers = new HashMap();
        this.particlesToAdd = Queues.newArrayDeque();
        this.spriteSets = Maps.newHashMap();
        this.trackedParticleCounts = new Object2IntOpenHashMap();
        this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
        p_107300_.register((ResourceLocation)this.textureAtlas.location(), (AbstractTexture)this.textureAtlas);
        this.level = p_107299_;
        this.textureManager = p_107300_;
        this.registerProviders();
    }

    private void registerProviders() {
        this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, (SpriteParticleRegistration)(SpellParticle.AmbientMobProvider::new));
        this.register(ParticleTypes.ANGRY_VILLAGER, (SpriteParticleRegistration)(HeartParticle.AngryVillagerProvider::new));
        this.register(ParticleTypes.BLOCK_MARKER, (ParticleProvider)(new BlockMarker.Provider()));
        this.register(ParticleTypes.BLOCK, (ParticleProvider)(new TerrainParticle.Provider()));
        this.register(ParticleTypes.BUBBLE, (SpriteParticleRegistration)(BubbleParticle.Provider::new));
        this.register(ParticleTypes.BUBBLE_COLUMN_UP, (SpriteParticleRegistration)(BubbleColumnUpParticle.Provider::new));
        this.register(ParticleTypes.BUBBLE_POP, (SpriteParticleRegistration)(BubblePopParticle.Provider::new));
        this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, (SpriteParticleRegistration)(CampfireSmokeParticle.CosyProvider::new));
        this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, (SpriteParticleRegistration)(CampfireSmokeParticle.SignalProvider::new));
        this.register(ParticleTypes.CLOUD, (SpriteParticleRegistration)(PlayerCloudParticle.Provider::new));
        this.register(ParticleTypes.COMPOSTER, (SpriteParticleRegistration)(SuspendedTownParticle.ComposterFillProvider::new));
        this.register(ParticleTypes.CRIT, (SpriteParticleRegistration)(CritParticle.Provider::new));
        this.register(ParticleTypes.CURRENT_DOWN, (SpriteParticleRegistration)(WaterCurrentDownParticle.Provider::new));
        this.register(ParticleTypes.DAMAGE_INDICATOR, (SpriteParticleRegistration)(CritParticle.DamageIndicatorProvider::new));
        this.register(ParticleTypes.DRAGON_BREATH, (SpriteParticleRegistration)(DragonBreathParticle.Provider::new));
        this.register(ParticleTypes.DOLPHIN, (SpriteParticleRegistration)(SuspendedTownParticle.DolphinSpeedProvider::new));
        this.register(ParticleTypes.DRIPPING_LAVA, (ParticleProvider.Sprite)(DripParticle::createLavaHangParticle));
        this.register(ParticleTypes.FALLING_LAVA, (ParticleProvider.Sprite)(DripParticle::createLavaFallParticle));
        this.register(ParticleTypes.LANDING_LAVA, (ParticleProvider.Sprite)(DripParticle::createLavaLandParticle));
        this.register(ParticleTypes.DRIPPING_WATER, (ParticleProvider.Sprite)(DripParticle::createWaterHangParticle));
        this.register(ParticleTypes.FALLING_WATER, (ParticleProvider.Sprite)(DripParticle::createWaterFallParticle));
        this.register(ParticleTypes.DUST, DustParticle.Provider::new);
        this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
        this.register(ParticleTypes.EFFECT, (SpriteParticleRegistration)(SpellParticle.Provider::new));
        this.register(ParticleTypes.ELDER_GUARDIAN, (ParticleProvider)(new MobAppearanceParticle.Provider()));
        this.register(ParticleTypes.ENCHANTED_HIT, (SpriteParticleRegistration)(CritParticle.MagicProvider::new));
        this.register(ParticleTypes.ENCHANT, (SpriteParticleRegistration)(EnchantmentTableParticle.Provider::new));
        this.register(ParticleTypes.END_ROD, (SpriteParticleRegistration)(EndRodParticle.Provider::new));
        this.register(ParticleTypes.ENTITY_EFFECT, (SpriteParticleRegistration)(SpellParticle.MobProvider::new));
        this.register(ParticleTypes.EXPLOSION_EMITTER, (ParticleProvider)(new HugeExplosionSeedParticle.Provider()));
        this.register(ParticleTypes.EXPLOSION, (SpriteParticleRegistration)(HugeExplosionParticle.Provider::new));
        this.register(ParticleTypes.SONIC_BOOM, (SpriteParticleRegistration)(SonicBoomParticle.Provider::new));
        this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
        this.register(ParticleTypes.FIREWORK, (SpriteParticleRegistration)(FireworkParticles.SparkProvider::new));
        this.register(ParticleTypes.FISHING, (SpriteParticleRegistration)(WakeParticle.Provider::new));
        this.register(ParticleTypes.FLAME, (SpriteParticleRegistration)(FlameParticle.Provider::new));
        this.register(ParticleTypes.SCULK_SOUL, (SpriteParticleRegistration)(SoulParticle.EmissiveProvider::new));
        this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
        this.register(ParticleTypes.SCULK_CHARGE_POP, (SpriteParticleRegistration)(SculkChargePopParticle.Provider::new));
        this.register(ParticleTypes.SOUL, (SpriteParticleRegistration)(SoulParticle.Provider::new));
        this.register(ParticleTypes.SOUL_FIRE_FLAME, (SpriteParticleRegistration)(FlameParticle.Provider::new));
        this.register(ParticleTypes.FLASH, (SpriteParticleRegistration)(FireworkParticles.FlashProvider::new));
        this.register(ParticleTypes.HAPPY_VILLAGER, (SpriteParticleRegistration)(SuspendedTownParticle.HappyVillagerProvider::new));
        this.register(ParticleTypes.HEART, (SpriteParticleRegistration)(HeartParticle.Provider::new));
        this.register(ParticleTypes.INSTANT_EFFECT, (SpriteParticleRegistration)(SpellParticle.InstantProvider::new));
        this.register(ParticleTypes.ITEM, (ParticleProvider)(new BreakingItemParticle.Provider()));
        this.register(ParticleTypes.ITEM_SLIME, (ParticleProvider)(new BreakingItemParticle.SlimeProvider()));
        this.register(ParticleTypes.ITEM_SNOWBALL, (ParticleProvider)(new BreakingItemParticle.SnowballProvider()));
        this.register(ParticleTypes.LARGE_SMOKE, (SpriteParticleRegistration)(LargeSmokeParticle.Provider::new));
        this.register(ParticleTypes.LAVA, (SpriteParticleRegistration)(LavaParticle.Provider::new));
        this.register(ParticleTypes.MYCELIUM, (SpriteParticleRegistration)(SuspendedTownParticle.Provider::new));
        this.register(ParticleTypes.NAUTILUS, (SpriteParticleRegistration)(EnchantmentTableParticle.NautilusProvider::new));
        this.register(ParticleTypes.NOTE, (SpriteParticleRegistration)(NoteParticle.Provider::new));
        this.register(ParticleTypes.POOF, (SpriteParticleRegistration)(ExplodeParticle.Provider::new));
        this.register(ParticleTypes.PORTAL, (SpriteParticleRegistration)(PortalParticle.Provider::new));
        this.register(ParticleTypes.RAIN, (SpriteParticleRegistration)(WaterDropParticle.Provider::new));
        this.register(ParticleTypes.SMOKE, (SpriteParticleRegistration)(SmokeParticle.Provider::new));
        this.register(ParticleTypes.SNEEZE, (SpriteParticleRegistration)(PlayerCloudParticle.SneezeProvider::new));
        this.register(ParticleTypes.SNOWFLAKE, (SpriteParticleRegistration)(SnowflakeParticle.Provider::new));
        this.register(ParticleTypes.SPIT, (SpriteParticleRegistration)(SpitParticle.Provider::new));
        this.register(ParticleTypes.SWEEP_ATTACK, (SpriteParticleRegistration)(AttackSweepParticle.Provider::new));
        this.register(ParticleTypes.TOTEM_OF_UNDYING, (SpriteParticleRegistration)(TotemParticle.Provider::new));
        this.register(ParticleTypes.SQUID_INK, (SpriteParticleRegistration)(SquidInkParticle.Provider::new));
        this.register(ParticleTypes.UNDERWATER, (SpriteParticleRegistration)(SuspendedParticle.UnderwaterProvider::new));
        this.register(ParticleTypes.SPLASH, (SpriteParticleRegistration)(SplashParticle.Provider::new));
        this.register(ParticleTypes.WITCH, (SpriteParticleRegistration)(SpellParticle.WitchProvider::new));
        this.register(ParticleTypes.DRIPPING_HONEY, (ParticleProvider.Sprite)(DripParticle::createHoneyHangParticle));
        this.register(ParticleTypes.FALLING_HONEY, (ParticleProvider.Sprite)(DripParticle::createHoneyFallParticle));
        this.register(ParticleTypes.LANDING_HONEY, (ParticleProvider.Sprite)(DripParticle::createHoneyLandParticle));
        this.register(ParticleTypes.FALLING_NECTAR, (ParticleProvider.Sprite)(DripParticle::createNectarFallParticle));
        this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, (ParticleProvider.Sprite)(DripParticle::createSporeBlossomFallParticle));
        this.register(ParticleTypes.SPORE_BLOSSOM_AIR, (SpriteParticleRegistration)(SuspendedParticle.SporeBlossomAirProvider::new));
        this.register(ParticleTypes.ASH, (SpriteParticleRegistration)(AshParticle.Provider::new));
        this.register(ParticleTypes.CRIMSON_SPORE, (SpriteParticleRegistration)(SuspendedParticle.CrimsonSporeProvider::new));
        this.register(ParticleTypes.WARPED_SPORE, (SpriteParticleRegistration)(SuspendedParticle.WarpedSporeProvider::new));
        this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (ParticleProvider.Sprite)(DripParticle::createObsidianTearHangParticle));
        this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, (ParticleProvider.Sprite)(DripParticle::createObsidianTearFallParticle));
        this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, (ParticleProvider.Sprite)(DripParticle::createObsidianTearLandParticle));
        this.register(ParticleTypes.REVERSE_PORTAL, (SpriteParticleRegistration)(ReversePortalParticle.ReversePortalProvider::new));
        this.register(ParticleTypes.WHITE_ASH, (SpriteParticleRegistration)(WhiteAshParticle.Provider::new));
        this.register(ParticleTypes.SMALL_FLAME, (SpriteParticleRegistration)(FlameParticle.SmallFlameProvider::new));
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, (ParticleProvider.Sprite)(DripParticle::createDripstoneWaterHangParticle));
        this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, (ParticleProvider.Sprite)(DripParticle::createDripstoneWaterFallParticle));
        this.register(ParticleTypes.CHERRY_LEAVES, (SpriteParticleRegistration)((p_277215_) -> {
            return (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> {
                return new CherryParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_);
            };
        }));
        this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, (ParticleProvider.Sprite)(DripParticle::createDripstoneLavaHangParticle));
        this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, (ParticleProvider.Sprite)(DripParticle::createDripstoneLavaFallParticle));
        this.register(ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
        this.register(ParticleTypes.GLOW_SQUID_INK, (SpriteParticleRegistration)(SquidInkParticle.GlowInkProvider::new));
        this.register(ParticleTypes.GLOW, (SpriteParticleRegistration)(GlowParticle.GlowSquidProvider::new));
        this.register(ParticleTypes.WAX_ON, (SpriteParticleRegistration)(GlowParticle.WaxOnProvider::new));
        this.register(ParticleTypes.WAX_OFF, (SpriteParticleRegistration)(GlowParticle.WaxOffProvider::new));
        this.register(ParticleTypes.ELECTRIC_SPARK, (SpriteParticleRegistration)(GlowParticle.ElectricSparkProvider::new));
        this.register(ParticleTypes.SCRAPE, (SpriteParticleRegistration)(GlowParticle.ScrapeProvider::new));
        this.register(ParticleTypes.SHRIEK, ShriekParticle.Provider::new);
        this.register(ParticleTypes.EGG_CRACK, (SpriteParticleRegistration)(SuspendedTownParticle.EggCrackProvider::new));
    }

    /** @deprecated */
    @Deprecated
    public <T extends ParticleOptions> void register(ParticleType<T> p_107382_, ParticleProvider<T> p_107383_) {
        this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getKey(p_107382_), p_107383_);
    }

    /** @deprecated */
    @Deprecated
    public <T extends ParticleOptions> void register(ParticleType<T> p_273423_, ParticleProvider.Sprite<T> p_273134_) {
        this.register(p_273423_, (p_272320_) -> {
            return (p_272323_, p_272324_, p_272325_, p_272326_, p_272327_, p_272328_, p_272329_, p_272330_) -> {
                TextureSheetParticle texturesheetparticle = p_273134_.createParticle(p_272323_, p_272324_, p_272325_, p_272326_, p_272327_, p_272328_, p_272329_, p_272330_);
                if (texturesheetparticle != null) {
                    texturesheetparticle.pickSprite(p_272320_);
                }

                return texturesheetparticle;
            };
        });
    }

    /** @deprecated */
    @Deprecated
    public <T extends ParticleOptions> void register(ParticleType<T> p_107379_, SpriteParticleRegistration<T> p_107380_) {
        MutableSpriteSet particleengine$mutablespriteset = new MutableSpriteSet();
        this.spriteSets.put(BuiltInRegistries.PARTICLE_TYPE.getKey(p_107379_), particleengine$mutablespriteset);
        this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getKey(p_107379_), p_107380_.create(particleengine$mutablespriteset));
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_107305_, ResourceManager p_107306_, ProfilerFiller p_107307_, ProfilerFiller p_107308_, Executor p_107309_, Executor p_107310_) {
        CompletableFuture<List<ParticleDefinition>> completablefuture = CompletableFuture.supplyAsync(() -> {
            return PARTICLE_LISTER.listMatchingResources(p_107306_);
        }, p_107309_).thenCompose((p_247914_) -> {
            List<CompletableFuture<ParticleDefinition>> list = new ArrayList(p_247914_.size());
            p_247914_.forEach((p_247903_, p_247904_) -> {
                ResourceLocation resourcelocation = PARTICLE_LISTER.fileToId(p_247903_);
                list.add(CompletableFuture.supplyAsync(() -> {
                    @OnlyIn(Dist.CLIENT)
                    record ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
                        ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
                            this.id = id;
                            this.sprites = sprites;
                        }

                        public ResourceLocation id() {
                            return this.id;
                        }

                        public Optional<List<ResourceLocation>> sprites() {
                            return this.sprites;
                        }
                    }

                    return new ParticleDefinition(resourcelocation, this.loadParticleDescription(resourcelocation, p_247904_));
                }, p_107309_));
            });
            return Util.sequence(list);
        });
        CompletableFuture<SpriteLoader.Preparations> completablefuture1 = SpriteLoader.create(this.textureAtlas).loadAndStitch(p_107306_, PARTICLES_ATLAS_INFO, 0, p_107309_).thenCompose(SpriteLoader.Preparations::waitForUpload);
        CompletableFuture var10000 = CompletableFuture.allOf(completablefuture1, completablefuture);
        Objects.requireNonNull(p_107305_);
        return var10000.thenCompose(p_107305_::wait).thenAcceptAsync((p_247900_) -> {
            this.clearParticles();
            p_107308_.startTick();
            p_107308_.push("upload");
            SpriteLoader.Preparations spriteloader$preparations = (SpriteLoader.Preparations)completablefuture1.join();
            this.textureAtlas.upload(spriteloader$preparations);
            p_107308_.popPush("bindSpriteSets");
            Set<ResourceLocation> set = new HashSet();
            TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
            ((List)completablefuture.join()).forEach((p_247911_) -> {
                Optional<List<ResourceLocation>> optional = p_247911_.sprites();
                if (!optional.isEmpty()) {
                    List<TextureAtlasSprite> list = new ArrayList();
                    Iterator var7 = ((List)optional.get()).iterator();

                    while(var7.hasNext()) {
                        ResourceLocation resourcelocation = (ResourceLocation)var7.next();
                        TextureAtlasSprite textureatlassprite1 = (TextureAtlasSprite)spriteloader$preparations.regions().get(resourcelocation);
                        if (textureatlassprite1 == null) {
                            set.add(resourcelocation);
                            list.add(textureatlassprite);
                        } else {
                            list.add(textureatlassprite1);
                        }
                    }

                    if (list.isEmpty()) {
                        list.add(textureatlassprite);
                    }

                    ((MutableSpriteSet)this.spriteSets.get(p_247911_.id())).rebind(list);
                }

            });
            if (!set.isEmpty()) {
                LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
            }

            p_107308_.pop();
            p_107308_.endTick();
        }, p_107310_);
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    private Optional<List<ResourceLocation>> loadParticleDescription(ResourceLocation p_250648_, Resource p_248793_) {
        if (!this.spriteSets.containsKey(p_250648_)) {
            LOGGER.debug("Redundant texture list for particle: {}", p_250648_);
            return Optional.empty();
        } else {
            try {
                Reader reader = p_248793_.openAsReader();

                Optional var5;
                try {
                    ParticleDescription particledescription = ParticleDescription.fromJson(GsonHelper.parse((Reader)reader));
                    var5 = Optional.of(particledescription.getTextures());
                } catch (Throwable var7) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var6) {
                            var7.addSuppressed(var6);
                        }
                    }

                    throw var7;
                }

                if (reader != null) {
                    reader.close();
                }

                return var5;
            } catch (IOException var8) {
                IOException ioexception = var8;
                throw new IllegalStateException("Failed to load description for particle " + p_250648_, ioexception);
            }
        }
    }

    public void createTrackingEmitter(Entity p_107330_, ParticleOptions p_107331_) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, p_107330_, p_107331_));
    }

    public void createTrackingEmitter(Entity p_107333_, ParticleOptions p_107334_, int p_107335_) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, p_107333_, p_107334_, p_107335_));
    }

    @Nullable
    public Particle createParticle(ParticleOptions p_107371_, double p_107372_, double p_107373_, double p_107374_, double p_107375_, double p_107376_, double p_107377_) {
        Particle particle = this.makeParticle(p_107371_, p_107372_, p_107373_, p_107374_, p_107375_, p_107376_, p_107377_);
        if (particle != null) {
            this.add(particle);
            return particle;
        } else {
            return null;
        }
    }

    @Nullable
    private <T extends ParticleOptions> Particle makeParticle(T p_107396_, double p_107397_, double p_107398_, double p_107399_, double p_107400_, double p_107401_, double p_107402_) {
        ParticleProvider<T> particleprovider = (ParticleProvider)this.providers.get(BuiltInRegistries.PARTICLE_TYPE.getKey(p_107396_.getType()));
        return particleprovider == null ? null : particleprovider.createParticle(p_107396_, this.level, p_107397_, p_107398_, p_107399_, p_107400_, p_107401_, p_107402_);
    }

    public void add(Particle p_107345_) {
        Optional<ParticleGroup> optional = p_107345_.getParticleGroup();
        if (optional.isPresent()) {
            if (this.hasSpaceInParticleLimit((ParticleGroup)optional.get())) {
                this.particlesToAdd.add(p_107345_);
                this.updateCount((ParticleGroup)optional.get(), 1);
            }
        } else {
            this.particlesToAdd.add(p_107345_);
        }

    }

    public void tick() {
        this.particles.forEach((p_288249_, p_288250_) -> {
            this.level.getProfiler().push(p_288249_.toString());
            this.tickParticleList(p_288250_);
            this.level.getProfiler().pop();
        });
        if (!this.trackingEmitters.isEmpty()) {
            List<TrackingEmitter> list = Lists.newArrayList();
            Iterator var2 = this.trackingEmitters.iterator();

            while(var2.hasNext()) {
                TrackingEmitter trackingemitter = (TrackingEmitter)var2.next();
                trackingemitter.tick();
                if (!trackingemitter.isAlive()) {
                    list.add(trackingemitter);
                }
            }

            this.trackingEmitters.removeAll(list);
        }

        Particle particle;
        if (!this.particlesToAdd.isEmpty()) {
            while((particle = (Particle)this.particlesToAdd.poll()) != null) {
                ((Queue)this.particles.computeIfAbsent(particle.getRenderType(), (p_107347_) -> {
                    return EvictingQueue.create(16384);
                })).add(particle);
            }
        }

    }

    private void tickParticleList(Collection<Particle> p_107385_) {
        if (!p_107385_.isEmpty()) {
            Iterator<Particle> iterator = p_107385_.iterator();

            while(iterator.hasNext()) {
                Particle particle = (Particle)iterator.next();
                this.tickParticle(particle);
                if (!particle.isAlive()) {
                    particle.getParticleGroup().ifPresent((p_172289_) -> {
                        this.updateCount(p_172289_, -1);
                    });
                    iterator.remove();
                }
            }
        }

    }

    private void updateCount(ParticleGroup p_172282_, int p_172283_) {
        this.trackedParticleCounts.addTo(p_172282_, p_172283_);
    }

    private void tickParticle(Particle p_107394_) {
        try {
            p_107394_.tick();
        } catch (Throwable var5) {
            Throwable throwable = var5;
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking Particle");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
            Objects.requireNonNull(p_107394_);
            crashreportcategory.setDetail("Particle", p_107394_::toString);
            ParticleRenderType var10002 = p_107394_.getRenderType();
            Objects.requireNonNull(var10002);
            crashreportcategory.setDetail("Particle Type", var10002::toString);
            throw new ReportedException(crashreport);
        }
    }

    /** @deprecated */
    @Deprecated
    public void render(PoseStack p_107337_, MultiBufferSource.BufferSource p_107338_, LightTexture p_107339_, Camera p_107340_, float p_107341_) {
        this.render(p_107337_, p_107338_, p_107339_, p_107340_, p_107341_, (Frustum)null);
    }

    public void render(PoseStack p_107337_, MultiBufferSource.BufferSource p_107338_, LightTexture p_107339_, Camera p_107340_, float p_107341_, @Nullable Frustum clippingHelper) {
        p_107339_.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.activeTexture(33986);
        RenderSystem.activeTexture(33984);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(p_107337_.last().pose());
        RenderSystem.applyModelViewMatrix();
        Iterator var8 = this.particles.keySet().iterator();

        label49:
        while(true) {
            ParticleRenderType particlerendertype;
            Iterable iterable;
            do {
                do {
                    if (!var8.hasNext()) {
                        posestack.popPose();
                        RenderSystem.applyModelViewMatrix();
                        RenderSystem.depthMask(true);
                        RenderSystem.disableBlend();
                        p_107339_.turnOffLightLayer();
                        return;
                    }

                    particlerendertype = (ParticleRenderType)var8.next();
                } while(particlerendertype == ParticleRenderType.NO_RENDER);

                iterable = (Iterable)this.particles.get(particlerendertype);
            } while(iterable == null);

            RenderSystem.setShader(GameRenderer::getParticleShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            particlerendertype.begin(bufferbuilder, this.textureManager);
            Iterator var13 = iterable.iterator();

            while(true) {
                Particle particle;
                do {
                    if (!var13.hasNext()) {
                        particlerendertype.end(tesselator);
                        continue label49;
                    }

                    particle = (Particle)var13.next();
                } while(clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox()));

                try {
                    particle.render(bufferbuilder, p_107340_, p_107341_);
                } catch (Throwable var18) {
                    Throwable throwable = var18;
                    CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                    CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                    Objects.requireNonNull(particle);
                    crashreportcategory.setDetail("Particle", particle::toString);
                    Objects.requireNonNull(particlerendertype);
                    crashreportcategory.setDetail("Particle Type", particlerendertype::toString);
                    throw new ReportedException(crashreport);
                }
            }
        }
    }

    public void setLevel(@Nullable ClientLevel p_107343_) {
        this.level = p_107343_;
        this.clearParticles();
        this.trackingEmitters.clear();
    }

    public void destroy(BlockPos p_107356_, BlockState p_107357_) {
        if (!p_107357_.isAir() && !IClientBlockExtensions.of(p_107357_).addDestroyEffects(p_107357_, this.level, p_107356_, this)) {
            VoxelShape voxelshape = p_107357_.getShape(this.level, p_107356_);
            double d0 = 0.25;
            voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
                double d1 = Math.min(1.0, p_172276_ - p_172273_);
                double d2 = Math.min(1.0, p_172277_ - p_172274_);
                double d3 = Math.min(1.0, p_172278_ - p_172275_);
                int i = Math.max(2, Mth.ceil(d1 / 0.25));
                int j = Math.max(2, Mth.ceil(d2 / 0.25));
                int k = Math.max(2, Mth.ceil(d3 / 0.25));

                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 < j; ++i1) {
                        for(int j1 = 0; j1 < k; ++j1) {
                            double d4 = ((double)l + 0.5) / (double)i;
                            double d5 = ((double)i1 + 0.5) / (double)j;
                            double d6 = ((double)j1 + 0.5) / (double)k;
                            double d7 = d4 * d1 + p_172273_;
                            double d8 = d5 * d2 + p_172274_;
                            double d9 = d6 * d3 + p_172275_;
                            this.add((new TerrainParticle(this.level, (double)p_107356_.getX() + d7, (double)p_107356_.getY() + d8, (double)p_107356_.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5, p_107357_, p_107356_)).updateSprite(p_107357_, p_107356_));
                        }
                    }
                }

            });
        }

    }

    public void crack(BlockPos p_107368_, Direction p_107369_) {
        BlockState blockstate = this.level.getBlockState(p_107368_);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            int i = p_107368_.getX();
            int j = p_107368_.getY();
            int k = p_107368_.getZ();
            float f = 0.1F;
            AABB aabb = blockstate.getShape(this.level, p_107368_).bounds();
            double d0 = (double)i + this.random.nextDouble() * (aabb.maxX - aabb.minX - 0.20000000298023224) + 0.10000000149011612 + aabb.minX;
            double d1 = (double)j + this.random.nextDouble() * (aabb.maxY - aabb.minY - 0.20000000298023224) + 0.10000000149011612 + aabb.minY;
            double d2 = (double)k + this.random.nextDouble() * (aabb.maxZ - aabb.minZ - 0.20000000298023224) + 0.10000000149011612 + aabb.minZ;
            if (p_107369_ == Direction.DOWN) {
                d1 = (double)j + aabb.minY - 0.10000000149011612;
            }

            if (p_107369_ == Direction.UP) {
                d1 = (double)j + aabb.maxY + 0.10000000149011612;
            }

            if (p_107369_ == Direction.NORTH) {
                d2 = (double)k + aabb.minZ - 0.10000000149011612;
            }

            if (p_107369_ == Direction.SOUTH) {
                d2 = (double)k + aabb.maxZ + 0.10000000149011612;
            }

            if (p_107369_ == Direction.WEST) {
                d0 = (double)i + aabb.minX - 0.10000000149011612;
            }

            if (p_107369_ == Direction.EAST) {
                d0 = (double)i + aabb.maxX + 0.10000000149011612;
            }

            this.add((new TerrainParticle(this.level, d0, d1, d2, 0.0, 0.0, 0.0, blockstate, p_107368_)).updateSprite(blockstate, p_107368_).setPower(0.2F).scale(0.6F));
        }

    }

    public String countParticles() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    public void addBlockHitEffects(BlockPos pos, BlockHitResult target) {
        BlockState state = this.level.getBlockState(pos);
        if (!IClientBlockExtensions.of(state).addHitEffects(state, this.level, target, this)) {
            this.crack(pos, target.getDirection());
        }

    }

    private boolean hasSpaceInParticleLimit(ParticleGroup p_172280_) {
        return this.trackedParticleCounts.getInt(p_172280_) < p_172280_.getLimit();
    }

    private void clearParticles() {
        this.particles.clear();
        this.particlesToAdd.clear();
        this.trackingEmitters.clear();
        this.trackedParticleCounts.clear();
    }

    static {
        RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface SpriteParticleRegistration<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet var1);
    }

    @OnlyIn(Dist.CLIENT)
    static class MutableSpriteSet implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        MutableSpriteSet() {
        }

        public TextureAtlasSprite get(int p_107413_, int p_107414_) {
            return (TextureAtlasSprite)this.sprites.get(p_107413_ * (this.sprites.size() - 1) / p_107414_);
        }

        public TextureAtlasSprite get(RandomSource p_233889_) {
            return (TextureAtlasSprite)this.sprites.get(p_233889_.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> p_107416_) {
            this.sprites = ImmutableList.copyOf(p_107416_);
        }
    }
}
