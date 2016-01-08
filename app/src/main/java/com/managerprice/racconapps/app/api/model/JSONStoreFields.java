package com.managerprice.racconapps.app.api.model;

public enum  JSONStoreFields {
    ID("id"), STORE_NAME("name"), TITLE_SELECTOR("title_selector"),
    PRICE_SELECTOR("price_selector"), TITLE_SELECTOR_VALUE("title_selector_value"),
    PRICE_SELECTOR_VALUE("price_selector_value");

    private String value;

    JSONStoreFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
