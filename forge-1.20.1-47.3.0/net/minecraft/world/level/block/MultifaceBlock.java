//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MultifaceBlock extends Block {
    private static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
    private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION;
    protected static final Direction[] DIRECTIONS;
    private final ImmutableMap<BlockState, VoxelShape> shapesCache;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    public MultifaceBlock(BlockBehaviour.Properties p_153822_) {
        super(p_153822_);
        this.registerDefaultState(getDefaultMultifaceState(this.stateDefinition));
        this.shapesCache = this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
        this.canRotate = Plane.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = Plane.HORIZONTAL.stream().filter(Axis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = Plane.HORIZONTAL.stream().filter(Axis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }

    public static Set<Direction> availableFaces(BlockState p_221585_) {
        if (!(p_221585_.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        } else {
            Set<Direction> $$1 = EnumSet.noneOf(Direction.class);
            Direction[] var2 = Direction.values();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Direction $$2 = var2[var4];
                if (hasFace(p_221585_, $$2)) {
                    $$1.add($$2);
                }
            }

            return $$1;
        }
    }

    public static Set<Direction> unpack(byte p_221570_) {
        Set<Direction> $$1 = EnumSet.noneOf(Direction.class);
        Direction[] var2 = Direction.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction $$2 = var2[var4];
            if ((p_221570_ & (byte)(1 << $$2.ordinal())) > 0) {
                $$1.add($$2);
            }
        }

        return $$1;
    }

    public static byte pack(Collection<Direction> p_221577_) {
        byte $$1 = 0;

        Direction $$2;
        for(Iterator var2 = p_221577_.iterator(); var2.hasNext(); $$1 = (byte)($$1 | 1 << $$2.ordinal())) {
            $$2 = (Direction)var2.next();
        }

        return $$1;
    }

    protected boolean isFaceSupported(Direction p_153921_) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153917_) {
        Direction[] var2 = DIRECTIONS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction $$1 = var2[var4];
            if (this.isFaceSupported($$1)) {
                p_153917_.add(getFaceProperty($$1));
            }
        }

    }

    public BlockState updateShape(BlockState p_153904_, Direction p_153905_, BlockState p_153906_, LevelAccessor p_153907_, BlockPos p_153908_, BlockPos p_153909_) {
        if (!hasAnyFace(p_153904_)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return hasFace(p_153904_, p_153905_) && !canAttachTo(p_153907_, p_153905_, p_153909_, p_153906_) ? removeFace(p_153904_, getFaceProperty(p_153905_)) : p_153904_;
        }
    }

    public VoxelShape getShape(BlockState p_153851_, BlockGetter p_153852_, BlockPos p_153853_, CollisionContext p_153854_) {
        return (VoxelShape)this.shapesCache.get(p_153851_);
    }

    public boolean canSurvive(BlockState p_153888_, LevelReader p_153889_, BlockPos p_153890_) {
        boolean $$3 = false;
        Direction[] var5 = DIRECTIONS;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction $$4 = var5[var7];
            if (hasFace(p_153888_, $$4)) {
                BlockPos $$5 = p_153890_.relative($$4);
                if (!canAttachTo(p_153889_, $$4, $$5, p_153889_.getBlockState($$5))) {
                    return false;
                }

                $$3 = true;
            }
        }

        return $$3;
    }

    public boolean canBeReplaced(BlockState p_153848_, BlockPlaceContext p_153849_) {
        return hasAnyVacantFace(p_153848_);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_153824_) {
        Level $$1 = p_153824_.getLevel();
        BlockPos $$2 = p_153824_.getClickedPos();
        BlockState $$3 = $$1.getBlockState($$2);
        return (BlockState)Arrays.stream(p_153824_.getNearestLookingDirections()).map((p_153865_) -> {
            return this.getStateForPlacement($$3, $$1, $$2, p_153865_);
        }).filter(Objects::nonNull).findFirst().orElse((Object)null);
    }

    public boolean isValidStateForPlacement(BlockGetter p_221572_, BlockState p_221573_, BlockPos p_221574_, Direction p_221575_) {
        if (this.isFaceSupported(p_221575_) && (!p_221573_.is(this) || !hasFace(p_221573_, p_221575_))) {
            BlockPos $$4 = p_221574_.relative(p_221575_);
            return canAttachTo(p_221572_, p_221575_, $$4, p_221572_.getBlockState($$4));
        } else {
            return false;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockState p_153941_, BlockGetter p_153942_, BlockPos p_153943_, Direction p_153944_) {
        if (!this.isValidStateForPlacement(p_153942_, p_153941_, p_153943_, p_153944_)) {
            return null;
        } else {
            BlockState $$6;
            if (p_153941_.is(this)) {
                $$6 = p_153941_;
            } else if (this.isWaterloggable() && p_153941_.getFluidState().isSourceOfType(Fluids.WATER)) {
                $$6 = (BlockState)this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
            } else {
                $$6 = this.defaultBlockState();
            }

            return (BlockState)$$6.setValue(getFaceProperty(p_153944_), true);
        }
    }

    public BlockState rotate(BlockState p_153895_, Rotation p_153896_) {
        if (!this.canRotate) {
            return p_153895_;
        } else {
            Objects.requireNonNull(p_153896_);
            return this.mapDirections(p_153895_, p_153896_::rotate);
        }
    }

    public BlockState mirror(BlockState p_153892_, Mirror p_153893_) {
        if (p_153893_ == Mirror.FRONT_BACK && !this.canMirrorX) {
            return p_153892_;
        } else if (p_153893_ == Mirror.LEFT_RIGHT && !this.canMirrorZ) {
            return p_153892_;
        } else {
            Objects.requireNonNull(p_153893_);
            return this.mapDirections(p_153892_, p_153893_::mirror);
        }
    }

    private BlockState mapDirections(BlockState p_153911_, Function<Direction, Direction> p_153912_) {
        BlockState $$2 = p_153911_;
        Direction[] var4 = DIRECTIONS;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Direction $$3 = var4[var6];
            if (this.isFaceSupported($$3)) {
                $$2 = (BlockState)$$2.setValue(getFaceProperty((Direction)p_153912_.apply($$3)), (Boolean)p_153911_.getValue(getFaceProperty($$3)));
            }
        }

        return $$2;
    }

    public static boolean hasFace(BlockState p_153901_, Direction p_153902_) {
        BooleanProperty $$2 = getFaceProperty(p_153902_);
        return p_153901_.hasProperty($$2) && (Boolean)p_153901_.getValue($$2);
    }

    public static boolean canAttachTo(BlockGetter p_153830_, Direction p_153831_, BlockPos p_153832_, BlockState p_153833_) {
        return Block.isFaceFull(p_153833_.getBlockSupportShape(p_153830_, p_153832_), p_153831_.getOpposite()) || Block.isFaceFull(p_153833_.getCollisionShape(p_153830_, p_153832_), p_153831_.getOpposite());
    }

    private boolean isWaterloggable() {
        return this.stateDefinition.getProperties().contains(BlockStateProperties.WATERLOGGED);
    }

    private static BlockState removeFace(BlockState p_153898_, BooleanProperty p_153899_) {
        BlockState $$2 = (BlockState)p_153898_.setValue(p_153899_, false);
        return hasAnyFace($$2) ? $$2 : Blocks.AIR.defaultBlockState();
    }

    public static BooleanProperty getFaceProperty(Direction p_153934_) {
        return (BooleanProperty)PROPERTY_BY_DIRECTION.get(p_153934_);
    }

    private static BlockState getDefaultMultifaceState(StateDefinition<Block, BlockState> p_153919_) {
        BlockState $$1 = (BlockState)p_153919_.any();
        Iterator var2 = PROPERTY_BY_DIRECTION.values().iterator();

        while(var2.hasNext()) {
            BooleanProperty $$2 = (BooleanProperty)var2.next();
            if ($$1.hasProperty($$2)) {
                $$1 = (BlockState)$$1.setValue($$2, false);
            }
        }

        return $$1;
    }

    private static VoxelShape calculateMultifaceShape(BlockState p_153959_) {
        VoxelShape $$1 = Shapes.empty();
        Direction[] var2 = DIRECTIONS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction $$2 = var2[var4];
            if (hasFace(p_153959_, $$2)) {
                $$1 = Shapes.or($$1, (VoxelShape)SHAPE_BY_DIRECTION.get($$2));
            }
        }

        return $$1.isEmpty() ? Shapes.block() : $$1;
    }

    protected static boolean hasAnyFace(BlockState p_153961_) {
        return Arrays.stream(DIRECTIONS).anyMatch((p_221583_) -> {
            return hasFace(p_153961_, p_221583_);
        });
    }

    private static boolean hasAnyVacantFace(BlockState p_153963_) {
        return Arrays.stream(DIRECTIONS).anyMatch((p_221580_) -> {
            return !hasFace(p_153963_, p_221580_);
        });
    }

    public abstract MultifaceSpreader getSpreader();

    static {
        PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
        SHAPE_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), (p_153923_) -> {
            p_153923_.put(Direction.NORTH, NORTH_AABB);
            p_153923_.put(Direction.EAST, EAST_AABB);
            p_153923_.put(Direction.SOUTH, SOUTH_AABB);
            p_153923_.put(Direction.WEST, WEST_AABB);
            p_153923_.put(Direction.UP, UP_AABB);
            p_153923_.put(Direction.DOWN, DOWN_AABB);
        });
        DIRECTIONS = Direction.values();
    }
}
