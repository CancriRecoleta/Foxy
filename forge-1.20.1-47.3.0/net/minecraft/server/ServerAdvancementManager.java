//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import org.slf4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private AdvancementList advancements;
    private final LootDataManager lootData;
    private final ICondition.IContext context;

    /** @deprecated */
    @Deprecated
    public ServerAdvancementManager(LootDataManager p_279237_) {
        this(p_279237_, IContext.EMPTY);
    }

    public ServerAdvancementManager(LootDataManager p_279237_, ICondition.IContext context) {
        super(GSON, "advancements");
        this.advancements = new AdvancementList();
        this.lootData = p_279237_;
        this.context = context;
    }

    protected void apply(Map<ResourceLocation, JsonElement> p_136034_, ResourceManager p_136035_, ProfilerFiller p_136036_) {
        Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
        p_136034_.forEach((p_278903_, p_278904_) -> {
            try {
                JsonObject jsonobject = GsonHelper.convertToJsonObject(p_278904_, "advancement");
                Advancement.Builder advancement$builder = Builder.fromJson(jsonobject, new DeserializationContext(p_278903_, this.lootData), this.context);
                if (advancement$builder == null) {
                    LOGGER.debug("Skipping loading advancement {} as its conditions were not met", p_278903_);
                    return;
                }

                map.put(p_278903_, advancement$builder);
            } catch (Exception var6) {
                Exception exception = var6;
                LOGGER.error("Parsing error loading custom advancement {}: {}", p_278903_, exception.getMessage());
            }

        });
        AdvancementList advancementlist = new AdvancementList();
        advancementlist.add(map);
        Iterator var6 = advancementlist.getRoots().iterator();

        while(var6.hasNext()) {
            Advancement advancement = (Advancement)var6.next();
            if (advancement.getDisplay() != null) {
                TreeNodePosition.run(advancement);
            }
        }

        this.advancements = advancementlist;
    }

    @Nullable
    public Advancement getAdvancement(ResourceLocation p_136042_) {
        return this.advancements.get(p_136042_);
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.getAllAdvancements();
    }
}
