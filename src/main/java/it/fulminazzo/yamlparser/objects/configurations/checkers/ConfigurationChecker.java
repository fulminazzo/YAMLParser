package it.fulminazzo.yamlparser.objects.configurations.checkers;

import it.angrybear.exceptions.GeneralCannotBeNullException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import lombok.Getter;

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
public class ConfigurationChecker {
    private final List<String> missingKeys;
    private final List<ConfigurationInvalidType> invalidValues;

    public ConfigurationChecker(IConfiguration config1, IConfiguration config2, String... ignore) {
        List<String> ignoredKeys = ignore == null ? new ArrayList<>() : Arrays.asList(ignore);
        this.missingKeys = new ArrayList<>();
        this.invalidValues = new ArrayList<>();
        if (config1 == null) throw new GeneralCannotBeNullException("config1");
        if (config2 == null) throw new GeneralCannotBeNullException("config2");
        Set<String> keys2 = config2.getKeys(true);
        config1.getKeys(true).stream()
                .filter(k -> !keys2.contains(k))
                .filter(k -> ignoredKeys.stream().noneMatch(s -> k.toLowerCase().startsWith(s.toLowerCase())))
                .forEach(missingKeys::add);
        Map<String, Object> values1 = config1.getValues(true);
        Map<String, Object> values2 = config2.getValues(true);
        for (String key : values1.keySet()) {
            if (!values2.containsKey(key) || ignoredKeys.contains(key)) continue;
            Object obj1 = values1.get(key);
            if (obj1 == null) continue;
            Object obj2 = values2.get(key);
            if (obj2 == null) continue;
            if (!obj1.getClass().equals(obj2.getClass()))
                invalidValues.add(new ConfigurationInvalidType(key, obj1.getClass(), obj2.getClass()));
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
}