package app.thirtyninth.githubviewer.utils

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter

class ConverterFactory: Converter.Factory() {

    @kotlinx.serialization.ExperimentalSerializationApi
    fun getConverter():Converter.Factory{
        val type = "application/json".toMediaType()
        return Json.asConverterFactory(type)
    }
}