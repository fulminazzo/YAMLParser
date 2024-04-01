package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * List YAML parser.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unchecked")
public class ListYAMLParser<T> extends CollectionYAMLParser<T, List<T>> {

    /**
     * Instantiates a new List YAML parser.
     */
    public ListYAMLParser() {
        super((Class<List<T>>) (Class<?>) List.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, List<T>> getLoader() {
        return (c, s) -> {
            Collection<T> loaded = super.getLoader().apply(c, s);
            return loaded == null ? null : new ArrayList<>(loaded);
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, List<T>> getDumper() {
        return (c, s, o) -> {
            if (o != null && !o.isEmpty()) {
                Object firstNonNull = o.stream().filter(Objects::nonNull).findFirst().orElse(null);
                if (firstNonNull instanceof IConfiguration) {
                    c.toMap().put(s, o);
                    return;
                }
            }
            super.getDumper().accept(c, s, o);
        };
    }
}
