//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;

public record TrimPattern(ResourceLocation assetId, Holder<Item> templateItem, Component description) {
    public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create((p_267105_) -> {
        return p_267105_.group(ResourceLocation.CODEC.fieldOf("asset_id").forGetter(TrimPattern::assetId), RegistryFixedCodec.create(Registries.ITEM).fieldOf("template_item").forGetter(TrimPattern::templateItem), ExtraCodecs.COMPONENT.fieldOf("description").forGetter(TrimPattern::description)).apply(p_267105_, TrimPattern::new);
    });
    public static final Codec<Holder<TrimPattern>> CODEC;

    public TrimPattern(ResourceLocation assetId, Holder<Item> templateItem, Component description) {
        this.assetId = assetId;
        this.templateItem = templateItem;
        this.description = description;
    }

    public Component copyWithStyle(Holder<TrimMaterial> p_266827_) {
        return this.description.copy().withStyle(((TrimMaterial)p_266827_.value()).description().getStyle());
    }

    public ResourceLocation assetId() {
        return this.assetId;
    }

    public Holder<Item> templateItem() {
        return this.templateItem;
    }

    public Component description() {
        return this.description;
    }

    static {
        CODEC = RegistryFileCodec.create(Registries.TRIM_PATTERN, DIRECT_CODEC);
    }
}
