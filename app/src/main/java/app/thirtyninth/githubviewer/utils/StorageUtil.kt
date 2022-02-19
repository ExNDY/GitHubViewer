package app.thirtyninth.githubviewer.utils

import android.content.Context
import app.thirtyninth.githubviewer.data.models.LanguageColor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

object StorageUtil {
    private fun readJSON(context: Context, jsonFilePath: String): String {
        val json: String

        try {
            json = context.assets.open(jsonFilePath).bufferedReader().use {
                it.readText()
            }
        } catch (ex: IOException) {
            return "null"
        }

        return json
    }

    fun jsonToLanguageColorList(context: Context, jsonFilePath: String): ArrayList<LanguageColor> {
        val languageColorList: ArrayList<LanguageColor> = ArrayList()

        try {
            val jsonObj = JSONObject(readJSON(context, jsonFilePath))
            val colorNames:Iterator<String> = jsonObj.keys()

            while (colorNames.hasNext()){
                val colorName = colorNames.next()
                val value: String

                try {
                    value = jsonObj.get(colorName).toString()
                }catch (ex: JSONException){
                    throw ex
                }

                languageColorList.add(LanguageColor(colorName, value))
            }

        } catch (e: JSONException) {
            throw e
        }

        return languageColorList
    }
}