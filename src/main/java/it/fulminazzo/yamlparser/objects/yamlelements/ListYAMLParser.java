package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;

import java.util.ArrayList;
import java.util.List;

/**
 * List YAML parser.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unchecked")
public class ListYAMLParser<T> extends CollectionYAMLParser<T, List<T>> {

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
        return (c, s) -> new ArrayList<>(super.getLoader().apply(c, s));
    }
}
