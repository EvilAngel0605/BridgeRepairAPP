package net.jsrbc.enumeration;

/**
 * Created by ZZZ on 2017-12-26.
 */
public enum AnimationType {

    TRANSLATION_X("translationX"),

    TRANSLATION_Y("translationY"),

    ROTATION("rotation"),

    ROTATION_X("rotationX"),

    ROTATION_Y("rotationY"),

    SCALE_X("scaleX"),

    SCALE_Y("scaleY"),

    PIVOT_X("pivotX"),

    PIVOT_Y("pivotY"),

    X("x"),

    Y("y"),

    ALPHA("alpha");

    private String value;

    AnimationType(String value){
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
