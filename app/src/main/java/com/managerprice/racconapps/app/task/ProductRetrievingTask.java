package com.managerprice.racconapps.app.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.managerprice.racconapps.app.api.retriever.ProductRetrieverImpl;

public class ProductRetrievingTask extends AsyncTask<Void, Void, Void> {

    ProgressDialog progressDialog;

    private ProductRetrieverImpl retriever;
    private Context context;

    public ProductRetrievingTask(Context context, ProductRetrieverImpl retriever) {
        this.retriever = retriever;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog != null)
            progressDialog = null;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Madness", "Starting asynctask");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        retriever.retrieveWebPage();
        Log.d("Madness", "Finishing asynctask");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        super.onPostExecute(aVoid);
    }

    public ProductRetrieverImpl getRetriever() {
        return retriever;
    }

}
