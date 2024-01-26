package it.fulminazzo.yamlparser.configuration;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Simple YAML Configuration.
 */
public class SimpleConfiguration implements IConfiguration {
    @Getter
    protected final String name;
    protected final @NotNull Map<String, Object> map;
    protected boolean nonNull;

    public SimpleConfiguration() {
        this("", new LinkedHashMap<>());
    }

    public SimpleConfiguration(String name, Map<?, ?> map) {
        this.name = name;
        this.map = IConfiguration.generalToConfigMap(this, map);
        setNonNull(false);
    }

    /**
     * Converts the current configuration to a map.
     *
     * @return the map
     */
    @Override
    public @NotNull Map<String, Object> toMap() {
        return map;
    }

    /**
     * Sets the nullability of the configuration.
     * If set to true, the plugin will not accept null objects
     * when calling get methods.
     *
     * @param nonNull the non-null boolean
     */
    @Override
    public void setNonNull(boolean nonNull) {
        this.nonNull = nonNull;
        IConfiguration.super.setNonNull(nonNull);
    }

    /**
     * Checks the nullability of the section.
     *
     * @return true if nullability is not allowed
     */
    @Override
    public boolean checkNonNull() {
        return nonNull;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    @Override
    public @Nullable IConfiguration getParent() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleConfiguration) {
            return Objects.equals(name, ((SimpleConfiguration) o).getName()) &&
                    nonNull == ((SimpleConfiguration) o).checkNonNull() &&
                    map.equals(((SimpleConfiguration) o).map);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.format("%s {name: %s}", getClass().getSimpleName(), name == null ? "null" : getName());
    }
}
