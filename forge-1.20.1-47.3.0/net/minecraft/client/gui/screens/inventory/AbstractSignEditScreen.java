//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSignEditScreen extends Screen {
    private final SignBlockEntity sign;
    private SignText text;
    private final String[] messages;
    private final boolean isFrontText;
    protected final WoodType woodType;
    private int frame;
    private int line;
    @Nullable
    private TextFieldHelper signField;

    public AbstractSignEditScreen(SignBlockEntity p_277842_, boolean p_277719_, boolean p_277969_) {
        this(p_277842_, p_277719_, p_277969_, Component.translatable("sign.edit"));
    }

    public AbstractSignEditScreen(SignBlockEntity p_277792_, boolean p_277607_, boolean p_278039_, Component p_277393_) {
        super(p_277393_);
        this.sign = p_277792_;
        this.text = p_277792_.getText(p_277607_);
        this.isFrontText = p_277607_;
        this.woodType = SignBlock.getWoodType(p_277792_.getBlockState().getBlock());
        this.messages = (String[])IntStream.range(0, 4).mapToObj((p_277214_) -> {
            return this.text.getMessage(p_277214_, p_278039_);
        }).map(Component::getString).toArray((p_249111_) -> {
            return new String[p_249111_];
        });
    }

    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_251194_) -> {
            this.onDone();
        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.signField = new TextFieldHelper(() -> {
            return this.messages[this.line];
        }, this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (p_280850_) -> {
            return this.minecraft.font.width(p_280850_) <= this.sign.getMaxTextLineWidth();
        });
    }

    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }

    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && !this.sign.isRemoved() && !this.sign.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
    }

    public boolean keyPressed(int p_252300_, int p_250424_, int p_250697_) {
        if (p_252300_ == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        } else if (p_252300_ != 264 && p_252300_ != 257 && p_252300_ != 335) {
            return this.signField.keyPressed(p_252300_) ? true : super.keyPressed(p_252300_, p_250424_, p_250697_);
        } else {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
    }

    public boolean charTyped(char p_252008_, int p_251178_) {
        this.signField.charTyped(p_252008_);
        return true;
    }

    public void render(GuiGraphics p_282418_, int p_281700_, int p_283040_, float p_282799_) {
        Lighting.setupForFlatItems();
        this.renderBackground(p_282418_);
        p_282418_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 40, 16777215);
        this.renderSign(p_282418_);
        Lighting.setupFor3DItems();
        super.render(p_282418_, p_281700_, p_283040_, p_282799_);
    }

    public void onClose() {
        this.onDone();
    }

    public void removed() {
        ClientPacketListener $$0 = this.minecraft.getConnection();
        if ($$0 != null) {
            $$0.send((Packet)(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), this.isFrontText, this.messages[0], this.messages[1], this.messages[2], this.messages[3])));
        }

    }

    public boolean isPauseScreen() {
        return false;
    }

    protected abstract void renderSignBackground(GuiGraphics var1, BlockState var2);

    protected abstract Vector3f getSignTextScale();

    protected void offsetSign(GuiGraphics p_282672_, BlockState p_283056_) {
        p_282672_.pose().translate((float)this.width / 2.0F, 90.0F, 50.0F);
    }

    private void renderSign(GuiGraphics p_282006_) {
        BlockState $$1 = this.sign.getBlockState();
        p_282006_.pose().pushPose();
        this.offsetSign(p_282006_, $$1);
        p_282006_.pose().pushPose();
        this.renderSignBackground(p_282006_, $$1);
        p_282006_.pose().popPose();
        this.renderSignText(p_282006_);
        p_282006_.pose().popPose();
    }

    private void renderSignText(GuiGraphics p_282366_) {
        p_282366_.pose().translate(0.0F, 0.0F, 4.0F);
        Vector3f $$1 = this.getSignTextScale();
        p_282366_.pose().scale($$1.x(), $$1.y(), $$1.z());
        int $$2 = this.text.getColor().getTextColor();
        boolean $$3 = this.frame / 6 % 2 == 0;
        int $$4 = this.signField.getCursorPos();
        int $$5 = this.signField.getSelectionPos();
        int $$6 = 4 * this.sign.getTextLineHeight() / 2;
        int $$7 = this.line * this.sign.getTextLineHeight() - $$6;

        int $$8;
        String $$14;
        int $$15;
        int $$16;
        int $$17;
        for($$8 = 0; $$8 < this.messages.length; ++$$8) {
            $$14 = this.messages[$$8];
            if ($$14 != null) {
                if (this.font.isBidirectional()) {
                    $$14 = this.font.bidirectionalShaping($$14);
                }

                $$15 = -this.font.width($$14) / 2;
                p_282366_.drawString(this.font, $$14, $$15, $$8 * this.sign.getTextLineHeight() - $$6, $$2, false);
                if ($$8 == this.line && $$4 >= 0 && $$3) {
                    $$16 = this.font.width($$14.substring(0, Math.max(Math.min($$4, $$14.length()), 0)));
                    $$17 = $$16 - this.font.width($$14) / 2;
                    if ($$4 >= $$14.length()) {
                        p_282366_.drawString(this.font, "_", $$17, $$7, $$2, false);
                    }
                }
            }
        }

        for($$8 = 0; $$8 < this.messages.length; ++$$8) {
            $$14 = this.messages[$$8];
            if ($$14 != null && $$8 == this.line && $$4 >= 0) {
                $$15 = this.font.width($$14.substring(0, Math.max(Math.min($$4, $$14.length()), 0)));
                $$16 = $$15 - this.font.width($$14) / 2;
                if ($$3 && $$4 < $$14.length()) {
                    p_282366_.fill($$16, $$7 - 1, $$16 + 1, $$7 + this.sign.getTextLineHeight(), -16777216 | $$2);
                }

                if ($$5 != $$4) {
                    $$17 = Math.min($$4, $$5);
                    int $$18 = Math.max($$4, $$5);
                    int $$19 = this.font.width($$14.substring(0, $$17)) - this.font.width($$14) / 2;
                    int $$20 = this.font.width($$14.substring(0, $$18)) - this.font.width($$14) / 2;
                    int $$21 = Math.min($$19, $$20);
                    int $$22 = Math.max($$19, $$20);
                    p_282366_.fill(RenderType.guiTextHighlight(), $$21, $$7, $$22, $$7 + this.sign.getTextLineHeight(), -16776961);
                }
            }
        }

    }

    private void setMessage(String p_277913_) {
        this.messages[this.line] = p_277913_;
        this.text = this.text.setMessage(this.line, Component.literal(p_277913_));
        this.sign.setText(this.text, this.isFrontText);
    }

    private void onDone() {
        this.minecraft.setScreen((Screen)null);
    }
}
