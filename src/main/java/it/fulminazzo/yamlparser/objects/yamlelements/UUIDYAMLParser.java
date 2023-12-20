package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;

import java.util.UUID;

/**
 * UUID YAML parser.
 */
public class UUIDYAMLParser extends YAMLParser<UUID> {

    public UUIDYAMLParser() {
        super(UUID.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, UUID> getLoader() {
        return (c, s) -> UUID.fromString(c.getString(s));
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, UUID> getDumper() {
        return (c, s, u) -> c.set(s, u.toString());
    }
}