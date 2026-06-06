//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringDecomposer;
import net.minecraftforge.server.LanguageHook;
import org.slf4j.Logger;

public abstract class Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public static final String DEFAULT = "en_us";
    private static volatile Language instance = loadDefault();

    public Language() {
    }

    private static Language loadDefault() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biconsumer = builder::put;
        parseTranslations(biconsumer, "/assets/minecraft/lang/en_us.json");
        final Map<String, String> map = new HashMap(builder.build());
        LanguageHook.captureLanguageMap(map);
        return new Language() {
            public String getOrDefault(String p_128127_, String p_265421_) {
                return (String)map.getOrDefault(p_128127_, p_265421_);
            }

            public boolean has(String p_128135_) {
                return map.containsKey(p_128135_);
            }

            public boolean isDefaultRightToLeft() {
                return false;
            }

            public FormattedCharSequence getVisualOrder(FormattedText p_128129_) {
                return (p_128132_) -> {
                    return p_128129_.visit((p_177835_, p_177836_) -> {
                        return StringDecomposer.iterateFormatted(p_177836_, p_177835_, p_128132_) ? Optional.empty() : FormattedText.STOP_ITERATION;
                    }, Style.EMPTY).isPresent();
                };
            }

            public Map<String, String> getLanguageData() {
                return map;
            }
        };
    }

    private static void parseTranslations(BiConsumer<String, String> p_282031_, String p_283638_) {
        try {
            InputStream inputstream = Language.class.getResourceAsStream(p_283638_);

            try {
                loadFromJson(inputstream, p_282031_);
            } catch (Throwable var6) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (inputstream != null) {
                inputstream.close();
            }
        } catch (IOException | JsonParseException var7) {
            Exception ioexception = var7;
            LOGGER.error("Couldn't read strings from {}", p_283638_, ioexception);
        }

    }

    public static void loadFromJson(InputStream p_128109_, BiConsumer<String, String> p_128110_) {
        JsonObject jsonobject = (JsonObject)GSON.fromJson(new InputStreamReader(p_128109_, StandardCharsets.UTF_8), JsonObject.class);
        Iterator var3 = jsonobject.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, JsonElement> entry = (Map.Entry)var3.next();
            String s = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
            p_128110_.accept((String)entry.getKey(), s);
        }

    }

    public static Language getInstance() {
        return instance;
    }

    public static void inject(Language p_128115_) {
        instance = p_128115_;
    }

    public Map<String, String> getLanguageData() {
        return ImmutableMap.of();
    }

    public String getOrDefault(String p_128111_) {
        return this.getOrDefault(p_128111_, p_128111_);
    }

    public abstract String getOrDefault(String var1, String var2);

    public abstract boolean has(String var1);

    public abstract boolean isDefaultRightToLeft();

    public abstract FormattedCharSequence getVisualOrder(FormattedText var1);

    public List<FormattedCharSequence> getVisualOrder(List<FormattedText> p_128113_) {
        return (List)p_128113_.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}
