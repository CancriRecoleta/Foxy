//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.network;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FilterMask;

public record FilteredText(String raw, FilterMask mask) {
    public static final FilteredText EMPTY = passThrough("");

    public FilteredText(String raw, FilterMask mask) {
        this.raw = raw;
        this.mask = mask;
    }

    public static FilteredText passThrough(String p_243257_) {
        return new FilteredText(p_243257_, FilterMask.PASS_THROUGH);
    }

    public static FilteredText fullyFiltered(String p_243261_) {
        return new FilteredText(p_243261_, FilterMask.FULLY_FILTERED);
    }

    @Nullable
    public String filtered() {
        return this.mask.apply(this.raw);
    }

    public String filteredOrEmpty() {
        return (String)Objects.requireNonNullElse(this.filtered(), "");
    }

    public boolean isFiltered() {
        return !this.mask.isEmpty();
    }

    public String raw() {
        return this.raw;
    }

    public FilterMask mask() {
        return this.mask;
    }
}
