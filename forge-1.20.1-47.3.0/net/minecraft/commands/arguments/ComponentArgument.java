//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;

public class ComponentArgument implements ArgumentType<Component> {
    private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
    public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((p_87121_) -> {
        return Component.translatable("argument.component.invalid", p_87121_);
    });

    private ComponentArgument() {
    }

    public static Component getComponent(CommandContext<CommandSourceStack> p_87118_, String p_87119_) {
        return (Component)p_87118_.getArgument(p_87119_, Component.class);
    }

    public static ComponentArgument textComponent() {
        return new ComponentArgument();
    }

    public Component parse(StringReader p_87116_) throws CommandSyntaxException {
        try {
            Component $$1 = Serializer.fromJson(p_87116_);
            if ($$1 == null) {
                throw ERROR_INVALID_JSON.createWithContext(p_87116_, "empty");
            } else {
                return $$1;
            }
        } catch (Exception var4) {
            Exception $$2 = var4;
            String $$3 = $$2.getCause() != null ? $$2.getCause().getMessage() : $$2.getMessage();
            throw ERROR_INVALID_JSON.createWithContext(p_87116_, $$3);
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
