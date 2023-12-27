package it.fulminazzo.yamlparser.enums;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogMessageTest {
    private static String rawLogMessage;
    private static LogMessage logMessage;

    @BeforeAll
    public static void setup() {
        rawLogMessage = "Error found at \"%path%%name%\" for object \"%object%\": %message%";
        logMessage = LogMessage.YAML_ERROR;
    }

    private static Object[] getTestValues() {
        return new Object[]{
                new Object[]{new String[]{"%path%", "person."},
                        rawLogMessage
                                .replace("%path%", "person.")
                },
                new Object[]{new String[]{"%path%", "person.", "Expected class String but got Integer"},
                        rawLogMessage.replace("%path%", "person.")},
                new Object[]{new String[]{"%path%", "person.", "name", "name", "%object%", null, "message", "Expected class String but got Integer"},
                        rawLogMessage
                                .replace("%path%", "person.")
                                .replace("%name%", "name")
                                .replace("%object%", "null")
                                .replace("%message%", "Expected class String but got Integer")
                },
                new Object[]{new String[]{"%path", "person.", "name%", "name", "%objec", "10", null, "Expected class String but got Integer"},
                        rawLogMessage
                                .replace("%path%", "person.")
                                .replace("%name%", "name")
                },
                new Object[]{null, rawLogMessage},
                new Object[]{new String[0], rawLogMessage},
                new Object[]{new String[]{null, null, null, null, null, null}, rawLogMessage},
                new Object[]{new String[6], rawLogMessage},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    public void testVariousParameters(String[] parameters, String expected) {
        assertEquals(expected, logMessage.getMessage(parameters));
    }

}