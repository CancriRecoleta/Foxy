//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OptimizeWorldScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object2IntMap<ResourceKey<Level>> DIMENSION_COLORS = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), (p_101324_) -> {
        p_101324_.put(Level.OVERWORLD, -13408734);
        p_101324_.put(Level.NETHER, -10075085);
        p_101324_.put(Level.END, -8943531);
        p_101324_.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpgrader upgrader;

    @Nullable
    public static OptimizeWorldScreen create(Minecraft p_101316_, BooleanConsumer p_101317_, DataFixer p_101318_, LevelStorageSource.LevelStorageAccess p_101319_, boolean p_101320_) {
        try {
            WorldStem $$5 = p_101316_.createWorldOpenFlows().loadWorldStem(p_101319_, false);

            OptimizeWorldScreen var8;
            try {
                WorldData $$6 = $$5.worldData();
                RegistryAccess.Frozen $$7 = $$5.registries().compositeAccess();
                p_101319_.saveDataTag($$7, $$6);
                var8 = new OptimizeWorldScreen(p_101317_, p_101318_, p_101319_, $$6.getLevelSettings(), p_101320_, $$7.registryOrThrow(Registries.LEVEL_STEM));
            } catch (Throwable var10) {
                if ($$5 != null) {
                    try {
                        $$5.close();
                    } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                    }
                }

                throw var10;
            }

            if ($$5 != null) {
                $$5.close();
            }

            return var8;
        } catch (Exception var11) {
            Exception $$8 = var11;
            LOGGER.warn("Failed to load datapacks, can't optimize world", $$8);
            return null;
        }
    }

    private OptimizeWorldScreen(BooleanConsumer p_251295_, DataFixer p_250489_, LevelStorageSource.LevelStorageAccess p_248781_, LevelSettings p_251180_, boolean p_250358_, Registry<LevelStem> p_248690_) {
        super(Component.translatable("optimizeWorld.title", p_251180_.levelName()));
        this.callback = p_251295_;
        this.upgrader = new WorldUpgrader(p_248781_, p_250489_, p_248690_, p_250358_);
    }

    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_101322_) -> {
            this.upgrader.cancel();
            this.callback.accept(false);
        }).bounds(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
    }

    public void tick() {
        if (this.upgrader.isFinished()) {
            this.callback.accept(true);
        }

    }

    public void onClose() {
        this.callback.accept(false);
    }

    public void removed() {
        this.upgrader.cancel();
    }

    public void render(GuiGraphics p_281829_, int p_101312_, int p_101313_, float p_101314_) {
        this.renderBackground(p_281829_);
        p_281829_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 20, 16777215);
        int $$4 = this.width / 2 - 150;
        int $$5 = this.width / 2 + 150;
        int $$6 = this.height / 4 + 100;
        int $$7 = $$6 + 10;
        Font var10001 = this.font;
        Component var10002 = this.upgrader.getStatus();
        int var10003 = this.width / 2;
        Objects.requireNonNull(this.font);
        p_281829_.drawCenteredString(var10001, var10002, var10003, $$6 - 9 - 2, 10526880);
        if (this.upgrader.getTotalChunks() > 0) {
            p_281829_.fill($$4 - 1, $$6 - 1, $$5 + 1, $$7 + 1, -16777216);
            p_281829_.drawString(this.font, (Component)Component.translatable("optimizeWorld.info.converted", this.upgrader.getConverted()), $$4, 40, 10526880);
            var10001 = this.font;
            MutableComponent var14 = Component.translatable("optimizeWorld.info.skipped", this.upgrader.getSkipped());
            Objects.requireNonNull(this.font);
            p_281829_.drawString(var10001, (Component)var14, $$4, 40 + 9 + 3, 10526880);
            var10001 = this.font;
            var14 = Component.translatable("optimizeWorld.info.total", this.upgrader.getTotalChunks());
            Objects.requireNonNull(this.font);
            p_281829_.drawString(var10001, (Component)var14, $$4, 40 + (9 + 3) * 2, 10526880);
            int $$8 = 0;

            int $$10;
            for(Iterator var10 = this.upgrader.levels().iterator(); var10.hasNext(); $$8 += $$10) {
                ResourceKey<Level> $$9 = (ResourceKey)var10.next();
                $$10 = Mth.floor(this.upgrader.dimensionProgress($$9) * (float)($$5 - $$4));
                p_281829_.fill($$4 + $$8, $$6, $$4 + $$8 + $$10, $$7, DIMENSION_COLORS.getInt($$9));
            }

            int $$11 = this.upgrader.getConverted() + this.upgrader.getSkipped();
            var10001 = this.font;
            String var15 = "" + $$11 + " / " + this.upgrader.getTotalChunks();
            var10003 = this.width / 2;
            Objects.requireNonNull(this.font);
            p_281829_.drawCenteredString(var10001, var15, var10003, $$6 + 2 * 9 + 2, 10526880);
            var10001 = this.font;
            var15 = Mth.floor(this.upgrader.getProgress() * 100.0F) + "%";
            var10003 = this.width / 2;
            int var10004 = $$6 + ($$7 - $$6) / 2;
            Objects.requireNonNull(this.font);
            p_281829_.drawCenteredString(var10001, var15, var10003, var10004 - 9 / 2, 10526880);
        }

        super.render(p_281829_, p_101312_, p_101313_, p_101314_);
    }
}
