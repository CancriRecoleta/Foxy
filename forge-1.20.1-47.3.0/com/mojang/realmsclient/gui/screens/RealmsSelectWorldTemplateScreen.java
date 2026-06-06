//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.dto.RealmsServer.WorldType;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.TextRenderingUtils;
import com.mojang.realmsclient.util.TextRenderingUtils.LineSegment;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsSelectWorldTemplateScreen extends RealmsScreen {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ResourceLocation LINK_ICON = new ResourceLocation("realms", "textures/gui/realms/link_icons.png");
    static final ResourceLocation TRAILER_ICON = new ResourceLocation("realms", "textures/gui/realms/trailer_icons.png");
    static final ResourceLocation SLOT_FRAME_LOCATION = new ResourceLocation("realms", "textures/gui/realms/slot_frame.png");
    static final Component PUBLISHER_LINK_TOOLTIP = Component.translatable("mco.template.info.tooltip");
    static final Component TRAILER_LINK_TOOLTIP = Component.translatable("mco.template.trailer.tooltip");
    private final Consumer<WorldTemplate> callback;
    WorldTemplateObjectSelectionList worldTemplateObjectSelectionList;
    int selectedTemplate;
    private Button selectButton;
    private Button trailerButton;
    private Button publisherButton;
    @Nullable
    Component toolTip;
    @Nullable
    String currentLink;
    private final RealmsServer.WorldType worldType;
    int clicks;
    @Nullable
    private Component[] warning;
    private String warningURL;
    boolean displayWarning;
    private boolean hoverWarning;
    @Nullable
    List<TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(Component p_167481_, Consumer<WorldTemplate> p_167482_, RealmsServer.WorldType p_167483_) {
        this(p_167481_, p_167482_, p_167483_, (WorldTemplatePaginatedList)null);
    }

    public RealmsSelectWorldTemplateScreen(Component p_167485_, Consumer<WorldTemplate> p_167486_, RealmsServer.WorldType p_167487_, @Nullable WorldTemplatePaginatedList p_167488_) {
        super(p_167485_);
        this.selectedTemplate = -1;
        this.callback = p_167486_;
        this.worldType = p_167487_;
        if (p_167488_ == null) {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList();
            this.fetchTemplatesAsync(new WorldTemplatePaginatedList(10));
        } else {
            this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList(Lists.newArrayList(p_167488_.templates));
            this.fetchTemplatesAsync(p_167488_);
        }

    }

    public void setWarning(Component... p_89683_) {
        this.warning = p_89683_;
        this.displayWarning = true;
    }

    public boolean mouseClicked(double p_89629_, double p_89630_, int p_89631_) {
        if (this.hoverWarning && this.warningURL != null) {
            Util.getPlatform().openUri("https://www.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        } else {
            return super.mouseClicked(p_89629_, p_89630_, p_89631_);
        }
    }

    public void init() {
        this.worldTemplateObjectSelectionList = new WorldTemplateObjectSelectionList(this.worldTemplateObjectSelectionList.getTemplates());
        this.trailerButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.trailer"), (p_89701_) -> {
            this.onTrailer();
        }).bounds(this.width / 2 - 206, this.height - 32, 100, 20).build());
        this.selectButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.select"), (p_89696_) -> {
            this.selectTemplate();
        }).bounds(this.width / 2 - 100, this.height - 32, 100, 20).build());
        Component $$0 = this.worldType == WorldType.MINIGAME ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_BACK;
        Button $$1 = Button.builder($$0, (p_89691_) -> {
            this.onClose();
        }).bounds(this.width / 2 + 6, this.height - 32, 100, 20).build();
        this.addRenderableWidget($$1);
        this.publisherButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("mco.template.button.publisher"), (p_89679_) -> {
            this.onPublish();
        }).bounds(this.width / 2 + 112, this.height - 32, 100, 20).build());
        this.selectButton.active = false;
        this.trailerButton.visible = false;
        this.publisherButton.visible = false;
        this.addWidget(this.worldTemplateObjectSelectionList);
        this.magicalSpecialHackyFocus(this.worldTemplateObjectSelectionList);
    }

    public Component getNarrationMessage() {
        List<Component> $$0 = Lists.newArrayListWithCapacity(2);
        if (this.title != null) {
            $$0.add(this.title);
        }

        if (this.warning != null) {
            $$0.addAll(Arrays.asList(this.warning));
        }

        return CommonComponents.joinLines((Collection)$$0);
    }

    void updateButtonStates() {
        this.publisherButton.visible = this.shouldPublisherBeVisible();
        this.trailerButton.visible = this.shouldTrailerBeVisible();
        this.selectButton.active = this.shouldSelectButtonBeActive();
    }

    private boolean shouldSelectButtonBeActive() {
        return this.selectedTemplate != -1;
    }

    private boolean shouldPublisherBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().link.isEmpty();
    }

    private WorldTemplate getSelectedTemplate() {
        return this.worldTemplateObjectSelectionList.get(this.selectedTemplate);
    }

    private boolean shouldTrailerBeVisible() {
        return this.selectedTemplate != -1 && !this.getSelectedTemplate().trailer.isEmpty();
    }

    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }

    }

    public void onClose() {
        this.callback.accept((Object)null);
    }

    void selectTemplate() {
        if (this.hasValidTemplate()) {
            this.callback.accept(this.getSelectedTemplate());
        }

    }

    private boolean hasValidTemplate() {
        return this.selectedTemplate >= 0 && this.selectedTemplate < this.worldTemplateObjectSelectionList.getItemCount();
    }

    private void onTrailer() {
        if (this.hasValidTemplate()) {
            WorldTemplate $$0 = this.getSelectedTemplate();
            if (!"".equals($$0.trailer)) {
                Util.getPlatform().openUri($$0.trailer);
            }
        }

    }

    private void onPublish() {
        if (this.hasValidTemplate()) {
            WorldTemplate $$0 = this.getSelectedTemplate();
            if (!"".equals($$0.link)) {
                Util.getPlatform().openUri($$0.link);
            }
        }

    }

    private void fetchTemplatesAsync(final WorldTemplatePaginatedList p_89654_) {
        (new Thread("realms-template-fetcher") {
            public void run() {
                WorldTemplatePaginatedList $$0 = p_89654_;

                Either $$2;
                for(RealmsClient $$1 = RealmsClient.create(); $$0 != null; $$0 = (WorldTemplatePaginatedList)RealmsSelectWorldTemplateScreen.this.minecraft.submit(() -> {
                    if ($$2.right().isPresent()) {
                        RealmsSelectWorldTemplateScreen.LOGGER.error("Couldn't fetch templates: {}", $$2.right().get());
                        if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                            RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(I18n.get("mco.template.select.failure"));
                        }

                        return null;
                    } else {
                        WorldTemplatePaginatedList $$1 = (WorldTemplatePaginatedList)$$2.left().get();
                        Iterator var3 = $$1.templates.iterator();

                        while(var3.hasNext()) {
                            WorldTemplate $$2x = (WorldTemplate)var3.next();
                            RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.addEntry($$2x);
                        }

                        if ($$1.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.isEmpty()) {
                                String $$3 = I18n.get("mco.template.select.none", "%link");
                                TextRenderingUtils.LineSegment $$4 = LineSegment.link(I18n.get("mco.template.select.none.linkTitle"), "https://aka.ms/MinecraftRealmsContentCreator");
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose($$3, $$4);
                            }

                            return null;
                        } else {
                            return $$1;
                        }
                    }
                }).join()) {
                    $$2 = RealmsSelectWorldTemplateScreen.this.fetchTemplates($$0, $$1);
                }

            }
        }).start();
    }

    Either<WorldTemplatePaginatedList, String> fetchTemplates(WorldTemplatePaginatedList p_89656_, RealmsClient p_89657_) {
        try {
            return Either.left(p_89657_.fetchWorldTemplates(p_89656_.page + 1, p_89656_.size, this.worldType));
        } catch (RealmsServiceException var4) {
            RealmsServiceException $$2 = var4;
            return Either.right($$2.getMessage());
        }
    }

    public void render(GuiGraphics p_282162_, int p_89640_, int p_89641_, float p_89642_) {
        this.toolTip = null;
        this.currentLink = null;
        this.hoverWarning = false;
        this.renderBackground(p_282162_);
        this.worldTemplateObjectSelectionList.render(p_282162_, p_89640_, p_89641_, p_89642_);
        if (this.noTemplatesMessage != null) {
            this.renderMultilineMessage(p_282162_, p_89640_, p_89641_, this.noTemplatesMessage);
        }

        p_282162_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 13, 16777215);
        if (this.displayWarning) {
            Component[] $$4 = this.warning;

            int $$9;
            int $$11;
            for($$9 = 0; $$9 < $$4.length; ++$$9) {
                int $$6 = this.font.width((FormattedText)$$4[$$9]);
                $$11 = this.width / 2 - $$6 / 2;
                int $$8 = row(-1 + $$9);
                if (p_89640_ >= $$11 && p_89640_ <= $$11 + $$6 && p_89641_ >= $$8) {
                    Objects.requireNonNull(this.font);
                    if (p_89641_ <= $$8 + 9) {
                        this.hoverWarning = true;
                    }
                }
            }

            for($$9 = 0; $$9 < $$4.length; ++$$9) {
                Component $$10 = $$4[$$9];
                $$11 = 10526880;
                if (this.warningURL != null) {
                    if (this.hoverWarning) {
                        $$11 = 7107012;
                        $$10 = ((Component)$$10).copy().withStyle(ChatFormatting.STRIKETHROUGH);
                    } else {
                        $$11 = 3368635;
                    }
                }

                p_282162_.drawCenteredString(this.font, (Component)$$10, this.width / 2, row(-1 + $$9), $$11);
            }
        }

        super.render(p_282162_, p_89640_, p_89641_, p_89642_);
        this.renderMousehoverTooltip(p_282162_, this.toolTip, p_89640_, p_89641_);
    }

    private void renderMultilineMessage(GuiGraphics p_282398_, int p_282163_, int p_282021_, List<TextRenderingUtils.Line> p_282203_) {
        for(int $$4 = 0; $$4 < p_282203_.size(); ++$$4) {
            TextRenderingUtils.Line $$5 = (TextRenderingUtils.Line)p_282203_.get($$4);
            int $$6 = row(4 + $$4);
            int $$7 = $$5.segments.stream().mapToInt((p_280748_) -> {
                return this.font.width(p_280748_.renderedText());
            }).sum();
            int $$8 = this.width / 2 - $$7 / 2;

            int $$11;
            for(Iterator var10 = $$5.segments.iterator(); var10.hasNext(); $$8 = $$11) {
                TextRenderingUtils.LineSegment $$9 = (TextRenderingUtils.LineSegment)var10.next();
                int $$10 = $$9.isLink() ? 3368635 : 16777215;
                $$11 = p_282398_.drawString(this.font, $$9.renderedText(), $$8, $$6, $$10);
                if ($$9.isLink() && p_282163_ > $$8 && p_282163_ < $$11 && p_282021_ > $$6 - 3 && p_282021_ < $$6 + 8) {
                    this.toolTip = Component.literal($$9.getLinkUrl());
                    this.currentLink = $$9.getLinkUrl();
                }
            }
        }

    }

    protected void renderMousehoverTooltip(GuiGraphics p_281524_, @Nullable Component p_281755_, int p_282387_, int p_281491_) {
        if (p_281755_ != null) {
            int $$4 = p_282387_ + 12;
            int $$5 = p_281491_ - 12;
            int $$6 = this.font.width((FormattedText)p_281755_);
            p_281524_.fillGradient($$4 - 3, $$5 - 3, $$4 + $$6 + 3, $$5 + 8 + 3, -1073741824, -1073741824);
            p_281524_.drawString(this.font, p_281755_, $$4, $$5, 16777215);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class WorldTemplateObjectSelectionList extends RealmsObjectSelectionList<Entry> {
        public WorldTemplateObjectSelectionList() {
            this(Collections.emptyList());
        }

        public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> p_89795_) {
            super(RealmsSelectWorldTemplateScreen.this.width, RealmsSelectWorldTemplateScreen.this.height, RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsSelectWorldTemplateScreen.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height - 40, 46);
            p_89795_.forEach(this::addEntry);
        }

        public void addEntry(WorldTemplate p_89805_) {
            this.addEntry(RealmsSelectWorldTemplateScreen.this.new Entry(p_89805_));
        }

        public boolean mouseClicked(double p_89797_, double p_89798_, int p_89799_) {
            if (p_89799_ == 0 && p_89798_ >= (double)this.y0 && p_89798_ <= (double)this.y1) {
                int $$3 = this.width / 2 - 150;
                if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                    Util.getPlatform().openUri(RealmsSelectWorldTemplateScreen.this.currentLink);
                }

                int $$4 = (int)Math.floor(p_89798_ - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int $$5 = $$4 / this.itemHeight;
                if (p_89797_ >= (double)$$3 && p_89797_ < (double)this.getScrollbarPosition() && $$5 >= 0 && $$4 >= 0 && $$5 < this.getItemCount()) {
                    this.selectItem($$5);
                    this.itemClicked($$4, $$5, p_89797_, p_89798_, this.width, p_89799_);
                    if ($$5 >= RealmsSelectWorldTemplateScreen.this.worldTemplateObjectSelectionList.getItemCount()) {
                        return super.mouseClicked(p_89797_, p_89798_, p_89799_);
                    }

                    RealmsSelectWorldTemplateScreen var10000 = RealmsSelectWorldTemplateScreen.this;
                    var10000.clicks += 7;
                    if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                        RealmsSelectWorldTemplateScreen.this.selectTemplate();
                    }

                    return true;
                }
            }

            return super.mouseClicked(p_89797_, p_89798_, p_89799_);
        }

        public void setSelected(@Nullable Entry p_89807_) {
            super.setSelected(p_89807_);
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = this.children().indexOf(p_89807_);
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }

        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }

        public int getRowWidth() {
            return 300;
        }

        public void renderBackground(GuiGraphics p_282384_) {
            RealmsSelectWorldTemplateScreen.this.renderBackground(p_282384_);
        }

        public boolean isEmpty() {
            return this.getItemCount() == 0;
        }

        public WorldTemplate get(int p_89812_) {
            return ((Entry)this.children().get(p_89812_)).template;
        }

        public List<WorldTemplate> getTemplates() {
            return (List)this.children().stream().map((p_89814_) -> {
                return p_89814_.template;
            }).collect(Collectors.toList());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private class Entry extends ObjectSelectionList.Entry<Entry> {
        final WorldTemplate template;

        public Entry(WorldTemplate p_89753_) {
            this.template = p_89753_;
        }

        public void render(GuiGraphics p_281796_, int p_282160_, int p_281759_, int p_282961_, int p_281497_, int p_282427_, int p_283550_, int p_282955_, boolean p_282866_, float p_281452_) {
            this.renderWorldTemplateItem(p_281796_, this.template, p_282961_, p_281759_, p_283550_, p_282955_);
        }

        private void renderWorldTemplateItem(GuiGraphics p_282991_, WorldTemplate p_281775_, int p_281335_, int p_282289_, int p_281708_, int p_281391_) {
            int $$6 = p_281335_ + 45 + 20;
            p_282991_.drawString(RealmsSelectWorldTemplateScreen.this.font, p_281775_.name, $$6, p_282289_ + 2, 16777215, false);
            p_282991_.drawString(RealmsSelectWorldTemplateScreen.this.font, p_281775_.author, $$6, p_282289_ + 15, 7105644, false);
            p_282991_.drawString(RealmsSelectWorldTemplateScreen.this.font, p_281775_.version, $$6 + 227 - RealmsSelectWorldTemplateScreen.this.font.width(p_281775_.version), p_282289_ + 1, 7105644, false);
            if (!"".equals(p_281775_.link) || !"".equals(p_281775_.trailer) || !"".equals(p_281775_.recommendedPlayers)) {
                this.drawIcons(p_282991_, $$6 - 1, p_282289_ + 25, p_281708_, p_281391_, p_281775_.link, p_281775_.trailer, p_281775_.recommendedPlayers);
            }

            this.drawImage(p_282991_, p_281335_, p_282289_ + 1, p_281708_, p_281391_, p_281775_);
        }

        private void drawImage(GuiGraphics p_282450_, int p_281877_, int p_282680_, int p_281921_, int p_283193_, WorldTemplate p_282405_) {
            p_282450_.blit(RealmsTextureManager.worldTemplate(p_282405_.id, p_282405_.image), p_281877_ + 1, p_282680_ + 1, 0.0F, 0.0F, 38, 38, 38, 38);
            p_282450_.blit(RealmsSelectWorldTemplateScreen.SLOT_FRAME_LOCATION, p_281877_, p_282680_, 0.0F, 0.0F, 40, 40, 40, 40);
        }

        private void drawIcons(GuiGraphics p_281993_, int p_281797_, int p_281328_, int p_283015_, int p_281905_, String p_281390_, String p_281552_, String p_281807_) {
            if (!"".equals(p_281807_)) {
                p_281993_.drawString(RealmsSelectWorldTemplateScreen.this.font, p_281807_, p_281797_, p_281328_ + 4, 5000268, false);
            }

            int $$8 = "".equals(p_281807_) ? 0 : RealmsSelectWorldTemplateScreen.this.font.width(p_281807_) + 2;
            boolean $$9 = false;
            boolean $$10 = false;
            boolean $$11 = "".equals(p_281390_);
            if (p_283015_ >= p_281797_ + $$8 && p_283015_ <= p_281797_ + $$8 + 32 && p_281905_ >= p_281328_ && p_281905_ <= p_281328_ + 15 && p_281905_ < RealmsSelectWorldTemplateScreen.this.height - 15 && p_281905_ > 32) {
                if (p_283015_ <= p_281797_ + 15 + $$8 && p_283015_ > $$8) {
                    if ($$11) {
                        $$10 = true;
                    } else {
                        $$9 = true;
                    }
                } else if (!$$11) {
                    $$10 = true;
                }
            }

            if (!$$11) {
                float $$12 = $$9 ? 15.0F : 0.0F;
                p_281993_.blit(RealmsSelectWorldTemplateScreen.LINK_ICON, p_281797_ + $$8, p_281328_, $$12, 0.0F, 15, 15, 30, 15);
            }

            if (!"".equals(p_281552_)) {
                int $$13 = p_281797_ + $$8 + ($$11 ? 0 : 17);
                float $$14 = $$10 ? 15.0F : 0.0F;
                p_281993_.blit(RealmsSelectWorldTemplateScreen.TRAILER_ICON, $$13, p_281328_, $$14, 0.0F, 15, 15, 30, 15);
            }

            if ($$9) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.PUBLISHER_LINK_TOOLTIP;
                RealmsSelectWorldTemplateScreen.this.currentLink = p_281390_;
            } else if ($$10 && !"".equals(p_281552_)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsSelectWorldTemplateScreen.TRAILER_LINK_TOOLTIP;
                RealmsSelectWorldTemplateScreen.this.currentLink = p_281552_;
            }

        }

        public Component getNarration() {
            Component $$0 = CommonComponents.joinLines(Component.literal(this.template.name), Component.translatable("mco.template.select.narrate.authors", this.template.author), Component.literal(this.template.recommendedPlayers), Component.translatable("mco.template.select.narrate.version", this.template.version));
            return Component.translatable("narrator.select", $$0);
        }
    }
}
