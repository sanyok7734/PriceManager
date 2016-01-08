package model

import com.managerprice.racconapps.app.api.model.JSONProductFields
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

object SimpleOperations {

    fun getItemIndex(id: UUID, array: JSONArray): Int {
        for (itemIndex in 0..array.length()) {
            val currentItem = array.get(itemIndex) as JSONObject
            if (currentItem.getString(JSONProductFields.ID.value).equals(id))
                return itemIndex
        }
        return -1
    }

    fun loadJSONFromFile(productsFile: File) = productsFile.readLines().joinToString("\n")

    fun writeJSONToFile(json: String, destination: File) = destination.writeText(json);
}

fun JSONArray.delete(position: Int) {
    val array = JSONArray()
    for (index in 0..length())
        if (index != position)
            array.put(get(index))
}