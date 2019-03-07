package net.jsrbc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.pojo.Token;

/**
 * Created by ZZZ on 2017-11-30.
 */
public final class DataUtils {

    /** 文件名 */
    private static final String NAME = "debridge";

    /** 存储模式 */
    private final static int MODE = Activity.MODE_PRIVATE;

    /**
     * 添加设置文件
     * @param context 上下文环境
     * @param key     键
     * @param value   值
     * @param <T>     参数化类型
     */
    public static <T> void put(Context context, String key, T value) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, JsonUtils.toJson(value));
        editor.apply();
    }

    /**
     * 获取设置文件
     * @param context 上下文环境
     * @param key     设置名
     * @param type    返回的类型
     * @param <T>     参数化类型
     * @return       json转换为对象
     */
    public static <T> T get(Context context, String key, Class<T> type) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, MODE);
        String json = preferences.getString(key, "");
        if (StringUtils.isEmpty(json)) return null;
        return JsonUtils.fromJson(json, type);
    }

    /**
     * 移除一个属性
     * @param context 上下文环境
     * @param key     属性键名
     */
    public static void remove(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, MODE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key).apply();
    }

    /** 获取当前缓存的Token信息 */
    public static Token getCurrentToken(Context context) {
        Token token =  DataUtils.get(context, AndroidConstant.TOKEN, Token.class);
        if (token == null) return Token.empty();
        else return token;
    }

    /** 封闭构造函数 */
    private DataUtils() {}
}
