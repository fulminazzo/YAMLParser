package it.fulminazzo.yamlparser.utils;

import it.fulminazzo.fulmicollection.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileUtilsTest {

    private static Object[] getWriteValues() {
        return new Object[]{
                "Another test\nTo check for FileUtils",
                "Another test\nTo check for FileUtils".getBytes(),
                new ByteArrayInputStream("Another test\nTo check for FileUtils".getBytes()),
        };
    }

    @Test
    public void testReadString() throws IOException {
        File file = new File("build/resources/test/test1.txt");
        assertEquals("Simple test\nTo check for FileUtils", FileUtils.readFileToString(file));
    }

    @Test
    public void testReadBytes() throws IOException {
        File file = new File("build/resources/test/test1.txt");
        assertArrayEquals("Simple test\nTo check for FileUtils".getBytes(), FileUtils.readFile(file));
    }

    @Test
    public void testReadBytesNonExistent() throws IOException {
        File file = new File("build/resources/test/test10.txt");
        assertNull(FileUtils.readFile(file));
    }

    @ParameterizedTest
    @MethodSource(value = "getWriteValues")
    public void testWrite(Object toWrite) throws IOException {
        File file = new File("build/resources/test/test2.txt");
        if (toWrite instanceof String) FileUtils.writeToFile(file, (String) toWrite);
        else if (toWrite instanceof byte[]) FileUtils.writeToFile(file, (byte[]) toWrite);
        else if (toWrite instanceof InputStream) FileUtils.writeToFile(file, (InputStream) toWrite);
        else return;
        assertEquals("Another test\nTo check for FileUtils", FileUtils.readFileToString(file));
    }

    @Test
    public void testReadHugeFileSize() throws IOException {
        FileInputStream inputStream = mock(FileInputStream.class);
        when(inputStream.available()).thenReturn((int) Runtime.getRuntime().freeMemory());
        assertThrowsExactly(OutOfMemoryError.class, () -> FileUtils.readFile(inputStream));
        inputStream.close();
    }

    @Test
    public void testCompareEquals() {
        File file = new File("build/resources/test/test1.txt");
        assertTrue(FileUtils.compareTwoFiles(file, file));
    }

    @Test
    public void testCompareNotEquals() {
        File file = new File("build/resources/test/test1.txt");
        File file2 = new File("build/resources/test/test2.txt");
        assertFalse(FileUtils.compareTwoFiles(file, file2));
    }

    @Test
    public void testCompareNotExisting() {
        File file = new File("build/resources/test/test1.txt");
        File file2 = new File("build/resources/test/test20.txt");
        assertFalse(FileUtils.compareTwoFiles(file, file2));
    }

    @ParameterizedTest
    @CsvSource({"Test String, test-string",
            "testString, test-string",
            "TEST STRING, t-e-s-t-s-t-r-i-n-g",
            "TeST sTriNG, te-s-t-s-tri-n-g",
            "test_STRING, test-s-t-r-i-n-g",
            "test_string, test-string",
            "test-string, test-string",
            "test\tstring, test-string",
    })
    public void testFormatStringToYaml(String string, String expected) {
        assertEquals(expected, FileUtils.formatStringToYaml(string));
    }
}