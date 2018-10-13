package ru.ifmo.task2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class InsideFile {
    String file;
    long size;
    String name;
    LocalDateTime mod;
    char r, w, x;
    boolean isDir;
    InsideFile parent;
    ArrayList<InsideFile> content;


    public InsideFile(Path file, InsideFile parent){
        this.file=file.toAbsolutePath().toString();
        this.parent=parent;
        this.content=new ArrayList<>();
        if(this.parent!=null) parent.addContent(this);
        r = Files.isReadable(file)?'+':'-';
        w = Files.isWritable(file)?'+':'-';
        x = Files.isExecutable(file)?'+':'-';
        try {
            size = Files.size(file);
        } catch (IOException e) {
            size=0;
        }
        isDir=Files.isDirectory(file)? true: false;
        name = file.getFileName().toString();
        try {
            mod = LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            mod = LocalDateTime.now();
        }

    }

    public void addContent(InsideFile insider){
        content.add(insider);
    }

    public LocalDateTime getMod() {
        return mod;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public InsideFile getFile(String target){
        if(file.equals(target)){
            return this;
        }
        for(InsideFile insideFile: content){
            if(target.startsWith(insideFile.file)){
                return insideFile.getFile(target);
            }
        }
        return null;
    }

    public void sortContent(String[] ways){
        Comparator baseComparator=null;
        for(int i=1; i<ways.length; i++){
            char order = ways[i].charAt(0);
            String comparator = ways[i].substring(1);
            System.out.println(order+" "+comparator);
            Comparator insideComparator;
            switch (comparator) {
                case "mod":
                    insideComparator = Comparator.comparing(InsideFile::getMod);
                    break;
                case "name":
                    insideComparator = Comparator.comparing(InsideFile::getName);
                    break;
                case "size":
                    insideComparator=Comparator.comparing(InsideFile::getSize);
                    break;
                    default: System.out.println("Неправильные входные данные");
                    return;
            }
            if(order=='-') insideComparator=insideComparator.reversed();
            if(baseComparator==null) baseComparator=insideComparator;
            else{
                baseComparator=baseComparator.thenComparing(insideComparator);
            }
        }
        content.sort(baseComparator);
    }

    public void printInfo()
    {
            int gs=  (int) (size/Math.pow(2, 30));
            int ms= (int)((size - gs*Math.pow(2, 30))/Math.pow(2, 20));
            int kbs = (int)((size - gs*Math.pow(2, 30)-ms*Math.pow(2, 20))/Math.pow(2, 10));
            int bs = (int) (size - gs*Math.pow(2, 30)-ms*Math.pow(2, 20) - kbs*Math.pow(2, 10));
            String info = String.format("%s : %dG %dM %dKb %db : r%sw%sx%s : %s",
                    name,gs, ms, kbs, bs,
                    r, w, x,
                    mod.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            System.out.println(info);
    }


}
