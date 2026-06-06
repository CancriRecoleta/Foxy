//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class ReloadCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ReloadCommand() {
    }

    public static void reloadPacks(Collection<String> p_138236_, CommandSourceStack p_138237_) {
        p_138237_.getServer().reloadResources(p_138236_).exceptionally((p_138234_) -> {
            LOGGER.warn("Failed to execute reload", p_138234_);
            p_138237_.sendFailure(Component.translatable("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> discoverNewPacks(PackRepository p_138223_, WorldData p_138224_, Collection<String> p_138225_) {
        p_138223_.reload();
        Collection<String> $$3 = Lists.newArrayList(p_138225_);
        Collection<String> $$4 = p_138224_.getDataConfiguration().dataPacks().getDisabled();
        Iterator var5 = p_138223_.getAvailableIds().iterator();

        while(var5.hasNext()) {
            String $$5 = (String)var5.next();
            if (!$$4.contains($$5) && !$$3.contains($$5)) {
                $$3.add($$5);
            }
        }

        return $$3;
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_138227_) {
        p_138227_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires((p_138231_) -> {
            return p_138231_.hasPermission(2);
        })).executes((p_288528_) -> {
            CommandSourceStack $$1 = (CommandSourceStack)p_288528_.getSource();
            MinecraftServer $$2 = $$1.getServer();
            PackRepository $$3 = $$2.getPackRepository();
            WorldData $$4 = $$2.getWorldData();
            Collection<String> $$5 = $$3.getSelectedIds();
            Collection<String> $$6 = discoverNewPacks($$3, $$4, $$5);
            $$1.sendSuccess(() -> {
                return Component.translatable("commands.reload.success");
            }, true);
            reloadPacks($$6, $$1);
            return 0;
        }));
    }
}
