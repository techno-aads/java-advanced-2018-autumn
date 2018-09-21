package ru.ifmo.task1;

import java.io.*;
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
    private static final int SIZE = 4096;

    public static int calculate(Path file) {
        int hash = INITIAL_VALUE;
        try (BufferedReader inReader = new BufferedReader(Files.newBufferedReader(file), SIZE) ){
            while (true){
                byte nextByte = (byte) inReader.read();
                if(nextByte==-1) break;
                    hash=update(hash, nextByte);
                }
        }
        catch (IOException e) {
            return INCORRECT_FILE_HASH;
        }
        return hash;
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}
