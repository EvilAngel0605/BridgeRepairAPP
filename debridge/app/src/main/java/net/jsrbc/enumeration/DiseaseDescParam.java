package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-29.
 */
public enum DiseaseDescParam {

    H_POSITION_START("hPositionStart"),

    H_POSITION_END("hPositionEnd"),

    L_POSITION_START("lPositionStart"),

    L_POSITION_END("lPositionEnd"),

    V_POSITION_START("vPositionStart"),

    V_POSITION_END("vPositionEnd"),

    POSITION("position"),

    COUNT("count"),

    ANGLE("angle"),

    MIN_LENGTH("minLength"),

    MAX_LENGTH("maxLength"),

    MIN_WIDTH("minWidth"),

    MAX_WIDTH("maxWidth"),

    MIN_DEPTH("minDepth"),

    MAX_DEPTH("maxDepth"),

    PERCENT_DEGREE("percentDegree"),

    DESC_DEGREE("descDegree"),

    BEHAVIOR_DESC("behaviorDesc")

    ;

    private String value;

    DiseaseDescParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
