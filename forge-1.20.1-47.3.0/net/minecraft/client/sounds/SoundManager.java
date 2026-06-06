//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound.Type;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.MultipliedFloats;
import net.minecraft.util.valueproviders.SampledFloat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SoundManager extends SimplePreparableReloadListener<Preparations> {
    public static final Sound EMPTY_SOUND;
    public static final ResourceLocation INTENTIONALLY_EMPTY_SOUND_LOCATION;
    public static final WeighedSoundEvents INTENTIONALLY_EMPTY_SOUND_EVENT;
    public static final Sound INTENTIONALLY_EMPTY_SOUND;
    static final Logger LOGGER;
    private static final String SOUNDS_PATH = "sounds.json";
    private static final Gson GSON;
    private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE;
    private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
    private final SoundEngine soundEngine;
    private final Map<ResourceLocation, Resource> soundCache = new HashMap();

    public SoundManager(Options p_250027_) {
        this.soundEngine = new SoundEngine(this, p_250027_, ResourceProvider.fromMap(this.soundCache));
    }

    protected Preparations prepare(ResourceManager p_120356_, ProfilerFiller p_120357_) {
        Preparations $$2 = new Preparations();
        p_120357_.startTick();
        p_120357_.push("list");
        $$2.listResources(p_120356_);
        p_120357_.pop();

        for(Iterator var4 = p_120356_.getNamespaces().iterator(); var4.hasNext(); p_120357_.pop()) {
            String $$3 = (String)var4.next();
            p_120357_.push($$3);

            try {
                List<Resource> $$4 = p_120356_.getResourceStack(new ResourceLocation($$3, "sounds.json"));

                for(Iterator var7 = $$4.iterator(); var7.hasNext(); p_120357_.pop()) {
                    Resource $$5 = (Resource)var7.next();
                    p_120357_.push($$5.sourcePackId());

                    try {
                        Reader $$6 = $$5.openAsReader();

                        try {
                            p_120357_.push("parse");
                            Map<String, SoundEventRegistration> $$7 = (Map)GsonHelper.fromJson(GSON, (Reader)$$6, (TypeToken)SOUND_EVENT_REGISTRATION_TYPE);
                            p_120357_.popPush("register");
                            Iterator var11 = $$7.entrySet().iterator();

                            while(true) {
                                if (!var11.hasNext()) {
                                    p_120357_.pop();
                                    break;
                                }

                                Map.Entry<String, SoundEventRegistration> $$8 = (Map.Entry)var11.next();
                                $$2.handleRegistration(new ResourceLocation($$3, (String)$$8.getKey()), (SoundEventRegistration)$$8.getValue());
                            }
                        } catch (Throwable var14) {
                            if ($$6 != null) {
                                try {
                                    $$6.close();
                                } catch (Throwable var13) {
                                    var14.addSuppressed(var13);
                                }
                            }

                            throw var14;
                        }

                        if ($$6 != null) {
                            $$6.close();
                        }
                    } catch (RuntimeException var15) {
                        RuntimeException $$9 = var15;
                        LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{"sounds.json", $$5.sourcePackId(), $$9});
                    }
                }
            } catch (IOException var16) {
            }
        }

        p_120357_.endTick();
        return $$2;
    }

    protected void apply(Preparations p_120377_, ResourceManager p_120378_, ProfilerFiller p_120379_) {
        p_120377_.apply(this.registry, this.soundCache, this.soundEngine);
        Iterator var4;
        ResourceLocation $$5;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            var4 = this.registry.keySet().iterator();

            while(var4.hasNext()) {
                $$5 = (ResourceLocation)var4.next();
                WeighedSoundEvents $$4 = (WeighedSoundEvents)this.registry.get($$5);
                if (!ComponentUtils.isTranslationResolvable($$4.getSubtitle()) && BuiltInRegistries.SOUND_EVENT.containsKey($$5)) {
                    LOGGER.error("Missing subtitle {} for sound event: {}", $$4.getSubtitle(), $$5);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            var4 = this.registry.keySet().iterator();

            while(var4.hasNext()) {
                $$5 = (ResourceLocation)var4.next();
                if (!BuiltInRegistries.SOUND_EVENT.containsKey($$5)) {
                    LOGGER.debug("Not having sound event for: {}", $$5);
                }
            }
        }

        this.soundEngine.reload();
    }

    public List<String> getAvailableSoundDevices() {
        return this.soundEngine.getAvailableSoundDevices();
    }

    static boolean validateSoundResource(Sound p_250396_, ResourceLocation p_250879_, ResourceProvider p_248737_) {
        ResourceLocation $$3 = p_250396_.getPath();
        if (p_248737_.getResource($$3).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", $$3, p_250879_);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    public WeighedSoundEvents getSoundEvent(ResourceLocation p_120385_) {
        return (WeighedSoundEvents)this.registry.get(p_120385_);
    }

    public Collection<ResourceLocation> getAvailableSounds() {
        return this.registry.keySet();
    }

    public void queueTickingSound(TickableSoundInstance p_120373_) {
        this.soundEngine.queueTickingSound(p_120373_);
    }

    public void play(SoundInstance p_120368_) {
        this.soundEngine.play(p_120368_);
    }

    public void playDelayed(SoundInstance p_120370_, int p_120371_) {
        this.soundEngine.playDelayed(p_120370_, p_120371_);
    }

    public void updateSource(Camera p_120362_) {
        this.soundEngine.updateSource(p_120362_);
    }

    public void pause() {
        this.soundEngine.pause();
    }

    public void stop() {
        this.soundEngine.stopAll();
    }

    public void destroy() {
        this.soundEngine.destroy();
    }

    public void tick(boolean p_120390_) {
        this.soundEngine.tick(p_120390_);
    }

    public void resume() {
        this.soundEngine.resume();
    }

    public void updateSourceVolume(SoundSource p_120359_, float p_120360_) {
        if (p_120359_ == SoundSource.MASTER && p_120360_ <= 0.0F) {
            this.stop();
        }

        this.soundEngine.updateCategoryVolume(p_120359_, p_120360_);
    }

    public void stop(SoundInstance p_120400_) {
        this.soundEngine.stop(p_120400_);
    }

    public boolean isActive(SoundInstance p_120404_) {
        return this.soundEngine.isActive(p_120404_);
    }

    public void addListener(SoundEventListener p_120375_) {
        this.soundEngine.addEventListener(p_120375_);
    }

    public void removeListener(SoundEventListener p_120402_) {
        this.soundEngine.removeEventListener(p_120402_);
    }

    public void stop(@Nullable ResourceLocation p_120387_, @Nullable SoundSource p_120388_) {
        this.soundEngine.stop(p_120387_, p_120388_);
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    public void reload() {
        this.soundEngine.reload();
    }

    static {
        EMPTY_SOUND = new Sound("minecraft:empty", ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Type.FILE, false, false, 16);
        INTENTIONALLY_EMPTY_SOUND_LOCATION = new ResourceLocation("minecraft", "intentionally_empty");
        INTENTIONALLY_EMPTY_SOUND_EVENT = new WeighedSoundEvents(INTENTIONALLY_EMPTY_SOUND_LOCATION, (String)null);
        INTENTIONALLY_EMPTY_SOUND = new Sound(INTENTIONALLY_EMPTY_SOUND_LOCATION.toString(), ConstantFloat.of(1.0F), ConstantFloat.of(1.0F), 1, Type.FILE, false, false, 16);
        LOGGER = LogUtils.getLogger();
        GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, new SoundEventRegistrationSerializer()).create();
        SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>() {
        };
    }

    @OnlyIn(Dist.CLIENT)
    protected static class Preparations {
        final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
        private Map<ResourceLocation, Resource> soundCache = Map.of();

        protected Preparations() {
        }

        void listResources(ResourceManager p_249271_) {
            this.soundCache = Sound.SOUND_LISTER.listMatchingResources(p_249271_);
        }

        void handleRegistration(ResourceLocation p_250806_, SoundEventRegistration p_249632_) {
            WeighedSoundEvents $$2 = (WeighedSoundEvents)this.registry.get(p_250806_);
            boolean $$3 = $$2 == null;
            if ($$3 || p_249632_.isReplace()) {
                if (!$$3) {
                    SoundManager.LOGGER.debug("Replaced sound event location {}", p_250806_);
                }

                $$2 = new WeighedSoundEvents(p_250806_, p_249632_.getSubtitle());
                this.registry.put(p_250806_, $$2);
            }

            ResourceProvider $$4 = ResourceProvider.fromMap(this.soundCache);
            Iterator var6 = p_249632_.getSounds().iterator();

            while(var6.hasNext()) {
                final Sound $$5 = (Sound)var6.next();
                final ResourceLocation $$6 = $$5.getLocation();
                Object $$9;
                switch ($$5.getType()) {
                    case FILE:
                        if (!SoundManager.validateSoundResource($$5, p_250806_, $$4)) {
                            continue;
                        }

                        $$9 = $$5;
                        break;
                    case SOUND_EVENT:
                        $$9 = new Weighted<Sound>() {
                            public int getWeight() {
                                WeighedSoundEvents $$0 = (WeighedSoundEvents)Preparations.this.registry.get($$6);
                                return $$0 == null ? 0 : $$0.getWeight();
                            }

                            public Sound getSound(RandomSource p_235261_) {
                                WeighedSoundEvents $$1 = (WeighedSoundEvents)Preparations.this.registry.get($$6);
                                if ($$1 == null) {
                                    return SoundManager.EMPTY_SOUND;
                                } else {
                                    Sound $$2 = $$1.getSound(p_235261_);
                                    return new Sound($$2.getLocation().toString(), new MultipliedFloats(new SampledFloat[]{$$2.getVolume(), $$5.getVolume()}), new MultipliedFloats(new SampledFloat[]{$$2.getPitch(), $$5.getPitch()}), $$5.getWeight(), Type.FILE, $$2.shouldStream() || $$5.shouldStream(), $$2.shouldPreload(), $$2.getAttenuationDistance());
                                }
                            }

                            public void preloadIfRequired(SoundEngine p_120438_) {
                                WeighedSoundEvents $$1 = (WeighedSoundEvents)Preparations.this.registry.get($$6);
                                if ($$1 != null) {
                                    $$1.preloadIfRequired(p_120438_);
                                }
                            }
                        };
                        break;
                    default:
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + $$5.getType());
                }

                $$2.addSound((Weighted)$$9);
            }

        }

        public void apply(Map<ResourceLocation, WeighedSoundEvents> p_251229_, Map<ResourceLocation, Resource> p_251045_, SoundEngine p_250302_) {
            p_251229_.clear();
            p_251045_.clear();
            p_251045_.putAll(this.soundCache);
            Iterator var4 = this.registry.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<ResourceLocation, WeighedSoundEvents> $$3 = (Map.Entry)var4.next();
                p_251229_.put((ResourceLocation)$$3.getKey(), (WeighedSoundEvents)$$3.getValue());
                ((WeighedSoundEvents)$$3.getValue()).preloadIfRequired(p_250302_);
            }

        }
    }
}
