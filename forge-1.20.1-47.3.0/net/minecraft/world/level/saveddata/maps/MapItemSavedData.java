//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import org.slf4j.Logger;

public class MapItemSavedData extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAP_SIZE = 128;
    private static final int HALF_MAP_SIZE = 64;
    public static final int MAX_SCALE = 4;
    public static final int TRACKED_DECORATION_LIMIT = 256;
    public final int centerX;
    public final int centerZ;
    public final ResourceKey<Level> dimension;
    private final boolean trackingPosition;
    private final boolean unlimitedTracking;
    public final byte scale;
    public byte[] colors = new byte[16384];
    public final boolean locked;
    private final List<HoldingPlayer> carriedBy = Lists.newArrayList();
    private final Map<Player, HoldingPlayer> carriedByPlayers = Maps.newHashMap();
    private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
    final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
    private int trackedDecorationCount;

    private MapItemSavedData(int p_164768_, int p_164769_, byte p_164770_, boolean p_164771_, boolean p_164772_, boolean p_164773_, ResourceKey<Level> p_164774_) {
        this.scale = p_164770_;
        this.centerX = p_164768_;
        this.centerZ = p_164769_;
        this.dimension = p_164774_;
        this.trackingPosition = p_164771_;
        this.unlimitedTracking = p_164772_;
        this.locked = p_164773_;
        this.setDirty();
    }

    public static MapItemSavedData createFresh(double p_164781_, double p_164782_, byte p_164783_, boolean p_164784_, boolean p_164785_, ResourceKey<Level> p_164786_) {
        int $$6 = 128 * (1 << p_164783_);
        int $$7 = Mth.floor((p_164781_ + 64.0) / (double)$$6);
        int $$8 = Mth.floor((p_164782_ + 64.0) / (double)$$6);
        int $$9 = $$7 * $$6 + $$6 / 2 - 64;
        int $$10 = $$8 * $$6 + $$6 / 2 - 64;
        return new MapItemSavedData($$9, $$10, p_164783_, p_164784_, p_164785_, false, p_164786_);
    }

    public static MapItemSavedData createForClient(byte p_164777_, boolean p_164778_, ResourceKey<Level> p_164779_) {
        return new MapItemSavedData(0, 0, p_164777_, false, false, p_164778_, p_164779_);
    }

    public static MapItemSavedData load(CompoundTag p_164808_) {
        DataResult var10000 = DimensionType.parseLegacy(new Dynamic(NbtOps.INSTANCE, p_164808_.get("dimension")));
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        ResourceKey<Level> $$1 = (ResourceKey)var10000.resultOrPartial(var10001::error).orElseThrow(() -> {
            return new IllegalArgumentException("Invalid map dimension: " + p_164808_.get("dimension"));
        });
        int $$2 = p_164808_.getInt("xCenter");
        int $$3 = p_164808_.getInt("zCenter");
        byte $$4 = (byte)Mth.clamp(p_164808_.getByte("scale"), 0, 4);
        boolean $$5 = !p_164808_.contains("trackingPosition", 1) || p_164808_.getBoolean("trackingPosition");
        boolean $$6 = p_164808_.getBoolean("unlimitedTracking");
        boolean $$7 = p_164808_.getBoolean("locked");
        MapItemSavedData $$8 = new MapItemSavedData($$2, $$3, $$4, $$5, $$6, $$7, $$1);
        byte[] $$9 = p_164808_.getByteArray("colors");
        if ($$9.length == 16384) {
            $$8.colors = $$9;
        }

        ListTag $$10 = p_164808_.getList("banners", 10);

        for(int $$11 = 0; $$11 < $$10.size(); ++$$11) {
            MapBanner $$12 = MapBanner.load($$10.getCompound($$11));
            $$8.bannerMarkers.put($$12.getId(), $$12);
            $$8.addDecoration($$12.getDecoration(), (LevelAccessor)null, $$12.getId(), (double)$$12.getPos().getX(), (double)$$12.getPos().getZ(), 180.0, $$12.getName());
        }

        ListTag $$13 = p_164808_.getList("frames", 10);

        for(int $$14 = 0; $$14 < $$13.size(); ++$$14) {
            MapFrame $$15 = MapFrame.load($$13.getCompound($$14));
            $$8.frameMarkers.put($$15.getId(), $$15);
            $$8.addDecoration(Type.FRAME, (LevelAccessor)null, "frame-" + $$15.getEntityId(), (double)$$15.getPos().getX(), (double)$$15.getPos().getZ(), (double)$$15.getRotation(), (Component)null);
        }

        return $$8;
    }

    public CompoundTag save(CompoundTag p_77956_) {
        DataResult var10000 = ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.location());
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((p_77954_) -> {
            p_77956_.put("dimension", p_77954_);
        });
        p_77956_.putInt("xCenter", this.centerX);
        p_77956_.putInt("zCenter", this.centerZ);
        p_77956_.putByte("scale", this.scale);
        p_77956_.putByteArray("colors", this.colors);
        p_77956_.putBoolean("trackingPosition", this.trackingPosition);
        p_77956_.putBoolean("unlimitedTracking", this.unlimitedTracking);
        p_77956_.putBoolean("locked", this.locked);
        ListTag $$1 = new ListTag();
        Iterator var3 = this.bannerMarkers.values().iterator();

        while(var3.hasNext()) {
            MapBanner $$2 = (MapBanner)var3.next();
            $$1.add($$2.save());
        }

        p_77956_.put("banners", $$1);
        ListTag $$3 = new ListTag();
        Iterator var7 = this.frameMarkers.values().iterator();

        while(var7.hasNext()) {
            MapFrame $$4 = (MapFrame)var7.next();
            $$3.add($$4.save());
        }

        p_77956_.put("frames", $$3);
        return p_77956_;
    }

    public MapItemSavedData locked() {
        MapItemSavedData $$0 = new MapItemSavedData(this.centerX, this.centerZ, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
        $$0.bannerMarkers.putAll(this.bannerMarkers);
        $$0.decorations.putAll(this.decorations);
        $$0.trackedDecorationCount = this.trackedDecorationCount;
        System.arraycopy(this.colors, 0, $$0.colors, 0, this.colors.length);
        $$0.setDirty();
        return $$0;
    }

    public MapItemSavedData scaled(int p_164788_) {
        return createFresh((double)this.centerX, (double)this.centerZ, (byte)Mth.clamp(this.scale + p_164788_, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
    }

    public void tickCarriedBy(Player p_77919_, ItemStack p_77920_) {
        if (!this.carriedByPlayers.containsKey(p_77919_)) {
            HoldingPlayer $$2 = new HoldingPlayer(p_77919_);
            this.carriedByPlayers.put(p_77919_, $$2);
            this.carriedBy.add($$2);
        }

        if (!p_77919_.getInventory().contains(p_77920_)) {
            this.removeDecoration(p_77919_.getName().getString());
        }

        for(int $$3 = 0; $$3 < this.carriedBy.size(); ++$$3) {
            HoldingPlayer $$4 = (HoldingPlayer)this.carriedBy.get($$3);
            String $$5 = $$4.player.getName().getString();
            if (!$$4.player.isRemoved() && ($$4.player.getInventory().contains(p_77920_) || p_77920_.isFramed())) {
                if (!p_77920_.isFramed() && $$4.player.level().dimension() == this.dimension && this.trackingPosition) {
                    this.addDecoration(Type.PLAYER, $$4.player.level(), $$5, $$4.player.getX(), $$4.player.getZ(), (double)$$4.player.getYRot(), (Component)null);
                }
            } else {
                this.carriedByPlayers.remove($$4.player);
                this.carriedBy.remove($$4);
                this.removeDecoration($$5);
            }
        }

        if (p_77920_.isFramed() && this.trackingPosition) {
            ItemFrame $$6 = p_77920_.getFrame();
            BlockPos $$7 = $$6.getPos();
            MapFrame $$8 = (MapFrame)this.frameMarkers.get(MapFrame.frameId($$7));
            if ($$8 != null && $$6.getId() != $$8.getEntityId() && this.frameMarkers.containsKey($$8.getId())) {
                this.removeDecoration("frame-" + $$8.getEntityId());
            }

            MapFrame $$9 = new MapFrame($$7, $$6.getDirection().get2DDataValue() * 90, $$6.getId());
            this.addDecoration(Type.FRAME, p_77919_.level(), "frame-" + $$6.getId(), (double)$$7.getX(), (double)$$7.getZ(), (double)($$6.getDirection().get2DDataValue() * 90), (Component)null);
            this.frameMarkers.put($$9.getId(), $$9);
        }

        CompoundTag $$10 = p_77920_.getTag();
        if ($$10 != null && $$10.contains("Decorations", 9)) {
            ListTag $$11 = $$10.getList("Decorations", 10);

            for(int $$12 = 0; $$12 < $$11.size(); ++$$12) {
                CompoundTag $$13 = $$11.getCompound($$12);
                if (!this.decorations.containsKey($$13.getString("id"))) {
                    this.addDecoration(Type.byIcon($$13.getByte("type")), p_77919_.level(), $$13.getString("id"), $$13.getDouble("x"), $$13.getDouble("z"), $$13.getDouble("rot"), (Component)null);
                }
            }
        }

    }

    private void removeDecoration(String p_164800_) {
        MapDecoration $$1 = (MapDecoration)this.decorations.remove(p_164800_);
        if ($$1 != null && $$1.getType().shouldTrackCount()) {
            --this.trackedDecorationCount;
        }

        this.setDecorationsDirty();
    }

    public static void addTargetDecoration(ItemStack p_77926_, BlockPos p_77927_, String p_77928_, MapDecoration.Type p_77929_) {
        ListTag $$5;
        if (p_77926_.hasTag() && p_77926_.getTag().contains("Decorations", 9)) {
            $$5 = p_77926_.getTag().getList("Decorations", 10);
        } else {
            $$5 = new ListTag();
            p_77926_.addTagElement("Decorations", $$5);
        }

        CompoundTag $$6 = new CompoundTag();
        $$6.putByte("type", p_77929_.getIcon());
        $$6.putString("id", p_77928_);
        $$6.putDouble("x", (double)p_77927_.getX());
        $$6.putDouble("z", (double)p_77927_.getZ());
        $$6.putDouble("rot", 180.0);
        $$5.add($$6);
        if (p_77929_.hasMapColor()) {
            CompoundTag $$7 = p_77926_.getOrCreateTagElement("display");
            $$7.putInt("MapColor", p_77929_.getMapColor());
        }

    }

    private void addDecoration(MapDecoration.Type p_77938_, @Nullable LevelAccessor p_77939_, String p_77940_, double p_77941_, double p_77942_, double p_77943_, @Nullable Component p_77944_) {
        int $$7 = 1 << this.scale;
        float $$8 = (float)(p_77941_ - (double)this.centerX) / (float)$$7;
        float $$9 = (float)(p_77942_ - (double)this.centerZ) / (float)$$7;
        byte $$10 = (byte)((int)((double)($$8 * 2.0F) + 0.5));
        byte $$11 = (byte)((int)((double)($$9 * 2.0F) + 0.5));
        int $$12 = true;
        byte $$16;
        if ($$8 >= -63.0F && $$9 >= -63.0F && $$8 <= 63.0F && $$9 <= 63.0F) {
            p_77943_ += p_77943_ < 0.0 ? -8.0 : 8.0;
            $$16 = (byte)((int)(p_77943_ * 16.0 / 360.0));
            if (this.dimension == Level.NETHER && p_77939_ != null) {
                int $$14 = (int)(p_77939_.getLevelData().getDayTime() / 10L);
                $$16 = (byte)($$14 * $$14 * 34187121 + $$14 * 121 >> 15 & 15);
            }
        } else {
            if (p_77938_ != Type.PLAYER) {
                this.removeDecoration(p_77940_);
                return;
            }

            int $$15 = true;
            if (Math.abs($$8) < 320.0F && Math.abs($$9) < 320.0F) {
                p_77938_ = Type.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.removeDecoration(p_77940_);
                    return;
                }

                p_77938_ = Type.PLAYER_OFF_LIMITS;
            }

            $$16 = 0;
            if ($$8 <= -63.0F) {
                $$10 = -128;
            }

            if ($$9 <= -63.0F) {
                $$11 = -128;
            }

            if ($$8 >= 63.0F) {
                $$10 = 127;
            }

            if ($$9 >= 63.0F) {
                $$11 = 127;
            }
        }

        MapDecoration $$18 = new MapDecoration(p_77938_, $$10, $$11, $$16, p_77944_);
        MapDecoration $$19 = (MapDecoration)this.decorations.put(p_77940_, $$18);
        if (!$$18.equals($$19)) {
            if ($$19 != null && $$19.getType().shouldTrackCount()) {
                --this.trackedDecorationCount;
            }

            if (p_77938_.shouldTrackCount()) {
                ++this.trackedDecorationCount;
            }

            this.setDecorationsDirty();
        }

    }

    @Nullable
    public Packet<?> getUpdatePacket(int p_164797_, Player p_164798_) {
        HoldingPlayer $$2 = (HoldingPlayer)this.carriedByPlayers.get(p_164798_);
        return $$2 == null ? null : $$2.nextUpdatePacket(p_164797_);
    }

    private void setColorsDirty(int p_164790_, int p_164791_) {
        this.setDirty();
        Iterator var3 = this.carriedBy.iterator();

        while(var3.hasNext()) {
            HoldingPlayer $$2 = (HoldingPlayer)var3.next();
            $$2.markColorsDirty(p_164790_, p_164791_);
        }

    }

    private void setDecorationsDirty() {
        this.setDirty();
        this.carriedBy.forEach(HoldingPlayer::markDecorationsDirty);
    }

    public HoldingPlayer getHoldingPlayer(Player p_77917_) {
        HoldingPlayer $$1 = (HoldingPlayer)this.carriedByPlayers.get(p_77917_);
        if ($$1 == null) {
            $$1 = new HoldingPlayer(p_77917_);
            this.carriedByPlayers.put(p_77917_, $$1);
            this.carriedBy.add($$1);
        }

        return $$1;
    }

    public boolean toggleBanner(LevelAccessor p_77935_, BlockPos p_77936_) {
        double $$2 = (double)p_77936_.getX() + 0.5;
        double $$3 = (double)p_77936_.getZ() + 0.5;
        int $$4 = 1 << this.scale;
        double $$5 = ($$2 - (double)this.centerX) / (double)$$4;
        double $$6 = ($$3 - (double)this.centerZ) / (double)$$4;
        int $$7 = true;
        if ($$5 >= -63.0 && $$6 >= -63.0 && $$5 <= 63.0 && $$6 <= 63.0) {
            MapBanner $$8 = MapBanner.fromWorld(p_77935_, p_77936_);
            if ($$8 == null) {
                return false;
            }

            if (this.bannerMarkers.remove($$8.getId(), $$8)) {
                this.removeDecoration($$8.getId());
                return true;
            }

            if (!this.isTrackedCountOverLimit(256)) {
                this.bannerMarkers.put($$8.getId(), $$8);
                this.addDecoration($$8.getDecoration(), p_77935_, $$8.getId(), $$2, $$3, 180.0, $$8.getName());
                return true;
            }
        }

        return false;
    }

    public void checkBanners(BlockGetter p_77931_, int p_77932_, int p_77933_) {
        Iterator<MapBanner> $$3 = this.bannerMarkers.values().iterator();

        while($$3.hasNext()) {
            MapBanner $$4 = (MapBanner)$$3.next();
            if ($$4.getPos().getX() == p_77932_ && $$4.getPos().getZ() == p_77933_) {
                MapBanner $$5 = MapBanner.fromWorld(p_77931_, $$4.getPos());
                if (!$$4.equals($$5)) {
                    $$3.remove();
                    this.removeDecoration($$4.getId());
                }
            }
        }

    }

    public Collection<MapBanner> getBanners() {
        return this.bannerMarkers.values();
    }

    public void removedFromFrame(BlockPos p_77948_, int p_77949_) {
        this.removeDecoration("frame-" + p_77949_);
        this.frameMarkers.remove(MapFrame.frameId(p_77948_));
    }

    public boolean updateColor(int p_164793_, int p_164794_, byte p_164795_) {
        byte $$3 = this.colors[p_164793_ + p_164794_ * 128];
        if ($$3 != p_164795_) {
            this.setColor(p_164793_, p_164794_, p_164795_);
            return true;
        } else {
            return false;
        }
    }

    public void setColor(int p_164804_, int p_164805_, byte p_164806_) {
        this.colors[p_164804_ + p_164805_ * 128] = p_164806_;
        this.setColorsDirty(p_164804_, p_164805_);
    }

    public boolean isExplorationMap() {
        Iterator var1 = this.decorations.values().iterator();

        MapDecoration $$0;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            $$0 = (MapDecoration)var1.next();
        } while($$0.getType() != Type.MANSION && $$0.getType() != Type.MONUMENT);

        return true;
    }

    public void addClientSideDecorations(List<MapDecoration> p_164802_) {
        this.decorations.clear();
        this.trackedDecorationCount = 0;

        for(int $$1 = 0; $$1 < p_164802_.size(); ++$$1) {
            MapDecoration $$2 = (MapDecoration)p_164802_.get($$1);
            this.decorations.put("icon-" + $$1, $$2);
            if ($$2.getType().shouldTrackCount()) {
                ++this.trackedDecorationCount;
            }
        }

    }

    public Iterable<MapDecoration> getDecorations() {
        return this.decorations.values();
    }

    public boolean isTrackedCountOverLimit(int p_181313_) {
        return this.trackedDecorationCount >= p_181313_;
    }

    public class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        private int minDirtyX;
        private int minDirtyY;
        private int maxDirtyX = 127;
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        HoldingPlayer(Player p_77970_) {
            this.player = p_77970_;
        }

        private MapPatch createPatch() {
            int $$0 = this.minDirtyX;
            int $$1 = this.minDirtyY;
            int $$2 = this.maxDirtyX + 1 - this.minDirtyX;
            int $$3 = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] $$4 = new byte[$$2 * $$3];

            for(int $$5 = 0; $$5 < $$2; ++$$5) {
                for(int $$6 = 0; $$6 < $$3; ++$$6) {
                    $$4[$$5 + $$6 * $$2] = MapItemSavedData.this.colors[$$0 + $$5 + ($$1 + $$6) * 128];
                }
            }

            return new MapPatch($$0, $$1, $$2, $$3, $$4);
        }

        @Nullable
        Packet<?> nextUpdatePacket(int p_164816_) {
            MapPatch $$2;
            if (this.dirtyData) {
                this.dirtyData = false;
                $$2 = this.createPatch();
            } else {
                $$2 = null;
            }

            Collection $$4;
            if (this.dirtyDecorations && this.tick++ % 5 == 0) {
                this.dirtyDecorations = false;
                $$4 = MapItemSavedData.this.decorations.values();
            } else {
                $$4 = null;
            }

            return $$4 == null && $$2 == null ? null : new ClientboundMapItemDataPacket(p_164816_, MapItemSavedData.this.scale, MapItemSavedData.this.locked, $$4, $$2);
        }

        void markColorsDirty(int p_164818_, int p_164819_) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, p_164818_);
                this.minDirtyY = Math.min(this.minDirtyY, p_164819_);
                this.maxDirtyX = Math.max(this.maxDirtyX, p_164818_);
                this.maxDirtyY = Math.max(this.maxDirtyY, p_164819_);
            } else {
                this.dirtyData = true;
                this.minDirtyX = p_164818_;
                this.minDirtyY = p_164819_;
                this.maxDirtyX = p_164818_;
                this.maxDirtyY = p_164819_;
            }

        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    public static class MapPatch {
        public final int startX;
        public final int startY;
        public final int width;
        public final int height;
        public final byte[] mapColors;

        public MapPatch(int p_164827_, int p_164828_, int p_164829_, int p_164830_, byte[] p_164831_) {
            this.startX = p_164827_;
            this.startY = p_164828_;
            this.width = p_164829_;
            this.height = p_164830_;
            this.mapColors = p_164831_;
        }

        public void applyToMap(MapItemSavedData p_164833_) {
            for(int $$1 = 0; $$1 < this.width; ++$$1) {
                for(int $$2 = 0; $$2 < this.height; ++$$2) {
                    p_164833_.setColor(this.startX + $$1, this.startY + $$2, this.mapColors[$$1 + $$2 * this.width]);
                }
            }

        }
    }
}
