package ru.ifmo.task2.model;

import ru.ifmo.task2.exeptions.NoFileSystemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

/**
 * Represent virtual flesystem.
 */
public class FileSystem {
    private MyPath root;
    private MyPath current;

    private static FileSystem fileSystem;

    private FileSystem(MyPath root) {
        this.root = root;
        this.current = root;
    }

    public static FileSystem getFileSystem() {
        if (fileSystem == null) {
            throw new NoFileSystemException("FileSystem was not identified.");
        }
        return fileSystem;
    }

    public static void createFileSystem(Path in) throws SecurityException, IOException {
        //todo what to do, if security exception? close the app or miss this file?
        MyPath root = new MyPath(in, null);
        fileSystem = new FileSystem(root);

        BuildFileSystemFileVisitor fileVisitor = new BuildFileSystemFileVisitor(root);
        Files.walkFileTree(in, fileVisitor);
    }

    public static void update() throws IOException {
        createFileSystem(Paths.get(getFileSystem().getRoot().getPath()));
    }

    public List<MyPath> getCurrentDirectoryChildrensList() {
        return current.getChildren();
    }

    public void changeCurrentDirectory(String path) throws InvalidPathException {
        MyPath newCurrentDirectory = this.current;
        String[] splittedPath = path.split("/");
        for (String pathElement : splittedPath) {
            if ("..".equals(pathElement)) {
                if (newCurrentDirectory.getParent() != null) {
                    newCurrentDirectory = newCurrentDirectory.getParent();
                } else {
                    throw new InvalidPathException(path, "Try to go out of fileSystem");
                }
                ;
            } else {
                newCurrentDirectory = findChild(newCurrentDirectory, pathElement);
            }
        }
        this.current = newCurrentDirectory;
    }

    public String getInfo(String path) {
        MyPath file = findChild(getFileSystem().getCurrent(), path);
        return file.toString();
    }

    public void sortChilds(String[] params) {

        Comparator<MyPath> myPathComparator = null;

        for (String param : params) {
            if (!FilesComparator.getMap().containsKey(param.substring(1)) || (param.charAt(0) != '-' && param.charAt(0) != '+')) {
                throw new IllegalArgumentException(String.format("There is no %s available to sort files.", param.substring(1)));
            }
            Comparator myNewPathComparator = FilesComparator.getMap().get(param.substring(1));
            if (param.charAt(0) == '-') {
                myNewPathComparator = myNewPathComparator.reversed();
            }
            if (myPathComparator == null) {
                myPathComparator = myNewPathComparator;
            } else {
                myPathComparator = myPathComparator.thenComparing(myNewPathComparator);
            }
        }

        current.getChildren().sort(myPathComparator);
    }

    private static MyPath findChild(MyPath path, String name) throws InvalidPathException {
        for (MyPath child : path.getChildren()) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        throw new InvalidPathException(name, "Invalid path");
    }

    public MyPath getRoot() {
        return root;
    }

    public void setRoot(MyPath root) {
        this.root = root;
    }

    public MyPath getCurrent() {
        return current;
    }

    public void setCurrent(MyPath current) {
        this.current = current;
    }
}
