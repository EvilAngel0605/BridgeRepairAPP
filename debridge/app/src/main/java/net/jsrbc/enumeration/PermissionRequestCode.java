package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-11-29.
 */
public enum PermissionRequestCode {

    PERMISSION_REQUEST(0);

    private int code;

    PermissionRequestCode(int code) {
        this.code = code;
    }

    /** 获取code */
    public int getCode() {
        return this.code;
    }

    /** 通过代码获取授权请求 */
    public static PermissionRequestCode of(int code) {
        return Arrays.stream(PermissionRequestCode.values()).filter(p->p.getCode() == code).findAny().orElse(null);
    }
}
