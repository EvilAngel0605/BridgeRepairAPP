package net.jsrbc.enumeration;

public enum DiseasePositionParam {

    /** 纵向起始位置 */
    L_POSITION_START("lPositionStart");

    private String value;

    DiseasePositionParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /** 是否含有prop值的枚举 */
    public static boolean contains(String prop) {
        for (DiseasePositionParam param : DiseasePositionParam.values()) {
            if (param.getValue().equals(prop)) return true;
        }
        return false;
    }

    /** 返回对应prop值的枚举 */
    public static DiseasePositionParam of(String prop) {
        for (DiseasePositionParam param : DiseasePositionParam.values()) {
            if (param.getValue().equals(prop)) return param;
        }
        return null;
    }
}
