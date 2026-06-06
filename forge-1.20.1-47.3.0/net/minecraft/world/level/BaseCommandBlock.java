//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Component DEFAULT_NAME = Component.literal("@");
    private long lastExecution = -1L;
    private boolean updateLastExecution = true;
    private int successCount;
    private boolean trackOutput = true;
    @Nullable
    private Component lastOutput;
    private String command = "";
    private Component name;

    public BaseCommandBlock() {
        this.name = DEFAULT_NAME;
    }

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int p_45411_) {
        this.successCount = p_45411_;
    }

    public Component getLastOutput() {
        return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
    }

    public CompoundTag save(CompoundTag p_45422_) {
        p_45422_.putString("Command", this.command);
        p_45422_.putInt("SuccessCount", this.successCount);
        p_45422_.putString("CustomName", Serializer.toJson(this.name));
        p_45422_.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            p_45422_.putString("LastOutput", Serializer.toJson(this.lastOutput));
        }

        p_45422_.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            p_45422_.putLong("LastExecution", this.lastExecution);
        }

        return p_45422_;
    }

    public void load(CompoundTag p_45432_) {
        this.command = p_45432_.getString("Command");
        this.successCount = p_45432_.getInt("SuccessCount");
        if (p_45432_.contains("CustomName", 8)) {
            this.setName(Serializer.fromJson(p_45432_.getString("CustomName")));
        }

        if (p_45432_.contains("TrackOutput", 1)) {
            this.trackOutput = p_45432_.getBoolean("TrackOutput");
        }

        if (p_45432_.contains("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = Serializer.fromJson(p_45432_.getString("LastOutput"));
            } catch (Throwable var3) {
                Throwable $$1 = var3;
                this.lastOutput = Component.literal($$1.getMessage());
            }
        } else {
            this.lastOutput = null;
        }

        if (p_45432_.contains("UpdateLastExecution")) {
            this.updateLastExecution = p_45432_.getBoolean("UpdateLastExecution");
        }

        if (this.updateLastExecution && p_45432_.contains("LastExecution")) {
            this.lastExecution = p_45432_.getLong("LastExecution");
        } else {
            this.lastExecution = -1L;
        }

    }

    public void setCommand(String p_45420_) {
        this.command = p_45420_;
        this.successCount = 0;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean performCommand(Level p_45415_) {
        if (!p_45415_.isClientSide && p_45415_.getGameTime() != this.lastExecution) {
            if ("Searge".equalsIgnoreCase(this.command)) {
                this.lastOutput = Component.literal("#itzlipofutzli");
                this.successCount = 1;
                return true;
            } else {
                this.successCount = 0;
                MinecraftServer $$1 = this.getLevel().getServer();
                if ($$1.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
                    try {
                        this.lastOutput = null;
                        CommandSourceStack $$2 = this.createCommandSourceStack().withCallback((p_45417_, p_45418_, p_45419_) -> {
                            if (p_45418_) {
                                ++this.successCount;
                            }

                        });
                        $$1.getCommands().performPrefixedCommand($$2, this.command);
                    } catch (Throwable var6) {
                        Throwable $$3 = var6;
                        CrashReport $$4 = CrashReport.forThrowable($$3, "Executing command block");
                        CrashReportCategory $$5 = $$4.addCategory("Command to be executed");
                        $$5.setDetail("Command", this::getCommand);
                        $$5.setDetail("Name", () -> {
                            return this.getName().getString();
                        });
                        throw new ReportedException($$4);
                    }
                }

                if (this.updateLastExecution) {
                    this.lastExecution = p_45415_.getGameTime();
                } else {
                    this.lastExecution = -1L;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public Component getName() {
        return this.name;
    }

    public void setName(@Nullable Component p_45424_) {
        if (p_45424_ != null) {
            this.name = p_45424_;
        } else {
            this.name = DEFAULT_NAME;
        }

    }

    public void sendSystemMessage(Component p_220330_) {
        if (this.trackOutput) {
            SimpleDateFormat var10001 = TIME_FORMAT;
            Date var10002 = new Date();
            this.lastOutput = Component.literal("[" + var10001.format(var10002) + "] ").append(p_220330_);
            this.onUpdated();
        }

    }

    public abstract ServerLevel getLevel();

    public abstract void onUpdated();

    public void setLastOutput(@Nullable Component p_45434_) {
        this.lastOutput = p_45434_;
    }

    public void setTrackOutput(boolean p_45429_) {
        this.trackOutput = p_45429_;
    }

    public boolean isTrackOutput() {
        return this.trackOutput;
    }

    public InteractionResult usedBy(Player p_45413_) {
        if (!p_45413_.canUseGameMasterBlocks()) {
            return InteractionResult.PASS;
        } else {
            if (p_45413_.getCommandSenderWorld().isClientSide) {
                p_45413_.openMinecartCommandBlock(this);
            }

            return InteractionResult.sidedSuccess(p_45413_.level().isClientSide);
        }
    }

    public abstract Vec3 getPosition();

    public abstract CommandSourceStack createCommandSourceStack();

    public boolean acceptsSuccess() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
    }

    public boolean acceptsFailure() {
        return this.trackOutput;
    }

    public boolean shouldInformAdmins() {
        return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
    }

    public abstract boolean isValid();
}
