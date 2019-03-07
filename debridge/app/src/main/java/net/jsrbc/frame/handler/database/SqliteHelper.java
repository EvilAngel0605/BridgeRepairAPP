package net.jsrbc.frame.handler.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.enumeration.SqliteDataType;
import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.Default;
import net.jsrbc.frame.annotation.database.restraint.NotNull;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;
import net.jsrbc.frame.annotation.database.restraint.Unique;

import java.lang.reflect.Field;

/**
 * Created by ZZZ on 2017-12-01.
 */
class SqliteHelper extends SQLiteOpenHelper {

    /** 上下文 */
    private Context context;

    SqliteHelper(Context context) {
        super(context, AndroidConstant.BASE_DIRECTORY + context.getString(R.string.databaseName), null, context.getResources().getInteger(R.integer.databaseVersion));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        dropTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    /** 创建所有注册过的表格 */
    private void createTables(SQLiteDatabase db) {
        for (String className : context.getResources().getStringArray(R.array.sqliteTables)) {
            StringBuilder sqlBuilder = new StringBuilder();
            try {
                Class<?> clazz = Class.forName(className);
                //检查是否有Table注解
                if (!clazz.isAnnotationPresent(Table.class)) {
                    Log.e(getClass().getName(), String.format("%s is not a database table object, do you forget add @Table to the class?", className));
                    throw new RuntimeException();
                }
                //拼接第一段语句
                sqlBuilder.append("CREATE TABLE ").append(clazz.getAnnotation(Table.class).value()).append("( ");
                //循环拼接属性
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(Column.class)) continue;
                    Column column = field.getAnnotation(Column.class);
                    sqlBuilder.append(column.value()).append(" ").append(SqliteDataType.of(field.getType())).append(" ");
                    //添加约束
                    if (field.isAnnotationPresent(PrimaryKey.class)) sqlBuilder.append(" PRIMARY KEY ");
                    if (field.isAnnotationPresent(NotNull.class)) sqlBuilder.append(" NOT NULL");
                    if (field.isAnnotationPresent(Unique.class)) sqlBuilder.append(" UNIQUE ");
                    if (field.isAnnotationPresent(Default.class)) sqlBuilder.append(" DEFAULT ").append(field.getAnnotation(Default.class).value()).append(" ");
                    //添加分隔符
                    sqlBuilder.append(",");
                }
                sqlBuilder.deleteCharAt(sqlBuilder.length()-1).append(")");
            }catch (ClassNotFoundException e) {
                Log.e(getClass().getName(), String.format("%s is not found", className), e);
                throw new RuntimeException(e);
            }
            db.execSQL(sqlBuilder.toString());
        }
    }

    //删除所有表格
    private void dropTables(SQLiteDatabase db) {
        for (String className : context.getResources().getStringArray(R.array.sqliteTables)) {
            StringBuilder sqlBuilder = new StringBuilder();
            try {
                Class<?> clazz = Class.forName(className);
                if (!clazz.isAnnotationPresent(Table.class)) {
                    Log.e(getClass().getName(), String.format("%s is not a database table object, do you forget to add @Table to this object?", className));
                    throw new RuntimeException();
                }
                sqlBuilder.append("DROP TABLE ").append(clazz.getAnnotation(Table.class).value());
            }catch (ClassNotFoundException e) {
                Log.e(getClass().getName(), String.format("%s is not found", className), e);
                throw new RuntimeException(e);
            }
            //删除
            db.execSQL(sqlBuilder.toString());
        }
    }
}
