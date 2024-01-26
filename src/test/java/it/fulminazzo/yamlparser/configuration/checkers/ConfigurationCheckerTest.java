package it.fulminazzo.yamlparser.configuration.checkers;

import it.fulminazzo.yamlparser.logging.LogMessage;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConfigurationCheckerTest {
    private FileConfiguration configuration1;

    @BeforeEach
    void setUp() {
        configuration1 = loadConfiguration("configcheck-test2.yml");
    }

    private FileConfiguration loadConfiguration(String fileName) {
        File file = new File("build/resources/test/" + fileName);
        if (!file.exists()) {
            try {
                FileUtils.createNewFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new FileConfiguration(file);
    }

    private ConfigurationChecker checkConfiguration(String fileName, String... ignore) {
        return new ConfigurationChecker(configuration1, loadConfiguration(fileName), ignore);
    }

    private static Object[] testValues() {
        return new Object[]{
                new Object[]{"configcheck-test2.yml", true, 0, 0},
                new Object[]{"configcheck-test3.yml", false, 1, 0},
                new Object[]{"configcheck-test4.yml", false, 0, 1},
                new Object[]{"configcheck-test5.yml", false, 2, 1},
                new Object[]{"configcheck-test6.yml", false, 0, 1},
        };
    }

    @ParameterizedTest
    @MethodSource("testValues")
    void testConfigurationParameters(String fileName, boolean empty, int missingKeys, int invalidValues) {
        ConfigurationChecker configurationChecker = checkConfiguration(fileName);
        assertEquals(empty, configurationChecker.isEmpty());
        assertEquals(missingKeys, configurationChecker.getMissingKeys().size());
        assertEquals(invalidValues, configurationChecker.getInvalidValues().size());
    }

    @Test
    void testConfigurationIgnore() {
        ConfigurationChecker configurationChecker = checkConfiguration("configcheck-test5.yml",
                "num", "objects.player.uuid");
        assertFalse(configurationChecker.isEmpty());
        assertEquals(0, configurationChecker.getMissingKeys().size());
        assertEquals(1, configurationChecker.getInvalidValues().size());
    }

    @Test
    void testInvalidKey() {
        ConfigurationChecker configurationChecker = checkConfiguration("configcheck-test4.yml");
        assertEquals("ConfigurationInvalidType {" + "num" + ": " + LogMessage.UNEXPECTED_CLASS.getMessage(
                "%expected%", "Integer", "%received%", "String") + "}",
                configurationChecker.getInvalidValues().get(0).toString());
    }

    @Test
    void testConfigurationCompare() {
        String fileName = "configcheck-test5.yml";
        ConfigurationChecker check1 = checkConfiguration(fileName);
        ConfigurationChecker check2 = configuration1.compare(loadConfiguration(fileName));
        assertEquals(check1, check2);
    }
}