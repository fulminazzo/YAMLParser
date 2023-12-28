package it.fulminazzo.yamlparser.objects.configurations;

import it.fulminazzo.yamlparser.enums.LogMessage;
import it.fulminazzo.yamlparser.exceptions.yamlexceptions.EmptyArrayException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.yamlelements.*;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileConfigurationTest {
    public static final String filePath = "build/resources/test/fileconfig-test1.yml";
    private FileConfiguration configuration;

    @BeforeAll
    public static void setAllUp() throws IOException {
        File file = new File(filePath);
        if (file.exists()) FileUtils.deleteFile(file);
    }

    @BeforeEach
    public void setUp() throws IOException {
        reloadConfiguration();
    }

    private void reloadConfiguration() throws IOException {
        if (configuration != null) configuration.save();
        File file = new File(filePath);
        if (!file.exists()) FileUtils.createNewFile(file);
        configuration = new FileConfiguration(filePath);
    }

    private static Object @NotNull [] getTestValues() {
        return new Object[]{
                "Hello",
                'w',
                10, 10.5f, 10.5d,
                (short) 10, (long) 10,
                (byte) 2, true,
                new Date(),
                UUID.randomUUID(),
                new HashSet<>(Collections.singletonList("Hello world")),
                new ArrayList<>(Collections.singletonList("Hello world")),
                new ArrayList<>(Collections.singletonList("Hello world")),
                new TextMessage("Hello world"),
                Arrays.asList(new TextMessage("Hello"), new TextMessage("world"))
        };
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    @Order(5)
    public void testWriteAndReadEveryType(@NotNull Object expected) throws IOException {
        String path = expected.getClass().getSimpleName().toLowerCase();
        if (expected.getClass().isArray()) path += "-array";
        // Add possibility for an inner object.
        if (new Random().nextInt(10) >= 5) path = "objects." + path;
        configuration.set(path, expected);
        reloadConfiguration();
        Object readObject;
        try {
            Method getObject = IConfiguration.class.getMethod("get" + expected.getClass().getSimpleName(), String.class);
            readObject = getObject.invoke(configuration, path);
        } catch (Exception e) {
            readObject = configuration.get(path, expected.getClass());
        }
        assertEquals(expected, readObject);
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    @Order(5)
    public void testWriteAndReadEveryListType(@NotNull Object expected) throws IOException {
        String path = expected.getClass().getSimpleName().toLowerCase();
        if (expected.getClass().isArray()) path += "-array";
        path = "lists." + path;
        configuration.set(path, Collections.singletonList(expected));
        reloadConfiguration();
        Object readObject;
        try {
            Method getObject = IConfiguration.class.getMethod("get" + expected.getClass().getSimpleName() + "List", String.class);
            readObject = getObject.invoke(configuration, path);
        } catch (Exception e) {
            readObject = configuration.getList(path, expected.getClass());
        }
        assertEquals(new ArrayList<>(Collections.singletonList(expected)), readObject);
    }

    @ParameterizedTest
    @MethodSource("getTestValues")
    @Order(6)
    public void testIsEveryType(@NotNull Object object) {
        String path = object.getClass().getSimpleName().toLowerCase();
        if (object.getClass().isArray()) path += "-array";
        if (!configuration.contains(path)) path = "objects." + path;
        boolean readObject;
        try {
            Method isObject = IConfiguration.class.getMethod("is" + object.getClass().getSimpleName(), String.class);
            readObject = (boolean) isObject.invoke(configuration, path);
        } catch (Exception e) {
            readObject = configuration.is(path, object.getClass());
        }
        assertTrue(readObject);
    }

    @Test
    @Order(6)
    public void testWriteAndReadEnum() throws IOException {
        LogMessage logMessage = LogMessage.UNEXPECTED_CLASS;
        configuration.set("enum", logMessage);
        reloadConfiguration();
        assertEquals(logMessage, configuration.getEnum("enum", LogMessage.class));
    }

    @Test
    @Order(6)
    public void testWriteAndReadEnumList() throws IOException {
        List<LogMessage> list = new ArrayList<>(Collections.singletonList(LogMessage.YAML_ERROR));
        configuration.set("lists.enum", list);
        reloadConfiguration();
        assertEquals(list, configuration.getEnumList("lists.enum", LogMessage.class));
    }

    @Test
    @Order(5)
    public void testWriteAndReadArray() throws IOException {
        String[] array = new String[]{"Welcome", "Friend"};
        configuration.set("string-array", array);
        reloadConfiguration();
        assertArrayEquals(array, configuration.get("string-array", String[].class));
    }

    @Test
    @Order(5)
    public void testWriteAndReadEmptyArray() throws IOException {
        String[] array = new String[0];
        configuration.set("string-array", array);
        reloadConfiguration();
        assertThrowsExactly(EmptyArrayException.class, () ->
                configuration.get("string-array", String[].class));
    }

    @Test
    @Order(5)
    public void testWriteAndReadCallableParser() throws IOException {
        User user = new User(UUID.randomUUID(), "Alex", new Date());
        CallableYAMLParser<User> userYAMLParser = new CallableYAMLParser<>(User.class,
                c -> new User(null, null, null));
        FileConfiguration.addParsers(userYAMLParser);
        configuration.set("user", user);
        reloadConfiguration();
        assertEquals(user, configuration.get("user", User.class));
    }

    @Test
    @Order(10)
    public void testNewConfigurationFromNotExistingFile() {
        assertThrowsExactly(FileNotFoundException.class, () -> {
            try {
                new FileConfiguration("not/existing/file.yml");
            } catch (RuntimeException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    @Order(10)
    public void testNewConfigurationFromInputStream() throws IOException {
        reloadConfiguration();
        File file = new File(filePath);
        InputStream inputStream = Files.newInputStream(file.toPath());
        assertEquals(configuration, new FileConfiguration(inputStream));
    }

    @Test
    @Order(10)
    public void testNewConfigurationFromString() throws IOException {
        reloadConfiguration();
        String contents = FileUtils.readFileToString(new File(filePath));
        assertNotNull(contents);
        assertEquals(configuration, FileConfiguration.fromString(contents));
    }

    @Test
    @Order(1)
    public void testAddParsersFromPackage() {
        FileConfiguration.addParsers();
        assertEquals(new LinkedList<>(Arrays.asList(new ArrayYAMLParser<>(),
                        new DateYAMLParser(),
                        new ListYAMLParser<>(),
                        new SetYAMLParser<>(),
                        new UUIDYAMLParser(),
                        new SerializableYAMLParser()
                )),
                FileConfiguration.getParsers());
    }

    @Test
    @Order(1)
    public void testRemoveParsersFromPackage() {
        FileConfiguration.removeParsers(UUIDYAMLParser.class.getPackage().getName());
        assertEquals(new LinkedList<>(), FileConfiguration.getParsers());
    }
}

class User {
    private final UUID uuid;
    private final String name;
    private final Date registrationDate;

    public User(UUID uuid, String name, Date registrationDate) {
        this.uuid = uuid;
        this.name = name;
        this.registrationDate = registrationDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User)
            return Objects.equals(uuid, ((User) o).getUuid()) &&
                    Objects.equals(name, ((User) o).getName()) &&
                    Objects.equals(registrationDate, ((User) o).getRegistrationDate());
        return super.equals(o);
    }
}

class TextMessage implements Serializable {
    private final String message;

    public TextMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextMessage)
            return message.equals(((TextMessage) o).message);
        return super.equals(o);
    }

    @Override
    public String toString() {
        return String.format("%s {\"%s\"}", getClass().getSimpleName(), message);
    }
}