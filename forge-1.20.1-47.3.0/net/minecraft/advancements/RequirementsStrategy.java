//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import java.util.Collection;
import java.util.Iterator;

public interface RequirementsStrategy {
    RequirementsStrategy AND = (p_15984_) -> {
        String[][] $$1 = new String[p_15984_.size()][];
        int $$2 = 0;

        String $$3;
        for(Iterator var3 = p_15984_.iterator(); var3.hasNext(); $$1[$$2++] = new String[]{$$3}) {
            $$3 = (String)var3.next();
        }

        return $$1;
    };
    RequirementsStrategy OR = (p_15982_) -> {
        return new String[][]{(String[])p_15982_.toArray(new String[0])};
    };

    String[][] createRequirements(Collection<String> var1);
}
