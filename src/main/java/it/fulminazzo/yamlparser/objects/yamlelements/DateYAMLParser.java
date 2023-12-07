package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.interfaces.functions.TriConsumer;

import java.util.Date;

/**
 * Date YAML parser.
 */
public class DateYAMLParser extends YAMLParser<Date> {

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
        return (c, s) -> new Date(c.getLong(s));
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Date> getDumper() {
        return (c, s, d) -> c.set(s, d.getTime());
    }
}
