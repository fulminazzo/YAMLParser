package it.fulminazzo.yamlparser.parsers;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.parsers.annotations.PreventSaving;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
    public CallableYAMLParser(@NotNull Class<T> tClass, FunctionException<ConfigurationSection, T> function) {
        super(tClass);
        this.function = function;
    }

    @Override
    protected @NotNull BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable T> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            T t = function.apply(section);
            if (t == null) return null;
            for (Field field : ReflectionUtils.getFields(t)) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (field.isAnnotationPresent(PreventSaving.class)) continue;
                Object object = section.get(FileUtils.formatStringToYaml(field.getName()), field.getType());
                if (object == null) continue;
                field.set(t, object);
            }
            return t;
        };
    }

    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @Nullable T> getDumper() {
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
