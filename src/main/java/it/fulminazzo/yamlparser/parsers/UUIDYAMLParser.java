package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.yamlparser.configurations.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable UUID> getLoader() {
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
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @Nullable UUID> getDumper() {
        return (c, s, u) -> c.set(s, u == null ? null : u.toString());
    }
}