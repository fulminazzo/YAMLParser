package it.fulminazzo.yamlparser.enums;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum LogMessage {
    GENERAL_CANNOT_BE_NULL("%object% cannot be null"),

    FILE_CREATE_ERROR("There was an error while creating file \"%file%\"!"),
    FOLDER_CREATE_ERROR("There was an error while creating folder \"%folder%\"!"),
    FILE_RENAME_ERROR("There was an error while renaming file \"%file%\"!"),
    FILE_DELETE_ERROR("There was an error while deleting file \"%file%\"!"),
    FOLDER_DELETE_ERROR("There was an error while deleting folder \"%folder%\"!"),

    YAML_ERROR("Error found at \"%path%%name%\" for object \"%object%\": %message%"),
    UNEXPECTED_CLASS("Expected class %expected% but got %received%."),
    CANNOT_DECIPHER_EMPTY_ARRAY("Cannot parse empty array. Type conversion will result in errors");

    private final @NotNull String message;

    LogMessage(@NotNull String message) {
        this.message = message;
    }

    /**
     * Returns a message and replaces the given strings
     * in it. For example,
     * getMessage("hello", "world")
     * will convert the string
     * "hello friend!" in "world friend!"
     *
     * @param replacements the to-replace replacement pairs of strings
     * @return the final message
     */
    @NotNull
    public String getMessage(@Nullable String @Nullable ... replacements) {
        if (replacements == null) return message;
        String tmp = message;
        if (replacements.length > 1)
            for (int i = 0; i < replacements.length - 1; i += 2) {
                String from = replacements[i];
                if (from == null) continue;
                if (!from.startsWith("%")) from = "%" + from;
                if (!from.endsWith("%")) from = from + "%";
                String to = replacements[i + 1];
                if (to == null) to = "null";
                tmp = tmp.replace(from, to);
            }
        return tmp;
    }
}