package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * UUID YAML parser.
 */
public class UUIDYAMLParser extends YAMLParser<UUID> {

    /**
     * Instantiates a new UUID YAML parser.
     */
    public UUIDYAMLParser() {
        super(UUID.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, UUID, Exception> getLoader() {
        return (c, s) -> {
            String raw = c.getString(s);
            return UUID.fromString(raw);
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, UUID> getDumper() {
        return (c, s, u) -> c.set(s, u == null ? null : u.toString());
    }
}