package com.raccoonapps.pricemanager.app.client.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.raccoonapps.pricemanager.app.MainActivity;
import com.raccoonapps.pricemanager.app.api.model.ProductItem;
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;
import com.raccoonapps.pricemanager.app.api.storage.ProductStorageJsonImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductsUpdateTask extends AsyncTask<Void, Void, Void> {

    private List<ProductItem> products;

    private File storesFile;
    private ArrayList<ProductItem> resultList;
    private Context context;
    private boolean isCompleteSuccessfully = true;

    public ProductsUpdateTask(List<ProductItem> itemsForUpdate, File productsFile, File storesFile, Context context) {
        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
        this.products = itemsForUpdate == null ? productStorage.getItemsList() : itemsForUpdate;
        this.storesFile = storesFile;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        resultList = new ArrayList<>();
        try {
            for (ProductItem product : products) {
                if (SimpleOperations.INSTANCE.isNetworkAvailable(context)) {
                    String link = product.getLink();
                    ProductRetrieverImpl retriever = new ProductRetrieverImpl(link);
                    retriever.retrieveWebPage();
                    ProductItem item = retriever.tryRetrieveExistingValues(storesFile);
                    Log.d("Madness", "Retrieved: " + item.getTitle() + ". Last update: " + item.getLastUpdate());
                    resultList.add(item);
                } else {
                    isCompleteSuccessfully = false;
                    break;
                }
            }
        } catch (Exception e) {
            isCompleteSuccessfully = false;
            Log.d("Async", "Exception catches");
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isCompleteSuccessfully)
            MainActivity.bus.post(resultList);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        MainActivity.bus.post(new ArrayList<ProductItem>());
    }
}
