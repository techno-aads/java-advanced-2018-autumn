package ru.ifmo.task1.directory;

import ru.ifmo.task1.utils.HashUtils;
import ru.ifmo.task1.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Watcher implements Runnable {
    private AtomicBoolean hasChanges = new AtomicBoolean(false);

    private File root;
    private File output;

    private Map<String, Integer> hashes = new HashMap<>();
    private WatchKey watchKey;
    private WatchService watchService;

    private Logger logger = new Logger();

    public Watcher(File root, File output) {
        this.root = root;
        this.output = output;
        this.accountHashes(root);
    }

    private void accountHashes(File root) {
        Map<String, Integer> hashes = this.hashes;
        try {
            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        hashes.put(file.toString(), HashUtils.calculate(file.toFile()));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Walk tree problem have been araised: " + e.getMessage());
        }
    }

    public void setWatches(File root) throws IOException {
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            WatchService service = this.watchService;

            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult  preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(
                            service,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY
                    );

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Walk tree problem have been araised: " + e.getMessage());
            this.watchService.close();
            throw e;
        }
    }

    public void run() {
        logger.info("running");
        try {
            do {
                this.watchKey = this.watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    // todo: fixit
                    this.accountHashes(root);
                    this.hasChanges.set(true);

                    logger.info(event.context().toString() + " have been changed");
                }
            }while(watchKey.reset());
        } catch (InterruptedException e) {
            logger.error("the watcher was interrupted");
        }
    }

    public boolean changed() {
        return this.hasChanges.get();
    }

    public void unchange() {
        this.hasChanges.compareAndSet(true, false);
    }

    public Map<String, Integer> getHashes() {
        return hashes;
    }
}
