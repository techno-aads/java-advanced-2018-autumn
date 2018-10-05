package ru.ifmo.task2.exeptions;

/**
 * Created by Анастасия on 02.10.2018.
 */
public class NoFileSystemException extends RuntimeException {
    public NoFileSystemException(){
        super();
    }
    public NoFileSystemException(String message){
        super(message);
    }
}
