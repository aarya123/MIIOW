package com.example.MIIOW;

import android.util.Log;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

/*
    if a directory should have hash to children else should be null
 */
public class DirectoryObject {
    //public static ArrayList<DirectoryObject> dirObj;
    //private HashMap<String, DirectoryObject> child;
    public static Stack<ArrayList<DirectoryObject>> dirStack = new Stack<ArrayList<DirectoryObject>>();//not sure if i can construct here
    public static HashMap<String, ArrayList<DirectoryObject>> cache = new HashMap<String, ArrayList<DirectoryObject>>();

    private String name, path;
    private Boolean isDir;

    //private ArrayList<DirectoryObject> children;
    DirectoryObject(String name, String path, boolean isDir) {
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        //if(!isAFile) child = null;
        //dirObj.add(this);
    }

    //getters
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Boolean isDir() {
        return isDir;
    }

    public static ArrayList<DirectoryObject> getPeek() {
        try {
            return dirStack.peek();
        } catch (EmptyStackException e) {
            Log.e("whoops", "Empty ArrayList.");
            return new ArrayList<DirectoryObject>();
        }
    }
}
