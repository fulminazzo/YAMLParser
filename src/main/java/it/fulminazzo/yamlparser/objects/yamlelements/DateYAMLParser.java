package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable Date> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
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
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull Date> getDumper() {
        return (c, s, d) -> {
            if (c == null || s == null) return;
            if (d == null) return;
            c.set(s, d.getTime());
        };
    }
}
