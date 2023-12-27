package it.fulminazzo.yamlparser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YAMLMainTest {
    private final PrintStream standardError = System.err;
    private ByteArrayOutputStream tempStandardError;

    @BeforeEach
    void setUp() {
        tempStandardError = new ByteArrayOutputStream();
        System.setErr(new PrintStream(tempStandardError));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardError);
        try {
            tempStandardError.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMain() {
        YAMLMain.main(new String[0]);
        assertEquals("Invalid use of YAMLParser!\n" +
                        "\n" +
                        "This file is a library created to interact with YAML files.\n" +
                        "As such, it should not be used as a standalone runnable file, " +
                        "but it should be imported in your project.\n" +
                        "\n" +
                        "If you do not know where to start, check out the following links:\n" +
                        "https://github.com/Fulminazzo/YAMLParser (Official Documentation)\n",
                tempStandardError.toString());
    }

}