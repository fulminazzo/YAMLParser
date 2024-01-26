package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enum YAML parser.
 *
 * @param <E> the type parameter
 */
@SuppressWarnings("unchecked")
public class EnumYAMLParser<E extends Enum<E>> extends YAMLParser<E> {

    /**
     * Instantiates a new Enum YAML parser.
     *
     * @param eClass the e class
     */
    public EnumYAMLParser(@NotNull Class<?> eClass) {
        super((Class<E>) eClass);
    }

    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable E> getLoader() {
        return (c, s) -> {
            String enumString = c.getString(s);
            return E.valueOf(getOClass(), enumString);
        };
    }

    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @Nullable E> getDumper() {
        return (c, s, e) -> c.set(s, e == null ? null : e.name());
    }
}
