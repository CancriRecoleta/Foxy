//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = (p_278916_, p_278917_) -> {
        LootDataManager $$2 = ((CommandSourceStack)p_278916_.getSource()).getServer().getLootData();
        return SharedSuggestionProvider.suggestResource((Iterable)$$2.getKeys(LootDataType.TABLE), p_278917_);
    };
    private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType((p_137999_) -> {
        return Component.translatable("commands.drop.no_held_items", p_137999_);
    });
    private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType((p_137977_) -> {
        return Component.translatable("commands.drop.no_loot_table", p_137977_);
    });

    public LootCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> p_214516_, CommandBuildContext p_214517_) {
        p_214516_.register((LiteralArgumentBuilder)addTargets((LiteralArgumentBuilder)Commands.literal("loot").requires((p_137937_) -> {
            return p_137937_.hasPermission(2);
        }), (p_214520_, p_214521_) -> {
            return p_214520_.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_180421_) -> {
                return dropFishingLoot(p_180421_, ResourceLocationArgument.getId(p_180421_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_180421_, "pos"), ItemStack.EMPTY, p_214521_);
            })).then(Commands.argument("tool", ItemArgument.item(p_214517_)).executes((p_180418_) -> {
                return dropFishingLoot(p_180418_, ResourceLocationArgument.getId(p_180418_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_180418_, "pos"), ItemArgument.getItem(p_180418_, "tool").createItemStack(1, false), p_214521_);
            }))).then(Commands.literal("mainhand").executes((p_180415_) -> {
                return dropFishingLoot(p_180415_, ResourceLocationArgument.getId(p_180415_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_180415_, "pos"), getSourceHandItem((CommandSourceStack)p_180415_.getSource(), EquipmentSlot.MAINHAND), p_214521_);
            }))).then(Commands.literal("offhand").executes((p_180412_) -> {
                return dropFishingLoot(p_180412_, ResourceLocationArgument.getId(p_180412_, "loot_table"), BlockPosArgument.getLoadedBlockPos(p_180412_, "pos"), getSourceHandItem((CommandSourceStack)p_180412_.getSource(), EquipmentSlot.OFFHAND), p_214521_);
            }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes((p_180409_) -> {
                return dropChestLoot(p_180409_, ResourceLocationArgument.getId(p_180409_, "loot_table"), p_214521_);
            }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((p_180406_) -> {
                return dropKillLoot(p_180406_, EntityArgument.getEntity(p_180406_, "target"), p_214521_);
            }))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_180403_) -> {
                return dropBlockLoot(p_180403_, BlockPosArgument.getLoadedBlockPos(p_180403_, "pos"), ItemStack.EMPTY, p_214521_);
            })).then(Commands.argument("tool", ItemArgument.item(p_214517_)).executes((p_180400_) -> {
                return dropBlockLoot(p_180400_, BlockPosArgument.getLoadedBlockPos(p_180400_, "pos"), ItemArgument.getItem(p_180400_, "tool").createItemStack(1, false), p_214521_);
            }))).then(Commands.literal("mainhand").executes((p_180397_) -> {
                return dropBlockLoot(p_180397_, BlockPosArgument.getLoadedBlockPos(p_180397_, "pos"), getSourceHandItem((CommandSourceStack)p_180397_.getSource(), EquipmentSlot.MAINHAND), p_214521_);
            }))).then(Commands.literal("offhand").executes((p_180394_) -> {
                return dropBlockLoot(p_180394_, BlockPosArgument.getLoadedBlockPos(p_180394_, "pos"), getSourceHandItem((CommandSourceStack)p_180394_.getSource(), EquipmentSlot.OFFHAND), p_214521_);
            }))));
        }));
    }

    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addTargets(T p_137903_, TailProvider p_137904_) {
        return p_137903_.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(p_137904_.construct(Commands.argument("slot", SlotArgument.slot()), (p_138032_, p_138033_, p_138034_) -> {
            return entityReplace(EntityArgument.getEntities(p_138032_, "entities"), SlotArgument.getSlot(p_138032_, "slot"), p_138033_.size(), p_138033_, p_138034_);
        }).then(p_137904_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_138025_, p_138026_, p_138027_) -> {
            return entityReplace(EntityArgument.getEntities(p_138025_, "entities"), SlotArgument.getSlot(p_138025_, "slot"), IntegerArgumentType.getInteger(p_138025_, "count"), p_138026_, p_138027_);
        })))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(p_137904_.construct(Commands.argument("slot", SlotArgument.slot()), (p_138018_, p_138019_, p_138020_) -> {
            return blockReplace((CommandSourceStack)p_138018_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138018_, "targetPos"), SlotArgument.getSlot(p_138018_, "slot"), p_138019_.size(), p_138019_, p_138020_);
        }).then(p_137904_.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (p_138011_, p_138012_, p_138013_) -> {
            return blockReplace((CommandSourceStack)p_138011_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138011_, "targetPos"), IntegerArgumentType.getInteger(p_138011_, "slot"), IntegerArgumentType.getInteger(p_138011_, "count"), p_138012_, p_138013_);
        })))))).then(Commands.literal("insert").then(p_137904_.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (p_138004_, p_138005_, p_138006_) -> {
            return blockDistribute((CommandSourceStack)p_138004_.getSource(), BlockPosArgument.getLoadedBlockPos(p_138004_, "targetPos"), p_138005_, p_138006_);
        }))).then(Commands.literal("give").then(p_137904_.construct(Commands.argument("players", EntityArgument.players()), (p_137992_, p_137993_, p_137994_) -> {
            return playerGive(EntityArgument.getPlayers(p_137992_, "players"), p_137993_, p_137994_);
        }))).then(Commands.literal("spawn").then(p_137904_.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (p_137918_, p_137919_, p_137920_) -> {
            return dropInWorld((CommandSourceStack)p_137918_.getSource(), Vec3Argument.getVec3(p_137918_, "targetPos"), p_137919_, p_137920_);
        })));
    }

    private static Container getContainer(CommandSourceStack p_137951_, BlockPos p_137952_) throws CommandSyntaxException {
        BlockEntity $$2 = p_137951_.getLevel().getBlockEntity(p_137952_);
        if (!($$2 instanceof Container)) {
            throw ItemCommands.ERROR_TARGET_NOT_A_CONTAINER.create(p_137952_.getX(), p_137952_.getY(), p_137952_.getZ());
        } else {
            return (Container)$$2;
        }
    }

    private static int blockDistribute(CommandSourceStack p_137961_, BlockPos p_137962_, List<ItemStack> p_137963_, Callback p_137964_) throws CommandSyntaxException {
        Container $$4 = getContainer(p_137961_, p_137962_);
        List<ItemStack> $$5 = Lists.newArrayListWithCapacity(p_137963_.size());
        Iterator var6 = p_137963_.iterator();

        while(var6.hasNext()) {
            ItemStack $$6 = (ItemStack)var6.next();
            if (distributeToContainer($$4, $$6.copy())) {
                $$4.setChanged();
                $$5.add($$6);
            }
        }

        p_137964_.accept($$5);
        return $$5.size();
    }

    private static boolean distributeToContainer(Container p_137886_, ItemStack p_137887_) {
        boolean $$2 = false;

        for(int $$3 = 0; $$3 < p_137886_.getContainerSize() && !p_137887_.isEmpty(); ++$$3) {
            ItemStack $$4 = p_137886_.getItem($$3);
            if (p_137886_.canPlaceItem($$3, p_137887_)) {
                if ($$4.isEmpty()) {
                    p_137886_.setItem($$3, p_137887_);
                    $$2 = true;
                    break;
                }

                if (canMergeItems($$4, p_137887_)) {
                    int $$5 = p_137887_.getMaxStackSize() - $$4.getCount();
                    int $$6 = Math.min(p_137887_.getCount(), $$5);
                    p_137887_.shrink($$6);
                    $$4.grow($$6);
                    $$2 = true;
                }
            }
        }

        return $$2;
    }

    private static int blockReplace(CommandSourceStack p_137954_, BlockPos p_137955_, int p_137956_, int p_137957_, List<ItemStack> p_137958_, Callback p_137959_) throws CommandSyntaxException {
        Container $$6 = getContainer(p_137954_, p_137955_);
        int $$7 = $$6.getContainerSize();
        if (p_137956_ >= 0 && p_137956_ < $$7) {
            List<ItemStack> $$8 = Lists.newArrayListWithCapacity(p_137958_.size());

            for(int $$9 = 0; $$9 < p_137957_; ++$$9) {
                int $$10 = p_137956_ + $$9;
                ItemStack $$11 = $$9 < p_137958_.size() ? (ItemStack)p_137958_.get($$9) : ItemStack.EMPTY;
                if ($$6.canPlaceItem($$10, $$11)) {
                    $$6.setItem($$10, $$11);
                    $$8.add($$11);
                }
            }

            p_137959_.accept($$8);
            return $$8.size();
        } else {
            throw ItemCommands.ERROR_TARGET_INAPPLICABLE_SLOT.create(p_137956_);
        }
    }

    private static boolean canMergeItems(ItemStack p_137895_, ItemStack p_137896_) {
        return p_137895_.getCount() <= p_137895_.getMaxStackSize() && ItemStack.isSameItemSameTags(p_137895_, p_137896_);
    }

    private static int playerGive(Collection<ServerPlayer> p_137985_, List<ItemStack> p_137986_, Callback p_137987_) throws CommandSyntaxException {
        List<ItemStack> $$3 = Lists.newArrayListWithCapacity(p_137986_.size());
        Iterator var4 = p_137986_.iterator();

        while(var4.hasNext()) {
            ItemStack $$4 = (ItemStack)var4.next();
            Iterator var6 = p_137985_.iterator();

            while(var6.hasNext()) {
                ServerPlayer $$5 = (ServerPlayer)var6.next();
                if ($$5.getInventory().add($$4.copy())) {
                    $$3.add($$4);
                }
            }
        }

        p_137987_.accept($$3);
        return $$3.size();
    }

    private static void setSlots(Entity p_137889_, List<ItemStack> p_137890_, int p_137891_, int p_137892_, List<ItemStack> p_137893_) {
        for(int $$5 = 0; $$5 < p_137892_; ++$$5) {
            ItemStack $$6 = $$5 < p_137890_.size() ? (ItemStack)p_137890_.get($$5) : ItemStack.EMPTY;
            SlotAccess $$7 = p_137889_.getSlot(p_137891_ + $$5);
            if ($$7 != SlotAccess.NULL && $$7.set($$6.copy())) {
                p_137893_.add($$6);
            }
        }

    }

    private static int entityReplace(Collection<? extends Entity> p_137979_, int p_137980_, int p_137981_, List<ItemStack> p_137982_, Callback p_137983_) throws CommandSyntaxException {
        List<ItemStack> $$5 = Lists.newArrayListWithCapacity(p_137982_.size());
        Iterator var6 = p_137979_.iterator();

        while(var6.hasNext()) {
            Entity $$6 = (Entity)var6.next();
            if ($$6 instanceof ServerPlayer $$7) {
                setSlots($$6, p_137982_, p_137980_, p_137981_, $$5);
                $$7.containerMenu.broadcastChanges();
            } else {
                setSlots($$6, p_137982_, p_137980_, p_137981_, $$5);
            }
        }

        p_137983_.accept($$5);
        return $$5.size();
    }

    private static int dropInWorld(CommandSourceStack p_137946_, Vec3 p_137947_, List<ItemStack> p_137948_, Callback p_137949_) throws CommandSyntaxException {
        ServerLevel $$4 = p_137946_.getLevel();
        p_137948_.forEach((p_137884_) -> {
            ItemEntity $$3 = new ItemEntity($$4, p_137947_.x, p_137947_.y, p_137947_.z, p_137884_.copy());
            $$3.setDefaultPickUpDelay();
            $$4.addFreshEntity($$3);
        });
        p_137949_.accept(p_137948_);
        return p_137948_.size();
    }

    private static void callback(CommandSourceStack p_137966_, List<ItemStack> p_137967_) {
        if (p_137967_.size() == 1) {
            ItemStack $$2 = (ItemStack)p_137967_.get(0);
            p_137966_.sendSuccess(() -> {
                return Component.translatable("commands.drop.success.single", $$2.getCount(), $$2.getDisplayName());
            }, false);
        } else {
            p_137966_.sendSuccess(() -> {
                return Component.translatable("commands.drop.success.multiple", p_137967_.size());
            }, false);
        }

    }

    private static void callback(CommandSourceStack p_137969_, List<ItemStack> p_137970_, ResourceLocation p_137971_) {
        if (p_137970_.size() == 1) {
            ItemStack $$3 = (ItemStack)p_137970_.get(0);
            p_137969_.sendSuccess(() -> {
                return Component.translatable("commands.drop.success.single_with_table", $$3.getCount(), $$3.getDisplayName(), p_137971_);
            }, false);
        } else {
            p_137969_.sendSuccess(() -> {
                return Component.translatable("commands.drop.success.multiple_with_table", p_137970_.size(), p_137971_);
            }, false);
        }

    }

    private static ItemStack getSourceHandItem(CommandSourceStack p_137939_, EquipmentSlot p_137940_) throws CommandSyntaxException {
        Entity $$2 = p_137939_.getEntityOrException();
        if ($$2 instanceof LivingEntity) {
            return ((LivingEntity)$$2).getItemBySlot(p_137940_);
        } else {
            throw ERROR_NO_HELD_ITEMS.create($$2.getDisplayName());
        }
    }

    private static int dropBlockLoot(CommandContext<CommandSourceStack> p_137913_, BlockPos p_137914_, ItemStack p_137915_, DropConsumer p_137916_) throws CommandSyntaxException {
        CommandSourceStack $$4 = (CommandSourceStack)p_137913_.getSource();
        ServerLevel $$5 = $$4.getLevel();
        BlockState $$6 = $$5.getBlockState(p_137914_);
        BlockEntity $$7 = $$5.getBlockEntity(p_137914_);
        LootParams.Builder $$8 = (new LootParams.Builder($$5)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_137914_)).withParameter(LootContextParams.BLOCK_STATE, $$6).withOptionalParameter(LootContextParams.BLOCK_ENTITY, $$7).withOptionalParameter(LootContextParams.THIS_ENTITY, $$4.getEntity()).withParameter(LootContextParams.TOOL, p_137915_);
        List<ItemStack> $$9 = $$6.getDrops($$8);
        return p_137916_.accept(p_137913_, $$9, (p_278915_) -> {
            callback($$4, p_278915_, $$6.getBlock().getLootTable());
        });
    }

    private static int dropKillLoot(CommandContext<CommandSourceStack> p_137906_, Entity p_137907_, DropConsumer p_137908_) throws CommandSyntaxException {
        if (!(p_137907_ instanceof LivingEntity)) {
            throw ERROR_NO_LOOT_TABLE.create(p_137907_.getDisplayName());
        } else {
            ResourceLocation $$3 = ((LivingEntity)p_137907_).getLootTable();
            CommandSourceStack $$4 = (CommandSourceStack)p_137906_.getSource();
            LootParams.Builder $$5 = new LootParams.Builder($$4.getLevel());
            Entity $$6 = $$4.getEntity();
            if ($$6 instanceof Player) {
                Player $$7 = (Player)$$6;
                $$5.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, $$7);
            }

            $$5.withParameter(LootContextParams.DAMAGE_SOURCE, p_137907_.damageSources().magic());
            $$5.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, $$6);
            $$5.withOptionalParameter(LootContextParams.KILLER_ENTITY, $$6);
            $$5.withParameter(LootContextParams.THIS_ENTITY, p_137907_);
            $$5.withParameter(LootContextParams.ORIGIN, $$4.getPosition());
            LootParams $$8 = $$5.create(LootContextParamSets.ENTITY);
            LootTable $$9 = $$4.getServer().getLootData().getLootTable($$3);
            List<ItemStack> $$10 = $$9.getRandomItems($$8);
            return p_137908_.accept(p_137906_, $$10, (p_137975_) -> {
                callback($$4, p_137975_, $$3);
            });
        }
    }

    private static int dropChestLoot(CommandContext<CommandSourceStack> p_137933_, ResourceLocation p_137934_, DropConsumer p_137935_) throws CommandSyntaxException {
        CommandSourceStack $$3 = (CommandSourceStack)p_137933_.getSource();
        LootParams $$4 = (new LootParams.Builder($$3.getLevel())).withOptionalParameter(LootContextParams.THIS_ENTITY, $$3.getEntity()).withParameter(LootContextParams.ORIGIN, $$3.getPosition()).create(LootContextParamSets.CHEST);
        return drop(p_137933_, p_137934_, $$4, p_137935_);
    }

    private static int dropFishingLoot(CommandContext<CommandSourceStack> p_137927_, ResourceLocation p_137928_, BlockPos p_137929_, ItemStack p_137930_, DropConsumer p_137931_) throws CommandSyntaxException {
        CommandSourceStack $$5 = (CommandSourceStack)p_137927_.getSource();
        LootParams $$6 = (new LootParams.Builder($$5.getLevel())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_137929_)).withParameter(LootContextParams.TOOL, p_137930_).withOptionalParameter(LootContextParams.THIS_ENTITY, $$5.getEntity()).create(LootContextParamSets.FISHING);
        return drop(p_137927_, p_137928_, $$6, p_137931_);
    }

    private static int drop(CommandContext<CommandSourceStack> p_287721_, ResourceLocation p_287610_, LootParams p_287728_, DropConsumer p_287770_) throws CommandSyntaxException {
        CommandSourceStack $$4 = (CommandSourceStack)p_287721_.getSource();
        LootTable $$5 = $$4.getServer().getLootData().getLootTable(p_287610_);
        List<ItemStack> $$6 = $$5.getRandomItems(p_287728_);
        return p_287770_.accept(p_287721_, $$6, (p_137997_) -> {
            callback($$4, p_137997_);
        });
    }

    @FunctionalInterface
    private interface TailProvider {
        ArgumentBuilder<CommandSourceStack, ?> construct(ArgumentBuilder<CommandSourceStack, ?> var1, DropConsumer var2);
    }

    @FunctionalInterface
    private interface DropConsumer {
        int accept(CommandContext<CommandSourceStack> var1, List<ItemStack> var2, Callback var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    private interface Callback {
        void accept(List<ItemStack> var1) throws CommandSyntaxException;
    }
}
