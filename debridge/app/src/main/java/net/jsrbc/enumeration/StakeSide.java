package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-07-03.
 */
public enum StakeSide {

    /** 不分大小桩号侧 */
    NONE(0, "不分大小桩号侧"),

    /** 小桩号侧 */
    LESS_SIDE(1, "小桩号侧"),

    /** 大桩号侧 */
    LARGER_SIDE(2, "大桩号侧"),

    /** 大小桩号侧对称 */
    BOTH_SIDE(3, "大小桩号侧对称");

    /** 大小桩号侧代码 */
    private int code;

    /** 名称 */
    private String name;

    StakeSide(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return this.name;
    }

    public static StakeSide of(int code) {
        return Arrays.stream(StakeSide.values()).filter(s->s.getCode() == code).findFirst().orElse(NONE);
    }

    @Override
    public String toString() {
        return getName();
    }
}
