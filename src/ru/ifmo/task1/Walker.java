package ru.ifmo.task1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 * <p>
 * При выполнение задания следует обратить внимание на
 * java.nio.file.Files и java.nio.file.Paths,
 * а также корректность использование try-with-resources.
 * <p>
 * https://docs.oracle.com/javase/tutorial/essential/io/index.html
 */
public class Walker {
    static Map<WatchKey, Path> keyPathMap = new HashMap<>();


    public static void main(String[] args) {

        if (args[0] != null && args[1] != null) {
            Path in = Paths.get(args[0]);
            Path out = Paths.get(args[1]);
            if (Files.exists(in) && Files.exists(out)) {
                new Walker().run(in, out);
            } else {
                System.err.println("Could not find file/files.");
            }
        } else {
            System.err.println("Invalid arguments.");
        }
        System.exit(1);
    }

    private void run(Path in, Path out) {
        try {
            if (Files.isReadable(in)) {
                List<String> directoriesToVisit = Files.readAllLines(in, Charset.forName("UTF-8"));
                CustomFileVisitor fileVisitor = new CustomFileVisitor(out);
                WatchService watcher = FileSystems.getDefault().newWatchService();
                WatchKey watchKey;

                for (String directoryName : directoriesToVisit) {
                    Path directory = Paths.get(directoryName);
                    if (Files.isDirectory(directory)) {
                        Files.walkFileTree(directory, fileVisitor);

                        registerDir(directory, watcher);
                    }
                }

                while (true) {
                    WatchKey queuedKey = watcher.take();
                    for (WatchEvent<?> event : queuedKey.pollEvents()) {
                        String eventKind = event.kind().name();
                        Path path = keyPathMap.get(queuedKey).resolve((Path) event.context());
                        if (Files.isRegularFile(path) || Files.isDirectory(path)) {
                            switch (eventKind) {
                                case "ENTRY_MODIFY":
                                    updateHash(path.toString(), HashUtils.calculate(path), out);
                                    break;
                                case "ENTRY_CREATE":
                                case "ENTRY_DELETE":
                            }
                        }
                    }
                    if (!(queuedKey.reset())) {
                        break;
                    }
                }

            }
        } catch (IOException ioe) {
            System.err.println("IOException with message " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            System.err.println("InterruptedException with message " + ie.getMessage());

        }
    }

    private static void registerDir(Path path, WatchService watchService) throws IOException {
        WatchKey watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keyPathMap.put(watchKey, path);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return Files.isDirectory(entry);
            }
        })) {
            for (Path dir : stream) {
                registerDir(dir, watchService);
            }
        }
    }

    private static void updateHash(String path, int newHash, Path fileToWrite) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(fileToWrite.toString(), "rw")) {

            String line;
            while ((line = file.readLine()) != null) {

                if (line.contains(path)) {
                    long filePointer = file.getFilePointer();
                    file.seek(filePointer - line.getBytes().length - 1);
                    file.writeBytes(String.format("%08x", newHash));

                    break;
                }

            }

        }
    }
}