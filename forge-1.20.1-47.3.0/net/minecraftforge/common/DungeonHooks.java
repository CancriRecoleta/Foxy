//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.EntityType;

public class DungeonHooks {
    private static ArrayList<DungeonMob> dungeonMobs = new ArrayList();

    public DungeonHooks() {
    }

    public static float addDungeonMob(EntityType<?> type, int rarity) {
        if (rarity <= 0) {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        } else {
            Iterator<DungeonMob> itr = dungeonMobs.iterator();

            while(itr.hasNext()) {
                DungeonMob mob = (DungeonMob)itr.next();
                if (type == mob.type) {
                    itr.remove();
                    rarity += mob.getWeight().asInt();
                    break;
                }
            }

            dungeonMobs.add(new DungeonMob(rarity, type));
            return (float)rarity;
        }
    }

    public static int removeDungeonMob(EntityType<?> name) {
        Iterator var1 = dungeonMobs.iterator();

        DungeonMob mob;
        do {
            if (!var1.hasNext()) {
                return 0;
            }

            mob = (DungeonMob)var1.next();
        } while(name != mob.type);

        dungeonMobs.remove(mob);
        return mob.getWeight().asInt();
    }

    public static EntityType<?> getRandomDungeonMob(RandomSource rand) {
        DungeonMob mob = (DungeonMob)WeightedRandom.getRandomItem(rand, dungeonMobs).orElseThrow();
        return mob.type;
    }

    static {
        addDungeonMob(EntityType.SKELETON, 100);
        addDungeonMob(EntityType.ZOMBIE, 200);
        addDungeonMob(EntityType.SPIDER, 100);
    }

    public static class DungeonMob extends WeightedEntry.IntrusiveBase {
        public final EntityType<?> type;

        public DungeonMob(int weight, EntityType<?> type) {
            super(weight);
            this.type = type;
        }

        public boolean equals(Object target) {
            return target instanceof DungeonMob && this.type.equals(((DungeonMob)target).type);
        }
    }
}
