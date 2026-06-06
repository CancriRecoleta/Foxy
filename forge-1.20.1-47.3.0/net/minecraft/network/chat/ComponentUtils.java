//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
    public static final String DEFAULT_SEPARATOR_TEXT = ", ";
    public static final Component DEFAULT_SEPARATOR;
    public static final Component DEFAULT_NO_STYLE_SEPARATOR;

    public ComponentUtils() {
    }

    public static MutableComponent mergeStyles(MutableComponent p_130751_, Style p_130752_) {
        if (p_130752_.isEmpty()) {
            return p_130751_;
        } else {
            Style $$2 = p_130751_.getStyle();
            if ($$2.isEmpty()) {
                return p_130751_.setStyle(p_130752_);
            } else {
                return $$2.equals(p_130752_) ? p_130751_ : p_130751_.setStyle($$2.applyTo(p_130752_));
            }
        }
    }

    public static Optional<MutableComponent> updateForEntity(@Nullable CommandSourceStack p_178425_, Optional<Component> p_178426_, @Nullable Entity p_178427_, int p_178428_) throws CommandSyntaxException {
        return p_178426_.isPresent() ? Optional.of(updateForEntity(p_178425_, (Component)p_178426_.get(), p_178427_, p_178428_)) : Optional.empty();
    }

    public static MutableComponent updateForEntity(@Nullable CommandSourceStack p_130732_, Component p_130733_, @Nullable Entity p_130734_, int p_130735_) throws CommandSyntaxException {
        if (p_130735_ > 100) {
            return p_130733_.copy();
        } else {
            MutableComponent $$4 = p_130733_.getContents().resolve(p_130732_, p_130734_, p_130735_ + 1);
            Iterator var5 = p_130733_.getSiblings().iterator();

            while(var5.hasNext()) {
                Component $$5 = (Component)var5.next();
                $$4.append((Component)updateForEntity(p_130732_, $$5, p_130734_, p_130735_ + 1));
            }

            return $$4.withStyle(resolveStyle(p_130732_, p_130733_.getStyle(), p_130734_, p_130735_));
        }
    }

    private static Style resolveStyle(@Nullable CommandSourceStack p_130737_, Style p_130738_, @Nullable Entity p_130739_, int p_130740_) throws CommandSyntaxException {
        HoverEvent $$4 = p_130738_.getHoverEvent();
        if ($$4 != null) {
            Component $$5 = (Component)$$4.getValue(Action.SHOW_TEXT);
            if ($$5 != null) {
                HoverEvent $$6 = new HoverEvent(Action.SHOW_TEXT, updateForEntity(p_130737_, $$5, p_130739_, p_130740_ + 1));
                return p_130738_.withHoverEvent($$6);
            }
        }

        return p_130738_;
    }

    public static Component getDisplayName(GameProfile p_130728_) {
        if (p_130728_.getName() != null) {
            return Component.literal(p_130728_.getName());
        } else {
            return p_130728_.getId() != null ? Component.literal(p_130728_.getId().toString()) : Component.literal("(unknown)");
        }
    }

    public static Component formatList(Collection<String> p_130744_) {
        return formatAndSortList(p_130744_, (p_130742_) -> {
            return Component.literal(p_130742_).withStyle(ChatFormatting.GREEN);
        });
    }

    public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> p_130746_, Function<T, Component> p_130747_) {
        if (p_130746_.isEmpty()) {
            return CommonComponents.EMPTY;
        } else if (p_130746_.size() == 1) {
            return (Component)p_130747_.apply((Comparable)p_130746_.iterator().next());
        } else {
            List<T> $$2 = Lists.newArrayList(p_130746_);
            $$2.sort(Comparable::compareTo);
            return formatList($$2, (Function)p_130747_);
        }
    }

    public static <T> Component formatList(Collection<? extends T> p_178441_, Function<T, Component> p_178442_) {
        return formatList(p_178441_, DEFAULT_SEPARATOR, p_178442_);
    }

    public static <T> MutableComponent formatList(Collection<? extends T> p_178430_, Optional<? extends Component> p_178431_, Function<T, Component> p_178432_) {
        return formatList(p_178430_, (Component)DataFixUtils.orElse(p_178431_, DEFAULT_SEPARATOR), p_178432_);
    }

    public static Component formatList(Collection<? extends Component> p_178434_, Component p_178435_) {
        return formatList(p_178434_, p_178435_, Function.identity());
    }

    public static <T> MutableComponent formatList(Collection<? extends T> p_178437_, Component p_178438_, Function<T, Component> p_178439_) {
        if (p_178437_.isEmpty()) {
            return Component.empty();
        } else if (p_178437_.size() == 1) {
            return ((Component)p_178439_.apply(p_178437_.iterator().next())).copy();
        } else {
            MutableComponent $$3 = Component.empty();
            boolean $$4 = true;

            for(Iterator var5 = p_178437_.iterator(); var5.hasNext(); $$4 = false) {
                T $$5 = var5.next();
                if (!$$4) {
                    $$3.append(p_178438_);
                }

                $$3.append((Component)p_178439_.apply($$5));
            }

            return $$3;
        }
    }

    public static MutableComponent wrapInSquareBrackets(Component p_130749_) {
        return Component.translatable("chat.square_brackets", p_130749_);
    }

    public static Component fromMessage(Message p_130730_) {
        if (p_130730_ instanceof Component $$1) {
            return $$1;
        } else {
            return Component.literal(p_130730_.getString());
        }
    }

    public static boolean isTranslationResolvable(@Nullable Component p_237135_) {
        if (p_237135_ != null) {
            ComponentContents var2 = p_237135_.getContents();
            if (var2 instanceof TranslatableContents) {
                TranslatableContents $$1 = (TranslatableContents)var2;
                String $$2 = $$1.getKey();
                String $$3 = $$1.getFallback();
                return $$3 != null || Language.getInstance().has($$2);
            }
        }

        return true;
    }

    public static MutableComponent copyOnClickText(String p_260039_) {
        return wrapInSquareBrackets(Component.literal(p_260039_).withStyle((p_258207_) -> {
            return p_258207_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, p_260039_)).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.translatable("chat.copy.click"))).withInsertion(p_260039_);
        }));
    }

    static {
        DEFAULT_SEPARATOR = Component.literal(", ").withStyle(ChatFormatting.GRAY);
        DEFAULT_NO_STYLE_SEPARATOR = Component.literal(", ");
    }
}
