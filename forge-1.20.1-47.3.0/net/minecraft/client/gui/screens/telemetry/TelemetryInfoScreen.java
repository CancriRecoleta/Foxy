//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.telemetry;

import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TelemetryInfoScreen extends Screen {
    private static final int PADDING = 8;
    private static final Component TITLE = Component.translatable("telemetry_info.screen.title");
    private static final Component DESCRIPTION;
    private static final Component BUTTON_GIVE_FEEDBACK;
    private static final Component BUTTON_SHOW_DATA;
    private final Screen lastScreen;
    private final Options options;
    private TelemetryEventWidget telemetryEventWidget;
    private double savedScroll;

    public TelemetryInfoScreen(Screen p_261720_, Options p_262019_) {
        super(TITLE);
        this.lastScreen = p_261720_;
        this.options = p_262019_;
    }

    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), DESCRIPTION);
    }

    protected void init() {
        FrameLayout $$0 = new FrameLayout();
        $$0.defaultChildLayoutSetting().padding(8);
        $$0.setMinHeight(this.height);
        GridLayout $$1 = (GridLayout)$$0.addChild(new GridLayout(), $$0.newChildLayoutSettings().align(0.5F, 0.0F));
        $$1.defaultCellSetting().alignHorizontallyCenter().paddingBottom(8);
        GridLayout.RowHelper $$2 = $$1.createRowHelper(1);
        $$2.addChild(new StringWidget(this.getTitle(), this.font));
        $$2.addChild((new MultiLineTextWidget(DESCRIPTION, this.font)).setMaxWidth(this.width - 16).setCentered(true));
        GridLayout $$3 = this.twoButtonContainer(Button.builder(BUTTON_GIVE_FEEDBACK, this::openFeedbackLink).build(), Button.builder(BUTTON_SHOW_DATA, this::openDataFolder).build());
        $$2.addChild($$3);
        GridLayout $$4 = this.twoButtonContainer(this.createTelemetryButton(), Button.builder(CommonComponents.GUI_DONE, this::openLastScreen).build());
        $$0.addChild($$4, $$0.newChildLayoutSettings().align(0.5F, 1.0F));
        $$0.arrangeElements();
        this.telemetryEventWidget = new TelemetryEventWidget(0, 0, this.width - 40, $$4.getY() - ($$3.getY() + $$3.getHeight()) - 16, this.minecraft.font);
        this.telemetryEventWidget.setScrollAmount(this.savedScroll);
        this.telemetryEventWidget.setOnScrolledListener((p_262168_) -> {
            this.savedScroll = p_262168_;
        });
        this.setInitialFocus(this.telemetryEventWidget);
        $$2.addChild(this.telemetryEventWidget);
        $$0.arrangeElements();
        FrameLayout.alignInRectangle($$0, 0, 0, this.width, this.height, 0.5F, 0.0F);
        $$0.visitWidgets((p_264696_) -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(p_264696_);
        });
    }

    private AbstractWidget createTelemetryButton() {
        AbstractWidget $$0 = this.options.telemetryOptInExtra().createButton(this.options, 0, 0, 150, (p_261857_) -> {
            this.telemetryEventWidget.onOptInChanged(p_261857_);
        });
        $$0.active = this.minecraft.extraTelemetryAvailable();
        return $$0;
    }

    private void openLastScreen(Button p_261672_) {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void openFeedbackLink(Button p_261531_) {
        this.minecraft.setScreen(new ConfirmLinkScreen((p_280897_) -> {
            if (p_280897_) {
                Util.getPlatform().openUri("https://aka.ms/javafeedback?ref=game");
            }

            this.minecraft.setScreen(this);
        }, "https://aka.ms/javafeedback?ref=game", true));
    }

    private void openDataFolder(Button p_261840_) {
        Path $$1 = this.minecraft.getTelemetryManager().getLogDirectory();
        Util.getPlatform().openUri($$1.toUri());
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public void render(GuiGraphics p_281800_, int p_283129_, int p_283666_, float p_282837_) {
        this.renderDirtBackground(p_281800_);
        super.render(p_281800_, p_283129_, p_283666_, p_282837_);
    }

    private GridLayout twoButtonContainer(AbstractWidget p_265763_, AbstractWidget p_265710_) {
        GridLayout $$2 = new GridLayout();
        $$2.defaultCellSetting().alignHorizontallyCenter().paddingHorizontal(4);
        $$2.addChild(p_265763_, 0, 0);
        $$2.addChild(p_265710_, 0, 1);
        return $$2;
    }

    static {
        DESCRIPTION = Component.translatable("telemetry_info.screen.description").withStyle(ChatFormatting.GRAY);
        BUTTON_GIVE_FEEDBACK = Component.translatable("telemetry_info.button.give_feedback");
        BUTTON_SHOW_DATA = Component.translatable("telemetry_info.button.show_data");
    }
}
