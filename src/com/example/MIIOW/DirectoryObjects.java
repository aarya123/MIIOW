package com.example.MIIOW;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Anubhaw
 * Date: 3/29/13
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class DirectoryObjects {
    static ArrayList<DirectoryObjects> dirObjs = new ArrayList<DirectoryObjects>();
    public String name, path;
    public Boolean isAFile;
    DirectoryObjects(String name, String path, boolean isAFile){
        this.name = name;
        this.path = path;
        this.isAFile=isAFile;
        dirObjs.add(this);
    }
}
