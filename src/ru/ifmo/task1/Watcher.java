package ru.ifmo.task1;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Watcher {
    File root;
    File output;
    Map<String, Integer> hashes = new HashMap<>();

    public Watcher(File root, File output) {
        this.root = root;
        this.output = output;
        this.run();
    }

    private void run() {
        Map<String, Integer> hashes = this.hashes;
        try {
            Files.walkFileTree(this.root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory() || attrs.isRegularFile()) {
                        hashes.put(file.toString(), HashUtils.calculate(file.toFile()));
                    }
                    // todo: watchers to be set
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {

        }
    }

    public Map<String, Integer> getHashes() {
        return hashes;
    }
}
