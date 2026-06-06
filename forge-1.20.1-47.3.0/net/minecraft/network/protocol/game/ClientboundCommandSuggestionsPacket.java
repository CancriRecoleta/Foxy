//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.protocol.game;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;

public class ClientboundCommandSuggestionsPacket implements Packet<ClientGamePacketListener> {
    private final int id;
    private final Suggestions suggestions;

    public ClientboundCommandSuggestionsPacket(int p_131846_, Suggestions p_131847_) {
        this.id = p_131846_;
        this.suggestions = p_131847_;
    }

    public ClientboundCommandSuggestionsPacket(FriendlyByteBuf p_178790_) {
        this.id = p_178790_.readVarInt();
        int $$1 = p_178790_.readVarInt();
        int $$2 = p_178790_.readVarInt();
        StringRange $$3 = StringRange.between($$1, $$1 + $$2);
        List<Suggestion> $$4 = p_178790_.readList((p_178793_) -> {
            String $$2 = p_178793_.readUtf();
            Component $$3x = (Component)p_178793_.readNullable(FriendlyByteBuf::readComponent);
            return new Suggestion($$3, $$2, $$3x);
        });
        this.suggestions = new Suggestions($$3, $$4);
    }

    public void write(FriendlyByteBuf p_131856_) {
        p_131856_.writeVarInt(this.id);
        p_131856_.writeVarInt(this.suggestions.getRange().getStart());
        p_131856_.writeVarInt(this.suggestions.getRange().getLength());
        p_131856_.writeCollection(this.suggestions.getList(), (p_237617_, p_237618_) -> {
            p_237617_.writeUtf(p_237618_.getText());
            p_237617_.writeNullable(p_237618_.getTooltip(), (p_237614_, p_237615_) -> {
                p_237614_.writeComponent(ComponentUtils.fromMessage(p_237615_));
            });
        });
    }

    public void handle(ClientGamePacketListener p_131853_) {
        p_131853_.handleCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}
