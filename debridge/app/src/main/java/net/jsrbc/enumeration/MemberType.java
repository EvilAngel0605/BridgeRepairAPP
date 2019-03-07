package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-16.
 */
public enum MemberType {

    PIER_BASE("墩基础"),

    ABUTMENT_BASE("台基础"),

    SLOP_PROTECTION("护坡"),

    DECK_PAVEMENT("桥面铺装"),

    SIDE_WALK("人行道"),

    HANDRAIL("栏杆"),

    GUARDRAIL("护栏"),

    DRAINAGE("排水系统"),

    ILLUMINATION("照明标志"),

    EXPANSION_JOINT("伸缩缝")
    ;

    private String value;

    MemberType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
