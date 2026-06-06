//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ScoreContents implements ComponentContents {
    private static final String SCORER_PLACEHOLDER = "*";
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseSelector(String p_237448_) {
        try {
            return (new EntitySelectorParser(new StringReader(p_237448_))).parse();
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public ScoreContents(String p_237438_, String p_237439_) {
        this.name = p_237438_;
        this.selector = parseSelector(p_237438_);
        this.objective = p_237439_;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public EntitySelector getSelector() {
        return this.selector;
    }

    public String getObjective() {
        return this.objective;
    }

    private String findTargetName(CommandSourceStack p_237442_) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> $$1 = this.selector.findEntities(p_237442_);
            if (!$$1.isEmpty()) {
                if ($$1.size() != 1) {
                    throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
                }

                return ((Entity)$$1.get(0)).getScoreboardName();
            }
        }

        return this.name;
    }

    private String getScore(String p_237450_, CommandSourceStack p_237451_) {
        MinecraftServer $$2 = p_237451_.getServer();
        if ($$2 != null) {
            Scoreboard $$3 = $$2.getScoreboard();
            Objective $$4 = $$3.getObjective(this.objective);
            if ($$3.hasPlayerScore(p_237450_, $$4)) {
                Score $$5 = $$3.getOrCreatePlayerScore(p_237450_, $$4);
                return Integer.toString($$5.getScore());
            }
        }

        return "";
    }

    public MutableComponent resolve(@Nullable CommandSourceStack p_237444_, @Nullable Entity p_237445_, int p_237446_) throws CommandSyntaxException {
        if (p_237444_ == null) {
            return Component.empty();
        } else {
            String $$3 = this.findTargetName(p_237444_);
            String $$4 = p_237445_ != null && $$3.equals("*") ? p_237445_.getScoreboardName() : $$3;
            return Component.literal(this.getScore($$4, p_237444_));
        }
    }

    public boolean equals(Object p_237455_) {
        if (this == p_237455_) {
            return true;
        } else {
            boolean var10000;
            if (p_237455_ instanceof ScoreContents) {
                ScoreContents $$1 = (ScoreContents)p_237455_;
                if (this.name.equals($$1.name) && this.objective.equals($$1.objective)) {
                    var10000 = true;
                    return var10000;
                }
            }

            var10000 = false;
            return var10000;
        }
    }

    public int hashCode() {
        int $$0 = this.name.hashCode();
        $$0 = 31 * $$0 + this.objective.hashCode();
        return $$0;
    }

    public String toString() {
        return "score{name='" + this.name + "', objective='" + this.objective + "'}";
    }
}
