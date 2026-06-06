//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.gui.screens.inventory;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentNames {
    private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
    private static final Style ROOT_STYLE;
    private static final EnchantmentNames INSTANCE;
    private final RandomSource random = RandomSource.create();
    private final String[] words = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    private EnchantmentNames() {
    }

    public static EnchantmentNames getInstance() {
        return INSTANCE;
    }

    public FormattedText getRandomName(Font p_98738_, int p_98739_) {
        StringBuilder $$2 = new StringBuilder();
        int $$3 = this.random.nextInt(2) + 3;

        for(int $$4 = 0; $$4 < $$3; ++$$4) {
            if ($$4 != 0) {
                $$2.append(" ");
            }

            $$2.append((String)Util.getRandom((Object[])this.words, this.random));
        }

        return p_98738_.getSplitter().headByWidth(Component.literal($$2.toString()).withStyle(ROOT_STYLE), p_98739_, Style.EMPTY);
    }

    public void initSeed(long p_98736_) {
        this.random.setSeed(p_98736_);
    }

    static {
        ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
        INSTANCE = new EnchantmentNames();
    }
}
