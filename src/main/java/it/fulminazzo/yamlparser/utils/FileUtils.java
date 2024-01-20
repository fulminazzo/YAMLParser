package it.fulminazzo.yamlparser.utils;

import it.fulminazzo.yamlparser.logging.LogMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * File utils.
 */
public class FileUtils {
    private static final int FILE_CHUNK = 8192;

    /**
     * Read a file and convert its output to a string.
     *
     * @param input the input
     * @return the string
     * @throws IOException the io exception
     */
    @Nullable
    public static String readFileToString(@NotNull File input) throws IOException {
        byte[] read = readFile(input);
        if (read == null) return null;
        else return new String(read);
    }

    /**
     * Read a file and convert its output to an array of bytes.
     *
     * @param input the input
     * @return the byte @ nullable [ ]
     * @throws IOException the io exception
     */
    public static byte @Nullable [] readFile(@NotNull File input) throws IOException {
        if (!input.exists()) return null;
        return readFile(Files.newInputStream(input.toPath()));
    }

    /**
     * Read an inputstream and convert its output to an array of bytes.
     *
     * @param inputStream the input
     * @return the byte @ nullable [ ]
     * @throws IOException the io exception
     */
    public static byte @Nullable [] readFile(@NotNull InputStream inputStream) throws IOException {
        if (inputStream.available() > Runtime.getRuntime().freeMemory() * 0.5)
            throw new OutOfMemoryError();
        ArrayList<Byte> result = new ArrayList<>();
        while (inputStream.available() > 0) {
            if (inputStream.available() > Runtime.getRuntime().freeMemory() * 0.5)
                throw new OutOfMemoryError();
            byte[] tmp = new byte[Math.min(inputStream.available(), FILE_CHUNK)];
            if (inputStream.read(tmp) == -1) break;
            for (byte t : tmp) result.add(t);
        }
        inputStream.close();
        byte[] finalResult = new byte[result.size()];
        for (int i = 0; i < result.size(); i++) finalResult[i] = result.get(i);
        result.clear();
        return finalResult;
    }

    /**
     * Writes the given string to the specified file.
     *
     * @param output      the file
     * @param string      the string
     * @throws IOException the IO Exception
     */
    public static void writeToFile(@NotNull File output, @NotNull String string) throws IOException {
        writeToFile(output, string.getBytes());
    }

    /**
     * Writes the given array of bytes to the specified file.
     *
     * @param output      the file
     * @param bytes       the array of bytes
     * @throws IOException the IO Exception
     */
    public static void writeToFile(@NotNull File output, byte @NotNull [] bytes) throws IOException {
        writeToFile(output, new ByteArrayInputStream(bytes));
    }

    /**
     * Writes the given input stream to the specified file.
     *
     * @param output      the file
     * @param inputStream the input stream
     * @throws IOException the IO Exception
     */
    public static void writeToFile(@NotNull File output, @NotNull InputStream inputStream) throws IOException {
        if (!output.exists()) createNewFile(output);
        FileOutputStream outputStream = new FileOutputStream(output);
        while (inputStream.available() > 0) {
            byte[] tmp = new byte[Math.min(inputStream.available(), FILE_CHUNK)];
            if (inputStream.read(tmp) == -1) break;
            outputStream.write(tmp);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * Recursively create a file (if the parents do not exist, they will be created).
     *
     * @param file the file
     * @throws IOException the IO Exception
     */
    public static void createNewFile(@NotNull File file) throws IOException {
        if (!file.getParentFile().exists()) createFolder(file.getParentFile());
        if (!file.createNewFile())
            throw new IOException(LogMessage.FILE_CREATE_ERROR.getMessage("%file%", file.getName()));
    }

    /**
     * Recursively create a folder.
     *
     * @param folder the folder
     * @throws IOException the IO Exception
     */
    public static void createFolder(@NotNull File folder) throws IOException {
        File parent = folder.getParentFile();
        if (parent != null && !parent.exists()) createFolder(parent);
        if (!folder.mkdir())
            throw new IOException(LogMessage.FOLDER_CREATE_ERROR.getMessage("%folder%", folder.getAbsolutePath()));
    }

    /**
     * Copy one file to another.
     *
     * @param file1 the file to copy
     * @param file2 the resulting file
     * @throws IOException the IO Exception
     */
    public static void copyFile(@NotNull File file1, @NotNull File file2) throws IOException {
        if (!file1.exists()) return;
        if (!file2.exists()) createNewFile(file2);
        FileInputStream inputStream = new FileInputStream(file1);
        writeToFile(file2, inputStream);
    }

    /**
     * Renames a file.
     *
     * @param fileFrom the file to start from
     * @param fileTo   the result file to be renamed to
     * @throws IOException the IO Exception
     */
    public static void renameFile(@NotNull File fileFrom, @NotNull File fileTo) throws IOException {
        if (!fileFrom.renameTo(fileTo))
            throw new IOException(LogMessage.FILE_RENAME_ERROR.getMessage("%file%", fileFrom.getAbsolutePath()));
    }

    /**
     * Deletes a file.
     *
     * @param file the file to be deleted
     * @throws IOException the IO Exception
     */
    public static void deleteFile(@NotNull File file) throws IOException {
        if (!file.delete())
            throw new IOException(LogMessage.FILE_DELETE_ERROR.getMessage("%file%", file.getAbsolutePath()));
    }

    /**
     * Recursively deletes a folder.
     *
     * @param folder the folder to be deleted
     * @throws IOException the IO Exception
     */
    public static void deleteFolder(@NotNull File folder) throws IOException {
        recursiveDelete(folder);
    }

    /**
     * Deletes all files and folders contained in the specified folder.
     * Then it deletes it as well.
     *
     * @param folder the folder to start with
     * @throws IOException the IO Exception
     */
    private static void recursiveDelete(@NotNull File folder) throws IOException {
        File[] allContents = folder.listFiles();
        if (allContents != null)
            for (File file : allContents)
                if (file.isDirectory()) recursiveDelete(file);
                else deleteFile(file);
        if (!folder.delete())
            throw new IOException(LogMessage.FOLDER_DELETE_ERROR.getMessage("%folder%", folder.getAbsolutePath()));
    }

    /**
     * Compares two files bit by bot.
     *
     * @param file1 the first file
     * @param file2 the second file
     * @return true, if they are identical
     */
    public static boolean compareTwoFiles(@NotNull File file1, @NotNull File file2) {
        if (!file1.exists() || !file2.exists()) return false;
        try (FileInputStream fileInputStream1 = new FileInputStream(file1);
             FileInputStream fileInputStream2 = new FileInputStream(file2)) {
            while (fileInputStream1.available() > 0 && fileInputStream2.available() > 0)
                if (fileInputStream1.read() != fileInputStream2.read()) return false;
            return fileInputStream1.available() == fileInputStream2.available();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Converts a string into kebab-case.
     * For example, if the string is "CamelCase", it will be
     * converted into "kebab-case".
     *
     * @param string the string to convert
     * @return the converted string
     */
    @NotNull
    public static String formatStringToYaml(@NotNull String string) {
        StringBuilder result = new StringBuilder();
        for (String s : string.split("")) {
            if (s.matches("[A-Z \t\n\r_]") && (result.length() > 0) && !result.toString().endsWith("-")) {
                result.append("-");
                if (!s.matches("[A-Z]")) continue;
            } result.append(s.toLowerCase());
        }
        return result.toString();
    }
}