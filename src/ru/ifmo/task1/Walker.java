package ru.ifmo.task1;


import ru.ifmo.task1.directory.Watcher;
import ru.ifmo.task1.utils.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 */
public class Walker {
    private static final int BLOCK_SIZE = 4 * 1024;

    private static Logger logger = new Logger();

    private class ParseArgsResult {
        File input;
        File output;
        boolean parseOk;

        ParseArgsResult(File input, File output, boolean parseOk) {
            this.input = input;
            this.output = output;
            this.parseOk = parseOk;
        }
    }

    private ParseArgsResult parseArgs(String... args) {
        if (args.length < 2) {
            logger.error("not enough parameters have been passed");
            return new ParseArgsResult(null, null, false);
        }

        File first = new File(args[0]);
        File second = new File(args[1]);

        boolean created = true;
        try {
            if (!second.exists()) {
                created = second.createNewFile();
            }
        } catch (IOException e) {
            logger.error("can't create new output file");
            created = false;
        }

        Function<File, Boolean> checkFile = (f) -> f.exists() && f.isFile() && f.canWrite();

        return new ParseArgsResult(first, second,
                checkFile.apply(first) && checkFile.apply(second) && created);
    }

    public static void main(String... args) {
        Walker w = new Walker();

        ParseArgsResult res = w.parseArgs(args);
        if (!res.parseOk) {
            logger.error("a error accrued while parsing arguments");
            return;
        }

        w.run(res.input, res.output);
    }

    private List<File> getObservingDirs(File input) {
        List<File> dirs = new ArrayList<>();
        try (BufferedReader bio = new BufferedReader(new FileReader(input), BLOCK_SIZE)) {
            String line = bio.readLine();
            while (line != null) {
                dirs.add(new File(line));
                line = bio.readLine();
            }
        } catch (FileNotFoundException e) {
            logger.error("Can't find the input file");
            return new ArrayList<>();
        } catch (IOException e) {
            logger.error("Can't read from an input file");
            return new ArrayList<>();
        }

        return dirs;
    }

    private static void writeHashes(File output, Map<String, Integer> hashes) throws IOException {
        logger.info("writing new hashes");

        Set<String> keys = hashes.keySet();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(output), BLOCK_SIZE)) {
            for (String key: keys) {
                String line = String.format("%08x", hashes.get(key)) + " " + key;
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        }
    }

    private final static class HashUpdater {
        final static Map<String, Integer> hashes = new HashMap<>();

        private static void updateHashes(List<Watcher> watchers) {
            boolean changed = false;
            while (!changed) {
                for (Watcher w : watchers) {
                    boolean c = w.changed();
                    if (c) {
                        changed = true;
                        w.unchange();
                        logger.info("found change");
                        break;
                    }
                }
            }

            final Map<String, Integer> newHashes = new HashMap<>();
            for (Watcher w : watchers) {
                newHashes.putAll(w.getHashes());
            }
            logger.info("checking hashes " + newHashes.keySet().toString());

            logger.info("hashes changed");
            hashes.clear();
            hashes.putAll(newHashes);
        }
    }

    private void run(File input, File output) {
        logger.info("Running");

        List<Watcher> watchers = new ArrayList<>();
        try {
            for (File f : getObservingDirs(input)) {
                if (f.isDirectory()) {
                    Watcher w = new Watcher(f, output);
                    w.setWatches(f);
                    (new Thread(w)).start();
                    watchers.add(w);
                }
            }
        } catch (IOException e) {
            logger.error("a problem accrued while setting watcher: " + e.getMessage());
        }

        // infinite 'cause it was nothing told about it
        while (true)  {
            HashUpdater.updateHashes(watchers);

            try {
                writeHashes(output, HashUpdater.hashes);
            } catch (IOException e) {
                logger.error("there is a problem with updating hashes: " + e.getMessage());
            }
        }
    }
}