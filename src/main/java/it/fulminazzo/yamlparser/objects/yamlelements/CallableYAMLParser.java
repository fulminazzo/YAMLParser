package it.fulminazzo.yamlparser.objects.yamlelements;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import it.fulminazzo.yamlparser.annotations.PreventSaving;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.ConfigurationSection;
import it.fulminazzo.yamlparser.utils.FileUtils;

import java.lang.reflect.Field;

/**
 * Callable YAML parser.
 *
 * @param <T> the type of the returned object
 */
public class CallableYAMLParser<T> extends YAMLParser<T> {
    private final FunctionException<ConfigurationSection, T> function;

    /**
     * Instantiates a new Callable yaml parser.
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
            ReflObject<T> tReflObject = new ReflObject<>(t);
            for (Field field : tReflObject.getFields()) {
                if (field.isAnnotationPresent(PreventSaving.class)) continue;
                Object object = section.get(FileUtils.formatStringToYaml(field.getName()), field.getType());
                if (object == null) continue;
                tReflObject.setField(field.getName(), object);
            }
            return t;
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, T> getDumper() {
        return (c, s, t) -> {
            ConfigurationSection section = c.createSection(s);
            if (t == null) return;
            ReflObject<T> tReflObject = new ReflObject<>(t);
            tReflObject.getFields().forEach(field -> {
                if (field.isAnnotationPresent(PreventSaving.class)) return;
                section.set(FileUtils.formatStringToYaml(field.getName()), tReflObject.getFieldObject(field.getName()));
            });
        };
    }
}
