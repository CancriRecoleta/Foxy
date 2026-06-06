//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument implements ArgumentType<Anchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType((p_90357_) -> {
        return Component.translatable("argument.anchor.invalid", p_90357_);
    });

    public EntityAnchorArgument() {
    }

    public static Anchor getAnchor(CommandContext<CommandSourceStack> p_90354_, String p_90355_) {
        return (Anchor)p_90354_.getArgument(p_90355_, Anchor.class);
    }

    public static EntityAnchorArgument anchor() {
        return new EntityAnchorArgument();
    }

    public Anchor parse(StringReader p_90352_) throws CommandSyntaxException {
        int $$1 = p_90352_.getCursor();
        String $$2 = p_90352_.readUnquotedString();
        Anchor $$3 = net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.getByName($$2);
        if ($$3 == null) {
            p_90352_.setCursor($$1);
            throw ERROR_INVALID.createWithContext(p_90352_, $$2);
        } else {
            return $$3;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_90360_, SuggestionsBuilder p_90361_) {
        return SharedSuggestionProvider.suggest((Iterable)net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.BY_NAME.keySet(), p_90361_);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static enum Anchor {
        FEET("feet", (p_90389_, p_90390_) -> {
            return p_90389_;
        }),
        EYES("eyes", (p_90382_, p_90383_) -> {
            return new Vec3(p_90382_.x, p_90382_.y + (double)p_90383_.getEyeHeight(), p_90382_.z);
        });

        static final Map<String, Anchor> BY_NAME = (Map)Util.make(Maps.newHashMap(), (p_90387_) -> {
            Anchor[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Anchor $$1 = var1[var3];
                p_90387_.put($$1.name, $$1);
            }

        });
        private final String name;
        private final BiFunction<Vec3, Entity, Vec3> transform;

        private Anchor(String p_90374_, BiFunction p_90375_) {
            this.name = p_90374_;
            this.transform = p_90375_;
        }

        @Nullable
        public static Anchor getByName(String p_90385_) {
            return (Anchor)BY_NAME.get(p_90385_);
        }

        public Vec3 apply(Entity p_90378_) {
            return (Vec3)this.transform.apply(p_90378_.position(), p_90378_);
        }

        public Vec3 apply(CommandSourceStack p_90380_) {
            Entity $$1 = p_90380_.getEntity();
            return $$1 == null ? p_90380_.getPosition() : (Vec3)this.transform.apply(p_90380_.getPosition(), $$1);
        }
    }
}
