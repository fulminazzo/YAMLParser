package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.utils.SerializeUtils;
import it.fulminazzo.reflectionutils.utils.ReflUtil;
import it.fulminazzo.yamlparser.exceptions.yamlexceptions.CannotBeNullException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.objects.configurations.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Map YAML parser. This parser should only be used
 * as helper for retrieving other objects.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
@SuppressWarnings("unchecked")
final class MapYAMLParser<K, V> extends YAMLParser<Map<K, V>> {
    private final Function<String, K> keyLoader;
    private final Function<K, String> keyParser;

    public MapYAMLParser(Function<String, K> keyLoader, Function<K, String> keyParser) {
        super((Class<Map<K, V>>) ((Class<?>) Map.class));
        this.keyLoader = keyLoader;
        this.keyParser = keyParser;
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable Map<K, V>> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            Class<V> oClass = null;
            String valueClass = SerializeUtils.deserializeFromBase64(section.getString("value-class"));
            if (valueClass != null) oClass = ReflUtil.getClass(valueClass);
            if (oClass == null) throw new CannotBeNullException(c.getCurrentPath(), "." + s, "value-class");
            HashMap<K, V> map = new HashMap<>();
            for (String k : section.getKeys()) {
                if (k.equalsIgnoreCase("value-class")) continue;
                map.put(keyLoader.apply(k), section.get(k, oClass));
            }
            return map;
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull Map<K, V>> getDumper() {
        return (c, s, m) -> {
            if (c == null || s == null) return;
            if (m == null) return;
            ConfigurationSection section = c.createSection(s);
            m.forEach((k, v) -> section.set(keyParser.apply(k), v));
            V v = m.values().stream().filter(Objects::nonNull).findFirst().orElse(null);
            if (v == null || IConfiguration.isPrimitiveOrWrapper(v.getClass())) return;
            section.set("value-class", SerializeUtils.serializeToBase64(v.getClass().getCanonicalName()));
        };
    }
}
