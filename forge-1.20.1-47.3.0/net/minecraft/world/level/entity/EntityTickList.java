//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityTickList {
    private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap();
    private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap();
    @Nullable
    private Int2ObjectMap<Entity> iterated;

    public EntityTickList() {
    }

    private void ensureActiveIsNotIterated() {
        if (this.iterated == this.active) {
            this.passive.clear();
            ObjectIterator var1 = Int2ObjectMaps.fastIterable(this.active).iterator();

            while(var1.hasNext()) {
                Int2ObjectMap.Entry<Entity> $$0 = (Int2ObjectMap.Entry)var1.next();
                this.passive.put($$0.getIntKey(), (Entity)$$0.getValue());
            }

            Int2ObjectMap<Entity> $$1 = this.active;
            this.active = this.passive;
            this.passive = $$1;
        }

    }

    public void add(Entity p_156909_) {
        this.ensureActiveIsNotIterated();
        this.active.put(p_156909_.getId(), p_156909_);
    }

    public void remove(Entity p_156913_) {
        this.ensureActiveIsNotIterated();
        this.active.remove(p_156913_.getId());
    }

    public boolean contains(Entity p_156915_) {
        return this.active.containsKey(p_156915_.getId());
    }

    public void forEach(Consumer<Entity> p_156911_) {
        if (this.iterated != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        } else {
            this.iterated = this.active;

            try {
                ObjectIterator var2 = this.active.values().iterator();

                while(var2.hasNext()) {
                    Entity $$1 = (Entity)var2.next();
                    p_156911_.accept($$1);
                }
            } finally {
                this.iterated = null;
            }

        }
    }
}
