//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.server.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.timings.ForgeTimings;
import net.minecraftforge.server.timings.TimeTracker;

class TrackCommand {
    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("#####0.00");

    TrackCommand() {
    }

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("track").then(net.minecraftforge.server.command.TrackCommand.StartTrackingCommand.register())).then(net.minecraftforge.server.command.TrackCommand.ResetTrackingCommand.register())).then(net.minecraftforge.server.command.TrackCommand.TrackResultsEntity.register())).then(net.minecraftforge.server.command.TrackCommand.TrackResultsBlockEntity.register())).then(net.minecraftforge.server.command.TrackCommand.StartTrackingCommand.register());
    }

    private static class StartTrackingCommand {
        private StartTrackingCommand() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("start").requires((cs) -> {
                return cs.hasPermission(2);
            })).then(Commands.literal("te").then(Commands.argument("duration", IntegerArgumentType.integer(1)).executes((ctx) -> {
                int duration = IntegerArgumentType.getInteger(ctx, "duration");
                TimeTracker.BLOCK_ENTITY_UPDATE.reset();
                TimeTracker.BLOCK_ENTITY_UPDATE.enable(duration);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.forge.tracking.be.enabled", duration);
                }, true);
                return 0;
            })))).then(Commands.literal("entity").then(Commands.argument("duration", IntegerArgumentType.integer(1)).executes((ctx) -> {
                int duration = IntegerArgumentType.getInteger(ctx, "duration");
                TimeTracker.ENTITY_UPDATE.reset();
                TimeTracker.ENTITY_UPDATE.enable(duration);
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.forge.tracking.entity.enabled", duration);
                }, true);
                return 0;
            })));
        }
    }

    private static class ResetTrackingCommand {
        private ResetTrackingCommand() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reset").requires((cs) -> {
                return cs.hasPermission(2);
            })).then(Commands.literal("te").executes((ctx) -> {
                TimeTracker.BLOCK_ENTITY_UPDATE.reset();
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.forge.tracking.be.reset");
                }, true);
                return 0;
            }))).then(Commands.literal("entity").executes((ctx) -> {
                TimeTracker.ENTITY_UPDATE.reset();
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> {
                    return Component.translatable("commands.forge.tracking.entity.reset");
                }, true);
                return 0;
            }));
        }
    }

    private static class TrackResultsEntity {
        private TrackResultsEntity() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("entity").executes((ctx) -> {
                return net.minecraftforge.server.command.TrackCommand.TrackResults.execute((CommandSourceStack)ctx.getSource(), TimeTracker.ENTITY_UPDATE, (data) -> {
                    Entity entity = (Entity)data.getObject().get();
                    if (entity == null) {
                        return Component.translatable("commands.forge.tracking.invalid");
                    } else {
                        BlockPos pos = entity.blockPosition();
                        double averageTimings = data.getAverageTimings();
                        String var10000 = averageTimings > 1000.0 ? TrackCommand.TIME_FORMAT.format(averageTimings / 1000.0) : TrackCommand.TIME_FORMAT.format(averageTimings);
                        String tickTime = var10000 + (averageTimings < 1000.0 ? "μs" : "ms");
                        return Component.translatable("commands.forge.tracking.timing_entry", ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()), entity.level().dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ(), tickTime);
                    }
                });
            });
        }
    }

    private static class TrackResultsBlockEntity {
        private TrackResultsBlockEntity() {
        }

        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("te").executes((ctx) -> {
                return net.minecraftforge.server.command.TrackCommand.TrackResults.execute((CommandSourceStack)ctx.getSource(), TimeTracker.BLOCK_ENTITY_UPDATE, (data) -> {
                    BlockEntity be = (BlockEntity)data.getObject().get();
                    if (be == null) {
                        return Component.translatable("commands.forge.tracking.invalid");
                    } else {
                        BlockPos pos = be.getBlockPos();
                        double averageTimings = data.getAverageTimings();
                        String var10000 = averageTimings > 1000.0 ? TrackCommand.TIME_FORMAT.format(averageTimings / 1000.0) : TrackCommand.TIME_FORMAT.format(averageTimings);
                        String tickTime = var10000 + (averageTimings < 1000.0 ? "μs" : "ms");
                        return Component.translatable("commands.forge.tracking.timing_entry", ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(be.getType()), be.getLevel().dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ(), tickTime);
                    }
                });
            });
        }
    }

    private static class TrackResults {
        private TrackResults() {
        }

        private static <T> List<ForgeTimings<T>> getSortedTimings(TimeTracker<T> tracker) {
            ArrayList<ForgeTimings<T>> list = new ArrayList();
            list.addAll(tracker.getTimingData());
            list.sort(Comparator.comparingDouble(ForgeTimings::getAverageTimings));
            Collections.reverse(list);
            return list;
        }

        private static <T> int execute(CommandSourceStack source, TimeTracker<T> tracker, Function<ForgeTimings<T>, Component> toString) throws CommandRuntimeException {
            List<ForgeTimings<T>> timingsList = getSortedTimings(tracker);
            if (timingsList.isEmpty()) {
                source.sendSuccess(() -> {
                    return Component.translatable("commands.forge.tracking.no_data");
                }, true);
            } else {
                timingsList.stream().filter((timings) -> {
                    return timings.getObject().get() != null;
                }).limit(10L).forEach((timings) -> {
                    source.sendSuccess(() -> {
                        return (Component)toString.apply(timings);
                    }, true);
                });
            }

            return 0;
        }
    }
}
