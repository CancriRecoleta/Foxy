//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WinScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
    private static final Component SECTION_HEADING;
    private static final String NAME_PREFIX = "           ";
    private static final String OBFUSCATE_TOKEN;
    private static final float SPEEDUP_FACTOR = 5.0F;
    private static final float SPEEDUP_FACTOR_FAST = 15.0F;
    private final boolean poem;
    private final Runnable onFinished;
    private float scroll;
    private List<FormattedCharSequence> lines;
    private IntSet centeredLines;
    private int totalScrollLength;
    private boolean speedupActive;
    private final IntSet speedupModifiers = new IntOpenHashSet();
    private float scrollSpeed;
    private final float unmodifiedScrollSpeed;
    private int direction;
    private final LogoRenderer logoRenderer = new LogoRenderer(false);

    public WinScreen(boolean p_276286_, Runnable p_276294_) {
        super(GameNarrator.NO_TITLE);
        this.poem = p_276286_;
        this.onFinished = p_276294_;
        if (!p_276286_) {
            this.unmodifiedScrollSpeed = 0.75F;
        } else {
            this.unmodifiedScrollSpeed = 0.5F;
        }

        this.direction = 1;
        this.scrollSpeed = this.unmodifiedScrollSpeed;
    }

    private float calculateScrollSpeed() {
        return this.speedupActive ? this.unmodifiedScrollSpeed * (5.0F + (float)this.speedupModifiers.size() * 15.0F) * (float)this.direction : this.unmodifiedScrollSpeed * (float)this.direction;
    }

    public void tick() {
        this.minecraft.getMusicManager().tick();
        this.minecraft.getSoundManager().tick(false);
        float $$0 = (float)(this.totalScrollLength + this.height + this.height + 24);
        if (this.scroll > $$0) {
            this.respawn();
        }

    }

    public boolean keyPressed(int p_169469_, int p_169470_, int p_169471_) {
        if (p_169469_ == 265) {
            this.direction = -1;
        } else if (p_169469_ != 341 && p_169469_ != 345) {
            if (p_169469_ == 32) {
                this.speedupActive = true;
            }
        } else {
            this.speedupModifiers.add(p_169469_);
        }

        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyPressed(p_169469_, p_169470_, p_169471_);
    }

    public boolean keyReleased(int p_169476_, int p_169477_, int p_169478_) {
        if (p_169476_ == 265) {
            this.direction = 1;
        }

        if (p_169476_ == 32) {
            this.speedupActive = false;
        } else if (p_169476_ == 341 || p_169476_ == 345) {
            this.speedupModifiers.remove(p_169476_);
        }

        this.scrollSpeed = this.calculateScrollSpeed();
        return super.keyReleased(p_169476_, p_169477_, p_169478_);
    }

    public void onClose() {
        this.respawn();
    }

    private void respawn() {
        this.onFinished.run();
    }

    protected void init() {
        if (this.lines == null) {
            this.lines = Lists.newArrayList();
            this.centeredLines = new IntOpenHashSet();
            if (this.poem) {
                this.wrapCreditsIO("texts/end.txt", this::addPoemFile);
            }

            this.wrapCreditsIO("texts/credits.json", this::addCreditsFile);
            if (this.poem) {
                this.wrapCreditsIO("texts/postcredits.txt", this::addPoemFile);
            }

            this.totalScrollLength = this.lines.size() * 12;
        }
    }

    private void wrapCreditsIO(String p_197399_, CreditsReader p_197400_) {
        try {
            Reader $$2 = this.minecraft.getResourceManager().openAsReader(new ResourceLocation(p_197399_));

            try {
                p_197400_.read($$2);
            } catch (Throwable var7) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if ($$2 != null) {
                $$2.close();
            }
        } catch (Exception var8) {
            Exception $$3 = var8;
            LOGGER.error("Couldn't load credits", $$3);
        }

    }

    private void addPoemFile(Reader p_232818_) throws IOException {
        BufferedReader $$1 = new BufferedReader(p_232818_);
        RandomSource $$2 = RandomSource.create(8124371L);

        String $$3;
        int $$4;
        while(($$3 = $$1.readLine()) != null) {
            String $$5;
            String $$6;
            for($$3 = $$3.replaceAll("PLAYERNAME", this.minecraft.getUser().getName()); ($$4 = $$3.indexOf(OBFUSCATE_TOKEN)) != -1; $$3 = $$5 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, $$2.nextInt(4) + 3) + $$6) {
                $$5 = $$3.substring(0, $$4);
                $$6 = $$3.substring($$4 + OBFUSCATE_TOKEN.length());
            }

            this.addPoemLines($$3);
            this.addEmptyLine();
        }

        for($$4 = 0; $$4 < 8; ++$$4) {
            this.addEmptyLine();
        }

    }

    private void addCreditsFile(Reader p_232820_) {
        JsonArray $$1 = GsonHelper.parseArray(p_232820_);
        Iterator var3 = $$1.iterator();

        while(var3.hasNext()) {
            JsonElement $$2 = (JsonElement)var3.next();
            JsonObject $$3 = $$2.getAsJsonObject();
            String $$4 = $$3.get("section").getAsString();
            this.addCreditsLine(SECTION_HEADING, true);
            this.addCreditsLine(Component.literal($$4).withStyle(ChatFormatting.YELLOW), true);
            this.addCreditsLine(SECTION_HEADING, true);
            this.addEmptyLine();
            this.addEmptyLine();
            JsonArray $$5 = $$3.getAsJsonArray("disciplines");
            Iterator var8 = $$5.iterator();

            while(var8.hasNext()) {
                JsonElement $$6 = (JsonElement)var8.next();
                JsonObject $$7 = $$6.getAsJsonObject();
                String $$8 = $$7.get("discipline").getAsString();
                if (StringUtils.isNotEmpty($$8)) {
                    this.addCreditsLine(Component.literal($$8).withStyle(ChatFormatting.YELLOW), true);
                    this.addEmptyLine();
                    this.addEmptyLine();
                }

                JsonArray $$9 = $$7.getAsJsonArray("titles");
                Iterator var13 = $$9.iterator();

                while(var13.hasNext()) {
                    JsonElement $$10 = (JsonElement)var13.next();
                    JsonObject $$11 = $$10.getAsJsonObject();
                    String $$12 = $$11.get("title").getAsString();
                    JsonArray $$13 = $$11.getAsJsonArray("names");
                    this.addCreditsLine(Component.literal($$12).withStyle(ChatFormatting.GRAY), false);
                    Iterator var18 = $$13.iterator();

                    while(var18.hasNext()) {
                        JsonElement $$14 = (JsonElement)var18.next();
                        String $$15 = $$14.getAsString();
                        this.addCreditsLine(Component.literal("           ").append($$15).withStyle(ChatFormatting.WHITE), false);
                    }

                    this.addEmptyLine();
                    this.addEmptyLine();
                }
            }
        }

    }

    private void addEmptyLine() {
        this.lines.add(FormattedCharSequence.EMPTY);
    }

    private void addPoemLines(String p_181398_) {
        this.lines.addAll(this.minecraft.font.split(Component.literal(p_181398_), 256));
    }

    private void addCreditsLine(Component p_169473_, boolean p_169474_) {
        if (p_169474_) {
            this.centeredLines.add(this.lines.size());
        }

        this.lines.add(p_169473_.getVisualOrderText());
    }

    private void renderBg(GuiGraphics p_282239_) {
        int $$1 = this.width;
        float $$2 = this.scroll * 0.5F;
        int $$3 = true;
        float $$4 = this.scroll / this.unmodifiedScrollSpeed;
        float $$5 = $$4 * 0.02F;
        float $$6 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.unmodifiedScrollSpeed;
        float $$7 = ($$6 - 20.0F - $$4) * 0.005F;
        if ($$7 < $$5) {
            $$5 = $$7;
        }

        if ($$5 > 1.0F) {
            $$5 = 1.0F;
        }

        $$5 *= $$5;
        $$5 = $$5 * 96.0F / 255.0F;
        p_282239_.setColor($$5, $$5, $$5, 1.0F);
        p_282239_.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, $$2, $$1, this.height, 64, 64);
        p_282239_.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(GuiGraphics p_281907_, int p_282364_, int p_282696_, float p_281316_) {
        this.scroll = Math.max(0.0F, this.scroll + p_281316_ * this.scrollSpeed);
        this.renderBg(p_281907_);
        int $$4 = this.width / 2 - 128;
        int $$5 = this.height + 50;
        float $$6 = -this.scroll;
        p_281907_.pose().pushPose();
        p_281907_.pose().translate(0.0F, $$6, 0.0F);
        this.logoRenderer.renderLogo(p_281907_, this.width, 1.0F, $$5);
        int $$7 = $$5 + 100;

        for(int $$8 = 0; $$8 < this.lines.size(); ++$$8) {
            if ($$8 == this.lines.size() - 1) {
                float $$9 = (float)$$7 + $$6 - (float)(this.height / 2 - 6);
                if ($$9 < 0.0F) {
                    p_281907_.pose().translate(0.0F, -$$9, 0.0F);
                }
            }

            if ((float)$$7 + $$6 + 12.0F + 8.0F > 0.0F && (float)$$7 + $$6 < (float)this.height) {
                FormattedCharSequence $$10 = (FormattedCharSequence)this.lines.get($$8);
                if (this.centeredLines.contains($$8)) {
                    p_281907_.drawCenteredString(this.font, $$10, $$4 + 128, $$7, 16777215);
                } else {
                    p_281907_.drawString(this.font, $$10, $$4, $$7, 16777215);
                }
            }

            $$7 += 12;
        }

        p_281907_.pose().popPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR);
        p_281907_.blit(VIGNETTE_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        super.render(p_281907_, p_282364_, p_282696_, p_281316_);
    }

    public void removed() {
        this.minecraft.getMusicManager().stopPlaying(Musics.CREDITS);
    }

    public Music getBackgroundMusic() {
        return Musics.CREDITS;
    }

    static {
        SECTION_HEADING = Component.literal("============").withStyle(ChatFormatting.WHITE);
        OBFUSCATE_TOKEN = ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    private interface CreditsReader {
        void read(Reader var1) throws IOException;
    }
}
