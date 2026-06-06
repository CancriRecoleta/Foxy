//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisibilitySet {
    private static final int FACINGS = Direction.values().length;
    private final BitSet data;

    public VisibilitySet() {
        this.data = new BitSet(FACINGS * FACINGS);
    }

    public void add(Set<Direction> p_112991_) {
        Iterator var2 = p_112991_.iterator();

        while(var2.hasNext()) {
            Direction $$1 = (Direction)var2.next();
            Iterator var4 = p_112991_.iterator();

            while(var4.hasNext()) {
                Direction $$2 = (Direction)var4.next();
                this.set($$1, $$2, true);
            }
        }

    }

    public void set(Direction p_112987_, Direction p_112988_, boolean p_112989_) {
        this.data.set(p_112987_.ordinal() + p_112988_.ordinal() * FACINGS, p_112989_);
        this.data.set(p_112988_.ordinal() + p_112987_.ordinal() * FACINGS, p_112989_);
    }

    public void setAll(boolean p_112993_) {
        this.data.set(0, this.data.size(), p_112993_);
    }

    public boolean visibilityBetween(Direction p_112984_, Direction p_112985_) {
        return this.data.get(p_112984_.ordinal() + p_112985_.ordinal() * FACINGS);
    }

    public String toString() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append(' ');
        Direction[] var2 = Direction.values();
        int var3 = var2.length;

        int var4;
        Direction $$2;
        for(var4 = 0; var4 < var3; ++var4) {
            $$2 = var2[var4];
            $$0.append(' ').append($$2.toString().toUpperCase().charAt(0));
        }

        $$0.append('\n');
        var2 = Direction.values();
        var3 = var2.length;

        for(var4 = 0; var4 < var3; ++var4) {
            $$2 = var2[var4];
            $$0.append($$2.toString().toUpperCase().charAt(0));
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction $$3 = var6[var8];
                if ($$2 == $$3) {
                    $$0.append("  ");
                } else {
                    boolean $$4 = this.visibilityBetween($$2, $$3);
                    $$0.append(' ').append((char)($$4 ? 'Y' : 'n'));
                }
            }

            $$0.append('\n');
        }

        return $$0.toString();
    }
}
