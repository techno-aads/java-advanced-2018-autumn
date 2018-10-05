package ru.ifmo.task2.model;

import ru.ifmo.task2.model.MyPath;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * FileVisitor, which builds virtual file system.
 */
public class BuildFileSystemFileVisitor extends SimpleFileVisitor {

    MyPath root;
    MyPath currentRoot;

    public BuildFileSystemFileVisitor(MyPath root) {
        super();
        this.currentRoot = root;
        this.root = root;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            MyPath path = new MyPath((Path)file, currentRoot);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException ioe) throws IOException {
        currentRoot = currentRoot.getParent();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) throws IOException {
        if (!dir.toString().equals(root.getPath().toString())){
            MyPath path = new MyPath((Path)dir, currentRoot);
            currentRoot = path;
        }

        return FileVisitResult.CONTINUE;
    }
}
