package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;
import lombok.Getter;

/**
 * A general exception that occurs many times
 * while working with IConfiguration instances.
 */
@Getter
public abstract class YAMLException extends RuntimeException {
    private final String path;
    private final String name;
    private final Object object;

    public YAMLException(String path, String name, Object object,
                         LogMessage message, String... strings) {
        this(path, name, object, message.getMessage(strings));
    }

    public YAMLException(String path, String name, Object object, String message) {
        super(LogMessage.YAML_ERROR.getMessage(
                "%path%", path.isEmpty() ? "" : path + ".", "%name%", name,
                "%object%", object == null ? null : object.toString(), "%message%", message
        ));
        this.path = path;
        this.name = name;
        this.object = object;
    }

}
