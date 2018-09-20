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

public class Viewer {
    File root;
    Map<String, Integer> hashes = new HashMap<>();

    public Viewer(File root) {
        this.root = root;
    }

    public boolean analyze() {
        return run();
    }

    public Map<String, Integer> getHashes() {
        return hashes;
    }

    private boolean run() {
        Map<String, Integer> hashes = new HashMap<>();
        try {
            Files.walkFileTree(this.root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isDirectory() || attrs.isRegularFile()) {
                        hashes.put(file.toString(), HashUtils.calculate(file.toFile()));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            System.out.println("Error occur on view files from dir [ Viewer.run ]");
            System.exit(1);
        }
        if (!hashes.equals(this.hashes)) {
            this.hashes = hashes;
            return true;
        }
        return false;
    }
}
