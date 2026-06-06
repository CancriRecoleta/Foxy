//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.crafting.conditions;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagManager;

public class ConditionContext implements ICondition.IContext {
    private final TagManager tagManager;
    private Map<ResourceKey<?>, Map<ResourceLocation, Collection<Holder<?>>>> loadedTags = null;

    public ConditionContext(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
        if (this.loadedTags == null) {
            List<TagManager.LoadResult<?>> tags = this.tagManager.getResult();
            if (tags.isEmpty()) {
                throw new IllegalStateException("Tags have not been loaded yet.");
            }

            this.loadedTags = new IdentityHashMap();
            Iterator var3 = tags.iterator();

            while(var3.hasNext()) {
                TagManager.LoadResult<?> loadResult = (TagManager.LoadResult)var3.next();
                Map<ResourceLocation, Collection<? extends Holder<?>>> map = Collections.unmodifiableMap(loadResult.tags());
                this.loadedTags.put(loadResult.key(), map);
            }
        }

        return (Map)this.loadedTags.getOrDefault(registry, Collections.emptyMap());
    }
}
