package com.snimmo.poc;

import java.util.Objects;

public class MessageValue {

    private String value1;

    public MessageValue() {
    }

    public MessageValue(String value1) {
        this.value1 = value1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageValue that = (MessageValue) o;
        return Objects.equals(value1, that.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1);
    }

    @Override
    public String toString() {
        return "MessageValue{" +
                "value1='" + value1 + '\'' +
                '}';
    }
}
