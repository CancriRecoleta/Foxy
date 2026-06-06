//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.narration;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenNarrationCollector {
    int generation;
    final Map<EntryKey, NarrationEntry> entries = Maps.newTreeMap(Comparator.comparing((p_169196_) -> {
        return p_169196_.type;
    }).thenComparing((p_169185_) -> {
        return p_169185_.depth;
    }));

    public ScreenNarrationCollector() {
    }

    public void update(Consumer<NarrationElementOutput> p_169187_) {
        ++this.generation;
        p_169187_.accept(new Output(0));
    }

    public String collectNarrationText(boolean p_169189_) {
        final StringBuilder $$1 = new StringBuilder();
        Consumer<String> $$2 = new Consumer<String>() {
            private boolean firstEntry = true;

            public void accept(String p_169204_) {
                if (!this.firstEntry) {
                    $$1.append(". ");
                }

                this.firstEntry = false;
                $$1.append(p_169204_);
            }
        };
        this.entries.forEach((p_169193_, p_169194_) -> {
            if (p_169194_.generation == this.generation && (p_169189_ || !p_169194_.alreadyNarrated)) {
                p_169194_.contents.getText($$2);
                p_169194_.alreadyNarrated = true;
            }

        });
        return $$1.toString();
    }

    @OnlyIn(Dist.CLIENT)
    class Output implements NarrationElementOutput {
        private final int depth;

        Output(int p_169223_) {
            this.depth = p_169223_;
        }

        public void add(NarratedElementType p_169226_, NarrationThunk<?> p_169227_) {
            ((NarrationEntry)ScreenNarrationCollector.this.entries.computeIfAbsent(new EntryKey(p_169226_, this.depth), (p_169229_) -> {
                return new NarrationEntry();
            })).update(ScreenNarrationCollector.this.generation, p_169227_);
        }

        public NarrationElementOutput nest() {
            return ScreenNarrationCollector.this.new Output(this.depth + 1);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class NarrationEntry {
        NarrationThunk<?> contents;
        int generation;
        boolean alreadyNarrated;

        NarrationEntry() {
            this.contents = NarrationThunk.EMPTY;
            this.generation = -1;
        }

        public NarrationEntry update(int p_169217_, NarrationThunk<?> p_169218_) {
            if (!this.contents.equals(p_169218_)) {
                this.contents = p_169218_;
                this.alreadyNarrated = false;
            } else if (this.generation + 1 != p_169217_) {
                this.alreadyNarrated = false;
            }

            this.generation = p_169217_;
            return this;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class EntryKey {
        final NarratedElementType type;
        final int depth;

        EntryKey(NarratedElementType p_169210_, int p_169211_) {
            this.type = p_169210_;
            this.depth = p_169211_;
        }
    }
}
