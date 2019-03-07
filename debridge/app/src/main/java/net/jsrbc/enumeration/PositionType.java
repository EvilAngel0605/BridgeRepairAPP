package net.jsrbc.enumeration;

import java.util.stream.Stream;

/**
 * Created by ZZZ on 2017-12-18.
 */
public enum PositionType {

    SUPER("上部结构"),

    SUB("下部结构"),

    DECK("桥面系")

    ;

    private String value;

    PositionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /** 部位 */
    public static PositionType of(String text) {
        return Stream.of(PositionType.values()).filter(p->p.getValue().equals(text)).findFirst().orElse(null);
    }
}
