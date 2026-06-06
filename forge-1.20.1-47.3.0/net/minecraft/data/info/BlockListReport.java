//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.data.info;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockListReport implements DataProvider {
    private final PackOutput output;

    public BlockListReport(PackOutput p_251533_) {
        this.output = p_251533_;
    }

    public CompletableFuture<?> run(CachedOutput p_236197_) {
        JsonObject $$1 = new JsonObject();
        Iterator var3 = BuiltInRegistries.BLOCK.iterator();

        while(var3.hasNext()) {
            Block $$2 = (Block)var3.next();
            ResourceLocation $$3 = BuiltInRegistries.BLOCK.getKey($$2);
            JsonObject $$4 = new JsonObject();
            StateDefinition<Block, BlockState> $$5 = $$2.getStateDefinition();
            if (!$$5.getProperties().isEmpty()) {
                JsonObject $$6 = new JsonObject();
                Iterator var9 = $$5.getProperties().iterator();

                while(true) {
                    if (!var9.hasNext()) {
                        $$4.add("properties", $$6);
                        break;
                    }

                    Property<?> $$7 = (Property)var9.next();
                    JsonArray $$8 = new JsonArray();
                    Iterator var12 = $$7.getPossibleValues().iterator();

                    while(var12.hasNext()) {
                        Comparable<?> $$9 = (Comparable)var12.next();
                        $$8.add(Util.getPropertyName($$7, $$9));
                    }

                    $$6.add($$7.getName(), $$8);
                }
            }

            JsonArray $$10 = new JsonArray();

            JsonObject $$12;
            for(UnmodifiableIterator var17 = $$5.getPossibleStates().iterator(); var17.hasNext(); $$10.add($$12)) {
                BlockState $$11 = (BlockState)var17.next();
                $$12 = new JsonObject();
                JsonObject $$13 = new JsonObject();
                Iterator var21 = $$5.getProperties().iterator();

                while(var21.hasNext()) {
                    Property<?> $$14 = (Property)var21.next();
                    $$13.addProperty($$14.getName(), Util.getPropertyName($$14, $$11.getValue($$14)));
                }

                if ($$13.size() > 0) {
                    $$12.add("properties", $$13);
                }

                $$12.addProperty("id", Block.getId($$11));
                if ($$11 == $$2.defaultBlockState()) {
                    $$12.addProperty("default", true);
                }
            }

            $$4.add("states", $$10);
            $$1.add($$3.toString(), $$4);
        }

        Path $$15 = this.output.getOutputFolder(Target.REPORTS).resolve("blocks.json");
        return DataProvider.saveStable(p_236197_, $$1, $$15);
    }

    public final String getName() {
        return "Block List";
    }
}
