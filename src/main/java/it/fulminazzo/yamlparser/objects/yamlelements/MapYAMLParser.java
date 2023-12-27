package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.utils.SerializeUtils;
import it.fulminazzo.reflectionutils.utils.ReflUtil;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.objects.configurations.ConfigurationSection;

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
public final class MapYAMLParser<K, V> extends YAMLParser<Map<K, V>> {
    private final Function<String, K> keyLoader;
    private final Function<K, String> keyParser;

    public MapYAMLParser() {
        this(s -> (K) s, Object::toString);
    }

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
    protected BiFunctionException<IConfiguration, String, Map<K, V>> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            Class<V> oClass = null;
            String valueClass = SerializeUtils.deserializeFromBase64(section.getString("value-class"));
            if (valueClass != null) oClass = ReflUtil.getClass(valueClass);
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
    protected TriConsumer<IConfiguration, String, Map<K, V>> getDumper() {
        return (c, s, m) -> {
            ConfigurationSection section = c.createSection(s);
            m.forEach((k, v) -> section.set(keyParser.apply(k), v));
            V v = m.values().stream().filter(Objects::nonNull).findFirst().orElse(null);
            if (v == null || IConfiguration.isPrimitiveOrWrapper(v.getClass())) return;
            section.set("value-class", SerializeUtils.serializeToBase64(v.getClass().getCanonicalName()));
        };
    }
}
