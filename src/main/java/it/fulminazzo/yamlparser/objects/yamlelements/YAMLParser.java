package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.interfaces.functions.TriConsumer;
import lombok.Getter;

/**
 * A YAMLParser is a class that allows to load and dump
 * custom objects in YAML format. Use this class to create
 * your own parsers.
 *
 * @param <O> the target object
 */
@Getter
public abstract class YAMLParser<O> {
    private final Class<O> oClass;

    public YAMLParser(Class<O> oClass) {
        this.oClass = oClass;
    }

    /**
     * Loads an object of type O from a YAML section
     * in the given path.
     *
     * @param section the YAML section
     * @param path    the path
     * @return the loaded object
     * @throws Exception the exception that might occur while loading
     */
    public O load(IConfiguration section, String path) throws Exception {
        return getLoader().apply(section, path);
    }

    /**
     * Dumps an object of type O into a YAML section
     * to the given path.
     *
     * @param section the YAML section
     * @param path    the path
     * @param o       the object to dump
     */
    public void dump(IConfiguration section, String path, O o) {
        getDumper().apply(section, path, o);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    protected abstract BiFunctionException<IConfiguration, String, O> getLoader();

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    protected abstract TriConsumer<IConfiguration, String, O> getDumper();
}