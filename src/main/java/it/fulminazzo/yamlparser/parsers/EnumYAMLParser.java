package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import org.jetbrains.annotations.NotNull;

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
    public EnumYAMLParser(Class<?> eClass) {
        super((Class<E>) eClass);
    }

    @Override
    protected BiFunctionException<IConfiguration, String, E, Exception> getLoader() {
        return (c, s) -> {
            String enumString = c.getString(s);
            return enumString == null ? null : E.valueOf(getOClass(), enumString.toUpperCase());
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, E> getDumper() {
        return (c, s, e) -> c.set(s, e == null ? null : e.name());
    }
}
