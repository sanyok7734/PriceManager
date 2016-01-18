package com.raccoonapps.pricemanager.app.api.model;

/**
 * Enum, which describe selectors like id or class.
 * */
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
