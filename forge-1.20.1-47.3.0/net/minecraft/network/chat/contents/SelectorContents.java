//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

public class SelectorContents implements ComponentContents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String pattern;
    @Nullable
    private final EntitySelector selector;
    protected final Optional<Component> separator;

    public SelectorContents(String p_237464_, Optional<Component> p_237465_) {
        this.pattern = p_237464_;
        this.separator = p_237465_;
        this.selector = parseSelector(p_237464_);
    }

    @Nullable
    private static EntitySelector parseSelector(String p_237472_) {
        EntitySelector $$1 = null;

        try {
            EntitySelectorParser $$2 = new EntitySelectorParser(new StringReader(p_237472_));
            $$1 = $$2.parse();
        } catch (CommandSyntaxException var3) {
            CommandSyntaxException $$3 = var3;
            LOGGER.warn("Invalid selector component: {}: {}", p_237472_, $$3.getMessage());
        }

        return $$1;
    }

    public String getPattern() {
        return this.pattern;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public Optional<Component> getSeparator() {
        return this.separator;
    }

    public MutableComponent resolve(@Nullable CommandSourceStack p_237468_, @Nullable Entity p_237469_, int p_237470_) throws CommandSyntaxException {
        if (p_237468_ != null && this.selector != null) {
            Optional<? extends Component> $$3 = ComponentUtils.updateForEntity(p_237468_, this.separator, p_237469_, p_237470_);
            return ComponentUtils.formatList(this.selector.findEntities(p_237468_), (Optional)$$3, Entity::getDisplayName);
        } else {
            return Component.empty();
        }
    }

    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_237476_, Style p_237477_) {
        return p_237476_.accept(p_237477_, this.pattern);
    }

    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_237474_) {
        return p_237474_.accept(this.pattern);
    }

    public boolean equals(Object p_237481_) {
        if (this == p_237481_) {
            return true;
        } else {
            boolean var10000;
            if (p_237481_ instanceof SelectorContents) {
                SelectorContents $$1 = (SelectorContents)p_237481_;
                if (this.pattern.equals($$1.pattern) && this.separator.equals($$1.separator)) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        int $$0 = this.pattern.hashCode();
        $$0 = 31 * $$0 + this.separator.hashCode();
        return $$0;
    }

    public String toString() {
        return "pattern{" + this.pattern + "}";
    }
}
