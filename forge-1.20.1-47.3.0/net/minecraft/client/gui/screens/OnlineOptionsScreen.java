//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;

@OnlyIn(Dist.CLIENT)
public class OnlineOptionsScreen extends SimpleOptionsSubScreen {
    @Nullable
    private final OptionInstance<Unit> difficultyDisplay;

    public static OnlineOptionsScreen createOnlineOptionsScreen(Minecraft p_262120_, Screen p_261548_, Options p_261609_) {
        List<OptionInstance<?>> $$3 = Lists.newArrayList();
        $$3.add(p_261609_.realmsNotifications());
        $$3.add(p_261609_.allowServerListing());
        OptionInstance<Unit> $$4 = (OptionInstance)Optionull.map(p_262120_.level, (p_288244_) -> {
            Difficulty $$1 = p_288244_.getDifficulty();
            return new OptionInstance("options.difficulty.online", OptionInstance.noTooltip(), (p_261484_, p_262113_) -> {
                return $$1.getDisplayName();
            }, new OptionInstance.Enum(List.of(Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, (p_261717_) -> {
            });
        });
        if ($$4 != null) {
            $$3.add($$4);
        }

        return new OnlineOptionsScreen(p_261548_, p_261609_, (OptionInstance[])$$3.toArray(new OptionInstance[0]), $$4);
    }

    private OnlineOptionsScreen(Screen p_261979_, Options p_261924_, OptionInstance<?>[] p_262151_, @Nullable OptionInstance<Unit> p_261692_) {
        super(p_261979_, p_261924_, Component.translatable("options.online.title"), p_262151_);
        this.difficultyDisplay = p_261692_;
    }

    protected void init() {
        super.init();
        AbstractWidget $$1;
        if (this.difficultyDisplay != null) {
            $$1 = this.list.findOption(this.difficultyDisplay);
            if ($$1 != null) {
                $$1.active = false;
            }
        }

        $$1 = this.list.findOption(this.options.telemetryOptInExtra());
        if ($$1 != null) {
            $$1.active = this.minecraft.extraTelemetryAvailable();
        }

    }
}
