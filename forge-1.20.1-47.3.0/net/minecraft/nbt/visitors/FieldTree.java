//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt.visitors;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.TagType;

public record FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
    private FieldTree(int p_202527_) {
        this(p_202527_, new HashMap(), new HashMap());
    }

    public FieldTree(int depth, Map<String, TagType<?>> selectedFields, Map<String, FieldTree> fieldsToRecurse) {
        this.depth = depth;
        this.selectedFields = selectedFields;
        this.fieldsToRecurse = fieldsToRecurse;
    }

    public static FieldTree createRoot() {
        return new FieldTree(1);
    }

    public void addEntry(FieldSelector p_202539_) {
        if (this.depth <= p_202539_.path().size()) {
            ((FieldTree)this.fieldsToRecurse.computeIfAbsent((String)p_202539_.path().get(this.depth - 1), (p_202534_) -> {
                return new FieldTree(this.depth + 1);
            })).addEntry(p_202539_);
        } else {
            this.selectedFields.put(p_202539_.name(), p_202539_.type());
        }

    }

    public boolean isSelected(TagType<?> p_202536_, String p_202537_) {
        return p_202536_.equals(this.selectedFields().get(p_202537_));
    }

    public int depth() {
        return this.depth;
    }

    public Map<String, TagType<?>> selectedFields() {
        return this.selectedFields;
    }

    public Map<String, FieldTree> fieldsToRecurse() {
        return this.fieldsToRecurse;
    }
}
