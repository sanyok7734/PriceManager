package com.raccoonapps.pricemanager.app.api.storage;

import com.raccoonapps.pricemanager.app.api.model.JSONStoreFields;
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations;
import com.raccoonapps.pricemanager.app.api.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoreStorageJsonImpl implements Storage<Store> {

    private static final String STORES_ARRAY = "stores";

    private JSONObject storesJson;

    public StoreStorageJsonImpl(File storesFile) {
        try {
            storesJson = new JSONObject(SimpleOperations.INSTANCE.loadJSONFromFile(storesFile));
            if (!storesJson.has(STORES_ARRAY))
                storesJson.put(STORES_ARRAY, new JSONArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addItem(Store item) {
        JSONArray storesArray = null;
        try {
            storesArray = storesJson.getJSONArray(STORES_ARRAY);
            storesArray.put(item.toJSONObject());
            storesJson.remove(STORES_ARRAY);
            storesJson.put(STORES_ARRAY, storesArray);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void updateItem(UUID id, Store to) {
        try {
            JSONArray storesArray = this.storesJson.getJSONArray(STORES_ARRAY);
            int itemIndex = SimpleOperations.INSTANCE.getItemIndex(id, storesArray);
            List<Store> storeList = getItemsList();
            storeList.remove(itemIndex);
            storesArray = convertToJSONArray(storeList);
            storesJson.remove(STORES_ARRAY);
            storesJson.put(STORES_ARRAY, storesArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray convertToJSONArray(List<Store> storeList) {
        JSONArray array = new JSONArray();
        for (Store store : storeList) {
            array.put(store.toJSONObject());
        }
        return array;
    }

    @Override
    public Store get(UUID uuid) {
        List<Store> stores = getItemsList();
        for (Store store : stores) {
            if (store.getId().equals(uuid))
                return store;
        }
        return null;
    }

    @Override
    public boolean deleteItem(UUID id) {
        try {
            JSONArray storesArray = storesJson.getJSONArray(STORES_ARRAY);
            int index = SimpleOperations.INSTANCE.getItemIndex(id, storesArray);
            List<Store> stores = getItemsList();
            stores.remove(index);
            storesJson.remove(STORES_ARRAY);
            storesArray = convertToJSONArray(stores);
            return storesJson.put(STORES_ARRAY, storesArray) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Store> getItemsList() {
        try {
            JSONArray stores = storesJson.getJSONArray(STORES_ARRAY);
            ArrayList<Store> collector = new ArrayList<Store>();
            if (stores != null) {
                for (int i = 0; i < stores.length(); i++) {
                    JSONObject currentObject = (JSONObject) stores.get(i);
                    String id = currentObject.getString(JSONStoreFields.ID.getValue());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public JSONObject getStoresJson() {
        return storesJson;
    }
}
