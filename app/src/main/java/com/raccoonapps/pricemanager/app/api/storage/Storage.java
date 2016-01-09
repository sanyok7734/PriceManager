package com.raccoonapps.pricemanager.app.api.storage;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

public interface Storage<T> {

    boolean addItem(T item) throws JSONException;

    boolean deleteItem(UUID id) throws JSONException;

    void updateItem(UUID id, T to) throws JSONException;

    T get(UUID uuid) throws JSONException;

    List<T> getItemsList() throws JSONException;

}
