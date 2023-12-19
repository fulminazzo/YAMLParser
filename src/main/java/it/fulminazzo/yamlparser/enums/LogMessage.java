package it.fulminazzo.yamlparser.enums;


public class LogMessage {
    public static final LogMessage GENERAL_CANNOT_BE_NULL = new LogMessage("%object% cannot be null");

    public static final LogMessage FILE_CREATE_ERROR = new LogMessage("There was an error while creating file \"%file%\"!");
    public static final LogMessage FOLDER_CREATE_ERROR = new LogMessage("There was an error while creating folder \"%folder%\"!");
    public static final LogMessage FILE_RENAME_ERROR = new LogMessage("There was an error while renaming file \"%file%\"!");
    public static final LogMessage FILE_DELETE_ERROR = new LogMessage("There was an error while deleting file \"%file%\"!");
    public static final LogMessage FOLDER_DELETE_ERROR = new LogMessage("There was an error while deleting folder \"%folder%\"!");

    public static final LogMessage YAML_ERROR = new LogMessage("Error found at \"%path%%name%\" for object \"%object%\": %message%");
    public static final LogMessage UNEXPECTED_CLASS = new LogMessage("Expected class %expected% but got %received%.");
    public static final LogMessage CANNOT_DECIPHER_EMPTY_ARRAY = new LogMessage("Cannot parse empty array. Type conversion will result in errors");

    private final String message;

    public LogMessage(String message) {
        this.message = message;
    }

    public LogMessage(LogMessage logMessage, String... strings) {
        this.message = logMessage.getMessage(strings);
    }

    /**
     * Returns a message and replaces the given strings
     * in it. For example,
     * getMessage("hello", "world")
     * will convert the string
     * "hello friend!" in "world friend!"
     *
     * @param strings the to-replace replacement pairs of strings
     * @return the final message
     */
    public String getMessage(String... strings) {
        String tmp = message;
        if (strings.length > 1)
            for (int i = 0; i < strings.length; i += 2)
                if (strings[i] != null)
                    tmp = tmp.replace(strings[i], strings[i + 1] == null ? "null" : strings[i + 1]);
        return tmp;
    }
}