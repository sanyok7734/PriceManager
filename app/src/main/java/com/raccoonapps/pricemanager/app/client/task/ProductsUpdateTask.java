package com.raccoonapps.pricemanager.app.client.task;

import android.os.AsyncTask;
import android.util.Log;

import com.raccoonapps.pricemanager.app.MainActivity;
import com.raccoonapps.pricemanager.app.api.model.ProductItem;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;
import com.raccoonapps.pricemanager.app.api.storage.ProductStorageJsonImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductsUpdateTask extends AsyncTask<Void, Void, Void> {

    private List<ProductItem> products;

    private File storesFile;
    private ArrayList<ProductItem> resultList;

    public ProductsUpdateTask(File productsFile, File storesFile) {
        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
        this.products = productStorage.getItemsList();
        this.storesFile = storesFile;
    }

    @Override
    protected Void doInBackground(Void... params) {
        resultList = new ArrayList<>();
        for (ProductItem product : products) {
            String link = product.getLink();
            ProductRetrieverImpl retriever = new ProductRetrieverImpl(link);
            retriever.retrieveWebPage();
            ProductItem item = retriever.tryRetrieveExistingValues(storesFile);
            Log.d("Madness", "Retrieved: " + item.getTitle() + ". Last update: " + item.getLastUpdate());
            resultList.add(item);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        MainActivity.bus.post(resultList);
    }
}
