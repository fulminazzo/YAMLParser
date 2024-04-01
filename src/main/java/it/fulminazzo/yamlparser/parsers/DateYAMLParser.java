package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Date YAML parser.
 */
public class DateYAMLParser extends YAMLParser<Date> {

    /**
     * Instantiates a new Date YAML parser.
     */
    public DateYAMLParser() {
        super(Date.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, Date> getLoader() {
        return (c, s) -> {
            Long l = c.getLong(s);
            return l == null ? null : new Date(l);
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Date> getDumper() {
        return (c, s, d) -> c.set(s, d == null ? null : d.getTime());
    }
}
