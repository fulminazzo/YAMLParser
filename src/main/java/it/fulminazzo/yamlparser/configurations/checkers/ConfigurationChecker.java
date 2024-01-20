package it.fulminazzo.yamlparser.configurations.checkers;

import it.fulminazzo.fulmicollection.objects.Printable;

import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.configurations.IConfiguration;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A ConfigurationChecker is a wrapper class
 * responsible for comparing two IConfigurations.
 * It does so in two steps:
 * - first, it checks if the second configuration
 *   has all the entries of the first configuration.
 * - second, it checks if every object of the second
 *   configuration is of the same type as its version
 *   in the first configuration.
 */
@Getter
public class ConfigurationChecker extends Printable {
    private final @NotNull List<String> missingKeys;
    private final @NotNull List<ConfigurationInvalidType> invalidValues;

    public ConfigurationChecker(@NotNull IConfiguration config1, @NotNull IConfiguration config2, String @Nullable ... ignore) {
        List<String> ignoredKeys = ignore == null ? new ArrayList<>() : Arrays.asList(ignore);
        this.missingKeys = new ArrayList<>();
        this.invalidValues = new ArrayList<>();
        Set<String> keys2 = config2.getKeys(true);
        config1.getKeys(true).stream()
                .filter(k -> !keys2.contains(k))
                .filter(k -> ignoredKeys.stream().noneMatch(s -> k.toLowerCase().startsWith(s.toLowerCase())))
                .forEach(missingKeys::add);
        Set<String> values1 = config1.getKeys(true);
        Set<String> values2 = config2.getKeys(true);
        for (String key : values1) {
            if (!values2.contains(key) || ignoredKeys.contains(key)) continue;
            Object obj1 = config1.getObject(key);
            if (obj1 == null) continue;
            Object obj2 = config2.getObject(key);
            if (obj2 == null) continue;
            if (!obj1.getClass().equals(obj2.getClass()))
                invalidValues.add(new ConfigurationInvalidType(key, obj1.getClass(), obj2.getClass()));
            else if (obj1 instanceof Collection && obj2 instanceof Collection) {
                Collection<?> col1 = (Collection<?>) obj1;
                Collection<?> col2 = (Collection<?>) obj2;
                obj1 = col1.stream().filter(Objects::nonNull).findFirst().orElse(null);
                obj2 = col2.stream().filter(Objects::nonNull).findFirst().orElse(null);
                if (obj1 == null) continue;
                if (obj2 == null) {
                    if (ReflectionUtils.isPrimitive(obj1.getClass()))
                        invalidValues.add(new ConfigurationInvalidType(key, obj1.getClass(), null));
                } else if (!obj1.getClass().equals(obj2.getClass()))
                    invalidValues.add(new ConfigurationInvalidType(key, obj1.getClass(), obj2.getClass()));
            }
        }
    }

    /**
     * Check if the second configuration has every
     * entry as the first one.
     *
     * @return true if no missing key is found and
     * every value is valid.
     */
    public boolean isEmpty() {
        return missingKeys.isEmpty() && invalidValues.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConfigurationChecker) {
            return missingKeys.equals(((ConfigurationChecker) o).getMissingKeys()) &&
                    invalidValues.equals(((ConfigurationChecker) o).getInvalidValues());
        }
        return super.equals(o);
    }
}