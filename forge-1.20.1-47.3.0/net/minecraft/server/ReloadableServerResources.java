//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandBuildContext.MissingTagAccessPolicy;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ConditionContext;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TagsUpdatedEvent;
import org.slf4j.Logger;

public class ReloadableServerResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK;
    private final CommandBuildContext.Configurable commandBuildContext;
    private final Commands commands;
    private final RecipeManager recipes;
    private final TagManager tagManager;
    private final LootDataManager lootData = new LootDataManager();
    private final ServerAdvancementManager advancements;
    private final ServerFunctionLibrary functionLibrary;
    private final ICondition.IContext context;

    public ReloadableServerResources(RegistryAccess.Frozen p_206857_, FeatureFlagSet p_250695_, Commands.CommandSelection p_206858_, int p_206859_) {
        this.tagManager = new TagManager(p_206857_);
        this.commandBuildContext = CommandBuildContext.configurable(p_206857_, p_250695_);
        this.commands = new Commands(p_206858_, this.commandBuildContext);
        this.commandBuildContext.missingTagAccessPolicy(MissingTagAccessPolicy.CREATE_NEW);
        this.functionLibrary = new ServerFunctionLibrary(p_206859_, this.commands.getDispatcher());
        this.context = new ConditionContext(this.tagManager);
        this.recipes = new RecipeManager(this.context);
        this.advancements = new ServerAdvancementManager(this.lootData, this.context);
    }

    public ServerFunctionLibrary getFunctionLibrary() {
        return this.functionLibrary;
    }

    public LootDataManager getLootData() {
        return this.lootData;
    }

    public RecipeManager getRecipeManager() {
        return this.recipes;
    }

    public Commands getCommands() {
        return this.commands;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.advancements;
    }

    public List<PreparableReloadListener> listeners() {
        return List.of(this.tagManager, this.lootData, this.recipes, this.functionLibrary, this.advancements);
    }

    public static CompletableFuture<ReloadableServerResources> loadResources(ResourceManager p_248588_, RegistryAccess.Frozen p_251163_, FeatureFlagSet p_250212_, Commands.CommandSelection p_249301_, int p_251126_, Executor p_249136_, Executor p_249601_) {
        ReloadableServerResources reloadableserverresources = new ReloadableServerResources(p_251163_, p_250212_, p_249301_, p_251126_);
        List<PreparableReloadListener> listeners = new ArrayList(reloadableserverresources.listeners());
        listeners.addAll(ForgeEventFactory.onResourceReload(reloadableserverresources, p_251163_));
        return SimpleReloadInstance.create(p_248588_, listeners, p_249136_, p_249601_, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled()).done().whenComplete((p_255534_, p_255535_) -> {
            reloadableserverresources.commandBuildContext.missingTagAccessPolicy(MissingTagAccessPolicy.FAIL);
        }).thenApply((p_214306_) -> {
            return reloadableserverresources;
        });
    }

    public void updateRegistryTags(RegistryAccess p_206869_) {
        this.tagManager.getResult().forEach((p_214315_) -> {
            updateRegistryTags(p_206869_, p_214315_);
        });
        Blocks.rebuildCache();
        MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent(p_206869_, false, false));
    }

    private static <T> void updateRegistryTags(RegistryAccess p_206871_, TagManager.LoadResult<T> p_206872_) {
        ResourceKey<? extends Registry<T>> resourcekey = p_206872_.key();
        Map<TagKey<T>, List<Holder<T>>> map = (Map)p_206872_.tags().entrySet().stream().collect(Collectors.toUnmodifiableMap((p_214303_) -> {
            return TagKey.create(resourcekey, (ResourceLocation)p_214303_.getKey());
        }, (p_214312_) -> {
            return List.copyOf((Collection)p_214312_.getValue());
        }));
        p_206871_.registryOrThrow(resourcekey).bindTags(map);
    }

    public ICondition.IContext getConditionContext() {
        return this.context;
    }

    static {
        DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
    }
}
