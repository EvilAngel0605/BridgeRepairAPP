package net.jsrbc.utils;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2017-11-29.
 */
public final class JsonUtils {

    private final static Gson gson = new Gson();

    /** 对象转为Json字符串 */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /** 解析Json字符串 */
    public static <T> T fromJson(String json, Class<T> returnType) {
        return gson.fromJson(json, returnType);
    }

    /** 解析Json字符串至集合 */
    public static <T> List<T> toList(String json, Class<T> returnType) {
        List<T> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(gson.fromJson(jsonArray.getString(i), returnType));
            }
        }catch (JSONException e) {
            Log.e(JsonUtils.class.getName(), String.format("%s is not a json array", json), e);
            throw new RuntimeException(e);
        }
        return list;
    }
}
