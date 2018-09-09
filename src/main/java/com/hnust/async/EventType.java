package com.hnust.async;

/**
 * 枚举
 */
public enum EventType {
    //定义 4 个枚举
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
