//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.ticks.SavedTick;
import org.slf4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final UpgradeData EMPTY;
    private static final String TAG_INDICES = "Indices";
    private static final Direction8[] DIRECTIONS;
    private final EnumSet<Direction8> sides;
    private final List<SavedTick<Block>> neighborBlockTicks;
    private final List<SavedTick<Fluid>> neighborFluidTicks;
    private final int[][] index;
    static final Map<Block, BlockFixer> MAP;
    static final Set<BlockFixer> CHUNKY_FIXERS;

    private UpgradeData(LevelHeightAccessor p_156506_) {
        this.sides = EnumSet.noneOf(Direction8.class);
        this.neighborBlockTicks = Lists.newArrayList();
        this.neighborFluidTicks = Lists.newArrayList();
        this.index = new int[p_156506_.getSectionsCount()][];
    }

    public UpgradeData(CompoundTag p_156508_, LevelHeightAccessor p_156509_) {
        this(p_156509_);
        if (p_156508_.contains("Indices", 10)) {
            CompoundTag $$2 = p_156508_.getCompound("Indices");

            for(int $$3 = 0; $$3 < this.index.length; ++$$3) {
                String $$4 = String.valueOf($$3);
                if ($$2.contains($$4, 11)) {
                    this.index[$$3] = $$2.getIntArray($$4);
                }
            }
        }

        int $$5 = p_156508_.getInt("Sides");
        Direction8[] var9 = Direction8.values();
        int var10 = var9.length;

        for(int var6 = 0; var6 < var10; ++var6) {
            Direction8 $$6 = var9[var6];
            if (($$5 & 1 << $$6.ordinal()) != 0) {
                this.sides.add($$6);
            }
        }

        loadTicks(p_156508_, "neighbor_block_ticks", (p_258983_) -> {
            return BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(p_258983_)).or(() -> {
                return Optional.of(Blocks.AIR);
            });
        }, this.neighborBlockTicks);
        loadTicks(p_156508_, "neighbor_fluid_ticks", (p_258986_) -> {
            return BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(p_258986_)).or(() -> {
                return Optional.of(Fluids.EMPTY);
            });
        }, this.neighborFluidTicks);
    }

    private static <T> void loadTicks(CompoundTag p_208133_, String p_208134_, Function<String, Optional<T>> p_208135_, List<SavedTick<T>> p_208136_) {
        if (p_208133_.contains(p_208134_, 9)) {
            ListTag $$4 = p_208133_.getList(p_208134_, 10);
            Iterator var5 = $$4.iterator();

            while(var5.hasNext()) {
                Tag $$5 = (Tag)var5.next();
                Optional var10000 = SavedTick.loadTick((CompoundTag)$$5, p_208135_);
                Objects.requireNonNull(p_208136_);
                var10000.ifPresent(p_208136_::add);
            }
        }

    }

    public void upgrade(LevelChunk p_63342_) {
        this.upgradeInside(p_63342_);
        Direction8[] var2 = DIRECTIONS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction8 $$1 = var2[var4];
            upgradeSides(p_63342_, $$1);
        }

        Level $$2 = p_63342_.getLevel();
        this.neighborBlockTicks.forEach((p_208142_) -> {
            Block $$2x = p_208142_.type() == Blocks.AIR ? $$2.getBlockState(p_208142_.pos()).getBlock() : (Block)p_208142_.type();
            $$2.scheduleTick(p_208142_.pos(), $$2x, p_208142_.delay(), p_208142_.priority());
        });
        this.neighborFluidTicks.forEach((p_208125_) -> {
            Fluid $$2x = p_208125_.type() == Fluids.EMPTY ? $$2.getFluidState(p_208125_.pos()).getType() : (Fluid)p_208125_.type();
            $$2.scheduleTick(p_208125_.pos(), $$2x, p_208125_.delay(), p_208125_.priority());
        });
        CHUNKY_FIXERS.forEach((p_208122_) -> {
            p_208122_.processChunk($$2);
        });
    }

    private static void upgradeSides(LevelChunk p_63344_, Direction8 p_63345_) {
        Level $$2 = p_63344_.getLevel();
        if (p_63344_.getUpgradeData().sides.remove(p_63345_)) {
            Set<Direction> $$3 = p_63345_.getDirections();
            int $$4 = false;
            int $$5 = true;
            boolean $$6 = $$3.contains(Direction.EAST);
            boolean $$7 = $$3.contains(Direction.WEST);
            boolean $$8 = $$3.contains(Direction.SOUTH);
            boolean $$9 = $$3.contains(Direction.NORTH);
            boolean $$10 = $$3.size() == 1;
            ChunkPos $$11 = p_63344_.getPos();
            int $$12 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 1 : ($$7 ? 0 : 15));
            int $$13 = $$11.getMinBlockX() + ($$10 && ($$9 || $$8) ? 14 : ($$7 ? 0 : 15));
            int $$14 = $$11.getMinBlockZ() + (!$$10 || !$$6 && !$$7 ? ($$9 ? 0 : 15) : 1);
            int $$15 = $$11.getMinBlockZ() + (!$$10 || !$$6 && !$$7 ? ($$9 ? 0 : 15) : 14);
            Direction[] $$16 = Direction.values();
            BlockPos.MutableBlockPos $$17 = new BlockPos.MutableBlockPos();
            Iterator var18 = BlockPos.betweenClosed($$12, $$2.getMinBuildHeight(), $$14, $$13, $$2.getMaxBuildHeight() - 1, $$15).iterator();

            while(var18.hasNext()) {
                BlockPos $$18 = (BlockPos)var18.next();
                BlockState $$19 = $$2.getBlockState($$18);
                BlockState $$20 = $$19;
                Direction[] var22 = $$16;
                int var23 = $$16.length;

                for(int var24 = 0; var24 < var23; ++var24) {
                    Direction $$21 = var22[var24];
                    $$17.setWithOffset($$18, (Direction)$$21);
                    $$20 = updateState($$20, $$21, $$2, $$18, $$17);
                }

                Block.updateOrDestroy($$19, $$20, $$2, $$18, 18);
            }

        }
    }

    private static BlockState updateState(BlockState p_63336_, Direction p_63337_, LevelAccessor p_63338_, BlockPos p_63339_, BlockPos p_63340_) {
        return ((BlockFixer)MAP.getOrDefault(p_63336_.getBlock(), net.minecraft.world.level.chunk.UpgradeData.BlockFixers.DEFAULT)).updateShape(p_63336_, p_63337_, p_63338_.getBlockState(p_63340_), p_63338_, p_63339_, p_63340_);
    }

    private void upgradeInside(LevelChunk p_63348_) {
        BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos $$2 = new BlockPos.MutableBlockPos();
        ChunkPos $$3 = p_63348_.getPos();
        LevelAccessor $$4 = p_63348_.getLevel();

        int $$19;
        for($$19 = 0; $$19 < this.index.length; ++$$19) {
            LevelChunkSection $$6 = p_63348_.getSection($$19);
            int[] $$7 = this.index[$$19];
            this.index[$$19] = null;
            if ($$7 != null && $$7.length > 0) {
                Direction[] $$8 = Direction.values();
                PalettedContainer<BlockState> $$9 = $$6.getStates();
                int $$10 = p_63348_.getSectionYFromSectionIndex($$19);
                int $$11 = SectionPos.sectionToBlockCoord($$10);
                int[] var13 = $$7;
                int var14 = $$7.length;

                for(int var15 = 0; var15 < var14; ++var15) {
                    int $$12 = var13[var15];
                    int $$13 = $$12 & 15;
                    int $$14 = $$12 >> 8 & 15;
                    int $$15 = $$12 >> 4 & 15;
                    $$1.set($$3.getMinBlockX() + $$13, $$11 + $$14, $$3.getMinBlockZ() + $$15);
                    BlockState $$16 = (BlockState)$$9.get($$12);
                    BlockState $$17 = $$16;
                    Direction[] var22 = $$8;
                    int var23 = $$8.length;

                    for(int var24 = 0; var24 < var23; ++var24) {
                        Direction $$18 = var22[var24];
                        $$2.setWithOffset($$1, (Direction)$$18);
                        if (SectionPos.blockToSectionCoord($$1.getX()) == $$3.x && SectionPos.blockToSectionCoord($$1.getZ()) == $$3.z) {
                            $$17 = updateState($$17, $$18, $$4, $$1, $$2);
                        }
                    }

                    Block.updateOrDestroy($$16, $$17, $$4, $$1, 18);
                }
            }
        }

        for($$19 = 0; $$19 < this.index.length; ++$$19) {
            if (this.index[$$19] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", new Object[]{$$4.getSectionYFromSectionIndex($$19), $$3.x, $$3.z});
            }

            this.index[$$19] = null;
        }

    }

    public boolean isEmpty() {
        int[][] var1 = this.index;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int[] $$0 = var1[var3];
            if ($$0 != null) {
                return false;
            }
        }

        return this.sides.isEmpty();
    }

    public CompoundTag write() {
        CompoundTag $$0 = new CompoundTag();
        CompoundTag $$1 = new CompoundTag();

        int $$4;
        for($$4 = 0; $$4 < this.index.length; ++$$4) {
            String $$3 = String.valueOf($$4);
            if (this.index[$$4] != null && this.index[$$4].length != 0) {
                $$1.putIntArray($$3, this.index[$$4]);
            }
        }

        if (!$$1.isEmpty()) {
            $$0.put("Indices", $$1);
        }

        $$4 = 0;

        Direction8 $$5;
        for(Iterator var6 = this.sides.iterator(); var6.hasNext(); $$4 |= 1 << $$5.ordinal()) {
            $$5 = (Direction8)var6.next();
        }

        $$0.putByte("Sides", (byte)$$4);
        ListTag $$7;
        if (!this.neighborBlockTicks.isEmpty()) {
            $$7 = new ListTag();
            this.neighborBlockTicks.forEach((p_208147_) -> {
                $$7.add(p_208147_.save((p_258984_) -> {
                    return BuiltInRegistries.BLOCK.getKey(p_258984_).toString();
                }));
            });
            $$0.put("neighbor_block_ticks", $$7);
        }

        if (!this.neighborFluidTicks.isEmpty()) {
            $$7 = new ListTag();
            this.neighborFluidTicks.forEach((p_208139_) -> {
                $$7.add(p_208139_.save((p_258985_) -> {
                    return BuiltInRegistries.FLUID.getKey(p_258985_).toString();
                }));
            });
            $$0.put("neighbor_fluid_ticks", $$7);
        }

        return $$0;
    }

    static {
        EMPTY = new UpgradeData(EmptyBlockGetter.INSTANCE);
        DIRECTIONS = Direction8.values();
        MAP = new IdentityHashMap();
        CHUNKY_FIXERS = Sets.newHashSet();
    }

    private static enum BlockFixers implements BlockFixer {
        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN}) {
            public BlockState updateShape(BlockState p_63394_, Direction p_63395_, BlockState p_63396_, LevelAccessor p_63397_, BlockPos p_63398_, BlockPos p_63399_) {
                return p_63394_;
            }
        },
        DEFAULT(new Block[0]) {
            public BlockState updateShape(BlockState p_63405_, Direction p_63406_, BlockState p_63407_, LevelAccessor p_63408_, BlockPos p_63409_, BlockPos p_63410_) {
                return p_63405_.updateShape(p_63406_, p_63408_.getBlockState(p_63410_), p_63408_, p_63409_, p_63410_);
            }
        },
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
            public BlockState updateShape(BlockState p_63416_, Direction p_63417_, BlockState p_63418_, LevelAccessor p_63419_, BlockPos p_63420_, BlockPos p_63421_) {
                if (p_63418_.is(p_63416_.getBlock()) && p_63417_.getAxis().isHorizontal() && p_63416_.getValue(ChestBlock.TYPE) == ChestType.SINGLE && p_63418_.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
                    Direction $$6 = (Direction)p_63416_.getValue(ChestBlock.FACING);
                    if (p_63417_.getAxis() != $$6.getAxis() && $$6 == p_63418_.getValue(ChestBlock.FACING)) {
                        ChestType $$7 = p_63417_ == $$6.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                        p_63419_.setBlock(p_63421_, (BlockState)p_63418_.setValue(ChestBlock.TYPE, $$7.getOpposite()), 18);
                        if ($$6 == Direction.NORTH || $$6 == Direction.EAST) {
                            BlockEntity $$8 = p_63419_.getBlockEntity(p_63420_);
                            BlockEntity $$9 = p_63419_.getBlockEntity(p_63421_);
                            if ($$8 instanceof ChestBlockEntity && $$9 instanceof ChestBlockEntity) {
                                ChestBlockEntity.swapContents((ChestBlockEntity)$$8, (ChestBlockEntity)$$9);
                            }
                        }

                        return (BlockState)p_63416_.setValue(ChestBlock.TYPE, $$7);
                    }
                }

                return p_63416_;
            }
        },
        LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.CHERRY_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}) {
            private final ThreadLocal<List<ObjectSet<BlockPos>>> queue = ThreadLocal.withInitial(() -> {
                return Lists.newArrayListWithCapacity(7);
            });

            public BlockState updateShape(BlockState p_63432_, Direction p_63433_, BlockState p_63434_, LevelAccessor p_63435_, BlockPos p_63436_, BlockPos p_63437_) {
                BlockState $$6 = p_63432_.updateShape(p_63433_, p_63435_.getBlockState(p_63437_), p_63435_, p_63436_, p_63437_);
                if (p_63432_ != $$6) {
                    int $$7 = (Integer)$$6.getValue(BlockStateProperties.DISTANCE);
                    List<ObjectSet<BlockPos>> $$8 = (List)this.queue.get();
                    if ($$8.isEmpty()) {
                        for(int $$9 = 0; $$9 < 7; ++$$9) {
                            $$8.add(new ObjectOpenHashSet());
                        }
                    }

                    ((ObjectSet)$$8.get($$7)).add(p_63436_.immutable());
                }

                return p_63432_;
            }

            public void processChunk(LevelAccessor p_63430_) {
                BlockPos.MutableBlockPos $$1 = new BlockPos.MutableBlockPos();
                List<ObjectSet<BlockPos>> $$2 = (List)this.queue.get();

                label44:
                for(int $$3 = 2; $$3 < $$2.size(); ++$$3) {
                    int $$4 = $$3 - 1;
                    ObjectSet<BlockPos> $$5 = (ObjectSet)$$2.get($$4);
                    ObjectSet<BlockPos> $$6 = (ObjectSet)$$2.get($$3);
                    ObjectIterator var8 = $$5.iterator();

                    while(true) {
                        BlockPos $$7;
                        BlockState $$8;
                        do {
                            do {
                                if (!var8.hasNext()) {
                                    continue label44;
                                }

                                $$7 = (BlockPos)var8.next();
                                $$8 = p_63430_.getBlockState($$7);
                            } while((Integer)$$8.getValue(BlockStateProperties.DISTANCE) < $$4);

                            p_63430_.setBlock($$7, (BlockState)$$8.setValue(BlockStateProperties.DISTANCE, $$4), 18);
                        } while($$3 == 7);

                        Direction[] var11 = DIRECTIONS;
                        int var12 = var11.length;

                        for(int var13 = 0; var13 < var12; ++var13) {
                            Direction $$9 = var11[var13];
                            $$1.setWithOffset($$7, (Direction)$$9);
                            BlockState $$10 = p_63430_.getBlockState($$1);
                            if ($$10.hasProperty(BlockStateProperties.DISTANCE) && (Integer)$$8.getValue(BlockStateProperties.DISTANCE) > $$3) {
                                $$6.add($$1.immutable());
                            }
                        }
                    }
                }

                $$2.clear();
            }
        },
        STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}) {
            public BlockState updateShape(BlockState p_63443_, Direction p_63444_, BlockState p_63445_, LevelAccessor p_63446_, BlockPos p_63447_, BlockPos p_63448_) {
                if ((Integer)p_63443_.getValue(StemBlock.AGE) == 7) {
                    StemGrownBlock $$6 = ((StemBlock)p_63443_.getBlock()).getFruit();
                    if (p_63445_.is($$6)) {
                        return (BlockState)$$6.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, p_63444_);
                    }
                }

                return p_63443_;
            }
        };

        public static final Direction[] DIRECTIONS = Direction.values();

        BlockFixers(Block... p_63380_) {
            this(false, p_63380_);
        }

        BlockFixers(boolean p_63369_, Block... p_63370_) {
            Block[] var5 = p_63370_;
            int var6 = p_63370_.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Block $$2 = var5[var7];
                UpgradeData.MAP.put($$2, this);
            }

            if (p_63369_) {
                UpgradeData.CHUNKY_FIXERS.add(this);
            }

        }
    }

    public interface BlockFixer {
        BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6);

        default void processChunk(LevelAccessor p_63351_) {
        }
    }
}
