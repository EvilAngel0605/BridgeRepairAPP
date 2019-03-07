package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-14.
 */
public enum MemberDescParam {

    /** 孔号 */
    SITE_NO("siteNo"),

    /** 联号 */
    JOINT_NO("jointNo"),

    /** 墩台号 */
    PIER_NO("pierNo"),

    /** 纵桥向编号 */
    VERTICAL_NO("verticalNo"),

    /** 横桥向编号 */
    HORIZONTAL_NO("horizontalNo"),

    /** 大小桩号侧 */
    STAKE_SIDE("stakeSide"),

    /** 特殊部分 */
    SPECIAL_SECTION("specialSection"),

    /** 构件名称 */
    MEMBER_TYPE_NAME("memberTypeName");

    private String value;

    MemberDescParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
