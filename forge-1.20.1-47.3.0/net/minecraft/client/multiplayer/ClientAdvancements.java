//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientAdvancements {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final WorldSessionTelemetryManager telemetryManager;
    private final AdvancementList advancements = new AdvancementList();
    private final Map<Advancement, AdvancementProgress> progress = Maps.newHashMap();
    @Nullable
    private Listener listener;
    @Nullable
    private Advancement selectedTab;

    public ClientAdvancements(Minecraft p_286782_, WorldSessionTelemetryManager p_286391_) {
        this.minecraft = p_286782_;
        this.telemetryManager = p_286391_;
    }

    public void update(ClientboundUpdateAdvancementsPacket p_104400_) {
        if (p_104400_.shouldReset()) {
            this.advancements.clear();
            this.progress.clear();
        }

        this.advancements.remove(p_104400_.getRemoved());
        this.advancements.add(p_104400_.getAdded());
        Iterator var2 = p_104400_.getProgress().entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<ResourceLocation, AdvancementProgress> $$1 = (Map.Entry)var2.next();
            Advancement $$2 = this.advancements.get((ResourceLocation)$$1.getKey());
            if ($$2 != null) {
                AdvancementProgress $$3 = (AdvancementProgress)$$1.getValue();
                $$3.update($$2.getCriteria(), $$2.getRequirements());
                this.progress.put($$2, $$3);
                if (this.listener != null) {
                    this.listener.onUpdateAdvancementProgress($$2, $$3);
                }

                if (!p_104400_.shouldReset() && $$3.isDone()) {
                    if (this.minecraft.level != null) {
                        this.telemetryManager.onAdvancementDone(this.minecraft.level, $$2);
                    }

                    if ($$2.getDisplay() != null && $$2.getDisplay().shouldShowToast()) {
                        this.minecraft.getToasts().addToast(new AdvancementToast($$2));
                    }
                }
            } else {
                LOGGER.warn("Server informed client about progress for unknown advancement {}", $$1.getKey());
            }
        }

    }

    public AdvancementList getAdvancements() {
        return this.advancements;
    }

    public void setSelectedTab(@Nullable Advancement p_104402_, boolean p_104403_) {
        ClientPacketListener $$2 = this.minecraft.getConnection();
        if ($$2 != null && p_104402_ != null && p_104403_) {
            $$2.send((Packet)ServerboundSeenAdvancementsPacket.openedTab(p_104402_));
        }

        if (this.selectedTab != p_104402_) {
            this.selectedTab = p_104402_;
            if (this.listener != null) {
                this.listener.onSelectedTabChanged(p_104402_);
            }
        }

    }

    public void setListener(@Nullable Listener p_104398_) {
        this.listener = p_104398_;
        this.advancements.setListener(p_104398_);
        if (p_104398_ != null) {
            Iterator var2 = this.progress.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<Advancement, AdvancementProgress> $$1 = (Map.Entry)var2.next();
                p_104398_.onUpdateAdvancementProgress((Advancement)$$1.getKey(), (AdvancementProgress)$$1.getValue());
            }

            p_104398_.onSelectedTabChanged(this.selectedTab);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public interface Listener extends AdvancementList.Listener {
        void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2);

        void onSelectedTabChanged(@Nullable Advancement var1);
    }
}
