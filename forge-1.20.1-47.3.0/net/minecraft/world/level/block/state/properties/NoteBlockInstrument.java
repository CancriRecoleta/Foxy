//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.state.properties;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;

public enum NoteBlockInstrument implements StringRepresentable {
    HARP("harp", SoundEvents.NOTE_BLOCK_HARP, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    BASEDRUM("basedrum", SoundEvents.NOTE_BLOCK_BASEDRUM, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    SNARE("snare", SoundEvents.NOTE_BLOCK_SNARE, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    HAT("hat", SoundEvents.NOTE_BLOCK_HAT, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    BASS("bass", SoundEvents.NOTE_BLOCK_BASS, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    FLUTE("flute", SoundEvents.NOTE_BLOCK_FLUTE, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    BELL("bell", SoundEvents.NOTE_BLOCK_BELL, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    GUITAR("guitar", SoundEvents.NOTE_BLOCK_GUITAR, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    CHIME("chime", SoundEvents.NOTE_BLOCK_CHIME, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    XYLOPHONE("xylophone", SoundEvents.NOTE_BLOCK_XYLOPHONE, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    IRON_XYLOPHONE("iron_xylophone", SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    COW_BELL("cow_bell", SoundEvents.NOTE_BLOCK_COW_BELL, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    DIDGERIDOO("didgeridoo", SoundEvents.NOTE_BLOCK_DIDGERIDOO, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    BIT("bit", SoundEvents.NOTE_BLOCK_BIT, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    BANJO("banjo", SoundEvents.NOTE_BLOCK_BANJO, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    PLING("pling", SoundEvents.NOTE_BLOCK_PLING, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK),
    ZOMBIE("zombie", SoundEvents.NOTE_BLOCK_IMITATE_ZOMBIE, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    SKELETON("skeleton", SoundEvents.NOTE_BLOCK_IMITATE_SKELETON, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    CREEPER("creeper", SoundEvents.NOTE_BLOCK_IMITATE_CREEPER, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    DRAGON("dragon", SoundEvents.NOTE_BLOCK_IMITATE_ENDER_DRAGON, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    WITHER_SKELETON("wither_skeleton", SoundEvents.NOTE_BLOCK_IMITATE_WITHER_SKELETON, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    PIGLIN("piglin", SoundEvents.NOTE_BLOCK_IMITATE_PIGLIN, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.MOB_HEAD),
    CUSTOM_HEAD("custom_head", SoundEvents.UI_BUTTON_CLICK, net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.CUSTOM);

    private final String name;
    private final Holder<SoundEvent> soundEvent;
    private final Type type;

    private NoteBlockInstrument(String p_263425_, Holder p_263341_, Type p_263322_) {
        this.name = p_263425_;
        this.soundEvent = p_263341_;
        this.type = p_263322_;
    }

    public String getSerializedName() {
        return this.name;
    }

    public Holder<SoundEvent> getSoundEvent() {
        return this.soundEvent;
    }

    public boolean isTunable() {
        return this.type == net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK;
    }

    public boolean hasCustomSound() {
        return this.type == net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.CUSTOM;
    }

    public boolean worksAboveNoteBlock() {
        return this.type != net.minecraft.world.level.block.state.properties.NoteBlockInstrument.Type.BASE_BLOCK;
    }

    private static enum Type {
        BASE_BLOCK,
        MOB_HEAD,
        CUSTOM;

        private Type() {
        }
    }
}
