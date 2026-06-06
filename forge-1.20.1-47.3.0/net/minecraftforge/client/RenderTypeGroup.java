//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;

public record RenderTypeGroup(RenderType block, RenderType entity, RenderType entityFabulous) {
    public static RenderTypeGroup EMPTY = new RenderTypeGroup((RenderType)null, (RenderType)null, (RenderType)null);

    public RenderTypeGroup(RenderType block, RenderType entity, RenderType entityFabulous) {
        if (block == null == (entity == null) && block == null == (entityFabulous == null)) {
            this.block = block;
            this.entity = entity;
            this.entityFabulous = entityFabulous;
        } else {
            throw new IllegalArgumentException("The render types in a group must either be all null, or all non-null.");
        }
    }

    public RenderTypeGroup(RenderType block, RenderType entity) {
        this(block, entity, entity);
    }

    public boolean isEmpty() {
        return this.block == null;
    }

    public RenderType block() {
        return this.block;
    }

    public RenderType entity() {
        return this.entity;
    }

    public RenderType entityFabulous() {
        return this.entityFabulous;
    }
}
