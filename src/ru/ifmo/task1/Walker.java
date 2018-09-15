package ru.ifmo.task1;


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
 *
 * При выполнение задания следует обратить внимание на
 *  java.nio.file.Files и java.nio.file.Paths,
 *  а также корректность использование try-with-resources.
 *
 * https://docs.oracle.com/javase/tutorial/essential/io/index.html
 */
public class Walker {
    // it will be better to move common constants into
    // one file, but I didn't do it since it's just a learning project
    private static final int BLOCK_SIZE = 4096;

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

    private ParseArgsResult parseArgs(String... args) throws IOException {
        if (args.length < 2) {
            logger.error("not enough parameters have been passed");
            return new ParseArgsResult(null, null, false);
        }

        File first = new File(args[0]);
        File second = new File(args[1]);

        boolean created = true;
        if (!second.exists()) {
            created = second.createNewFile();
        }

        Function<File, Boolean> checkFile = (f) -> f.exists() && f.isFile() && f.canWrite();

        return new ParseArgsResult(first, second,
                checkFile.apply(first) && checkFile.apply(second) && created);
    }

    public static void main(String... args) throws IOException {
        Walker w = new Walker();

        ParseArgsResult res = w.parseArgs(args);
        if (!res.parseOk) {
            logger.error("a error accrued while parsing arguments");
            return;
        }

        w.run(res.input, res.output);
    }

    private List<File> getObservingDirs(File input) throws IOException {
        List<File> dirs = new ArrayList<>();
        try (BufferedReader bio = new BufferedReader(new FileReader(input), BLOCK_SIZE)) {
            dirs.add(new File(bio.readLine()));
        } catch (FileNotFoundException e) {
            logger.error("Can't find the input file");
            return new ArrayList<>();
        } catch (IOException e) {
            logger.error("Can't read from an input file");
            return new ArrayList<>();
        }

        return dirs;
    }

    private void writeHashes(File output, Map<String, Integer> hashes) throws IOException {
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

    // hint: Вывести результат хеша в правильном формате - String.format("%08x", hash)
    private void run(File input, File output) throws IOException {
        logger.info("Start running");

        List<Watcher> watchers = new ArrayList<>();
        for (File f: getObservingDirs(input)) {
            if (f.isDirectory()) {
                watchers.add(new Watcher(f, output));
            }
        }

        // infine 'cause it was nothing told about it
        final Map<String, Integer> hashes = new HashMap<>();
        while (true) {
            final Map<String, Integer> newHashes = new HashMap<>();
            for (Watcher w: watchers) {
                newHashes.putAll(w.getHashes());
            }

            if (!hashes.equals(newHashes)) {
                hashes.clear();
                hashes.putAll(newHashes);
                writeHashes(output, hashes);
            }
        }
    }
}