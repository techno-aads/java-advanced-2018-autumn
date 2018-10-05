package ru.ifmo.task2.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Анастасия on 30.09.2018.
 */
public class FilesComparator {
    private Map<String, Comparator<MyPath>> map = new HashMap<>();

    public static Map<String, Comparator<MyPath>> getMap() {
        if (instance == null){
            instance = new FilesComparator();
        }
        return instance.map;
    }

    private static FilesComparator instance;



    public FilesComparator() {
        this.map.put("name", new Comparator<MyPath>() {
            @Override
            public int compare(MyPath o1, MyPath o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        this.map.put("size", new Comparator<MyPath>() {
            @Override
            public int compare(MyPath o1, MyPath o2) {
                return Long.compare(o1.getSize(), o2.getSize());
            }
        });
        this.map.put("mod", new Comparator<MyPath>() {
            @Override
            public int compare(MyPath o1, MyPath o2) {
                //todo compare without seconds?
                return Long.compare(o1.getDateTime(), o2.getDateTime());
            }
        });
    }
}
