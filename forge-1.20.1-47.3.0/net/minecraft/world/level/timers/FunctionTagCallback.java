//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.timers;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public class FunctionTagCallback implements TimerCallback<MinecraftServer> {
    final ResourceLocation tagId;

    public FunctionTagCallback(ResourceLocation p_82191_) {
        this.tagId = p_82191_;
    }

    public void handle(MinecraftServer p_82199_, TimerQueue<MinecraftServer> p_82200_, long p_82201_) {
        ServerFunctionManager $$3 = p_82199_.getFunctions();
        Collection<CommandFunction> $$4 = $$3.getTag(this.tagId);
        Iterator var7 = $$4.iterator();

        while(var7.hasNext()) {
            CommandFunction $$5 = (CommandFunction)var7.next();
            $$3.execute($$5, $$3.getGameLoopSender());
        }

    }

    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
        public Serializer() {
            super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
        }

        public void serialize(CompoundTag p_82206_, FunctionTagCallback p_82207_) {
            p_82206_.putString("Name", p_82207_.tagId.toString());
        }

        public FunctionTagCallback deserialize(CompoundTag p_82204_) {
            ResourceLocation $$1 = new ResourceLocation(p_82204_.getString("Name"));
            return new FunctionTagCallback($$1);
        }
    }
}
