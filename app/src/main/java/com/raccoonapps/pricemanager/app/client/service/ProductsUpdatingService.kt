package com.raccoonapps.pricemanager.app.client.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.raccoonapps.pricemanager.app.api.model.SimpleOperations
import com.raccoonapps.pricemanager.app.api.retriever.ProductRetrieverImpl
import com.raccoonapps.pricemanager.app.api.storage.ProductStorageJsonImpl
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ProductsUpdatingService : Service() {

    private val TAG = "Service_madness"
    private val interval: Long = 30000

    private val timer: Timer by lazy {
        Timer()
    }

    private val productsFile: File by lazy {
        File("${applicationContext.getExternalFilesDir(null)}/products.json")
    }

    private val storesFile: File by lazy {
        File("${applicationContext.getExternalFilesDir(null)}/store.json")
    }

    private var tTask: TimerTask? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate() called")
        schedule()
    }

    private fun schedule() {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.scheduleAtFixedRate({
            Log.d(TAG, "Run invoked")
            if (SimpleOperations.isNetworkAvailable(applicationContext)) {
                Log.d(TAG, "Network available")
                val productStorage = ProductStorageJsonImpl(productsFile)
                val products = productStorage.itemsList
                val newProducts = products.map {
                    val retriever = ProductRetrieverImpl(it.link)
                    retriever.retrieveWebPage()
                    val product = retriever.tryRetrieveExistingValues(storesFile)
                    Log.d(TAG, "Product last update: ${product.lastUpdate}. Product price: ${product.price}")
                    product
                }.toCollection(arrayListOf())
                productStorage.updateItemsList(newProducts);
                Log.d(TAG, "Updated JSON: ${productStorage.productsJSON}")
                SimpleOperations.writeJSONToFile(productStorage.productsJSON.toString(), productsFile)
            }
        }, 0, 5, TimeUnit.MINUTES)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "OnBind invoked")
        return null
    }

}