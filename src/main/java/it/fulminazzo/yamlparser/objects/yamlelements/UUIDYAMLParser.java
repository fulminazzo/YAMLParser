package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
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
            if (c == null || s == null) return null;
            String raw = c.getString(s);
            if (raw == null) return null;
            return UUID.fromString(raw);
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull UUID> getDumper() {
        return (c, s, u) -> {
            if (c == null || s == null) return;
            if (u == null) return;
            c.set(s, u.toString());
        };
    }
}