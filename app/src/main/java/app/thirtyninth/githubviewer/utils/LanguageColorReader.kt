package app.thirtyninth.githubviewer.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Color.alpha
import android.graphics.Color.blue
import android.graphics.Color.colorSpace
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.ColorSpace
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.io.IOException

class LanguageColorReader {
    private val TAG = LanguageColorReader::class.java.simpleName

    private fun readJSON(context: Context, jsonFilePath: String): String {
        val json: String

        try {
            json = context.assets.open(jsonFilePath).bufferedReader().use {
                it.readText()
            }
        } catch (e: IOException) {
            Timber.tag(TAG).e(e)

            throw e
        }

        return json
    }

    private fun jsonToJsonObj(context: Context, jsonFilePath: String): JsonObject {
        return Json.parseToJsonElement(readJSON(context, jsonFilePath)).jsonObject
    }

    //TODO сделать нормальный json файл и залить себе в гист
    fun fetchLanguageColorsMap(
        context: Context,
        jsonFilePath: String
    ): Map<String, Color> {
        val colorsJson = jsonToJsonObj(context, jsonFilePath)

        return colorsJson.mapValues { (_, colorString) ->
            Color.parseColor(colorString.jsonPrimitive.content).let { color ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    Color.valueOf(color)
                } else {
                    Color()
                }
            }
        }
    }
}