package com.example.ozymaxx.redisdemo.service.dto;

import java.io.Serializable;

public class ResponseA implements Serializable {

    private String value;

    public ResponseA(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
