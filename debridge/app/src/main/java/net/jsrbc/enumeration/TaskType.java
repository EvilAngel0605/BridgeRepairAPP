package net.jsrbc.enumeration;

import java.util.Arrays;

/**
 * Created by ZZZ on 2017-12-06.
 */
public enum TaskType {

    FREQUENCY(1) {
        @Override
        public String getText() {
            return "经常";
        }
    },

    REGULAR(2) {
        @Override
        public String getText() {
            return "定期";
        }
    },

    SPECIAL(3) {
        @Override
        public String getText() {
            return "特殊";
        }
    };

    private int code;

    TaskType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    abstract public String getText();

    public static TaskType of(int code) {
        return Arrays.stream(TaskType.values()).filter(t->t.getCode() == code).findFirst().orElse(null);
    }

}
