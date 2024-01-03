package it.fulminazzo.yamlparser.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileUtilsTest {
    private File file;
    
    @BeforeEach
    void setUp() {
        file = new File("build/resources/test/fileutils-test1.txt");
    }

    private static Object[] getWriteValues() {
        return new Object[]{
                "Another test\nTo check for FileUtils",
                "Another test\nTo check for FileUtils".getBytes(),
                new ByteArrayInputStream("Another test\nTo check for FileUtils".getBytes()),
        };
    }

    @Test
    void testReadString() throws IOException {
        assertEquals("Simple test\nTo check for FileUtils", FileUtils.readFileToString(file));
    }

    @Test
    void testReadBytes() throws IOException {
        assertArrayEquals("Simple test\nTo check for FileUtils".getBytes(), FileUtils.readFile(file));
    }

    @Test
    void testReadBytesNonExistent() throws IOException {
        File file = new File(this.file.getParent(), "fileutils-test10.txt");
        assertNull(FileUtils.readFile(file));
    }

    @ParameterizedTest
    @MethodSource(value = "getWriteValues")
    void testWrite(Object toWrite) throws IOException {
        File file = new File(this.file.getParent(), "fileutils-test2.txt");
        if (toWrite instanceof String) FileUtils.writeToFile(file, (String) toWrite);
        else if (toWrite instanceof byte[]) FileUtils.writeToFile(file, (byte[]) toWrite);
        else if (toWrite instanceof InputStream) FileUtils.writeToFile(file, (InputStream) toWrite);
        else return;
        assertEquals("Another test\nTo check for FileUtils", FileUtils.readFileToString(file));
    }

    @Test
    void testReadHugeFileSize() throws IOException {
        FileInputStream inputStream = mock(FileInputStream.class);
        when(inputStream.available()).thenReturn((int) Runtime.getRuntime().freeMemory());
        assertThrowsExactly(OutOfMemoryError.class, () -> FileUtils.readFile(inputStream));
        inputStream.close();
    }

    @Test
    void testCreateFolder() throws IOException {
        File file = new File(this.file.getParent(), "test/folder");
        FileUtils.createFolder(file);
        assertTrue(file.isDirectory());
    }

    @Test
    void testDeleteFolder() throws IOException {
        File file = new File(this.file.getParent(), "test");
        if (!file.isDirectory()) FileUtils.createFolder(file);
        FileUtils.deleteFolder(file);
        assertFalse(file.isDirectory());
    }

    @Test
    void testCopy() throws IOException {
        File file2 = new File(file.getParent(), "fileutils-test3.txt");
        FileUtils.copyFile(file, file2);
        assertEquals(FileUtils.readFileToString(file), FileUtils.readFileToString(file2));
    }

    @Test
    void testRename() throws IOException {
        File file = new File(this.file.getParent(), "fileutils-test3.txt");
        File file2 = new File(this.file.getParent(), "fileutils-test4.txt");
        if (file2.exists()) FileUtils.deleteFile(file2);
        FileUtils.renameFile(file, file2);
        assertFalse(file.isFile());
        assertTrue(file2.isFile());
    }

    @Test
    void testDelete() throws IOException {
        File file = new File(this.file.getParent(), "fileutils-test5.txt");
        if (file.exists()) FileUtils.deleteFile(file);
        assertFalse(file.isFile());
    }

    @Test
    void testCompareEquals() {
        assertTrue(FileUtils.compareTwoFiles(file, file));
    }

    @Test
    void testCompareNotEquals() {
        File file2 = new File(this.file.getParent(), "fileutils-test2.txt");
        assertFalse(FileUtils.compareTwoFiles(file, file2));
    }

    @Test
    void testCompareNotExisting() {
        File file2 = new File(this.file.getParent(), "fileutils-test20.txt");
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
    void testFormatStringToYaml(String string, String expected) {
        assertEquals(expected, FileUtils.formatStringToYaml(string));
    }
}