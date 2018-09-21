package ru.ifmo.task1;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

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

    public static void main(String[] args) {
        /**
         * todo: проверить аргументы на корректность и передать дальше если всё ок
         * args[0] - входной файл
         * args[1] - выходной файл
         */
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        if (Files.exists(input)) {
            if(!Files.exists(output)) try {
                    Files.createFile(output);
                } catch (IOException e) {
                    System.out.println("Возникла проблема при создании файла назначения");
                }
            new Walker().run(input, output);
        }
        else System.out.println("Файл, содержащий входные данные, не найден");
    }

    //todo: дополнить сигнатуру метода входными файлами, полученными в main
    // При необходимости создайте дополнительные классы
    // hint: Вывести результат хеша в правильном формате - String.format("%08x", hash)
    private void run(Path input, Path output) {
        {
            ArrayList <String> results = new ArrayList<>();
            try {
                for (String startLocation : Files.readAllLines(input, StandardCharsets.UTF_8)) {
                    Path location = Paths.get(startLocation);
                    try {
                        Files.walkFileTree(location, new FileVisitor<>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                results.add(String.format("%08x %s", HashUtils.calculate(file), file.toAbsolutePath()));
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                                results.add(String.format("%08x %s", HashUtils.INCORRECT_FILE_HASH, file.toAbsolutePath()));
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                                results.add(String.format("%08x %s", HashUtils.calculate(dir), dir.toAbsolutePath()));
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (IOException e) {
                        System.out.println("Возникли проблемы при посещении файла "+ e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.out.println("Ошибка при чтении из входного файла");
            }
            try {
                Files.write(output, results, StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Ошибка при попытке внесения информации о хэшах в файл");
            }

        }
    }

}