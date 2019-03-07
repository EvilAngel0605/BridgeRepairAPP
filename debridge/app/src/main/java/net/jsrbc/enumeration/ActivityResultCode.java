package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-12-07.
 */
public enum ActivityResultCode {

    CANCEL(0),

    SUCCESS(1),
    ;

    private int code;

    ActivityResultCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ActivityResultCode of(int code) {
        return Arrays.stream(ActivityResultCode.values()).filter(rc->rc.getCode() == code).findFirst().orElse(null);
    }
}
