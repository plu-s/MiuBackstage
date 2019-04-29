package com.corydon.miu;

import java.io.File;
import java.util.UUID;

public class Util {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String getRealFilePath(String path) {
        return path.replace("/", FILE_SEPARATOR).replace("\\", FILE_SEPARATOR);
    }

    public static String getHttpURLPath(String path) {
        return path.replace("\\", "/");
    }
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }


    public static boolean deleteFile(String path) {
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean deleteDirectoryAllFile(String path){
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        for(File file:dirFile.listFiles()){
            if(file.isDirectory())
                deleteDirectory(file.getAbsolutePath());
            else
                deleteFile(file.getAbsolutePath());
        }
        return true;
    }

    public static boolean deleteDirectory(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                deleteFile(files[i].getAbsolutePath());
            }
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
}
