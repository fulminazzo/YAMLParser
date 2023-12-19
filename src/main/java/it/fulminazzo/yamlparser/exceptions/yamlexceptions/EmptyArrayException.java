package it.fulminazzo.yamlparser.exceptions.yamlexceptions;

import it.fulminazzo.yamlparser.enums.LogMessage;

/**
 * Exception used in ArrayYAMLParser.
 * If the array is empty, because of how Java
 * works, the plugin will not be able to determine
 * the array type. Therefore, this exception will be
 * thrown.
 */
public class EmptyArrayException extends YAMLException {
    public EmptyArrayException(String path, String name, Object object) {
        super(path, name, object, LogMessage.CANNOT_DECIPHER_EMPTY_ARRAY);
    }
}
