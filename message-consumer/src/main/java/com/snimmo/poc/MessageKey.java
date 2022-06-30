package com.snimmo.poc;

import java.util.Objects;

public class MessageKey {

    private String key1;

    public MessageKey() {
    }

    public MessageKey(String key1) {
        this.key1 = key1;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageKey that = (MessageKey) o;
        return Objects.equals(key1, that.key1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1);
    }
}
