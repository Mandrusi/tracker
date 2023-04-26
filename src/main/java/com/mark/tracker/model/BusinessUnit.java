package com.mark.tracker.model;

public enum BusinessUnit {
    DIGITAL_EXPERIENCE_GROUP("Digital Experience Group"),
    ADOBE("Adobe"),
    IBM_NBU("IBM NBU"),
    API_MANAGEMENT("API Management");

    private final String value;

    BusinessUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

