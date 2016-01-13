package com.raccoonapps.pricemanager.app.api.storage;

import com.raccoonapps.pricemanager.app.api.model.JSONProductFields;
import com.raccoonapps.pricemanager.app.api.model.ProductItem;
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductStorageJsonImpl implements Storage<ProductItem> {

    public static final String PRODUCTS_ARRAY = "products";

    private JSONObject productsJSON;

    public ProductStorageJsonImpl(File productsFile) {
        try {
            productsJSON = new JSONObject(SimpleOperations.INSTANCE.loadJSONFromFile(productsFile));
            if (!productsJSON.has(PRODUCTS_ARRAY))
                productsJSON.put(PRODUCTS_ARRAY, new JSONArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addItem(ProductItem productItem) {
        JSONArray products = null;
        try {
            products = productsJSON.getJSONArray(PRODUCTS_ARRAY);
            products.put(productItem.toJSONObject());
            productsJSON.remove(PRODUCTS_ARRAY);
            productsJSON.put(PRODUCTS_ARRAY, products);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateItem(UUID id, ProductItem to) {
        try {
            JSONArray productsArray = this.productsJSON.getJSONArray(PRODUCTS_ARRAY);
            int itemIndex = SimpleOperations.INSTANCE.getItemIndex(id, productsArray);
            List<ProductItem> productsList = getItemsList();
            productsList.remove(itemIndex);
            productsList.add(itemIndex, to);
            productsJSON.remove(PRODUCTS_ARRAY);
            productsArray = convertToJSONArray(productsList);
            productsJSON.put(PRODUCTS_ARRAY, productsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray convertToJSONArray(List<ProductItem> productsList) {
        JSONArray array = new JSONArray();
        for (ProductItem item : productsList) {
            array.put(item.toJSONObject());
        }
        return array;
    }

    @Override
    public boolean deleteItem(UUID productId) {
        try {
            JSONArray products = productsJSON.getJSONArray("products");
            int index = SimpleOperations.INSTANCE.getItemIndex(productId, products);
            List<ProductItem> productItems = getItemsList();
            productItems.remove(index);
            productsJSON.remove(PRODUCTS_ARRAY);
            products = convertToJSONArray(productItems);
            return productsJSON.put(PRODUCTS_ARRAY, products) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ProductItem get(UUID uuid) {
        List<ProductItem> products = getItemsList();
        for (ProductItem product : products) {
            if (product.getId().equals(uuid))
                return product;
        }
        return null;
    }

    @Override
    public List<ProductItem> getItemsList() {
        try {
            JSONArray products = (JSONArray) productsJSON.get("products");
            ArrayList<ProductItem> collector = new ArrayList<>();
            if (products != null) {
                for (int i = 0; i < products.length(); i++) {
                    JSONObject currentObject = (JSONObject) products.get(i);
                    String id = currentObject.getString(JSONProductFields.ID.getValue());
                    String storeId = currentObject.getString(JSONProductFields.STORE_ID.getValue());
                    String title = currentObject.getString(JSONProductFields.TITLE.getValue());
                    String url = currentObject.getString(JSONProductFields.URL.getValue());
                    String price = currentObject.getString(JSONProductFields.PRICE.getValue());
                    LocalDateTime lastUpdate = LocalDateTime.parse(currentObject.getString(JSONProductFields.LAST_UPDATE.getValue()));
                    collector.add(new ProductItem(UUID.fromString(id), UUID.fromString(storeId), title, price, url, lastUpdate));
                }
                return collector;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void updateItemsList(List<ProductItem> newProducts) {
        try {
            List<ProductItem> items = getItemsList();
            for (int i = 0; i < newProducts.size(); i++) {
                newProducts.get(i).setId(items.get(i).getId());
            }
            productsJSON.remove(PRODUCTS_ARRAY);
            JSONArray array = new JSONArray();
            for (ProductItem item : newProducts)
                array.put(item.toJSONObject());
            productsJSON.put(PRODUCTS_ARRAY, array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getProductsJSON() {
        return productsJSON;
    }
}
