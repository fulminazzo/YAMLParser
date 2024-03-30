package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.yamlparser.parsers.exceptions.EmptyArrayException;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Array YAML parser.
 *
 * @param <T> the type of the array elements
 */
@SuppressWarnings("unchecked")
public class ArrayYAMLParser<T> extends YAMLParser<T[]> {
    private final @NotNull ListYAMLParser<T> listYamlParser;

    /**
     * Instantiates a new Array YAML parser.
     */
    public ArrayYAMLParser() {
        super((Class<T[]>) ((Class<?>) Object[].class));
        this.listYamlParser = new ListYAMLParser<>();
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected @NotNull BiFunctionException<IConfiguration, String, T[]> getLoader() {
        return (c, s) -> {
            List<T> tmp = listYamlParser.load(c, s);
            if (tmp == null) return null;
            T elem = tmp.stream().filter(Objects::nonNull).findAny().orElse(null);
            if (elem == null) throw new EmptyArrayException(String.join(".", c.parseSectionPath(s)), c.getNameFromPath(s), tmp);
            T[] t = (T[]) Array.newInstance(elem.getClass(), tmp.size());
            for (int i = 0; i < t.length; i++) t[i] = tmp.get(i);
            return t;
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected @NotNull TriConsumer<IConfiguration, String, T[]> getDumper() {
        return (c, s, o) -> {
            assert o != null;
            listYamlParser.dump(c, s, Arrays.asList(o));
        };
    }
}