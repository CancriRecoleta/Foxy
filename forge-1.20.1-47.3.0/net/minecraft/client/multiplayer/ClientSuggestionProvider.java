//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientSuggestionProvider implements SharedSuggestionProvider {
    private final ClientPacketListener connection;
    private final Minecraft minecraft;
    private int pendingSuggestionsId = -1;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestionsFuture;
    private final Set<String> customCompletionSuggestions = new HashSet();

    public ClientSuggestionProvider(ClientPacketListener p_105165_, Minecraft p_105166_) {
        this.connection = p_105165_;
        this.minecraft = p_105166_;
    }

    public Collection<String> getOnlinePlayerNames() {
        List<String> $$0 = Lists.newArrayList();
        Iterator var2 = this.connection.getOnlinePlayers().iterator();

        while(var2.hasNext()) {
            PlayerInfo $$1 = (PlayerInfo)var2.next();
            $$0.add($$1.getProfile().getName());
        }

        return $$0;
    }

    public Collection<String> getCustomTabSugggestions() {
        if (this.customCompletionSuggestions.isEmpty()) {
            return this.getOnlinePlayerNames();
        } else {
            Set<String> $$0 = new HashSet(this.getOnlinePlayerNames());
            $$0.addAll(this.customCompletionSuggestions);
            return $$0;
        }
    }

    public Collection<String> getSelectedEntities() {
        return (Collection)(this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == Type.ENTITY ? Collections.singleton(((EntityHitResult)this.minecraft.hitResult).getEntity().getStringUUID()) : Collections.emptyList());
    }

    public Collection<String> getAllTeams() {
        return this.connection.getLevel().getScoreboard().getTeamNames();
    }

    public Stream<ResourceLocation> getAvailableSounds() {
        return this.minecraft.getSoundManager().getAvailableSounds().stream();
    }

    public Stream<ResourceLocation> getRecipeNames() {
        return this.connection.getRecipeManager().getRecipeIds();
    }

    public boolean hasPermission(int p_105178_) {
        LocalPlayer $$1 = this.minecraft.player;
        return $$1 != null ? $$1.hasPermissions(p_105178_) : p_105178_ == 0;
    }

    public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> p_212429_, SharedSuggestionProvider.ElementSuggestionType p_212430_, SuggestionsBuilder p_212431_, CommandContext<?> p_212432_) {
        return (CompletableFuture)this.registryAccess().registry(p_212429_).map((p_212427_) -> {
            this.suggestRegistryElements(p_212427_, p_212430_, p_212431_);
            return p_212431_.buildFuture();
        }).orElseGet(() -> {
            return this.customSuggestion(p_212432_);
        });
    }

    public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> p_212423_) {
        if (this.pendingSuggestionsFuture != null) {
            this.pendingSuggestionsFuture.cancel(false);
        }

        this.pendingSuggestionsFuture = new CompletableFuture();
        int $$1 = ++this.pendingSuggestionsId;
        this.connection.send((Packet)(new ServerboundCommandSuggestionPacket($$1, p_212423_.getInput())));
        return this.pendingSuggestionsFuture;
    }

    private static String prettyPrint(double p_105168_) {
        return String.format(Locale.ROOT, "%.2f", p_105168_);
    }

    private static String prettyPrint(int p_105170_) {
        return Integer.toString(p_105170_);
    }

    public Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 != null && $$0.getType() == Type.BLOCK) {
            BlockPos $$1 = ((BlockHitResult)$$0).getBlockPos();
            return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint($$1.getX()), prettyPrint($$1.getY()), prettyPrint($$1.getZ())));
        } else {
            return SharedSuggestionProvider.super.getRelevantCoordinates();
        }
    }

    public Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates() {
        HitResult $$0 = this.minecraft.hitResult;
        if ($$0 != null && $$0.getType() == Type.BLOCK) {
            Vec3 $$1 = $$0.getLocation();
            return Collections.singleton(new SharedSuggestionProvider.TextCoordinates(prettyPrint($$1.x), prettyPrint($$1.y), prettyPrint($$1.z)));
        } else {
            return SharedSuggestionProvider.super.getAbsoluteCoordinates();
        }
    }

    public Set<ResourceKey<Level>> levels() {
        return this.connection.levels();
    }

    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    public FeatureFlagSet enabledFeatures() {
        return this.connection.enabledFeatures();
    }

    public void completeCustomSuggestions(int p_105172_, Suggestions p_105173_) {
        if (p_105172_ == this.pendingSuggestionsId) {
            this.pendingSuggestionsFuture.complete(p_105173_);
            this.pendingSuggestionsFuture = null;
            this.pendingSuggestionsId = -1;
        }

    }

    public void modifyCustomCompletions(ClientboundCustomChatCompletionsPacket.Action p_240810_, List<String> p_240765_) {
        switch (p_240810_) {
            case ADD:
                this.customCompletionSuggestions.addAll(p_240765_);
                break;
            case REMOVE:
                Set var10001 = this.customCompletionSuggestions;
                Objects.requireNonNull(var10001);
                p_240765_.forEach(var10001::remove);
                break;
            case SET:
                this.customCompletionSuggestions.clear();
                this.customCompletionSuggestions.addAll(p_240765_);
        }

    }
}
