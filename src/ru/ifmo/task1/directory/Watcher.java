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
    private File root;

    private AtomicBoolean hasChanges = new AtomicBoolean(false);

    private Map<String, Integer> hashes = new HashMap<>();
    private final WatchService watchService;

    private Logger logger = new Logger();

    public Watcher(File root) throws IOException {
        this.root = root;
        this.accountHashes(root);
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    private void accountHashes(File root) {
        Map<String, Integer> hashes = this.hashes;
        try {
            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
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
        logger.info("setting watcher for " + root.toString());
        try {
            Files.walkFileTree(root.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    dir.register(
                            Watcher.this.watchService,
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
        } catch (ClosedWatchServiceException e) {
            logger.error("The main watcher was closed since it was IOException");
        }
    }

    public void run() {
        logger.info("running");
        try {
            WatchKey watchKey;
            do {
                watchKey = this.watchService.take();
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    // todo: fixit >> lol, forgot what it was
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
