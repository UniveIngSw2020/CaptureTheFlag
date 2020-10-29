package com.junipero.capturetheflag;


import java.io.File;

public class StoredDataManager {

    private final String path;
    private File file;
    private final String folder = "/userdata";

    public StoredDataManager(String path) {
        this.path = path;
        this.file = new File(path);
    }



}
