package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-12-25.
 */
public enum InspectionDirection {

    LEFT_TO_RIGHT(0, "横桥向从左往右编号"),

    RIGHT_TO_LEFT(1, "横桥向从右往左编号");

    private int code;

    private String name;

    InspectionDirection(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public InspectionDirection of(int code) {
        return Arrays.stream(InspectionDirection.values()).filter(d->d.getCode() == code).findAny().orElse(LEFT_TO_RIGHT);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
