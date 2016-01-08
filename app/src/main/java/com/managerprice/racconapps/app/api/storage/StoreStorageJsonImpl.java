package com.managerprice.racconapps.app.api.storage;

import com.managerprice.racconapps.app.api.model.JSONStoreFields;
import com.managerprice.racconapps.app.api.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.SimpleOperations;

public class StoreStorageJsonImpl implements Storage<Store> {

    private static final String STORES_ARRAY = "stores";

    private JSONObject storesJson;

    public StoreStorageJsonImpl(File storesFile) throws JSONException {
        storesJson = new JSONObject(SimpleOperations.INSTANCE.loadJSONFromFile(storesFile));
        if (storesJson.getJSONArray(STORES_ARRAY) == null)
            storesJson.put(STORES_ARRAY, new JSONArray());
    }

    @Override
    public boolean addItem(Store item) throws JSONException {
        JSONArray storesArray = storesJson.getJSONArray(STORES_ARRAY);
        return storesArray.put(item.toJSONObject()) != null;
    }

    @Override
    public void updateItem(UUID id, Store to) throws JSONException {
        JSONArray storesArray = this.storesJson.getJSONArray(STORES_ARRAY);
        int itemIndex = SimpleOperations.INSTANCE.getItemIndex(id, storesArray);
        List<Store> storeList = getItemsList();
        storeList.remove(itemIndex);
        storesArray = convertToJSONArray(storeList);
        storesJson.remove(STORES_ARRAY);
        storesJson.put(STORES_ARRAY, storesArray);
    }

    private JSONArray convertToJSONArray(List<Store> storeList) {
        JSONArray array = new JSONArray();
        for (Store store : storeList) {
            array.put(store.toJSONObject());
        }
        return array;
    }

    @Override
    public Store get(UUID uuid) throws JSONException {
        List<Store> stores = getItemsList();
        for (Store store : stores) {
            if (store.getId().equals(uuid))
                return store;
        }
        return null;
    }

    @Override
    public boolean deleteItem(UUID id) throws JSONException {
        JSONArray storesArray = storesJson.getJSONArray(STORES_ARRAY);
        int index = SimpleOperations.INSTANCE.getItemIndex(id, storesArray);
        return storesArray.remove(index) != null;
    }

    @Override
    public List<Store> getItemsList() throws JSONException {
        JSONArray stores = storesJson.getJSONArray(STORES_ARRAY);
        ArrayList<Store> collector = new ArrayList<Store>();
        if (stores != null) {
            for (int i = 0; i < stores.length(); i++) {
                JSONObject currentObject = (JSONObject) stores.get(i);
                String id =  currentObject.getString(JSONStoreFields.ID.getValue());
                String title = currentObject.getString(JSONStoreFields.STORE_NAME.getValue());
                String titleSelector = currentObject.getString(JSONStoreFields.TITLE_SELECTOR.getValue());
                String priceSelector = currentObject.getString(JSONStoreFields.PRICE_SELECTOR.getValue());
                String titleSelectorValue = currentObject.getString(JSONStoreFields.TITLE_SELECTOR_VALUE.getValue());
                String priceSelectorValue = currentObject.getString(JSONStoreFields.PRICE_SELECTOR_VALUE.getValue());
                SelectorStorage selectorStorage = new SelectorStorage();
                selectorStorage.setTitleSelector(titleSelector);
                selectorStorage.setPriceSelector(priceSelector);
                selectorStorage.setTitleSelectorValue(titleSelectorValue);
                selectorStorage.setPriceSelectorValue(priceSelectorValue);
                collector.add(new Store(UUID.fromString(id), title, selectorStorage));
            }
            return collector;
        }
        return new ArrayList<Store>();
    }

    public JSONObject getStoresJson() {
        return storesJson;
    }
}
