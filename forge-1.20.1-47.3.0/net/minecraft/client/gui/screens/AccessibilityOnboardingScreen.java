//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AccessibilityOnboardingTextWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AccessibilityOnboardingScreen extends Screen {
    private static final Component ONBOARDING_NARRATOR_MESSAGE = Component.translatable("accessibility.onboarding.screen.narrator");
    private static final int PADDING = 4;
    private static final int TITLE_PADDING = 16;
    private final PanoramaRenderer panorama;
    private final LogoRenderer logoRenderer;
    private final Options options;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    @Nullable
    private AccessibilityOnboardingTextWidget textWidget;

    public AccessibilityOnboardingScreen(Options p_265483_) {
        super(Component.translatable("accessibility.onboarding.screen.title"));
        this.panorama = new PanoramaRenderer(TitleScreen.CUBE_MAP);
        this.options = p_265483_;
        this.logoRenderer = new LogoRenderer(true);
        this.narratorAvailable = Minecraft.getInstance().getNarrator().isActive();
    }

    public void init() {
        int $$0 = this.initTitleYPos();
        FrameLayout $$1 = new FrameLayout(this.width, this.height - $$0);
        $$1.defaultChildLayoutSetting().alignVerticallyTop().padding(4);
        GridLayout $$2 = (GridLayout)$$1.addChild(new GridLayout());
        $$2.defaultCellSetting().alignHorizontallyCenter().padding(4);
        GridLayout.RowHelper $$3 = $$2.createRowHelper(1);
        $$3.defaultCellSetting().padding(2);
        this.textWidget = new AccessibilityOnboardingTextWidget(this.font, this.title, this.width);
        $$3.addChild(this.textWidget, $$3.newCellSettings().paddingBottom(16));
        AbstractWidget $$4 = this.options.narrator().createButton(this.options, 0, 0, 150);
        $$4.active = this.narratorAvailable;
        $$3.addChild($$4);
        if (this.narratorAvailable) {
            this.setInitialFocus($$4);
        }

        $$3.addChild(CommonButtons.accessibilityTextAndImage((p_280782_) -> {
            this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options));
        }));
        $$3.addChild(CommonButtons.languageTextAndImage((p_280781_) -> {
            this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
        }));
        $$1.addChild(Button.builder(CommonComponents.GUI_CONTINUE, (p_267841_) -> {
            this.onClose();
        }).build(), $$1.newChildLayoutSettings().alignVerticallyBottom().padding(8));
        $$1.arrangeElements();
        FrameLayout.alignInRectangle($$1, 0, $$0, this.width, this.height, 0.5F, 0.0F);
        $$1.visitWidgets(this::addRenderableWidget);
    }

    private int initTitleYPos() {
        return 90;
    }

    public void onClose() {
        this.closeAndSetScreen(new TitleScreen(true, this.logoRenderer));
    }

    private void closeAndSetScreen(Screen p_272914_) {
        this.options.onboardAccessibility = false;
        this.options.save();
        Narrator.getNarrator().clear();
        this.minecraft.setScreen(p_272914_);
    }

    public void render(GuiGraphics p_282353_, int p_265135_, int p_265032_, float p_265387_) {
        this.handleInitialNarrationDelay();
        this.panorama.render(0.0F, 1.0F);
        p_282353_.fill(0, 0, this.width, this.height, -1877995504);
        this.logoRenderer.renderLogo(p_282353_, this.width, 1.0F);
        if (this.textWidget != null) {
            this.textWidget.render(p_282353_, p_265135_, p_265032_, p_265387_);
        }

        super.render(p_282353_, p_265135_, p_265032_, p_265387_);
    }

    private void handleInitialNarrationDelay() {
        if (!this.hasNarrated && this.narratorAvailable) {
            if (this.timer < 40.0F) {
                ++this.timer;
            } else if (this.minecraft.isWindowActive()) {
                Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
                this.hasNarrated = true;
            }
        }

    }
}
