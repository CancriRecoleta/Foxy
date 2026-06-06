//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.BrainBuilder;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.ModMismatchEvent;
import net.minecraftforge.event.RegisterStructureConversionsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingMakeBrainEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.ForgeRegistry.Snapshot;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ForgeHooks {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker FORGEHOOKS = MarkerManager.getMarker("FORGEHOOKS");
    private static final Marker WORLDPERSISTENCE = MarkerManager.getMarker("WP");
    static final Pattern URL_PATTERN = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"§ \n]|$))", 2);
    private static ThreadLocal<Player> craftingPlayer = new ThreadLocal();
    private static final ThreadLocal<Deque<LootTableContext>> lootContext = new ThreadLocal();
    private static final Map<Holder.Reference<Item>, Integer> VANILLA_BURNS = new HashMap();
    private static final Set<String> VANILLA_DIMS = Sets.newHashSet(new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"});
    private static final String DIMENSIONS_KEY = "dimensions";
    private static final String SEED_KEY = "seed";
    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES = new HashMap();
    private static final Lazy<Map<String, StructuresBecomeConfiguredFix.Conversion>> FORGE_CONVERSION_MAP = Lazy.concurrentOf(() -> {
        Map<String, StructuresBecomeConfiguredFix.Conversion> map = new HashMap();
        MinecraftForge.EVENT_BUS.post(new RegisterStructureConversionsEvent(map));
        return ImmutableMap.copyOf(map);
    });

    public ForgeHooks() {
    }

    public static boolean canContinueUsing(@NotNull ItemStack from, @NotNull ItemStack to) {
        return !from.isEmpty() && !to.isEmpty() ? from.getItem().canContinueUsing(from, to) : false;
    }

    public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
        return !state.requiresCorrectToolForDrops() ? ForgeEventFactory.doPlayerHarvestCheck(player, state, true) : player.hasCorrectToolForDrops(state);
    }

    public static boolean onItemStackedOn(ItemStack carriedItem, ItemStack stackedOnItem, Slot slot, ClickAction action, Player player, SlotAccess carriedSlotAccess) {
        return MinecraftForge.EVENT_BUS.post(new ItemStackedOnOtherEvent(carriedItem, stackedOnItem, slot, action, player, carriedSlotAccess));
    }

    public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
        MinecraftForge.EVENT_BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
    }

    public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
        LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static Brain<?> onLivingMakeBrain(LivingEntity entity, Brain<?> originalBrain, Dynamic<?> dynamic) {
        BrainBuilder<?> brainBuilder = originalBrain.createBuilder();
        LivingMakeBrainEvent event = new LivingMakeBrainEvent(entity, brainBuilder);
        MinecraftForge.EVENT_BUS.post(event);
        return brainBuilder.makeBrain(dynamic);
    }

    public static boolean onLivingTick(LivingEntity entity) {
        return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingTickEvent(entity));
    }

    public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
        return entity instanceof Player || !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
        return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ) {
        LivingKnockBackEvent event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onLivingUseTotem(LivingEntity entity, DamageSource damageSource, ItemStack totem, InteractionHand hand) {
        return !MinecraftForge.EVENT_BUS.post(new LivingUseTotemEvent(entity, damageSource, totem, hand));
    }

    public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.getAmount();
    }

    public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
        LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.getAmount();
    }

    public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
    }

    public static @Nullable float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
        LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
        return MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()};
    }

    public static int getLootingLevel(Entity target, @Nullable Entity killer, @Nullable DamageSource cause) {
        int looting = 0;
        if (killer instanceof LivingEntity) {
            looting = EnchantmentHelper.getMobLooting((LivingEntity)killer);
        }

        if (target instanceof LivingEntity) {
            looting = getLootingLevel((LivingEntity)target, cause, looting);
        }

        return looting;
    }

    public static int getLootingLevel(LivingEntity target, @Nullable DamageSource cause, int level) {
        LootingLevelEvent event = new LootingLevelEvent(target, cause, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getLootingLevel();
    }

    public static double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier) {
        LivingEvent.LivingVisibilityEvent event = new LivingEvent.LivingVisibilityEvent(entity, lookingEntity, originalMultiplier);
        MinecraftForge.EVENT_BUS.post(event);
        return Math.max(0.0, event.getVisibilityModifier());
    }

    public static Optional<BlockPos> isLivingOnLadder(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
        boolean isSpectator = entity instanceof Player && entity.isSpectator();
        if (isSpectator) {
            return Optional.empty();
        } else if (!(Boolean)ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
            return state.isLadder(level, pos, entity) ? Optional.of(pos) : Optional.empty();
        } else {
            AABB bb = entity.getBoundingBox();
            int mX = Mth.floor(bb.minX);
            int mY = Mth.floor(bb.minY);
            int mZ = Mth.floor(bb.minZ);

            for(int y2 = mY; (double)y2 < bb.maxY; ++y2) {
                for(int x2 = mX; (double)x2 < bb.maxX; ++x2) {
                    for(int z2 = mZ; (double)z2 < bb.maxZ; ++z2) {
                        BlockPos tmp = new BlockPos(x2, y2, z2);
                        state = level.getBlockState(tmp);
                        if (state.isLadder(level, tmp, entity)) {
                            return Optional.of(tmp);
                        }
                    }
                }
            }

            return Optional.empty();
        }
    }

    public static void onLivingJump(LivingEntity entity) {
        MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingJumpEvent(entity));
    }

    public static @Nullable ItemEntity onPlayerTossEvent(@NotNull Player player, @NotNull ItemStack item, boolean includeName) {
        player.captureDrops(Lists.newArrayList());
        ItemEntity ret = player.drop(item, false, includeName);
        player.captureDrops((Collection)null);
        if (ret == null) {
            return null;
        } else {
            ItemTossEvent event = new ItemTossEvent(ret, player);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return null;
            } else {
                if (!player.level().isClientSide) {
                    player.getCommandSenderWorld().addFreshEntity(event.getEntity());
                }

                return event.getEntity();
            }
        }
    }

    public static boolean onVanillaGameEvent(Level level, GameEvent vanillaEvent, Vec3 pos, GameEvent.Context context) {
        return !MinecraftForge.EVENT_BUS.post(new VanillaGameEvent(level, vanillaEvent, pos, context));
    }

    private static String getRawText(Component message) {
        ComponentContents var2 = message.getContents();
        String var10000;
        if (var2 instanceof LiteralContents literalContents) {
            var10000 = literalContents.text();
        } else {
            var10000 = "";
        }

        return var10000;
    }

    public static @Nullable Component onServerChatSubmittedEvent(ServerPlayer player, String plain, Component decorated) {
        ServerChatEvent event = new ServerChatEvent(player, plain, decorated);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    public static @NotNull ChatDecorator getServerChatSubmittedDecorator() {
        return (sender, message) -> {
            return CompletableFuture.supplyAsync(() -> {
                return sender == null ? message : onServerChatSubmittedEvent(sender, getRawText(message), message);
            });
        };
    }

    public static Component newChatWithLinks(String string) {
        return newChatWithLinks(string, true);
    }

    public static Component newChatWithLinks(String string, boolean allowMissingHeader) {
        MutableComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;

        while(true) {
            String url;
            MutableComponent link;
            while(true) {
                if (!matcher.find()) {
                    String end = string.substring(lastEnd);
                    if (ichat == null) {
                        ichat = Component.literal(end);
                    } else if (end.length() > 0) {
                        ichat.append((Component)Component.literal(string.substring(lastEnd)));
                    }

                    return ichat;
                }

                int start = matcher.start();
                int end = matcher.end();
                String part = string.substring(lastEnd, start);
                if (part.length() > 0) {
                    if (ichat == null) {
                        ichat = Component.literal(part);
                    } else {
                        ichat.append(part);
                    }
                }

                lastEnd = end;
                url = string.substring(start, end);
                link = Component.literal(url);

                try {
                    if ((new URI(url)).getScheme() != null) {
                        break;
                    }

                    if (allowMissingHeader) {
                        url = "http://" + url;
                        break;
                    }

                    if (ichat == null) {
                        ichat = Component.literal(url);
                    } else {
                        ichat.append(url);
                    }
                } catch (URISyntaxException var11) {
                    if (ichat == null) {
                        ichat = Component.literal(url);
                    } else {
                        ichat.append(url);
                    }
                }
            }

            ClickEvent click = new ClickEvent(Action.OPEN_URL, url);
            link.setStyle(link.getStyle().withClickEvent(click).withUnderlined(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE)));
            if (ichat == null) {
                ichat = Component.literal("");
            }

            ichat.append((Component)link);
        }
    }

    public static void dropXpForBlock(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack) {
        int fortuneLevel = stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
        int silkTouchLevel = stack.getEnchantmentLevel(Enchantments.SILK_TOUCH);
        int exp = state.getExpDrop(level, level.random, pos, fortuneLevel, silkTouchLevel);
        if (exp > 0) {
            state.getBlock().popExperience(level, pos, exp);
        }

    }

    public static int onBlockBreakEvent(Level level, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
        boolean preCancelEvent = false;
        ItemStack itemstack = entityPlayer.getMainHandItem();
        if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(level.getBlockState(pos), level, pos, entityPlayer)) {
            preCancelEvent = true;
        }

        if (gameType.isBlockPlacingRestricted()) {
            if (gameType == GameType.SPECTATOR) {
                preCancelEvent = true;
            }

            if (!entityPlayer.mayBuild() && (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(level, pos, false)))) {
                preCancelEvent = true;
            }
        }

        if (level.getBlockEntity(pos) == null) {
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, level.getFluidState(pos).createLegacyBlock()));
        }

        BlockState state = level.getBlockState(pos);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, entityPlayer);
        event.setCanceled(preCancelEvent);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            entityPlayer.connection.send(new ClientboundBlockUpdatePacket(level, pos));
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                Packet<?> pkt = blockEntity.getUpdatePacket();
                if (pkt != null) {
                    entityPlayer.connection.send(pkt);
                }
            }
        }

        return event.isCanceled() ? -1 : event.getExpToDrop();
    }

    public static InteractionResult onPlaceItemIntoWorld(@NotNull UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().mayBuild && !itemstack.hasAdventureModePlaceTagForBlock(level.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(level, context.getClickedPos(), false))) {
            return InteractionResult.PASS;
        } else {
            Item item = itemstack.getItem();
            int size = itemstack.getCount();
            CompoundTag nbt = null;
            if (itemstack.getTag() != null) {
                nbt = itemstack.getTag().copy();
            }

            if (!(itemstack.getItem() instanceof BucketItem)) {
                level.captureBlockSnapshots = true;
            }

            ItemStack copy = itemstack.copy();
            InteractionResult ret = itemstack.getItem().useOn(context);
            if (itemstack.isEmpty()) {
                ForgeEventFactory.onPlayerDestroyItem(player, copy, context.getHand());
            }

            level.captureBlockSnapshots = false;
            if (ret.consumesAction()) {
                int newSize = itemstack.getCount();
                CompoundTag newNBT = null;
                if (itemstack.getTag() != null) {
                    newNBT = itemstack.getTag().copy();
                }

                List<BlockSnapshot> blockSnapshots = (List)level.capturedBlockSnapshots.clone();
                level.capturedBlockSnapshots.clear();
                itemstack.setCount(size);
                itemstack.setTag(nbt);
                Direction side = context.getClickedFace();
                boolean eventResult = false;
                if (blockSnapshots.size() > 1) {
                    eventResult = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, side);
                } else if (blockSnapshots.size() == 1) {
                    eventResult = ForgeEventFactory.onBlockPlace(player, (BlockSnapshot)blockSnapshots.get(0), side);
                }

                Iterator var14;
                BlockSnapshot blocksnapshot;
                if (eventResult) {
                    ret = InteractionResult.FAIL;

                    for(var14 = Lists.reverse(blockSnapshots).iterator(); var14.hasNext(); level.restoringBlockSnapshots = false) {
                        blocksnapshot = (BlockSnapshot)var14.next();
                        level.restoringBlockSnapshots = true;
                        blocksnapshot.restore(true, false);
                    }
                } else {
                    itemstack.setCount(newSize);
                    itemstack.setTag(newNBT);
                    var14 = blockSnapshots.iterator();

                    while(var14.hasNext()) {
                        blocksnapshot = (BlockSnapshot)var14.next();
                        int updateFlag = blocksnapshot.getFlag();
                        BlockState oldBlock = blocksnapshot.getReplacedBlock();
                        BlockState newBlock = level.getBlockState(blocksnapshot.getPos());
                        newBlock.onPlace(level, blocksnapshot.getPos(), oldBlock, false);
                        level.markAndNotifyBlock(blocksnapshot.getPos(), level.getChunkAt(blocksnapshot.getPos()), oldBlock, newBlock, updateFlag, 512);
                    }

                    if (player != null) {
                        player.awardStat(Stats.ITEM_USED.get(item));
                    }
                }
            }

            level.capturedBlockSnapshots.clear();
            return ret;
        }
    }

    public static boolean onAnvilChange(AnvilMenu container, @NotNull ItemStack left, @NotNull ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost, player);
        if (MinecraftForge.EVENT_BUS.post(e)) {
            return false;
        } else if (e.getOutput().isEmpty()) {
            return true;
        } else {
            outputSlot.setItem(0, e.getOutput());
            container.setMaximumCost(e.getCost());
            container.repairItemCountCost = e.getMaterialCost();
            return false;
        }
    }

    public static float onAnvilRepair(Player player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right) {
        AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getBreakChance();
    }

    public static int onGrindstoneChange(@NotNull ItemStack top, @NotNull ItemStack bottom, Container outputSlot, int xp) {
        GrindstoneEvent.OnPlaceItem e = new GrindstoneEvent.OnPlaceItem(top, bottom, xp);
        if (MinecraftForge.EVENT_BUS.post(e)) {
            outputSlot.setItem(0, ItemStack.EMPTY);
            return -1;
        } else if (e.getOutput().isEmpty()) {
            return Integer.MIN_VALUE;
        } else {
            outputSlot.setItem(0, e.getOutput());
            return e.getXp();
        }
    }

    public static boolean onGrindstoneTake(Container inputSlots, ContainerLevelAccess access, Function<Level, Integer> xpFunction) {
        access.execute((l, p) -> {
            int xp = (Integer)xpFunction.apply(l);
            GrindstoneEvent.OnTakeItem e = new GrindstoneEvent.OnTakeItem(inputSlots.getItem(0), inputSlots.getItem(1), xp);
            if (!MinecraftForge.EVENT_BUS.post(e)) {
                if (l instanceof ServerLevel) {
                    ExperienceOrb.award((ServerLevel)l, Vec3.atCenterOf(p), e.getXp());
                }

                l.levelEvent(1042, p, 0);
                inputSlots.setItem(0, e.getNewTopItem());
                inputSlots.setItem(1, e.getNewBottomItem());
                inputSlots.setChanged();
            }
        });
        return true;
    }

    public static void setCraftingPlayer(Player player) {
        craftingPlayer.set(player);
    }

    public static Player getCraftingPlayer() {
        return (Player)craftingPlayer.get();
    }

    public static @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem(stack)) {
            stack = stack.getItem().getCraftingRemainingItem(stack);
            if (!stack.isEmpty() && stack.isDamageableItem() && stack.getDamageValue() > stack.getMaxDamage()) {
                ForgeEventFactory.onPlayerDestroyItem((Player)craftingPlayer.get(), stack, (InteractionHand)null);
                return ItemStack.EMPTY;
            } else {
                return stack;
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static boolean onPlayerAttackTarget(Player player, Entity target) {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) {
            return false;
        } else {
            ItemStack stack = player.getMainHandItem();
            return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
        }
    }

    public static boolean onTravelToDimension(Entity entity, ResourceKey<Level> dimension) {
        EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimension);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand) {
        Vec3 vec3d = ray.getLocation().subtract(entity.position());
        return onInteractEntityAt(player, entity, vec3d, hand);
    }

    public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec3d, InteractionHand hand) {
        PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onInteractEntity(Player player, Entity entity, InteractionHand hand) {
        PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
        PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? evt.getCancellationResult() : null;
    }

    /** @deprecated */
    @Deprecated(
        since = "1.20.1",
        forRemoval = true
    )
    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face) {
        return onLeftClickBlock(player, pos, face, net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
    }

    public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face, ServerboundPlayerActionPacket.Action action) {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.convert(action));
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static PlayerInteractEvent.LeftClickBlock onClientMineHold(Player player, BlockPos pos, Direction face) {
        PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static PlayerInteractEvent.RightClickBlock onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
        PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, hitVec);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void onEmptyClick(Player player, InteractionHand hand) {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
    }

    public static void onEmptyLeftClick(Player player) {
        MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
    }

    public static @Nullable GameType onChangeGameType(Player player, GameType currentGameType, GameType newGameType) {
        if (currentGameType != newGameType) {
            PlayerEvent.PlayerChangeGameModeEvent evt = new PlayerEvent.PlayerChangeGameModeEvent(player, currentGameType, newGameType);
            MinecraftForge.EVENT_BUS.post(evt);
            return evt.isCanceled() ? null : evt.getNewGameMode();
        } else {
            return newGameType;
        }
    }

    private static LootTableContext getLootTableContext() {
        LootTableContext ctx = (LootTableContext)((Deque)lootContext.get()).peek();
        if (ctx == null) {
            throw new JsonParseException("Invalid call stack, could not grab json context!");
        } else {
            return ctx;
        }
    }

    public static TriFunction<ResourceLocation, JsonElement, ResourceManager, Optional<LootTable>> getLootTableDeserializer(Gson gson, String directory) {
        return (location, data, resourceManager) -> {
            try {
                Resource resource = (Resource)resourceManager.getResource(location.withPath(directory + "/" + location.getPath() + ".json")).orElse((Object)null);
                boolean custom = resource == null || !resource.isBuiltin();
                return Optional.ofNullable(loadLootTable(gson, location, data, custom));
            } catch (Exception var7) {
                Exception exception = var7;
                LOGGER.error("Couldn't parse element {}:{}", directory, location, exception);
                return Optional.empty();
            }
        };
    }

    public static LootTable loadLootTable(Gson gson, ResourceLocation name, JsonElement data, boolean custom) {
        Deque<LootTableContext> que = (Deque)lootContext.get();
        if (que == null) {
            que = Queues.newArrayDeque();
            lootContext.set(que);
        }

        LootTable ret;
        try {
            ((Deque)que).push(new LootTableContext(name, custom));
            ret = (LootTable)gson.fromJson(data, LootTable.class);
            ret.setLootTableId(name);
        } catch (JsonParseException var10) {
            JsonParseException e = var10;
            throw e;
        } finally {
            ((Deque)que).pop();
        }

        if (!custom) {
            ret = ForgeEventFactory.loadLootTable(name, ret);
        }

        if (ret != null) {
            ret.freeze();
        }

        return ret;
    }

    public static String readPoolName(JsonObject json) {
        LootTableContext ctx = getLootTableContext();
        if (json.has("name")) {
            return GsonHelper.getAsString(json, "name");
        } else if (ctx.custom) {
            return "custom#" + json.hashCode();
        } else {
            ++ctx.poolCount;
            return ctx.poolCount == 1 ? "main" : "pool" + (ctx.poolCount - 1);
        }
    }

    public static FluidType getVanillaFluidType(Fluid fluid) {
        if (fluid == Fluids.EMPTY) {
            return (FluidType)ForgeMod.EMPTY_TYPE.get();
        } else if (fluid != Fluids.WATER && fluid != Fluids.FLOWING_WATER) {
            if (fluid != Fluids.LAVA && fluid != Fluids.FLOWING_LAVA) {
                if (!ForgeMod.MILK.filter((milk) -> {
                    return milk == fluid;
                }).isPresent() && !ForgeMod.FLOWING_MILK.filter((milk) -> {
                    return milk == fluid;
                }).isPresent()) {
                    throw new RuntimeException("Mod fluids must override getFluidType.");
                } else {
                    return (FluidType)ForgeMod.MILK_TYPE.get();
                }
            } else {
                return (FluidType)ForgeMod.LAVA_TYPE.get();
            }
        } else {
            return (FluidType)ForgeMod.WATER_TYPE.get();
        }
    }

    public static TagKey<Block> getTagFromVanillaTier(Tiers tier) {
        TagKey var10000;
        switch (tier) {
            case WOOD -> var10000 = Blocks.NEEDS_WOOD_TOOL;
            case GOLD -> var10000 = Blocks.NEEDS_GOLD_TOOL;
            case STONE -> var10000 = BlockTags.NEEDS_STONE_TOOL;
            case IRON -> var10000 = BlockTags.NEEDS_IRON_TOOL;
            case DIAMOND -> var10000 = BlockTags.NEEDS_DIAMOND_TOOL;
            case NETHERITE -> var10000 = Blocks.NEEDS_NETHERITE_TOOL;
            default -> throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static Collection<CreativeModeTab> onCheckCreativeTabs(CreativeModeTab... vanillaTabs) {
        List<CreativeModeTab> tabs = new ArrayList(Arrays.asList(vanillaTabs));
        return tabs;
    }

    public static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        BlockEvent ev = new BlockEvent.CropGrowEvent.Pre(level, pos, state);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getResult() == Result.ALLOW || ev.getResult() == Result.DEFAULT && def;
    }

    public static void onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(level, pos, state, level.getBlockState(pos)));
    }

    public static @Nullable CriticalHitEvent getCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier) {
        CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
        MinecraftForge.EVENT_BUS.post(hitResult);
        return hitResult.getResult() != Result.ALLOW && (!vanillaCritical || hitResult.getResult() != Result.DEFAULT) ? null : hitResult;
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute, AttributeModifier> attributes) {
        ItemAttributeModifierEvent event = new ItemAttributeModifierEvent(stack, equipmentSlot, attributes);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    public static ItemStack getProjectile(LivingEntity entity, ItemStack projectileWeaponItem, ItemStack projectile) {
        LivingGetProjectileEvent event = new LivingGetProjectileEvent(entity, projectileWeaponItem, projectile);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getProjectileItemStack();
    }

    public static @Nullable String getDefaultCreatorModId(@NotNull ItemStack itemStack) {
        Item item = itemStack.getItem();
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
        String modId = registryName == null ? null : registryName.getNamespace();
        if ("minecraft".equals(modId)) {
            if (item instanceof EnchantedBookItem) {
                ListTag enchantmentsNbt = EnchantedBookItem.getEnchantments(itemStack);
                if (enchantmentsNbt.size() == 1) {
                    CompoundTag nbttagcompound = enchantmentsNbt.getCompound(0);
                    ResourceLocation resourceLocation = ResourceLocation.tryParse(nbttagcompound.getString("id"));
                    if (resourceLocation != null && ForgeRegistries.ENCHANTMENTS.containsKey(resourceLocation)) {
                        return resourceLocation.getNamespace();
                    }
                }
            } else if (!(item instanceof PotionItem) && !(item instanceof TippedArrowItem)) {
                if (item instanceof SpawnEggItem) {
                    ResourceLocation resourceLocation = ForgeRegistries.ENTITY_TYPES.getKey(((SpawnEggItem)item).getType((CompoundTag)null));
                    if (resourceLocation != null) {
                        return resourceLocation.getNamespace();
                    }
                }
            } else {
                Potion potionType = PotionUtils.getPotion(itemStack);
                ResourceLocation resourceLocation = ForgeRegistries.POTIONS.getKey(potionType);
                if (resourceLocation != null) {
                    return resourceLocation.getNamespace();
                }
            }
        }

        return modId;
    }

    public static boolean onFarmlandTrample(Level level, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
        if (entity.canTrample(state, pos, fallDistance)) {
            BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(level, pos, state, fallDistance, entity);
            MinecraftForge.EVENT_BUS.post(event);
            return !event.isCanceled();
        } else {
            return false;
        }
    }

    public static int onNoteChange(Level level, BlockPos pos, BlockState state, int old, int _new) {
        NoteBlockEvent.Change event = new NoteBlockEvent.Change(level, pos, state, old, _new);
        return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getVanillaNoteId();
    }

    public static boolean hasNoElements(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) {
            return true;
        } else if (items.length != 1) {
            return false;
        } else {
            ItemStack item = items[0];
            boolean var10000;
            if (item.getItem() == Items.BARRIER) {
                Component var4 = item.getHoverName();
                if (var4 instanceof MutableComponent) {
                    MutableComponent hoverName = (MutableComponent)var4;
                    if (hoverName.getString().startsWith("Empty Tag: ")) {
                        var10000 = true;
                        return var10000;
                    }
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    /** @deprecated */
    @Deprecated(
        forRemoval = true,
        since = "1.20.1"
    )
    public static <T> void deserializeTagAdditions(List<TagEntry> list, JsonObject json, List<TagEntry> allList) {
    }

    public static @Nullable EntityDataSerializer<?> getSerializer(int id, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
        EntityDataSerializer<?> serializer = (EntityDataSerializer)vanilla.byId(id);
        if (serializer == null) {
            ForgeRegistry<EntityDataSerializer<?>> registry = (ForgeRegistry)ForgeRegistries.ENTITY_DATA_SERIALIZERS.get();
            if (registry != null) {
                serializer = (EntityDataSerializer)registry.getValue(id);
            }
        }

        return serializer;
    }

    public static int getSerializerId(EntityDataSerializer<?> serializer, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
        int id = vanilla.getId(serializer);
        if (id < 0) {
            ForgeRegistry<EntityDataSerializer<?>> registry = (ForgeRegistry)ForgeRegistries.ENTITY_DATA_SERIALIZERS.get();
            if (registry != null) {
                id = registry.getID((Object)serializer);
            }
        }

        return id;
    }

    public static boolean canEntityDestroy(Level level, BlockPos pos, LivingEntity entity) {
        if (!level.isLoaded(pos)) {
            return false;
        } else {
            BlockState state = level.getBlockState(pos);
            return ForgeEventFactory.getMobGriefingEvent(level, entity) && state.canEntityDestroy(level, pos, entity) && ForgeEventFactory.onEntityDestroyBlock(entity, pos, state);
        }
    }

    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            Item item = stack.getItem();
            int ret = stack.getBurnTime(recipeType);
            return ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? (Integer)VANILLA_BURNS.getOrDefault(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)item), 0) : ret, recipeType);
        }
    }

    public static synchronized void updateBurns() {
        VANILLA_BURNS.clear();
        FurnaceBlockEntity.getFuel().entrySet().forEach((e) -> {
            VANILLA_BURNS.put(ForgeRegistries.ITEMS.getDelegateOrThrow((Object)((Item)e.getKey())), (Integer)e.getValue());
        });
    }

    /** @deprecated */
    @Deprecated
    public static List<ItemStack> modifyLoot(List<ItemStack> list, LootContext context) {
        return modifyLoot(LootTableIdCondition.UNKNOWN_LOOT_TABLE, ObjectArrayList.wrap((ItemStack[])list.toArray()), context);
    }

    public static ObjectArrayList<ItemStack> modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        context.setQueriedLootTableId(lootTableId);
        LootModifierManager man = ForgeInternalHandler.getLootModifierManager();

        IGlobalLootModifier mod;
        for(Iterator var4 = man.getAllLootMods().iterator(); var4.hasNext(); generatedLoot = mod.apply(generatedLoot, context)) {
            mod = (IGlobalLootModifier)var4.next();
        }

        return generatedLoot;
    }

    public static List<String> getModPacks() {
        List<String> modpacks = ResourcePackLoader.getPackNames();
        if (modpacks.isEmpty()) {
            throw new IllegalStateException("Attempted to retrieve mod packs before they were loaded in!");
        } else {
            return modpacks;
        }
    }

    public static List<String> getModPacksWithVanilla() {
        List<String> modpacks = getModPacks();
        modpacks.add("vanilla");
        return modpacks;
    }

    /** @deprecated */
    @Deprecated
    public static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getAttributesView() {
        return Collections.unmodifiableMap(FORGE_ATTRIBUTES);
    }

    /** @deprecated */
    @Deprecated
    public static void modifyAttributes() {
        ModLoader.get().postEvent(new EntityAttributeCreationEvent(FORGE_ATTRIBUTES));
        Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> finalMap = new HashMap();
        ModLoader.get().postEvent(new EntityAttributeModificationEvent(finalMap));
        finalMap.forEach((k, v) -> {
            AttributeSupplier supplier = DefaultAttributes.getSupplier(k);
            AttributeSupplier.Builder newBuilder = supplier != null ? new AttributeSupplier.Builder(supplier) : new AttributeSupplier.Builder();
            newBuilder.combine(v);
            FORGE_ATTRIBUTES.put(k, newBuilder.build());
        });
    }

    public static void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos) {
        MinecraftForge.EVENT_BUS.post(new EntityEvent.EnteringSection(entity, packedOldPos, packedNewPos));
    }

    public static ShieldBlockEvent onShieldBlock(LivingEntity blocker, DamageSource source, float blocked) {
        ShieldBlockEvent e = new ShieldBlockEvent(blocker, source, blocked);
        MinecraftForge.EVENT_BUS.post(e);
        return e;
    }

    public static LivingSwapItemsEvent.Hands onLivingSwapHandItems(LivingEntity livingEntity) {
        LivingSwapItemsEvent.Hands event = new LivingSwapItemsEvent.Hands(livingEntity);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void writeAdditionalLevelSaveData(WorldData worldData, CompoundTag levelTag) {
        CompoundTag fmlData = new CompoundTag();
        ListTag modList = new ListTag();
        ModList.get().getMods().forEach((mi) -> {
            CompoundTag mod = new CompoundTag();
            mod.putString("ModId", mi.getModId());
            mod.putString("ModVersion", MavenVersionStringHelper.artifactVersionToString(mi.getVersion()));
            modList.add(mod);
        });
        fmlData.put("LoadingModList", modList);
        CompoundTag registries = new CompoundTag();
        fmlData.put("Registries", registries);
        LOGGER.debug(WORLDPERSISTENCE, "Gathering id map for writing to world save {}", worldData.getLevelName());
        Iterator var5 = RegistryManager.ACTIVE.takeSnapshot(true).entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry<ResourceLocation, ForgeRegistry.Snapshot> e = (Map.Entry)var5.next();
            registries.put(((ResourceLocation)e.getKey()).toString(), ((ForgeRegistry.Snapshot)e.getValue()).write());
        }

        LOGGER.debug(WORLDPERSISTENCE, "ID Map collection complete {}", worldData.getLevelName());
        levelTag.put("fml", fmlData);
    }

    @Internal
    public static void readAdditionalLevelSaveData(CompoundTag rootTag, LevelStorageSource.LevelDirectory levelDirectory) {
        CompoundTag tag = rootTag.getCompound("fml");
        HashMap mismatchedVersions;
        if (tag.contains("LoadingModList")) {
            ListTag modList = tag.getList("LoadingModList", 10);
            mismatchedVersions = new HashMap(modList.size());
            Map<String, ArtifactVersion> missingVersions = new HashMap(modList.size());

            for(int i = 0; i < modList.size(); ++i) {
                CompoundTag mod = modList.getCompound(i);
                String modId = mod.getString("ModId");
                if (!Objects.equals("minecraft", modId)) {
                    String modVersion = mod.getString("ModVersion");
                    DefaultArtifactVersion previousVersion = new DefaultArtifactVersion(modVersion);
                    ModList.get().getModContainerById(modId).ifPresentOrElse((container) -> {
                        ArtifactVersion loadingVersion = container.getModInfo().getVersion();
                        if (!loadingVersion.equals(previousVersion)) {
                            mismatchedVersions.put(modId, previousVersion);
                        }

                    }, () -> {
                        missingVersions.put(modId, previousVersion);
                    });
                }
            }

            ModMismatchEvent mismatchEvent = new ModMismatchEvent(levelDirectory, mismatchedVersions, missingVersions);
            ModLoader.get().postEvent(mismatchEvent);
            StringBuilder resolved = new StringBuilder("The following mods have version differences that were marked resolved:");
            StringBuilder unresolved = new StringBuilder("The following mods have version differences that were not resolved:");
            mismatchEvent.getResolved().forEachOrdered((res) -> {
                String modid = res.modid();
                ModMismatchEvent.MismatchedVersionInfo diff = res.versionDifference();
                if (res.wasSelfResolved()) {
                    resolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING, self-resolved)".formatted(modid, diff.oldVersion()) : "%s (version %s -> %s, self-resolved)".formatted(modid, diff.oldVersion(), diff.newVersion()));
                } else {
                    String resolver = res.resolver().getModId();
                    resolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING, resolved by %s)".formatted(modid, diff.oldVersion(), resolver) : "%s (version %s -> %s, resolved by %s)".formatted(modid, diff.oldVersion(), diff.newVersion(), resolver));
                }

            });
            mismatchEvent.getUnresolved().forEachOrdered((unres) -> {
                String modid = unres.modid();
                ModMismatchEvent.MismatchedVersionInfo diff = unres.versionDifference();
                unresolved.append(System.lineSeparator()).append(diff.isMissing() ? "%s (version %s -> MISSING)".formatted(modid, diff.oldVersion()) : "%s (version %s -> %s)".formatted(modid, diff.oldVersion(), diff.newVersion()));
            });
            if (mismatchEvent.anyResolved()) {
                resolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.debug(WORLDPERSISTENCE, resolved.toString());
            }

            if (mismatchEvent.anyUnresolved()) {
                unresolved.append(System.lineSeparator()).append("Things may not work well.");
                LOGGER.warn(WORLDPERSISTENCE, unresolved.toString());
            }
        }

        Multimap<ResourceLocation, ResourceLocation> failedElements = null;
        if (tag.contains("Registries")) {
            mismatchedVersions = new HashMap();
            CompoundTag regs = tag.getCompound("Registries");
            Iterator var15 = regs.getAllKeys().iterator();

            while(var15.hasNext()) {
                String key = (String)var15.next();
                mismatchedVersions.put(new ResourceLocation(key), Snapshot.read(regs.getCompound(key)));
            }

            failedElements = GameData.injectSnapshot(mismatchedVersions, true, true);
        }

        if (failedElements != null && !failedElements.isEmpty()) {
            StringBuilder buf = new StringBuilder();
            buf.append("Forge Mod Loader could not load this save.\n\n").append("There are ").append(failedElements.size()).append(" unassigned registry entries in this save.\n").append("You will not be able to load until they are present again.\n\n");
            failedElements.asMap().forEach((name, entries) -> {
                buf.append("Missing ").append(name).append(":\n");
                entries.forEach((rl) -> {
                    buf.append("    ").append(rl).append("\n");
                });
            });
            LOGGER.error(WORLDPERSISTENCE, buf.toString());
        }

    }

    public static String encodeLifecycle(Lifecycle lifecycle) {
        if (lifecycle == Lifecycle.stable()) {
            return "stable";
        } else if (lifecycle == Lifecycle.experimental()) {
            return "experimental";
        } else if (lifecycle instanceof Lifecycle.Deprecated) {
            Lifecycle.Deprecated dep = (Lifecycle.Deprecated)lifecycle;
            return "deprecated=" + dep.since();
        } else {
            throw new IllegalArgumentException("Unknown lifecycle.");
        }
    }

    public static Lifecycle parseLifecycle(String lifecycle) {
        if (lifecycle.equals("stable")) {
            return Lifecycle.stable();
        } else if (lifecycle.equals("experimental")) {
            return Lifecycle.experimental();
        } else if (lifecycle.startsWith("deprecated=")) {
            return Lifecycle.deprecated(Integer.parseInt(lifecycle.substring(lifecycle.indexOf(61) + 1)));
        } else {
            throw new IllegalArgumentException("Unknown lifecycle.");
        }
    }

    public static void saveMobEffect(CompoundTag nbt, String key, MobEffect effect) {
        ResourceLocation registryName = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (registryName != null) {
            nbt.putString(key, registryName.toString());
        }

    }

    public static @Nullable MobEffect loadMobEffect(CompoundTag nbt, String key, @Nullable MobEffect fallback) {
        String registryName = nbt.getString(key);
        if (Strings.isNullOrEmpty(registryName)) {
            return fallback;
        } else {
            try {
                return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(registryName));
            } catch (ResourceLocationException var5) {
                return fallback;
            }
        }
    }

    public static boolean shouldSuppressEnderManAnger(EnderMan enderMan, Player player, ItemStack mask) {
        return mask.isEnderMask(player, enderMan) || MinecraftForge.EVENT_BUS.post(new EnderManAngerEvent(enderMan, player));
    }

    @Nullable
    public static StructuresBecomeConfiguredFix.@Nullable Conversion getStructureConversion(String originalBiome) {
        return (StructuresBecomeConfiguredFix.Conversion)((Map)FORGE_CONVERSION_MAP.get()).get(originalBiome);
    }

    public static boolean checkStructureNamespace(String biome) {
        ResourceLocation biomeLocation = ResourceLocation.tryParse(biome);
        return biomeLocation != null && !biomeLocation.getNamespace().equals("minecraft");
    }

    public static Map<PackType, Integer> readTypedPackFormats(JsonObject json) {
        ImmutableMap.Builder<PackType, Integer> map = ImmutableMap.builder();
        PackType[] var2 = PackType.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            PackType packType = var2[var4];
            String key = makePackFormatKey(packType);
            if (json.has(key)) {
                map.put(packType, GsonHelper.getAsInt(json, key));
            }
        }

        return map.buildOrThrow();
    }

    public static void writeTypedPackFormats(JsonObject json, PackMetadataSection section) {
        int packFormat = section.getPackFormat();
        PackType[] var3 = PackType.values();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            PackType packType = var3[var5];
            int format = section.getPackFormat(packType);
            if (format != packFormat) {
                json.addProperty(makePackFormatKey(packType), format);
            }
        }

    }

    private static String makePackFormatKey(PackType packType) {
        String var10000 = packType.name();
        return "forge:" + var10000.toLowerCase(Locale.ROOT) + "_pack_format";
    }

    public static String prefixNamespace(ResourceLocation registryKey) {
        return registryKey.getNamespace().equals("minecraft") ? registryKey.getPath() : registryKey.getNamespace() + "/" + registryKey.getPath();
    }

    public static boolean canUseEntitySelectors(SharedSuggestionProvider provider) {
        if (provider.hasPermission(2)) {
            return true;
        } else {
            if (provider instanceof CommandSourceStack) {
                CommandSourceStack source = (CommandSourceStack)provider;
                CommandSource var3 = source.source;
                if (var3 instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer)var3;
                    return (Boolean)PermissionAPI.getPermission(player, ForgeMod.USE_SELECTORS_PERMISSION);
                }
            }

            return false;
        }
    }

    @Internal
    public static <T> HolderLookup.RegistryLookup<T> wrapRegistryLookup(final HolderLookup.RegistryLookup<T> lookup) {
        return new HolderLookup.RegistryLookup.Delegate<T>() {
            protected HolderLookup.RegistryLookup<T> parent() {
                return lookup;
            }

            public Stream<HolderSet.Named<T>> listTags() {
                return Stream.empty();
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> key) {
                return Optional.of(HolderSet.emptyNamed(lookup, key));
            }
        };
    }

    public static void onLivingBreathe(LivingEntity entity, int consumeAirAmount, int refillAirAmount) {
        boolean isAir = entity.getEyeInFluidType().isAir() || entity.level().getBlockState(BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ())).is(net.minecraft.world.level.block.Blocks.BUBBLE_COLUMN);
        boolean canBreathe = !entity.canDrownInFluidType(entity.getEyeInFluidType()) || MobEffectUtil.hasWaterBreathing(entity) || entity instanceof Player && ((Player)entity).getAbilities().invulnerable;
        LivingBreatheEvent breatheEvent = new LivingBreatheEvent(entity, isAir || canBreathe, consumeAirAmount, refillAirAmount, isAir);
        MinecraftForge.EVENT_BUS.post(breatheEvent);
        if (breatheEvent.canBreathe()) {
            if (breatheEvent.canRefillAir()) {
                entity.setAirSupply(Math.min(entity.getAirSupply() + breatheEvent.getRefillAirAmount(), entity.getMaxAirSupply()));
            }
        } else {
            entity.setAirSupply(entity.getAirSupply() - breatheEvent.getConsumeAirAmount());
        }

        if (entity.getAirSupply() <= 0) {
            LivingDrownEvent drownEvent = new LivingDrownEvent(entity, entity.getAirSupply() <= -20, 2.0F, 8);
            if (!MinecraftForge.EVENT_BUS.post(drownEvent) && drownEvent.isDrowning()) {
                entity.setAirSupply(0);
                Vec3 vec3 = entity.getDeltaMovement();

                for(int i = 0; i < drownEvent.getBubbleCount(); ++i) {
                    double d2 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d3 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    double d4 = entity.getRandom().nextDouble() - entity.getRandom().nextDouble();
                    entity.level().addParticle(ParticleTypes.BUBBLE, entity.getX() + d2, entity.getY() + d3, entity.getZ() + d4, vec3.x, vec3.y, vec3.z);
                }

                if (drownEvent.getDamageAmount() > 0.0F) {
                    entity.hurt(entity.damageSources().drown(), drownEvent.getDamageAmount());
                }
            }
        }

        if (!isAir && !entity.level().isClientSide && entity.isPassenger() && entity.getVehicle() != null && !entity.getVehicle().canBeRiddenUnderFluidType(entity.getEyeInFluidType(), entity)) {
            entity.stopRiding();
        }

    }

    public static void onCreativeModeTabBuildContents(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = new MutableHashedLinkedMap(ItemStackLinkedSet.TYPE_AND_TAG, (key, left, right) -> {
            return TabVisibility.PARENT_AND_SEARCH_TABS;
        });
        originalGenerator.accept(params, (stack, vis) -> {
            if (stack.getCount() != 1) {
                throw new IllegalArgumentException("The stack count must be 1");
            } else {
                entries.put(stack, vis);
            }
        });
        ModLoader.get().postEvent(new BuildCreativeModeTabContentsEvent(tab, tabKey, params, entries));
        Iterator var6 = entries.iterator();

        while(var6.hasNext()) {
            Map.Entry<ItemStack, CreativeModeTab.TabVisibility> entry = (Map.Entry)var6.next();
            output.accept((ItemStack)entry.getKey(), (CreativeModeTab.TabVisibility)entry.getValue());
        }

    }

    private static class LootTableContext {
        public final ResourceLocation name;
        public final boolean vanilla;
        public final boolean custom;
        public int poolCount = 0;

        private LootTableContext(ResourceLocation name, boolean custom) {
            this.name = name;
            this.custom = custom;
            this.vanilla = "minecraft".equals(this.name.getNamespace());
        }
    }

    @FunctionalInterface
    public interface BiomeCallbackFunction {
        Biome apply(Biome.ClimateSettings var1, BiomeSpecialEffects var2, BiomeGenerationSettings var3, MobSpawnSettings var4);
    }
}
