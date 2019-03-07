package net.jsrbc.enumeration;

import java.util.stream.Stream;

public enum DimensionUnit {

    /** 米 */
    M("m"),

    /** 厘米 */
    CM("cm"),

    /** 毫米 */
    MM("mm");

    private String value;

    DimensionUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /** 根据给定的字符串返回单位 */
    public static DimensionUnit of(String unit) {
        return Stream.of(DimensionUnit.values()).filter(u->u.getValue().equals(unit)).findFirst().orElse(null);
    }
}
