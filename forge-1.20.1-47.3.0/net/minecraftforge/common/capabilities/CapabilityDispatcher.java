//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.capabilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher implements INBTSerializable<CompoundTag>, ICapabilityProvider {
    private final ICapabilityProvider[] caps;
    private final INBTSerializable<Tag>[] writers;
    private final String[] names;
    private final List<Runnable> listeners;

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners) {
        this(list, listeners, (ICapabilityProvider)null);
    }

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
        List<ICapabilityProvider> lstCaps = new ArrayList();
        List<INBTSerializable<Tag>> lstWriters = new ArrayList();
        List<String> lstNames = new ArrayList();
        this.listeners = listeners;
        if (parent != null) {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable)parent);
                lstNames.add("Parent");
            }
        }

        Iterator var7 = list.entrySet().iterator();

        while(var7.hasNext()) {
            Map.Entry<ResourceLocation, ICapabilityProvider> entry = (Map.Entry)var7.next();
            ICapabilityProvider prov = (ICapabilityProvider)entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable)prov);
                lstNames.add(((ResourceLocation)entry.getKey()).toString());
            }
        }

        this.caps = (ICapabilityProvider[])lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        this.writers = (INBTSerializable[])lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        this.names = (String[])lstNames.toArray(new String[lstNames.size()]);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        ICapabilityProvider[] var3 = this.caps;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            ICapabilityProvider c = var3[var5];
            LazyOptional<T> ret = c.getCapability(cap, side);
            if (ret == null) {
                throw new RuntimeException(String.format(Locale.ENGLISH, "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!", c.getClass().getTypeName()));
            }

            if (ret.isPresent()) {
                return ret;
            }
        }

        return LazyOptional.empty();
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        for(int x = 0; x < this.writers.length; ++x) {
            nbt.put(this.names[x], this.writers[x].serializeNBT());
        }

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        for(int x = 0; x < this.writers.length; ++x) {
            if (nbt.contains(this.names[x])) {
                this.writers[x].deserializeNBT(nbt.get(this.names[x]));
            }
        }

    }

    public boolean areCompatible(@Nullable CapabilityDispatcher other) {
        if (other == null) {
            return this.writers.length == 0;
        } else if (this.writers.length == 0) {
            return other.writers.length == 0;
        } else {
            return this.serializeNBT().equals(other.serializeNBT());
        }
    }

    public void invalidate() {
        this.listeners.forEach(Runnable::run);
    }
}
