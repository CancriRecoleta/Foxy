//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class CustomBossEvent extends ServerBossEvent {
    private final ResourceLocation id;
    private final Set<UUID> players = Sets.newHashSet();
    private int value;
    private int max = 100;

    public CustomBossEvent(ResourceLocation p_136261_, Component p_136262_) {
        super(p_136262_, net.minecraft.world.BossEvent.BossBarColor.WHITE, net.minecraft.world.BossEvent.BossBarOverlay.PROGRESS);
        this.id = p_136261_;
        this.setProgress(0.0F);
    }

    public ResourceLocation getTextId() {
        return this.id;
    }

    public void addPlayer(ServerPlayer p_136267_) {
        super.addPlayer(p_136267_);
        this.players.add(p_136267_.getUUID());
    }

    public void addOfflinePlayer(UUID p_136271_) {
        this.players.add(p_136271_);
    }

    public void removePlayer(ServerPlayer p_136281_) {
        super.removePlayer(p_136281_);
        this.players.remove(p_136281_.getUUID());
    }

    public void removeAllPlayers() {
        super.removeAllPlayers();
        this.players.clear();
    }

    public int getValue() {
        return this.value;
    }

    public int getMax() {
        return this.max;
    }

    public void setValue(int p_136265_) {
        this.value = p_136265_;
        this.setProgress(Mth.clamp((float)p_136265_ / (float)this.max, 0.0F, 1.0F));
    }

    public void setMax(int p_136279_) {
        this.max = p_136279_;
        this.setProgress(Mth.clamp((float)this.value / (float)p_136279_, 0.0F, 1.0F));
    }

    public final Component getDisplayName() {
        return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((p_136276_) -> {
            return p_136276_.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(Action.SHOW_TEXT, Component.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString());
        });
    }

    public boolean setPlayers(Collection<ServerPlayer> p_136269_) {
        Set<UUID> $$1 = Sets.newHashSet();
        Set<ServerPlayer> $$2 = Sets.newHashSet();
        Iterator var4 = this.players.iterator();

        UUID $$9;
        boolean $$7;
        Iterator var7;
        while(var4.hasNext()) {
            $$9 = (UUID)var4.next();
            $$7 = false;
            var7 = p_136269_.iterator();

            while(var7.hasNext()) {
                ServerPlayer $$5 = (ServerPlayer)var7.next();
                if ($$5.getUUID().equals($$9)) {
                    $$7 = true;
                    break;
                }
            }

            if (!$$7) {
                $$1.add($$9);
            }
        }

        var4 = p_136269_.iterator();

        ServerPlayer $$11;
        while(var4.hasNext()) {
            $$11 = (ServerPlayer)var4.next();
            $$7 = false;
            var7 = this.players.iterator();

            while(var7.hasNext()) {
                UUID $$8 = (UUID)var7.next();
                if ($$11.getUUID().equals($$8)) {
                    $$7 = true;
                    break;
                }
            }

            if (!$$7) {
                $$2.add($$11);
            }
        }

        for(var4 = $$1.iterator(); var4.hasNext(); this.players.remove($$9)) {
            $$9 = (UUID)var4.next();
            Iterator var11 = this.getPlayers().iterator();

            while(var11.hasNext()) {
                ServerPlayer $$10 = (ServerPlayer)var11.next();
                if ($$10.getUUID().equals($$9)) {
                    this.removePlayer($$10);
                    break;
                }
            }
        }

        var4 = $$2.iterator();

        while(var4.hasNext()) {
            $$11 = (ServerPlayer)var4.next();
            this.addPlayer($$11);
        }

        return !$$1.isEmpty() || !$$2.isEmpty();
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("Name", Serializer.toJson(this.name));
        $$0.putBoolean("Visible", this.isVisible());
        $$0.putInt("Value", this.value);
        $$0.putInt("Max", this.max);
        $$0.putString("Color", this.getColor().getName());
        $$0.putString("Overlay", this.getOverlay().getName());
        $$0.putBoolean("DarkenScreen", this.shouldDarkenScreen());
        $$0.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
        $$0.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
        ListTag $$1 = new ListTag();
        Iterator var3 = this.players.iterator();

        while(var3.hasNext()) {
            UUID $$2 = (UUID)var3.next();
            $$1.add(NbtUtils.createUUID($$2));
        }

        $$0.put("Players", $$1);
        return $$0;
    }

    public static CustomBossEvent load(CompoundTag p_136273_, ResourceLocation p_136274_) {
        CustomBossEvent $$2 = new CustomBossEvent(p_136274_, Serializer.fromJson(p_136273_.getString("Name")));
        $$2.setVisible(p_136273_.getBoolean("Visible"));
        $$2.setValue(p_136273_.getInt("Value"));
        $$2.setMax(p_136273_.getInt("Max"));
        $$2.setColor(net.minecraft.world.BossEvent.BossBarColor.byName(p_136273_.getString("Color")));
        $$2.setOverlay(net.minecraft.world.BossEvent.BossBarOverlay.byName(p_136273_.getString("Overlay")));
        $$2.setDarkenScreen(p_136273_.getBoolean("DarkenScreen"));
        $$2.setPlayBossMusic(p_136273_.getBoolean("PlayBossMusic"));
        $$2.setCreateWorldFog(p_136273_.getBoolean("CreateWorldFog"));
        ListTag $$3 = p_136273_.getList("Players", 11);

        for(int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            $$2.addOfflinePlayer(NbtUtils.loadUUID($$3.get($$4)));
        }

        return $$2;
    }

    public void onPlayerConnect(ServerPlayer p_136284_) {
        if (this.players.contains(p_136284_.getUUID())) {
            this.addPlayer(p_136284_);
        }

    }

    public void onPlayerDisconnect(ServerPlayer p_136287_) {
        super.removePlayer(p_136287_);
    }
}
