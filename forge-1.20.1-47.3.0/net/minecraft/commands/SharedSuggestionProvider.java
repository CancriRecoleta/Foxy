//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;

public interface SharedSuggestionProvider {
    Collection<String> getOnlinePlayerNames();

    default Collection<String> getCustomTabSugggestions() {
        return this.getOnlinePlayerNames();
    }

    default Collection<String> getSelectedEntities() {
        return Collections.emptyList();
    }

    Collection<String> getAllTeams();

    Stream<ResourceLocation> getAvailableSounds();

    Stream<ResourceLocation> getRecipeNames();

    CompletableFuture<Suggestions> customSuggestion(CommandContext<?> var1);

    default Collection<TextCoordinates> getRelevantCoordinates() {
        return Collections.singleton(net.minecraft.commands.SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    default Collection<TextCoordinates> getAbsoluteCoordinates() {
        return Collections.singleton(net.minecraft.commands.SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    Set<ResourceKey<Level>> levels();

    RegistryAccess registryAccess();

    FeatureFlagSet enabledFeatures();

    default void suggestRegistryElements(Registry<?> p_212336_, ElementSuggestionType p_212337_, SuggestionsBuilder p_212338_) {
        if (p_212337_.shouldSuggestTags()) {
            suggestResource(p_212336_.getTagNames().map(TagKey::location), p_212338_, "#");
        }

        if (p_212337_.shouldSuggestElements()) {
            suggestResource((Iterable)p_212336_.keySet(), p_212338_);
        }

    }

    CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> var1, ElementSuggestionType var2, SuggestionsBuilder var3, CommandContext<?> var4);

    boolean hasPermission(int var1);

    static <T> void filterResources(Iterable<T> p_82945_, String p_82946_, Function<T, ResourceLocation> p_82947_, Consumer<T> p_82948_) {
        boolean $$4 = p_82946_.indexOf(58) > -1;
        Iterator var5 = p_82945_.iterator();

        while(true) {
            while(var5.hasNext()) {
                T $$5 = var5.next();
                ResourceLocation $$6 = (ResourceLocation)p_82947_.apply($$5);
                if ($$4) {
                    String $$7 = $$6.toString();
                    if (matchesSubStr(p_82946_, $$7)) {
                        p_82948_.accept($$5);
                    }
                } else if (matchesSubStr(p_82946_, $$6.getNamespace()) || $$6.getNamespace().equals("minecraft") && matchesSubStr(p_82946_, $$6.getPath())) {
                    p_82948_.accept($$5);
                }
            }

            return;
        }
    }

    static <T> void filterResources(Iterable<T> p_82939_, String p_82940_, String p_82941_, Function<T, ResourceLocation> p_82942_, Consumer<T> p_82943_) {
        if (p_82940_.isEmpty()) {
            p_82939_.forEach(p_82943_);
        } else {
            String $$5 = Strings.commonPrefix(p_82940_, p_82941_);
            if (!$$5.isEmpty()) {
                String $$6 = p_82940_.substring($$5.length());
                filterResources(p_82939_, $$6, p_82942_, p_82943_);
            }
        }

    }

    static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> p_82930_, SuggestionsBuilder p_82931_, String p_82932_) {
        String $$3 = p_82931_.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(p_82930_, $$3, p_82932_, (p_82985_) -> {
            return p_82985_;
        }, (p_82917_) -> {
            p_82931_.suggest(p_82932_ + p_82917_);
        });
        return p_82931_.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> p_205107_, SuggestionsBuilder p_205108_, String p_205109_) {
        Objects.requireNonNull(p_205107_);
        return suggestResource(p_205107_::iterator, p_205108_, p_205109_);
    }

    static CompletableFuture<Suggestions> suggestResource(Iterable<ResourceLocation> p_82927_, SuggestionsBuilder p_82928_) {
        String $$2 = p_82928_.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(p_82927_, $$2, (p_82966_) -> {
            return p_82966_;
        }, (p_82925_) -> {
            p_82928_.suggest(p_82925_.toString());
        });
        return p_82928_.buildFuture();
    }

    static <T> CompletableFuture<Suggestions> suggestResource(Iterable<T> p_82934_, SuggestionsBuilder p_82935_, Function<T, ResourceLocation> p_82936_, Function<T, Message> p_82937_) {
        String $$4 = p_82935_.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(p_82934_, $$4, p_82936_, (p_82922_) -> {
            p_82935_.suggest(((ResourceLocation)p_82936_.apply(p_82922_)).toString(), (Message)p_82937_.apply(p_82922_));
        });
        return p_82935_.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestResource(Stream<ResourceLocation> p_82958_, SuggestionsBuilder p_82959_) {
        Objects.requireNonNull(p_82958_);
        return suggestResource(p_82958_::iterator, p_82959_);
    }

    static <T> CompletableFuture<Suggestions> suggestResource(Stream<T> p_82961_, SuggestionsBuilder p_82962_, Function<T, ResourceLocation> p_82963_, Function<T, Message> p_82964_) {
        Objects.requireNonNull(p_82961_);
        return suggestResource(p_82961_::iterator, p_82962_, p_82963_, p_82964_);
    }

    static CompletableFuture<Suggestions> suggestCoordinates(String p_82953_, Collection<TextCoordinates> p_82954_, SuggestionsBuilder p_82955_, Predicate<String> p_82956_) {
        List<String> $$4 = Lists.newArrayList();
        if (Strings.isNullOrEmpty(p_82953_)) {
            Iterator var5 = p_82954_.iterator();

            while(var5.hasNext()) {
                TextCoordinates $$5 = (TextCoordinates)var5.next();
                String $$6 = $$5.x + " " + $$5.y + " " + $$5.z;
                if (p_82956_.test($$6)) {
                    $$4.add($$5.x);
                    $$4.add($$5.x + " " + $$5.y);
                    $$4.add($$6);
                }
            }
        } else {
            String[] $$7 = p_82953_.split(" ");
            String $$11;
            Iterator var10;
            TextCoordinates $$8;
            if ($$7.length == 1) {
                var10 = p_82954_.iterator();

                while(var10.hasNext()) {
                    $$8 = (TextCoordinates)var10.next();
                    $$11 = $$7[0] + " " + $$8.y + " " + $$8.z;
                    if (p_82956_.test($$11)) {
                        $$4.add($$7[0] + " " + $$8.y);
                        $$4.add($$11);
                    }
                }
            } else if ($$7.length == 2) {
                var10 = p_82954_.iterator();

                while(var10.hasNext()) {
                    $$8 = (TextCoordinates)var10.next();
                    $$11 = $$7[0] + " " + $$7[1] + " " + $$8.z;
                    if (p_82956_.test($$11)) {
                        $$4.add($$11);
                    }
                }
            }
        }

        return suggest((Iterable)$$4, p_82955_);
    }

    static CompletableFuture<Suggestions> suggest2DCoordinates(String p_82977_, Collection<TextCoordinates> p_82978_, SuggestionsBuilder p_82979_, Predicate<String> p_82980_) {
        List<String> $$4 = Lists.newArrayList();
        if (Strings.isNullOrEmpty(p_82977_)) {
            Iterator var5 = p_82978_.iterator();

            while(var5.hasNext()) {
                TextCoordinates $$5 = (TextCoordinates)var5.next();
                String $$6 = $$5.x + " " + $$5.z;
                if (p_82980_.test($$6)) {
                    $$4.add($$5.x);
                    $$4.add($$6);
                }
            }
        } else {
            String[] $$7 = p_82977_.split(" ");
            if ($$7.length == 1) {
                Iterator var10 = p_82978_.iterator();

                while(var10.hasNext()) {
                    TextCoordinates $$8 = (TextCoordinates)var10.next();
                    String $$9 = $$7[0] + " " + $$8.z;
                    if (p_82980_.test($$9)) {
                        $$4.add($$9);
                    }
                }
            }
        }

        return suggest((Iterable)$$4, p_82979_);
    }

    static CompletableFuture<Suggestions> suggest(Iterable<String> p_82971_, SuggestionsBuilder p_82972_) {
        String $$2 = p_82972_.getRemaining().toLowerCase(Locale.ROOT);
        Iterator var3 = p_82971_.iterator();

        while(var3.hasNext()) {
            String $$3 = (String)var3.next();
            if (matchesSubStr($$2, $$3.toLowerCase(Locale.ROOT))) {
                p_82972_.suggest($$3);
            }
        }

        return p_82972_.buildFuture();
    }

    static CompletableFuture<Suggestions> suggest(Stream<String> p_82982_, SuggestionsBuilder p_82983_) {
        String $$2 = p_82983_.getRemaining().toLowerCase(Locale.ROOT);
        Stream var10000 = p_82982_.filter((p_82975_) -> {
            return matchesSubStr($$2, p_82975_.toLowerCase(Locale.ROOT));
        });
        Objects.requireNonNull(p_82983_);
        var10000.forEach(p_82983_::suggest);
        return p_82983_.buildFuture();
    }

    static CompletableFuture<Suggestions> suggest(String[] p_82968_, SuggestionsBuilder p_82969_) {
        String $$2 = p_82969_.getRemaining().toLowerCase(Locale.ROOT);
        String[] var3 = p_82968_;
        int var4 = p_82968_.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String $$3 = var3[var5];
            if (matchesSubStr($$2, $$3.toLowerCase(Locale.ROOT))) {
                p_82969_.suggest($$3);
            }
        }

        return p_82969_.buildFuture();
    }

    static <T> CompletableFuture<Suggestions> suggest(Iterable<T> p_165917_, SuggestionsBuilder p_165918_, Function<T, String> p_165919_, Function<T, Message> p_165920_) {
        String $$4 = p_165918_.getRemaining().toLowerCase(Locale.ROOT);
        Iterator var5 = p_165917_.iterator();

        while(var5.hasNext()) {
            T $$5 = var5.next();
            String $$6 = (String)p_165919_.apply($$5);
            if (matchesSubStr($$4, $$6.toLowerCase(Locale.ROOT))) {
                p_165918_.suggest($$6, (Message)p_165920_.apply($$5));
            }
        }

        return p_165918_.buildFuture();
    }

    static boolean matchesSubStr(String p_82950_, String p_82951_) {
        for(int $$2 = 0; !p_82951_.startsWith(p_82950_, $$2); ++$$2) {
            $$2 = p_82951_.indexOf(95, $$2);
            if ($$2 < 0) {
                return false;
            }
        }

        return true;
    }

    public static class TextCoordinates {
        public static final TextCoordinates DEFAULT_LOCAL = new TextCoordinates("^", "^", "^");
        public static final TextCoordinates DEFAULT_GLOBAL = new TextCoordinates("~", "~", "~");
        public final String x;
        public final String y;
        public final String z;

        public TextCoordinates(String p_82994_, String p_82995_, String p_82996_) {
            this.x = p_82994_;
            this.y = p_82995_;
            this.z = p_82996_;
        }
    }

    public static enum ElementSuggestionType {
        TAGS,
        ELEMENTS,
        ALL;

        private ElementSuggestionType() {
        }

        public boolean shouldSuggestTags() {
            return this == TAGS || this == ALL;
        }

        public boolean shouldSuggestElements() {
            return this == ELEMENTS || this == ALL;
        }
    }
}
