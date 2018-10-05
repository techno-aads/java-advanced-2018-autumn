package ru.ifmo.task2;

import ru.ifmo.task2.commands.MapCommands;
import ru.ifmo.task2.model.FileSystem;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Анастасия on 05.10.2018.
 */
public class Terminal {
    public static void main(String[] args) {

        if (args[0] != null && args.length >= 1) {
            run(args[0]);
        } else {
            System.out.println("Missed argument");
        }

    }

    public static void run(String path) {
        System.out.println("Welcome to MyApp!");

        try {
            Path in = Paths.get(path);
            FileSystem.createFileSystem(in);

            Scanner scanner = new Scanner(System.in);
            MapCommands mapCommands = new MapCommands();

            while (true) {
                String command = scanner.nextLine();
                String[] commandSplitted = command.split(" ");
                if (mapCommands.getMapCommands().containsKey(commandSplitted[0])) {
                    mapCommands.getMapCommands().get(commandSplitted[0]).execute(command);
                } else {
                    System.out.println("There is no such command.");
                }
            }

        } catch (InvalidPathException invalidPathException) {
            System.out.println("Invalid path argument: " + path);
        } catch (SecurityException securityException) {
            System.out.println("Can not access file." + securityException.getMessage());
        } catch (IOException ioe) {

        }
    }
}
