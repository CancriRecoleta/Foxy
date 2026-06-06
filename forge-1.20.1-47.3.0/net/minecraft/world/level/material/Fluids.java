//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class Fluids {
    public static final Fluid EMPTY = register("empty", new EmptyFluid());
    public static final FlowingFluid FLOWING_WATER = (FlowingFluid)register("flowing_water", new WaterFluid.Flowing());
    public static final FlowingFluid WATER = (FlowingFluid)register("water", new WaterFluid.Source());
    public static final FlowingFluid FLOWING_LAVA = (FlowingFluid)register("flowing_lava", new LavaFluid.Flowing());
    public static final FlowingFluid LAVA = (FlowingFluid)register("lava", new LavaFluid.Source());

    public Fluids() {
    }

    private static <T extends Fluid> T register(String p_76198_, T p_76199_) {
        return (Fluid)Registry.register(BuiltInRegistries.FLUID, (String)p_76198_, p_76199_);
    }

    static {
        Iterator var0 = BuiltInRegistries.FLUID.iterator();

        while(var0.hasNext()) {
            Fluid $$0 = (Fluid)var0.next();
            UnmodifiableIterator var2 = $$0.getStateDefinition().getPossibleStates().iterator();

            while(var2.hasNext()) {
                FluidState $$1 = (FluidState)var2.next();
                Fluid.FLUID_STATE_REGISTRY.add($$1);
            }
        }

    }
}
