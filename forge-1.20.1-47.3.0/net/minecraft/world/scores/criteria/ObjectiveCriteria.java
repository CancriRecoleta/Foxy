//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.util.StringRepresentable;

public class ObjectiveCriteria {
    private static final Map<String, ObjectiveCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
    private static final Map<String, ObjectiveCriteria> CRITERIA_CACHE = Maps.newHashMap();
    public static final ObjectiveCriteria DUMMY = registerCustom("dummy");
    public static final ObjectiveCriteria TRIGGER = registerCustom("trigger");
    public static final ObjectiveCriteria DEATH_COUNT = registerCustom("deathCount");
    public static final ObjectiveCriteria KILL_COUNT_PLAYERS = registerCustom("playerKillCount");
    public static final ObjectiveCriteria KILL_COUNT_ALL = registerCustom("totalKillCount");
    public static final ObjectiveCriteria HEALTH;
    public static final ObjectiveCriteria FOOD;
    public static final ObjectiveCriteria AIR;
    public static final ObjectiveCriteria ARMOR;
    public static final ObjectiveCriteria EXPERIENCE;
    public static final ObjectiveCriteria LEVEL;
    public static final ObjectiveCriteria[] TEAM_KILL;
    public static final ObjectiveCriteria[] KILLED_BY_TEAM;
    private final String name;
    private final boolean readOnly;
    private final RenderType renderType;

    private static ObjectiveCriteria registerCustom(String p_166110_, boolean p_166111_, RenderType p_166112_) {
        ObjectiveCriteria $$3 = new ObjectiveCriteria(p_166110_, p_166111_, p_166112_);
        CUSTOM_CRITERIA.put(p_166110_, $$3);
        return $$3;
    }

    private static ObjectiveCriteria registerCustom(String p_166114_) {
        return registerCustom(p_166114_, false, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
    }

    protected ObjectiveCriteria(String p_83606_) {
        this(p_83606_, false, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
    }

    protected ObjectiveCriteria(String p_83608_, boolean p_83609_, RenderType p_83610_) {
        this.name = p_83608_;
        this.readOnly = p_83609_;
        this.renderType = p_83610_;
        CRITERIA_CACHE.put(p_83608_, this);
    }

    public static Set<String> getCustomCriteriaNames() {
        return ImmutableSet.copyOf(CUSTOM_CRITERIA.keySet());
    }

    public static Optional<ObjectiveCriteria> byName(String p_83615_) {
        ObjectiveCriteria $$1 = (ObjectiveCriteria)CRITERIA_CACHE.get(p_83615_);
        if ($$1 != null) {
            return Optional.of($$1);
        } else {
            int $$2 = p_83615_.indexOf(58);
            return $$2 < 0 ? Optional.empty() : BuiltInRegistries.STAT_TYPE.getOptional(ResourceLocation.of(p_83615_.substring(0, $$2), '.')).flatMap((p_83619_) -> {
                return getStat(p_83619_, ResourceLocation.of(p_83615_.substring($$2 + 1), '.'));
            });
        }
    }

    private static <T> Optional<ObjectiveCriteria> getStat(StatType<T> p_83612_, ResourceLocation p_83613_) {
        Optional var10000 = p_83612_.getRegistry().getOptional(p_83613_);
        Objects.requireNonNull(p_83612_);
        return var10000.map(p_83612_::get);
    }

    public String getName() {
        return this.name;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public RenderType getDefaultRenderType() {
        return this.renderType;
    }

    static {
        HEALTH = registerCustom("health", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.HEARTS);
        FOOD = registerCustom("food", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
        AIR = registerCustom("air", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
        ARMOR = registerCustom("armor", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
        EXPERIENCE = registerCustom("xp", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
        LEVEL = registerCustom("level", true, net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER);
        TEAM_KILL = new ObjectiveCriteria[]{registerCustom("teamkill." + ChatFormatting.BLACK.getName()), registerCustom("teamkill." + ChatFormatting.DARK_BLUE.getName()), registerCustom("teamkill." + ChatFormatting.DARK_GREEN.getName()), registerCustom("teamkill." + ChatFormatting.DARK_AQUA.getName()), registerCustom("teamkill." + ChatFormatting.DARK_RED.getName()), registerCustom("teamkill." + ChatFormatting.DARK_PURPLE.getName()), registerCustom("teamkill." + ChatFormatting.GOLD.getName()), registerCustom("teamkill." + ChatFormatting.GRAY.getName()), registerCustom("teamkill." + ChatFormatting.DARK_GRAY.getName()), registerCustom("teamkill." + ChatFormatting.BLUE.getName()), registerCustom("teamkill." + ChatFormatting.GREEN.getName()), registerCustom("teamkill." + ChatFormatting.AQUA.getName()), registerCustom("teamkill." + ChatFormatting.RED.getName()), registerCustom("teamkill." + ChatFormatting.LIGHT_PURPLE.getName()), registerCustom("teamkill." + ChatFormatting.YELLOW.getName()), registerCustom("teamkill." + ChatFormatting.WHITE.getName())};
        KILLED_BY_TEAM = new ObjectiveCriteria[]{registerCustom("killedByTeam." + ChatFormatting.BLACK.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_BLUE.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_GREEN.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_AQUA.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_RED.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_PURPLE.getName()), registerCustom("killedByTeam." + ChatFormatting.GOLD.getName()), registerCustom("killedByTeam." + ChatFormatting.GRAY.getName()), registerCustom("killedByTeam." + ChatFormatting.DARK_GRAY.getName()), registerCustom("killedByTeam." + ChatFormatting.BLUE.getName()), registerCustom("killedByTeam." + ChatFormatting.GREEN.getName()), registerCustom("killedByTeam." + ChatFormatting.AQUA.getName()), registerCustom("killedByTeam." + ChatFormatting.RED.getName()), registerCustom("killedByTeam." + ChatFormatting.LIGHT_PURPLE.getName()), registerCustom("killedByTeam." + ChatFormatting.YELLOW.getName()), registerCustom("killedByTeam." + ChatFormatting.WHITE.getName())};
    }

    public static enum RenderType implements StringRepresentable {
        INTEGER("integer"),
        HEARTS("hearts");

        private final String id;
        public static final StringRepresentable.EnumCodec<RenderType> CODEC = StringRepresentable.fromEnum(RenderType::values);

        private RenderType(String p_83632_) {
            this.id = p_83632_;
        }

        public String getId() {
            return this.id;
        }

        public String getSerializedName() {
            return this.id;
        }

        public static RenderType byId(String p_83635_) {
            return (RenderType)CODEC.byName(p_83635_, INTEGER);
        }
    }
}
