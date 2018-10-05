package ru.ifmo.task2.commands;

import ru.ifmo.task2.model.FileSystem;
import ru.ifmo.task2.model.MyPath;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Анастасия on 29.09.2018.
 */
public class MapCommands {
    private Map<String, Command> mapCommands = new HashMap<>();
    static private MapCommands instance;

    public Map<String, Command> getMapCommands() {
        if (instance == null) {
            instance = new MapCommands();
        }
        return instance.mapCommands;
    }

    public MapCommands() {

        this.mapCommands.put("cd", new Command() {
            @Override
            public void execute(String param) {
                try {
                    FileSystem.getFileSystem().changeCurrentDirectory(param.substring(3));
                } catch (InvalidPathException invalidPathException) {
                    System.out.println(invalidPathException.getMessage());
                }

            }
        });

        this.mapCommands.put("ls", new Command() {
            @Override
            public void execute(String param) {
                try {
                    if (param.length() > 4) {
                        String[] comporators = param.substring(3).split(" ");
                        if (comporators.length < 3){
                            FileSystem.getFileSystem().sortChilds(comporators);
                        }else{
                            System.out.println("To many parameters to ls command. Maximum 3.");
                        }
                    }
                    for (MyPath path : FileSystem.getFileSystem().getCurrentDirectoryChildrensList()) {
                        System.out.println(path);
                    }
                } catch (PatternSyntaxException patternSyntaxError) {
                    System.out.println("Can not parse argument of ls command. " + patternSyntaxError.getMessage());
                }

            }
        });

        this.mapCommands.put("info", new Command() {
            @Override
            public void execute(String param) {
                try {
                    System.out.println(FileSystem.getFileSystem().getInfo(param.substring(5)));
                } catch (InvalidPathException invalidPathException) {
                    System.out.println(invalidPathException.getMessage());
                }

            }
        });

        this.mapCommands.put("update", new Command() {
            @Override
            public void execute(String param) throws IOException {
                FileSystem.getFileSystem().update();
            }
        });

        this.mapCommands.put("quit", new Command() {
            @Override
            public void execute(String param) {
                System.out.println("Stop running the app");
                System.exit(0);
            }
        });

    }
}
