package net.jsrbc.enumeration;

/** 照片类型 */
public enum PhotoType {

    /** 正面照 */
    FRONT("front"),

    /** 侧面照 */
    SIDE("side"),

    /** 仰视照 */
    UPWARD("upward");

    private String text;

    PhotoType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    /** 通过PhotoType内容获取对应枚举对象 */
    public static PhotoType of(String text) {
        if (FRONT.getText().equals(text)) {
            return FRONT;
        } else if (SIDE.getText().equals(text)) {
            return SIDE;
        } else if (UPWARD.getText().equals(text)) {
            return UPWARD;
        }
        return null;
    }
}
