//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.searchtree;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface PlainTextSearchTree<T> {
    static <T> PlainTextSearchTree<T> empty() {
        return (p_235196_) -> {
            return List.of();
        };
    }

    static <T> PlainTextSearchTree<T> create(List<T> p_235198_, Function<T, Stream<String>> p_235199_) {
        if (p_235198_.isEmpty()) {
            return empty();
        } else {
            SuffixArray<T> $$2 = new SuffixArray();
            Iterator var3 = p_235198_.iterator();

            while(var3.hasNext()) {
                T $$3 = var3.next();
                ((Stream)p_235199_.apply($$3)).forEach((p_235194_) -> {
                    $$2.add($$3, p_235194_.toLowerCase(Locale.ROOT));
                });
            }

            $$2.generate();
            Objects.requireNonNull($$2);
            return $$2::search;
        }
    }

    List<T> search(String var1);
}
