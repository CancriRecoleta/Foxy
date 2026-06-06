//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class UnicodeGlyphButton extends ExtendedButton {
    public String glyph;
    public float glyphScale;

    public UnicodeGlyphButton(int xPos, int yPos, int width, int height, Component displayString, String glyph, float glyphScale, Button.OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler);
        this.glyph = glyph;
        this.glyphScale = glyphScale;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = !this.active ? 0 : (this.isHoveredOrFocused() ? 2 : 1);
            guiGraphics.blitWithBorder(WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2);
            Component buttonText = this.createNarrationMessage();
            int glyphWidth = (int)((float)mc.font.width(this.glyph) * this.glyphScale);
            int strWidth = mc.font.width((FormattedText)buttonText);
            int ellipsisWidth = mc.font.width("...");
            int totalWidth = strWidth + glyphWidth;
            if (totalWidth > this.width - 6 && totalWidth > ellipsisWidth) {
                FormattedText var10000 = mc.font.substrByWidth(buttonText, this.width - 6 - ellipsisWidth);
                buttonText = Component.literal(var10000.getString().trim() + "...");
            }

            strWidth = mc.font.width((FormattedText)buttonText);
            int var12 = glyphWidth + strWidth;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(this.glyphScale, this.glyphScale, 1.0F);
            guiGraphics.drawCenteredString(mc.font, (Component)Component.literal(this.glyph), (int)((float)(this.getX() + this.width / 2 - strWidth / 2) / this.glyphScale - (float)glyphWidth / (2.0F * this.glyphScale) + 2.0F), (int)(((float)this.getY() + (float)(this.height - 8) / this.glyphScale / 2.0F - 1.0F) / this.glyphScale), this.getFGColor());
            guiGraphics.pose().popPose();
            guiGraphics.drawCenteredString(mc.font, (Component)buttonText, (int)((float)(this.getX() + this.width / 2) + (float)glyphWidth / this.glyphScale), this.getY() + (this.height - 8) / 2, this.getFGColor());
        }

    }
}
