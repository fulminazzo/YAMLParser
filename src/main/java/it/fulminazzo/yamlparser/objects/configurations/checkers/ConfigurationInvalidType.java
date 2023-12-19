package it.fulminazzo.yamlparser.objects.configurations.checkers;

import it.fulminazzo.yamlparser.enums.LogMessage;
import it.angrybear.exceptions.ClassCannotBeNullException;
import lombok.Getter;

/**
 * A class to identify an invalid type
 * received while comparing two configurations.
 * Check {@link ConfigurationChecker} for more.
 */
@Getter
public class ConfigurationInvalidType {
    private final String entry;
    private final Class<?> expectedType;
    private final Class<?> receivedType;

    public ConfigurationInvalidType(String entry, Class<?> expectedType, Class<?> receivedType) {
        this.entry = entry;
        if (expectedType == null) throw new ClassCannotBeNullException("Expected Type");
        if (receivedType == null) throw new ClassCannotBeNullException("Received Type");
        this.expectedType = expectedType;
        this.receivedType = receivedType;
    }

    @Override
    public String toString() {
        return String.format("%s {%s: %s}", getClass().getSimpleName(), entry,
                LogMessage.UNEXPECTED_CLASS.getMessage(
                        "%expected%", expectedType.getSimpleName(),
                        "%received%", receivedType.getSimpleName()));
    }
}