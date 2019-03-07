package net.jsrbc.utils;

/**
 * Created by ZZZ on 2017-12-16.
 */
public final class NumericUtils {

    /** 如果为零，则返回zeroValue */
    public static int zeroThen(Integer intValue, Integer zeroValue) {
        return intValue == null || intValue == 0 ? zeroValue : intValue;
    }

    /**
     * 获取四舍五入后的值
     * @param value      待转换的值
     * @param precision  转换后的精度
     * @return           转化后的值
     */
    public static double getPrecisionValue(double value, int precision) {
        return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
    }

    /**
     * 获取四舍五入后的值
     * @param value      待转换的值
     * @param precision  转换后的精度
     * @return           转化后的值
     */
    public static double getPrecisionValue(float value, int precision) {
        return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
    }

    /** 封闭构造函数 */
    private NumericUtils() {}
}
