package ru.ifmo.task2;

import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;

public class InsideFile {
    String file;
    long size;
    String Name;
    LocalDateTime mod;
    char r, w, x;
    boolean isDir;
    InsideFile parent;
    ArrayList <InsideFile> content;


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
        Name = file.getFileName().toString();
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
        return Name;
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
            Comparator C=null;
            switch (comparator) {
                case "mod":
                    C = Comparator.comparing(InsideFile::getMod);
                    break;
                case "name":
                    C = Comparator.comparing(InsideFile::getName);
                    break;
                case "size":
                    C=Comparator.comparing(InsideFile::getSize);
                    break;
                    default: System.out.println("Неправильные входные данные");
                    return;
            }
            if(order=='-') C=C.reversed();
            if(baseComparator==null) baseComparator=C;
            else{
                baseComparator=baseComparator.thenComparing(C);
            }
        }
        content.sort(baseComparator);
    }

    public void printInfo ()
    {
            int Gs=  (int) (size/Math.pow(2, 30));
            int Ms= (int)((size - Gs*Math.pow(2, 30))/Math.pow(2, 20));
            int Kbs = (int)((size - Gs*Math.pow(2, 30)-Ms*Math.pow(2, 20))/Math.pow(2, 10));
            int bs = (int) (size - Gs*Math.pow(2, 30)-Ms*Math.pow(2, 20) - Kbs*Math.pow(2, 10));
            String info = String.format("%s : %dG %dM %dKb %db : r%sw%sx%s : %s",
                    Name,Gs, Ms, Kbs, bs,
                    r, w, x,
                    mod.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            System.out.println(info);
    }


}
