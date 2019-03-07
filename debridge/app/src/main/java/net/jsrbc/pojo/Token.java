package net.jsrbc.pojo;

/**
 * Created by ZZZ on 2017-11-30.
 */
public final class Token {

    /**  token的内容 */
    private String id;

    /**  token所包含的内容s */
    private User content;

    /** 空对象 */
    private final static Token EMPTY = new Token();

    /** 返回一个空对象 */
    public static Token empty() {
        return EMPTY;
    }

    /** 是否为空对象 */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getContent() {
        return content;
    }

    public void setContent(User content) {
        this.content = content;
    }
}
