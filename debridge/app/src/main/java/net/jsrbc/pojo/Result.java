package net.jsrbc.pojo;

/**
 * Created by zhang on 2017-11-29.
 */
public final class Result {

    private String message;

    private Status status;

    private Token content;

    private Object extra;

    /**  反馈给前台的消息等级 */
    public enum Status {
        SUCCESS, ERROR, UNAUTHORIZED, NOT_ALLOWED
    }


    /**  构建器 */
    public static class Builder {

        private String message;

        private Status status;

        private Token content;

        private Object extra;

        /**  反馈是否成功  */
        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        /**  对应返回结果的说明 */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder content(Token content) {
            this.content = content;
            return this;
        }

        public Builder extra(Object extra) {
            this.extra = extra;
            return this;
        }

        /**  构建结果 */
        public Result build() {
            return new Result(this);
        }
    }

    private Result(Builder builder) {
        this.message = builder.message;
        this.status = builder.status;
        this.content = builder.content;
        this.extra = builder.extra;
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

    public Token getContent() {
        return content;
    }

    public void setContent(Token content) {
        this.content = content;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
