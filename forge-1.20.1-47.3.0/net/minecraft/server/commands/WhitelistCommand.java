//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(Component.translatable("commands.whitelist.remove.failed"));

    public WhitelistCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_139202_) {
        p_139202_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires((p_139234_) -> {
            return p_139234_.hasPermission(3);
        })).then(Commands.literal("on").executes((p_139236_) -> {
            return enableWhitelist((CommandSourceStack)p_139236_.getSource());
        }))).then(Commands.literal("off").executes((p_139232_) -> {
            return disableWhitelist((CommandSourceStack)p_139232_.getSource());
        }))).then(Commands.literal("list").executes((p_139228_) -> {
            return showList((CommandSourceStack)p_139228_.getSource());
        }))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_139216_, p_139217_) -> {
            PlayerList $$2 = ((CommandSourceStack)p_139216_.getSource()).getServer().getPlayerList();
            return SharedSuggestionProvider.suggest($$2.getPlayers().stream().filter((p_289303_) -> {
                return !$$2.getWhiteList().isWhiteListed(p_289303_.getGameProfile());
            }).map((p_289304_) -> {
                return p_289304_.getGameProfile().getName();
            }), p_139217_);
        }).executes((p_139224_) -> {
            return addPlayers((CommandSourceStack)p_139224_.getSource(), GameProfileArgument.getGameProfiles(p_139224_, "targets"));
        })))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_139206_, p_139207_) -> {
            return SharedSuggestionProvider.suggest(((CommandSourceStack)p_139206_.getSource()).getServer().getPlayerList().getWhiteListNames(), p_139207_);
        }).executes((p_139214_) -> {
            return removePlayers((CommandSourceStack)p_139214_.getSource(), GameProfileArgument.getGameProfiles(p_139214_, "targets"));
        })))).then(Commands.literal("reload").executes((p_139204_) -> {
            return reload((CommandSourceStack)p_139204_.getSource());
        })));
    }

    private static int reload(CommandSourceStack p_139209_) {
        p_139209_.getServer().getPlayerList().reloadWhiteList();
        p_139209_.sendSuccess(() -> {
            return Component.translatable("commands.whitelist.reloaded");
        }, true);
        p_139209_.getServer().kickUnlistedPlayers(p_139209_);
        return 1;
    }

    private static int addPlayers(CommandSourceStack p_139211_, Collection<GameProfile> p_139212_) throws CommandSyntaxException {
        UserWhiteList $$2 = p_139211_.getServer().getPlayerList().getWhiteList();
        int $$3 = 0;
        Iterator var4 = p_139212_.iterator();

        while(var4.hasNext()) {
            GameProfile $$4 = (GameProfile)var4.next();
            if (!$$2.isWhiteListed($$4)) {
                UserWhiteListEntry $$5 = new UserWhiteListEntry($$4);
                $$2.add($$5);
                p_139211_.sendSuccess(() -> {
                    return Component.translatable("commands.whitelist.add.success", ComponentUtils.getDisplayName($$4));
                }, true);
                ++$$3;
            }
        }

        if ($$3 == 0) {
            throw ERROR_ALREADY_WHITELISTED.create();
        } else {
            return $$3;
        }
    }

    private static int removePlayers(CommandSourceStack p_139221_, Collection<GameProfile> p_139222_) throws CommandSyntaxException {
        UserWhiteList $$2 = p_139221_.getServer().getPlayerList().getWhiteList();
        int $$3 = 0;
        Iterator var4 = p_139222_.iterator();

        while(var4.hasNext()) {
            GameProfile $$4 = (GameProfile)var4.next();
            if ($$2.isWhiteListed($$4)) {
                UserWhiteListEntry $$5 = new UserWhiteListEntry($$4);
                $$2.remove($$5);
                p_139221_.sendSuccess(() -> {
                    return Component.translatable("commands.whitelist.remove.success", ComponentUtils.getDisplayName($$4));
                }, true);
                ++$$3;
            }
        }

        if ($$3 == 0) {
            throw ERROR_NOT_WHITELISTED.create();
        } else {
            p_139221_.getServer().kickUnlistedPlayers(p_139221_);
            return $$3;
        }
    }

    private static int enableWhitelist(CommandSourceStack p_139219_) throws CommandSyntaxException {
        PlayerList $$1 = p_139219_.getServer().getPlayerList();
        if ($$1.isUsingWhitelist()) {
            throw ERROR_ALREADY_ENABLED.create();
        } else {
            $$1.setUsingWhiteList(true);
            p_139219_.sendSuccess(() -> {
                return Component.translatable("commands.whitelist.enabled");
            }, true);
            p_139219_.getServer().kickUnlistedPlayers(p_139219_);
            return 1;
        }
    }

    private static int disableWhitelist(CommandSourceStack p_139226_) throws CommandSyntaxException {
        PlayerList $$1 = p_139226_.getServer().getPlayerList();
        if (!$$1.isUsingWhitelist()) {
            throw ERROR_ALREADY_DISABLED.create();
        } else {
            $$1.setUsingWhiteList(false);
            p_139226_.sendSuccess(() -> {
                return Component.translatable("commands.whitelist.disabled");
            }, true);
            return 1;
        }
    }

    private static int showList(CommandSourceStack p_139230_) {
        String[] $$1 = p_139230_.getServer().getPlayerList().getWhiteListNames();
        if ($$1.length == 0) {
            p_139230_.sendSuccess(() -> {
                return Component.translatable("commands.whitelist.none");
            }, false);
        } else {
            p_139230_.sendSuccess(() -> {
                return Component.translatable("commands.whitelist.list", $$1.length, String.join(", ", $$1));
            }, false);
        }

        return $$1.length;
    }
}
