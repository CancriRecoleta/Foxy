//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;

public class MapBanner {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Component name;

    public MapBanner(BlockPos p_77770_, DyeColor p_77771_, @Nullable Component p_77772_) {
        this.pos = p_77770_;
        this.color = p_77771_;
        this.name = p_77772_;
    }

    public static MapBanner load(CompoundTag p_77778_) {
        BlockPos $$1 = NbtUtils.readBlockPos(p_77778_.getCompound("Pos"));
        DyeColor $$2 = DyeColor.byName(p_77778_.getString("Color"), DyeColor.WHITE);
        Component $$3 = p_77778_.contains("Name") ? Serializer.fromJson(p_77778_.getString("Name")) : null;
        return new MapBanner($$1, $$2, $$3);
    }

    @Nullable
    public static MapBanner fromWorld(BlockGetter p_77775_, BlockPos p_77776_) {
        BlockEntity $$2 = p_77775_.getBlockEntity(p_77776_);
        if ($$2 instanceof BannerBlockEntity $$3) {
            DyeColor $$4 = $$3.getBaseColor();
            Component $$5 = $$3.hasCustomName() ? $$3.getCustomName() : null;
            return new MapBanner(p_77776_, $$4, $$5);
        } else {
            return null;
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public MapDecoration.Type getDecoration() {
        switch (this.color) {
            case WHITE:
                return Type.BANNER_WHITE;
            case ORANGE:
                return Type.BANNER_ORANGE;
            case MAGENTA:
                return Type.BANNER_MAGENTA;
            case LIGHT_BLUE:
                return Type.BANNER_LIGHT_BLUE;
            case YELLOW:
                return Type.BANNER_YELLOW;
            case LIME:
                return Type.BANNER_LIME;
            case PINK:
                return Type.BANNER_PINK;
            case GRAY:
                return Type.BANNER_GRAY;
            case LIGHT_GRAY:
                return Type.BANNER_LIGHT_GRAY;
            case CYAN:
                return Type.BANNER_CYAN;
            case PURPLE:
                return Type.BANNER_PURPLE;
            case BLUE:
                return Type.BANNER_BLUE;
            case BROWN:
                return Type.BANNER_BROWN;
            case GREEN:
                return Type.BANNER_GREEN;
            case RED:
                return Type.BANNER_RED;
            case BLACK:
            default:
                return Type.BANNER_BLACK;
        }
    }

    @Nullable
    public Component getName() {
        return this.name;
    }

    public boolean equals(Object p_77786_) {
        if (this == p_77786_) {
            return true;
        } else if (p_77786_ != null && this.getClass() == p_77786_.getClass()) {
            MapBanner $$1 = (MapBanner)p_77786_;
            return Objects.equals(this.pos, $$1.pos) && this.color == $$1.color && Objects.equals(this.name, $$1.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pos, this.color, this.name});
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.put("Pos", NbtUtils.writeBlockPos(this.pos));
        $$0.putString("Color", this.color.getName());
        if (this.name != null) {
            $$0.putString("Name", Serializer.toJson(this.name));
        }

        return $$0;
    }

    public String getId() {
        int var10000 = this.pos.getX();
        return "banner-" + var10000 + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}
