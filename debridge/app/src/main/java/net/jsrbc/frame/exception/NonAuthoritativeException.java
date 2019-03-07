package net.jsrbc.frame.exception;

/**
 * 未授权异常
 * Created by ZZZ on 2017-12-01.
 */
public class NonAuthoritativeException extends Exception {

    public NonAuthoritativeException() {}

    public NonAuthoritativeException(String msg) {
        super(msg);
    }
}
