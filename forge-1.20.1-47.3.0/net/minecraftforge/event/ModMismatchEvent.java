//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ModMismatchEvent extends Event implements IModBusEvent {
    private final LevelStorageSource.LevelDirectory levelDirectory;
    private final HashMap<String, MismatchedVersionInfo> versionDifferences;
    private final HashMap<String, ModContainer> resolved;

    @Internal
    public ModMismatchEvent(LevelStorageSource.LevelDirectory levelDirectory, Map<String, ArtifactVersion> previousVersions, Map<String, ArtifactVersion> missingVersions) {
        this.levelDirectory = levelDirectory;
        this.resolved = new HashMap(previousVersions.size());
        this.versionDifferences = new HashMap();
        previousVersions.forEach((modId, version) -> {
            this.versionDifferences.put(modId, new MismatchedVersionInfo(version, (ArtifactVersion)ModList.get().getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getVersion).orElse((Object)null)));
        });
        missingVersions.forEach((modId, version) -> {
            this.versionDifferences.put(modId, new MismatchedVersionInfo(version, (ArtifactVersion)null));
        });
    }

    public LevelStorageSource.LevelDirectory getLevelDirectory() {
        return this.levelDirectory;
    }

    public @Nullable ArtifactVersion getPreviousVersion(String modId) {
        return this.versionDifferences.containsKey(modId) ? ((MismatchedVersionInfo)this.versionDifferences.get(modId)).oldVersion() : null;
    }

    public @Nullable ArtifactVersion getCurrentVersion(String modid) {
        return this.versionDifferences.containsKey(modid) ? ((MismatchedVersionInfo)this.versionDifferences.get(modid)).newVersion() : null;
    }

    public void markResolved(String modId) {
        ModContainer resolvedBy = ModLoadingContext.get().getActiveContainer();
        this.resolved.putIfAbsent(modId, resolvedBy);
    }

    public boolean wasResolved(String modId) {
        return this.resolved.containsKey(modId);
    }

    public Optional<MismatchedVersionInfo> getVersionDifference(String modid) {
        return Optional.ofNullable((MismatchedVersionInfo)this.versionDifferences.get(modid));
    }

    public Optional<ModContainer> getResolver(String modid) {
        return Optional.ofNullable((ModContainer)this.resolved.get(modid));
    }

    public boolean anyUnresolved() {
        return this.resolved.size() < this.versionDifferences.size();
    }

    public Stream<MismatchResolutionResult> getUnresolved() {
        return this.versionDifferences.keySet().stream().filter((modid) -> {
            return !this.resolved.containsKey(modid);
        }).map((unresolved) -> {
            return new MismatchResolutionResult(unresolved, (MismatchedVersionInfo)this.versionDifferences.get(unresolved), (ModContainer)null);
        }).sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public boolean anyResolved() {
        return !this.resolved.isEmpty();
    }

    public Stream<MismatchResolutionResult> getResolved() {
        return this.resolved.keySet().stream().map((modid) -> {
            return new MismatchResolutionResult(modid, (MismatchedVersionInfo)this.versionDifferences.get(modid), (ModContainer)this.resolved.get(modid));
        }).sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public static record MismatchedVersionInfo(ArtifactVersion oldVersion, @Nullable ArtifactVersion newVersion) {
        public MismatchedVersionInfo(ArtifactVersion oldVersion, @Nullable ArtifactVersion newVersion) {
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }

        public boolean isMissing() {
            return this.newVersion == null;
        }

        public boolean wasUpgrade() {
            if (this.newVersion == null) {
                return false;
            } else {
                return this.newVersion.compareTo(this.oldVersion) > 0;
            }
        }

        public ArtifactVersion oldVersion() {
            return this.oldVersion;
        }

        public @Nullable ArtifactVersion newVersion() {
            return this.newVersion;
        }
    }

    public static record MismatchResolutionResult(String modid, MismatchedVersionInfo versionDifference, @Nullable ModContainer resolver) {
        public MismatchResolutionResult(String modid, MismatchedVersionInfo versionDifference, @Nullable ModContainer resolver) {
            this.modid = modid;
            this.versionDifference = versionDifference;
            this.resolver = resolver;
        }

        public boolean wasSelfResolved() {
            return this.resolver != null && this.resolver.getModId().equals(this.modid);
        }

        public String modid() {
            return this.modid;
        }

        public MismatchedVersionInfo versionDifference() {
            return this.versionDifference;
        }

        public @Nullable ModContainer resolver() {
            return this.resolver;
        }
    }
}
