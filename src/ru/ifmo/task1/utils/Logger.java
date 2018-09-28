package ru.ifmo.task1.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

public class Logger {
    private boolean consoleOutput;
    private final BufferedWriter out;

    private static final int BLOCK_SIZE = 4 * 1024;

    public Logger() {
        this.consoleOutput = true;
        this.out = null;
        System.out.println("logger have been initialised");
    }

    public Logger(String fileName) throws IOException {
        this(fileName, false);
    }

    private Logger(String fileName, boolean consoleOutput) throws IOException {
        File file = new File(fileName);
        if (file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("can't log into a missing file");
        }
        if (!file.canWrite()) {
            throw new IOException("can't write to a file");
        }

        this.consoleOutput = consoleOutput;

        // WTF?! 1 << 12 doesn't validates
        // regular block size in linux
        this.out = new BufferedWriter(new FileWriter(file), BLOCK_SIZE);
        this.info("logger have been initialised");
    }

    private static final String INFO_PREFIX = "Info: ";
    private static final String WARNING_PREFIX = "!Warning: ";
    private static final String ERROR_PREFIX = "ERROR: ";

    private void print(final String msg) {
        if (this.consoleOutput) {
            System.out.println(msg);
        }
        if (this.out == null) {
            return;
        }
        try (this.out) {
            this.out.write(msg);
        } catch (IOException e) {
            System.out.println("An exception accrued while writing log: " + e.getMessage());
        }
    }

    public void info(final String msg) {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = INFO_PREFIX + time + msg;

        print(message);
    }

    public void warning(final String msg) {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = WARNING_PREFIX + time + msg;

        print(message);
    }

    public void error(final String msg) {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = ERROR_PREFIX + time + msg;

        print(message);
    }
}