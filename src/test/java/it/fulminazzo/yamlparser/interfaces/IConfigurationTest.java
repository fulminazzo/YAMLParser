package it.fulminazzo.yamlparser.interfaces;

import it.fulminazzo.yamlparser.objects.configurations.ConfigurationSection;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IConfigurationTest {
    private FileConfiguration configuration;
    private ConfigurationSection mainSection;
    private ConfigurationSection innerSection;

    @BeforeEach
    public void setUp() {
        configuration = new FileConfiguration("build/resources/test/configcheck-test2.yml");
        mainSection = configuration.getConfigurationSection("objects");
        if (mainSection != null) innerSection = mainSection.getConfigurationSection("player");
    }

    private Map<String, Object> createInnerContents() {
        Map<String, Object> innerContents = new LinkedHashMap<>();
        innerContents.put("uuid", "64b8cc63-034f-46e7-b6b0-d1a67f830f76");
        innerContents.put("names", Arrays.asList("Alex", "Fulminazzo", "Luke"));
        return innerContents;
    }

    @Test
    @Order(1)
    public void testGetConfigurationSection() {
        mainSection = configuration.getConfigurationSection("objects");
        Map<Object, Object> contents = new LinkedHashMap<>();
        Map<String, Object> innerContents = createInnerContents();
        contents.put("player", innerContents);
        ConfigurationSection section = new ConfigurationSection(configuration, "objects", contents);
        assertEquals(section, mainSection);
    }

    @Test
    @Order(2)
    public void testCreateInnerConfiguration() {
        ConfigurationSection innerSection = mainSection.createSection("another-player");
        ConfigurationSection section = new ConfigurationSection(mainSection, "another-player");
        assertEquals(section, innerSection);
    }

    @Test
    @Order(2)
    public void testCreateInnerConfigurationFull() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("content1", "Hello");
        map.put("content2", 10);
        ConfigurationSection innerSection = mainSection.createSection("another-player", map);
        ConfigurationSection section = new ConfigurationSection(mainSection, "another-player");
        map.forEach(section::set);
        assertEquals(section, innerSection);
    }

    @Test
    @Order(3)
    public void testGetRoot() {
        assertEquals(configuration, innerSection.getRoot());
    }

    @Test
    @Order(3)
    public void testGetKeys() {
        assertEquals(new HashSet<>(Collections.singletonList("player")), mainSection.getKeys());
    }

    @Test
    @Order(3)
    public void testGetKeysDeep() {
        assertEquals(new HashSet<>(Arrays.asList("player", "player.uuid", "player.names")),
                mainSection.getKeys(true));
    }

    @Test
    @Order(3)
    public void testGetValues() {
        Map<String, Object> map = new HashMap<>();
        assertEquals(map, mainSection.getValues());
    }

    @Test
    @Order(3)
    public void testGetValuesDeep() {
        Map<String, Object> innerContents = createInnerContents();
        assertEquals(new LinkedHashMap<String, Object>(){{put("player", innerContents);}},
                mainSection.getValues(true));
    }

    @Test
    @Order(3)
    public void testContainsValidValue() {
        assertTrue(mainSection.contains("player"));
    }

    @Test
    @Order(3)
    public void testContainsInvalidValue() {
        assertFalse(mainSection.contains("player2"));
    }

    @Test
    @Order(3)
    public void testPrint() {
        final PrintStream standardError = System.err;
        ByteArrayOutputStream tempStandardOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(tempStandardOutput));
        configuration.print();
        System.setOut(standardError);
        try {
            tempStandardOutput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String output = tempStandardOutput.toString();
        assertEquals("FileConfiguration {file: %file%, non-null: false}\n"
                        .replace("%file%", new File("build/resources/test/configcheck-test2.yml").getAbsolutePath()) +
                "string: Hello world\n" +
                "num: 10\n" +
                "numbers: [10, 11, 12]\n" +
                "objects: \n" +
                "  player: \n" +
                "    uuid: 64b8cc63-034f-46e7-b6b0-d1a67f830f76\n" +
                "    names: [Alex, Fulminazzo, Luke]\n\n", output);
    }
}