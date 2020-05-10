package com.example.ozymaxx.redisdemo.service.dto;

public class ResponseAUpdate {

    private String newValue;

    public ResponseAUpdate() {
        this.newValue = null;
    }

    public ResponseAUpdate(final String newValue) {
        this.newValue = newValue;
    }

    public void setNewValue(final String newValue) {
        this.newValue = newValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
