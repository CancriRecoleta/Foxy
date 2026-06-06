//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperimentsScreen extends Screen {
    private static final int MAIN_CONTENT_WIDTH = 310;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen parent;
    private final PackRepository packRepository;
    private final Consumer<PackRepository> output;
    private final Object2BooleanMap<Pack> packs = new Object2BooleanLinkedOpenHashMap();

    protected ExperimentsScreen(Screen p_270165_, PackRepository p_270308_, Consumer<PackRepository> p_270392_) {
        super(Component.translatable("experiments_screen.title"));
        this.parent = p_270165_;
        this.packRepository = p_270308_;
        this.output = p_270392_;
        Iterator var4 = p_270308_.getAvailablePacks().iterator();

        while(var4.hasNext()) {
            Pack $$3 = (Pack)var4.next();
            if ($$3.getPackSource() == PackSource.FEATURE) {
                this.packs.put($$3, p_270308_.getSelectedPacks().contains($$3));
            }
        }

    }

    protected void init() {
        this.layout.addToHeader(new StringWidget(Component.translatable("selectWorld.experiments"), this.font));
        GridLayout.RowHelper $$0 = ((GridLayout)this.layout.addToContents(new GridLayout())).createRowHelper(1);
        $$0.addChild((new MultiLineTextWidget(Component.translatable("selectWorld.experiments.info").withStyle(ChatFormatting.RED), this.font)).setMaxWidth(310), $$0.newCellSettings().paddingBottom(15));
        SwitchGrid.Builder $$1 = SwitchGrid.builder(310).withInfoUnderneath(2, true).withRowSpacing(4);
        this.packs.forEach((p_270880_, p_270874_) -> {
            $$1.addSwitch(getHumanReadableTitle(p_270880_), () -> {
                return this.packs.getBoolean(p_270880_);
            }, (p_270491_) -> {
                this.packs.put(p_270880_, p_270491_);
            }).withInfo(p_270880_.getDescription());
        });
        Objects.requireNonNull($$0);
        $$1.build($$0::addChild);
        GridLayout.RowHelper $$2 = ((GridLayout)this.layout.addToFooter((new GridLayout()).columnSpacing(10))).createRowHelper(2);
        $$2.addChild(Button.builder(CommonComponents.GUI_DONE, (p_270336_) -> {
            this.onDone();
        }).build());
        $$2.addChild(Button.builder(CommonComponents.GUI_CANCEL, (p_274702_) -> {
            this.onClose();
        }).build());
        this.layout.visitWidgets((p_270313_) -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(p_270313_);
        });
        this.repositionElements();
    }

    private static Component getHumanReadableTitle(Pack p_270861_) {
        String $$1 = "dataPack." + p_270861_.getId() + ".name";
        return (Component)(I18n.exists($$1) ? Component.translatable($$1) : p_270861_.getTitle());
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void onDone() {
        List<Pack> $$0 = new ArrayList(this.packRepository.getSelectedPacks());
        List<Pack> $$1 = new ArrayList();
        this.packs.forEach((p_270540_, p_270780_) -> {
            $$0.remove(p_270540_);
            if (p_270780_) {
                $$1.add(p_270540_);
            }

        });
        $$0.addAll(Lists.reverse($$1));
        this.packRepository.setSelected($$0.stream().map(Pack::getId).toList());
        this.output.accept(this.packRepository);
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    public void render(GuiGraphics p_283515_, int p_283170_, int p_283248_, float p_283106_) {
        this.renderBackground(p_283515_);
        p_283515_.setColor(0.125F, 0.125F, 0.125F, 1.0F);
        int $$4 = true;
        p_283515_.blit(BACKGROUND_LOCATION, 0, this.layout.getHeaderHeight(), 0.0F, 0.0F, this.width, this.height - this.layout.getHeaderHeight() - this.layout.getFooterHeight(), 32, 32);
        p_283515_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(p_283515_, p_283170_, p_283248_, p_283106_);
    }
}
