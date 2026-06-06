//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import java.util.List;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.ModLoadingPhase;
import net.minecraftforge.fml.ModLoadingState;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;

public class ForgeStatesProvider implements IModStateProvider {
    final ModLoadingState CREATE_REGISTRIES;
    final ModLoadingState OBJECT_HOLDERS;
    final ModLoadingState INJECT_CAPABILITIES;
    final ModLoadingState UNFREEZE;
    final ModLoadingState LOAD_REGISTRIES;
    final ModLoadingState FREEZE;
    final ModLoadingState NETLOCK;

    public ForgeStatesProvider() {
        this.CREATE_REGISTRIES = ModLoadingState.withInline("CREATE_REGISTRIES", "CONSTRUCT", ModLoadingPhase.GATHER, (ml) -> {
            RegistryManager.postNewRegistryEvent();
        });
        this.OBJECT_HOLDERS = ModLoadingState.withInline("OBJECT_HOLDERS", "CREATE_REGISTRIES", ModLoadingPhase.GATHER, (ml) -> {
            ObjectHolderRegistry.findObjectHolders();
        });
        this.INJECT_CAPABILITIES = ModLoadingState.withInline("INJECT_CAPABILITIES", "OBJECT_HOLDERS", ModLoadingPhase.GATHER, (ml) -> {
            CapabilityManager.INSTANCE.injectCapabilities(ml.getAllScanData());
        });
        this.UNFREEZE = ModLoadingState.withInline("UNFREEZE_DATA", "INJECT_CAPABILITIES", ModLoadingPhase.GATHER, (ml) -> {
            GameData.unfreezeData();
        });
        this.LOAD_REGISTRIES = ModLoadingState.withInline("LOAD_REGISTRIES", "UNFREEZE_DATA", ModLoadingPhase.GATHER, (ml) -> {
            GameData.postRegisterEvents();
        });
        this.FREEZE = ModLoadingState.withInline("FREEZE_DATA", "COMPLETE", ModLoadingPhase.COMPLETE, (ml) -> {
            GameData.freezeData();
        });
        this.NETLOCK = ModLoadingState.withInline("NETWORK_LOCK", "FREEZE_DATA", ModLoadingPhase.COMPLETE, (ml) -> {
            NetworkRegistry.lock();
        });
    }

    public List<IModLoadingState> getAllStates() {
        return List.of(this.CREATE_REGISTRIES, this.OBJECT_HOLDERS, this.INJECT_CAPABILITIES, this.UNFREEZE, this.LOAD_REGISTRIES, this.FREEZE, this.NETLOCK);
    }
}
