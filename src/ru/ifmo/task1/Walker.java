package ru.ifmo.task1;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.concurrent.TimeUnit;


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

    public static final int SLEEP_TIME = 2;

    private class Data {
        File input;
        File output;
        boolean existes;

        Data(File in, File out, boolean status) {
            this.input = in;
            this.output = out;
            this.existes = status;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException  {
        Walker walker = new Walker();
        Data files = walker.checkArgs(args);
        if (!files.existes) {
            System.out.println("Error: input parguments are invalid [ Walker.main ]");
            System.exit(1);
        }
        walker.run(files.input, files.output);
    }

    /**
     * Check of argumetns are correct set.
     *
     * @param args - input arguments
     * @return Data
     */
    private Data checkArgs(String... args) {
        if (args.length < 2) {
            return new Data(null, null, false);
        }

        File input = new File(args[0]);
        File output = new File(args[1]);

        Function<File, Boolean> checkFile = (f) -> f.exists() && f.isFile() && f.canWrite();

        return new Data(input, output, checkFile.apply(input) && checkFile.apply(output));
    }

    /**
     * Get directories from file.
     *
     * @param input - input File
     * @return List
     * @throws IOExeptions - when error occur with file
     */
    private List<File> getDirsFromFile(File input) throws IOException {
        List<File> dirs = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(input))) {
            String line = in.readLine();
            while (line != null) {
                dirs.add(new File(line));
                line = in.readLine();
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: input file does not found [ Walker.getDirsFromFile ]");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error: crashed while read from input file [ Walker.getDirsFromFile ]");
            System.exit(1);
        }
        return dirs;
    }

    /**
     * Write hashes to output file.
     *
     * @param output - File
     * @param hashes - Map
     * @throws IOException - when error occur with file
     */
    private void writeHashes(File output, Map<String, Integer> hashes) throws IOException {
        Set<String> keys = hashes.keySet();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(output))) {
            for (String key: keys) {
                String line = String.format("%08x", hashes.get(key)) + " " + key;
                out.write(line);
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error occur while writing hashes to the output file [ Walker.writeHashes ]");
            System.exit(1);
        }
    }

    // hint: Вывести результат хеша в правильном формате - String.format("%08x", hash)
    private void run(File input, File output) throws IOException, InterruptedException {
        List<Viewer> viewers = new ArrayList<>();
        for(File f: getDirsFromFile(input)) {
            if(f.isDirectory()) {
                viewers.add(new Viewer(f));
            }
        }
        boolean changed = false;
        final Map<String, Integer> newHashes = new HashMap<>();
        while (true) {
            changed = false;
            newHashes.clear();
            for (Viewer v: viewers) {
                 if (v.analyze() && !changed) {
                     changed = true;
                 }
                newHashes.putAll(v.getHashes());
            }
            if (changed) {
                writeHashes(output, newHashes);
            }
            // Pause for 2 seconds
            TimeUnit.SECONDS.sleep(this.SLEEP_TIME);
        }

    }
}