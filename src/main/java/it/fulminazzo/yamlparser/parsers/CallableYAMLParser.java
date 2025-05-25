package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.fulmicollection.structures.tuples.Singlet;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.yamlparser.parsers.annotations.PreventSaving;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * Callable YAML parser.
 *
 * @param <T> the type of the returned object
 */
public class CallableYAMLParser<T> extends YAMLParser<T> {
    private final FunctionException<ConfigurationSection, T, Exception> function;

    /**
     * Instantiates a new Callable YAML parser.
     *
     * @param tClass   the t class
     * @param function the function
     */
    public CallableYAMLParser(Class<T> tClass, FunctionException<ConfigurationSection, T, Exception> function) {
        super(tClass);
        this.function = function;
    }

    @Override
    protected BiFunctionException<IConfiguration, String, T, Exception> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            final T t = function.apply(section);
            if (t == null) return null;
            objectFields(t).forEach(f -> {
                if (Modifier.isStatic(f.getModifiers())) return;
                if (f.isAnnotationPresent(PreventSaving.class)) return;
                Object object = section.get(FileUtils.formatStringToYaml(f.getName()), f.getType());
                if (object == null) return;
                try {
                    f.set(t, object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return t;
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, T> getDumper() {
        return (c, s, t) -> {
            c.set(s, null);
            if (t == null) return;
            ConfigurationSection section = c.createSection(s);
            objectFields(t).forEach(f -> {
                if (Modifier.isStatic(f.getModifiers())) return;
                if (f.isAnnotationPresent(PreventSaving.class)) return;
                String fieldName = f.getName();
                try {
                    section.set(FileUtils.formatStringToYaml(fieldName), f.get(t));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }

    private static <T> @NotNull Stream<Field> objectFields(final @NotNull T t) {
        return ReflectionUtils.getFields(t).stream()
                .map(ReflectionUtils::setAccessible)
                .filter(f -> f.isPresent())
                .map(Singlet::getValue);
    }
}
