package ru.ifmo.task1;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by Анастасия on 17.09.2018.
 */
public class CustomFileVisitor extends SimpleFileVisitor {

    Path out;

    public CustomFileVisitor(Path out) {
        super();
        this.out = out;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            writeHash(file.toString());
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException ioe) throws IOException {
        writeHash(dir.toString());
        return FileVisitResult.CONTINUE;
    }

    private void writeHash(String path) throws IOException {
        int fileHash = HashUtils.calculate(Paths.get(path));
        Files.write(out, (String.format("%08x", fileHash) + " " + path.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
    }
}
