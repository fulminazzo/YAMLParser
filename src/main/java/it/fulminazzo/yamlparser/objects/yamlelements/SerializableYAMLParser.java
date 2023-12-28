package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.utils.SerializeUtils;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Serializable YAML parser.
 */
public class SerializableYAMLParser extends YAMLParser<Serializable> {

    public SerializableYAMLParser() {
        super(Serializable.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable Serializable> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
            String string = c.getString(s);
            try {
                return string == null ? null : SerializeUtils.deserializeFromBase64(string);
            } catch (IllegalArgumentException e) {
                return string;
            }
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull Serializable> getDumper() {
        return (c, s, ser) -> {
            if (c == null || s == null) return;
            if (ser == null) return;
            String serialized = SerializeUtils.serializeToBase64(ser);
            if (serialized != null) c.set(s, serialized);
        };
    }
}
