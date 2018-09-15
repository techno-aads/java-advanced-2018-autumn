package ru.ifmo.task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

public class Logger {
    private File file;
    private boolean consoleOutput = false;
    private final BufferedWriter out;

    public Logger() {
        this.consoleOutput = true;
        this.out = null;
        System.out.println("Logger have been initialised");
    }

    public Logger(String fileName) throws IOException {
        this(fileName, false);
    }

    public Logger(String fileName, boolean consoleOutput) throws IOException {
        this.file = new File(fileName);
        if (this.file.exists() || this.file.isDirectory()) {
            throw new FileNotFoundException("can't log into a missing file");
        }
        if (!this.file.canWrite()) {
            throw new IOException("can't write to a file");
        }

        this.consoleOutput = consoleOutput;

        // WTF?! 1 << 12 doesn't validates
        // regular block size in linux
        final int bufferSize = 4096;
        this.out = new BufferedWriter(new FileWriter(this.file), bufferSize);
        this.info("Logger have been initialised");
    }

    private static final String INFO_PREFIX = "Info: ";
    private static final String WARNING_PREFIX = "!Warning: ";
    private static final String ERROR_PREFIX = "ERROR: ";

    private void print(final String msg) throws IOException {
        if (this.consoleOutput) {
            System.out.println(msg);
        }
        if (this.out == null) {
            return;
        }
        // only in java9
        // try (this.out) {
        try (BufferedWriter bw = this.out) {
            bw.write(msg);
        }
    }

    public void info(final String msg) throws IOException {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = INFO_PREFIX + time + msg;

        print(message);
    }

    public void warning(final String msg) throws IOException {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = WARNING_PREFIX + time + msg;

        print(message);
    }

    public void error(final String msg) throws IOException {
        final String time = "[" + String.valueOf(System.currentTimeMillis()) + "] ";
        String message = ERROR_PREFIX + time + msg;

        print(message);
    }
}