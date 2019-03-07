package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * SQLITE的数据类型
 * Created by ZZZ on 2017-12-01.
 */
public enum SqliteDataType {

    /** 整型 */
    INTEGER(byte.class, short.class, int.class, long.class, Byte.class, Short.class, Integer.class, Long.class),

    /** 浮点型 */
    REAL(float.class, double.class, Float.class, Double.class),

    /** 文本类型 */
    TEXT;

    private Class<?>[] javaClasses;

    SqliteDataType(Class<?>... javaClasses) {
        this.javaClasses = javaClasses;
    }

    /** 根据JAVA类型获取对应的数据类型 */
    public static SqliteDataType of(Class<?> clazz) {
        return Arrays.stream(SqliteDataType.values())
                .filter(t->t.javaClasses != null && Arrays.asList(t.javaClasses).contains(clazz))
                .findFirst()
                .orElse(TEXT);
    }
}
