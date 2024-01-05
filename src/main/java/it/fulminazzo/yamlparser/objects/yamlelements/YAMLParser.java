package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.exceptions.GeneralCannotBeNullException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A YAMLParser is a class that allows to load and dump
 * custom objects in YAML format. Use this class to create
 * your own parsers.
 *
 * @param <O> the target object
 */
@Getter
public abstract class YAMLParser<O> {
    private final @NotNull Class<O> oClass;

    /**
     * Instantiates a new YAML parser.
     *
     * @param oClass the o class
     */
    public YAMLParser(@NotNull Class<O> oClass) {
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
    public @Nullable O load(@Nullable IConfiguration section, @Nullable String path) throws Exception {
        if (section == null) return null;
        if (path == null) throw new GeneralCannotBeNullException("Path");
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
    public void dump(@Nullable IConfiguration section, @Nullable String path, @Nullable O o) {
        if (section == null) return;
        if (path == null) throw new GeneralCannotBeNullException("Path");
        if (o == null) section.set(path, null);
        getDumper().accept(section, path, o);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    protected abstract BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable O> getLoader();

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    protected abstract TriConsumer<@NotNull IConfiguration, @NotNull String, @Nullable O> getDumper();

    @Override
    public boolean equals(Object o) {
        if (o instanceof YAMLParser)
            return o.getClass().equals(this.getClass()) && ((YAMLParser<?>) o).getOClass().equals(this.getOClass());
        return super.equals(o);
    }
}