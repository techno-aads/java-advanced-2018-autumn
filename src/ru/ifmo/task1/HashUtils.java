package ru.ifmo.task1;

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

    public static int calculate(File input) {
        int hash = INITIAL_VALUE;

        try (BufferedReader in = new BufferedReader(new FileReader(input), BLOCK_SIZE)) {
            int b = 0;
            while (b != -1) {
                b = in.read();
                if (b != -1) {
                    hash = update(hash, (byte)b);
                }
            }
        } catch(IOException e) {
            System.out.println("Error occur on reading bytes of file [ HashUtil.calculate].");
            System.exit(1);
        }
        return hash;
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}