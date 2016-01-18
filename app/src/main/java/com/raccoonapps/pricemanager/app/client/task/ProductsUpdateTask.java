package com.raccoonapps.pricemanager.app.client.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.raccoonapps.pricemanager.app.MainActivity;
import com.raccoonapps.pricemanager.app.api.model.ProductItem;
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;
import com.raccoonapps.pricemanager.app.api.storage.ProductStorageJsonImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for updating single or multiple products
 * */
public class ProductsUpdateTask extends AsyncTask<Void, Void, Void> {

    private List<ProductItem> products;

    private ProgressDialog progressDialog;

    private File storesFile;
    private ArrayList<ProductItem> resultList;
    private Context context;
    private boolean isCompleteSuccessfully = true;
    private boolean blockUI;

    /**
     * @param itemsForUpdate Products, which we want update.
     * @param productsFile File, which stores products.
     * @param storesFile File, which stores shops.
     * @param blockUI if true, than UI will be blocked by progressDialog
     * */
    public ProductsUpdateTask(List<ProductItem> itemsForUpdate, File productsFile, File storesFile, boolean blockUI, Context context) {
        ProductStorageJsonImpl productStorage = new ProductStorageJsonImpl(productsFile);
        this.products = itemsForUpdate == null ? productStorage.getItemsList() : itemsForUpdate;
        this.storesFile = storesFile;
        this.context = context;
        this.blockUI = blockUI;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (blockUI) {
            if (progressDialog != null)
                progressDialog = null;
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please wait, while product updates");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
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
                    resultList.add(item);
                } else {
                    isCompleteSuccessfully = false;
                    break;
                }
            }
        } catch (Exception e) {
            isCompleteSuccessfully = false;
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if  (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (isCompleteSuccessfully)
            MainActivity.bus.post(resultList);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        MainActivity.bus.post(new ArrayList<ProductItem>());
    }
}
