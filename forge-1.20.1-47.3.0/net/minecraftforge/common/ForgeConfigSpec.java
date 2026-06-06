//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.common;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.InMemoryFormat;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.ConfigSpec.CorrectionAction;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ForgeConfigSpec extends UnmodifiableConfigWrapper<UnmodifiableConfig> implements IConfigSpec<ForgeConfigSpec> {
    private final Map<List<String>, String> levelComments;
    private final Map<List<String>, String> levelTranslationKeys;
    private final UnmodifiableConfig values;
    private Config childConfig;
    private boolean isCorrecting = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern WINDOWS_NEWLINE = Pattern.compile("\r\n");
    private static final Joiner LINE_JOINER = Joiner.on("\n");
    private static final Joiner DOT_JOINER = Joiner.on(".");
    private static final Splitter DOT_SPLITTER = Splitter.on(".");

    private ForgeConfigSpec(UnmodifiableConfig storage, UnmodifiableConfig values, Map<List<String>, String> levelComments, Map<List<String>, String> levelTranslationKeys) {
        super(storage);
        this.values = values;
        this.levelComments = levelComments;
        this.levelTranslationKeys = levelTranslationKeys;
    }

    public String getLevelComment(List<String> path) {
        return (String)this.levelComments.get(path);
    }

    public String getLevelTranslationKey(List<String> path) {
        return (String)this.levelTranslationKeys.get(path);
    }

    public void setConfig(CommentedConfig config) {
        this.childConfig = config;
        if (config != null && !this.isCorrect(config)) {
            String configName = config instanceof FileConfig ? ((FileConfig)config).getNioPath().toString() : config.toString();
            LOGGER.warn(Logging.CORE, "Configuration file {} is not correct. Correcting", configName);
            this.correct(config, (action, path, incorrectValue, correctedValue) -> {
                LOGGER.warn(Logging.CORE, "Incorrect key {} was corrected from {} to its default, {}. {}", DOT_JOINER.join(path), incorrectValue, correctedValue, incorrectValue == correctedValue ? "This seems to be an error." : "");
            }, (action, path, incorrectValue, correctedValue) -> {
                LOGGER.debug(Logging.CORE, "The comment on key {} does not match the spec. This may create a backup.", DOT_JOINER.join(path));
            });
            if (config instanceof FileConfig) {
                ((FileConfig)config).save();
            }
        }

        this.afterReload();
    }

    public void acceptConfig(CommentedConfig data) {
        this.setConfig(data);
    }

    public boolean isCorrecting() {
        return this.isCorrecting;
    }

    public boolean isLoaded() {
        return this.childConfig != null;
    }

    public UnmodifiableConfig getSpec() {
        return this.config;
    }

    public UnmodifiableConfig getValues() {
        return this.values;
    }

    public void afterReload() {
        this.resetCaches(this.getValues().valueMap().values());
    }

    private void resetCaches(Iterable<Object> configValues) {
        configValues.forEach((value) -> {
            if (value instanceof ConfigValue<?> configValue) {
                configValue.clearCache();
            } else if (value instanceof Config innerConfig) {
                this.resetCaches(innerConfig.valueMap().values());
            }

        });
    }

    public void save() {
        Preconditions.checkNotNull(this.childConfig, "Cannot save config value without assigned Config object present");
        if (this.childConfig instanceof FileConfig) {
            ((FileConfig)this.childConfig).save();
        }

    }

    public synchronized boolean isCorrect(CommentedConfig config) {
        LinkedList<String> parentPath = new LinkedList();
        return this.correct(this.config, config, parentPath, Collections.unmodifiableList(parentPath), (a, b, c, d) -> {
        }, (ConfigSpec.CorrectionListener)null, true) == 0;
    }

    public int correct(CommentedConfig config) {
        return this.correct(config, (action, path, incorrectValue, correctedValue) -> {
        }, (ConfigSpec.CorrectionListener)null);
    }

    public synchronized int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener) {
        return this.correct(config, listener, (ConfigSpec.CorrectionListener)null);
    }

    public synchronized int correct(CommentedConfig config, ConfigSpec.CorrectionListener listener, ConfigSpec.CorrectionListener commentListener) {
        LinkedList<String> parentPath = new LinkedList();
        int ret = true;

        int ret;
        try {
            this.isCorrecting = true;
            ret = this.correct(this.config, config, parentPath, Collections.unmodifiableList(parentPath), listener, commentListener, false);
        } finally {
            this.isCorrecting = false;
        }

        return ret;
    }

    private int correct(UnmodifiableConfig spec, CommentedConfig config, LinkedList<String> parentPath, List<String> parentPathUnmodifiable, ConfigSpec.CorrectionListener listener, ConfigSpec.CorrectionListener commentListener, boolean dryRun) {
        int count = 0;
        Map<String, Object> specMap = spec.valueMap();
        Map<String, Object> configMap = config.valueMap();

        Iterator ittr;
        Map.Entry entry;
        for(ittr = specMap.entrySet().iterator(); ittr.hasNext(); parentPath.removeLast()) {
            entry = (Map.Entry)ittr.next();
            String key = (String)entry.getKey();
            Object specValue = entry.getValue();
            Object configValue = configMap.get(key);
            ConfigSpec.CorrectionAction action = configValue == null ? CorrectionAction.ADD : CorrectionAction.REPLACE;
            parentPath.addLast(key);
            String oldComment;
            if (specValue instanceof Config) {
                if (configValue instanceof CommentedConfig) {
                    count += this.correct((Config)specValue, (CommentedConfig)configValue, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                    if (count > 0 && dryRun) {
                        return count;
                    }
                } else {
                    if (dryRun) {
                        return 1;
                    }

                    CommentedConfig newValue = config.createSubConfig();
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    ++count;
                    count += this.correct((Config)specValue, newValue, parentPath, parentPathUnmodifiable, listener, commentListener, dryRun);
                }

                String newComment = (String)this.levelComments.get(parentPath);
                oldComment = config.getComment(key);
                if (!this.stringsMatchIgnoringNewlines(oldComment, newComment)) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, newComment);
                    }

                    if (dryRun) {
                        return 1;
                    }

                    config.setComment(key, newComment);
                }
            } else {
                ValueSpec valueSpec = (ValueSpec)specValue;
                if (!valueSpec.test(configValue)) {
                    if (dryRun) {
                        return 1;
                    }

                    Object newValue = valueSpec.correct(configValue);
                    configMap.put(key, newValue);
                    listener.onCorrect(action, parentPathUnmodifiable, configValue, newValue);
                    ++count;
                }

                oldComment = config.getComment(key);
                if (!this.stringsMatchIgnoringNewlines(oldComment, valueSpec.getComment())) {
                    if (commentListener != null) {
                        commentListener.onCorrect(action, parentPathUnmodifiable, oldComment, valueSpec.getComment());
                    }

                    if (dryRun) {
                        return 1;
                    }

                    config.setComment(key, valueSpec.getComment());
                }
            }
        }

        ittr = configMap.entrySet().iterator();

        while(ittr.hasNext()) {
            entry = (Map.Entry)ittr.next();
            if (!specMap.containsKey(entry.getKey())) {
                if (dryRun) {
                    return 1;
                }

                ittr.remove();
                parentPath.addLast((String)entry.getKey());
                listener.onCorrect(CorrectionAction.REMOVE, parentPathUnmodifiable, entry.getValue(), (Object)null);
                parentPath.removeLast();
                ++count;
            }
        }

        return count;
    }

    private boolean stringsMatchIgnoringNewlines(@Nullable Object obj1, @Nullable Object obj2) {
        if (obj1 instanceof String string1) {
            if (obj2 instanceof String string2) {
                if (!string1.isEmpty() && !string2.isEmpty()) {
                    return WINDOWS_NEWLINE.matcher(string1).replaceAll("\n").equals(WINDOWS_NEWLINE.matcher(string2).replaceAll("\n"));
                }
            }
        }

        return Objects.equals(obj1, obj2);
    }

    private static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }

    public static class ValueSpec {
        private final String comment;
        private final String langKey;
        private final Range<?> range;
        private final boolean worldRestart;
        private final Class<?> clazz;
        private final Supplier<?> supplier;
        private final Predicate<Object> validator;

        private ValueSpec(Supplier<?> supplier, Predicate<Object> validator, BuilderContext context, List<String> path) {
            Objects.requireNonNull(supplier, "Default supplier can not be null");
            Objects.requireNonNull(validator, "Validator can not be null");
            this.comment = context.hasComment() ? context.buildComment(path) : null;
            this.langKey = context.getTranslationKey();
            this.range = context.getRange();
            this.worldRestart = context.needsWorldRestart();
            this.clazz = context.getClazz();
            this.supplier = supplier;
            this.validator = validator;
        }

        public String getComment() {
            return this.comment;
        }

        public String getTranslationKey() {
            return this.langKey;
        }

        public <V extends Comparable<? super V>> Range<V> getRange() {
            return this.range;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public boolean test(Object value) {
            return this.validator.test(value);
        }

        public Object correct(Object value) {
            return this.range == null ? this.getDefault() : this.range.correct(value, this.getDefault());
        }

        public Object getDefault() {
            return this.supplier.get();
        }
    }

    public static class ConfigValue<T> implements Supplier<T> {
        private static boolean USE_CACHES = true;
        private final Builder parent;
        private final List<String> path;
        private final Supplier<T> defaultSupplier;
        private T cachedValue = null;
        private ForgeConfigSpec spec;

        ConfigValue(Builder parent, List<String> path, Supplier<T> defaultSupplier) {
            this.parent = parent;
            this.path = path;
            this.defaultSupplier = defaultSupplier;
            this.parent.values.add(this);
        }

        public List<String> getPath() {
            return new ArrayList(this.path);
        }

        public T get() {
            Preconditions.checkNotNull(this.spec, "Cannot get config value before spec is built");
            if (!FMLEnvironment.production) {
                Preconditions.checkState(this.spec.childConfig != null, "Cannot get config value before config is loaded.\nThis error is currently only thrown in the development environment, to avoid breaking published mods.\nIn a future version, this will also throw in the production environment.\n");
            }

            if (this.spec.childConfig == null) {
                return this.defaultSupplier.get();
            } else {
                if (USE_CACHES && this.cachedValue == null) {
                    this.cachedValue = this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
                } else if (!USE_CACHES) {
                    return this.getRaw(this.spec.childConfig, this.path, this.defaultSupplier);
                }

                return this.cachedValue;
            }
        }

        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getOrElse(path, defaultSupplier);
        }

        public T getDefault() {
            return this.defaultSupplier.get();
        }

        public Builder next() {
            return this.parent;
        }

        public void save() {
            Preconditions.checkNotNull(this.spec, "Cannot save config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot save config value without assigned Config object present");
            this.spec.save();
        }

        public void set(T value) {
            Preconditions.checkNotNull(this.spec, "Cannot set config value before spec is built");
            Preconditions.checkNotNull(this.spec.childConfig, "Cannot set config value without assigned Config object present");
            this.spec.childConfig.set(this.path, value);
            this.cachedValue = value;
        }

        public void clearCache() {
            this.cachedValue = null;
        }
    }

    public static class EnumValue<T extends Enum<T>> extends ConfigValue<T> {
        private final EnumGetMethod converter;
        private final Class<T> clazz;

        EnumValue(Builder parent, List<String> path, Supplier<T> defaultSupplier, EnumGetMethod converter, Class<T> clazz) {
            super(parent, path, defaultSupplier);
            this.converter = converter;
            this.clazz = clazz;
        }

        protected T getRaw(Config config, List<String> path, Supplier<T> defaultSupplier) {
            return config.getEnumOrElse(path, this.clazz, this.converter, defaultSupplier);
        }
    }

    public static class DoubleValue extends ConfigValue<Double> {
        DoubleValue(Builder parent, List<String> path, Supplier<Double> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        protected Double getRaw(Config config, List<String> path, Supplier<Double> defaultSupplier) {
            Number n = (Number)config.get(path);
            return n == null ? (Double)defaultSupplier.get() : n.doubleValue();
        }
    }

    public static class LongValue extends ConfigValue<Long> {
        LongValue(Builder parent, List<String> path, Supplier<Long> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        protected Long getRaw(Config config, List<String> path, Supplier<Long> defaultSupplier) {
            return config.getLongOrElse(path, () -> {
                return (Long)defaultSupplier.get();
            });
        }
    }

    public static class IntValue extends ConfigValue<Integer> {
        IntValue(Builder parent, List<String> path, Supplier<Integer> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }

        protected Integer getRaw(Config config, List<String> path, Supplier<Integer> defaultSupplier) {
            return config.getIntOrElse(path, () -> {
                return (Integer)defaultSupplier.get();
            });
        }
    }

    public static class BooleanValue extends ConfigValue<Boolean> {
        BooleanValue(Builder parent, List<String> path, Supplier<Boolean> defaultSupplier) {
            super(parent, path, defaultSupplier);
        }
    }

    public static class Range<V extends Comparable<? super V>> implements Predicate<Object> {
        private final Class<? extends V> clazz;
        private final V min;
        private final V max;

        private Range(Class<V> clazz, V min, V max) {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }

        public Class<? extends V> getClazz() {
            return this.clazz;
        }

        public V getMin() {
            return this.min;
        }

        public V getMax() {
            return this.max;
        }

        private boolean isNumber(Object other) {
            return Number.class.isAssignableFrom(this.clazz) && other instanceof Number;
        }

        public boolean test(Object t) {
            boolean result;
            if (this.isNumber(t)) {
                Number n = (Number)t;
                result = ((Number)this.min).doubleValue() <= n.doubleValue() && n.doubleValue() <= ((Number)this.max).doubleValue();
                if (!result) {
                    ForgeConfigSpec.LOGGER.debug(Logging.CORE, "Range value {} is not within its bounds {}-{}", n.doubleValue(), ((Number)this.min).doubleValue(), ((Number)this.max).doubleValue());
                }

                return result;
            } else if (!this.clazz.isInstance(t)) {
                return false;
            } else {
                V c = (Comparable)this.clazz.cast(t);
                result = c.compareTo(this.min) >= 0 && c.compareTo(this.max) <= 0;
                if (!result) {
                    ForgeConfigSpec.LOGGER.debug(Logging.CORE, "Range value {} is not within its bounds {}-{}", c, this.min, this.max);
                }

                return result;
            }
        }

        public Object correct(Object value, Object def) {
            if (this.isNumber(value)) {
                Number n = (Number)value;
                return n.doubleValue() < ((Number)this.min).doubleValue() ? this.min : (n.doubleValue() > ((Number)this.max).doubleValue() ? this.max : value);
            } else if (!this.clazz.isInstance(value)) {
                return def;
            } else {
                V c = (Comparable)this.clazz.cast(value);
                return c.compareTo(this.min) < 0 ? this.min : (c.compareTo(this.max) > 0 ? this.max : value);
            }
        }

        public String toString() {
            if (this.clazz == Integer.class) {
                if (this.max.equals(Integer.MAX_VALUE)) {
                    return "> " + this.min;
                }

                if (this.min.equals(Integer.MIN_VALUE)) {
                    return "< " + this.max;
                }
            }

            return this.min + " ~ " + this.max;
        }
    }

    private static class BuilderContext {
        private final List<String> comment = new LinkedList();
        private String langKey;
        private Range<?> range;
        private boolean worldRestart = false;
        private Class<?> clazz;

        private BuilderContext() {
        }

        public void addComment(String value) {
            Preconditions.checkNotNull(value, "Passed in null value for comment");
            this.comment.add(value);
        }

        public void clearComment() {
            this.comment.clear();
        }

        public boolean hasComment() {
            return this.comment.size() > 0;
        }

        public String buildComment() {
            return this.buildComment(List.of("unknown", "unknown"));
        }

        public String buildComment(List<String> path) {
            if (this.comment.stream().allMatch(String::isBlank)) {
                if (FMLEnvironment.production) {
                    ForgeConfigSpec.LOGGER.warn(Logging.CORE, "Detected a comment that is all whitespace for config option {}, which causes obscure bugs in Forge's config system and will cause a crash in the future. Please report this to the mod author.", ForgeConfigSpec.DOT_JOINER.join(path));
                    return "A developer of this mod has defined this config option with a blank comment, which causes obscure bugs in Forge's config system and will cause a crash in the future. Please report this to the mod author.";
                } else {
                    throw new IllegalStateException("Can not build comment for config option " + ForgeConfigSpec.DOT_JOINER.join(path) + " as it comprises entirely of blank lines/whitespace. This is not allowed as it causes a \"constantly correcting config\" bug with NightConfig in Forge's config system.");
                }
            } else {
                return ForgeConfigSpec.LINE_JOINER.join(this.comment);
            }
        }

        public void setTranslationKey(String value) {
            this.langKey = value;
        }

        public String getTranslationKey() {
            return this.langKey;
        }

        public <V extends Comparable<? super V>> void setRange(Range<V> value) {
            this.range = value;
            this.setClazz(value.getClazz());
        }

        public <V extends Comparable<? super V>> Range<V> getRange() {
            return this.range;
        }

        public void worldRestart() {
            this.worldRestart = true;
        }

        public boolean needsWorldRestart() {
            return this.worldRestart;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return this.clazz;
        }

        public void ensureEmpty() {
            this.validate(this.hasComment(), "Non-empty comment when empty expected");
            this.validate(this.langKey, "Non-null translation key when null expected");
            this.validate(this.range, "Non-null range when null expected");
            this.validate(this.worldRestart, "Dangeling world restart value set to true");
        }

        private void validate(Object value, String message) {
            if (value != null) {
                throw new IllegalStateException(message);
            }
        }

        private void validate(boolean value, String message) {
            if (value) {
                throw new IllegalStateException(message);
            }
        }
    }

    public static class Builder {
        private final Config storage = Config.of(LinkedHashMap::new, InMemoryFormat.withUniversalSupport());
        private BuilderContext context = new BuilderContext();
        private final Map<List<String>, String> levelComments = new HashMap();
        private final Map<List<String>, String> levelTranslationKeys = new HashMap();
        private final List<String> currentPath = new ArrayList();
        private final List<ConfigValue<?>> values = new ArrayList();

        public Builder() {
        }

        public <T> ConfigValue<T> define(String path, T defaultValue) {
            return this.define(ForgeConfigSpec.split(path), defaultValue);
        }

        public <T> ConfigValue<T> define(List<String> path, T defaultValue) {
            return this.define(path, defaultValue, (o) -> {
                return o != null && defaultValue.getClass().isAssignableFrom(o.getClass());
            });
        }

        public <T> ConfigValue<T> define(String path, T defaultValue, Predicate<Object> validator) {
            return this.define(ForgeConfigSpec.split(path), defaultValue, validator);
        }

        public <T> ConfigValue<T> define(List<String> path, T defaultValue, Predicate<Object> validator) {
            Objects.requireNonNull(defaultValue, "Default value can not be null");
            return this.define(path, () -> {
                return defaultValue;
            }, validator);
        }

        public <T> ConfigValue<T> define(String path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
            return this.define(ForgeConfigSpec.split(path), defaultSupplier, validator);
        }

        public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator) {
            return this.define(path, defaultSupplier, validator, Object.class);
        }

        public <T> ConfigValue<T> define(List<String> path, Supplier<T> defaultSupplier, Predicate<Object> validator, Class<?> clazz) {
            this.context.setClazz(clazz);
            return this.define(path, new ValueSpec(defaultSupplier, validator, this.context, path), defaultSupplier);
        }

        public <T> ConfigValue<T> define(List<String> path, ValueSpec value, Supplier<T> defaultSupplier) {
            if (!this.currentPath.isEmpty()) {
                List<String> tmp = new ArrayList(this.currentPath.size() + ((List)path).size());
                tmp.addAll(this.currentPath);
                tmp.addAll((Collection)path);
                path = tmp;
            }

            this.storage.set((List)path, value);
            this.context = new BuilderContext();
            return new ConfigValue(this, (List)path, defaultSupplier);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, V defaultValue, V min, V max, Class<V> clazz) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultValue, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, V defaultValue, V min, V max, Class<V> clazz) {
            return this.defineInRange(path, () -> {
                return defaultValue;
            }, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(String path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultSupplier, min, max, clazz);
        }

        public <V extends Comparable<? super V>> ConfigValue<V> defineInRange(List<String> path, Supplier<V> defaultSupplier, V min, V max, Class<V> clazz) {
            Range<V> range = new Range(clazz, min, max);
            this.context.setRange(range);
            this.comment("Range: " + range.toString());
            if (min.compareTo(max) > 0) {
                throw new IllegalArgumentException("Range min most be less then max.");
            } else {
                return this.define((List)path, (Supplier)defaultSupplier, (Predicate)range);
            }
        }

        public <T> ConfigValue<T> defineInList(String path, T defaultValue, Collection<? extends T> acceptableValues) {
            return this.defineInList(ForgeConfigSpec.split(path), defaultValue, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(String path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
            return this.defineInList(ForgeConfigSpec.split(path), defaultSupplier, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(List<String> path, T defaultValue, Collection<? extends T> acceptableValues) {
            return this.defineInList(path, () -> {
                return defaultValue;
            }, acceptableValues);
        }

        public <T> ConfigValue<T> defineInList(List<String> path, Supplier<T> defaultSupplier, Collection<? extends T> acceptableValues) {
            return this.define(path, defaultSupplier, (o) -> {
                return o != null && acceptableValues.contains(o);
            });
        }

        public <T> ConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineList(ForgeConfigSpec.split(path), defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            return this.defineList(ForgeConfigSpec.split(path), defaultSupplier, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineList(path, () -> {
                return defaultValue;
            }, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineList(final List<String> path, Supplier<List<? extends T>> defaultSupplier, final Predicate<Object> elementValidator) {
            this.context.setClazz(List.class);
            return this.define(path, new ValueSpec(defaultSupplier, (x) -> {
                return x instanceof List && ((List)x).stream().allMatch(elementValidator);
            }, this.context, path) {
                public Object correct(Object value) {
                    if (value != null && value instanceof List && !((List)value).isEmpty()) {
                        List<?> list = new ArrayList((List)value);
                        list.removeIf(elementValidator.negate());
                        if (list.isEmpty()) {
                            ForgeConfigSpec.LOGGER.debug(Logging.CORE, "List on key {} is deemed to need correction. It failed validation.", path.get(path.size() - 1));
                            return this.getDefault();
                        } else {
                            return list;
                        }
                    } else {
                        ForgeConfigSpec.LOGGER.debug(Logging.CORE, "List on key {} is deemed to need correction. It is null, not a list, or an empty list. Modders, consider defineListAllowEmpty?", path.get(path.size() - 1));
                        return this.getDefault();
                    }
                }
            }, defaultSupplier);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(ForgeConfigSpec.split(path), defaultValue, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(String path, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(ForgeConfigSpec.split(path), defaultSupplier, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return this.defineListAllowEmpty(path, () -> {
                return defaultValue;
            }, elementValidator);
        }

        public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(final List<String> path, Supplier<List<? extends T>> defaultSupplier, final Predicate<Object> elementValidator) {
            this.context.setClazz(List.class);
            return this.define(path, new ValueSpec(defaultSupplier, (x) -> {
                return x instanceof List && ((List)x).stream().allMatch(elementValidator);
            }, this.context, path) {
                public Object correct(Object value) {
                    if (value != null && value instanceof List) {
                        List<?> list = new ArrayList((List)value);
                        list.removeIf(elementValidator.negate());
                        if (list.isEmpty()) {
                            ForgeConfigSpec.LOGGER.debug(Logging.CORE, "List on key {} is deemed to need correction. It failed validation.", path.get(path.size() - 1));
                            return this.getDefault();
                        } else {
                            return list;
                        }
                    } else {
                        ForgeConfigSpec.LOGGER.debug(Logging.CORE, "List on key {} is deemed to need correction, as it is null or not a list.", path.get(path.size() - 1));
                        return this.getDefault();
                    }
                }
            }, defaultSupplier);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, converter);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue) {
            return this.defineEnum(path, defaultValue, (Enum[])defaultValue.getDeclaringClass().getEnumConstants());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter) {
            return this.defineEnum(path, defaultValue, converter, (Enum[])defaultValue.getDeclaringClass().getEnumConstants());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, V... acceptableValues) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, converter, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, V... acceptableValues) {
            return this.defineEnum((List)path, defaultValue, (Collection)Arrays.asList(acceptableValues));
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, V... acceptableValues) {
            return this.defineEnum((List)path, (Enum)defaultValue, (EnumGetMethod)converter, (Collection)Arrays.asList(acceptableValues));
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, Collection<V> acceptableValues) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, converter, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, Collection<V> acceptableValues) {
            return this.defineEnum(path, defaultValue, EnumGetMethod.NAME_IGNORECASE, acceptableValues);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Collection<V> acceptableValues) {
            return this.defineEnum(path, defaultValue, converter, (obj) -> {
                if (obj instanceof Enum) {
                    return acceptableValues.contains(obj);
                } else if (obj == null) {
                    return false;
                } else {
                    try {
                        return acceptableValues.contains(converter.get(obj, defaultValue.getDeclaringClass()));
                    } catch (ClassCastException | IllegalArgumentException var5) {
                        return false;
                    }
                }
            });
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, Predicate<Object> validator) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, validator);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultValue, converter, validator);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, Predicate<Object> validator) {
            return this.defineEnum(path, () -> {
                return defaultValue;
            }, validator, defaultValue.getDeclaringClass());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, V defaultValue, EnumGetMethod converter, Predicate<Object> validator) {
            return this.defineEnum(path, () -> {
                return defaultValue;
            }, converter, validator, defaultValue.getDeclaringClass());
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultSupplier, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(String path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(ForgeConfigSpec.split(path), defaultSupplier, converter, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, Predicate<Object> validator, Class<V> clazz) {
            return this.defineEnum(path, defaultSupplier, EnumGetMethod.NAME_IGNORECASE, validator, clazz);
        }

        public <V extends Enum<V>> EnumValue<V> defineEnum(List<String> path, Supplier<V> defaultSupplier, EnumGetMethod converter, Predicate<Object> validator, Class<V> clazz) {
            this.context.setClazz(clazz);
            V[] allowedValues = (Enum[])clazz.getEnumConstants();
            Stream var10001 = Arrays.stream(allowedValues).filter(validator).map(Enum::name);
            this.comment("Allowed Values: " + (String)var10001.collect(Collectors.joining(", ")));
            return new EnumValue(this, this.define(path, new ValueSpec(defaultSupplier, validator, this.context, path), defaultSupplier).getPath(), defaultSupplier, converter, clazz);
        }

        public BooleanValue define(String path, boolean defaultValue) {
            return this.define(ForgeConfigSpec.split(path), defaultValue);
        }

        public BooleanValue define(List<String> path, boolean defaultValue) {
            return this.define(path, () -> {
                return defaultValue;
            });
        }

        public BooleanValue define(String path, Supplier<Boolean> defaultSupplier) {
            return this.define(ForgeConfigSpec.split(path), defaultSupplier);
        }

        public BooleanValue define(List<String> path, Supplier<Boolean> defaultSupplier) {
            return new BooleanValue(this, this.define(path, defaultSupplier, (o) -> {
                if (!(o instanceof String)) {
                    return o instanceof Boolean;
                } else {
                    return ((String)o).equalsIgnoreCase("true") || ((String)o).equalsIgnoreCase("false");
                }
            }, Boolean.class).getPath(), defaultSupplier);
        }

        public DoubleValue defineInRange(String path, double defaultValue, double min, double max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultValue, min, max);
        }

        public DoubleValue defineInRange(List<String> path, double defaultValue, double min, double max) {
            return this.defineInRange(path, () -> {
                return defaultValue;
            }, min, max);
        }

        public DoubleValue defineInRange(String path, Supplier<Double> defaultSupplier, double min, double max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultSupplier, min, max);
        }

        public DoubleValue defineInRange(List<String> path, Supplier<Double> defaultSupplier, double min, double max) {
            return new DoubleValue(this, this.defineInRange((List)path, (Supplier)defaultSupplier, min, max, Double.class).getPath(), defaultSupplier);
        }

        public IntValue defineInRange(String path, int defaultValue, int min, int max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultValue, min, max);
        }

        public IntValue defineInRange(List<String> path, int defaultValue, int min, int max) {
            return this.defineInRange(path, () -> {
                return defaultValue;
            }, min, max);
        }

        public IntValue defineInRange(String path, Supplier<Integer> defaultSupplier, int min, int max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultSupplier, min, max);
        }

        public IntValue defineInRange(List<String> path, Supplier<Integer> defaultSupplier, int min, int max) {
            return new IntValue(this, this.defineInRange((List)path, (Supplier)defaultSupplier, min, max, Integer.class).getPath(), defaultSupplier);
        }

        public LongValue defineInRange(String path, long defaultValue, long min, long max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultValue, min, max);
        }

        public LongValue defineInRange(List<String> path, long defaultValue, long min, long max) {
            return this.defineInRange(path, () -> {
                return defaultValue;
            }, min, max);
        }

        public LongValue defineInRange(String path, Supplier<Long> defaultSupplier, long min, long max) {
            return this.defineInRange(ForgeConfigSpec.split(path), defaultSupplier, min, max);
        }

        public LongValue defineInRange(List<String> path, Supplier<Long> defaultSupplier, long min, long max) {
            return new LongValue(this, this.defineInRange((List)path, (Supplier)defaultSupplier, min, max, Long.class).getPath(), defaultSupplier);
        }

        public Builder comment(String comment) {
            this.context.addComment(comment);
            return this;
        }

        public Builder comment(String... comment) {
            for(int i = 0; i < comment.length; ++i) {
                Preconditions.checkNotNull(comment[i], "Comment string at " + i + " is null.");
            }

            String[] var6 = comment;
            int var3 = comment.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String s = var6[var4];
                this.context.addComment(s);
            }

            return this;
        }

        public Builder translation(String translationKey) {
            this.context.setTranslationKey(translationKey);
            return this;
        }

        public Builder worldRestart() {
            this.context.worldRestart();
            return this;
        }

        public Builder push(String path) {
            return this.push(ForgeConfigSpec.split(path));
        }

        public Builder push(List<String> path) {
            this.currentPath.addAll(path);
            if (this.context.hasComment()) {
                this.levelComments.put(new ArrayList(this.currentPath), this.context.buildComment(path));
                this.context.clearComment();
            }

            if (this.context.getTranslationKey() != null) {
                this.levelTranslationKeys.put(new ArrayList(this.currentPath), this.context.getTranslationKey());
                this.context.setTranslationKey((String)null);
            }

            this.context.ensureEmpty();
            return this;
        }

        public Builder pop() {
            return this.pop(1);
        }

        public Builder pop(int count) {
            if (count > this.currentPath.size()) {
                throw new IllegalArgumentException("Attempted to pop " + count + " elements when we only had: " + this.currentPath);
            } else {
                for(int x = 0; x < count; ++x) {
                    this.currentPath.remove(this.currentPath.size() - 1);
                }

                return this;
            }
        }

        public <T> Pair<T, ForgeConfigSpec> configure(Function<Builder, T> consumer) {
            T o = consumer.apply(this);
            return Pair.of(o, this.build());
        }

        public ForgeConfigSpec build() {
            this.context.ensureEmpty();
            Supplier var10000 = Config.getDefaultMapCreator(true, true);
            Objects.requireNonNull(ConfigValue.class);
            Config valueCfg = Config.of(var10000, InMemoryFormat.withSupport(ConfigValue.class::isAssignableFrom));
            this.values.forEach((v) -> {
                valueCfg.set(v.getPath(), v);
            });
            ForgeConfigSpec ret = new ForgeConfigSpec(this.storage, valueCfg, this.levelComments, this.levelTranslationKeys);
            this.values.forEach((v) -> {
                v.spec = ret;
            });
            return ret;
        }

        public interface BuilderConsumer {
            void accept(Builder var1);
        }
    }
}
