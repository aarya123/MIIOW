package com.example.MIIOW;

import java.util.ArrayList;
public class DirectoryObjects {
    public static ArrayList<DirectoryObjects> dirObjs = new ArrayList<DirectoryObjects>();
    public String name, path;
    public Boolean isAFile;
    DirectoryObjects(String name, String path, boolean isAFile){
        this.name = name;
        this.path = path;
        this.isAFile=isAFile;
        dirObjs.add(this);
    }
}
