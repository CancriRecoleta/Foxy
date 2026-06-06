//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum TutorialSteps {
    MOVEMENT("movement", MovementTutorialStepInstance::new),
    FIND_TREE("find_tree", FindTreeTutorialStepInstance::new),
    PUNCH_TREE("punch_tree", PunchTreeTutorialStepInstance::new),
    OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStep::new),
    CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStep::new),
    NONE("none", CompletedTutorialStepInstance::new);

    private final String name;
    private final Function<Tutorial, ? extends TutorialStepInstance> constructor;

    private TutorialSteps(String p_120637_, Function p_120638_) {
        this.name = p_120637_;
        this.constructor = p_120638_;
    }

    public TutorialStepInstance create(Tutorial p_120641_) {
        return (TutorialStepInstance)this.constructor.apply(p_120641_);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialSteps getByName(String p_120643_) {
        TutorialSteps[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TutorialSteps $$1 = var1[var3];
            if ($$1.name.equals(p_120643_)) {
                return $$1;
            }
        }

        return NONE;
    }
}
