package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.exceptions.yamlexceptions.EmptyArrayException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable T[]> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
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
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull T[]> getDumper() {
        return (c, s, o) -> {
            assert o != null;
            listYamlParser.dump(c, s, Arrays.asList(o));
        };
    }
}