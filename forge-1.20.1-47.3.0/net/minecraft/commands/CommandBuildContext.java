//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
    <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1);

    static CommandBuildContext simple(final HolderLookup.Provider p_255702_, final FeatureFlagSet p_255968_) {
        return new CommandBuildContext() {
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_255791_) {
                return p_255702_.lookupOrThrow(p_255791_).filterFeatures(p_255968_);
            }
        };
    }

    static Configurable configurable(final RegistryAccess p_255925_, final FeatureFlagSet p_255945_) {
        return new Configurable() {
            MissingTagAccessPolicy missingTagAccessPolicy;

            {
                this.missingTagAccessPolicy = net.minecraft.commands.CommandBuildContext.MissingTagAccessPolicy.FAIL;
            }

            public void missingTagAccessPolicy(MissingTagAccessPolicy p_256626_) {
                this.missingTagAccessPolicy = p_256626_;
            }

            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> p_256616_) {
                Registry<T> $$1 = p_255925_.registryOrThrow(p_256616_);
                final HolderLookup.RegistryLookup<T> $$2 = $$1.asLookup();
                final HolderLookup.RegistryLookup<T> $$3 = $$1.asTagAddingLookup();
                HolderLookup.RegistryLookup<T> $$4 = new HolderLookup.RegistryLookup.Delegate<T>() {
                    protected HolderLookup.RegistryLookup<T> parent() {
                        HolderLookup.RegistryLookup var10000;
                        switch (missingTagAccessPolicy) {
                            case FAIL -> var10000 = $$2;
                            case CREATE_NEW -> var10000 = $$3;
                            default -> throw new IncompatibleClassChangeError();
                        }

                        return var10000;
                    }
                };
                return $$4.filterFeatures(p_255945_);
            }
        };
    }

    public interface Configurable extends CommandBuildContext {
        void missingTagAccessPolicy(MissingTagAccessPolicy var1);
    }

    public static enum MissingTagAccessPolicy {
        CREATE_NEW,
        FAIL;

        private MissingTagAccessPolicy() {
        }
    }
}
