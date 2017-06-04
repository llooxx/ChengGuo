package com.chengmeng.tools.file;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 缓存工具类
 * 添加缓存之后，流畅度提升非常明显
 */
public class CacheTool {
    private static final long TIME_OUT = 10 * 60 * 1000;//缓存超时时间
    private static final String TIME_TAG = "_time";
    private static final String CACHE_SAVE_PATH = "all.cache";
    private static SharedPreferences read;
    private static SharedPreferences.Editor write;

    //必须先初始化，建议在Application中
    public static void initCache(Context context) {
        read = context.getSharedPreferences(
                CACHE_SAVE_PATH, Activity.MODE_PRIVATE);
        write = read.edit();
    }

    public static void saveCache(String key, String value) {
        if (read == null) return;
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) return;
        key = getKey(key);
        write.putLong(key + TIME_TAG, System.currentTimeMillis());
        write.putString(key, value);
        write.commit();
    }

    public static String getCache(String key) {
        if (read == null) return null;
        if (TextUtils.isEmpty(key)) return null;
        key = getKey(key);
        long time = read.getLong(key + TIME_TAG, 0);
        if (System.currentTimeMillis() - time < TIME_OUT)
            return read.getString(key, null);
        return null;
    }

    public static void clearCache() {
        if (read == null) return;
        write.clear();
    }

    private static String getKey(String key) {
        return PassTool.getMD5(key);
    }

}
