package net.jsrbc.utils;

import net.jsrbc.enumeration.DimensionUnit;

/**
 * Created by ZZZ on 2017-12-13.
 */
public final class UnitConverter {

    /** 毫米转换为米 */
    public static float millimetreToMetre(float milliMetreValue) {
        return milliMetreValue / 1000F;
    }

    /** 厘米转换为米 */
    public static float centimetreToMetre(float centimetreValue) {
        return centimetreValue / 100F;
    }

    /** 米转换为毫米 */
    public static float metreToMillimetre(float metreValue) {
        return metreValue * 1000F;
    }

    /** 米转换为厘米 */
    public static float metreToCentimetre(float metreValue) {
        return metreValue * 100F;
    }

    /** 转换为毫米 */
    public static double toMillimetre(double value, DimensionUnit unit){
        switch (unit) {
            case M:
                return value * 1000D;
            case CM:
                return value * 10D;
            default:
                return value;
        }
    }

    /** 毫米转化为 */
    public static double fromMillimetre(double value, DimensionUnit unit) {
        switch (unit) {
            case M:
                return value / 1000D;
            case CM:
                return value / 10;
            default:
                return value;
        }
    }

    /** 封闭构造函数 */
    private UnitConverter() {}
}
