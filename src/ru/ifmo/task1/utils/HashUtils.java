package ru.ifmo.task1.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 */
public class HashUtils {

    public static final int INCORRECT_FILE_HASH = 0;

    private static final int INITIAL_VALUE = 0x811c9dc5;
    private static final int MULTIPLIER = 0x01000193;
    private static final int LOW_BITS = 0xff;

    private static final int BLOCK_SIZE = 4096;

    private static Logger logger = new Logger();

    public static int calculate(File input) {
        int hash = INITIAL_VALUE;

        try (BufferedReader bio = new BufferedReader(new FileReader(input), BLOCK_SIZE)) {
            int bt = 0;
            while (bt != -1) {
                bt = bio.read();
                if (bt != -1) {
                    hash = update(hash, (byte)bt);
                }
            }
        } catch(IOException e) {
            logger.error("It looks like smth have happened during bytes reading");
            return 0;
        }
        return hash;
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}
