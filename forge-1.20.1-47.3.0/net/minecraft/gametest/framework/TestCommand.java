//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.io.IOUtils;

public class TestCommand {
    private static final int DEFAULT_CLEAR_RADIUS = 200;
    private static final int MAX_CLEAR_RADIUS = 1024;
    private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
    private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
    private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final int DEFAULT_X_SIZE = 5;
    private static final int DEFAULT_Y_SIZE = 5;
    private static final int DEFAULT_Z_SIZE = 5;

    public TestCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_127947_) {
        p_127947_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("test").then(Commands.literal("runthis").executes((p_128057_) -> {
            return runNearbyTest((CommandSourceStack)p_128057_.getSource());
        }))).then(Commands.literal("runthese").executes((p_128055_) -> {
            return runAllNearbyTests((CommandSourceStack)p_128055_.getSource());
        }))).then(((LiteralArgumentBuilder)Commands.literal("runfailed").executes((p_128053_) -> {
            return runLastFailedTests((CommandSourceStack)p_128053_.getSource(), false, 0, 8);
        })).then(((RequiredArgumentBuilder)Commands.argument("onlyRequiredTests", BoolArgumentType.bool()).executes((p_128051_) -> {
            return runLastFailedTests((CommandSourceStack)p_128051_.getSource(), BoolArgumentType.getBool(p_128051_, "onlyRequiredTests"), 0, 8);
        })).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128049_) -> {
            return runLastFailedTests((CommandSourceStack)p_128049_.getSource(), BoolArgumentType.getBool(p_128049_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_128049_, "rotationSteps"), 8);
        })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128047_) -> {
            return runLastFailedTests((CommandSourceStack)p_128047_.getSource(), BoolArgumentType.getBool(p_128047_, "onlyRequiredTests"), IntegerArgumentType.getInteger(p_128047_, "rotationSteps"), IntegerArgumentType.getInteger(p_128047_, "testsPerRow"));
        })))))).then(Commands.literal("run").then(((RequiredArgumentBuilder)Commands.argument("testName", TestFunctionArgument.testFunctionArgument()).executes((p_128045_) -> {
            return runTest((CommandSourceStack)p_128045_.getSource(), TestFunctionArgument.getTestFunction(p_128045_, "testName"), 0);
        })).then(Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128043_) -> {
            return runTest((CommandSourceStack)p_128043_.getSource(), TestFunctionArgument.getTestFunction(p_128043_, "testName"), IntegerArgumentType.getInteger(p_128043_, "rotationSteps"));
        }))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("runall").executes((p_128041_) -> {
            return runAllTests((CommandSourceStack)p_128041_.getSource(), 0, 8);
        })).then(((RequiredArgumentBuilder)Commands.argument("testClassName", TestClassNameArgument.testClassName()).executes((p_128039_) -> {
            return runAllTestsInClass((CommandSourceStack)p_128039_.getSource(), TestClassNameArgument.getTestClassName(p_128039_, "testClassName"), 0, 8);
        })).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128037_) -> {
            return runAllTestsInClass((CommandSourceStack)p_128037_.getSource(), TestClassNameArgument.getTestClassName(p_128037_, "testClassName"), IntegerArgumentType.getInteger(p_128037_, "rotationSteps"), 8);
        })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128035_) -> {
            return runAllTestsInClass((CommandSourceStack)p_128035_.getSource(), TestClassNameArgument.getTestClassName(p_128035_, "testClassName"), IntegerArgumentType.getInteger(p_128035_, "rotationSteps"), IntegerArgumentType.getInteger(p_128035_, "testsPerRow"));
        }))))).then(((RequiredArgumentBuilder)Commands.argument("rotationSteps", IntegerArgumentType.integer()).executes((p_128033_) -> {
            return runAllTests((CommandSourceStack)p_128033_.getSource(), IntegerArgumentType.getInteger(p_128033_, "rotationSteps"), 8);
        })).then(Commands.argument("testsPerRow", IntegerArgumentType.integer()).executes((p_128031_) -> {
            return runAllTests((CommandSourceStack)p_128031_.getSource(), IntegerArgumentType.getInteger(p_128031_, "rotationSteps"), IntegerArgumentType.getInteger(p_128031_, "testsPerRow"));
        }))))).then(Commands.literal("export").then(Commands.argument("testName", StringArgumentType.word()).executes((p_128029_) -> {
            return exportTestStructure((CommandSourceStack)p_128029_.getSource(), StringArgumentType.getString(p_128029_, "testName"));
        })))).then(Commands.literal("exportthis").executes((p_128027_) -> {
            return exportNearestTestStructure((CommandSourceStack)p_128027_.getSource());
        }))).then(Commands.literal("import").then(Commands.argument("testName", StringArgumentType.word()).executes((p_128025_) -> {
            return importTestStructure((CommandSourceStack)p_128025_.getSource(), StringArgumentType.getString(p_128025_, "testName"));
        })))).then(((LiteralArgumentBuilder)Commands.literal("pos").executes((p_128023_) -> {
            return showPos((CommandSourceStack)p_128023_.getSource(), "pos");
        })).then(Commands.argument("var", StringArgumentType.word()).executes((p_128021_) -> {
            return showPos((CommandSourceStack)p_128021_.getSource(), StringArgumentType.getString(p_128021_, "var"));
        })))).then(Commands.literal("create").then(((RequiredArgumentBuilder)Commands.argument("testName", StringArgumentType.word()).executes((p_128019_) -> {
            return createNewStructure((CommandSourceStack)p_128019_.getSource(), StringArgumentType.getString(p_128019_, "testName"), 5, 5, 5);
        })).then(((RequiredArgumentBuilder)Commands.argument("width", IntegerArgumentType.integer()).executes((p_128014_) -> {
            return createNewStructure((CommandSourceStack)p_128014_.getSource(), StringArgumentType.getString(p_128014_, "testName"), IntegerArgumentType.getInteger(p_128014_, "width"), IntegerArgumentType.getInteger(p_128014_, "width"), IntegerArgumentType.getInteger(p_128014_, "width"));
        })).then(Commands.argument("height", IntegerArgumentType.integer()).then(Commands.argument("depth", IntegerArgumentType.integer()).executes((p_128007_) -> {
            return createNewStructure((CommandSourceStack)p_128007_.getSource(), StringArgumentType.getString(p_128007_, "testName"), IntegerArgumentType.getInteger(p_128007_, "width"), IntegerArgumentType.getInteger(p_128007_, "height"), IntegerArgumentType.getInteger(p_128007_, "depth"));
        }))))))).then(((LiteralArgumentBuilder)Commands.literal("clearall").executes((p_128000_) -> {
            return clearAllTests((CommandSourceStack)p_128000_.getSource(), 200);
        })).then(Commands.argument("radius", IntegerArgumentType.integer()).executes((p_127949_) -> {
            return clearAllTests((CommandSourceStack)p_127949_.getSource(), IntegerArgumentType.getInteger(p_127949_, "radius"));
        }))));
    }

    private static int createNewStructure(CommandSourceStack p_127968_, String p_127969_, int p_127970_, int p_127971_, int p_127972_) {
        if (p_127970_ <= 48 && p_127971_ <= 48 && p_127972_ <= 48) {
            ServerLevel $$5 = p_127968_.getLevel();
            BlockPos $$6 = BlockPos.containing(p_127968_.getPosition());
            BlockPos $$7 = new BlockPos($$6.getX(), p_127968_.getLevel().getHeightmapPos(Types.WORLD_SURFACE, $$6).getY(), $$6.getZ() + 3);
            StructureUtils.createNewEmptyStructureBlock(p_127969_.toLowerCase(), $$7, new Vec3i(p_127970_, p_127971_, p_127972_), Rotation.NONE, $$5);

            for(int $$8 = 0; $$8 < p_127970_; ++$$8) {
                for(int $$9 = 0; $$9 < p_127972_; ++$$9) {
                    BlockPos $$10 = new BlockPos($$7.getX() + $$8, $$7.getY() + 1, $$7.getZ() + $$9);
                    Block $$11 = Blocks.POLISHED_ANDESITE;
                    BlockInput $$12 = new BlockInput($$11.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
                    $$12.place($$5, $$10, 2);
                }
            }

            StructureUtils.addCommandBlockAndButtonToStartTest($$7, new BlockPos(1, 0, -1), Rotation.NONE, $$5);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
    }

    private static int showPos(CommandSourceStack p_127960_, String p_127961_) throws CommandSyntaxException {
        BlockHitResult $$2 = (BlockHitResult)p_127960_.getPlayerOrException().pick(10.0, 1.0F, false);
        BlockPos $$3 = $$2.getBlockPos();
        ServerLevel $$4 = p_127960_.getLevel();
        Optional<BlockPos> $$5 = StructureUtils.findStructureBlockContainingPos($$3, 15, $$4);
        if (!$$5.isPresent()) {
            $$5 = StructureUtils.findStructureBlockContainingPos($$3, 200, $$4);
        }

        if (!$$5.isPresent()) {
            p_127960_.sendFailure(Component.literal("Can't find a structure block that contains the targeted pos " + $$3));
            return 0;
        } else {
            StructureBlockEntity $$6 = (StructureBlockEntity)$$4.getBlockEntity((BlockPos)$$5.get());
            BlockPos $$7 = $$3.subtract((Vec3i)$$5.get());
            int var10000 = $$7.getX();
            String $$8 = "" + var10000 + ", " + $$7.getY() + ", " + $$7.getZ();
            String $$9 = $$6.getStructurePath();
            Component $$10 = Component.literal($$8).setStyle(Style.EMPTY.withBold(true).withColor(ChatFormatting.GREEN).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.literal("Click to copy to clipboard"))).withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + p_127961_ + " = new BlockPos(" + $$8 + ");")));
            p_127960_.sendSuccess(() -> {
                return Component.literal("Position relative to " + $$9 + ": ").append($$10);
            }, false);
            DebugPackets.sendGameTestAddMarker($$4, new BlockPos($$3), $$8, -2147418368, 10000);
            return 1;
        }
    }

    private static int runNearbyTest(CommandSourceStack p_127951_) {
        BlockPos $$1 = BlockPos.containing(p_127951_.getPosition());
        ServerLevel $$2 = p_127951_.getLevel();
        BlockPos $$3 = StructureUtils.findNearestStructureBlock($$1, 15, $$2);
        if ($$3 == null) {
            say($$2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        } else {
            GameTestRunner.clearMarkers($$2);
            runTest($$2, $$3, (MultipleTestTracker)null);
            return 1;
        }
    }

    private static int runAllNearbyTests(CommandSourceStack p_128002_) {
        BlockPos $$1 = BlockPos.containing(p_128002_.getPosition());
        ServerLevel $$2 = p_128002_.getLevel();
        Collection<BlockPos> $$3 = StructureUtils.findStructureBlocks($$1, 200, $$2);
        if ($$3.isEmpty()) {
            say($$2, "Couldn't find any structure blocks within 200 block radius", ChatFormatting.RED);
            return 1;
        } else {
            GameTestRunner.clearMarkers($$2);
            say(p_128002_, "Running " + $$3.size() + " tests...");
            MultipleTestTracker $$4 = new MultipleTestTracker();
            $$3.forEach((p_127943_) -> {
                runTest($$2, p_127943_, $$4);
            });
            return 1;
        }
    }

    private static void runTest(ServerLevel p_127930_, BlockPos p_127931_, @Nullable MultipleTestTracker p_127932_) {
        StructureBlockEntity $$3 = (StructureBlockEntity)p_127930_.getBlockEntity(p_127931_);
        String $$4 = $$3.getStructurePath();
        TestFunction $$5 = GameTestRegistry.getTestFunction($$4);
        GameTestInfo $$6 = new GameTestInfo($$5, $$3.getRotation(), p_127930_);
        if (p_127932_ != null) {
            p_127932_.addTestToTrack($$6);
            $$6.addListener(new TestSummaryDisplayer(p_127930_, p_127932_));
        }

        runTestPreparation($$5, p_127930_);
        AABB $$7 = StructureUtils.getStructureBounds($$3);
        BlockPos $$8 = BlockPos.containing($$7.minX, $$7.minY, $$7.minZ);
        GameTestRunner.runTest($$6, $$8, GameTestTicker.SINGLETON);
    }

    static void showTestSummaryIfAllDone(ServerLevel p_127997_, MultipleTestTracker p_127998_) {
        if (p_127998_.isDone()) {
            say(p_127997_, "GameTest done! " + p_127998_.getTotalCount() + " tests were run", ChatFormatting.WHITE);
            if (p_127998_.hasFailedRequired()) {
                say(p_127997_, p_127998_.getFailedRequiredCount() + " required tests failed :(", ChatFormatting.RED);
            } else {
                say(p_127997_, "All required tests passed :)", ChatFormatting.GREEN);
            }

            if (p_127998_.hasFailedOptional()) {
                say(p_127997_, p_127998_.getFailedOptionalCount() + " optional tests failed", ChatFormatting.GRAY);
            }
        }

    }

    private static int clearAllTests(CommandSourceStack p_127953_, int p_127954_) {
        ServerLevel $$2 = p_127953_.getLevel();
        GameTestRunner.clearMarkers($$2);
        BlockPos $$3 = BlockPos.containing(p_127953_.getPosition().x, (double)p_127953_.getLevel().getHeightmapPos(Types.WORLD_SURFACE, BlockPos.containing(p_127953_.getPosition())).getY(), p_127953_.getPosition().z);
        GameTestRunner.clearAllTests($$2, $$3, GameTestTicker.SINGLETON, Mth.clamp(p_127954_, 0, 1024));
        return 1;
    }

    private static int runTest(CommandSourceStack p_127979_, TestFunction p_127980_, int p_127981_) {
        ServerLevel $$3 = p_127979_.getLevel();
        BlockPos $$4 = BlockPos.containing(p_127979_.getPosition());
        int $$5 = p_127979_.getLevel().getHeightmapPos(Types.WORLD_SURFACE, $$4).getY();
        BlockPos $$6 = new BlockPos($$4.getX(), $$5, $$4.getZ() + 3);
        GameTestRunner.clearMarkers($$3);
        runTestPreparation(p_127980_, $$3);
        Rotation $$7 = StructureUtils.getRotationForRotationSteps(p_127981_);
        GameTestInfo $$8 = new GameTestInfo(p_127980_, $$7, $$3);
        GameTestRunner.runTest($$8, $$6, GameTestTicker.SINGLETON);
        return 1;
    }

    private static void runTestPreparation(TestFunction p_127994_, ServerLevel p_127995_) {
        Consumer<ServerLevel> $$2 = GameTestRegistry.getBeforeBatchFunction(p_127994_.getBatchName());
        if ($$2 != null) {
            $$2.accept(p_127995_);
        }

    }

    private static int runAllTests(CommandSourceStack p_127956_, int p_127957_, int p_127958_) {
        GameTestRunner.clearMarkers(p_127956_.getLevel());
        Collection<TestFunction> $$3 = GameTestRegistry.getAllTestFunctions();
        say(p_127956_, "Running all " + $$3.size() + " tests...");
        GameTestRegistry.forgetFailedTests();
        runTests(p_127956_, $$3, p_127957_, p_127958_);
        return 1;
    }

    private static int runAllTestsInClass(CommandSourceStack p_127963_, String p_127964_, int p_127965_, int p_127966_) {
        Collection<TestFunction> $$4 = GameTestRegistry.getTestFunctionsForClassName(p_127964_);
        GameTestRunner.clearMarkers(p_127963_.getLevel());
        int var10001 = $$4.size();
        say(p_127963_, "Running " + var10001 + " tests from " + p_127964_ + "...");
        GameTestRegistry.forgetFailedTests();
        runTests(p_127963_, $$4, p_127965_, p_127966_);
        return 1;
    }

    private static int runLastFailedTests(CommandSourceStack p_127983_, boolean p_127984_, int p_127985_, int p_127986_) {
        Collection $$5;
        if (p_127984_) {
            $$5 = (Collection)GameTestRegistry.getLastFailedTests().stream().filter(TestFunction::isRequired).collect(Collectors.toList());
        } else {
            $$5 = GameTestRegistry.getLastFailedTests();
        }

        if ($$5.isEmpty()) {
            say(p_127983_, "No failed tests to rerun");
            return 0;
        } else {
            GameTestRunner.clearMarkers(p_127983_.getLevel());
            int var10001 = $$5.size();
            say(p_127983_, "Rerunning " + var10001 + " failed tests (" + (p_127984_ ? "only required tests" : "including optional tests") + ")");
            runTests(p_127983_, $$5, p_127985_, p_127986_);
            return 1;
        }
    }

    private static void runTests(CommandSourceStack p_127974_, Collection<TestFunction> p_127975_, int p_127976_, int p_127977_) {
        BlockPos $$4 = BlockPos.containing(p_127974_.getPosition());
        BlockPos $$5 = new BlockPos($$4.getX(), p_127974_.getLevel().getHeightmapPos(Types.WORLD_SURFACE, $$4).getY(), $$4.getZ() + 3);
        ServerLevel $$6 = p_127974_.getLevel();
        Rotation $$7 = StructureUtils.getRotationForRotationSteps(p_127976_);
        Collection<GameTestInfo> $$8 = GameTestRunner.runTests(p_127975_, $$5, $$7, $$6, GameTestTicker.SINGLETON, p_127977_);
        MultipleTestTracker $$9 = new MultipleTestTracker($$8);
        $$9.addListener(new TestSummaryDisplayer($$6, $$9));
        $$9.addFailureListener((p_127992_) -> {
            GameTestRegistry.rememberFailedTest(p_127992_.getTestFunction());
        });
    }

    private static void say(CommandSourceStack p_128004_, String p_128005_) {
        p_128004_.sendSuccess(() -> {
            return Component.literal(p_128005_);
        }, false);
    }

    private static int exportNearestTestStructure(CommandSourceStack p_128009_) {
        BlockPos $$1 = BlockPos.containing(p_128009_.getPosition());
        ServerLevel $$2 = p_128009_.getLevel();
        BlockPos $$3 = StructureUtils.findNearestStructureBlock($$1, 15, $$2);
        if ($$3 == null) {
            say($$2, "Couldn't find any structure block within 15 radius", ChatFormatting.RED);
            return 0;
        } else {
            StructureBlockEntity $$4 = (StructureBlockEntity)$$2.getBlockEntity($$3);
            String $$5 = $$4.getStructurePath();
            return exportTestStructure(p_128009_, $$5);
        }
    }

    private static int exportTestStructure(CommandSourceStack p_128011_, String p_128012_) {
        Path $$2 = Paths.get(StructureUtils.testStructuresDir);
        ResourceLocation $$3 = new ResourceLocation("minecraft", p_128012_);
        Path $$4 = p_128011_.getLevel().getStructureManager().getPathToGeneratedStructure($$3, ".nbt");
        Path $$5 = NbtToSnbt.convertStructure(CachedOutput.NO_CACHE, $$4, p_128012_, $$2);
        if ($$5 == null) {
            say(p_128011_, "Failed to export " + $$4);
            return 1;
        } else {
            try {
                Files.createDirectories($$5.getParent());
            } catch (IOException var7) {
                IOException $$6 = var7;
                say(p_128011_, "Could not create folder " + $$5.getParent());
                $$6.printStackTrace();
                return 1;
            }

            say(p_128011_, "Exported " + p_128012_ + " to " + $$5.toAbsolutePath());
            return 0;
        }
    }

    private static int importTestStructure(CommandSourceStack p_128016_, String p_128017_) {
        Path $$2 = Paths.get(StructureUtils.testStructuresDir, p_128017_ + ".snbt");
        ResourceLocation $$3 = new ResourceLocation("minecraft", p_128017_);
        Path $$4 = p_128016_.getLevel().getStructureManager().getPathToGeneratedStructure($$3, ".nbt");

        try {
            BufferedReader $$5 = Files.newBufferedReader($$2);
            String $$6 = IOUtils.toString($$5);
            Files.createDirectories($$4.getParent());
            OutputStream $$7 = Files.newOutputStream($$4);

            try {
                NbtIo.writeCompressed(NbtUtils.snbtToStructure($$6), $$7);
            } catch (Throwable var11) {
                if ($$7 != null) {
                    try {
                        $$7.close();
                    } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                    }
                }

                throw var11;
            }

            if ($$7 != null) {
                $$7.close();
            }

            say(p_128016_, "Imported to " + $$4.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException var12) {
            Exception $$8 = var12;
            System.err.println("Failed to load structure " + p_128017_);
            $$8.printStackTrace();
            return 1;
        }
    }

    private static void say(ServerLevel p_127934_, String p_127935_, ChatFormatting p_127936_) {
        p_127934_.getPlayers((p_127945_) -> {
            return true;
        }).forEach((p_127990_) -> {
            p_127990_.sendSystemMessage(Component.literal("" + p_127936_ + p_127935_));
        });
    }

    private static class TestSummaryDisplayer implements GameTestListener {
        private final ServerLevel level;
        private final MultipleTestTracker tracker;

        public TestSummaryDisplayer(ServerLevel p_128061_, MultipleTestTracker p_128062_) {
            this.level = p_128061_;
            this.tracker = p_128062_;
        }

        public void testStructureLoaded(GameTestInfo p_128064_) {
        }

        public void testPassed(GameTestInfo p_177797_) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }

        public void testFailed(GameTestInfo p_128066_) {
            TestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }
    }
}
