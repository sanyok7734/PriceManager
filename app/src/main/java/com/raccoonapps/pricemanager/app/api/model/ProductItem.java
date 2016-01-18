package com.raccoonapps.pricemanager.app.api.model;

import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Product object representation
 * */
public class ProductItem {

    /**
     * Product unique identifier
     * */
    private UUID id;

    /**
     * Identifier of store object, which associated with this product
     * */
    private UUID storeId;

    /**
     * Product title
     * */
    private String title;

    private String price;

    /**
     * Link of the product web page
     * */
    private String link;

    /**
     * Last time product updates
     * */
    private LocalDateTime lastUpdate;

    public ProductItem(UUID id, UUID storeId, String title, String price, String link, LocalDateTime lastUpdate) {
        this.id = id;
        this.storeId = storeId;
        this.title = title;
        this.price = price;
        this.link = link;
        this.lastUpdate = lastUpdate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public JSONObject toJSONObject() {
        try {
            return new JSONObject()
                    .accumulate(JSONProductFields.ID.getValue(), id)
                    .accumulate(JSONProductFields.STORE_ID.getValue(), storeId)
                    .accumulate(JSONProductFields.URL.getValue(), link)
                    .accumulate(JSONProductFields.TITLE.getValue(), title)
                    .accumulate(JSONProductFields.PRICE.getValue(), price)
                    .accumulate(JSONProductFields.LAST_UPDATE.getValue(), lastUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public String toString() {
        return "ProductItem [" +
                "id=" + id +
                ", storeId=" + storeId +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", link='" + link + '\'' +
                ", lastUpdate=" + lastUpdate +
                ']';
    }
}
