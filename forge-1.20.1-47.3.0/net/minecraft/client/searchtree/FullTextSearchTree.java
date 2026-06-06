//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FullTextSearchTree<T> extends IdSearchTree<T> {
    private final List<T> contents;
    private final Function<T, Stream<String>> filler;
    private PlainTextSearchTree<T> plainTextSearchTree = PlainTextSearchTree.empty();

    public FullTextSearchTree(Function<T, Stream<String>> p_235155_, Function<T, Stream<ResourceLocation>> p_235156_, List<T> p_235157_) {
        super(p_235156_, p_235157_);
        this.contents = p_235157_;
        this.filler = p_235155_;
    }

    public void refresh() {
        super.refresh();
        this.plainTextSearchTree = PlainTextSearchTree.create(this.contents, this.filler);
    }

    protected List<T> searchPlainText(String p_235160_) {
        return this.plainTextSearchTree.search(p_235160_);
    }

    protected List<T> searchResourceLocation(String p_235162_, String p_235163_) {
        List<T> $$2 = this.resourceLocationSearchTree.searchNamespace(p_235162_);
        List<T> $$3 = this.resourceLocationSearchTree.searchPath(p_235163_);
        List<T> $$4 = this.plainTextSearchTree.search(p_235163_);
        Iterator<T> $$5 = new MergingUniqueIterator($$3.iterator(), $$4.iterator(), this.additionOrder);
        return ImmutableList.copyOf(new IntersectionIterator($$2.iterator(), $$5, this.additionOrder));
    }
}
