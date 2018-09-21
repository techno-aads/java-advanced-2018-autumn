package ru.ifmo.task1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    private HashMap<String, Integer> hashPathMap = new HashMap<>();
    private HashCalculatorVisitor fileVisitor = new HashCalculatorVisitor(hashPathMap);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Too few arguments");
            return;
        }
        String inputFileName = args[0];
        String outputFileName = args[1];

        new Walker().run(inputFileName, outputFileName);
    }

    private void run(String inputFile, String outputFile) {
        List<Path> directories = getDirectories(inputFile);
        if (directories == null) return;
        iterateDirectories(directories);
        writeResults(outputFile);
    }

    private void writeResults(String outputFile) {
        Iterable<String> lines = hashPathMap.entrySet().stream()
                .map((entry) -> String.format("%08x %s", entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(outputFile), lines);
        } catch (IOException e) {
            System.out.println("Cannot write to output file");
        }
    }

    private void iterateDirectories(List<Path> directories) {
        for (Path directory : directories) {
            walkDirectory(directory);
        }
    }

    private void walkDirectory(Path directory) {
        try {
            Files.walkFileTree(directory, fileVisitor);
        } catch (IOException e) {
            System.out.println("Cannot walk directory " + directory);
        }
    }

    private List<Path> getDirectories(String inputFile) {
        Path path = Paths.get(inputFile);
        if (!path.toFile().exists()) {
            System.out.println("Cannot find input file");
            return null;
        }
        try {
            return Files.lines(path)
                    .map((String it) -> Paths.get(it))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Cannot read from input file");
        }
        return null;
    }
}
