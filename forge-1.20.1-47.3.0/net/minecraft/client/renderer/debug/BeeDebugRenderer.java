//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final boolean SHOW_GOAL_FOR_ALL_BEES = true;
    private static final boolean SHOW_NAME_FOR_ALL_BEES = true;
    private static final boolean SHOW_HIVE_FOR_ALL_BEES = true;
    private static final boolean SHOW_FLOWER_POS_FOR_ALL_BEES = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_ALL_BEES = true;
    private static final boolean SHOW_PATH_FOR_ALL_BEES = false;
    private static final boolean SHOW_GOAL_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_NAME_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_FLOWER_POS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_TRAVEL_TICKS_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_PATH_FOR_SELECTED_BEE = true;
    private static final boolean SHOW_HIVE_MEMBERS = true;
    private static final boolean SHOW_BLACKLISTS = true;
    private static final int MAX_RENDER_DIST_FOR_HIVE_OVERLAY = 30;
    private static final int MAX_RENDER_DIST_FOR_BEE_OVERLAY = 30;
    private static final int MAX_TARGETING_DIST = 8;
    private static final int HIVE_TIMEOUT = 20;
    private static final float TEXT_SCALE = 0.02F;
    private static final int WHITE = -1;
    private static final int YELLOW = -256;
    private static final int ORANGE = -23296;
    private static final int GREEN = -16711936;
    private static final int GRAY = -3355444;
    private static final int PINK = -98404;
    private static final int RED = -65536;
    private final Minecraft minecraft;
    private final Map<BlockPos, HiveInfo> hives = Maps.newHashMap();
    private final Map<UUID, BeeInfo> beeInfosPerEntity = Maps.newHashMap();
    private UUID lastLookedAtUuid;

    public BeeDebugRenderer(Minecraft p_113053_) {
        this.minecraft = p_113053_;
    }

    public void clear() {
        this.hives.clear();
        this.beeInfosPerEntity.clear();
        this.lastLookedAtUuid = null;
    }

    public void addOrUpdateHiveInfo(HiveInfo p_113072_) {
        this.hives.put(p_113072_.pos, p_113072_);
    }

    public void addOrUpdateBeeInfo(BeeInfo p_113067_) {
        this.beeInfosPerEntity.put(p_113067_.uuid, p_113067_);
    }

    public void removeBeeInfo(int p_173764_) {
        this.beeInfosPerEntity.values().removeIf((p_173767_) -> {
            return p_173767_.id == p_173764_;
        });
    }

    public void render(PoseStack p_113061_, MultiBufferSource p_113062_, double p_113063_, double p_113064_, double p_113065_) {
        this.clearRemovedHives();
        this.clearRemovedBees();
        this.doRender(p_113061_, p_113062_);
        if (!this.minecraft.player.isSpectator()) {
            this.updateLastLookedAtUuid();
        }

    }

    private void clearRemovedBees() {
        this.beeInfosPerEntity.entrySet().removeIf((p_113132_) -> {
            return this.minecraft.level.getEntity(((BeeInfo)p_113132_.getValue()).id) == null;
        });
    }

    private void clearRemovedHives() {
        long $$0 = this.minecraft.level.getGameTime() - 20L;
        this.hives.entrySet().removeIf((p_113057_) -> {
            return ((HiveInfo)p_113057_.getValue()).lastSeen < $$0;
        });
    }

    private void doRender(PoseStack p_270886_, MultiBufferSource p_270808_) {
        BlockPos $$2 = this.getCamera().getBlockPosition();
        this.beeInfosPerEntity.values().forEach((p_269703_) -> {
            if (this.isPlayerCloseEnoughToMob(p_269703_)) {
                this.renderBeeInfo(p_270886_, p_270808_, p_269703_);
            }

        });
        this.renderFlowerInfos(p_270886_, p_270808_);
        Iterator var4 = this.hives.keySet().iterator();

        while(var4.hasNext()) {
            BlockPos $$3 = (BlockPos)var4.next();
            if ($$2.closerThan($$3, 30.0)) {
                highlightHive(p_270886_, p_270808_, $$3);
            }
        }

        Map<BlockPos, Set<UUID>> $$4 = this.createHiveBlacklistMap();
        this.hives.values().forEach((p_269692_) -> {
            if ($$2.closerThan(p_269692_.pos, 30.0)) {
                Set<UUID> $$5 = (Set)$$4.get(p_269692_.pos);
                this.renderHiveInfo(p_270886_, p_270808_, p_269692_, (Collection)($$5 == null ? Sets.newHashSet() : $$5));
            }

        });
        this.getGhostHives().forEach((p_269699_, p_269700_) -> {
            if ($$2.closerThan(p_269699_, 30.0)) {
                this.renderGhostHive(p_270886_, p_270808_, p_269699_, p_269700_);
            }

        });
    }

    private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
        Map<BlockPos, Set<UUID>> $$0 = Maps.newHashMap();
        this.beeInfosPerEntity.values().forEach((p_113135_) -> {
            p_113135_.blacklistedHives.forEach((p_173771_) -> {
                ((Set)$$0.computeIfAbsent(p_173771_, (p_173777_) -> {
                    return Sets.newHashSet();
                })).add(p_113135_.getUuid());
            });
        });
        return $$0;
    }

    private void renderFlowerInfos(PoseStack p_270578_, MultiBufferSource p_270098_) {
        Map<BlockPos, Set<UUID>> $$2 = Maps.newHashMap();
        this.beeInfosPerEntity.values().stream().filter(BeeInfo::hasFlower).forEach((p_113121_) -> {
            ((Set)$$2.computeIfAbsent(p_113121_.flowerPos, (p_173775_) -> {
                return Sets.newHashSet();
            })).add(p_113121_.getUuid());
        });
        $$2.entrySet().forEach((p_269695_) -> {
            BlockPos $$3 = (BlockPos)p_269695_.getKey();
            Set<UUID> $$4 = (Set)p_269695_.getValue();
            Set<String> $$5 = (Set)$$4.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
            int $$6 = 1;
            renderTextOverPos(p_270578_, p_270098_, $$5.toString(), $$3, $$6++, -256);
            renderTextOverPos(p_270578_, p_270098_, "Flower", $$3, $$6++, -1);
            float $$7 = 0.05F;
            DebugRenderer.renderFilledBox(p_270578_, p_270098_, $$3, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
        });
    }

    private static String getBeeUuidsAsString(Collection<UUID> p_113116_) {
        if (p_113116_.isEmpty()) {
            return "-";
        } else {
            return p_113116_.size() > 3 ? p_113116_.size() + " bees" : ((Set)p_113116_.stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet())).toString();
        }
    }

    private static void highlightHive(PoseStack p_270133_, MultiBufferSource p_270766_, BlockPos p_270687_) {
        float $$3 = 0.05F;
        DebugRenderer.renderFilledBox(p_270133_, p_270766_, p_270687_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
    }

    private void renderGhostHive(PoseStack p_270949_, MultiBufferSource p_270718_, BlockPos p_270550_, List<String> p_270221_) {
        float $$4 = 0.05F;
        DebugRenderer.renderFilledBox(p_270949_, p_270718_, p_270550_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
        renderTextOverPos(p_270949_, p_270718_, "" + p_270221_, p_270550_, 0, -256);
        renderTextOverPos(p_270949_, p_270718_, "Ghost Hive", p_270550_, 1, -65536);
    }

    private void renderHiveInfo(PoseStack p_270194_, MultiBufferSource p_270431_, HiveInfo p_270658_, Collection<UUID> p_270946_) {
        int $$4 = 0;
        if (!p_270946_.isEmpty()) {
            renderTextOverHive(p_270194_, p_270431_, "Blacklisted by " + getBeeUuidsAsString(p_270946_), p_270658_, $$4++, -65536);
        }

        renderTextOverHive(p_270194_, p_270431_, "Out: " + getBeeUuidsAsString(this.getHiveMembers(p_270658_.pos)), p_270658_, $$4++, -3355444);
        if (p_270658_.occupantCount == 0) {
            renderTextOverHive(p_270194_, p_270431_, "In: -", p_270658_, $$4++, -256);
        } else if (p_270658_.occupantCount == 1) {
            renderTextOverHive(p_270194_, p_270431_, "In: 1 bee", p_270658_, $$4++, -256);
        } else {
            renderTextOverHive(p_270194_, p_270431_, "In: " + p_270658_.occupantCount + " bees", p_270658_, $$4++, -256);
        }

        int var6 = p_270658_.honeyLevel;
        renderTextOverHive(p_270194_, p_270431_, "Honey: " + var6, p_270658_, $$4++, -23296);
        renderTextOverHive(p_270194_, p_270431_, p_270658_.hiveType + (p_270658_.sedated ? " (sedated)" : ""), p_270658_, $$4++, -1);
    }

    private void renderPath(PoseStack p_270424_, MultiBufferSource p_270123_, BeeInfo p_270137_) {
        if (p_270137_.path != null) {
            PathfindingRenderer.renderPath(p_270424_, p_270123_, p_270137_.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
        }

    }

    private void renderBeeInfo(PoseStack p_270154_, MultiBufferSource p_270397_, BeeInfo p_270783_) {
        boolean $$3 = this.isBeeSelected(p_270783_);
        int $$4 = 0;
        renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, p_270783_.toString(), -1, 0.03F);
        if (p_270783_.hivePos == null) {
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, "No hive", -98404, 0.02F);
        } else {
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, "Hive: " + this.getPosDescription(p_270783_, p_270783_.hivePos), -256, 0.02F);
        }

        if (p_270783_.flowerPos == null) {
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, "No flower", -98404, 0.02F);
        } else {
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, "Flower: " + this.getPosDescription(p_270783_, p_270783_.flowerPos), -256, 0.02F);
        }

        Iterator var6 = p_270783_.goals.iterator();

        while(var6.hasNext()) {
            String $$5 = (String)var6.next();
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, $$5, -16711936, 0.02F);
        }

        if ($$3) {
            this.renderPath(p_270154_, p_270397_, p_270783_);
        }

        if (p_270783_.travelTicks > 0) {
            int $$6 = p_270783_.travelTicks < 600 ? -3355444 : -23296;
            renderTextOverMob(p_270154_, p_270397_, p_270783_.pos, $$4++, "Travelling: " + p_270783_.travelTicks + " ticks", $$6, 0.02F);
        }

    }

    private static void renderTextOverHive(PoseStack p_270915_, MultiBufferSource p_270663_, String p_270119_, HiveInfo p_270243_, int p_270930_, int p_270094_) {
        BlockPos $$6 = p_270243_.pos;
        renderTextOverPos(p_270915_, p_270663_, p_270119_, $$6, p_270930_, p_270094_);
    }

    private static void renderTextOverPos(PoseStack p_270438_, MultiBufferSource p_270244_, String p_270486_, BlockPos p_270062_, int p_270574_, int p_270228_) {
        double $$6 = 1.3;
        double $$7 = 0.2;
        double $$8 = (double)p_270062_.getX() + 0.5;
        double $$9 = (double)p_270062_.getY() + 1.3 + (double)p_270574_ * 0.2;
        double $$10 = (double)p_270062_.getZ() + 0.5;
        DebugRenderer.renderFloatingText(p_270438_, p_270244_, p_270486_, $$8, $$9, $$10, p_270228_, 0.02F, true, 0.0F, true);
    }

    private static void renderTextOverMob(PoseStack p_270426_, MultiBufferSource p_270600_, Position p_270548_, int p_270592_, String p_270198_, int p_270792_, float p_270938_) {
        double $$7 = 2.4;
        double $$8 = 0.25;
        BlockPos $$9 = BlockPos.containing(p_270548_);
        double $$10 = (double)$$9.getX() + 0.5;
        double $$11 = p_270548_.y() + 2.4 + (double)p_270592_ * 0.25;
        double $$12 = (double)$$9.getZ() + 0.5;
        float $$13 = 0.5F;
        DebugRenderer.renderFloatingText(p_270426_, p_270600_, p_270198_, $$10, $$11, $$12, p_270792_, p_270938_, false, 0.5F, true);
    }

    private Camera getCamera() {
        return this.minecraft.gameRenderer.getMainCamera();
    }

    private Set<String> getHiveMemberNames(HiveInfo p_173773_) {
        return (Set)this.getHiveMembers(p_173773_.pos).stream().map(DebugEntityNameGenerator::getEntityName).collect(Collectors.toSet());
    }

    private String getPosDescription(BeeInfo p_113069_, BlockPos p_113070_) {
        double $$2 = Math.sqrt(p_113070_.distToCenterSqr(p_113069_.pos));
        double $$3 = (double)Math.round($$2 * 10.0) / 10.0;
        String var10000 = p_113070_.toShortString();
        return var10000 + " (dist " + $$3 + ")";
    }

    private boolean isBeeSelected(BeeInfo p_113143_) {
        return Objects.equals(this.lastLookedAtUuid, p_113143_.uuid);
    }

    private boolean isPlayerCloseEnoughToMob(BeeInfo p_113148_) {
        Player $$1 = this.minecraft.player;
        BlockPos $$2 = BlockPos.containing($$1.getX(), p_113148_.pos.y(), $$1.getZ());
        BlockPos $$3 = BlockPos.containing(p_113148_.pos);
        return $$2.closerThan($$3, 30.0);
    }

    private Collection<UUID> getHiveMembers(BlockPos p_113130_) {
        return (Collection)this.beeInfosPerEntity.values().stream().filter((p_113087_) -> {
            return p_113087_.hasHive(p_113130_);
        }).map(BeeInfo::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getGhostHives() {
        Map<BlockPos, List<String>> $$0 = Maps.newHashMap();
        Iterator var2 = this.beeInfosPerEntity.values().iterator();

        while(var2.hasNext()) {
            BeeInfo $$1 = (BeeInfo)var2.next();
            if ($$1.hivePos != null && !this.hives.containsKey($$1.hivePos)) {
                ((List)$$0.computeIfAbsent($$1.hivePos, (p_113140_) -> {
                    return Lists.newArrayList();
                })).add($$1.getName());
            }
        }

        return $$0;
    }

    private void updateLastLookedAtUuid() {
        DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_113059_) -> {
            this.lastLookedAtUuid = p_113059_.getUUID();
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static class HiveInfo {
        public final BlockPos pos;
        public final String hiveType;
        public final int occupantCount;
        public final int honeyLevel;
        public final boolean sedated;
        public final long lastSeen;

        public HiveInfo(BlockPos p_113187_, String p_113188_, int p_113189_, int p_113190_, boolean p_113191_, long p_113192_) {
            this.pos = p_113187_;
            this.hiveType = p_113188_;
            this.occupantCount = p_113189_;
            this.honeyLevel = p_113190_;
            this.sedated = p_113191_;
            this.lastSeen = p_113192_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class BeeInfo {
        public final UUID uuid;
        public final int id;
        public final Position pos;
        @Nullable
        public final Path path;
        @Nullable
        public final BlockPos hivePos;
        @Nullable
        public final BlockPos flowerPos;
        public final int travelTicks;
        public final List<String> goals = Lists.newArrayList();
        public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

        public BeeInfo(UUID p_113167_, int p_113168_, Position p_113169_, @Nullable Path p_113170_, @Nullable BlockPos p_113171_, @Nullable BlockPos p_113172_, int p_113173_) {
            this.uuid = p_113167_;
            this.id = p_113168_;
            this.pos = p_113169_;
            this.path = p_113170_;
            this.hivePos = p_113171_;
            this.flowerPos = p_113172_;
            this.travelTicks = p_113173_;
        }

        public boolean hasHive(BlockPos p_113176_) {
            return this.hivePos != null && this.hivePos.equals(p_113176_);
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getName() {
            return DebugEntityNameGenerator.getEntityName(this.uuid);
        }

        public String toString() {
            return this.getName();
        }

        public boolean hasFlower() {
            return this.flowerPos != null;
        }
    }
}
