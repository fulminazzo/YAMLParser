package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This exception occurs when loading an object
 * from an IConfiguration, but the result does not
 * correspond to the expected type.
 */
public class UnexpectedClassException extends YAMLException {

    public UnexpectedClassException(@NotNull String path, @NotNull String name, @Nullable Object object, @NotNull Class<?> expected) {
        super(path, name, object, LogMessage.UNEXPECTED_CLASS.getMessage(
                "%expected%", expected.getCanonicalName(),
                "%received%", object == null ? "null" : object.getClass().getCanonicalName()
        ));
    }
}
