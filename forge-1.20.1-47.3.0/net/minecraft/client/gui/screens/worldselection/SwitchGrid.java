//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.worldselection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
class SwitchGrid {
    private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 44;
    private final List<LabeledSwitch> switches;

    SwitchGrid(List<LabeledSwitch> p_268257_) {
        this.switches = p_268257_;
    }

    public void refreshStates() {
        this.switches.forEach(LabeledSwitch::refreshState);
    }

    public static Builder builder(int p_268344_) {
        return new Builder(p_268344_);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Builder {
        final int width;
        private final List<SwitchBuilder> switchBuilders = new ArrayList();
        int paddingLeft;
        int rowSpacing = 4;
        int rowCount;
        Optional<InfoUnderneathSettings> infoUnderneath = Optional.empty();

        public Builder(int p_267987_) {
            this.width = p_267987_;
        }

        void increaseRow() {
            ++this.rowCount;
        }

        public SwitchBuilder addSwitch(Component p_268004_, BooleanSupplier p_268017_, Consumer<Boolean> p_268320_) {
            SwitchBuilder $$3 = new SwitchBuilder(p_268004_, p_268017_, p_268320_, 44);
            this.switchBuilders.add($$3);
            return $$3;
        }

        public Builder withPaddingLeft(int p_267998_) {
            this.paddingLeft = p_267998_;
            return this;
        }

        public Builder withRowSpacing(int p_270750_) {
            this.rowSpacing = p_270750_;
            return this;
        }

        public SwitchGrid build(Consumer<LayoutElement> p_268301_) {
            GridLayout $$1 = (new GridLayout()).rowSpacing(this.rowSpacing);
            $$1.addChild(SpacerElement.width(this.width - 44), 0, 0);
            $$1.addChild(SpacerElement.width(44), 0, 1);
            List<LabeledSwitch> $$2 = new ArrayList();
            this.rowCount = 0;
            Iterator var4 = this.switchBuilders.iterator();

            while(var4.hasNext()) {
                SwitchBuilder $$3 = (SwitchBuilder)var4.next();
                $$2.add($$3.build(this, $$1, 0));
            }

            $$1.arrangeElements();
            p_268301_.accept($$1);
            SwitchGrid $$4 = new SwitchGrid($$2);
            $$4.refreshStates();
            return $$4;
        }

        public Builder withInfoUnderneath(int p_270730_, boolean p_270594_) {
            this.infoUnderneath = Optional.of(new InfoUnderneathSettings(p_270730_, p_270594_));
            return this;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static record InfoUnderneathSettings(int maxInfoRows, boolean alwaysMaxHeight) {
        InfoUnderneathSettings(int maxInfoRows, boolean alwaysMaxHeight) {
            this.maxInfoRows = maxInfoRows;
            this.alwaysMaxHeight = alwaysMaxHeight;
        }

        public int maxInfoRows() {
            return this.maxInfoRows;
        }

        public boolean alwaysMaxHeight() {
            return this.alwaysMaxHeight;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static record LabeledSwitch(CycleButton<Boolean> button, BooleanSupplier stateSupplier, @Nullable BooleanSupplier isActiveCondition) {
        LabeledSwitch(CycleButton<Boolean> button, BooleanSupplier stateSupplier, @Nullable BooleanSupplier isActiveCondition) {
            this.button = button;
            this.stateSupplier = stateSupplier;
            this.isActiveCondition = isActiveCondition;
        }

        public void refreshState() {
            this.button.setValue(this.stateSupplier.getAsBoolean());
            if (this.isActiveCondition != null) {
                this.button.active = this.isActiveCondition.getAsBoolean();
            }

        }

        public CycleButton<Boolean> button() {
            return this.button;
        }

        public BooleanSupplier stateSupplier() {
            return this.stateSupplier;
        }

        @Nullable
        public BooleanSupplier isActiveCondition() {
            return this.isActiveCondition;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SwitchBuilder {
        private final Component label;
        private final BooleanSupplier stateSupplier;
        private final Consumer<Boolean> onClicked;
        @Nullable
        private Component info;
        @Nullable
        private BooleanSupplier isActiveCondition;
        private final int buttonWidth;

        SwitchBuilder(Component p_268282_, BooleanSupplier p_268294_, Consumer<Boolean> p_268132_, int p_268250_) {
            this.label = p_268282_;
            this.stateSupplier = p_268294_;
            this.onClicked = p_268132_;
            this.buttonWidth = p_268250_;
        }

        public SwitchBuilder withIsActiveCondition(BooleanSupplier p_267966_) {
            this.isActiveCondition = p_267966_;
            return this;
        }

        public SwitchBuilder withInfo(Component p_268240_) {
            this.info = p_268240_;
            return this;
        }

        LabeledSwitch build(Builder p_270513_, GridLayout p_271004_, int p_270506_) {
            p_270513_.increaseRow();
            StringWidget $$3 = (new StringWidget(this.label, Minecraft.getInstance().font)).alignLeft();
            p_271004_.addChild($$3, p_270513_.rowCount, p_270506_, p_271004_.newCellSettings().align(0.0F, 0.5F).paddingLeft(p_270513_.paddingLeft));
            Optional<InfoUnderneathSettings> $$4 = p_270513_.infoUnderneath;
            CycleButton.Builder<Boolean> $$5 = CycleButton.onOffBuilder(this.stateSupplier.getAsBoolean());
            $$5.displayOnlyValue();
            boolean $$6 = this.info != null && !$$4.isPresent();
            if ($$6) {
                Tooltip $$7 = Tooltip.create(this.info);
                $$5.withTooltip((p_269644_) -> {
                    return $$7;
                });
            }

            if (this.info != null && !$$6) {
                $$5.withCustomNarration((p_269645_) -> {
                    return CommonComponents.joinForNarration(this.label, p_269645_.createDefaultNarrationMessage(), this.info);
                });
            } else {
                $$5.withCustomNarration((p_268230_) -> {
                    return CommonComponents.joinForNarration(this.label, p_268230_.createDefaultNarrationMessage());
                });
            }

            CycleButton<Boolean> $$8 = $$5.create(0, 0, this.buttonWidth, 20, Component.empty(), (p_267942_, p_268251_) -> {
                this.onClicked.accept(p_268251_);
            });
            if (this.isActiveCondition != null) {
                $$8.active = this.isActiveCondition.getAsBoolean();
            }

            p_271004_.addChild($$8, p_270513_.rowCount, p_270506_ + 1, p_271004_.newCellSettings().alignHorizontallyRight());
            if (this.info != null) {
                $$4.ifPresent((p_269649_) -> {
                    Component $$4 = this.info.copy().withStyle(ChatFormatting.GRAY);
                    Font $$5 = Minecraft.getInstance().font;
                    MultiLineTextWidget $$6 = new MultiLineTextWidget($$4, $$5);
                    $$6.setMaxWidth(p_270513_.width - p_270513_.paddingLeft - this.buttonWidth);
                    $$6.setMaxRows(p_269649_.maxInfoRows());
                    p_270513_.increaseRow();
                    int var10000;
                    if (p_269649_.alwaysMaxHeight) {
                        Objects.requireNonNull($$5);
                        var10000 = 9 * p_269649_.maxInfoRows - $$6.getHeight();
                    } else {
                        var10000 = 0;
                    }

                    int $$7 = var10000;
                    p_271004_.addChild($$6, p_270513_.rowCount, p_270506_, p_271004_.newCellSettings().paddingTop(-p_270513_.rowSpacing).paddingBottom($$7));
                });
            }

            return new LabeledSwitch($$8, this.stateSupplier, this.isActiveCondition);
        }
    }
}
