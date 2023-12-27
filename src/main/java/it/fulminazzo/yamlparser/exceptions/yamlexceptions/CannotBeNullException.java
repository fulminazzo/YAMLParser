package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;
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
