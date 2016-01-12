package com.raccoonapps.pricemanager.app.api.model

import android.content.Context
import android.net.ConnectivityManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object SimpleOperations {

    fun getItemIndex(id: UUID, array: JSONArray): Int {
        for (itemIndex in 0..array.length() - 1) {
            val currentItem = array.get(itemIndex) as JSONObject
            if (currentItem.getString(JSONProductFields.ID.value).equals(id.toString()))
                return itemIndex
        }
        return -1
    }

    fun loadJSONFromFile(productsFile: File) = productsFile.readLines().joinToString("\n")

    fun writeJSONToFile(json: String, destination: File) = destination.writeText(json);

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo.isAvailable && networkInfo.type == ConnectivityManager.TYPE_WIFI;
    }
}