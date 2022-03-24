package app.thirtyninth.githubviewer.utils

import android.content.Context
import android.graphics.Color
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

    private fun jsonToJsonObj(context: Context, jsonFilePath: String): JsonObject {
        return Json.parseToJsonElement(readJSON(context, jsonFilePath)).jsonObject
    }

    fun fetchLanguageColorsMap(
        context: Context,
        jsonFilePath: String
    ): Map<String, Color> {
        val colorsJson = jsonToJsonObj(context, jsonFilePath)

        return colorsJson.mapValues { (_, colorString) ->
            Color.parseColor(colorString.jsonPrimitive.content).let {
                Color.valueOf(it)
            }
        }
    }
}