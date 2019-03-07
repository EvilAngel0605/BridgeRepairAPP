package net.jsrbc.frame.handler.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;
import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.QueryColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZZZ on 2017-12-01.
 */
public final class DatabaseAnnotationHandler {

    private Context context;

    private SqliteHelper sqliteHelper;

    /** 准备好 */
    public static DatabaseAnnotationHandler newInstance(Context context) {
        return new DatabaseAnnotationHandler(context);
    }

    /** 绑定数据访问对象 */
    public void bindMapper() {
        DatabaseProxy proxy = new DatabaseProxy();
        for (Field field : context.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Mapper.class)) continue;
            if (!field.getType().isInterface()) {
                String errorMsg = String.format("%s is not an interface", field.getName());
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            try {
                field.setAccessible(true);
                field.set(context, Proxy.newProxyInstance(field.getType().getClassLoader(), new Class[] {field.getType()}, proxy));
            } catch (IllegalAccessException e) {
                Log.e(getClass().getName(), String.format("%s can not access", field.getName()), e);
            }
        }
    }

    /** 关闭数据库 */
    public void closeDatabase() {
        if (this.sqliteHelper != null) sqliteHelper.close();
    }

    /** 代理对象 */
    private class DatabaseProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
            //处理基类自带的方法
            if (method.getDeclaringClass() == Object.class) return method.invoke(this, args);
            //初始化数据库对象及原生sql语句
            SQLiteDatabase db = null;
            String rawSql = null;
            boolean isWritable = false;
            if (method.isAnnotationPresent(Select.class)) {
                db = sqliteHelper.getReadableDatabase();
                rawSql = context.getString(method.getAnnotation(Select.class).value());
            } else {
                db = sqliteHelper.getWritableDatabase();
                isWritable = true;
                if (method.isAnnotationPresent(Insert.class)) rawSql = context.getResources().getString(method.getAnnotation(Insert.class).value());
                else if (method.isAnnotationPresent(Update.class)) rawSql = context.getResources().getString(method.getAnnotation(Update.class).value());
                else if (method.isAnnotationPresent(Delete.class)) rawSql = context.getResources().getString(method.getAnnotation(Delete.class).value());
            }
            //空值检查
            if (db == null) {
                String errorMsg = "database can not access";
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            if (rawSql == null) {
                String errorMsg = String.format("%s should be annotated by @Select or @Update or @Delete or @Insert", method.getName());
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            //执行SQL语句
            if (isWritable) {
                executionWritableSql(db, method, rawSql, args);
            } else {
                return executionQuerySql(db, method, rawSql, args);
            }
            return null;
        }
    }

    /** 执行数据库写入、修改、删除操作 */
    private void executionWritableSql(SQLiteDatabase db, Method method, String rawSql, Object... args) {
        if (args == null) return;
        //首先判断是集合的情况，执行事物式的添加
        if (args.length == 1 && args[0] instanceof List) {
            List<?> list = (List<?>)args[0];
            if (list.isEmpty()) return;
            //开始批量进行集合添加
            db.beginTransaction();
            try {
                SQLiteStatement statement = db.compileStatement(cleanRawSql(rawSql));
                for (Object obj : list) {
                    statement.bindAllArgsAsStrings(getParamArrayFromObject(rawSql, obj));
                    statement.execute();
                    statement.clearBindings();
                }
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
        } else {
            HashMap<String, Object> paramMap = getParamMapFromArguments(method, args);
            db.execSQL(cleanRawSql(rawSql), getParamArrayFromParamMap(rawSql, paramMap));
        }
    }

    /** 执行数据查询操作 */
    @SuppressWarnings("unchecked")
    private Object executionQuerySql(SQLiteDatabase db, Method method, String rawSql, Object... args) {
        HashMap<String, Object> paramMap = getParamMapFromArguments(method, args);
        Cursor cursor = null;
        try {
            Class returnType = method.getReturnType();
            //结果类型为null，则抛出异常
            if (returnType == null) {
                String errorMsg = "return type should not be void";
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            cursor = db.rawQuery(cleanRawSql(rawSql), getParamArrayFromParamMap(rawSql, paramMap));
            //cursor必须先往前走一步才能取值
            if (cursor.getCount() == 0) return null;
            //根据返回结果类型分为：集合类、单个类
            if (List.class.isAssignableFrom(returnType)) {
                Type genericReturnType = method.getGenericReturnType();
                if (!(genericReturnType instanceof ParameterizedType)) {
                    String errorMsg = String.format("%s should be an explicit", returnType.getName());
                    Log.e(getClass().getName(), errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                returnType = (Class)((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                List results = new ArrayList();
                while (cursor.moveToNext()) {
                    results.add(createFromCursor(cursor, returnType));
                }
                //返回结果
                return results;
            } else {
                cursor.moveToNext();
                return getSingleResultFromCursor(cursor, returnType);
            }
        }finally {
            if (cursor != null) cursor.close();
        }
    }

    /** 清理原SQL语言 */
    private String cleanRawSql(String rawSql) {
        return rawSql.replaceAll("#\\{\\w+\\}", "?");
    }

    /** 通过游标创建对象 */
    private <T> T createFromCursor(Cursor cursor, Class<T> clazz) {
        if (clazz == null || cursor == null) return null;
        try {
            T t = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(QueryColumn.class)) continue;
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                String columnName;
                if (field.isAnnotationPresent(Column.class)) columnName = field.getAnnotation(Column.class).value();
                else columnName = field.getAnnotation(QueryColumn.class).value();
                field.set(t, getMultipleResultFromCursor(cursor, fieldType, columnName));
            }
            return t;
        }catch (InstantiationException e) {
            Log.e(getClass().getName(), "create object failed", e);
        }catch (IllegalAccessException e) {
            Log.e(getClass().getName(), String.format("%s can not access", clazz.getName()), e);
        }
        return null;
    }

    /**
     * 从游标中获取结果，先遍历一遍基本类型，没有则判定为自定义类型
     * @param cursor       游标
     * @param type         结果类型
     * @return             游标查询的结果
     */
    private Object getSingleResultFromCursor(Cursor cursor, Class<?> type) {
        if (type == short.class || type == Short.class) {
            return cursor.getShort(0);
        } else if (type == int.class || type == Integer.class) {
            return cursor.getInt(0);
        } else if (type == long.class || type == Long.class) {
            return cursor.getLong(0);
        } else if (type == float.class || type == Float.class) {
            return cursor.getFloat(0);
        } else if (type == double.class || type == Double.class) {
            return cursor.getDouble(0);
        } else if (type == String.class){
            return cursor.getString(0);
        } else {
            return createFromCursor(cursor, type);
        }
    }

    /**
     * 从游标中获取结果
     * @param cursor       游标
     * @param type         结果类型
     * @param columnName   列名
     * @return             游标查询的结果
     */
    private Object getMultipleResultFromCursor(Cursor cursor, Class<?> type, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (type == short.class || type == Short.class) {
            return columnIndex < 0 ? 0 : cursor.getShort(columnIndex);
        } else if (type == int.class || type == Integer.class) {
            return columnIndex < 0 ? 0 : cursor.getInt(columnIndex);
        } else if (type == long.class || type == Long.class) {
            return columnIndex < 0L ? 0L : cursor.getLong(columnIndex);
        } else if (type == float.class || type == Float.class) {
            return columnIndex < 0D ? 0F : cursor.getFloat(columnIndex);
        } else if (type == double.class || type == Double.class) {
            return columnIndex < 0D ? 0D : cursor.getDouble(columnIndex);
        } else if (type == String.class){
            return columnIndex < 0 ? "" : cursor.getString(columnIndex);
        } else {
            throw new RuntimeException(String.format("Unknown column type : %s", type.getName()));
        }
    }

    /** 获取集合中某个对象的参数 */
    private String[] getParamArrayFromObject(String rawSql, Object obj) {
        List<String> result = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Matcher matcher = Pattern.compile("#\\{(\\w+)\\}").matcher(rawSql);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            try {
                Field field = clazz.getDeclaredField(paramName);
                field.setAccessible(true);
                result.add(field.get(obj) == null ? "" : String.valueOf(field.get(obj)));
            }catch (NoSuchFieldException e) {
                Log.e(getClass().getName(), String.format("%s is not found", paramName), e);
                throw new RuntimeException(e);
            }catch (IllegalAccessException e) {
                Log.e(getClass().getName(), String.format("%s can not access", paramName), e);
                throw new RuntimeException(e);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /** 从参数Map中获取参数值数组 */
    private String[] getParamArrayFromParamMap(String rawSql, HashMap<String, Object> paramMap) {
        List<String> result = new ArrayList<>();
        Matcher matcher = Pattern.compile("#\\{(\\w+)\\}").matcher(rawSql);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (!paramMap.containsKey(paramName)) {
                String errorMsg = String.format("%s is not found", paramName);
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            Object value = paramMap.get(paramName);
            result.add(value == null ? "" : String.valueOf(value));
        }
        return result.toArray(new String[result.size()]);
    }

    /** 获取所有的参数 */
    private HashMap<String, Object> getParamMapFromArguments(Method method, Object... args) {
        HashMap<String, Object> paramMap = new HashMap<>();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i=0; i<annotations.length; i++) {
            //有param注解，就使用注解作为参数名，没有则取属性名
            if (annotations[i].length > 0) {
                for (Annotation annotation : annotations[i]) {
                    if (annotation instanceof Param)
                        paramMap.put(((Param) annotation).value(), args[i]);
                }
            } else {
                for (Field field : args[i].getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        if (paramMap.get(field.getName()) != null) {
                            String errorMsg = String.format("%s is duplicated", field.getName());
                            Log.e(getClass().getName(), errorMsg);
                            throw new RuntimeException(errorMsg);
                        }
                        paramMap.put(field.getName(), field.get(args[i]));
                    } catch (IllegalAccessException e) {
                        Log.e(getClass().getName(), String.format("%s can not access", field.getName()), e);
                    }
                }
            }
        }
        return paramMap;
    }

    private DatabaseAnnotationHandler(Context context) {
        this.context = context;
        sqliteHelper = new SqliteHelper(context);
    }
}
