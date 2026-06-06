//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateBuffetWorldScreen extends Screen {
    private static final Component BIOME_SELECT_INFO = Component.translatable("createWorld.customize.buffet.biome");
    private final Screen parent;
    private final Consumer<Holder<Biome>> applySettings;
    final Registry<Biome> biomes;
    private BiomeList list;
    Holder<Biome> biome;
    private Button doneButton;

    public CreateBuffetWorldScreen(Screen p_232732_, WorldCreationContext p_232733_, Consumer<Holder<Biome>> p_232734_) {
        super(Component.translatable("createWorld.customize.buffet.title"));
        this.parent = p_232732_;
        this.applySettings = p_232734_;
        this.biomes = p_232733_.worldgenLoadContext().registryOrThrow(Registries.BIOME);
        Holder<Biome> $$3 = (Holder)this.biomes.getHolder(Biomes.PLAINS).or(() -> {
            return this.biomes.holders().findAny();
        }).orElseThrow();
        this.biome = (Holder)p_232733_.selectedDimensions().overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse($$3);
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    protected void init() {
        this.list = new BiomeList();
        this.addWidget(this.list);
        this.doneButton = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_280788_) -> {
            this.applySettings.accept(this.biome);
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_280789_) -> {
            this.minecraft.setScreen(this.parent);
        }).bounds(this.width / 2 + 5, this.height - 28, 150, 20).build());
        this.list.setSelected((BiomeList.Entry)this.list.children().stream().filter((p_232738_) -> {
            return Objects.equals(p_232738_.biome, this.biome);
        }).findFirst().orElse((Object)null));
    }

    void updateButtonValidity() {
        this.doneButton.active = this.list.getSelected() != null;
    }

    public void render(GuiGraphics p_281766_, int p_95757_, int p_95758_, float p_95759_) {
        this.renderDirtBackground(p_281766_);
        this.list.render(p_281766_, p_95757_, p_95758_, p_95759_);
        p_281766_.drawCenteredString(this.font, (Component)this.title, this.width / 2, 8, 16777215);
        p_281766_.drawCenteredString(this.font, (Component)BIOME_SELECT_INFO, this.width / 2, 28, 10526880);
        super.render(p_281766_, p_95757_, p_95758_, p_95759_);
    }

    @OnlyIn(Dist.CLIENT)
    class BiomeList extends ObjectSelectionList<Entry> {
        BiomeList() {
            super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 40, CreateBuffetWorldScreen.this.height - 37, 16);
            Collator $$0 = Collator.getInstance(Locale.getDefault());
            CreateBuffetWorldScreen.this.biomes.holders().map((p_205389_) -> {
                return new Entry(p_205389_);
            }).sorted(Comparator.comparing((p_203142_) -> {
                return p_203142_.name.getString();
            }, $$0)).forEach((p_203138_) -> {
                this.addEntry(p_203138_);
            });
        }

        public void setSelected(@Nullable Entry p_95785_) {
            super.setSelected(p_95785_);
            if (p_95785_ != null) {
                CreateBuffetWorldScreen.this.biome = p_95785_.biome;
            }

            CreateBuffetWorldScreen.this.updateButtonValidity();
        }

        @OnlyIn(Dist.CLIENT)
        private class Entry extends ObjectSelectionList.Entry<Entry> {
            final Holder.Reference<Biome> biome;
            final Component name;

            public Entry(Holder.Reference<Biome> p_205392_) {
                this.biome = p_205392_;
                ResourceLocation $$1 = p_205392_.key().location();
                String $$2 = $$1.toLanguageKey("biome");
                if (Language.getInstance().has($$2)) {
                    this.name = Component.translatable($$2);
                } else {
                    this.name = Component.literal($$1.toString());
                }

            }

            public Component getNarration() {
                return Component.translatable("narrator.select", this.name);
            }

            public void render(GuiGraphics p_281315_, int p_282451_, int p_283356_, int p_283563_, int p_282677_, int p_283473_, int p_283681_, int p_281493_, boolean p_281302_, float p_283122_) {
                p_281315_.drawString(CreateBuffetWorldScreen.this.font, this.name, p_283563_ + 5, p_283356_ + 2, 16777215);
            }

            public boolean mouseClicked(double p_95798_, double p_95799_, int p_95800_) {
                if (p_95800_ == 0) {
                    BiomeList.this.setSelected(this);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
