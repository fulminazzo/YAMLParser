package it.fulminazzo.yamlparser.configuration.exceptions;

import it.fulminazzo.yamlparser.logging.LogMessage;
import it.fulminazzo.yamlparser.exceptions.YAMLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception thrown when an object from
 * an IConfiguration is null and nullability
 * is not allowed.
 */
public class CannotBeNullException extends YAMLException {

    public CannotBeNullException(@NotNull String path, @NotNull String name, @Nullable String objectName) {
        super(path, name, null, LogMessage.GENERAL_CANNOT_BE_NULL, "%object%", objectName);
    }
}
