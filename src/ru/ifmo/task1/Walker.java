package ru.ifmo.task1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

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

    public static void main(String[] args) {

        if (args != null && args.length >= 2 && args[0] != null && args[1] != null) {
            try {
                Path in = Paths.get(args[0]);
                Path out = Paths.get(args[1]);

                if (Files.exists(in) && Files.exists(out)) {
                    new Walker().run(in, out);
                } else {
                    System.err.println("Could not find file/files.");
                }
            }catch(InvalidPathException invalidPathException){
                System.err.println("Invalid path.");
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

                for (String directoryName : directoriesToVisit) {
                    Path directory = Paths.get(directoryName);
                    if (Files.isDirectory(directory)) {
                        Files.walkFileTree(directory, fileVisitor);
                    }
                }

            }
        } catch (IOException ioe) {
            System.err.println("IOException with message " + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

}