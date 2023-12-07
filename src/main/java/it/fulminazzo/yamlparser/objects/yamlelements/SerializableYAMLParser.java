package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.utils.SerializeUtils;

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
    protected BiFunctionException<IConfiguration, String, Serializable> getLoader() {
        return (c, s) -> SerializeUtils.deserializeFromBase64(c.getString(s));
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
