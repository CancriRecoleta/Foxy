//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JigsawBlockEditScreen extends Screen {
    private static final int MAX_LEVELS = 7;
    private static final Component JOINT_LABEL = Component.translatable("jigsaw_block.joint_label");
    private static final Component POOL_LABEL = Component.translatable("jigsaw_block.pool");
    private static final Component NAME_LABEL = Component.translatable("jigsaw_block.name");
    private static final Component TARGET_LABEL = Component.translatable("jigsaw_block.target");
    private static final Component FINAL_STATE_LABEL = Component.translatable("jigsaw_block.final_state");
    private final JigsawBlockEntity jigsawEntity;
    private EditBox nameEdit;
    private EditBox targetEdit;
    private EditBox poolEdit;
    private EditBox finalStateEdit;
    int levels;
    private boolean keepJigsaws = true;
    private CycleButton<JigsawBlockEntity.JointType> jointButton;
    private Button doneButton;
    private Button generateButton;
    private JigsawBlockEntity.JointType joint;

    public JigsawBlockEditScreen(JigsawBlockEntity p_98949_) {
        super(GameNarrator.NO_TITLE);
        this.jigsawEntity = p_98949_;
    }

    public void tick() {
        this.nameEdit.tick();
        this.targetEdit.tick();
        this.poolEdit.tick();
        this.finalStateEdit.tick();
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen((Screen)null);
    }

    private void onCancel() {
        this.minecraft.setScreen((Screen)null);
    }

    private void sendToServer() {
        this.minecraft.getConnection().send((Packet)(new ServerboundSetJigsawBlockPacket(this.jigsawEntity.getBlockPos(), new ResourceLocation(this.nameEdit.getValue()), new ResourceLocation(this.targetEdit.getValue()), new ResourceLocation(this.poolEdit.getValue()), this.finalStateEdit.getValue(), this.joint)));
    }

    private void sendGenerate() {
        this.minecraft.getConnection().send((Packet)(new ServerboundJigsawGeneratePacket(this.jigsawEntity.getBlockPos(), this.levels, this.keepJigsaws)));
    }

    public void onClose() {
        this.onCancel();
    }

    protected void init() {
        this.poolEdit = new EditBox(this.font, this.width / 2 - 152, 20, 300, 20, Component.translatable("jigsaw_block.pool"));
        this.poolEdit.setMaxLength(128);
        this.poolEdit.setValue(this.jigsawEntity.getPool().location().toString());
        this.poolEdit.setResponder((p_98986_) -> {
            this.updateValidity();
        });
        this.addWidget(this.poolEdit);
        this.nameEdit = new EditBox(this.font, this.width / 2 - 152, 55, 300, 20, Component.translatable("jigsaw_block.name"));
        this.nameEdit.setMaxLength(128);
        this.nameEdit.setValue(this.jigsawEntity.getName().toString());
        this.nameEdit.setResponder((p_98981_) -> {
            this.updateValidity();
        });
        this.addWidget(this.nameEdit);
        this.targetEdit = new EditBox(this.font, this.width / 2 - 152, 90, 300, 20, Component.translatable("jigsaw_block.target"));
        this.targetEdit.setMaxLength(128);
        this.targetEdit.setValue(this.jigsawEntity.getTarget().toString());
        this.targetEdit.setResponder((p_98977_) -> {
            this.updateValidity();
        });
        this.addWidget(this.targetEdit);
        this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, Component.translatable("jigsaw_block.final_state"));
        this.finalStateEdit.setMaxLength(256);
        this.finalStateEdit.setValue(this.jigsawEntity.getFinalState());
        this.addWidget(this.finalStateEdit);
        this.joint = this.jigsawEntity.getJoint();
        int $$0 = this.font.width((FormattedText)JOINT_LABEL) + 10;
        this.jointButton = (CycleButton)this.addRenderableWidget(CycleButton.builder(JigsawBlockEntity.JointType::getTranslatedName).withValues((Object[])JointType.values()).withInitialValue(this.joint).displayOnlyValue().create(this.width / 2 - 152 + $$0, 150, 300 - $$0, 20, JOINT_LABEL, (p_169765_, p_169766_) -> {
            this.joint = p_169766_;
        }));
        boolean $$1 = JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical();
        this.jointButton.active = $$1;
        this.jointButton.visible = $$1;
        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, CommonComponents.EMPTY, 0.0) {
            {
                this.updateMessage();
            }

            protected void updateMessage() {
                this.setMessage(Component.translatable("jigsaw_block.levels", JigsawBlockEditScreen.this.levels));
            }

            protected void applyValue() {
                JigsawBlockEditScreen.this.levels = Mth.floor(Mth.clampedLerp(0.0, 7.0, this.value));
            }
        });
        this.addRenderableWidget(CycleButton.onOffBuilder(this.keepJigsaws).create(this.width / 2 - 50, 180, 100, 20, Component.translatable("jigsaw_block.keep_jigsaws"), (p_169768_, p_169769_) -> {
            this.keepJigsaws = p_169769_;
        }));
        this.generateButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("jigsaw_block.generate"), (p_98979_) -> {
            this.onDone();
            this.sendGenerate();
        }).bounds(this.width / 2 + 54, 180, 100, 20).build());
        this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_98973_) -> {
            this.onDone();
        }).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_98964_) -> {
            this.onCancel();
        }).bounds(this.width / 2 + 4, 210, 150, 20).build());
        this.setInitialFocus(this.poolEdit);
        this.updateValidity();
    }

    private void updateValidity() {
        boolean $$0 = ResourceLocation.isValidResourceLocation(this.nameEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.targetEdit.getValue()) && ResourceLocation.isValidResourceLocation(this.poolEdit.getValue());
        this.doneButton.active = $$0;
        this.generateButton.active = $$0;
    }

    public void resize(Minecraft p_98960_, int p_98961_, int p_98962_) {
        String $$3 = this.nameEdit.getValue();
        String $$4 = this.targetEdit.getValue();
        String $$5 = this.poolEdit.getValue();
        String $$6 = this.finalStateEdit.getValue();
        int $$7 = this.levels;
        JigsawBlockEntity.JointType $$8 = this.joint;
        this.init(p_98960_, p_98961_, p_98962_);
        this.nameEdit.setValue($$3);
        this.targetEdit.setValue($$4);
        this.poolEdit.setValue($$5);
        this.finalStateEdit.setValue($$6);
        this.levels = $$7;
        this.joint = $$8;
        this.jointButton.setValue($$8);
    }

    public boolean keyPressed(int p_98951_, int p_98952_, int p_98953_) {
        if (super.keyPressed(p_98951_, p_98952_, p_98953_)) {
            return true;
        } else if (!this.doneButton.active || p_98951_ != 257 && p_98951_ != 335) {
            return false;
        } else {
            this.onDone();
            return true;
        }
    }

    public void render(GuiGraphics p_282514_, int p_98956_, int p_98957_, float p_98958_) {
        this.renderBackground(p_282514_);
        p_282514_.drawString(this.font, (Component)POOL_LABEL, this.width / 2 - 153, 10, 10526880);
        this.poolEdit.render(p_282514_, p_98956_, p_98957_, p_98958_);
        p_282514_.drawString(this.font, (Component)NAME_LABEL, this.width / 2 - 153, 45, 10526880);
        this.nameEdit.render(p_282514_, p_98956_, p_98957_, p_98958_);
        p_282514_.drawString(this.font, (Component)TARGET_LABEL, this.width / 2 - 153, 80, 10526880);
        this.targetEdit.render(p_282514_, p_98956_, p_98957_, p_98958_);
        p_282514_.drawString(this.font, (Component)FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
        this.finalStateEdit.render(p_282514_, p_98956_, p_98957_, p_98958_);
        if (JigsawBlock.getFrontFacing(this.jigsawEntity.getBlockState()).getAxis().isVertical()) {
            p_282514_.drawString(this.font, (Component)JOINT_LABEL, this.width / 2 - 153, 156, 16777215);
        }

        super.render(p_282514_, p_98956_, p_98957_, p_98958_);
    }
}
