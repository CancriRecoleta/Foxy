//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class JigsawPlacement {
    static final Logger LOGGER = LogUtils.getLogger();

    public JigsawPlacement() {
    }

    public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext p_227239_, Holder<StructureTemplatePool> p_227240_, Optional<ResourceLocation> p_227241_, int p_227242_, BlockPos p_227243_, boolean p_227244_, Optional<Heightmap.Types> p_227245_, int p_227246_) {
        RegistryAccess $$8 = p_227239_.registryAccess();
        ChunkGenerator $$9 = p_227239_.chunkGenerator();
        StructureTemplateManager $$10 = p_227239_.structureTemplateManager();
        LevelHeightAccessor $$11 = p_227239_.heightAccessor();
        WorldgenRandom $$12 = p_227239_.random();
        Registry<StructureTemplatePool> $$13 = $$8.registryOrThrow(Registries.TEMPLATE_POOL);
        Rotation $$14 = Rotation.getRandom($$12);
        StructureTemplatePool $$15 = (StructureTemplatePool)p_227240_.value();
        StructurePoolElement $$16 = $$15.getRandomTemplate($$12);
        if ($$16 == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        } else {
            BlockPos $$20;
            if (p_227241_.isPresent()) {
                ResourceLocation $$17 = (ResourceLocation)p_227241_.get();
                Optional<BlockPos> $$18 = getRandomNamedJigsaw($$16, $$17, p_227243_, $$14, $$10, $$12);
                if ($$18.isEmpty()) {
                    LOGGER.error("No starting jigsaw {} found in start pool {}", $$17, p_227240_.unwrapKey().map((p_248484_) -> {
                        return p_248484_.location().toString();
                    }).orElse("<unregistered>"));
                    return Optional.empty();
                }

                $$20 = (BlockPos)$$18.get();
            } else {
                $$20 = p_227243_;
            }

            Vec3i $$21 = $$20.subtract(p_227243_);
            BlockPos $$22 = p_227243_.subtract($$21);
            PoolElementStructurePiece $$23 = new PoolElementStructurePiece($$10, $$16, $$22, $$16.getGroundLevelDelta(), $$14, $$16.getBoundingBox($$10, $$22, $$14));
            BoundingBox $$24 = $$23.getBoundingBox();
            int $$25 = ($$24.maxX() + $$24.minX()) / 2;
            int $$26 = ($$24.maxZ() + $$24.minZ()) / 2;
            int $$28;
            if (p_227245_.isPresent()) {
                $$28 = p_227243_.getY() + $$9.getFirstFreeHeight($$25, $$26, (Heightmap.Types)p_227245_.get(), $$11, p_227239_.randomState());
            } else {
                $$28 = $$22.getY();
            }

            int $$29 = $$24.minY() + $$23.getGroundLevelDelta();
            $$23.move(0, $$28 - $$29, 0);
            int $$30 = $$28 + $$21.getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos($$25, $$30, $$26), (p_227237_) -> {
                List<PoolElementStructurePiece> $$15 = Lists.newArrayList();
                $$15.add($$23);
                if (p_227242_ > 0) {
                    AABB $$16 = new AABB((double)($$25 - p_227246_), (double)($$30 - p_227246_), (double)($$26 - p_227246_), (double)($$25 + p_227246_ + 1), (double)($$30 + p_227246_ + 1), (double)($$26 + p_227246_ + 1));
                    VoxelShape $$17 = Shapes.join(Shapes.create($$16), Shapes.create(AABB.of($$24)), BooleanOp.ONLY_FIRST);
                    addPieces(p_227239_.randomState(), p_227242_, p_227244_, $$9, $$10, $$11, $$12, $$13, $$23, $$15, $$17);
                    Objects.requireNonNull(p_227237_);
                    $$15.forEach(p_227237_::addPiece);
                }
            }));
        }
    }

    private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement p_227248_, ResourceLocation p_227249_, BlockPos p_227250_, Rotation p_227251_, StructureTemplateManager p_227252_, WorldgenRandom p_227253_) {
        List<StructureTemplate.StructureBlockInfo> $$6 = p_227248_.getShuffledJigsawBlocks(p_227252_, p_227250_, p_227251_, p_227253_);
        Optional<BlockPos> $$7 = Optional.empty();
        Iterator var8 = $$6.iterator();

        while(var8.hasNext()) {
            StructureTemplate.StructureBlockInfo $$8 = (StructureTemplate.StructureBlockInfo)var8.next();
            ResourceLocation $$9 = ResourceLocation.tryParse($$8.nbt().getString("name"));
            if (p_227249_.equals($$9)) {
                $$7 = Optional.of($$8.pos());
                break;
            }
        }

        return $$7;
    }

    private static void addPieces(RandomState p_227211_, int p_227212_, boolean p_227213_, ChunkGenerator p_227214_, StructureTemplateManager p_227215_, LevelHeightAccessor p_227216_, RandomSource p_227217_, Registry<StructureTemplatePool> p_227218_, PoolElementStructurePiece p_227219_, List<PoolElementStructurePiece> p_227220_, VoxelShape p_227221_) {
        Placer $$11 = new Placer(p_227218_, p_227212_, p_227214_, p_227215_, p_227220_, p_227217_);
        $$11.placing.addLast(new PieceState(p_227219_, new MutableObject(p_227221_), 0));

        while(!$$11.placing.isEmpty()) {
            PieceState $$12 = (PieceState)$$11.placing.removeFirst();
            $$11.tryPlacingChildren($$12.piece, $$12.free, $$12.depth, p_227213_, p_227216_, p_227211_);
        }

    }

    public static boolean generateJigsaw(ServerLevel p_227204_, Holder<StructureTemplatePool> p_227205_, ResourceLocation p_227206_, int p_227207_, BlockPos p_227208_, boolean p_227209_) {
        ChunkGenerator $$6 = p_227204_.getChunkSource().getGenerator();
        StructureTemplateManager $$7 = p_227204_.getStructureManager();
        StructureManager $$8 = p_227204_.structureManager();
        RandomSource $$9 = p_227204_.getRandom();
        Structure.GenerationContext $$10 = new Structure.GenerationContext(p_227204_.registryAccess(), $$6, $$6.getBiomeSource(), p_227204_.getChunkSource().randomState(), $$7, p_227204_.getSeed(), new ChunkPos(p_227208_), p_227204_, (p_227255_) -> {
            return true;
        });
        Optional<Structure.GenerationStub> $$11 = addPieces($$10, p_227205_, Optional.of(p_227206_), p_227207_, p_227208_, false, Optional.empty(), 128);
        if ($$11.isPresent()) {
            StructurePiecesBuilder $$12 = ((Structure.GenerationStub)$$11.get()).getPiecesBuilder();
            Iterator var13 = $$12.build().pieces().iterator();

            while(var13.hasNext()) {
                StructurePiece $$13 = (StructurePiece)var13.next();
                if ($$13 instanceof PoolElementStructurePiece) {
                    PoolElementStructurePiece $$14 = (PoolElementStructurePiece)$$13;
                    $$14.place(p_227204_, $$8, $$6, $$9, BoundingBox.infinite(), p_227208_, p_227209_);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static final class Placer {
        private final Registry<StructureTemplatePool> pools;
        private final int maxDepth;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolElementStructurePiece> pieces;
        private final RandomSource random;
        final Deque<PieceState> placing = Queues.newArrayDeque();

        Placer(Registry<StructureTemplatePool> p_227258_, int p_227259_, ChunkGenerator p_227260_, StructureTemplateManager p_227261_, List<? super PoolElementStructurePiece> p_227262_, RandomSource p_227263_) {
            this.pools = p_227258_;
            this.maxDepth = p_227259_;
            this.chunkGenerator = p_227260_;
            this.structureTemplateManager = p_227261_;
            this.pieces = p_227262_;
            this.random = p_227263_;
        }

        void tryPlacingChildren(PoolElementStructurePiece p_227265_, MutableObject<VoxelShape> p_227266_, int p_227267_, boolean p_227268_, LevelHeightAccessor p_227269_, RandomState p_227270_) {
            StructurePoolElement $$6 = p_227265_.getElement();
            BlockPos $$7 = p_227265_.getPosition();
            Rotation $$8 = p_227265_.getRotation();
            StructureTemplatePool.Projection $$9 = $$6.getProjection();
            boolean $$10 = $$9 == Projection.RIGID;
            MutableObject<VoxelShape> $$11 = new MutableObject();
            BoundingBox $$12 = p_227265_.getBoundingBox();
            int $$13 = $$12.minY();
            Iterator var15 = $$6.getShuffledJigsawBlocks(this.structureTemplateManager, $$7, $$8, this.random).iterator();

            while(true) {
                label129:
                while(var15.hasNext()) {
                    StructureTemplate.StructureBlockInfo $$14 = (StructureTemplate.StructureBlockInfo)var15.next();
                    Direction $$15 = JigsawBlock.getFrontFacing($$14.state());
                    BlockPos $$16 = $$14.pos();
                    BlockPos $$17 = $$16.relative($$15);
                    int $$18 = $$16.getY() - $$13;
                    int $$19 = -1;
                    ResourceKey<StructureTemplatePool> $$20 = readPoolName($$14);
                    Optional<? extends Holder<StructureTemplatePool>> $$21 = this.pools.getHolder($$20);
                    if ($$21.isEmpty()) {
                        JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", $$20.location());
                    } else {
                        Holder<StructureTemplatePool> $$22 = (Holder)$$21.get();
                        if (((StructureTemplatePool)$$22.value()).size() == 0 && !$$22.is(Pools.EMPTY)) {
                            JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", $$20.location());
                        } else {
                            Holder<StructureTemplatePool> $$23 = ((StructureTemplatePool)$$22.value()).getFallback();
                            if (((StructureTemplatePool)$$23.value()).size() == 0 && !$$23.is(Pools.EMPTY)) {
                                JigsawPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", $$23.unwrapKey().map((p_255599_) -> {
                                    return p_255599_.location().toString();
                                }).orElse("<unregistered>"));
                            } else {
                                boolean $$24 = $$12.isInside($$17);
                                MutableObject $$26;
                                if ($$24) {
                                    $$26 = $$11;
                                    if ($$11.getValue() == null) {
                                        $$11.setValue(Shapes.create(AABB.of($$12)));
                                    }
                                } else {
                                    $$26 = p_227266_;
                                }

                                List<StructurePoolElement> $$27 = Lists.newArrayList();
                                if (p_227267_ != this.maxDepth) {
                                    $$27.addAll(((StructureTemplatePool)$$22.value()).getShuffledTemplates(this.random));
                                }

                                $$27.addAll(((StructureTemplatePool)$$23.value()).getShuffledTemplates(this.random));
                                Iterator var29 = $$27.iterator();

                                while(var29.hasNext()) {
                                    StructurePoolElement $$28 = (StructurePoolElement)var29.next();
                                    if ($$28 == EmptyPoolElement.INSTANCE) {
                                        break;
                                    }

                                    Iterator var31 = Rotation.getShuffled(this.random).iterator();

                                    label125:
                                    while(var31.hasNext()) {
                                        Rotation $$29 = (Rotation)var31.next();
                                        List<StructureTemplate.StructureBlockInfo> $$30 = $$28.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, $$29, this.random);
                                        BoundingBox $$31 = $$28.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, $$29);
                                        int $$33;
                                        if (p_227268_ && $$31.getYSpan() <= 16) {
                                            $$33 = $$30.stream().mapToInt((p_255598_) -> {
                                                if (!$$31.isInside(p_255598_.pos().relative(JigsawBlock.getFrontFacing(p_255598_.state())))) {
                                                    return 0;
                                                } else {
                                                    ResourceKey<StructureTemplatePool> $$2 = readPoolName(p_255598_);
                                                    Optional<? extends Holder<StructureTemplatePool>> $$3 = this.pools.getHolder($$2);
                                                    Optional<Holder<StructureTemplatePool>> $$4 = $$3.map((p_255600_) -> {
                                                        return ((StructureTemplatePool)p_255600_.value()).getFallback();
                                                    });
                                                    int $$5 = (Integer)$$3.map((p_255596_) -> {
                                                        return ((StructureTemplatePool)p_255596_.value()).getMaxSize(this.structureTemplateManager);
                                                    }).orElse(0);
                                                    int $$6 = (Integer)$$4.map((p_255601_) -> {
                                                        return ((StructureTemplatePool)p_255601_.value()).getMaxSize(this.structureTemplateManager);
                                                    }).orElse(0);
                                                    return Math.max($$5, $$6);
                                                }
                                            }).max().orElse(0);
                                        } else {
                                            $$33 = 0;
                                        }

                                        Iterator var36 = $$30.iterator();

                                        StructureTemplatePool.Projection $$39;
                                        boolean $$40;
                                        int $$41;
                                        int $$42;
                                        int $$44;
                                        BoundingBox $$46;
                                        BlockPos $$47;
                                        int $$49;
                                        do {
                                            StructureTemplate.StructureBlockInfo $$34;
                                            do {
                                                if (!var36.hasNext()) {
                                                    continue label125;
                                                }

                                                $$34 = (StructureTemplate.StructureBlockInfo)var36.next();
                                            } while(!JigsawBlock.canAttach($$14, $$34));

                                            BlockPos $$35 = $$34.pos();
                                            BlockPos $$36 = $$17.subtract($$35);
                                            BoundingBox $$37 = $$28.getBoundingBox(this.structureTemplateManager, $$36, $$29);
                                            int $$38 = $$37.minY();
                                            $$39 = $$28.getProjection();
                                            $$40 = $$39 == Projection.RIGID;
                                            $$41 = $$35.getY();
                                            $$42 = $$18 - $$41 + JigsawBlock.getFrontFacing($$14.state()).getStepY();
                                            if ($$10 && $$40) {
                                                $$44 = $$13 + $$42;
                                            } else {
                                                if ($$19 == -1) {
                                                    $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Types.WORLD_SURFACE_WG, p_227269_, p_227270_);
                                                }

                                                $$44 = $$19 - $$41;
                                            }

                                            int $$45 = $$44 - $$38;
                                            $$46 = $$37.moved(0, $$45, 0);
                                            $$47 = $$36.offset(0, $$45, 0);
                                            if ($$33 > 0) {
                                                $$49 = Math.max($$33 + 1, $$46.maxY() - $$46.minY());
                                                $$46.encapsulate(new BlockPos($$46.minX(), $$46.minY() + $$49, $$46.minZ()));
                                            }
                                        } while(Shapes.joinIsNotEmpty((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46).deflate(0.25)), BooleanOp.ONLY_SECOND));

                                        $$26.setValue(Shapes.joinUnoptimized((VoxelShape)$$26.getValue(), Shapes.create(AABB.of($$46)), BooleanOp.ONLY_FIRST));
                                        $$49 = p_227265_.getGroundLevelDelta();
                                        int $$51;
                                        if ($$40) {
                                            $$51 = $$49 - $$42;
                                        } else {
                                            $$51 = $$28.getGroundLevelDelta();
                                        }

                                        PoolElementStructurePiece $$52 = new PoolElementStructurePiece(this.structureTemplateManager, $$28, $$47, $$51, $$29, $$46);
                                        int $$55;
                                        if ($$10) {
                                            $$55 = $$13 + $$18;
                                        } else if ($$40) {
                                            $$55 = $$44 + $$41;
                                        } else {
                                            if ($$19 == -1) {
                                                $$19 = this.chunkGenerator.getFirstFreeHeight($$16.getX(), $$16.getZ(), Types.WORLD_SURFACE_WG, p_227269_, p_227270_);
                                            }

                                            $$55 = $$19 + $$42 / 2;
                                        }

                                        p_227265_.addJunction(new JigsawJunction($$17.getX(), $$55 - $$18 + $$49, $$17.getZ(), $$42, $$39));
                                        $$52.addJunction(new JigsawJunction($$16.getX(), $$55 - $$41 + $$51, $$16.getZ(), -$$42, $$9));
                                        this.pieces.add($$52);
                                        if (p_227267_ + 1 <= this.maxDepth) {
                                            this.placing.addLast(new PieceState($$52, $$26, p_227267_ + 1));
                                        }
                                        continue label129;
                                    }
                                }
                            }
                        }
                    }
                }

                return;
            }
        }

        private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo p_256491_) {
            return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_256491_.nbt().getString("pool")));
        }
    }

    static final class PieceState {
        final PoolElementStructurePiece piece;
        final MutableObject<VoxelShape> free;
        final int depth;

        PieceState(PoolElementStructurePiece p_210311_, MutableObject<VoxelShape> p_210312_, int p_210313_) {
            this.piece = p_210311_;
            this.free = p_210312_;
            this.depth = p_210313_;
        }
    }
}
