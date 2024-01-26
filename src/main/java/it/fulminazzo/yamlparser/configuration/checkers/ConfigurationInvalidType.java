package it.fulminazzo.yamlparser.configuration.checkers;

import it.fulminazzo.yamlparser.logging.LogMessage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A class to identify an invalid type
 * received while comparing two configurations.
 * Check {@link ConfigurationChecker} for more.
 */
@Getter
public class ConfigurationInvalidType {
    private final @NotNull String entry;
    private final @NotNull Class<?> expectedType;
    private final @Nullable Class<?> receivedType;

    public ConfigurationInvalidType(@NotNull String entry, @NotNull Class<?> expectedType, @Nullable Class<?> receivedType) {
        this.entry = entry;
        this.expectedType = expectedType;
        this.receivedType = receivedType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConfigurationInvalidType)
            return entry.equalsIgnoreCase(((ConfigurationInvalidType) o).getEntry()) &&
                    expectedType.equals(((ConfigurationInvalidType) o).getExpectedType()) &&
                    Objects.equals(receivedType, ((ConfigurationInvalidType) o).getReceivedType());
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.format("%s {%s: %s}", getClass().getSimpleName(), entry,
                LogMessage.UNEXPECTED_CLASS.getMessage(
                        "%expected%", expectedType.getSimpleName(),
                        "%received%", receivedType == null ? "null" : receivedType.getSimpleName()));
    }
}