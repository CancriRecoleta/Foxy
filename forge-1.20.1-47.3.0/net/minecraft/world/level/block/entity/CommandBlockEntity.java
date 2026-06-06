//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.block.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity extends BlockEntity {
    private boolean powered;
    private boolean auto;
    private boolean conditionMet;
    private final BaseCommandBlock commandBlock = new BaseCommandBlock() {
        public void setCommand(String p_59157_) {
            super.setCommand(p_59157_);
            CommandBlockEntity.this.setChanged();
        }

        public ServerLevel getLevel() {
            return (ServerLevel)CommandBlockEntity.this.level;
        }

        public void onUpdated() {
            BlockState $$0 = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
            this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, $$0, $$0, 3);
        }

        public Vec3 getPosition() {
            return Vec3.atCenterOf(CommandBlockEntity.this.worldPosition);
        }

        public CommandSourceStack createCommandSourceStack() {
            Direction $$0 = (Direction)CommandBlockEntity.this.getBlockState().getValue(CommandBlock.FACING);
            return new CommandSourceStack(this, Vec3.atCenterOf(CommandBlockEntity.this.worldPosition), new Vec2(0.0F, $$0.toYRot()), this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity)null);
        }

        public boolean isValid() {
            return !CommandBlockEntity.this.isRemoved();
        }
    };

    public CommandBlockEntity(BlockPos p_155380_, BlockState p_155381_) {
        super(BlockEntityType.COMMAND_BLOCK, p_155380_, p_155381_);
    }

    protected void saveAdditional(CompoundTag p_187491_) {
        super.saveAdditional(p_187491_);
        this.commandBlock.save(p_187491_);
        p_187491_.putBoolean("powered", this.isPowered());
        p_187491_.putBoolean("conditionMet", this.wasConditionMet());
        p_187491_.putBoolean("auto", this.isAutomatic());
    }

    public void load(CompoundTag p_155383_) {
        super.load(p_155383_);
        this.commandBlock.load(p_155383_);
        this.powered = p_155383_.getBoolean("powered");
        this.conditionMet = p_155383_.getBoolean("conditionMet");
        this.setAutomatic(p_155383_.getBoolean("auto"));
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public BaseCommandBlock getCommandBlock() {
        return this.commandBlock;
    }

    public void setPowered(boolean p_59136_) {
        this.powered = p_59136_;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAutomatic() {
        return this.auto;
    }

    public void setAutomatic(boolean p_59138_) {
        boolean $$1 = this.auto;
        this.auto = p_59138_;
        if (!$$1 && p_59138_ && !this.powered && this.level != null && this.getMode() != net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.SEQUENCE) {
            this.scheduleTick();
        }

    }

    public void onModeSwitch() {
        Mode $$0 = this.getMode();
        if ($$0 == net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.AUTO && (this.powered || this.auto) && this.level != null) {
            this.scheduleTick();
        }

    }

    private void scheduleTick() {
        Block $$0 = this.getBlockState().getBlock();
        if ($$0 instanceof CommandBlock) {
            this.markConditionMet();
            this.level.scheduleTick(this.worldPosition, $$0, 1);
        }

    }

    public boolean wasConditionMet() {
        return this.conditionMet;
    }

    public boolean markConditionMet() {
        this.conditionMet = true;
        if (this.isConditional()) {
            BlockPos $$0 = this.worldPosition.relative(((Direction)this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING)).getOpposite());
            if (this.level.getBlockState($$0).getBlock() instanceof CommandBlock) {
                BlockEntity $$1 = this.level.getBlockEntity($$0);
                this.conditionMet = $$1 instanceof CommandBlockEntity && ((CommandBlockEntity)$$1).getCommandBlock().getSuccessCount() > 0;
            } else {
                this.conditionMet = false;
            }
        }

        return this.conditionMet;
    }

    public Mode getMode() {
        BlockState $$0 = this.getBlockState();
        if ($$0.is(Blocks.COMMAND_BLOCK)) {
            return net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.REDSTONE;
        } else if ($$0.is(Blocks.REPEATING_COMMAND_BLOCK)) {
            return net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.AUTO;
        } else {
            return $$0.is(Blocks.CHAIN_COMMAND_BLOCK) ? net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.SEQUENCE : net.minecraft.world.level.block.entity.CommandBlockEntity.Mode.REDSTONE;
        }
    }

    public boolean isConditional() {
        BlockState $$0 = this.level.getBlockState(this.getBlockPos());
        return $$0.getBlock() instanceof CommandBlock ? (Boolean)$$0.getValue(CommandBlock.CONDITIONAL) : false;
    }

    public static enum Mode {
        SEQUENCE,
        AUTO,
        REDSTONE;

        private Mode() {
        }
    }
}
