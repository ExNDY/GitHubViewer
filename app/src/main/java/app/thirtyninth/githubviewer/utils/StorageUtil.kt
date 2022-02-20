package app.thirtyninth.githubviewer.utils

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.io.IOException

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

    fun jsonToLanguageColorList(context: Context, jsonFilePath: String): JsonObject {
        return Json.parseToJsonElement(readJSON(context, jsonFilePath)).jsonObject
    }
}