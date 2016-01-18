package com.raccoonapps.pricemanager.app.api.model;

import com.raccoonapps.pricemanager.app.api.storage.SelectorStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Object representation of store
 * */
public class Store {

    /**
     * Unique store identifier
     * */
    private UUID id;

    private String storeName;

    /**
     *  @see {@link SelectorStorage}
     * */
    private SelectorStorage selectorStorage;

    public Store(UUID id, String storeName, SelectorStorage selectorStorage) {
        this.id = id;
        this.storeName = storeName;
        this.selectorStorage = selectorStorage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public SelectorStorage getSelectorStorage() {
        return selectorStorage;
    }

    public void setSelectorStorage(SelectorStorage selectorStorage) {
        this.selectorStorage = selectorStorage;
    }

    public JSONObject toJSONObject() {
        try {
            return new JSONObject()
                    .accumulate(JSONStoreFields.ID.getValue(), id)
                    .accumulate(JSONStoreFields.STORE_NAME.getValue(), storeName)
                    .accumulate(JSONStoreFields.TITLE_SELECTOR.getValue(), selectorStorage.getTitleSelector())
                    .accumulate(JSONStoreFields.TITLE_SELECTOR_VALUE.getValue(), selectorStorage.getTitleSelectorValue())
                    .accumulate(JSONStoreFields.PRICE_SELECTOR.getValue(), selectorStorage.getPriceSelector())
                    .accumulate(JSONStoreFields.PRICE_SELECTOR_VALUE.getValue(), selectorStorage.getPriceSelectorValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
