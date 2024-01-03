package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enum YAML parser.
 *
 * @param <E> the type parameter
 */
@SuppressWarnings("unchecked")
public class EnumYAMLParser<E extends Enum<E>> extends YAMLParser<E> {

    /**
     * Instantiates a new Enum YAML parser.
     *
     * @param eClass the e class
     */
    public EnumYAMLParser(@NotNull Class<?> eClass) {
        super((Class<E>) eClass);
    }

    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable E> getLoader() {
        return (c, s) -> {
            if (c == null || s == null) return null;
            String enumString = c.getString(s);
            if (enumString == null) return null;
            return E.valueOf(getOClass(), enumString);
        };
    }

    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull E> getDumper() {
        return (c, s, e) -> {
            if (c == null || s == null) return;
            if (e == null) return;
            c.set(s, e.name());
        };
    }
}
