package it.fulminazzo.yamlparser.objects.configurations;

import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import lombok.Getter;

import java.util.Map;

/**
 * Represents a YAML Section.
 */
@Getter
public class ConfigurationSection extends SimpleConfiguration {
    private final IConfiguration parent;

    public ConfigurationSection(IConfiguration parent, String name) {
        this(parent, name, null);
    }

    public ConfigurationSection(IConfiguration parent, String name, Map<?, ?> map) {
        super(name, map);
        this.parent = parent;
        setNonNull(false);
    }

    @Override
    public String toString() {
        return String.format("%s {path: %s, name: %s, root: %s}", getClass().getSimpleName(), getCurrentPath(), getName(), getRoot());
    }
}
