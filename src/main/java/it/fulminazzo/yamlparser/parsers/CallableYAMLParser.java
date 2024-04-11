package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.yamlparser.parsers.annotations.PreventSaving;
import it.fulminazzo.yamlparser.utils.FileUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Callable YAML parser.
 *
 * @param <T> the type of the returned object
 */
public class CallableYAMLParser<T> extends YAMLParser<T> {
    private final FunctionException<ConfigurationSection, T> function;

    /**
     * Instantiates a new Callable YAML parser.
     *
     * @param tClass   the t class
     * @param function the function
     */
    public CallableYAMLParser(Class<T> tClass, FunctionException<ConfigurationSection, T> function) {
        super(tClass);
        this.function = function;
    }

    @Override
    protected BiFunctionException<IConfiguration, String, T> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            T t = function.apply(section);
            if (t == null) return null;
            for (Field field : ReflectionUtils.getFields(t)) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(PreventSaving.class)) continue;
                final String path = FileUtils.formatStringToYaml(field.getName());
                final Class<?> fieldType = field.getType();
                final Object object;
                if (List.class.isAssignableFrom(fieldType)) object = section.getObjectList(path);
                else object = section.get(path, fieldType);
                if (object == null) continue;
                field.set(t, object);
            }
            return t;
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, T> getDumper() {
        return (c, s, t) -> {
            c.set(s, null);
            if (t == null) return;
            ConfigurationSection section = c.createSection(s);
            ReflectionUtils.getFields(t).forEach(field -> {
                if (Modifier.isStatic(field.getModifiers())) return;
                if (field.isAnnotationPresent(PreventSaving.class)) return;
                String fieldName = field.getName();
                try {
                    section.set(FileUtils.formatStringToYaml(fieldName), field.get(t));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        };
    }
}
