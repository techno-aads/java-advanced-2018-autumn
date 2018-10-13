package ru.ifmo.task2;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class CustomerFileVisitor implements FileVisitor<Path> {
    public InsideFile root=null;
    private InsideFile current=null;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

        if(root==null)
        {
            root=new InsideFile(dir, null);
        }
        else {
            current= root.getFile(dir.getParent().toAbsolutePath().toString());
            new InsideFile(dir, current);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        current= root.getFile(file.getParent().toAbsolutePath().toString());
        new InsideFile(file, current);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
