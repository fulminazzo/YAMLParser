package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.exceptions.yamlexceptions.EmptyArrayException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;

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
    private final ListYAMLParser<T> listYamlParser;

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
    protected BiFunctionException<IConfiguration, String, T[]> getLoader() {
        return (c, s) -> {
            List<T> tmp = listYamlParser.load(c, s);
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
    protected TriConsumer<IConfiguration, String, T[]> getDumper() {
        return (c, s, o) -> listYamlParser.dump(c, s, Arrays.asList(o));
    }
}