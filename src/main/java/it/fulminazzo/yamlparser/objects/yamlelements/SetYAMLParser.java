package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;

import java.util.HashSet;
import java.util.Set;

/**
 * Set YAML parser.
 *
 * @param <T> the type of the set elements
 */
@SuppressWarnings("unchecked")
public class SetYAMLParser<T> extends CollectionYAMLParser<T, Set<T>> {

    public SetYAMLParser() {
        super((Class<Set<T>>) (Class<?>) Set.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, Set<T>> getLoader() {
        return (c, s) -> new HashSet<>(super.getLoader().apply(c, s));
    }
}
