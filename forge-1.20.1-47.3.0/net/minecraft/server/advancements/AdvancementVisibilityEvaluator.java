//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.advancements;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;

public class AdvancementVisibilityEvaluator {
    private static final int VISIBILITY_DEPTH = 2;

    public AdvancementVisibilityEvaluator() {
    }

    private static VisibilityRule evaluateVisibilityRule(Advancement p_265736_, boolean p_265426_) {
        DisplayInfo displayinfo = p_265736_.getDisplay();
        if (displayinfo == null) {
            return net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.HIDE;
        } else if (p_265426_) {
            return net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.SHOW;
        } else {
            return displayinfo.isHidden() ? net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.HIDE : net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE;
        }
    }

    private static boolean evaluateVisiblityForUnfinishedNode(Stack<VisibilityRule> p_265343_) {
        for(int i = 0; i <= 2; ++i) {
            VisibilityRule advancementvisibilityevaluator$visibilityrule = (VisibilityRule)p_265343_.peek(i);
            if (advancementvisibilityevaluator$visibilityrule == net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.SHOW) {
                return true;
            }

            if (advancementvisibilityevaluator$visibilityrule == net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.HIDE) {
                return false;
            }
        }

        return false;
    }

    private static boolean evaluateVisibility(Advancement p_265202_, Stack<VisibilityRule> p_265086_, Predicate<Advancement> p_265561_, Output p_265381_) {
        boolean flag = p_265561_.test(p_265202_);
        VisibilityRule advancementvisibilityevaluator$visibilityrule = evaluateVisibilityRule(p_265202_, flag);
        boolean flag1 = flag;
        p_265086_.push(advancementvisibilityevaluator$visibilityrule);

        Advancement advancement;
        for(Iterator var7 = p_265202_.getChildren().iterator(); var7.hasNext(); flag1 |= evaluateVisibility(advancement, p_265086_, p_265561_, p_265381_)) {
            advancement = (Advancement)var7.next();
        }

        boolean flag2 = flag1 || evaluateVisiblityForUnfinishedNode(p_265086_);
        p_265086_.pop();
        p_265381_.accept(p_265202_, flag2);
        return flag1;
    }

    public static void evaluateVisibility(Advancement p_265578_, Predicate<Advancement> p_265359_, Output p_265303_) {
        Advancement advancement = p_265578_.getRoot();
        Stack<VisibilityRule> stack = new ObjectArrayList();

        for(int i = 0; i <= 2; ++i) {
            stack.push(net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
        }

        evaluateVisibility(advancement, stack, p_265359_, p_265303_);
    }

    public static boolean isVisible(Advancement advancement, Predicate<Advancement> test) {
        Stack<VisibilityRule> stack = new ObjectArrayList();

        for(int i = 0; i <= 2; ++i) {
            stack.push(net.minecraft.server.advancements.AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
        }

        return evaluateVisibility(advancement.getRoot(), stack, test, (p_265639_, p_265580_) -> {
        });
    }

    static enum VisibilityRule {
        SHOW,
        HIDE,
        NO_CHANGE;

        private VisibilityRule() {
        }
    }

    @FunctionalInterface
    public interface Output {
        void accept(Advancement var1, boolean var2);
    }
}
