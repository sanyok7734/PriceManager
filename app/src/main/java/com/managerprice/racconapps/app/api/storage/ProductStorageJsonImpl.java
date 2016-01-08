package com.managerprice.racconapps.app.api.storage;

import com.managerprice.racconapps.app.api.model.JSONProductFields;
import com.managerprice.racconapps.app.api.model.ProductItem;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.SimpleOperations;

public class ProductStorageJsonImpl implements Storage<ProductItem> {

    public static final String PRODUCTS_ARRAY = "products";

    private JSONObject productsJSON;

    public ProductStorageJsonImpl(File productsFile) throws JSONException {
        productsJSON = new JSONObject(SimpleOperations.INSTANCE.loadJSONFromFile(productsFile));
        if (productsJSON.getJSONArray(PRODUCTS_ARRAY) == null)
            productsJSON.put(PRODUCTS_ARRAY, new JSONArray());
    }

    @Override
    public boolean addItem(ProductItem productItem) throws JSONException {
        JSONArray products = productsJSON.getJSONArray(PRODUCTS_ARRAY);
        products.put(productItem.toJSONObject());
        productsJSON.remove(PRODUCTS_ARRAY);
        productsJSON.put(PRODUCTS_ARRAY, products);
        return true;
    }

    @Override
    public void updateItem(UUID id, ProductItem to) throws JSONException {
        JSONArray productsArray = this.productsJSON.getJSONArray(PRODUCTS_ARRAY);
        int itemIndex = SimpleOperations.INSTANCE.getItemIndex(id, productsArray);
        List<ProductItem> productsList = getItemsList();
        productsList.remove(itemIndex);
        productsList.add(to);
        productsJSON.remove(PRODUCTS_ARRAY);
        productsArray = convertToJSONArray(productsList);
        productsJSON.put(PRODUCTS_ARRAY, productsArray);
    }

    private JSONArray convertToJSONArray(List<ProductItem> productsList) {
        JSONArray array = new JSONArray();
        for (ProductItem item : productsList) {
            array.put(item.toJSONObject());
        }
        return array;
    }

    @Override
    public boolean deleteItem(UUID productId) throws JSONException {
        JSONArray products = productsJSON.getJSONArray("products");
        int index = SimpleOperations.INSTANCE.getItemIndex(productId, products);
        List<ProductItem> productItems = getItemsList();
        productItems.remove(index);
        productsJSON.remove(PRODUCTS_ARRAY);
        products = convertToJSONArray(productItems);
        return productsJSON.put(PRODUCTS_ARRAY, products) != null;
    }

    @Override
    public ProductItem get(UUID uuid) throws JSONException {
        List<ProductItem> products = getItemsList();
        for (ProductItem product : products) {
            if (product.getId().equals(uuid))
                return product;
        }
        return null;
    }

    @Override
    public List<ProductItem> getItemsList() throws JSONException {
        JSONArray products = (JSONArray) productsJSON.get("products");
        ArrayList<ProductItem> collector = new ArrayList<ProductItem>();
        if (products != null) {
            for (int i = 0; i < products.length(); i++) {
                JSONObject currentObject = (JSONObject) products.get(i);
                String id =  currentObject.getString(JSONProductFields.ID.getValue());
                String storeId = currentObject.getString(JSONProductFields.STORE_ID.getValue());
                String title = currentObject.getString(JSONProductFields.TITLE.getValue());
                String url = currentObject.getString(JSONProductFields.URL.getValue());
                String price = currentObject.getString(JSONProductFields.PRICE.getValue());
                LocalDateTime lastUpdate = LocalDateTime.parse(currentObject.getString(JSONProductFields.LAST_UPDATE.getValue()));
                collector.add(new ProductItem(UUID.fromString(id), UUID.fromString(storeId), title, price, url, lastUpdate));
            }
            return collector;
        }
        return new ArrayList<ProductItem>();
    }

    public JSONObject getProductsJSON() {
        return productsJSON;
    }
}
