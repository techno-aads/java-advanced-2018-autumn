package ru.ifmo.task1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 */
public class HashUtils {

    public static final int INCORRECT_FILE_HASH = 0;

    private static final int INITIAL_VALUE = 0x811c9dc5;
    private static final int MULTIPLIER = 0x01000193;
    private static final int LOW_BITS = 0xff;
    private static final int RAM_PAGE_SIZE = 4 * 1024;


    public static int calculate(Path file) throws IOException {
        int hash = INITIAL_VALUE;

        byte[] buffer = new byte[RAM_PAGE_SIZE];
        int toRead;

        try (InputStream inputStream = Files.newInputStream(file)) {
            do {
                toRead = inputStream.read(buffer);
                if (toRead > 0) {
                    for (int i = 0; i < toRead; i++) {
                        hash = update(hash, buffer[i]);
                    }
                }
            } while (toRead != -1);
        } catch (IOException ioe) {
            hash = INCORRECT_FILE_HASH;
        }

        return hash;
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}
