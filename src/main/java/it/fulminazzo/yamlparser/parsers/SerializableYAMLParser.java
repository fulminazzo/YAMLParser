package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.utils.SerializeUtils;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Serializable YAML parser.
 */
public class SerializableYAMLParser extends YAMLParser<Serializable> {

    /**
     * Instantiates a new Serializable YAML parser.
     */
    public SerializableYAMLParser() {
        super(Serializable.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunctionException<IConfiguration, String, Serializable, Exception> getLoader() {
        return (c, s) -> {
            try {
                String string = (String) c.getObject(s);
                return string == null ? null : SerializeUtils.deserializeFromBase64(string);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Serializable> getDumper() {
        return (c, s, ser) -> c.set(s, SerializeUtils.serializeToBase64(ser));
    }
}
