package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Collection YAML parser.
 *
 * @param <T> the type of the collection elements
 * @param <C> the type of the collection
 */
@SuppressWarnings("unchecked")
class CollectionYAMLParser<T, C extends Collection<T>> extends YAMLParser<C> {
    protected final @NotNull MapYAMLParser<Integer, T> mapYamlParser;

    public CollectionYAMLParser(@NotNull Class<C> aClass) {
        super(aClass);
        this.mapYamlParser = new MapYAMLParser<>(Integer::valueOf, Object::toString);
    }

    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable C> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
            if (c.isConfigurationSection(s)) {
                @Nullable Map<Integer, T> map = mapYamlParser.load(c, s);
                return map == null ? null : (C) map.values();
            }
            else return (C) c.getObject(s);
        };
    }

    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull C> getDumper() {
        return (c, s, o) -> {
            if (c == null || s == null) return;
            if (o == null) return;
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