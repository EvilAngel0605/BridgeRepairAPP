package net.jsrbc.utils;

import net.jsrbc.entity.TblBridgeSite;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by ZZZ on 2017-11-30.
 */
public final class StringUtils {

    /** 判断字符串是否为空 */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** 生成32位的UUID */
    public static String createId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /** 将字符串转化为浮点 */
    public static float toFloat(String str) {
        if (isEmpty(str) || isEmpty(str.trim())) return 0;
        str = str.trim();
        return Float.valueOf(str);
    }

    /** 将字符串转化为浮点 */
    public static double toDouble(String str) {
        if (isEmpty(str) || isEmpty(str.trim())) return 0;
        str = str.trim();
        return Double.valueOf(str);
    }

    /** 将字符串转换为整型 */
    public static int toInt(String str) {
        return isEmpty(str)?0:Integer.valueOf(str.trim());
    }

    /** 解析桩号 */
    public static String parseStakeNo(double stakeNo) {
        BigDecimal bigDecimal = BigDecimal.valueOf(stakeNo).setScale(3, BigDecimal.ROUND_HALF_UP);
        return ("K" + bigDecimal.toPlainString()).replaceAll("\\.", "+");
    }

    /** 解析孔跨名称 */
    public static String parseSiteName(int jointNo, int siteNo) {
        StringBuilder nameBuilder = new StringBuilder();
        if (jointNo != 0) nameBuilder.append("第").append(jointNo).append("联");
        nameBuilder.append("第").append(siteNo).append("孔");
        return nameBuilder.toString();
    }

    /** 将尺寸值转化为字符串，mm转化为m */
    public static String sizeToStr(float value, RoundingMode roundingMode) {
        if (value == 0F) return "";
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(1000), roundingMode).toPlainString();
    }

    /** 将日期转换为字符串 */
    public static String fromDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    /** 封闭构造函数 */
    private StringUtils() {}
}
