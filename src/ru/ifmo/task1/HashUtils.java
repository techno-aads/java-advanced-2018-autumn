package ru.ifmo.task1;

public class HashUtils {

    public static final int INCORRECT_FILE_HASH = 0;

    private static final int INITIAL_VALUE = 0x811c9dc5;
    private static final int MULTIPLIER = 0x01000193;
    private static final int LOW_BITS = 0xff;


    public static int calculate(/* todo */) {
        int hash = INITIAL_VALUE;
        //todo: для каждого байта файла: hash = update(hash, nextByte);
        return hash;
    }

    private static int update(int currentHash, byte nextByte) {
        return (currentHash * MULTIPLIER) ^ (nextByte & LOW_BITS);
    }
}
