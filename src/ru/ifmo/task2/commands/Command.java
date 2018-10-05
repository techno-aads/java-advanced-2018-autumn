package ru.ifmo.task2.commands;

import java.io.IOException;

/**
 * Created by Анастасия on 29.09.2018.
 */
public interface Command {
    void execute(String param) throws IOException;
}
