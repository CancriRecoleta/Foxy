//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SIZE = 4096;
    private static final short SIZE_BITS = 12;

    public TrappedChestBlockEntityFix(Schema p_17018_, boolean p_17019_) {
        super(p_17018_, p_17019_);
    }

    public TypeRewriteRule makeRule() {
        Type<?> $$0 = this.getOutputSchema().getType(References.CHUNK);
        Type<?> $$1 = $$0.findFieldType("Level");
        Type<?> $$2 = $$1.findFieldType("TileEntities");
        if (!($$2 instanceof List.ListType<?> $$3)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        } else {
            OpticFinder<? extends java.util.List<?>> $$4 = DSL.fieldFinder("TileEntities", $$3);
            Type<?> $$5 = this.getInputSchema().getType(References.CHUNK);
            OpticFinder<?> $$6 = $$5.findField("Level");
            OpticFinder<?> $$7 = $$6.type().findField("Sections");
            Type<?> $$8 = $$7.type();
            if (!($$8 instanceof List.ListType)) {
                throw new IllegalStateException("Expecting sections to be a list.");
            } else {
                Type<?> $$9 = ((List.ListType)$$8).getElement();
                OpticFinder<?> $$10 = DSL.typeFinder($$9);
                return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", $$5, (p_17031_) -> {
                    return p_17031_.updateTyped($$6, (p_145746_) -> {
                        Optional<? extends Typed<?>> $$4x = p_145746_.getOptionalTyped($$7);
                        if (!$$4x.isPresent()) {
                            return p_145746_;
                        } else {
                            java.util.List<? extends Typed<?>> $$5 = ((Typed)$$4x.get()).getAllTyped($$10);
                            IntSet $$6 = new IntOpenHashSet();
                            Iterator var8 = $$5.iterator();

                            while(true) {
                                TrappedChestSection $$8;
                                do {
                                    if (!var8.hasNext()) {
                                        Dynamic<?> $$11 = (Dynamic)p_145746_.get(DSL.remainderFinder());
                                        int $$12 = $$11.get("xPos").asInt(0);
                                        int $$13 = $$11.get("zPos").asInt(0);
                                        TaggedChoice.TaggedChoiceType<String> $$14 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                                        return p_145746_.updateTyped($$4, (p_145752_) -> {
                                            return p_145752_.updateTyped($$14.finder(), (p_145741_) -> {
                                                Dynamic<?> $$5 = (Dynamic)p_145741_.getOrCreate(DSL.remainderFinder());
                                                int $$6x = $$5.get("x").asInt(0) - ($$12 << 4);
                                                int $$7 = $$5.get("y").asInt(0);
                                                int $$8 = $$5.get("z").asInt(0) - ($$13 << 4);
                                                return $$6.contains(LeavesFix.getIndex($$6x, $$7, $$8)) ? p_145741_.update($$14.finder(), (p_145754_) -> {
                                                    return p_145754_.mapFirst((p_145756_) -> {
                                                        if (!Objects.equals(p_145756_, "minecraft:chest")) {
                                                            LOGGER.warn("Block Entity was expected to be a chest");
                                                        }

                                                        return "minecraft:trapped_chest";
                                                    });
                                                }) : p_145741_;
                                            });
                                        });
                                    }

                                    Typed<?> $$7x = (Typed)var8.next();
                                    $$8 = new TrappedChestSection($$7x, this.getInputSchema());
                                } while($$8.isSkippable());

                                for(int $$9 = 0; $$9 < 4096; ++$$9) {
                                    int $$10x = $$8.getBlock($$9);
                                    if ($$8.isTrappedChest($$10x)) {
                                        $$6.add($$8.getIndex() << 12 | $$9);
                                    }
                                }
                            }
                        }
                    });
                }));
            }
        }
    }

    public static final class TrappedChestSection extends LeavesFix.Section {
        @Nullable
        private IntSet chestIds;

        public TrappedChestSection(Typed<?> p_17050_, Schema p_17051_) {
            super(p_17050_, p_17051_);
        }

        protected boolean skippable() {
            this.chestIds = new IntOpenHashSet();

            for(int $$0 = 0; $$0 < this.palette.size(); ++$$0) {
                Dynamic<?> $$1 = (Dynamic)this.palette.get($$0);
                String $$2 = $$1.get("Name").asString("");
                if (Objects.equals($$2, "minecraft:trapped_chest")) {
                    this.chestIds.add($$0);
                }
            }

            return this.chestIds.isEmpty();
        }

        public boolean isTrappedChest(int p_17054_) {
            return this.chestIds.contains(p_17054_);
        }
    }
}
