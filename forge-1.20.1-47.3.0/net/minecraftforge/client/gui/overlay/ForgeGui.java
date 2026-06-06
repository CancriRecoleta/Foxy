//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.Objective;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeGui extends Gui {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WHITE = 16777215;
    public static double rayTraceDistance = 20.0;
    public int leftHeight = 39;
    public int rightHeight = 39;
    private Font font = null;
    private final ForgeDebugScreenOverlay debugOverlay;

    public ForgeGui(Minecraft mc) {
        super(mc, mc.getItemRenderer());
        this.debugOverlay = new ForgeDebugScreenOverlay(mc);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public void setupOverlayRenderState(boolean blend, boolean depthTest) {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.disableBlend();
        }

        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        this.screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        this.screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
        this.rightHeight = 39;
        this.leftHeight = 39;
        if (!MinecraftForge.EVENT_BUS.post(new RenderGuiEvent.Pre(this.minecraft.getWindow(), guiGraphics, partialTick))) {
            this.font = this.minecraft.font;
            this.random.setSeed((long)this.tickCount * 312871L);
            GuiOverlayManager.getOverlays().forEach((entry) -> {
                try {
                    IGuiOverlay overlay = entry.overlay();
                    if (this.pre(entry, guiGraphics)) {
                        return;
                    }

                    overlay.render(this, guiGraphics, partialTick, this.screenWidth, this.screenHeight);
                    this.post(entry, guiGraphics);
                } catch (Exception var5) {
                    Exception e = var5;
                    LOGGER.error("Error rendering overlay '{}'", entry.id(), e);
                }

            });
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            MinecraftForge.EVENT_BUS.post(new RenderGuiEvent.Post(this.minecraft.getWindow(), guiGraphics, partialTick));
        }
    }

    public boolean shouldDrawSurvivalElements() {
        return this.minecraft.gameMode.canHurtPlayer() && this.minecraft.getCameraEntity() instanceof Player;
    }

    protected void renderSubtitles(GuiGraphics guiGraphics) {
        this.subtitleOverlay.render(guiGraphics);
    }

    protected void renderBossHealth(GuiGraphics guiGraphics) {
        RenderSystem.defaultBlendFunc();
        this.minecraft.getProfiler().push("bossHealth");
        this.bossOverlay.render(guiGraphics);
        this.minecraft.getProfiler().pop();
    }

    void renderSpyglassOverlay(GuiGraphics guiGraphics) {
        float deltaFrame = this.minecraft.getDeltaFrameTime();
        this.scopeScale = Mth.lerp(0.5F * deltaFrame, this.scopeScale, 1.125F);
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if (this.minecraft.player.isScoping()) {
                this.renderSpyglassOverlay(guiGraphics, this.scopeScale);
            } else {
                this.scopeScale = 0.5F;
            }
        }

    }

    void renderHelmet(float partialTick, GuiGraphics guiGraphics) {
        ItemStack itemstack = this.minecraft.player.getInventory().getArmor(3);
        if (this.minecraft.options.getCameraType().isFirstPerson() && !itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            if (item == Blocks.CARVED_PUMPKIN.asItem()) {
                this.renderTextureOverlay(guiGraphics, PUMPKIN_BLUR_LOCATION, 1.0F);
            } else {
                IClientItemExtensions.of(item).renderHelmetOverlay(itemstack, this.minecraft.player, this.screenWidth, this.screenHeight, partialTick);
            }
        }

    }

    void renderFrostbite(GuiGraphics guiGraphics) {
        if (this.minecraft.player.getTicksFrozen() > 0) {
            this.renderTextureOverlay(guiGraphics, POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
        }

    }

    protected void renderArmor(GuiGraphics guiGraphics, int width, int height) {
        this.minecraft.getProfiler().push("armor");
        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        int level = this.minecraft.player.getArmorValue();

        for(int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 34, 9, 9, 9);
            } else if (i == level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 25, 9, 9, 9);
            } else if (i > level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, left, top, 16, 9, 9, 9);
            }

            left += 8;
        }

        this.leftHeight += 10;
        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    protected void renderPortalOverlay(GuiGraphics guiGraphics, float alpha) {
        if (alpha > 0.0F) {
            super.renderPortalOverlay(guiGraphics, alpha);
        }

    }

    protected void renderAir(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("air");
        Player player = (Player)this.minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - this.rightHeight;
        int air = player.getAirSupply();
        if (player.isEyeInFluidType((FluidType)ForgeMod.WATER_TYPE.get()) || air < 300) {
            int full = Mth.ceil((double)(air - 2) * 10.0 / 300.0);
            int partial = Mth.ceil((double)air * 10.0 / 300.0) - full;

            for(int i = 0; i < full + partial; ++i) {
                guiGraphics.blit(GUI_ICONS_LOCATION, left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
            }

            this.rightHeight += 10;
        }

        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("health");
        RenderSystem.enableBlend();
        Player player = (Player)this.minecraft.getCameraEntity();
        int health = Mth.ceil(player.getHealth());
        boolean highlight = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        if (health < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long)(this.tickCount + 20);
        } else if (health > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (long)(this.tickCount + 10);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L) {
            this.lastHealth = health;
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;
        int healthLast = this.displayHealth;
        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = Math.max((float)attrMaxHealth.getValue(), (float)Math.max(healthLast, health));
        int absorb = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((healthMax + (float)absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        this.random.setSeed((long)(this.tickCount * 312871));
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        this.leftHeight += healthRows * rowHeight;
        if (rowHeight != 10) {
            this.leftHeight += 10 - rowHeight;
        }

        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regen = this.tickCount % Mth.ceil(healthMax + 5.0F);
        }

        this.renderHearts(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    public void renderFood(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("food");
        Player player = (Player)this.minecraft.getCameraEntity();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - this.rightHeight;
        this.rightHeight += 10;
        boolean unused = false;
        FoodData stats = this.minecraft.player.getFoodData();
        int level = stats.getFoodLevel();

        for(int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            byte background = 0;
            if (this.minecraft.player.hasEffect(MobEffects.HUNGER)) {
                icon += 36;
                background = 13;
            }

            if (unused) {
                background = 1;
            }

            if (player.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (level * 3 + 1) == 0) {
                y = top + (this.random.nextInt(3) - 1);
            }

            guiGraphics.blit(GUI_ICONS_LOCATION, x, y, 16 + background * 9, 27, 9, 9);
            if (idx < level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 36, 27, 9, 9);
            } else if (idx == level) {
                guiGraphics.blit(GUI_ICONS_LOCATION, x, y, icon + 45, 27, 9, 9);
            }
        }

        RenderSystem.disableBlend();
        this.minecraft.getProfiler().pop();
    }

    protected void renderSleepFade(int width, int height, GuiGraphics guiGraphics) {
        if (this.minecraft.player.getSleepTimer() > 0) {
            this.minecraft.getProfiler().push("sleep");
            int sleepTime = this.minecraft.player.getSleepTimer();
            float opacity = (float)sleepTime / 100.0F;
            if (opacity > 1.0F) {
                opacity = 1.0F - (float)(sleepTime - 100) / 10.0F;
            }

            int color = (int)(220.0F * opacity) << 24 | 1052704;
            guiGraphics.fill(RenderType.guiOverlay(), 0, 0, width, height, color);
            this.minecraft.getProfiler().pop();
        }

    }

    protected void renderExperience(int x, GuiGraphics guiGraphics) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        if (this.minecraft.gameMode.hasExperience()) {
            super.renderExperienceBar(guiGraphics, x);
        }

        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderJumpMeter(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int x) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        super.renderJumpMeter(playerRideableJumping, guiGraphics, x);
        RenderSystem.enableBlend();
        this.minecraft.getProfiler().pop();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void renderHUDText(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("forgeHudText");
        RenderSystem.defaultBlendFunc();
        ArrayList<String> listL = new ArrayList();
        ArrayList<String> listR = new ArrayList();
        if (this.minecraft.isDemo()) {
            long time = this.minecraft.level.getGameTime();
            if (time >= 120500L) {
                listR.add(I18n.get("demo.demoExpired"));
            } else {
                listR.add(I18n.get("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - time))));
            }
        }

        if (this.minecraft.options.renderDebug) {
            this.debugOverlay.update();
            listL.addAll(this.debugOverlay.getLeft());
            listR.addAll(this.debugOverlay.getRight());
        }

        CustomizeGuiOverlayEvent.DebugText event = new CustomizeGuiOverlayEvent.DebugText(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), listL, listR);
        MinecraftForge.EVENT_BUS.post(event);
        int top = 2;

        Iterator var8;
        String msg;
        int var10002;
        int var10003;
        for(var8 = listL.iterator(); var8.hasNext(); top += 9) {
            msg = (String)var8.next();
            if (msg != null && !msg.isEmpty()) {
                var10002 = top - 1;
                var10003 = 2 + this.font.width(msg) + 1;
                Objects.requireNonNull(this.font);
                guiGraphics.fill(1, var10002, var10003, top + 9 - 1, -1873784752);
                guiGraphics.drawString(this.font, (String)msg, 2, top, 14737632, false);
            }

            Objects.requireNonNull(this.font);
        }

        top = 2;

        for(var8 = listR.iterator(); var8.hasNext(); top += 9) {
            msg = (String)var8.next();
            if (msg != null && !msg.isEmpty()) {
                int w = this.font.width(msg);
                int left = width - 2 - w;
                int var10001 = left - 1;
                var10002 = top - 1;
                var10003 = left + w + 1;
                Objects.requireNonNull(this.font);
                guiGraphics.fill(var10001, var10002, var10003, top + 9 - 1, -1873784752);
                guiGraphics.drawString(this.font, msg, left, top, 14737632, false);
            }

            Objects.requireNonNull(this.font);
        }

        this.minecraft.getProfiler().pop();
    }

    protected void renderFPSGraph(GuiGraphics guiGraphics) {
        if (this.minecraft.options.renderDebug && this.minecraft.options.renderFpsChart) {
            this.debugOverlay.render(guiGraphics);
        }

    }

    public void clearCache() {
        super.clearCache();
        this.debugOverlay.clearChunkCache();
    }

    protected void renderRecordOverlay(int width, int height, float partialTick, GuiGraphics guiGraphics) {
        if (this.overlayMessageTime > 0) {
            this.minecraft.getProfiler().push("overlayMessage");
            float hue = (float)this.overlayMessageTime - partialTick;
            int opacity = (int)(hue * 255.0F / 20.0F);
            if (opacity > 255) {
                opacity = 255;
            }

            if (opacity > 8) {
                int yShift = Math.max(this.leftHeight, this.rightHeight) + 9;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((double)width / 2.0, (double)(height - Math.max(yShift, 68)), 0.0);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = this.animateOverlayMessageColor ? Mth.hsvToRgb(hue / 50.0F, 0.7F, 0.6F) & 16777215 : 16777215;
                int messageWidth = this.font.width((FormattedText)this.overlayMessageString);
                this.drawBackdrop(guiGraphics, this.font, -4, messageWidth, 16777215 | opacity << 24);
                guiGraphics.drawString(this.font, (FormattedCharSequence)this.overlayMessageString.getVisualOrderText(), -messageWidth / 2, -4, color | opacity << 24);
                RenderSystem.disableBlend();
                guiGraphics.pose().popPose();
            }

            this.minecraft.getProfiler().pop();
        }

    }

    protected void renderTitle(int width, int height, float partialTick, GuiGraphics guiGraphics) {
        if (this.title != null && this.titleTime > 0) {
            this.minecraft.getProfiler().push("titleAndSubtitle");
            float age = (float)this.titleTime - partialTick;
            int opacity = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
                float f3 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - age;
                opacity = (int)(f3 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
                opacity = (int)(age * 255.0F / (float)this.titleFadeOutTime);
            }

            opacity = Mth.clamp(opacity, 0, 255);
            if (opacity > 8) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((double)width / 2.0, (double)height / 2.0, 0.0);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(4.0F, 4.0F, 4.0F);
                int l = opacity << 24 & -16777216;
                guiGraphics.drawString(this.font, (FormattedCharSequence)this.title.getVisualOrderText(), -this.getFont().width((FormattedText)this.title) / 2, -10, 16777215 | l, true);
                guiGraphics.pose().popPose();
                if (this.subtitle != null) {
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(2.0F, 2.0F, 2.0F);
                    guiGraphics.drawString(this.font, (FormattedCharSequence)this.subtitle.getVisualOrderText(), -this.getFont().width((FormattedText)this.subtitle) / 2, 5, 16777215 | l, true);
                    guiGraphics.pose().popPose();
                }

                RenderSystem.disableBlend();
                guiGraphics.pose().popPose();
            }

            this.minecraft.getProfiler().pop();
        }

    }

    protected void renderChat(int width, int height, GuiGraphics guiGraphics) {
        this.minecraft.getProfiler().push("chat");
        Window window = this.minecraft.getWindow();
        CustomizeGuiOverlayEvent.Chat event = new CustomizeGuiOverlayEvent.Chat(window, guiGraphics, this.minecraft.getFrameTime(), 0, height - 40);
        MinecraftForge.EVENT_BUS.post(event);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((double)event.getPosX(), (double)(event.getPosY() - height + 40) / this.chat.getScale(), 0.0);
        int mouseX = Mth.floor(this.minecraft.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth());
        int mouseY = Mth.floor(this.minecraft.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight());
        this.chat.render(guiGraphics, this.tickCount, mouseX, mouseY);
        guiGraphics.pose().popPose();
        this.minecraft.getProfiler().pop();
    }

    protected void renderPlayerList(int width, int height, GuiGraphics guiGraphics) {
        Objective scoreobjective = this.minecraft.level.getScoreboard().getDisplayObjective(0);
        ClientPacketListener handler = this.minecraft.player.connection;
        if (!this.minecraft.options.keyPlayerList.isDown() || this.minecraft.isLocalServer() && handler.getOnlinePlayers().size() <= 1 && scoreobjective == null) {
            this.tabList.setVisible(false);
        } else {
            this.tabList.setVisible(true);
            this.tabList.render(guiGraphics, width, this.minecraft.level.getScoreboard(), scoreobjective);
        }

    }

    protected void renderHealthMount(int width, int height, GuiGraphics guiGraphics) {
        Player player = (Player)this.minecraft.getCameraEntity();
        Entity tmp = player.getVehicle();
        if (tmp instanceof LivingEntity) {
            boolean unused = false;
            int left_align = width / 2 + 91;
            this.minecraft.getProfiler().popPush("mountHealth");
            RenderSystem.enableBlend();
            LivingEntity mount = (LivingEntity)tmp;
            int health = (int)Math.ceil((double)mount.getHealth());
            float healthMax = mount.getMaxHealth();
            int hearts = (int)(healthMax + 0.5F) / 2;
            if (hearts > 30) {
                hearts = 30;
            }

            int MARGIN = true;
            int BACKGROUND = 52 + (unused ? 1 : 0);
            int HALF = true;
            int FULL = true;

            for(int heart = 0; hearts > 0; heart += 20) {
                int top = height - this.rightHeight;
                int rowCount = Math.min(hearts, 10);
                hearts -= rowCount;

                for(int i = 0; i < rowCount; ++i) {
                    int x = left_align - i * 8 - 9;
                    guiGraphics.blit(GUI_ICONS_LOCATION, x, top, BACKGROUND, 9, 9, 9);
                    if (i * 2 + 1 + heart < health) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, x, top, 88, 9, 9, 9);
                    } else if (i * 2 + 1 + heart == health) {
                        guiGraphics.blit(GUI_ICONS_LOCATION, x, top, 97, 9, 9, 9);
                    }
                }

                this.rightHeight += 10;
            }

            RenderSystem.disableBlend();
        }
    }

    private boolean pre(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        return MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Pre(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay));
    }

    private void post(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(this.minecraft.getWindow(), guiGraphics, this.minecraft.getFrameTime(), overlay));
    }

    private static class ForgeDebugScreenOverlay extends DebugScreenOverlay {
        private final Minecraft mc;

        private ForgeDebugScreenOverlay(Minecraft mc) {
            super(mc);
            this.mc = mc;
        }

        public void update() {
            Entity entity = this.mc.getCameraEntity();
            this.block = entity.pick(ForgeGui.rayTraceDistance, 0.0F, false);
            this.liquid = entity.pick(ForgeGui.rayTraceDistance, 0.0F, true);
        }

        protected void drawGameInformation(GuiGraphics guiGraphics) {
            RenderSystem.disableDepthTest();
        }

        protected void drawSystemInformation(GuiGraphics guiGraphics) {
        }

        private List<String> getLeft() {
            List<String> ret = this.getGameInformation();
            ret.add("");
            boolean flag = this.mc.getSingleplayerServer() != null;
            String var10001 = this.mc.options.renderDebugCharts ? "visible" : "hidden";
            ret.add("Debug: Pie [shift]: " + var10001 + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.mc.options.renderFpsChart ? "visible" : "hidden"));
            ret.add("For help: press F3 + Q");
            return ret;
        }

        private List<String> getRight() {
            return this.getSystemInformation();
        }
    }
}
