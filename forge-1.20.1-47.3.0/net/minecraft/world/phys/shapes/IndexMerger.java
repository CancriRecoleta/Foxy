//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IndexMerger {
    DoubleList getList();

    boolean forMergedIndexes(IndexConsumer var1);

    int size();

    public interface IndexConsumer {
        boolean merge(int var1, int var2, int var3);
    }
}
