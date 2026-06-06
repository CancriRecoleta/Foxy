//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.fluids;

import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;

public final class FluidInteractionRegistry {
    private static final Map<FluidType, List<InteractionInformation>> INTERACTIONS = new HashMap();

    public FluidInteractionRegistry() {
    }

    public static synchronized void addInteraction(FluidType source, InteractionInformation interaction) {
        ((List)INTERACTIONS.computeIfAbsent(source, (s) -> {
            return new ArrayList();
        })).add(interaction);
    }

    public static boolean canInteract(Level level, BlockPos pos) {
        FluidState state = level.getFluidState(pos);
        UnmodifiableIterator var3 = LiquidBlock.POSSIBLE_FLOW_DIRECTIONS.iterator();

        while(var3.hasNext()) {
            Direction direction = (Direction)var3.next();
            BlockPos relativePos = pos.relative(direction.getOpposite());
            List<InteractionInformation> interactions = (List)INTERACTIONS.getOrDefault(state.getFluidType(), Collections.emptyList());
            Iterator var7 = interactions.iterator();

            while(var7.hasNext()) {
                InteractionInformation interaction = (InteractionInformation)var7.next();
                if (interaction.predicate().test(level, pos, relativePos, state)) {
                    interaction.interaction().interact(level, pos, relativePos, state);
                    return true;
                }
            }
        }

        return false;
    }

    static {
        addInteraction((FluidType)ForgeMod.LAVA_TYPE.get(), new InteractionInformation((FluidType)ForgeMod.WATER_TYPE.get(), (fluidState) -> {
            return fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState();
        }));
        addInteraction((FluidType)ForgeMod.LAVA_TYPE.get(), new InteractionInformation((level, currentPos, relativePos, currentState) -> {
            return level.getBlockState(currentPos.below()).is(Blocks.SOUL_SOIL) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE);
        }, Blocks.BASALT.defaultBlockState()));
    }

    public static record InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction) {
        public InteractionInformation(FluidType type, BlockState state) {
            this(type, (fluidState) -> {
                return state;
            });
        }

        public InteractionInformation(HasFluidInteraction predicate, BlockState state) {
            this(predicate, (fluidState) -> {
                return state;
            });
        }

        public InteractionInformation(FluidType type, Function<FluidState, BlockState> getState) {
            this((level, currentPos, relativePos, currentState) -> {
                return level.getFluidState(relativePos).getFluidType() == type;
            }, getState);
        }

        public InteractionInformation(HasFluidInteraction predicate, Function<FluidState, BlockState> getState) {
            this(predicate, (level, currentPos, relativePos, currentState) -> {
                level.setBlockAndUpdate(currentPos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, currentPos, currentPos, (BlockState)getState.apply(currentState)));
                level.levelEvent(1501, currentPos, 0);
            });
        }

        public InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction) {
            this.predicate = predicate;
            this.interaction = interaction;
        }

        public HasFluidInteraction predicate() {
            return this.predicate;
        }

        public FluidInteraction interaction() {
            return this.interaction;
        }
    }

    @FunctionalInterface
    public interface HasFluidInteraction {
        boolean test(Level var1, BlockPos var2, BlockPos var3, FluidState var4);
    }

    @FunctionalInterface
    public interface FluidInteraction {
        void interact(Level var1, BlockPos var2, BlockPos var3, FluidState var4);
    }
}
