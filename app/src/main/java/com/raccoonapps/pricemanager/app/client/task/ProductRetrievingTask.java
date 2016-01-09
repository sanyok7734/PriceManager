package com.raccoonapps.pricemanager.app.client.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.raccoonapps.pricemanager.app.MainActivity;
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl;

public class ProductRetrievingTask extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDialog;

    private ProductRetrieverImpl retriever;

    private Context context;

    private boolean isDone = false;

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
        Log.d("Madness", "Starting asynctask");
        retriever.retrieveWebPage();
        Log.d("Madness", "Finishing asynctask");
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
