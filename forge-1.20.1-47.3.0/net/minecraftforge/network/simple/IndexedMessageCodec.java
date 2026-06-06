//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network.simple;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class IndexedMessageCodec {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
    private final Short2ObjectArrayMap<MessageHandler<?>> indicies;
    private final Object2ObjectArrayMap<Class<?>, MessageHandler<?>> types;
    private final NetworkInstance networkInstance;

    public IndexedMessageCodec() {
        this((NetworkInstance)null);
    }

    public IndexedMessageCodec(NetworkInstance instance) {
        this.indicies = new Short2ObjectArrayMap();
        this.types = new Object2ObjectArrayMap();
        this.networkInstance = instance;
    }

    public <MSG> MessageHandler<MSG> findMessageType(MSG msgToReply) {
        return (MessageHandler)this.types.get(msgToReply.getClass());
    }

    <MSG> MessageHandler<MSG> findIndex(short i) {
        return (MessageHandler)this.indicies.get(i);
    }

    private static <M> void tryDecode(FriendlyByteBuf payload, Supplier<NetworkEvent.Context> context, int payloadIndex, MessageHandler<M> codec) {
        codec.decoder.map((d) -> {
            return d.apply(payload);
        }).map((p) -> {
            if (payloadIndex != Integer.MIN_VALUE) {
                codec.getLoginIndexSetter().ifPresent((f) -> {
                    f.accept(p, payloadIndex);
                });
            }

            return p;
        }).ifPresent((m) -> {
            codec.messageConsumer.accept(m, context);
        });
    }

    private static <M> int tryEncode(FriendlyByteBuf target, M message, MessageHandler<M> codec) {
        codec.encoder.ifPresent((encoder) -> {
            target.writeByte(codec.index & 255);
            encoder.accept(message, target);
        });
        return (Integer)((Function)codec.loginIndexGetter.orElse((m) -> {
            return Integer.MIN_VALUE;
        })).apply(message);
    }

    public <MSG> int build(MSG message, FriendlyByteBuf target) {
        MessageHandler<MSG> messageHandler = (MessageHandler)this.types.get(message.getClass());
        if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid message {} on channel {}", message.getClass().getName(), Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            throw new IllegalArgumentException("Invalid message " + message.getClass().getName());
        } else {
            return tryEncode(target, message, messageHandler);
        }
    }

    void consume(FriendlyByteBuf payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
        if (payload != null && payload.isReadable()) {
            short discriminator = payload.readUnsignedByte();
            MessageHandler<?> messageHandler = (MessageHandler)this.indicies.get(discriminator);
            if (messageHandler == null) {
                LOGGER.error(SIMPLENET, "Received invalid discriminator byte {} on channel {}", discriminator, Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            } else {
                NetworkHooks.validatePacketDirection(((NetworkEvent.Context)context.get()).getDirection(), messageHandler.networkDirection, ((NetworkEvent.Context)context.get()).getNetworkManager());
                tryDecode(payload, context, payloadIndex, messageHandler);
            }
        } else {
            LOGGER.error(SIMPLENET, "Received empty payload on channel {}", Optional.ofNullable(this.networkInstance).map(NetworkInstance::getChannelName).map(Objects::toString).orElse("MISSING CHANNEL"));
            if (!HandshakeHandler.packetNeedsResponse(((NetworkEvent.Context)context.get()).getNetworkManager(), payloadIndex)) {
                ((NetworkEvent.Context)context.get()).setPacketHandled(true);
            }

        }
    }

    <MSG> MessageHandler<MSG> addCodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> networkDirection) {
        return new MessageHandler(index, messageType, encoder, decoder, messageConsumer, networkDirection);
    }

    class MessageHandler<MSG> {
        private final Optional<BiConsumer<MSG, FriendlyByteBuf>> encoder;
        private final Optional<Function<FriendlyByteBuf, MSG>> decoder;
        private final int index;
        private final BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer;
        private final Class<MSG> messageType;
        private final Optional<NetworkDirection> networkDirection;
        private Optional<BiConsumer<MSG, Integer>> loginIndexSetter;
        private Optional<Function<MSG, Integer>> loginIndexGetter;

        public MessageHandler(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> networkDirection) {
            this.index = index;
            this.messageType = messageType;
            this.encoder = Optional.ofNullable(encoder);
            this.decoder = Optional.ofNullable(decoder);
            this.messageConsumer = messageConsumer;
            this.networkDirection = networkDirection;
            this.loginIndexGetter = Optional.empty();
            this.loginIndexSetter = Optional.empty();
            IndexedMessageCodec.this.indicies.put((short)(index & 255), this);
            IndexedMessageCodec.this.types.put(messageType, this);
        }

        void setLoginIndexSetter(BiConsumer<MSG, Integer> loginIndexSetter) {
            this.loginIndexSetter = Optional.of(loginIndexSetter);
        }

        Optional<BiConsumer<MSG, Integer>> getLoginIndexSetter() {
            return this.loginIndexSetter;
        }

        void setLoginIndexGetter(Function<MSG, Integer> loginIndexGetter) {
            this.loginIndexGetter = Optional.of(loginIndexGetter);
        }

        public Optional<Function<MSG, Integer>> getLoginIndexGetter() {
            return this.loginIndexGetter;
        }

        MSG newInstance() {
            try {
                return this.messageType.newInstance();
            } catch (IllegalAccessException | InstantiationException var2) {
                ReflectiveOperationException e = var2;
                IndexedMessageCodec.LOGGER.error("Invalid login message", e);
                throw new RuntimeException(e);
            }
        }
    }
}
