//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.world;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.StructureModifier.Phase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ModifiableStructureInfo {
    @NotNull
    private final @NotNull StructureInfo originalStructureInfo;
    @Nullable
    private @Nullable StructureInfo modifiedStructureInfo = null;

    public ModifiableStructureInfo(@NotNull @NotNull StructureInfo originalStructureInfo) {
        this.originalStructureInfo = originalStructureInfo;
    }

    @NotNull
    public @NotNull StructureInfo get() {
        return this.modifiedStructureInfo == null ? this.originalStructureInfo : this.modifiedStructureInfo;
    }

    @NotNull
    public @NotNull StructureInfo getOriginalStructureInfo() {
        return this.originalStructureInfo;
    }

    @Nullable
    public @Nullable StructureInfo getModifiedStructureInfo() {
        return this.modifiedStructureInfo;
    }

    @Internal
    public void applyStructureModifiers(Holder<Structure> structure, List<StructureModifier> structureModifiers) {
        if (this.modifiedStructureInfo != null) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Structure %s already modified", structure));
        } else {
            StructureInfo original = this.getOriginalStructureInfo();
            StructureInfo.Builder builder = net.minecraftforge.common.world.ModifiableStructureInfo.StructureInfo.Builder.copyOf(original);
            StructureModifier.Phase[] var5 = Phase.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                StructureModifier.Phase phase = var5[var7];
                Iterator var9 = structureModifiers.iterator();

                while(var9.hasNext()) {
                    StructureModifier modifier = (StructureModifier)var9.next();
                    modifier.modify(structure, phase, builder);
                }
            }

            this.modifiedStructureInfo = builder.build();
        }
    }

    public static record StructureInfo(Structure.StructureSettings structureSettings) {
        public StructureInfo(Structure.StructureSettings structureSettings) {
            this.structureSettings = structureSettings;
        }

        public Structure.StructureSettings structureSettings() {
            return this.structureSettings;
        }

        public static class Builder {
            private StructureSettingsBuilder structureSettings;

            public static Builder copyOf(StructureInfo original) {
                StructureSettingsBuilder structureBuilder = StructureSettingsBuilder.copyOf(original.structureSettings());
                return new Builder(structureBuilder);
            }

            private Builder(StructureSettingsBuilder structureSettings) {
                this.structureSettings = structureSettings;
            }

            public StructureInfo build() {
                return new StructureInfo(this.structureSettings.build());
            }

            public StructureSettingsBuilder getStructureSettings() {
                return this.structureSettings;
            }
        }
    }
}
