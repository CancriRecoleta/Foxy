//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotArgument implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType((p_111283_) -> {
        return Component.translatable("slot.unknown", p_111283_);
    });
    private static final Map<String, Integer> SLOTS = (Map)Util.make(Maps.newHashMap(), (p_111285_) -> {
        int $$6;
        for($$6 = 0; $$6 < 54; ++$$6) {
            p_111285_.put("container." + $$6, $$6);
        }

        for($$6 = 0; $$6 < 9; ++$$6) {
            p_111285_.put("hotbar." + $$6, $$6);
        }

        for($$6 = 0; $$6 < 27; ++$$6) {
            p_111285_.put("inventory." + $$6, 9 + $$6);
        }

        for($$6 = 0; $$6 < 27; ++$$6) {
            p_111285_.put("enderchest." + $$6, 200 + $$6);
        }

        for($$6 = 0; $$6 < 8; ++$$6) {
            p_111285_.put("villager." + $$6, 300 + $$6);
        }

        for($$6 = 0; $$6 < 15; ++$$6) {
            p_111285_.put("horse." + $$6, 500 + $$6);
        }

        p_111285_.put("weapon", EquipmentSlot.MAINHAND.getIndex(98));
        p_111285_.put("weapon.mainhand", EquipmentSlot.MAINHAND.getIndex(98));
        p_111285_.put("weapon.offhand", EquipmentSlot.OFFHAND.getIndex(98));
        p_111285_.put("armor.head", EquipmentSlot.HEAD.getIndex(100));
        p_111285_.put("armor.chest", EquipmentSlot.CHEST.getIndex(100));
        p_111285_.put("armor.legs", EquipmentSlot.LEGS.getIndex(100));
        p_111285_.put("armor.feet", EquipmentSlot.FEET.getIndex(100));
        p_111285_.put("horse.saddle", 400);
        p_111285_.put("horse.armor", 401);
        p_111285_.put("horse.chest", 499);
    });

    public SlotArgument() {
    }

    public static SlotArgument slot() {
        return new SlotArgument();
    }

    public static int getSlot(CommandContext<CommandSourceStack> p_111280_, String p_111281_) {
        return (Integer)p_111280_.getArgument(p_111281_, Integer.class);
    }

    public Integer parse(StringReader p_111278_) throws CommandSyntaxException {
        String $$1 = p_111278_.readUnquotedString();
        if (!SLOTS.containsKey($$1)) {
            throw ERROR_UNKNOWN_SLOT.create($$1);
        } else {
            return (Integer)SLOTS.get($$1);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_111288_, SuggestionsBuilder p_111289_) {
        return SharedSuggestionProvider.suggest((Iterable)SLOTS.keySet(), p_111289_);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
