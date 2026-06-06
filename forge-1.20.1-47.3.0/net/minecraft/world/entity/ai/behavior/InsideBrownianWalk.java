//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity.ai.behavior;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class InsideBrownianWalk {
    public InsideBrownianWalk() {
    }

    public static BehaviorControl<PathfinderMob> create(float p_259775_) {
        return BehaviorBuilder.create((p_258399_) -> {
            return p_258399_.group(p_258399_.absent(MemoryModuleType.WALK_TARGET)).apply(p_258399_, (p_258397_) -> {
                return (p_258393_, p_258394_, p_258395_) -> {
                    if (p_258393_.canSeeSky(p_258394_.blockPosition())) {
                        return false;
                    } else {
                        BlockPos $$5 = p_258394_.blockPosition();
                        List<BlockPos> $$6 = (List)BlockPos.betweenClosedStream($$5.offset(-1, -1, -1), $$5.offset(1, 1, 1)).map(BlockPos::immutable).collect(Collectors.toList());
                        Collections.shuffle($$6);
                        $$6.stream().filter((p_23230_) -> {
                            return !p_258393_.canSeeSky(p_23230_);
                        }).filter((p_23237_) -> {
                            return p_258393_.loadedAndEntityCanStandOn(p_23237_, p_258394_);
                        }).filter((p_23227_) -> {
                            return p_258393_.noCollision(p_258394_);
                        }).findFirst().ifPresent((p_258402_) -> {
                            p_258397_.set(new WalkTarget(p_258402_, p_259775_, 0));
                        });
                        return true;
                    }
                };
            });
        });
    }
}
