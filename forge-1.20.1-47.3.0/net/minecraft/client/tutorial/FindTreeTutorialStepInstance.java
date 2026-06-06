//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.tutorial;

import java.util.Iterator;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.components.toasts.TutorialToast.Icons;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FindTreeTutorialStepInstance implements TutorialStepInstance {
    private static final int HINT_DELAY = 6000;
    private static final Component TITLE = Component.translatable("tutorial.find_tree.title");
    private static final Component DESCRIPTION = Component.translatable("tutorial.find_tree.description");
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;

    public FindTreeTutorialStepInstance(Tutorial p_120496_) {
        this.tutorial = p_120496_;
    }

    public void tick() {
        ++this.timeWaiting;
        if (!this.tutorial.isSurvival()) {
            this.tutorial.setStep(TutorialSteps.NONE);
        } else {
            if (this.timeWaiting == 1) {
                LocalPlayer $$0 = this.tutorial.getMinecraft().player;
                if ($$0 != null && (hasCollectedTreeItems($$0) || hasPunchedTreesPreviously($$0))) {
                    this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                    return;
                }
            }

            if (this.timeWaiting >= 6000 && this.toast == null) {
                this.toast = new TutorialToast(Icons.TREE, TITLE, DESCRIPTION, false);
                this.tutorial.getMinecraft().getToasts().addToast(this.toast);
            }

        }
    }

    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }

    }

    public void onLookAt(ClientLevel p_120501_, HitResult p_120502_) {
        if (p_120502_.getType() == Type.BLOCK) {
            BlockState $$2 = p_120501_.getBlockState(((BlockHitResult)p_120502_).getBlockPos());
            if ($$2.is(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)) {
                this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
            }
        }

    }

    public void onGetItem(ItemStack p_120499_) {
        if (p_120499_.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL)) {
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
        }

    }

    private static boolean hasCollectedTreeItems(LocalPlayer p_235272_) {
        return p_235272_.getInventory().hasAnyMatching((p_235270_) -> {
            return p_235270_.is(ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
        });
    }

    public static boolean hasPunchedTreesPreviously(LocalPlayer p_120504_) {
        Iterator var1 = BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.COMPLETES_FIND_TREE_TUTORIAL).iterator();

        Block $$2;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            Holder<Block> $$1 = (Holder)var1.next();
            $$2 = (Block)$$1.value();
        } while(p_120504_.getStats().getValue(Stats.BLOCK_MINED.get($$2)) <= 0);

        return true;
    }
}
