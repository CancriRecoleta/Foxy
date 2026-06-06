//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestListener {
    private final GameTestInfo originalTestInfo;
    private final GameTestTicker testTicker;
    private final BlockPos structurePos;
    int attempts;
    int successes;

    public ReportGameListener(GameTestInfo p_177692_, GameTestTicker p_177693_, BlockPos p_177694_) {
        this.originalTestInfo = p_177692_;
        this.testTicker = p_177693_;
        this.structurePos = p_177694_;
        this.attempts = 0;
        this.successes = 0;
    }

    public void testStructureLoaded(GameTestInfo p_177718_) {
        spawnBeacon(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
        ++this.attempts;
    }

    public void testPassed(GameTestInfo p_177729_) {
        ++this.successes;
        if (!p_177729_.isFlaky()) {
            String var10001 = p_177729_.getTestName();
            reportPassed(p_177729_, var10001 + " passed! (" + p_177729_.getRunTime() + "ms)");
        } else {
            if (this.successes >= p_177729_.requiredSuccesses()) {
                reportPassed(p_177729_, "" + p_177729_ + " passed " + this.successes + " times of " + this.attempts + " attempts.");
            } else {
                say(this.originalTestInfo.getLevel(), ChatFormatting.GREEN, "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
                this.rerunTest();
            }

        }
    }

    public void testFailed(GameTestInfo p_177737_) {
        if (!p_177737_.isFlaky()) {
            reportFailure(p_177737_, p_177737_.getError());
        } else {
            TestFunction $$1 = this.originalTestInfo.getTestFunction();
            GameTestInfo var10000 = this.originalTestInfo;
            String $$2 = "Flaky test " + var10000 + " failed, attempt: " + this.attempts + "/" + $$1.getMaxAttempts();
            if ($$1.getRequiredSuccesses() > 1) {
                $$2 = $$2 + ", successes: " + this.successes + " (" + $$1.getRequiredSuccesses() + " required)";
            }

            say(this.originalTestInfo.getLevel(), ChatFormatting.YELLOW, $$2);
            if (p_177737_.maxAttempts() - this.attempts + this.successes >= p_177737_.requiredSuccesses()) {
                this.rerunTest();
            } else {
                reportFailure(p_177737_, new ExhaustedAttemptsException(this.attempts, this.successes, p_177737_));
            }

        }
    }

    public static void reportPassed(GameTestInfo p_177723_, String p_177724_) {
        spawnBeacon(p_177723_, Blocks.LIME_STAINED_GLASS);
        visualizePassedTest(p_177723_, p_177724_);
    }

    private static void visualizePassedTest(GameTestInfo p_177731_, String p_177732_) {
        say(p_177731_.getLevel(), ChatFormatting.GREEN, p_177732_);
        GlobalTestReporter.onTestSuccess(p_177731_);
    }

    protected static void reportFailure(GameTestInfo p_177726_, Throwable p_177727_) {
        spawnBeacon(p_177726_, p_177726_.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        spawnLectern(p_177726_, Util.describeError(p_177727_));
        visualizeFailedTest(p_177726_, p_177727_);
    }

    protected static void visualizeFailedTest(GameTestInfo p_177734_, Throwable p_177735_) {
        String var10000 = p_177735_.getMessage();
        String $$2 = var10000 + (p_177735_.getCause() == null ? "" : " cause: " + Util.describeError(p_177735_.getCause()));
        var10000 = p_177734_.isRequired() ? "" : "(optional) ";
        String $$3 = var10000 + p_177734_.getTestName() + " failed! " + $$2;
        say(p_177734_.getLevel(), p_177734_.isRequired() ? ChatFormatting.RED : ChatFormatting.YELLOW, $$3);
        Throwable $$4 = (Throwable)MoreObjects.firstNonNull(ExceptionUtils.getRootCause(p_177735_), p_177735_);
        if ($$4 instanceof GameTestAssertPosException $$5) {
            showRedBox(p_177734_.getLevel(), $$5.getAbsolutePos(), $$5.getMessageToShowAtBlock());
        }

        GlobalTestReporter.onTestFailed(p_177734_);
    }

    private void rerunTest() {
        this.originalTestInfo.clearStructure();
        GameTestInfo $$0 = new GameTestInfo(this.originalTestInfo.getTestFunction(), this.originalTestInfo.getRotation(), this.originalTestInfo.getLevel());
        $$0.startExecution();
        this.testTicker.add($$0);
        $$0.addListener(this);
        $$0.spawnStructure(this.structurePos, 2);
    }

    protected static void spawnBeacon(GameTestInfo p_177720_, Block p_177721_) {
        ServerLevel $$2 = p_177720_.getLevel();
        BlockPos $$3 = p_177720_.getStructureBlockPos();
        BlockPos $$4 = new BlockPos(-1, -1, -1);
        BlockPos $$5 = StructureTemplate.transform($$3.offset($$4), Mirror.NONE, p_177720_.getRotation(), $$3);
        $$2.setBlockAndUpdate($$5, Blocks.BEACON.defaultBlockState().rotate(p_177720_.getRotation()));
        BlockPos $$6 = $$5.offset(0, 1, 0);
        $$2.setBlockAndUpdate($$6, p_177721_.defaultBlockState());

        for(int $$7 = -1; $$7 <= 1; ++$$7) {
            for(int $$8 = -1; $$8 <= 1; ++$$8) {
                BlockPos $$9 = $$5.offset($$7, -1, $$8);
                $$2.setBlockAndUpdate($$9, Blocks.IRON_BLOCK.defaultBlockState());
            }
        }

    }

    private static void spawnLectern(GameTestInfo p_177739_, String p_177740_) {
        ServerLevel $$2 = p_177739_.getLevel();
        BlockPos $$3 = p_177739_.getStructureBlockPos();
        BlockPos $$4 = new BlockPos(-1, 1, -1);
        BlockPos $$5 = StructureTemplate.transform($$3.offset($$4), Mirror.NONE, p_177739_.getRotation(), $$3);
        $$2.setBlockAndUpdate($$5, Blocks.LECTERN.defaultBlockState().rotate(p_177739_.getRotation()));
        BlockState $$6 = $$2.getBlockState($$5);
        ItemStack $$7 = createBook(p_177739_.getTestName(), p_177739_.isRequired(), p_177740_);
        LecternBlock.tryPlaceBook((Entity)null, $$2, $$5, $$6, $$7);
    }

    private static ItemStack createBook(String p_177711_, boolean p_177712_, String p_177713_) {
        ItemStack $$3 = new ItemStack(Items.WRITABLE_BOOK);
        ListTag $$4 = new ListTag();
        StringBuffer $$5 = new StringBuffer();
        Arrays.stream(p_177711_.split("\\.")).forEach((p_177716_) -> {
            $$5.append(p_177716_).append('\n');
        });
        if (!p_177712_) {
            $$5.append("(optional)\n");
        }

        $$5.append("-------------------\n");
        $$4.add(StringTag.valueOf("" + $$5 + p_177713_));
        $$3.addTagElement("pages", $$4);
        return $$3;
    }

    protected static void say(ServerLevel p_177701_, ChatFormatting p_177702_, String p_177703_) {
        p_177701_.getPlayers((p_177705_) -> {
            return true;
        }).forEach((p_177709_) -> {
            p_177709_.sendSystemMessage(Component.literal(p_177703_).withStyle(p_177702_));
        });
    }

    private static void showRedBox(ServerLevel p_177697_, BlockPos p_177698_, String p_177699_) {
        DebugPackets.sendGameTestAddMarker(p_177697_, p_177698_, p_177699_, -2130771968, Integer.MAX_VALUE);
    }
}
