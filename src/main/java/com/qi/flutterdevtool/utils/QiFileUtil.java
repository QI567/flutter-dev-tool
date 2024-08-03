package com.qi.flutterdevtool.utils;

import java.io.File;

public class QiFileUtil {

    public static File findFile(File file, String regex) {
        if (!file.exists()) {
            return null;
        }
        if (file.getName().matches(regex)) {
            return file;
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    File resFile = findFile(subFile, regex);
                    if (resFile != null) {
                        return resFile;
                    }
                }
            }
        }
        return null;
    }

}
