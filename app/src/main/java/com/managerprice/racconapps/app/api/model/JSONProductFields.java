package com.managerprice.racconapps.app.api.model;

public enum JSONProductFields {
    ID("id"), URL("url"), TITLE("title"), PRICE("price"), LAST_UPDATE("last_update"), STORE_ID("store_id");

    private String value;

    JSONProductFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
