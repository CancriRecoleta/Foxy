//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public abstract class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
    private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
    private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy;

    /** @deprecated */
    @Deprecated
    public ItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_) {
        this(p_275343_, p_275729_, p_275322_, "vanilla", (ExistingFileHelper)null);
    }

    public ItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, Registries.ITEM, p_275729_, (p_255790_) -> {
            return p_255790_.builtInRegistryHolder().key();
        }, modId, existingFileHelper);
        this.tagsToCopy = new HashMap();
        this.blockTags = p_275322_;
    }

    /** @deprecated */
    @Deprecated
    public ItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagsProvider.TagLookup<Item>> p_275207_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275634_) {
        this(p_275204_, p_275194_, p_275207_, p_275634_, "vanilla", (ExistingFileHelper)null);
    }

    public ItemTagsProvider(PackOutput p_275204_, CompletableFuture<HolderLookup.Provider> p_275194_, CompletableFuture<TagsProvider.TagLookup<Item>> p_275207_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275634_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275204_, Registries.ITEM, p_275194_, p_275207_, (p_274765_) -> {
            return p_274765_.builtInRegistryHolder().key();
        });
        this.tagsToCopy = new HashMap();
        this.blockTags = p_275634_;
    }

    protected void copy(TagKey<Block> p_206422_, TagKey<Item> p_206423_) {
        this.tagsToCopy.put(p_206422_, p_206423_);
    }

    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return super.createContentsProvider().thenCombineAsync(this.blockTags, (p_274766_, p_274767_) -> {
            this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
                TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_274764_);
                Optional<TagBuilder> optional = (Optional)p_274767_.apply(p_274763_);
                List var10000 = ((TagBuilder)optional.orElseThrow(() -> {
                    return new IllegalStateException("Missing block tag " + p_274764_.location());
                })).build();
                Objects.requireNonNull(tagbuilder);
                var10000.forEach(tagbuilder::add);
            });
            return p_274766_;
        });
    }
}
