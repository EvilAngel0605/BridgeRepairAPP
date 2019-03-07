package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-12.
 */
public enum UploadStatus {

    /** 不需要上传 */
    DO_NOT_UPLOAD(0),

    /** 需要上传 */
    NEED_UPLOAD(1);

    private int code;

    UploadStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
