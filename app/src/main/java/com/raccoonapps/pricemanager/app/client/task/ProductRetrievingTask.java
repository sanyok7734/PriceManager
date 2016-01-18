package com.raccoonapps.pricemanager.app.client.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.raccoonapps.pricemanager.app.MainActivity;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;

/**
 * Task, for retrieving new product
 * */
public class ProductRetrievingTask extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDialog;

    private ProductRetrieverImpl retriever;

    private Context context;

    private boolean isDone = false;

    /**
     * @param context Context of activity, that starts this task,
     * @param retriever Retriever, that we used to load product
     * */
    public ProductRetrievingTask(Context context, ProductRetrieverImpl retriever) {
        this.retriever = retriever;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog != null)
            progressDialog = null;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            retriever.retrieveWebPage();
        } catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            MainActivity.bus.post(true);
        }
    }

    public ProductRetrieverImpl getRetriever() {
        return retriever;
    }

    public boolean isDone() {
        return isDone;
    }
}
