//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.network.filters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

class CommandTreeCleaner {
    CommandTreeCleaner() {
    }

    public static <S> RootCommandNode<S> cleanArgumentTypes(RootCommandNode<S> root, Predicate<ArgumentType<?>> argumentTypeFilter) {
        Predicate<CommandNode<?>> nodeFilter = (node) -> {
            return !(node instanceof ArgumentCommandNode) || argumentTypeFilter.test(((ArgumentCommandNode)node).getType());
        };
        return (RootCommandNode)processCommandNode(root, nodeFilter, new HashMap());
    }

    private static <S> CommandNode<S> processCommandNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes) {
        CommandNode<S> existingNode = (CommandNode)newNodes.get(node);
        if (existingNode == null) {
            CommandNode<S> newNode = cloneNode(node, nodeFilter, newNodes);
            newNodes.put(node, newNode);
            Stream var10000 = node.getChildren().stream().filter(nodeFilter).map((child) -> {
                return processCommandNode(child, nodeFilter, newNodes);
            });
            Objects.requireNonNull(newNode);
            var10000.forEach(newNode::addChild);
            return newNode;
        } else {
            return existingNode;
        }
    }

    private static <S> CommandNode<S> cloneNode(CommandNode<S> node, Predicate<CommandNode<?>> nodeFilter, Map<CommandNode<S>, CommandNode<S>> newNodes) {
        if (node instanceof RootCommandNode) {
            return new RootCommandNode();
        } else {
            ArgumentBuilder<S, ?> builder = node.createBuilder();
            if (node.getRedirect() != null) {
                if (nodeFilter.test(node.getRedirect())) {
                    builder.forward(processCommandNode(node.getRedirect(), nodeFilter, newNodes), node.getRedirectModifier(), node.isFork());
                } else {
                    builder.redirect((CommandNode)null);
                }
            }

            return builder.build();
        }
    }
}
