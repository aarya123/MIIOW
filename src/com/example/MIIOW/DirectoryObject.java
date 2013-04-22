package com.example.MIIOW;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

/*
    An object to represent a file within a directory.
    @name The name of the file
    @path The name of the file prefixed by a path separator
    @url The url of the file on the SmartFile servers
    @isDir A boolean value to determine object is a plain file or directory
 */
public class DirectoryObject {
    public static Stack<ArrayList<DirectoryObject>> dirStack = new Stack<ArrayList<DirectoryObject>>();// keeps track of previous and current directories
    public static HashMap<String, ArrayList<DirectoryObject>> cache = new HashMap<String, ArrayList<DirectoryObject>>(); //caches used directories, key is the directory name value is the directory array list

    private String name, path, url;
    private Boolean isDir;

    DirectoryObject(String name, String path, boolean isDir, String url) {
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    //wrapper method for peek() so that it doesn't return null
    public static ArrayList<DirectoryObject> getPeek() {
        try {
            return dirStack.peek();
        } catch (EmptyStackException e) {
            return new ArrayList<DirectoryObject>();
        }
    }
}
