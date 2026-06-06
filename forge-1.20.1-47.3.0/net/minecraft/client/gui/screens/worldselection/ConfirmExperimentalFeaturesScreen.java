//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.worldselection;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmExperimentalFeaturesScreen extends Screen {
    private static final Component TITLE = Component.translatable("selectWorld.experimental.title");
    private static final Component MESSAGE = Component.translatable("selectWorld.experimental.message");
    private static final Component DETAILS_BUTTON = Component.translatable("selectWorld.experimental.details");
    private static final int COLUMN_SPACING = 10;
    private static final int DETAILS_BUTTON_WIDTH = 100;
    private final BooleanConsumer callback;
    final Collection<Pack> enabledPacks;
    private final GridLayout layout = (new GridLayout()).columnSpacing(10).rowSpacing(20);

    public ConfirmExperimentalFeaturesScreen(Collection<Pack> p_252011_, BooleanConsumer p_250152_) {
        super(TITLE);
        this.enabledPacks = p_252011_;
        this.callback = p_250152_;
    }

    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), MESSAGE);
    }

    protected void init() {
        super.init();
        GridLayout.RowHelper $$0 = this.layout.createRowHelper(2);
        LayoutSettings $$1 = $$0.newCellSettings().alignHorizontallyCenter();
        $$0.addChild(new StringWidget(this.title, this.font), 2, $$1);
        MultiLineTextWidget $$2 = (MultiLineTextWidget)$$0.addChild((new MultiLineTextWidget(MESSAGE, this.font)).setCentered(true), 2, $$1);
        $$2.setMaxWidth(310);
        $$0.addChild(Button.builder(DETAILS_BUTTON, (p_280898_) -> {
            this.minecraft.setScreen(new DetailsScreen());
        }).width(100).build(), 2, $$1);
        $$0.addChild(Button.builder(CommonComponents.GUI_PROCEED, (p_252248_) -> {
            this.callback.accept(true);
        }).build());
        $$0.addChild(Button.builder(CommonComponents.GUI_BACK, (p_250397_) -> {
            this.callback.accept(false);
        }).build());
        this.layout.visitWidgets((p_269625_) -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(p_269625_);
        });
        this.layout.arrangeElements();
        this.repositionElements();
    }

    protected void repositionElements() {
        FrameLayout.alignInRectangle(this.layout, 0, 0, this.width, this.height, 0.5F, 0.5F);
    }

    public void render(GuiGraphics p_282635_, int p_281935_, int p_283434_, float p_282471_) {
        this.renderBackground(p_282635_);
        super.render(p_282635_, p_281935_, p_283434_, p_282471_);
    }

    public void onClose() {
        this.callback.accept(false);
    }

    @OnlyIn(Dist.CLIENT)
    private class DetailsScreen extends Screen {
        private PackList packList;

        DetailsScreen() {
            super(Component.translatable("selectWorld.experimental.details.title"));
        }

        public void onClose() {
            this.minecraft.setScreen(ConfirmExperimentalFeaturesScreen.this);
        }

        protected void init() {
            super.init();
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (p_251286_) -> {
                this.onClose();
            }).bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20).build());
            this.packList = new PackList(this.minecraft, ConfirmExperimentalFeaturesScreen.this.enabledPacks);
            this.addWidget(this.packList);
        }

        public void render(GuiGraphics p_281368_, int p_281413_, int p_281557_, float p_282492_) {
            this.renderBackground(p_281368_);
            this.packList.render(p_281368_, p_281413_, p_281557_, p_282492_);
            p_281368_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 10, 16777215);
            super.render(p_281368_, p_281413_, p_281557_, p_282492_);
        }

        @OnlyIn(Dist.CLIENT)
        class PackList extends ObjectSelectionList<PackListEntry> {
            public PackList(Minecraft p_249776_, Collection<Pack> p_251183_) {
                int var10002 = DetailsScreen.this.width;
                int var10003 = DetailsScreen.this.height;
                int var10005 = DetailsScreen.this.height - 64;
                Objects.requireNonNull(p_249776_.font);
                super(p_249776_, var10002, var10003, 32, var10005, (9 + 2) * 3);
                Iterator var4 = p_251183_.iterator();

                while(var4.hasNext()) {
                    Pack $$2 = (Pack)var4.next();
                    String $$3 = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_SET, $$2.getRequestedFeatures());
                    if (!$$3.isEmpty()) {
                        Component $$4 = ComponentUtils.mergeStyles($$2.getTitle().copy(), Style.EMPTY.withBold(true));
                        Component $$5 = Component.translatable("selectWorld.experimental.details.entry", $$3);
                        this.addEntry(DetailsScreen.this.new PackListEntry($$4, $$5, MultiLineLabel.create(DetailsScreen.this.font, $$5, this.getRowWidth())));
                    }
                }

            }

            public int getRowWidth() {
                return this.width * 3 / 4;
            }
        }

        @OnlyIn(Dist.CLIENT)
        private class PackListEntry extends ObjectSelectionList.Entry<PackListEntry> {
            private final Component packId;
            private final Component message;
            private final MultiLineLabel splitMessage;

            PackListEntry(Component p_250724_, Component p_248883_, MultiLineLabel p_250949_) {
                this.packId = p_250724_;
                this.message = p_248883_;
                this.splitMessage = p_250949_;
            }

            public void render(GuiGraphics p_282199_, int p_282727_, int p_283089_, int p_283116_, int p_281268_, int p_283038_, int p_283070_, int p_282448_, boolean p_281417_, float p_283226_) {
                p_282199_.drawString(DetailsScreen.this.minecraft.font, this.packId, p_283116_, p_283089_, 16777215);
                MultiLineLabel var10000 = this.splitMessage;
                int var10003 = p_283089_ + 12;
                Objects.requireNonNull(DetailsScreen.this.font);
                var10000.renderLeftAligned(p_282199_, p_283116_, var10003, 9, 16777215);
            }

            public Component getNarration() {
                return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.packId, this.message));
            }
        }
    }
}
