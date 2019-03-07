package net.jsrbc.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ZZZ on 2017-12-10.
 */
public final class FileUtils {

    /** 判断文件是否存在 */
    public static boolean exists(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        File file = new File(filePath);
        return file.exists() && file.canRead();
    }

    /** 创建路径下不存在的文件夹 */
    public static boolean createDirectory(String directory) {
        File file = new File(directory);
        return file.exists() || file.mkdirs();
    }

    /** 复制文件 */
    public static void copy(File fromFile, File toFile) {
        try(FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile)) {
            int hasRead = 0;
            byte[] buffer = new byte[1024];
            while ((hasRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, hasRead);
            }
        }catch (IOException e) {
            Log.e(FileUtils.class.getName(), "copy file failed", e);
        }
    }

    /** 删除一个文件 */
    public static void delete(String path) {
        File file = new File(path);
        file.delete();
    }

    /** 封闭构造函数 */
    private FileUtils() {}
}
