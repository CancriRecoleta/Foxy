//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity.Mode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandBlockEditScreen extends AbstractCommandBlockEditScreen {
    private final CommandBlockEntity autoCommandBlock;
    private CycleButton<CommandBlockEntity.Mode> modeButton;
    private CycleButton<Boolean> conditionalButton;
    private CycleButton<Boolean> autoexecButton;
    private CommandBlockEntity.Mode mode;
    private boolean conditional;
    private boolean autoexec;

    public CommandBlockEditScreen(CommandBlockEntity p_98382_) {
        this.mode = Mode.REDSTONE;
        this.autoCommandBlock = p_98382_;
    }

    BaseCommandBlock getCommandBlock() {
        return this.autoCommandBlock.getCommandBlock();
    }

    int getPreviousY() {
        return 135;
    }

    protected void init() {
        super.init();
        this.modeButton = (CycleButton)this.addRenderableWidget(CycleButton.builder((p_287312_) -> {
            MutableComponent var10000;
            switch (p_287312_) {
                case SEQUENCE -> var10000 = Component.translatable("advMode.mode.sequence");
                case AUTO -> var10000 = Component.translatable("advMode.mode.auto");
                case REDSTONE -> var10000 = Component.translatable("advMode.mode.redstone");
                default -> throw new IncompatibleClassChangeError();
            }

            return var10000;
        }).withValues((Object[])Mode.values()).displayOnlyValue().withInitialValue(this.mode).create(this.width / 2 - 50 - 100 - 4, 165, 100, 20, Component.translatable("advMode.mode"), (p_169721_, p_169722_) -> {
            this.mode = p_169722_;
        }));
        this.conditionalButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.conditional"), Component.translatable("advMode.mode.unconditional")).displayOnlyValue().withInitialValue(this.conditional).create(this.width / 2 - 50, 165, 100, 20, Component.translatable("advMode.type"), (p_169727_, p_169728_) -> {
            this.conditional = p_169728_;
        }));
        this.autoexecButton = (CycleButton)this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("advMode.mode.autoexec.bat"), Component.translatable("advMode.mode.redstoneTriggered")).displayOnlyValue().withInitialValue(this.autoexec).create(this.width / 2 + 50 + 4, 165, 100, 20, Component.translatable("advMode.triggering"), (p_169724_, p_169725_) -> {
            this.autoexec = p_169725_;
        }));
        this.enableControls(false);
    }

    private void enableControls(boolean p_169730_) {
        this.doneButton.active = p_169730_;
        this.outputButton.active = p_169730_;
        this.modeButton.active = p_169730_;
        this.conditionalButton.active = p_169730_;
        this.autoexecButton.active = p_169730_;
    }

    public void updateGui() {
        BaseCommandBlock $$0 = this.autoCommandBlock.getCommandBlock();
        this.commandEdit.setValue($$0.getCommand());
        boolean $$1 = $$0.isTrackOutput();
        this.mode = this.autoCommandBlock.getMode();
        this.conditional = this.autoCommandBlock.isConditional();
        this.autoexec = this.autoCommandBlock.isAutomatic();
        this.outputButton.setValue($$1);
        this.modeButton.setValue(this.mode);
        this.conditionalButton.setValue(this.conditional);
        this.autoexecButton.setValue(this.autoexec);
        this.updatePreviousOutput($$1);
        this.enableControls(true);
    }

    public void resize(Minecraft p_98386_, int p_98387_, int p_98388_) {
        super.resize(p_98386_, p_98387_, p_98388_);
        this.enableControls(true);
    }

    protected void populateAndSendPacket(BaseCommandBlock p_98384_) {
        this.minecraft.getConnection().send((Packet)(new ServerboundSetCommandBlockPacket(BlockPos.containing(p_98384_.getPosition()), this.commandEdit.getValue(), this.mode, p_98384_.isTrackOutput(), this.conditional, this.autoexec)));
    }
}
