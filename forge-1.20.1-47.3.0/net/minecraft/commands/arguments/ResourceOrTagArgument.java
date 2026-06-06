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
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
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
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagArgument<T> implements ArgumentType<Result<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_TAG = new Dynamic2CommandExceptionType((p_250953_, p_249704_) -> {
        return Component.translatable("argument.resource_tag.not_found", p_250953_, p_249704_);
    });
    private static final Dynamic3CommandExceptionType ERROR_INVALID_TAG_TYPE = new Dynamic3CommandExceptionType((p_250188_, p_252173_, p_251453_) -> {
        return Component.translatable("argument.resource_tag.invalid_type", p_250188_, p_252173_, p_251453_);
    });
    private final HolderLookup<T> registryLookup;
    final ResourceKey<? extends Registry<T>> registryKey;

    public ResourceOrTagArgument(CommandBuildContext p_249382_, ResourceKey<? extends Registry<T>> p_251209_) {
        this.registryKey = p_251209_;
        this.registryLookup = p_249382_.holderLookup(p_251209_);
    }

    public static <T> ResourceOrTagArgument<T> resourceOrTag(CommandBuildContext p_251101_, ResourceKey<? extends Registry<T>> p_248888_) {
        return new ResourceOrTagArgument(p_251101_, p_248888_);
    }

    public static <T> Result<T> getResourceOrTag(CommandContext<CommandSourceStack> p_249001_, String p_251520_, ResourceKey<Registry<T>> p_250370_) throws CommandSyntaxException {
        Result<?> $$3 = (Result)p_249001_.getArgument(p_251520_, Result.class);
        Optional<Result<T>> $$4 = $$3.cast(p_250370_);
        return (Result)$$4.orElseThrow(() -> {
            return (CommandSyntaxException)$$3.unwrap().map((p_252340_) -> {
                ResourceKey<?> $$2 = p_252340_.key();
                return ResourceArgument.ERROR_INVALID_RESOURCE_TYPE.create($$2.location(), $$2.registry(), p_250370_.location());
            }, (p_250301_) -> {
                TagKey<?> $$2 = p_250301_.key();
                return ERROR_INVALID_TAG_TYPE.create($$2.location(), $$2.registry(), p_250370_.location());
            });
        });
    }

    public Result<T> parse(StringReader p_250860_) throws CommandSyntaxException {
        if (p_250860_.canRead() && p_250860_.peek() == '#') {
            int $$1 = p_250860_.getCursor();

            try {
                p_250860_.skip();
                ResourceLocation $$2 = ResourceLocation.read(p_250860_);
                TagKey<T> $$3 = TagKey.create(this.registryKey, $$2);
                HolderSet.Named<T> $$4 = (HolderSet.Named)this.registryLookup.get($$3).orElseThrow(() -> {
                    return ERROR_UNKNOWN_TAG.create($$2, this.registryKey.location());
                });
                return new TagResult($$4);
            } catch (CommandSyntaxException var6) {
                CommandSyntaxException $$5 = var6;
                p_250860_.setCursor($$1);
                throw $$5;
            }
        } else {
            ResourceLocation $$6 = ResourceLocation.read(p_250860_);
            ResourceKey<T> $$7 = ResourceKey.create(this.registryKey, $$6);
            Holder.Reference<T> $$8 = (Holder.Reference)this.registryLookup.get($$7).orElseThrow(() -> {
                return ResourceArgument.ERROR_UNKNOWN_RESOURCE.create($$6, this.registryKey.location());
            });
            return new ResourceResult($$8);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_250223_, SuggestionsBuilder p_252354_) {
        SharedSuggestionProvider.suggestResource(this.registryLookup.listTagIds().map(TagKey::location), p_252354_, "#");
        return SharedSuggestionProvider.suggestResource(this.registryLookup.listElementIds().map(ResourceKey::location), p_252354_);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public interface Result<T> extends Predicate<Holder<T>> {
        Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap();

        <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

        String asPrintable();
    }

    private static record TagResult<T>(HolderSet.Named<T> tag) implements Result<T> {
        TagResult(HolderSet.Named<T> tag) {
            this.tag = tag;
        }

        public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
            return Either.right(this.tag);
        }

        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> p_250945_) {
            return this.tag.key().isFor(p_250945_) ? Optional.of(this) : Optional.empty();
        }

        public boolean test(Holder<T> p_252187_) {
            return this.tag.contains(p_252187_);
        }

        public String asPrintable() {
            return "#" + this.tag.key().location();
        }

        public HolderSet.Named<T> tag() {
            return this.tag;
        }
    }

    static record ResourceResult<T>(Holder.Reference<T> value) implements Result<T> {
        ResourceResult(Holder.Reference<T> value) {
            this.value = value;
        }

        public Either<Holder.Reference<T>, HolderSet.Named<T>> unwrap() {
            return Either.left(this.value);
        }

        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> p_250007_) {
            return this.value.key().isFor(p_250007_) ? Optional.of(this) : Optional.empty();
        }

        public boolean test(Holder<T> p_249230_) {
            return p_249230_.equals(this.value);
        }

        public String asPrintable() {
            return this.value.key().location().toString();
        }

        public Holder.Reference<T> value() {
            return this.value;
        }
    }

    public static class Info<T> implements ArgumentTypeInfo<ResourceOrTagArgument<T>, Info<T>.Template> {
        public Info() {
        }

        public void serializeToNetwork(Info<T>.Template p_250419_, FriendlyByteBuf p_249726_) {
            p_249726_.writeResourceLocation(p_250419_.registryKey.location());
        }

        public Info<T>.Template deserializeFromNetwork(FriendlyByteBuf p_250205_) {
            ResourceLocation $$1 = p_250205_.readResourceLocation();
            return new Template(ResourceKey.createRegistryKey($$1));
        }

        public void serializeToJson(Info<T>.Template p_251957_, JsonObject p_249067_) {
            p_249067_.addProperty("registry", p_251957_.registryKey.location().toString());
        }

        public Info<T>.Template unpack(ResourceOrTagArgument<T> p_252206_) {
            return new Template(p_252206_.registryKey);
        }

        public final class Template implements ArgumentTypeInfo.Template<ResourceOrTagArgument<T>> {
            final ResourceKey<? extends Registry<T>> registryKey;

            Template(ResourceKey<? extends Registry<T>> p_250107_) {
                this.registryKey = p_250107_;
            }

            public ResourceOrTagArgument<T> instantiate(CommandBuildContext p_251386_) {
                return new ResourceOrTagArgument(p_251386_, this.registryKey);
            }

            public ArgumentTypeInfo<ResourceOrTagArgument<T>, ?> type() {
                return Info.this;
            }
        }
    }
}
