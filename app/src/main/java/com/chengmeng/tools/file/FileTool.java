package com.chengmeng.tools.file;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileTool {// 将来static
    private static final String BASE_SD_PATH = "GaoNeng";

    /**
     * 获取本软件在存储卡的基本路径
     */
    public static File getBaseSDCardPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File root_path = new File(
                        Environment.getExternalStorageDirectory(), BASE_SD_PATH);// 存在无卡、无权风险
                if (!root_path.exists())
                    root_path.mkdirs();
                return root_path;
            } catch (Exception e) {
                return null;// 禁止权限在此处捕获
            }
        }
        return null;
    }

    /**
     * 清除缓存
     */
    public static void clearCatch() {
        final File root_path = new File(
                Environment.getExternalStorageDirectory(), BASE_SD_PATH);
        if (root_path.exists())
            try {
                deleteFile(root_path);
            } catch (Exception e) {
            }
    }

    public static String getFilePath() {
        return getBaseSDCardPath().getAbsolutePath();
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    private static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            if (file.getName().equals("Message"))
                return;//暂时不删除消息界面
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }


    // 读取数据
    public static String readFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buff = new byte[fis.available()];
            fis.read(buff);
            String result = new String(buff, "GBK");
            fis.close();
            result = (result == null ? "" : result);
            return result;
        } catch (Exception e) {// 文件不存在
        }
        return "";
    }

    // 保存数据
    public static void saveFile(File file, String text) {
        try {
            FileOutputStream fos = null;
            if (!file.exists())
                file.createNewFile();// 无卡与未创建目录在此报错
            fos = new FileOutputStream(file);
            fos.write(text.getBytes("GBK"));// 以GBK方式存储，方便手动修改测试
            fos.flush();// 清除缓存
            fos.close();// 一般关闭要写在finally中
        } catch (Exception e) {
            return;
        }
    }
}
