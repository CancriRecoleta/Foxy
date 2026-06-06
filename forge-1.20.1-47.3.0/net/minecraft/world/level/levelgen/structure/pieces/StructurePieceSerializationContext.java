//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pieces;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record StructurePieceSerializationContext(ResourceManager resourceManager, RegistryAccess registryAccess, StructureTemplateManager structureTemplateManager) {
    public StructurePieceSerializationContext(ResourceManager resourceManager, RegistryAccess registryAccess, StructureTemplateManager structureTemplateManager) {
        this.resourceManager = resourceManager;
        this.registryAccess = registryAccess;
        this.structureTemplateManager = structureTemplateManager;
    }

    public static StructurePieceSerializationContext fromLevel(ServerLevel p_192771_) {
        MinecraftServer $$1 = p_192771_.getServer();
        return new StructurePieceSerializationContext($$1.getResourceManager(), $$1.registryAccess(), $$1.getStructureManager());
    }

    public ResourceManager resourceManager() {
        return this.resourceManager;
    }

    public RegistryAccess registryAccess() {
        return this.registryAccess;
    }

    public StructureTemplateManager structureTemplateManager() {
        return this.structureTemplateManager;
    }
}
