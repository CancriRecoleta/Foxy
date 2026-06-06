//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsServer extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsWorldOptions> slots;
    public boolean expired;
    public boolean expiredTrial;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public String minigameImage;
    public RealmsServerPing serverPing = new RealmsServerPing();

    public RealmsServer() {
    }

    public String getDescription() {
        return this.motd;
    }

    public String getName() {
        return this.name;
    }

    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String p_87509_) {
        this.name = p_87509_;
    }

    public void setDescription(String p_87516_) {
        this.motd = p_87516_;
    }

    public void updateServerPing(RealmsServerPlayerList p_87507_) {
        List<String> $$1 = Lists.newArrayList();
        int $$2 = 0;
        Iterator var4 = p_87507_.players.iterator();

        while(true) {
            String $$3;
            do {
                if (!var4.hasNext()) {
                    this.serverPing.nrOfPlayers = String.valueOf($$2);
                    this.serverPing.playerList = Joiner.on('\n').join($$1);
                    return;
                }

                $$3 = (String)var4.next();
            } while($$3.equals(Minecraft.getInstance().getUser().getUuid()));

            String $$4 = "";

            try {
                $$4 = RealmsUtil.uuidToName($$3);
            } catch (Exception var8) {
                Exception $$5 = var8;
                LOGGER.error("Could not get name for {}", $$3, $$5);
                continue;
            }

            $$1.add($$4);
            ++$$2;
        }
    }

    public static RealmsServer parse(JsonObject p_87500_) {
        RealmsServer $$1 = new RealmsServer();

        try {
            $$1.id = JsonUtils.getLongOr("id", p_87500_, -1L);
            $$1.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", p_87500_, (String)null);
            $$1.name = JsonUtils.getStringOr("name", p_87500_, (String)null);
            $$1.motd = JsonUtils.getStringOr("motd", p_87500_, (String)null);
            $$1.state = getState(JsonUtils.getStringOr("state", p_87500_, com.mojang.realmsclient.dto.RealmsServer.State.CLOSED.name()));
            $$1.owner = JsonUtils.getStringOr("owner", p_87500_, (String)null);
            if (p_87500_.get("players") != null && p_87500_.get("players").isJsonArray()) {
                $$1.players = parseInvited(p_87500_.get("players").getAsJsonArray());
                sortInvited($$1);
            } else {
                $$1.players = Lists.newArrayList();
            }

            $$1.daysLeft = JsonUtils.getIntOr("daysLeft", p_87500_, 0);
            $$1.expired = JsonUtils.getBooleanOr("expired", p_87500_, false);
            $$1.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", p_87500_, false);
            $$1.worldType = getWorldType(JsonUtils.getStringOr("worldType", p_87500_, com.mojang.realmsclient.dto.RealmsServer.WorldType.NORMAL.name()));
            $$1.ownerUUID = JsonUtils.getStringOr("ownerUUID", p_87500_, "");
            if (p_87500_.get("slots") != null && p_87500_.get("slots").isJsonArray()) {
                $$1.slots = parseSlots(p_87500_.get("slots").getAsJsonArray());
            } else {
                $$1.slots = createEmptySlots();
            }

            $$1.minigameName = JsonUtils.getStringOr("minigameName", p_87500_, (String)null);
            $$1.activeSlot = JsonUtils.getIntOr("activeSlot", p_87500_, -1);
            $$1.minigameId = JsonUtils.getIntOr("minigameId", p_87500_, -1);
            $$1.minigameImage = JsonUtils.getStringOr("minigameImage", p_87500_, (String)null);
        } catch (Exception var3) {
            Exception $$2 = var3;
            LOGGER.error("Could not parse McoServer: {}", $$2.getMessage());
        }

        return $$1;
    }

    private static void sortInvited(RealmsServer p_87505_) {
        p_87505_.players.sort((p_87502_, p_87503_) -> {
            return ComparisonChain.start().compareFalseFirst(p_87503_.getAccepted(), p_87502_.getAccepted()).compare(p_87502_.getName().toLowerCase(Locale.ROOT), p_87503_.getName().toLowerCase(Locale.ROOT)).result();
        });
    }

    private static List<PlayerInfo> parseInvited(JsonArray p_87498_) {
        List<PlayerInfo> $$1 = Lists.newArrayList();
        Iterator var2 = p_87498_.iterator();

        while(var2.hasNext()) {
            JsonElement $$2 = (JsonElement)var2.next();

            try {
                JsonObject $$3 = $$2.getAsJsonObject();
                PlayerInfo $$4 = new PlayerInfo();
                $$4.setName(JsonUtils.getStringOr("name", $$3, (String)null));
                $$4.setUuid(JsonUtils.getStringOr("uuid", $$3, (String)null));
                $$4.setOperator(JsonUtils.getBooleanOr("operator", $$3, false));
                $$4.setAccepted(JsonUtils.getBooleanOr("accepted", $$3, false));
                $$4.setOnline(JsonUtils.getBooleanOr("online", $$3, false));
                $$1.add($$4);
            } catch (Exception var6) {
            }
        }

        return $$1;
    }

    private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray p_87514_) {
        Map<Integer, RealmsWorldOptions> $$1 = Maps.newHashMap();
        Iterator var2 = p_87514_.iterator();

        while(var2.hasNext()) {
            JsonElement $$2 = (JsonElement)var2.next();

            try {
                JsonObject $$3 = $$2.getAsJsonObject();
                JsonParser $$4 = new JsonParser();
                JsonElement $$5 = $$4.parse($$3.get("options").getAsString());
                RealmsWorldOptions $$7;
                if ($$5 == null) {
                    $$7 = RealmsWorldOptions.createDefaults();
                } else {
                    $$7 = RealmsWorldOptions.parse($$5.getAsJsonObject());
                }

                int $$8 = JsonUtils.getIntOr("slotId", $$3, -1);
                $$1.put($$8, $$7);
            } catch (Exception var9) {
            }
        }

        for(int $$9 = 1; $$9 <= 3; ++$$9) {
            if (!$$1.containsKey($$9)) {
                $$1.put($$9, RealmsWorldOptions.createEmptyDefaults());
            }
        }

        return $$1;
    }

    private static Map<Integer, RealmsWorldOptions> createEmptySlots() {
        Map<Integer, RealmsWorldOptions> $$0 = Maps.newHashMap();
        $$0.put(1, RealmsWorldOptions.createEmptyDefaults());
        $$0.put(2, RealmsWorldOptions.createEmptyDefaults());
        $$0.put(3, RealmsWorldOptions.createEmptyDefaults());
        return $$0;
    }

    public static RealmsServer parse(String p_87519_) {
        try {
            return parse((new JsonParser()).parse(p_87519_).getAsJsonObject());
        } catch (Exception var2) {
            Exception $$1 = var2;
            LOGGER.error("Could not parse McoServer: {}", $$1.getMessage());
            return new RealmsServer();
        }
    }

    private static State getState(String p_87526_) {
        try {
            return com.mojang.realmsclient.dto.RealmsServer.State.valueOf(p_87526_);
        } catch (Exception var2) {
            return com.mojang.realmsclient.dto.RealmsServer.State.CLOSED;
        }
    }

    private static WorldType getWorldType(String p_87530_) {
        try {
            return com.mojang.realmsclient.dto.RealmsServer.WorldType.valueOf(p_87530_);
        } catch (Exception var2) {
            return com.mojang.realmsclient.dto.RealmsServer.WorldType.NORMAL;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
    }

    public boolean equals(Object p_87528_) {
        if (p_87528_ == null) {
            return false;
        } else if (p_87528_ == this) {
            return true;
        } else if (p_87528_.getClass() != this.getClass()) {
            return false;
        } else {
            RealmsServer $$1 = (RealmsServer)p_87528_;
            return (new EqualsBuilder()).append(this.id, $$1.id).append(this.name, $$1.name).append(this.motd, $$1.motd).append(this.state, $$1.state).append(this.owner, $$1.owner).append(this.expired, $$1.expired).append(this.worldType, this.worldType).isEquals();
        }
    }

    public RealmsServer clone() {
        RealmsServer $$0 = new RealmsServer();
        $$0.id = this.id;
        $$0.remoteSubscriptionId = this.remoteSubscriptionId;
        $$0.name = this.name;
        $$0.motd = this.motd;
        $$0.state = this.state;
        $$0.owner = this.owner;
        $$0.players = this.players;
        $$0.slots = this.cloneSlots(this.slots);
        $$0.expired = this.expired;
        $$0.expiredTrial = this.expiredTrial;
        $$0.daysLeft = this.daysLeft;
        $$0.serverPing = new RealmsServerPing();
        $$0.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        $$0.serverPing.playerList = this.serverPing.playerList;
        $$0.worldType = this.worldType;
        $$0.ownerUUID = this.ownerUUID;
        $$0.minigameName = this.minigameName;
        $$0.activeSlot = this.activeSlot;
        $$0.minigameId = this.minigameId;
        $$0.minigameImage = this.minigameImage;
        return $$0;
    }

    public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> p_87511_) {
        Map<Integer, RealmsWorldOptions> $$1 = Maps.newHashMap();
        Iterator var3 = p_87511_.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<Integer, RealmsWorldOptions> $$2 = (Map.Entry)var3.next();
            $$1.put((Integer)$$2.getKey(), ((RealmsWorldOptions)$$2.getValue()).clone());
        }

        return $$1;
    }

    public String getWorldName(int p_87496_) {
        return this.name + " (" + ((RealmsWorldOptions)this.slots.get(p_87496_)).getSlotName(p_87496_) + ")";
    }

    public ServerData toServerData(String p_87523_) {
        return new ServerData(this.name, p_87523_, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static enum State {
        CLOSED,
        OPEN,
        UNINITIALIZED;

        private State() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum WorldType {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

        private WorldType() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class McoServerComparator implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String p_87534_) {
            this.refOwner = p_87534_;
        }

        public int compare(RealmsServer p_87536_, RealmsServer p_87537_) {
            return ComparisonChain.start().compareTrueFirst(p_87536_.state == com.mojang.realmsclient.dto.RealmsServer.State.UNINITIALIZED, p_87537_.state == com.mojang.realmsclient.dto.RealmsServer.State.UNINITIALIZED).compareTrueFirst(p_87536_.expiredTrial, p_87537_.expiredTrial).compareTrueFirst(p_87536_.owner.equals(this.refOwner), p_87537_.owner.equals(this.refOwner)).compareFalseFirst(p_87536_.expired, p_87537_.expired).compareTrueFirst(p_87536_.state == com.mojang.realmsclient.dto.RealmsServer.State.OPEN, p_87537_.state == com.mojang.realmsclient.dto.RealmsServer.State.OPEN).compare(p_87536_.id, p_87537_.id).result();
        }
    }
}
