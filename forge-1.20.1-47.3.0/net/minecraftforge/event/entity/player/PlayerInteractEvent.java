//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.event.entity.player;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class PlayerInteractEvent extends PlayerEvent {
    private final InteractionHand hand;
    private final BlockPos pos;
    private final @Nullable Direction face;
    private InteractionResult cancellationResult;

    private PlayerInteractEvent(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
        super((Player)Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
        this.cancellationResult = InteractionResult.PASS;
        this.hand = (InteractionHand)Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
        this.pos = (BlockPos)Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
        this.face = face;
    }

    public @NotNull InteractionHand getHand() {
        return this.hand;
    }

    public @NotNull ItemStack getItemStack() {
        return this.getEntity().getItemInHand(this.hand);
    }

    public @NotNull BlockPos getPos() {
        return this.pos;
    }

    public @Nullable Direction getFace() {
        return this.face;
    }

    public Level getLevel() {
        return this.getEntity().level();
    }

    public LogicalSide getSide() {
        return this.getLevel().isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }

    public InteractionResult getCancellationResult() {
        return this.cancellationResult;
    }

    public void setCancellationResult(InteractionResult result) {
        this.cancellationResult = result;
    }

    public static class LeftClickEmpty extends PlayerInteractEvent {
        public LeftClickEmpty(Player player) {
            super(player, InteractionHand.MAIN_HAND, player.blockPosition(), (Direction)null);
        }
    }

    @Cancelable
    public static class LeftClickBlock extends PlayerInteractEvent {
        private Event.Result useBlock;
        private Event.Result useItem;
        private final Action action;

        /** @deprecated */
        @Deprecated(
            since = "1.20.1",
            forRemoval = true
        )
        public LeftClickBlock(Player player, BlockPos pos, Direction face) {
            this(player, pos, face, net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.START);
        }

        @Internal
        public LeftClickBlock(Player player, BlockPos pos, Direction face, Action action) {
            super(player, InteractionHand.MAIN_HAND, pos, face);
            this.useBlock = Result.DEFAULT;
            this.useItem = Result.DEFAULT;
            this.action = action;
        }

        public Event.Result getUseBlock() {
            return this.useBlock;
        }

        public Event.Result getUseItem() {
            return this.useItem;
        }

        @NotNull
        public @NotNull Action getAction() {
            return this.action;
        }

        public void setUseBlock(Event.Result triggerBlock) {
            this.useBlock = triggerBlock;
        }

        public void setUseItem(Event.Result triggerItem) {
            this.useItem = triggerItem;
        }

        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);
            if (canceled) {
                this.useBlock = Result.DENY;
                this.useItem = Result.DENY;
            }

        }

        public static enum Action {
            START,
            STOP,
            ABORT,
            CLIENT_HOLD;

            private Action() {
            }

            public static Action convert(ServerboundPlayerActionPacket.Action action) {
                Action var10000;
                switch (action) {
                    case START_DESTROY_BLOCK -> var10000 = START;
                    case STOP_DESTROY_BLOCK -> var10000 = STOP;
                    case ABORT_DESTROY_BLOCK -> var10000 = ABORT;
                    default -> var10000 = START;
                }

                return var10000;
            }
        }
    }

    public static class RightClickEmpty extends PlayerInteractEvent {
        public RightClickEmpty(Player player, InteractionHand hand) {
            super(player, hand, player.blockPosition(), (Direction)null);
        }
    }

    @Cancelable
    public static class RightClickItem extends PlayerInteractEvent {
        public RightClickItem(Player player, InteractionHand hand) {
            super(player, hand, player.blockPosition(), (Direction)null);
        }
    }

    @Cancelable
    public static class RightClickBlock extends PlayerInteractEvent {
        private Event.Result useBlock;
        private Event.Result useItem;
        private BlockHitResult hitVec;

        public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
            super(player, hand, pos, hitVec.getDirection());
            this.useBlock = Result.DEFAULT;
            this.useItem = Result.DEFAULT;
            this.hitVec = hitVec;
        }

        public Event.Result getUseBlock() {
            return this.useBlock;
        }

        public Event.Result getUseItem() {
            return this.useItem;
        }

        public BlockHitResult getHitVec() {
            return this.hitVec;
        }

        public void setUseBlock(Event.Result triggerBlock) {
            this.useBlock = triggerBlock;
        }

        public void setUseItem(Event.Result triggerItem) {
            this.useItem = triggerItem;
        }

        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);
            if (canceled) {
                this.useBlock = Result.DENY;
                this.useItem = Result.DENY;
            }

        }
    }

    @Cancelable
    public static class EntityInteract extends PlayerInteractEvent {
        private final Entity target;

        public EntityInteract(Player player, InteractionHand hand, Entity target) {
            super(player, hand, target.blockPosition(), (Direction)null);
            this.target = target;
        }

        public Entity getTarget() {
            return this.target;
        }
    }

    @Cancelable
    public static class EntityInteractSpecific extends PlayerInteractEvent {
        private final Vec3 localPos;
        private final Entity target;

        public EntityInteractSpecific(Player player, InteractionHand hand, Entity target, Vec3 localPos) {
            super(player, hand, target.blockPosition(), (Direction)null);
            this.localPos = localPos;
            this.target = target;
        }

        public Vec3 getLocalPos() {
            return this.localPos;
        }

        public Entity getTarget() {
            return this.target;
        }
    }
}
