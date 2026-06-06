//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component.Serializer;

public class BlockEntitySignDoubleSidedEditableTextFix extends NamedEntityFix {
    public BlockEntitySignDoubleSidedEditableTextFix(Schema p_277789_, String p_278061_, String p_277403_) {
        super(p_277789_, false, p_278061_, References.BLOCK_ENTITY, p_277403_);
    }

    private static Dynamic<?> fixTag(Dynamic<?> p_278110_) {
        String $$1 = "black";
        Dynamic<?> $$2 = p_278110_.emptyMap();
        $$2 = $$2.set("messages", getTextList(p_278110_, "Text"));
        $$2 = $$2.set("filtered_messages", getTextList(p_278110_, "FilteredText"));
        Optional<? extends Dynamic<?>> $$3 = p_278110_.get("Color").result();
        $$2 = $$2.set("color", $$3.isPresent() ? (Dynamic)$$3.get() : $$2.createString("black"));
        Optional<? extends Dynamic<?>> $$4 = p_278110_.get("GlowingText").result();
        $$2 = $$2.set("has_glowing_text", $$4.isPresent() ? (Dynamic)$$4.get() : $$2.createBoolean(false));
        Dynamic<?> $$5 = p_278110_.emptyMap();
        Dynamic<?> $$6 = getEmptyTextList(p_278110_);
        $$5 = $$5.set("messages", $$6);
        $$5 = $$5.set("filtered_messages", $$6);
        $$5 = $$5.set("color", $$5.createString("black"));
        $$5 = $$5.set("has_glowing_text", $$5.createBoolean(false));
        p_278110_ = p_278110_.set("front_text", $$2);
        p_278110_ = p_278110_.set("back_text", $$5);
        return p_278110_;
    }

    private static <T> Dynamic<T> getTextList(Dynamic<T> p_277452_, String p_277422_) {
        Dynamic<T> $$2 = p_277452_.createString(getEmptyComponent());
        return p_277452_.createList(Stream.of((Dynamic)p_277452_.get(p_277422_ + "1").result().orElse($$2), (Dynamic)p_277452_.get(p_277422_ + "2").result().orElse($$2), (Dynamic)p_277452_.get(p_277422_ + "3").result().orElse($$2), (Dynamic)p_277452_.get(p_277422_ + "4").result().orElse($$2)));
    }

    private static <T> Dynamic<T> getEmptyTextList(Dynamic<T> p_277949_) {
        Dynamic<T> $$1 = p_277949_.createString(getEmptyComponent());
        return p_277949_.createList(Stream.of($$1, $$1, $$1, $$1));
    }

    private static String getEmptyComponent() {
        return Serializer.toJson(CommonComponents.EMPTY);
    }

    protected Typed<?> fix(Typed<?> p_277962_) {
        return p_277962_.update(DSL.remainderFinder(), BlockEntitySignDoubleSidedEditableTextFix::fixTag);
    }
}
