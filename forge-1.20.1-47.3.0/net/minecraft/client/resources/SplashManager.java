//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplashManager extends SimplePreparableReloadListener<List<String>> {
    private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
    private static final RandomSource RANDOM = RandomSource.create();
    private final List<String> splashes = Lists.newArrayList();
    private final User user;

    public SplashManager(User p_118866_) {
        this.user = p_118866_;
    }

    protected List<String> prepare(ResourceManager p_118869_, ProfilerFiller p_118870_) {
        try {
            BufferedReader $$2 = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION);

            List var4;
            try {
                var4 = (List)$$2.lines().map(String::trim).filter((p_118876_) -> {
                    return p_118876_.hashCode() != 125780783;
                }).collect(Collectors.toList());
            } catch (Throwable var7) {
                if ($$2 != null) {
                    try {
                        $$2.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if ($$2 != null) {
                $$2.close();
            }

            return var4;
        } catch (IOException var8) {
            return Collections.emptyList();
        }
    }

    protected void apply(List<String> p_118878_, ResourceManager p_118879_, ProfilerFiller p_118880_) {
        this.splashes.clear();
        this.splashes.addAll(p_118878_);
    }

    @Nullable
    public SplashRenderer getSplash() {
        Calendar $$0 = Calendar.getInstance();
        $$0.setTime(new Date());
        if ($$0.get(2) + 1 == 12 && $$0.get(5) == 24) {
            return SplashRenderer.CHRISTMAS;
        } else if ($$0.get(2) + 1 == 1 && $$0.get(5) == 1) {
            return SplashRenderer.NEW_YEAR;
        } else if ($$0.get(2) + 1 == 10 && $$0.get(5) == 31) {
            return SplashRenderer.HALLOWEEN;
        } else if (this.splashes.isEmpty()) {
            return null;
        } else if (this.user != null && RANDOM.nextInt(this.splashes.size()) == 42) {
            String var10002 = this.user.getName();
            return new SplashRenderer(var10002.toUpperCase(Locale.ROOT) + " IS YOU");
        } else {
            return new SplashRenderer((String)this.splashes.get(RANDOM.nextInt(this.splashes.size())));
        }
    }
}
