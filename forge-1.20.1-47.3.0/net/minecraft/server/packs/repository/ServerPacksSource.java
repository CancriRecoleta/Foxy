//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.repository;

import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModLoader;

public class ServerPacksSource extends BuiltInPackSource {
    private static final PackMetadataSection VERSION_METADATA_SECTION;
    private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION;
    private static final BuiltInMetadata BUILT_IN_METADATA;
    private static final Component VANILLA_NAME;
    private static final ResourceLocation PACKS_DIR;

    public ServerPacksSource() {
        super(PackType.SERVER_DATA, createVanillaPackSource(), PACKS_DIR);
    }

    public static VanillaPackResources createVanillaPackSource() {
        return (new VanillaPackResourcesBuilder()).setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build();
    }

    protected Component getPackTitle(String p_249692_) {
        return Component.literal(p_249692_);
    }

    @Nullable
    protected Pack createVanillaPack(PackResources p_250283_) {
        return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, false, (p_248248_) -> {
            return p_250283_;
        }, PackType.SERVER_DATA, Position.BOTTOM, PackSource.BUILT_IN);
    }

    @Nullable
    protected Pack createBuiltinPack(String p_250596_, Pack.ResourcesSupplier p_249625_, Component p_249043_) {
        return Pack.readMetaAndCreate(p_250596_, p_249043_, false, p_249625_, PackType.SERVER_DATA, Position.TOP, PackSource.FEATURE);
    }

    public static PackRepository createPackRepository(Path p_251569_) {
        PackRepository packRepository = new PackRepository(new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(p_251569_, PackType.SERVER_DATA, PackSource.WORLD)});
        ModLoader var10000 = ModLoader.get();
        PackType var10003 = PackType.SERVER_DATA;
        Objects.requireNonNull(packRepository);
        var10000.postEvent(new AddPackFindersEvent(var10003, packRepository::addPackFinder));
        return packRepository;
    }

    public static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess p_250213_) {
        return createPackRepository(p_250213_.getLevelPath(LevelResource.DATAPACK_DIR));
    }

    static {
        VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("dataPack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA));
        FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
        BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION);
        VANILLA_NAME = Component.translatable("dataPack.vanilla.name");
        PACKS_DIR = new ResourceLocation("minecraft", "datapacks");
    }
}
