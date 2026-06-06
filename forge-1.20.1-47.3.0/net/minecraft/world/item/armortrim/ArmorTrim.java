//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.armortrim;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class ArmorTrim {
    public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((p_267058_) -> {
        return p_267058_.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply(p_267058_, ArmorTrim::new);
    });
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String TAG_TRIM_ID = "Trim";
    private static final Component UPGRADE_TITLE;
    private final Holder<TrimMaterial> material;
    private final Holder<TrimPattern> pattern;
    private final Function<ArmorMaterial, ResourceLocation> innerTexture;
    private final Function<ArmorMaterial, ResourceLocation> outerTexture;

    public ArmorTrim(Holder<TrimMaterial> p_267249_, Holder<TrimPattern> p_267212_) {
        this.material = p_267249_;
        this.pattern = p_267212_;
        this.innerTexture = Util.memoize((p_267934_) -> {
            ResourceLocation $$2 = ((TrimPattern)p_267212_.value()).assetId();
            String $$3 = this.getColorPaletteSuffix(p_267934_);
            return $$2.withPath((p_266737_) -> {
                return "trims/models/armor/" + p_266737_ + "_leggings_" + $$3;
            });
        });
        this.outerTexture = Util.memoize((p_267932_) -> {
            ResourceLocation $$2 = ((TrimPattern)p_267212_.value()).assetId();
            String $$3 = this.getColorPaletteSuffix(p_267932_);
            return $$2.withPath((p_266864_) -> {
                return "trims/models/armor/" + p_266864_ + "_" + $$3;
            });
        });
    }

    private String getColorPaletteSuffix(ArmorMaterial p_268122_) {
        Map<ArmorMaterials, String> $$1 = ((TrimMaterial)this.material.value()).overrideArmorMaterials();
        return p_268122_ instanceof ArmorMaterials && $$1.containsKey(p_268122_) ? (String)$$1.get(p_268122_) : ((TrimMaterial)this.material.value()).assetName();
    }

    public boolean hasPatternAndMaterial(Holder<TrimPattern> p_266942_, Holder<TrimMaterial> p_267247_) {
        return p_266942_ == this.pattern && p_267247_ == this.material;
    }

    public Holder<TrimPattern> pattern() {
        return this.pattern;
    }

    public Holder<TrimMaterial> material() {
        return this.material;
    }

    public ResourceLocation innerTexture(ArmorMaterial p_268043_) {
        return (ResourceLocation)this.innerTexture.apply(p_268043_);
    }

    public ResourceLocation outerTexture(ArmorMaterial p_268143_) {
        return (ResourceLocation)this.outerTexture.apply(p_268143_);
    }

    public boolean equals(Object p_267123_) {
        if (!(p_267123_ instanceof ArmorTrim $$1)) {
            return false;
        } else {
            return $$1.pattern == this.pattern && $$1.material == this.material;
        }
    }

    public static boolean setTrim(RegistryAccess p_267181_, ItemStack p_266994_, ArmorTrim p_267002_) {
        if (p_266994_.is(ItemTags.TRIMMABLE_ARMOR)) {
            p_266994_.getOrCreateTag().put("Trim", (Tag)CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)p_267181_), p_267002_).result().orElseThrow());
            return true;
        } else {
            return false;
        }
    }

    public static Optional<ArmorTrim> getTrim(RegistryAccess p_266952_, ItemStack p_266766_) {
        if (p_266766_.is(ItemTags.TRIMMABLE_ARMOR) && p_266766_.getTag() != null && p_266766_.getTag().contains("Trim")) {
            CompoundTag $$2 = p_266766_.getTagElement("Trim");
            DataResult var10000 = CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, (HolderLookup.Provider)p_266952_), $$2);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            ArmorTrim $$3 = (ArmorTrim)var10000.resultOrPartial(var10001::error).orElse((Object)null);
            return Optional.ofNullable($$3);
        } else {
            return Optional.empty();
        }
    }

    public static void appendUpgradeHoverText(ItemStack p_266761_, RegistryAccess p_266979_, List<Component> p_267199_) {
        Optional<ArmorTrim> $$3 = getTrim(p_266979_, p_266761_);
        if ($$3.isPresent()) {
            ArmorTrim $$4 = (ArmorTrim)$$3.get();
            p_267199_.add(UPGRADE_TITLE);
            p_267199_.add(CommonComponents.space().append(((TrimPattern)$$4.pattern().value()).copyWithStyle($$4.material())));
            p_267199_.add(CommonComponents.space().append(((TrimMaterial)$$4.material().value()).description()));
        }

    }

    static {
        UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
    }
}
