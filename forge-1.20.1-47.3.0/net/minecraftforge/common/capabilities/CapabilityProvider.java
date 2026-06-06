//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common.capabilities;

import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class CapabilityProvider<B extends ICapabilityProviderImpl<B>> implements ICapabilityProviderImpl<B> {
    @VisibleForTesting
    static boolean SUPPORTS_LAZY_CAPABILITIES = true;
    private final @NotNull Class<B> baseClass;
    private @Nullable CapabilityDispatcher capabilities;
    private boolean valid;
    private boolean isLazy;
    private Supplier<ICapabilityProvider> lazyParentSupplier;
    private CompoundTag lazyData;
    private boolean initialized;

    protected CapabilityProvider(Class<B> baseClass) {
        this(baseClass, false);
    }

    protected CapabilityProvider(Class<B> baseClass, boolean isLazy) {
        this.valid = true;
        this.isLazy = false;
        this.lazyParentSupplier = null;
        this.lazyData = null;
        this.initialized = false;
        this.baseClass = baseClass;
        this.isLazy = SUPPORTS_LAZY_CAPABILITIES && isLazy;
    }

    protected final void gatherCapabilities() {
        this.gatherCapabilities(() -> {
            return null;
        });
    }

    protected final void gatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.gatherCapabilities(() -> {
            return parent;
        });
    }

    protected final void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
        if (this.isLazy && !this.initialized) {
            this.lazyParentSupplier = parent == null ? () -> {
                return null;
            } : parent;
        } else {
            this.doGatherCapabilities(parent == null ? null : (ICapabilityProvider)parent.get());
        }
    }

    private void doGatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.capabilities = ForgeEventFactory.gatherCapabilities(this.baseClass, this.getProvider(), parent);
        this.initialized = true;
    }

    @NotNull B getProvider() {
        return this;
    }

    protected final @Nullable CapabilityDispatcher getCapabilities() {
        if (this.isLazy && !this.initialized) {
            this.doGatherCapabilities(this.lazyParentSupplier == null ? null : (ICapabilityProvider)this.lazyParentSupplier.get());
            if (this.lazyData != null) {
                this.deserializeCaps(this.lazyData);
            }
        }

        return this.capabilities;
    }

    public final boolean areCapsCompatible(CapabilityProvider<B> other) {
        return this.areCapsCompatible(other.getCapabilities());
    }

    public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
        CapabilityDispatcher disp = this.getCapabilities();
        if (disp == null) {
            return other == null ? true : other.areCompatible((CapabilityDispatcher)null);
        } else {
            return disp.areCompatible(other);
        }
    }

    protected final @Nullable CompoundTag serializeCaps() {
        if (this.isLazy && !this.initialized) {
            return this.lazyData;
        } else {
            CapabilityDispatcher disp = this.getCapabilities();
            return disp != null ? disp.serializeNBT() : null;
        }
    }

    protected final void deserializeCaps(CompoundTag tag) {
        if (this.isLazy && !this.initialized) {
            this.lazyData = tag;
        } else {
            CapabilityDispatcher disp = this.getCapabilities();
            if (disp != null) {
                disp.deserializeNBT(tag);
            }

        }
    }

    public void invalidateCaps() {
        this.valid = false;
        CapabilityDispatcher disp = this.getCapabilities();
        if (disp != null) {
            disp.invalidate();
        }

    }

    public void reviveCaps() {
        this.valid = true;
    }

    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        CapabilityDispatcher disp = this.getCapabilities();
        return this.valid && disp != null ? disp.getCapability(cap, side) : LazyOptional.empty();
    }

    public static class AsField<B extends ICapabilityProviderImpl<B>> extends CapabilityProvider<B> {
        private final B owner;

        public AsField(Class<B> baseClass, B owner) {
            super(baseClass);
            this.owner = owner;
        }

        public AsField(Class<B> baseClass, B owner, boolean isLazy) {
            super(baseClass, isLazy);
            this.owner = owner;
        }

        public void initInternal() {
            this.gatherCapabilities();
        }

        public @Nullable CompoundTag serializeInternal() {
            return this.serializeCaps();
        }

        public void deserializeInternal(CompoundTag tag) {
            this.deserializeCaps(tag);
        }

        @NotNull B getProvider() {
            return this.owner;
        }
    }
}
