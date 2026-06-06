//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.telemetry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TelemetryEventWidget extends AbstractScrollWidget {
    private static final int HEADER_HORIZONTAL_PADDING = 32;
    private static final String TELEMETRY_REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
    private static final String TELEMETRY_OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
    private static final Component PROPERTY_TITLE;
    private final Font font;
    private Content content;
    @Nullable
    private DoubleConsumer onScrolledListener;

    public TelemetryEventWidget(int p_261584_, int p_261895_, int p_261803_, int p_261967_, Font p_261662_) {
        super(p_261584_, p_261895_, p_261803_, p_261967_, Component.empty());
        this.font = p_261662_;
        this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
    }

    public void onOptInChanged(boolean p_261772_) {
        this.content = this.buildContent(p_261772_);
        this.setScrollAmount(this.scrollAmount());
    }

    private Content buildContent(boolean p_261628_) {
        ContentBuilder $$1 = new ContentBuilder(this.containerWidth());
        List<TelemetryEventType> $$2 = new ArrayList(TelemetryEventType.values());
        $$2.sort(Comparator.comparing(TelemetryEventType::isOptIn));
        if (!p_261628_) {
            $$2.removeIf(TelemetryEventType::isOptIn);
        }

        for(int $$3 = 0; $$3 < $$2.size(); ++$$3) {
            TelemetryEventType $$4 = (TelemetryEventType)$$2.get($$3);
            this.addEventType($$1, $$4);
            if ($$3 < $$2.size() - 1) {
                Objects.requireNonNull(this.font);
                $$1.addSpacer(9);
            }
        }

        return $$1.build();
    }

    public void setOnScrolledListener(@Nullable DoubleConsumer p_261686_) {
        this.onScrolledListener = p_261686_;
    }

    protected void setScrollAmount(double p_261736_) {
        super.setScrollAmount(p_261736_);
        if (this.onScrolledListener != null) {
            this.onScrolledListener.accept(this.scrollAmount());
        }

    }

    protected int getInnerHeight() {
        return this.content.container().getHeight();
    }

    protected double scrollRate() {
        Objects.requireNonNull(this.font);
        return 9.0;
    }

    protected void renderContents(GuiGraphics p_283081_, int p_283426_, int p_282414_, float p_283358_) {
        int $$4 = this.getY() + this.innerPadding();
        int $$5 = this.getX() + this.innerPadding();
        p_283081_.pose().pushPose();
        p_283081_.pose().translate((double)$$5, (double)$$4, 0.0);
        this.content.container().visitWidgets((p_280896_) -> {
            p_280896_.render(p_283081_, p_283426_, p_282414_, p_283358_);
        });
        p_283081_.pose().popPose();
    }

    protected void updateWidgetNarration(NarrationElementOutput p_261538_) {
        p_261538_.add(NarratedElementType.TITLE, this.content.narration());
    }

    private void addEventType(ContentBuilder p_261823_, TelemetryEventType p_262127_) {
        String $$2 = p_262127_.isOptIn() ? "telemetry.event.optional" : "telemetry.event.required";
        p_261823_.addHeader(this.font, Component.translatable($$2, p_262127_.title()));
        p_261823_.addHeader(this.font, p_262127_.description().withStyle(ChatFormatting.GRAY));
        Objects.requireNonNull(this.font);
        p_261823_.addSpacer(9 / 2);
        p_261823_.addLine(this.font, PROPERTY_TITLE, 2);
        this.addEventTypeProperties(p_262127_, p_261823_);
    }

    private void addEventTypeProperties(TelemetryEventType p_262105_, ContentBuilder p_261932_) {
        Iterator var3 = p_262105_.properties().iterator();

        while(var3.hasNext()) {
            TelemetryProperty<?> $$2 = (TelemetryProperty)var3.next();
            p_261932_.addLine(this.font, $$2.title());
        }

    }

    private int containerWidth() {
        return this.width - this.totalInnerPadding();
    }

    static {
        PROPERTY_TITLE = Component.translatable("telemetry_info.property_title").withStyle(ChatFormatting.UNDERLINE);
    }

    @OnlyIn(Dist.CLIENT)
    private static record Content(GridLayout container, Component narration) {
        Content(GridLayout container, Component narration) {
            this.container = container;
            this.narration = narration;
        }

        public GridLayout container() {
            return this.container;
        }

        public Component narration() {
            return this.narration;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class ContentBuilder {
        private final int width;
        private final GridLayout grid;
        private final GridLayout.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final MutableComponent narration = Component.empty();

        public ContentBuilder(int p_261784_) {
            this.width = p_261784_;
            this.grid = new GridLayout();
            this.grid.defaultCellSetting().alignHorizontallyLeft();
            this.helper = this.grid.createRowHelper(1);
            this.helper.addChild(SpacerElement.width(p_261784_));
            this.alignHeader = this.helper.newCellSettings().alignHorizontallyCenter().paddingHorizontal(32);
        }

        public void addLine(Font p_261503_, Component p_261550_) {
            this.addLine(p_261503_, p_261550_, 0);
        }

        public void addLine(Font p_261894_, Component p_261816_, int p_261721_) {
            this.helper.addChild((new MultiLineTextWidget(p_261816_, p_261894_)).setMaxWidth(this.width), this.helper.newCellSettings().paddingBottom(p_261721_));
            this.narration.append(p_261816_).append("\n");
        }

        public void addHeader(Font p_261496_, Component p_261670_) {
            this.helper.addChild((new MultiLineTextWidget(p_261670_, p_261496_)).setMaxWidth(this.width - 64).setCentered(true), this.alignHeader);
            this.narration.append(p_261670_).append("\n");
        }

        public void addSpacer(int p_261997_) {
            this.helper.addChild(SpacerElement.height(p_261997_));
        }

        public Content build() {
            this.grid.arrangeElements();
            return new Content(this.grid, this.narration);
        }
    }
}
