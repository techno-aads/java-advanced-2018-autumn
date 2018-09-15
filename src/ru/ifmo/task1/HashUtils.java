package ru.ifmo.task1;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 */
public class HashUtils {

    public static final int INCORRECT_FILE_HASH = 0;

    private static final int INITIAL_VALUE = 0x811c9dc5;
    private static final int MULTIPLIER = 0x01000193;
    private static final int LOW_BITS = 0xff;


    public static int calculate(String filePath) {
        int hash = INITIAL_VALUE;
        byte[] bytes = new byte[4096];
        int bytesRead;

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            while ((bytesRead = inputStream.read(bytes)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    hash = update(hash, bytes[i]);
                }
            }
            return hash;
        } catch (IOException e){
            return INCORRECT_FILE_HASH;
        }
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}
