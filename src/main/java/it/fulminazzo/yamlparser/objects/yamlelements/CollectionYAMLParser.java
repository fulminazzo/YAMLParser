package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.interfaces.functions.TriConsumer;

import java.util.*;

/**
 * Collection YAML parser.
 *
 * @param <T> the type of the collection elements
 * @param <C> the type of the collection
 */
@SuppressWarnings("unchecked")
public class CollectionYAMLParser<T, C extends Collection<T>> extends YAMLParser<C> {
    protected final MapYAMLParser<Integer, T> mapYamlParser;

    public CollectionYAMLParser() {
        // Don't get fooled! Cast (Class<?>) is required at compilation time!
        this((Class<C>) (Class<?>) Collection.class);
    }

    public CollectionYAMLParser(Class<C> aClass) {
        super(aClass);
        this.mapYamlParser = new MapYAMLParser<>(Integer::valueOf, Object::toString);
    }

    @Override
    protected BiFunctionException<IConfiguration, String, C> getLoader() {
        return (c, s) -> {
            if (c.isConfigurationSection(s)) return (C) mapYamlParser.load(c, s).values();
            else return (C) c.getObject(s);
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, C> getDumper() {
        return (c, s, o) -> {
            List<T> list = new ArrayList<>(o);
            if (IConfiguration.isPrimitiveOrWrapper(list)) {
                c.set(s, list);
                return;
            }
            HashMap<Integer, T> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) map.put(i, list.get(i));
            mapYamlParser.dump(c, s, map);
        };
    }
}