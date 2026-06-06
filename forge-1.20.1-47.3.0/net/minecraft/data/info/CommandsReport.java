//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.info;

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;

public class CommandsReport implements DataProvider {
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public CommandsReport(PackOutput p_256167_, CompletableFuture<HolderLookup.Provider> p_256506_) {
        this.output = p_256167_;
        this.registries = p_256506_;
    }

    public CompletableFuture<?> run(CachedOutput p_253721_) {
        Path $$1 = this.output.getOutputFolder(Target.REPORTS).resolve("commands.json");
        return this.registries.thenCompose((p_256367_) -> {
            CommandDispatcher<CommandSourceStack> $$3 = (new Commands(CommandSelection.ALL, Commands.createValidationContext(p_256367_))).getDispatcher();
            return DataProvider.saveStable(p_253721_, ArgumentUtils.serializeNodeToJson($$3, $$3.getRoot()), $$1);
        });
    }

    public final String getName() {
        return "Command Syntax";
    }
}
