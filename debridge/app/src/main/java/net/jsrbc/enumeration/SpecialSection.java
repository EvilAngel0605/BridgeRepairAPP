package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-15.
 */
public enum SpecialSection {

    CLOSURE_SECTION("合拢段"),

    SIDE_SECTION("边跨现浇段"),

    POSITION_LEFT("左侧"),

    POSITION_RIGHT("右侧"),

    EXISTS("存在")

    ;

    private String value;

    SpecialSection(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
