//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String id;
    private final ResourcesSupplier resources;
    private final Component title;
    private final Component description;
    private final PackCompatibility compatibility;
    private final FeatureFlagSet requestedFeatures;
    private final Position defaultPosition;
    private final boolean required;
    private final boolean fixedPosition;
    private final boolean hidden;
    private final PackSource packSource;

    @Nullable
    public static Pack readMetaAndCreate(String p_249649_, Component p_248632_, boolean p_251594_, ResourcesSupplier p_252210_, PackType p_250595_, Position p_248706_, PackSource p_251233_) {
        Info pack$info = readPackInfo(p_249649_, p_252210_);
        return pack$info != null ? create(p_249649_, p_248632_, p_251594_, p_252210_, pack$info, p_250595_, p_248706_, false, p_251233_) : null;
    }

    public static Pack create(String p_252257_, Component p_248717_, boolean p_248811_, ResourcesSupplier p_248969_, Info p_251314_, PackType p_250264_, Position p_252110_, boolean p_250237_, PackSource p_248524_) {
        return new Pack(p_252257_, p_248811_, p_248969_, p_248717_, p_251314_, p_251314_.compatibility(p_250264_), p_252110_, p_250237_, p_248524_);
    }

    private Pack(String p_252218_, boolean p_248829_, ResourcesSupplier p_249377_, Component p_251718_, Info p_250162_, PackCompatibility p_250361_, Position p_251298_, boolean p_249753_, PackSource p_251608_) {
        this.id = p_252218_;
        this.resources = p_249377_;
        this.title = p_251718_;
        this.description = p_250162_.description();
        this.compatibility = p_250361_;
        this.requestedFeatures = p_250162_.requestedFeatures();
        this.required = p_248829_;
        this.defaultPosition = p_251298_;
        this.fixedPosition = p_249753_;
        this.packSource = p_251608_;
        this.hidden = p_250162_.hidden();
    }

    @Nullable
    public static Info readPackInfo(String p_250591_, ResourcesSupplier p_250739_) {
        try {
            PackResources packresources = p_250739_.open(p_250591_);

            FeatureFlagsMetadataSection featureflagsmetadatasection;
            label52: {
                Info var6;
                try {
                    PackMetadataSection packmetadatasection = (PackMetadataSection)packresources.getMetadataSection(PackMetadataSection.TYPE);
                    if (packmetadatasection == null) {
                        LOGGER.warn("Missing metadata in pack {}", p_250591_);
                        featureflagsmetadatasection = null;
                        break label52;
                    }

                    featureflagsmetadatasection = (FeatureFlagsMetadataSection)packresources.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
                    FeatureFlagSet featureflagset = featureflagsmetadatasection != null ? featureflagsmetadatasection.flags() : FeatureFlagSet.of();
                    var6 = new Info(packmetadatasection.getDescription(), packmetadatasection.getPackFormat(PackType.SERVER_DATA), packmetadatasection.getPackFormat(PackType.CLIENT_RESOURCES), featureflagset, packresources.isHidden());
                } catch (Throwable var8) {
                    if (packresources != null) {
                        try {
                            packresources.close();
                        } catch (Throwable var7) {
                            var8.addSuppressed(var7);
                        }
                    }

                    throw var8;
                }

                if (packresources != null) {
                    packresources.close();
                }

                return var6;
            }

            if (packresources != null) {
                packresources.close();
            }

            return featureflagsmetadatasection;
        } catch (Exception var9) {
            Exception exception = var9;
            LOGGER.warn("Failed to read pack metadata", exception);
            return null;
        }
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public Component getChatLink(boolean p_10438_) {
        return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(Component.literal(this.id))).withStyle((p_10441_) -> {
            return p_10441_.withColor(p_10438_ ? ChatFormatting.GREEN : ChatFormatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(this.description)));
        });
    }

    public PackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public FeatureFlagSet getRequestedFeatures() {
        return this.requestedFeatures;
    }

    public PackResources open() {
        return this.resources.open(this.id);
    }

    public String getId() {
        return this.id;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isFixedPosition() {
        return this.fixedPosition;
    }

    public Position getDefaultPosition() {
        return this.defaultPosition;
    }

    public PackSource getPackSource() {
        return this.packSource;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean equals(Object p_10448_) {
        if (this == p_10448_) {
            return true;
        } else if (!(p_10448_ instanceof Pack)) {
            return false;
        } else {
            Pack pack = (Pack)p_10448_;
            return this.id.equals(pack.id);
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    @FunctionalInterface
    public interface ResourcesSupplier {
        PackResources open(String var1);
    }

    public static record Info(Component description, int dataFormat, int resourceFormat, FeatureFlagSet requestedFeatures, boolean hidden) {
        public Info(Component description, int format, FeatureFlagSet requestedFeatures) {
            this(description, format, format, requestedFeatures, false);
        }

        public Info(Component description, int dataFormat, int resourceFormat, FeatureFlagSet requestedFeatures, boolean hidden) {
            this.description = description;
            this.dataFormat = dataFormat;
            this.resourceFormat = resourceFormat;
            this.requestedFeatures = requestedFeatures;
            this.hidden = hidden;
        }

        public int getFormat(PackType type) {
            return type == PackType.SERVER_DATA ? this.dataFormat : this.resourceFormat;
        }

        public PackCompatibility compatibility(PackType p_249204_) {
            return PackCompatibility.forFormat(this.getFormat(p_249204_), p_249204_);
        }

        public Component description() {
            return this.description;
        }

        public int dataFormat() {
            return this.dataFormat;
        }

        public int resourceFormat() {
            return this.resourceFormat;
        }

        public FeatureFlagSet requestedFeatures() {
            return this.requestedFeatures;
        }

        public boolean hidden() {
            return this.hidden;
        }
    }

    public static enum Position {
        TOP,
        BOTTOM;

        private Position() {
        }

        public <T> int insert(List<T> p_10471_, T p_10472_, Function<T, Pack> p_10473_, boolean p_10474_) {
            Position pack$position = p_10474_ ? this.opposite() : this;
            int i;
            Pack pack;
            if (pack$position == BOTTOM) {
                for(i = 0; i < p_10471_.size(); ++i) {
                    pack = (Pack)p_10473_.apply(p_10471_.get(i));
                    if (!pack.isFixedPosition() || pack.getDefaultPosition() != this) {
                        break;
                    }
                }

                p_10471_.add(i, p_10472_);
                return i;
            } else {
                for(i = p_10471_.size() - 1; i >= 0; --i) {
                    pack = (Pack)p_10473_.apply(p_10471_.get(i));
                    if (!pack.isFixedPosition() || pack.getDefaultPosition() != this) {
                        break;
                    }
                }

                p_10471_.add(i + 1, p_10472_);
                return i + 1;
            }
        }

        public Position opposite() {
            return this == TOP ? BOTTOM : TOP;
        }
    }
}
