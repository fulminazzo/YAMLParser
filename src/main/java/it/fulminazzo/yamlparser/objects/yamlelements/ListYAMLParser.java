package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable List<T>> getLoader() {
        return (c, s) -> {
            Collection<T> loaded = super.getLoader().apply(c, s);
            return new ArrayList<>(loaded);
        };
    }
}
