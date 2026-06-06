//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;

public class ItemEvent extends EntityEvent {
    private final ItemEntity itemEntity;

    public ItemEvent(ItemEntity itemEntity) {
        super(itemEntity);
        this.itemEntity = itemEntity;
    }

    public ItemEntity getEntity() {
        return this.itemEntity;
    }
}
