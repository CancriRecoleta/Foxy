//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources.metadata.animation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerMetaDataSection {
    public static final VillagerMetadataSectionSerializer SERIALIZER = new VillagerMetadataSectionSerializer();
    public static final String SECTION_NAME = "villager";
    private final Hat hat;

    public VillagerMetaDataSection(Hat p_119069_) {
        this.hat = p_119069_;
    }

    public Hat getHat() {
        return this.hat;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Hat {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        private static final Map<String, Hat> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Hat::getName, (p_119084_) -> {
            return p_119084_;
        }));
        private final String name;

        private Hat(String p_119081_) {
            this.name = p_119081_;
        }

        public String getName() {
            return this.name;
        }

        public static Hat getByName(String p_119086_) {
            return (Hat)BY_NAME.getOrDefault(p_119086_, NONE);
        }
    }
}
