package com.raccoonapps.pricemanager.app.api.model

import android.content.Context
import android.net.ConnectivityManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object SimpleOperations {

    /**
     * Function that calculates index of object in JSONArray
     * @param id UUID of json object
     * @param array JSONArray, which contains object
     * @return Index of object, which id we provide as first parameter
     * */
    fun getItemIndex(id: UUID, array: JSONArray): Int {
        for (itemIndex in 0..array.length() - 1) {
            val currentItem = array.get(itemIndex) as JSONObject
            if (currentItem.getString(JSONProductFields.ID.value).equals(id.toString()))
                return itemIndex
        }
        return -1
    }

    /**
     * Function for loading JSON String from {@param productsFile}
     * @return JSON object in String format
     * */
    fun loadJSONFromFile(productsFile: File) = productsFile.readLines().joinToString("\n")

    /**
     * Function, that writes json in file
     * @param json String, which need to be write in file
     * */
    fun writeJSONToFile(json: String, destination: File) = destination.writeText(json);

    /**
     * Function, that checks internet availability
     * @return true, if connection is available and network type is WiFi, otherwise, it returns false
     * */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo.isAvailable && networkInfo.type == ConnectivityManager.TYPE_WIFI;
    }
}