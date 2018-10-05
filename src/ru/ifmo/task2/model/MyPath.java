package ru.ifmo.task2.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents file in virtual file system.
 */
public class MyPath {

    private String path;
    private String name;
    private MyPath parent;
    private List<MyPath> children = new ArrayList<>();
    private long byteSize;
    private String size;
    private String access;
    private String lastModyfiedDate;
    private long millisDateTime;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm");

    public MyPath(Path path, MyPath parent) throws SecurityException, IOException {
        this.path = path.toString();
        this.name = path.getFileName().toString();
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }

        this.byteSize = Files.size(path);
        this.size = formatSize(this.byteSize);
        this.access = "r" + (Files.isReadable(path) ? "+" : "-") + "w" + (Files.isWritable(path) ? "+" : "-") + "x" + (Files.isExecutable(path) ? "+" : "-");

        this.millisDateTime = Files.getLastModifiedTime(path).toMillis();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());

        this.lastModyfiedDate = dateTime.format(dateTimeFormatter);

    }

    @Override
    public String toString() {
        return String.format("%10s : %8s : %8s : %16s", this.name, this.size, this.access, this.lastModyfiedDate);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MyPath getParent() {
        return parent;
    }

    public void setParent(MyPath parent) {
        this.parent = parent;
    }

    public List<MyPath> getChildren() {
        return children;
    }

    public void setChildren(List<MyPath> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return byteSize;
    }

    public void setSize(long size) {
        this.byteSize = size;
        this.size = formatSize(size);
    }

    public long getDateTime() {
        return millisDateTime;
    }

    private String formatSize(long size) {
        String formatedSize = null;
        String[] units = {"B", "KB", "MB", "GB"};
        long unitSize = size;
        for (String unit : units) {
            if (unitSize / 1024 > 1) {
                unitSize = unitSize / 1024;
            } else {
                formatedSize = unitSize + " " + unit;
                break;
            }
        }
        if (formatedSize == null) {
            formatedSize = unitSize + " " + units[units.length - 1];
        }

        return formatedSize;
    }
}
