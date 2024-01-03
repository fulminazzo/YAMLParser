package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.annotations.PreventSaving;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.ConfigurationSection;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joor.Reflect;

import java.lang.reflect.Field;

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
            if (c == null || s == null) return null;
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            T t = function.apply(section);
            if (t == null) return null;
            Reflect tReflect = Reflect.on(t);
            for (Field field : ReflectionUtils.getFields(t)) {
                if (field.isAnnotationPresent(PreventSaving.class)) continue;
                Object object = section.get(FileUtils.formatStringToYaml(field.getName()), field.getType());
                if (object == null) continue;
                tReflect.set(field.getName(), object);
            }
            return t;
        };
    }

    @Override
    protected @NotNull TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull T> getDumper() {
        return (c, s, t) -> {
            if (c == null || s == null) return;
            ConfigurationSection section = c.createSection(s);
            if (t == null) return;
            Reflect tReflect = Reflect.on(t);
            ReflectionUtils.getFields(t).forEach(field -> {
                if (field.isAnnotationPresent(PreventSaving.class)) return;
                String fieldName = field.getName();
                section.set(FileUtils.formatStringToYaml(fieldName), tReflect.get(fieldName));
            });
        };
    }
}
