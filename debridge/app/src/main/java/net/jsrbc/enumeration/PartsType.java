package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-14.
 */
public enum PartsType {

    SUPER_BEARING("上部承重构件"),

    SUPPORT("支座"),

    PIER("桥墩"),

    ABUTMENT("桥台"),

    BASE("墩台基础"),

    WING_EAR_WALL("翼墙、耳墙"),

    CONICAL_PROTECTION_SLOP("锥坡、护坡"),

    DECK_PAVEMENT("桥面铺装"),

    SIDE_WALK("人行道"),

    DRAINAGE("排水系统"),

    ILLUMINATION("照明标志"),

    EXPANSION_JOINT("伸缩缝")

    ;


    private String value;

    PartsType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
