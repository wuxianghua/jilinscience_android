package cn.palmap.jilinscience.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by stone on 2017/5/12.
 */

public class SharedPreferenceUtils {
    public static void putValue(Context context, String name, String key,
                                String value) {
        SharedPreferences.Editor sp = getEditor(context, name);
        sp.putString(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String name, String key,
                                boolean value) {
        SharedPreferences.Editor sp = getEditor(context, name);
        sp.putBoolean(key, value);
        sp.commit();
    }

    /**
     * 从SharedPreferences中读取int类型的数据
     *
     * @param context 上下文环境
     * @param name 对应的xml文件名称
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static int getValue(Context context, String name, String key,
                               int defValue) {
        SharedPreferences sp = getSharedPreferences(context, name);
        int value = sp.getInt(key, defValue);
        return value;
    }

    public static boolean getValue(Context context, String name, String key,
                               boolean defValue) {
        SharedPreferences sp = getSharedPreferences(context, name);
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }

    /**
     * 向SharedPreferences中写入int类型数据
     *
     * @param context 上下文环境
     * @param name 对应的xml文件名称
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String name, String key,
                                int value) {
        SharedPreferences.Editor sp = getEditor(context, name);
        sp.putInt(key, value);
        sp.commit();
    }

    //获取Editor实例
    private static SharedPreferences.Editor getEditor(Context context, String name) {
        return getSharedPreferences(context, name).edit();
    }

    //获取SharedPreferences实例
    private static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 从SharedPreferences中读取String类型的数据
     *
     * @param context 上下文环境
     * @param name 对应的xml文件名称
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static String getValue(Context context, String name, String key,
                                  String defValue) {
        SharedPreferences sp = getSharedPreferences(context, name);
        String value = sp.getString(key, defValue);
        return value;
    }
}
