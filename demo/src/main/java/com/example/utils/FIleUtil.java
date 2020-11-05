package com.example.utils;

import java.io.File;

public class FIleUtil {

    public static boolean delete(String path){
        File file = new File(path);
        if(file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    delete(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
        return true;
    }
}
