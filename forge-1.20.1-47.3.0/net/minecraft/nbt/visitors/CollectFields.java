//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.nbt.visitors;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class CollectFields extends CollectToTag {
    private int fieldsToGetCount;
    private final Set<TagType<?>> wantedTypes;
    private final Deque<FieldTree> stack = new ArrayDeque();

    public CollectFields(FieldSelector... p_202496_) {
        this.fieldsToGetCount = p_202496_.length;
        ImmutableSet.Builder<TagType<?>> $$1 = ImmutableSet.builder();
        FieldTree $$2 = FieldTree.createRoot();
        FieldSelector[] var4 = p_202496_;
        int var5 = p_202496_.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            FieldSelector $$3 = var4[var6];
            $$2.addEntry($$3);
            $$1.add($$3.type());
        }

        this.stack.push($$2);
        $$1.add(CompoundTag.TYPE);
        this.wantedTypes = $$1.build();
    }

    public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> p_197614_) {
        return p_197614_ != CompoundTag.TYPE ? net.minecraft.nbt.StreamTagVisitor.ValueResult.HALT : super.visitRootEntry(p_197614_);
    }

    public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197608_) {
        FieldTree $$1 = (FieldTree)this.stack.element();
        if (this.depth() > $$1.depth()) {
            return super.visitEntry(p_197608_);
        } else if (this.fieldsToGetCount <= 0) {
            return net.minecraft.nbt.StreamTagVisitor.EntryResult.HALT;
        } else {
            return !this.wantedTypes.contains(p_197608_) ? net.minecraft.nbt.StreamTagVisitor.EntryResult.SKIP : super.visitEntry(p_197608_);
        }
    }

    public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197610_, String p_197611_) {
        FieldTree $$2 = (FieldTree)this.stack.element();
        if (this.depth() > $$2.depth()) {
            return super.visitEntry(p_197610_, p_197611_);
        } else if ($$2.selectedFields().remove(p_197611_, p_197610_)) {
            --this.fieldsToGetCount;
            return super.visitEntry(p_197610_, p_197611_);
        } else {
            if (p_197610_ == CompoundTag.TYPE) {
                FieldTree $$3 = (FieldTree)$$2.fieldsToRecurse().get(p_197611_);
                if ($$3 != null) {
                    this.stack.push($$3);
                    return super.visitEntry(p_197610_, p_197611_);
                }
            }

            return net.minecraft.nbt.StreamTagVisitor.EntryResult.SKIP;
        }
    }

    public StreamTagVisitor.ValueResult visitContainerEnd() {
        if (this.depth() == ((FieldTree)this.stack.element()).depth()) {
            this.stack.pop();
        }

        return super.visitContainerEnd();
    }

    public int getMissingFieldCount() {
        return this.fieldsToGetCount;
    }
}
