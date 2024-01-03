package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception used in ArrayYAMLParser.
 * If the array is empty, because of how Java
 * works, the plugin will not be able to determine
 * the array type. Therefore, this exception will be
 * thrown.
 */
public class EmptyArrayException extends YAMLException {

    public EmptyArrayException(@NotNull String path, @NotNull String name, @Nullable Object object) {
        super(path, name, object, LogMessage.CANNOT_DECIPHER_EMPTY_ARRAY);
    }
}
