//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
    private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((p_136717_) -> {
        return Component.translatable("clear.failed.single", p_136717_);
    });
    private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((p_136711_) -> {
        return Component.translatable("clear.failed.multiple", p_136711_);
    });

    public ClearInventoryCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_214421_, CommandBuildContext p_214422_) {
        p_214421_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires((p_136704_) -> {
            return p_136704_.hasPermission(2);
        })).executes((p_136721_) -> {
            return clearInventory((CommandSourceStack)p_136721_.getSource(), Collections.singleton(((CommandSourceStack)p_136721_.getSource()).getPlayerOrException()), (p_180029_) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_136719_) -> {
            return clearInventory((CommandSourceStack)p_136719_.getSource(), EntityArgument.getPlayers(p_136719_, "targets"), (p_180027_) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate(p_214422_)).executes((p_136715_) -> {
            return clearInventory((CommandSourceStack)p_136715_.getSource(), EntityArgument.getPlayers(p_136715_, "targets"), ItemPredicateArgument.getItemPredicate(p_136715_, "item"), -1);
        })).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_136702_) -> {
            return clearInventory((CommandSourceStack)p_136702_.getSource(), EntityArgument.getPlayers(p_136702_, "targets"), ItemPredicateArgument.getItemPredicate(p_136702_, "item"), IntegerArgumentType.getInteger(p_136702_, "maxCount"));
        })))));
    }

    private static int clearInventory(CommandSourceStack p_136706_, Collection<ServerPlayer> p_136707_, Predicate<ItemStack> p_136708_, int p_136709_) throws CommandSyntaxException {
        int $$4 = 0;
        Iterator var5 = p_136707_.iterator();

        while(var5.hasNext()) {
            ServerPlayer $$5 = (ServerPlayer)var5.next();
            $$4 += $$5.getInventory().clearOrCountMatchingItems(p_136708_, p_136709_, $$5.inventoryMenu.getCraftSlots());
            $$5.containerMenu.broadcastChanges();
            $$5.inventoryMenu.slotsChanged($$5.getInventory());
        }

        if ($$4 == 0) {
            if (p_136707_.size() == 1) {
                throw ERROR_SINGLE.create(((ServerPlayer)p_136707_.iterator().next()).getName());
            } else {
                throw ERROR_MULTIPLE.create(p_136707_.size());
            }
        } else {
            int $$6 = $$4;
            if (p_136709_ == 0) {
                if (p_136707_.size() == 1) {
                    p_136706_.sendSuccess(() -> {
                        return Component.translatable("commands.clear.test.single", $$6, ((ServerPlayer)p_136707_.iterator().next()).getDisplayName());
                    }, true);
                } else {
                    p_136706_.sendSuccess(() -> {
                        return Component.translatable("commands.clear.test.multiple", $$6, p_136707_.size());
                    }, true);
                }
            } else if (p_136707_.size() == 1) {
                p_136706_.sendSuccess(() -> {
                    return Component.translatable("commands.clear.success.single", $$6, ((ServerPlayer)p_136707_.iterator().next()).getDisplayName());
                }, true);
            } else {
                p_136706_.sendSuccess(() -> {
                    return Component.translatable("commands.clear.success.multiple", $$6, p_136707_.size());
                }, true);
            }

            return $$4;
        }
    }
}
