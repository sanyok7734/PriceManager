package com.managerprice.racconapps.app.api.model;

public enum Selector {
    CLASS("[class]"), ID("[id]");

    private String selectorType;

    Selector(String type) {
        selectorType = type;
    }

    public String getSelectorType() {
        return selectorType;
    }
}
