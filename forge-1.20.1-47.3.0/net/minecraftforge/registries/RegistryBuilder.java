//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.registries;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RegistryBuilder<T> {
    private static final int MAX_ID = 2147483646;
    private ResourceLocation registryName;
    private ResourceLocation optionalDefaultKey;
    private int minId = 0;
    private int maxId = 2147483646;
    private List<IForgeRegistry.AddCallback<T>> addCallback = Lists.newArrayList();
    private List<IForgeRegistry.ClearCallback<T>> clearCallback = Lists.newArrayList();
    private List<IForgeRegistry.CreateCallback<T>> createCallback = Lists.newArrayList();
    private List<IForgeRegistry.ValidateCallback<T>> validateCallback = Lists.newArrayList();
    private List<IForgeRegistry.BakeCallback<T>> bakeCallback = Lists.newArrayList();
    private boolean saveToDisc = true;
    private boolean sync = true;
    private boolean allowOverrides = true;
    private boolean allowModifications = false;
    private boolean hasWrapper = false;
    private IForgeRegistry.MissingFactory<T> missingFactory;
    private Set<ResourceLocation> legacyNames = new HashSet();
    private @Nullable Function<T, Holder.Reference<T>> intrusiveHolderCallback = null;

    public RegistryBuilder() {
    }

    public static <T> RegistryBuilder<T> of() {
        return new RegistryBuilder();
    }

    public static <T> RegistryBuilder<T> of(String name) {
        return of(new ResourceLocation(name));
    }

    public static <T> RegistryBuilder<T> of(ResourceLocation name) {
        return (new RegistryBuilder()).setName(name);
    }

    public RegistryBuilder<T> setName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    public RegistryBuilder<T> setIDRange(int min, int max) {
        this.minId = Math.max(min, 0);
        this.maxId = Math.min(max, 2147483646);
        return this;
    }

    public RegistryBuilder<T> setMaxID(int max) {
        return this.setIDRange(0, max);
    }

    public RegistryBuilder<T> setDefaultKey(ResourceLocation key) {
        this.optionalDefaultKey = key;
        return this;
    }

    public RegistryBuilder<T> addCallback(Object inst) {
        if (inst instanceof IForgeRegistry.AddCallback) {
            this.add((IForgeRegistry.AddCallback)inst);
        }

        if (inst instanceof IForgeRegistry.ClearCallback) {
            this.add((IForgeRegistry.ClearCallback)inst);
        }

        if (inst instanceof IForgeRegistry.CreateCallback) {
            this.add((IForgeRegistry.CreateCallback)inst);
        }

        if (inst instanceof IForgeRegistry.ValidateCallback) {
            this.add((IForgeRegistry.ValidateCallback)inst);
        }

        if (inst instanceof IForgeRegistry.BakeCallback) {
            this.add((IForgeRegistry.BakeCallback)inst);
        }

        if (inst instanceof IForgeRegistry.MissingFactory) {
            this.set((IForgeRegistry.MissingFactory)inst);
        }

        return this;
    }

    public RegistryBuilder<T> add(IForgeRegistry.AddCallback<T> add) {
        this.addCallback.add(add);
        return this;
    }

    public RegistryBuilder<T> onAdd(IForgeRegistry.AddCallback<T> add) {
        return this.add(add);
    }

    public RegistryBuilder<T> add(IForgeRegistry.ClearCallback<T> clear) {
        this.clearCallback.add(clear);
        return this;
    }

    public RegistryBuilder<T> onClear(IForgeRegistry.ClearCallback<T> clear) {
        return this.add(clear);
    }

    public RegistryBuilder<T> add(IForgeRegistry.CreateCallback<T> create) {
        this.createCallback.add(create);
        return this;
    }

    public RegistryBuilder<T> onCreate(IForgeRegistry.CreateCallback<T> create) {
        return this.add(create);
    }

    public RegistryBuilder<T> add(IForgeRegistry.ValidateCallback<T> validate) {
        this.validateCallback.add(validate);
        return this;
    }

    public RegistryBuilder<T> onValidate(IForgeRegistry.ValidateCallback<T> validate) {
        return this.add(validate);
    }

    public RegistryBuilder<T> add(IForgeRegistry.BakeCallback<T> bake) {
        this.bakeCallback.add(bake);
        return this;
    }

    public RegistryBuilder<T> onBake(IForgeRegistry.BakeCallback<T> bake) {
        return this.add(bake);
    }

    public RegistryBuilder<T> set(IForgeRegistry.MissingFactory<T> missing) {
        this.missingFactory = missing;
        return this;
    }

    public RegistryBuilder<T> missing(IForgeRegistry.MissingFactory<T> missing) {
        return this.set(missing);
    }

    public RegistryBuilder<T> disableSaving() {
        this.saveToDisc = false;
        return this;
    }

    public RegistryBuilder<T> disableSync() {
        this.sync = false;
        return this;
    }

    public RegistryBuilder<T> disableOverrides() {
        this.allowOverrides = false;
        return this;
    }

    public RegistryBuilder<T> allowModification() {
        this.allowModifications = true;
        return this;
    }

    RegistryBuilder<T> hasWrapper() {
        this.hasWrapper = true;
        return this;
    }

    public RegistryBuilder<T> legacyName(String name) {
        return this.legacyName(new ResourceLocation(name));
    }

    public RegistryBuilder<T> legacyName(ResourceLocation name) {
        this.legacyNames.add(name);
        return this;
    }

    RegistryBuilder<T> intrusiveHolderCallback(Function<T, Holder.Reference<T>> intrusiveHolderCallback) {
        this.intrusiveHolderCallback = intrusiveHolderCallback;
        return this;
    }

    public RegistryBuilder<T> hasTags() {
        this.hasWrapper();
        return this;
    }

    IForgeRegistry<T> create() {
        if (this.hasWrapper) {
            if (this.getDefault() == null) {
                this.addCallback(new NamespacedWrapper.Factory());
            } else {
                this.addCallback(new NamespacedDefaultedWrapper.Factory());
            }
        }

        return RegistryManager.ACTIVE.createRegistry(this.registryName, this);
    }

    @Nullable
    public IForgeRegistry.@Nullable AddCallback<T> getAdd() {
        if (this.addCallback.isEmpty()) {
            return null;
        } else {
            return this.addCallback.size() == 1 ? (IForgeRegistry.AddCallback)this.addCallback.get(0) : (owner, stage, id, key, obj, old) -> {
                Iterator var7 = this.addCallback.iterator();

                while(var7.hasNext()) {
                    IForgeRegistry.AddCallback<T> cb = (IForgeRegistry.AddCallback)var7.next();
                    cb.onAdd(owner, stage, id, key, obj, old);
                }

            };
        }
    }

    @Nullable
    public IForgeRegistry.@Nullable ClearCallback<T> getClear() {
        if (this.clearCallback.isEmpty()) {
            return null;
        } else {
            return this.clearCallback.size() == 1 ? (IForgeRegistry.ClearCallback)this.clearCallback.get(0) : (owner, stage) -> {
                Iterator var3 = this.clearCallback.iterator();

                while(var3.hasNext()) {
                    IForgeRegistry.ClearCallback<T> cb = (IForgeRegistry.ClearCallback)var3.next();
                    cb.onClear(owner, stage);
                }

            };
        }
    }

    @Nullable
    public IForgeRegistry.@Nullable CreateCallback<T> getCreate() {
        if (this.createCallback.isEmpty()) {
            return null;
        } else {
            return this.createCallback.size() == 1 ? (IForgeRegistry.CreateCallback)this.createCallback.get(0) : (owner, stage) -> {
                Iterator var3 = this.createCallback.iterator();

                while(var3.hasNext()) {
                    IForgeRegistry.CreateCallback<T> cb = (IForgeRegistry.CreateCallback)var3.next();
                    cb.onCreate(owner, stage);
                }

            };
        }
    }

    @Nullable
    public IForgeRegistry.@Nullable ValidateCallback<T> getValidate() {
        if (this.validateCallback.isEmpty()) {
            return null;
        } else {
            return this.validateCallback.size() == 1 ? (IForgeRegistry.ValidateCallback)this.validateCallback.get(0) : (owner, stage, id, key, obj) -> {
                Iterator var6 = this.validateCallback.iterator();

                while(var6.hasNext()) {
                    IForgeRegistry.ValidateCallback<T> cb = (IForgeRegistry.ValidateCallback)var6.next();
                    cb.onValidate(owner, stage, id, key, obj);
                }

            };
        }
    }

    @Nullable
    public IForgeRegistry.@Nullable BakeCallback<T> getBake() {
        if (this.bakeCallback.isEmpty()) {
            return null;
        } else {
            return this.bakeCallback.size() == 1 ? (IForgeRegistry.BakeCallback)this.bakeCallback.get(0) : (owner, stage) -> {
                Iterator var3 = this.bakeCallback.iterator();

                while(var3.hasNext()) {
                    IForgeRegistry.BakeCallback<T> cb = (IForgeRegistry.BakeCallback)var3.next();
                    cb.onBake(owner, stage);
                }

            };
        }
    }

    public @Nullable ResourceLocation getDefault() {
        return this.optionalDefaultKey;
    }

    public int getMinId() {
        return this.minId;
    }

    public int getMaxId() {
        return this.maxId;
    }

    public boolean getAllowOverrides() {
        return this.allowOverrides;
    }

    public boolean getAllowModifications() {
        return this.allowModifications;
    }

    @Nullable
    public IForgeRegistry.@Nullable MissingFactory<T> getMissingFactory() {
        return this.missingFactory;
    }

    public boolean getSaveToDisc() {
        return this.saveToDisc;
    }

    public boolean getSync() {
        return this.sync;
    }

    public Set<ResourceLocation> getLegacyNames() {
        return this.legacyNames;
    }

    Function<T, Holder.Reference<T>> getIntrusiveHolderCallback() {
        return this.intrusiveHolderCallback;
    }

    boolean getHasWrapper() {
        return this.hasWrapper;
    }
}
