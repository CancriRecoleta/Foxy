//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BeehiveDecorator extends TreeDecorator {
    public static final Codec<BeehiveDecorator> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveDecorator::new, (p_69971_) -> {
        return p_69971_.probability;
    }).codec();
    private static final Direction WORLDGEN_FACING;
    private static final Direction[] SPAWN_DIRECTIONS;
    private final float probability;

    public BeehiveDecorator(float p_69958_) {
        this.probability = p_69958_;
    }

    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.BEEHIVE;
    }

    public void place(TreeDecorator.Context p_226019_) {
        RandomSource $$1 = p_226019_.random();
        if (!($$1.nextFloat() >= this.probability)) {
            List<BlockPos> $$2 = p_226019_.leaves();
            List<BlockPos> $$3 = p_226019_.logs();
            int $$4 = !$$2.isEmpty() ? Math.max(((BlockPos)$$2.get(0)).getY() - 1, ((BlockPos)$$3.get(0)).getY() + 1) : Math.min(((BlockPos)$$3.get(0)).getY() + 1 + $$1.nextInt(3), ((BlockPos)$$3.get($$3.size() - 1)).getY());
            List<BlockPos> $$5 = (List)$$3.stream().filter((p_202300_) -> {
                return p_202300_.getY() == $$4;
            }).flatMap((p_202305_) -> {
                Stream var10000 = Stream.of(SPAWN_DIRECTIONS);
                Objects.requireNonNull(p_202305_);
                return var10000.map(p_202305_::relative);
            }).collect(Collectors.toList());
            if (!$$5.isEmpty()) {
                Collections.shuffle($$5);
                Optional<BlockPos> $$6 = $$5.stream().filter((p_226022_) -> {
                    return p_226019_.isAir(p_226022_) && p_226019_.isAir(p_226022_.relative(WORLDGEN_FACING));
                }).findFirst();
                if (!$$6.isEmpty()) {
                    p_226019_.setBlock((BlockPos)$$6.get(), (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
                    p_226019_.level().getBlockEntity((BlockPos)$$6.get(), BlockEntityType.BEEHIVE).ifPresent((p_259007_) -> {
                        int $$2 = 2 + $$1.nextInt(2);

                        for(int $$3 = 0; $$3 < $$2; ++$$3) {
                            CompoundTag $$4 = new CompoundTag();
                            $$4.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                            p_259007_.storeBee($$4, $$1.nextInt(599), false);
                        }

                    });
                }
            }
        }
    }

    static {
        WORLDGEN_FACING = Direction.SOUTH;
        SPAWN_DIRECTIONS = (Direction[])Plane.HORIZONTAL.stream().filter((p_202307_) -> {
            return p_202307_ != WORLDGEN_FACING.getOpposite();
        }).toArray((p_202297_) -> {
            return new Direction[p_202297_];
        });
    }
}
