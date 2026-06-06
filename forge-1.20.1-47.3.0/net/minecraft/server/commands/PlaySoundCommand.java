//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(Component.translatable("commands.playsound.failed"));

    public PlaySoundCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_138157_) {
        RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> $$1 = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
        SoundSource[] var2 = SoundSource.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            SoundSource $$2 = var2[var4];
            $$1.then(source($$2));
        }

        p_138157_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires((p_138159_) -> {
            return p_138159_.hasPermission(2);
        })).then($$1));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource p_138152_) {
        return (LiteralArgumentBuilder)Commands.literal(p_138152_.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_138180_) -> {
            return playSound((CommandSourceStack)p_138180_.getSource(), EntityArgument.getPlayers(p_138180_, "targets"), ResourceLocationArgument.getId(p_138180_, "sound"), p_138152_, ((CommandSourceStack)p_138180_.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((p_138177_) -> {
            return playSound((CommandSourceStack)p_138177_.getSource(), EntityArgument.getPlayers(p_138177_, "targets"), ResourceLocationArgument.getId(p_138177_, "sound"), p_138152_, Vec3Argument.getVec3(p_138177_, "pos"), 1.0F, 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((p_138174_) -> {
            return playSound((CommandSourceStack)p_138174_.getSource(), EntityArgument.getPlayers(p_138174_, "targets"), ResourceLocationArgument.getId(p_138174_, "sound"), p_138152_, Vec3Argument.getVec3(p_138174_, "pos"), (Float)p_138174_.getArgument("volume", Float.class), 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((p_138171_) -> {
            return playSound((CommandSourceStack)p_138171_.getSource(), EntityArgument.getPlayers(p_138171_, "targets"), ResourceLocationArgument.getId(p_138171_, "sound"), p_138152_, Vec3Argument.getVec3(p_138171_, "pos"), (Float)p_138171_.getArgument("volume", Float.class), (Float)p_138171_.getArgument("pitch", Float.class), 0.0F);
        })).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((p_138155_) -> {
            return playSound((CommandSourceStack)p_138155_.getSource(), EntityArgument.getPlayers(p_138155_, "targets"), ResourceLocationArgument.getId(p_138155_, "sound"), p_138152_, Vec3Argument.getVec3(p_138155_, "pos"), (Float)p_138155_.getArgument("volume", Float.class), (Float)p_138155_.getArgument("pitch", Float.class), (Float)p_138155_.getArgument("minVolume", Float.class));
        }))))));
    }

    private static int playSound(CommandSourceStack p_138161_, Collection<ServerPlayer> p_138162_, ResourceLocation p_138163_, SoundSource p_138164_, Vec3 p_138165_, float p_138166_, float p_138167_, float p_138168_) throws CommandSyntaxException {
        Holder<SoundEvent> $$8 = Holder.direct(SoundEvent.createVariableRangeEvent(p_138163_));
        double $$9 = (double)Mth.square(((SoundEvent)$$8.value()).getRange(p_138166_));
        int $$10 = 0;
        long $$11 = p_138161_.getLevel().getRandom().nextLong();
        Iterator var14 = p_138162_.iterator();

        while(true) {
            ServerPlayer $$12;
            Vec3 $$17;
            float $$18;
            while(true) {
                if (!var14.hasNext()) {
                    if ($$10 == 0) {
                        throw ERROR_TOO_FAR.create();
                    }

                    if (p_138162_.size() == 1) {
                        p_138161_.sendSuccess(() -> {
                            return Component.translatable("commands.playsound.success.single", p_138163_, ((ServerPlayer)p_138162_.iterator().next()).getDisplayName());
                        }, true);
                    } else {
                        p_138161_.sendSuccess(() -> {
                            return Component.translatable("commands.playsound.success.multiple", p_138163_, p_138162_.size());
                        }, true);
                    }

                    return $$10;
                }

                $$12 = (ServerPlayer)var14.next();
                double $$13 = p_138165_.x - $$12.getX();
                double $$14 = p_138165_.y - $$12.getY();
                double $$15 = p_138165_.z - $$12.getZ();
                double $$16 = $$13 * $$13 + $$14 * $$14 + $$15 * $$15;
                $$17 = p_138165_;
                $$18 = p_138166_;
                if (!($$16 > $$9)) {
                    break;
                }

                if (!(p_138168_ <= 0.0F)) {
                    double $$19 = Math.sqrt($$16);
                    $$17 = new Vec3($$12.getX() + $$13 / $$19 * 2.0, $$12.getY() + $$14 / $$19 * 2.0, $$12.getZ() + $$15 / $$19 * 2.0);
                    $$18 = p_138168_;
                    break;
                }
            }

            $$12.connection.send(new ClientboundSoundPacket($$8, p_138164_, $$17.x(), $$17.y(), $$17.z(), $$18, p_138167_, $$11));
            ++$$10;
        }
    }
}
