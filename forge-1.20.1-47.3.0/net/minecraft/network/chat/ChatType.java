//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public record ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
    public static final Codec<ChatType> CODEC = RecordCodecBuilder.create((p_240514_) -> {
        return p_240514_.group(ChatTypeDecoration.CODEC.fieldOf("chat").forGetter(ChatType::chat), ChatTypeDecoration.CODEC.fieldOf("narration").forGetter(ChatType::narration)).apply(p_240514_, ChatType::new);
    });
    public static final ChatTypeDecoration DEFAULT_CHAT_DECORATION = ChatTypeDecoration.withSender("chat.type.text");
    public static final ResourceKey<ChatType> CHAT = create("chat");
    public static final ResourceKey<ChatType> SAY_COMMAND = create("say_command");
    public static final ResourceKey<ChatType> MSG_COMMAND_INCOMING = create("msg_command_incoming");
    public static final ResourceKey<ChatType> MSG_COMMAND_OUTGOING = create("msg_command_outgoing");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_INCOMING = create("team_msg_command_incoming");
    public static final ResourceKey<ChatType> TEAM_MSG_COMMAND_OUTGOING = create("team_msg_command_outgoing");
    public static final ResourceKey<ChatType> EMOTE_COMMAND = create("emote_command");

    public ChatType(ChatTypeDecoration chat, ChatTypeDecoration narration) {
        this.chat = chat;
        this.narration = narration;
    }

    private static ResourceKey<ChatType> create(String p_237024_) {
        return ResourceKey.create(Registries.CHAT_TYPE, new ResourceLocation(p_237024_));
    }

    public static void bootstrap(BootstapContext<ChatType> p_256390_) {
        p_256390_.register(CHAT, new ChatType(DEFAULT_CHAT_DECORATION, ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(SAY_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.announcement"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.incomingDirectMessage("commands.message.display.incoming"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.outgoingDirectMessage("commands.message.display.outgoing"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(TEAM_MSG_COMMAND_INCOMING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.text"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(TEAM_MSG_COMMAND_OUTGOING, new ChatType(ChatTypeDecoration.teamMessage("chat.type.team.sent"), ChatTypeDecoration.withSender("chat.type.text.narrate")));
        p_256390_.register(EMOTE_COMMAND, new ChatType(ChatTypeDecoration.withSender("chat.type.emote"), ChatTypeDecoration.withSender("chat.type.emote")));
    }

    public static Bound bind(ResourceKey<ChatType> p_241279_, Entity p_241483_) {
        return bind(p_241279_, p_241483_.level().registryAccess(), p_241483_.getDisplayName());
    }

    public static Bound bind(ResourceKey<ChatType> p_241345_, CommandSourceStack p_241466_) {
        return bind(p_241345_, p_241466_.registryAccess(), p_241466_.getDisplayName());
    }

    public static Bound bind(ResourceKey<ChatType> p_241284_, RegistryAccess p_241373_, Component p_241455_) {
        Registry<ChatType> $$3 = p_241373_.registryOrThrow(Registries.CHAT_TYPE);
        return ((ChatType)$$3.getOrThrow(p_241284_)).bind(p_241455_);
    }

    public Bound bind(Component p_241506_) {
        return new Bound(this, p_241506_);
    }

    public ChatTypeDecoration chat() {
        return this.chat;
    }

    public ChatTypeDecoration narration() {
        return this.narration;
    }

    public static record Bound(ChatType chatType, Component name, @Nullable Component targetName) {
        Bound(ChatType p_241377_, Component p_241447_) {
            this(p_241377_, p_241447_, (Component)null);
        }

        public Bound(ChatType chatType, Component name, @Nullable Component targetName) {
            this.chatType = chatType;
            this.name = name;
            this.targetName = targetName;
        }

        public Component decorate(Component p_241411_) {
            return this.chatType.chat().decorate(p_241411_, this);
        }

        public Component decorateNarration(Component p_241354_) {
            return this.chatType.narration().decorate(p_241354_, this);
        }

        public Bound withTargetName(Component p_241530_) {
            return new Bound(this.chatType, this.name, p_241530_);
        }

        public BoundNetwork toNetwork(RegistryAccess p_241362_) {
            Registry<ChatType> $$1 = p_241362_.registryOrThrow(Registries.CHAT_TYPE);
            return new BoundNetwork($$1.getId(this.chatType), this.name, this.targetName);
        }

        public ChatType chatType() {
            return this.chatType;
        }

        public Component name() {
            return this.name;
        }

        @Nullable
        public Component targetName() {
            return this.targetName;
        }
    }

    public static record BoundNetwork(int chatType, Component name, @Nullable Component targetName) {
        public BoundNetwork(FriendlyByteBuf p_241341_) {
            this(p_241341_.readVarInt(), p_241341_.readComponent(), (Component)p_241341_.readNullable(FriendlyByteBuf::readComponent));
        }

        public BoundNetwork(int chatType, Component name, @Nullable Component targetName) {
            this.chatType = chatType;
            this.name = name;
            this.targetName = targetName;
        }

        public void write(FriendlyByteBuf p_241522_) {
            p_241522_.writeVarInt(this.chatType);
            p_241522_.writeComponent(this.name);
            p_241522_.writeNullable(this.targetName, FriendlyByteBuf::writeComponent);
        }

        public Optional<Bound> resolve(RegistryAccess p_242936_) {
            Registry<ChatType> $$1 = p_242936_.registryOrThrow(Registries.CHAT_TYPE);
            ChatType $$2 = (ChatType)$$1.byId(this.chatType);
            return Optional.ofNullable($$2).map((p_242929_) -> {
                return new Bound(p_242929_, this.name, this.targetName);
            });
        }

        public int chatType() {
            return this.chatType;
        }

        public Component name() {
            return this.name;
        }

        @Nullable
        public Component targetName() {
            return this.targetName;
        }
    }
}
