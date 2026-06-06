//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.level;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.eventbus.api.Cancelable;

public class NoteBlockEvent extends BlockEvent {
    private int noteId;

    protected NoteBlockEvent(Level world, BlockPos pos, BlockState state, int note) {
        super(world, pos, state);
        this.noteId = note;
    }

    public Note getNote() {
        return net.minecraftforge.event.level.NoteBlockEvent.Note.fromId(this.noteId);
    }

    public Octave getOctave() {
        return net.minecraftforge.event.level.NoteBlockEvent.Octave.fromId(this.noteId);
    }

    public int getVanillaNoteId() {
        return this.noteId;
    }

    public void setNote(Note note, Octave octave) {
        Preconditions.checkArgument(octave != net.minecraftforge.event.level.NoteBlockEvent.Octave.HIGH || note == net.minecraftforge.event.level.NoteBlockEvent.Note.F_SHARP, "Octave.HIGH is only valid for Note.F_SHARP!");
        this.noteId = note.ordinal() + octave.ordinal() * 12;
    }

    public static enum Note {
        F_SHARP,
        G,
        G_SHARP,
        A,
        A_SHARP,
        B,
        C,
        C_SHARP,
        D,
        D_SHARP,
        E,
        F;

        private static final Note[] values = values();

        private Note() {
        }

        static Note fromId(int id) {
            return values[id % 12];
        }
    }

    public static enum Octave {
        LOW,
        MID,
        HIGH;

        private Octave() {
        }

        static Octave fromId(int id) {
            return id < 12 ? LOW : (id == 24 ? HIGH : MID);
        }
    }

    @Cancelable
    public static class Change extends NoteBlockEvent {
        private final Note oldNote;
        private final Octave oldOctave;

        public Change(Level world, BlockPos pos, BlockState state, int oldNote, int newNote) {
            super(world, pos, state, newNote);
            this.oldNote = net.minecraftforge.event.level.NoteBlockEvent.Note.fromId(oldNote);
            this.oldOctave = net.minecraftforge.event.level.NoteBlockEvent.Octave.fromId(oldNote);
        }

        public Note getOldNote() {
            return this.oldNote;
        }

        public Octave getOldOctave() {
            return this.oldOctave;
        }
    }

    @Cancelable
    public static class Play extends NoteBlockEvent {
        private NoteBlockInstrument instrument;

        public Play(Level world, BlockPos pos, BlockState state, int note, NoteBlockInstrument instrument) {
            super(world, pos, state, note);
            this.instrument = instrument;
        }

        public NoteBlockInstrument getInstrument() {
            return this.instrument;
        }

        public void setInstrument(NoteBlockInstrument instrument) {
            this.instrument = instrument;
        }
    }
}
