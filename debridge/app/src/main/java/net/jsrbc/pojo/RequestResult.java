package net.jsrbc.pojo;

/**
 * 请求结果
 * Created by ZZZ on 2018-01-04.
 */
public final class RequestResult {

    private String message;

    private Status status;

    /**  反馈给前台的消息等级 */
    public enum Status {
        SUCCESS, ERROR, UNAUTHORIZED, NOT_ALLOWED
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
