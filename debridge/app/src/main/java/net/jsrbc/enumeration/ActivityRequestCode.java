package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-12-07.
 */
public enum ActivityRequestCode {

    GENERAL_REQUEST(0)
    ;

    private int code;

    ActivityRequestCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ActivityRequestCode of(int code) {
        return Arrays.stream(ActivityRequestCode.values()).filter(rc->rc.getCode() == code).findFirst().orElse(null);
    }
}
