//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CustomBossEvents {
    private final Map<ResourceLocation, CustomBossEvent> events = Maps.newHashMap();

    public CustomBossEvents() {
    }

    @Nullable
    public CustomBossEvent get(ResourceLocation p_136298_) {
        return (CustomBossEvent)this.events.get(p_136298_);
    }

    public CustomBossEvent create(ResourceLocation p_136300_, Component p_136301_) {
        CustomBossEvent $$2 = new CustomBossEvent(p_136300_, p_136301_);
        this.events.put(p_136300_, $$2);
        return $$2;
    }

    public void remove(CustomBossEvent p_136303_) {
        this.events.remove(p_136303_.getTextId());
    }

    public Collection<ResourceLocation> getIds() {
        return this.events.keySet();
    }

    public Collection<CustomBossEvent> getEvents() {
        return this.events.values();
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        Iterator var2 = this.events.values().iterator();

        while(var2.hasNext()) {
            CustomBossEvent $$1 = (CustomBossEvent)var2.next();
            $$0.put($$1.getTextId().toString(), $$1.save());
        }

        return $$0;
    }

    public void load(CompoundTag p_136296_) {
        Iterator var2 = p_136296_.getAllKeys().iterator();

        while(var2.hasNext()) {
            String $$1 = (String)var2.next();
            ResourceLocation $$2 = new ResourceLocation($$1);
            this.events.put($$2, CustomBossEvent.load(p_136296_.getCompound($$1), $$2));
        }

    }

    public void onPlayerConnect(ServerPlayer p_136294_) {
        Iterator var2 = this.events.values().iterator();

        while(var2.hasNext()) {
            CustomBossEvent $$1 = (CustomBossEvent)var2.next();
            $$1.onPlayerConnect(p_136294_);
        }

    }

    public void onPlayerDisconnect(ServerPlayer p_136306_) {
        Iterator var2 = this.events.values().iterator();

        while(var2.hasNext()) {
            CustomBossEvent $$1 = (CustomBossEvent)var2.next();
            $$1.onPlayerDisconnect(p_136306_);
        }

    }
}
