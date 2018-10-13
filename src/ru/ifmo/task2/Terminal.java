package ru.ifmo.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

public class Terminal {

    public static void main(String[] args) {
        Path startLocation = Paths.get(args[0]);
        if (Files.exists(startLocation))
            new Terminal().run(startLocation);
        else System.out.println("Неккоректная стартовая директория");
    }

    private void run(Path startLocation) {
        InsideFile root = null;
        InsideFile current = null;
        try {
            CustomerFileVisitor fileVisitor = new CustomerFileVisitor();
            Files.walkFileTree(startLocation, fileVisitor);
            root = fileVisitor.root;
        } catch (IOException e) {
            System.out.println("Ошибка при обходе файлов в директории");
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                if(current==null)current=root;
                System.out.print(current.file+"> ");
                String[] command = bufferedReader.readLine().split(" ");
                String target;
                if(command.length==0)continue;
                switch (command[0]) {
                    case "cd":
                        if(command.length!=2){
                            System.out.println("Неверные входные параметры команды");
                            continue;
                        }
                        target = Paths.get(current.file, command[1]).normalize().toString();
                        if (target.startsWith(root.file)) {
                            InsideFile way = root.getFile(target);
                            if (way==null)System.out.println("Целевой директории не существует");
                            else current=way;
                                continue;
                        } else System.out.println("Целевая папка выше корневой");
                        break;
                    case "ls":
                        if(command.length>1)current.sortContent(command);
                        for (InsideFile insideFile: current.content) insideFile.printInfo();
                        break;
                    case "info":
                        target = Paths.get(current.file, command[1]).normalize().toString();
                        System.out.println("target - "+ target);
                            if (target.startsWith(root.file)) {
                                InsideFile infoFile = root.getFile(target);
                                if(infoFile!=null)infoFile.printInfo();
                                else System.out.println("Целевой файл не найден");
                            } else System.out.println("Целевой файл вне виртуальной вайловой системы");
                        break;
                    case "update":
                        CustomerFileVisitor fileVisitor = new CustomerFileVisitor();
                        Files.walkFileTree(startLocation, fileVisitor);
                        root = fileVisitor.root;
                        current = root;
                        break;
                    case "quit":
                        System.out.println("Завершение работы");
                        return;
                    default:
                        System.out.println("Неправильная команда");
                }
            }
        } catch (IOException e) {
            System.out.println();
        }
    }
}
