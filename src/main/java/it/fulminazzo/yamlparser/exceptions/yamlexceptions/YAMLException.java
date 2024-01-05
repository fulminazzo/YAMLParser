package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A general exception that occurs many times
 * while working with IConfiguration instances.
 */
@Getter
public class YAMLException extends RuntimeException {
    private final @NotNull String path;
    private final @NotNull String name;
    private final @Nullable Object object;

    public YAMLException(@NotNull String path, @NotNull String name, @Nullable Object object,
                         @NotNull LogMessage message, @Nullable String... strings) {
        this(path, name, object, message.getMessage(strings));
    }

    public YAMLException(@NotNull String path, @Nullable Object object, @NotNull String message) {
        this(path.contains(".") ? path.substring(0, path.lastIndexOf(".")) : "",
                path.contains(".") ? path.substring(path.indexOf(".") + 1) : path, object, message);
    }

    public YAMLException(@NotNull String path, @NotNull String name, @Nullable Object object, @NotNull String message) {
        super(LogMessage.YAML_ERROR.getMessage(
                "%path%", path.isEmpty() ? "" : path + ".", "%name%", name,
                "%object%", object == null ? null : object.toString(), "%message%", message
        ));
        this.path = path;
        this.name = name;
        this.object = object;
    }
}
