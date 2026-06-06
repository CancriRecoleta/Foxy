//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider.ElementSuggestionType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T> implements ArgumentType<Result<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    final ResourceKey<? extends Registry<T>> registryKey;

    public ResourceOrTagKeyArgument(ResourceKey<? extends Registry<T>> p_248579_) {
        this.registryKey = p_248579_;
    }

    public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends Registry<T>> p_249175_) {
        return new ResourceOrTagKeyArgument(p_249175_);
    }

    public static <T> Result<T> getResourceOrTagKey(CommandContext<CommandSourceStack> p_252162_, String p_248628_, ResourceKey<Registry<T>> p_249008_, DynamicCommandExceptionType p_251387_) throws CommandSyntaxException {
        Result<?> $$4 = (Result)p_252162_.getArgument(p_248628_, Result.class);
        Optional<Result<T>> $$5 = $$4.cast(p_249008_);
        return (Result)$$5.orElseThrow(() -> {
            return p_251387_.create($$4);
        });
    }

    public Result<T> parse(StringReader p_250307_) throws CommandSyntaxException {
        if (p_250307_.canRead() && p_250307_.peek() == '#') {
            int $$1 = p_250307_.getCursor();

            try {
                p_250307_.skip();
                ResourceLocation $$2 = ResourceLocation.read(p_250307_);
                return new TagResult(TagKey.create(this.registryKey, $$2));
            } catch (CommandSyntaxException var4) {
                CommandSyntaxException $$3 = var4;
                p_250307_.setCursor($$1);
                throw $$3;
            }
        } else {
            ResourceLocation $$4 = ResourceLocation.read(p_250307_);
            return new ResourceResult(ResourceKey.create(this.registryKey, $$4));
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_251659_, SuggestionsBuilder p_251141_) {
        Object var4 = p_251659_.getSource();
        if (var4 instanceof SharedSuggestionProvider $$2) {
            return $$2.suggestRegistryElements(this.registryKey, ElementSuggestionType.ALL, p_251141_, p_251659_);
        } else {
            return p_251141_.buildFuture();
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface Result<T> extends Predicate<Holder<T>> {
        Either<ResourceKey<T>, TagKey<T>> unwrap();

        <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

        String asPrintable();
    }

    static record TagResult<T>(TagKey<T> key) implements Result<T> {
        TagResult(TagKey<T> key) {
            this.key = key;
        }

        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> p_251833_) {
            return this.key.cast(p_251833_).map(TagResult::new);
        }

        public boolean test(Holder<T> p_252238_) {
            return p_252238_.is(this.key);
        }

        public String asPrintable() {
            return "#" + this.key.location();
        }

        public TagKey<T> key() {
            return this.key;
        }
    }

    private static record ResourceResult<T>(ResourceKey<T> key) implements Result<T> {
        ResourceResult(ResourceKey<T> key) {
            this.key = key;
        }

        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> p_251369_) {
            return this.key.cast(p_251369_).map(ResourceResult::new);
        }

        public boolean test(Holder<T> p_250257_) {
            return p_250257_.is(this.key);
        }

        public String asPrintable() {
            return this.key.location().toString();
        }

        public ResourceKey<T> key() {
            return this.key;
        }
    }

    public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, Info<T>.Template> {
        public Info() {
        }

        public void serializeToNetwork(Info<T>.Template p_252211_, FriendlyByteBuf p_248784_) {
            p_248784_.writeResourceLocation(p_252211_.registryKey.location());
        }

        public Info<T>.Template deserializeFromNetwork(FriendlyByteBuf p_250656_) {
            ResourceLocation $$1 = p_250656_.readResourceLocation();
            return new Template(ResourceKey.createRegistryKey($$1));
        }

        public void serializeToJson(Info<T>.Template p_250715_, JsonObject p_249208_) {
            p_249208_.addProperty("registry", p_250715_.registryKey.location().toString());
        }

        public Info<T>.Template unpack(ResourceOrTagKeyArgument<T> p_250422_) {
            return new Template(p_250422_.registryKey);
        }

        public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
            final ResourceKey<? extends Registry<T>> registryKey;

            Template(ResourceKey<? extends Registry<T>> p_251992_) {
                this.registryKey = p_251992_;
            }

            public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext p_251559_) {
                return new ResourceOrTagKeyArgument(this.registryKey);
            }

            public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
                return Info.this;
            }
        }
    }
}
