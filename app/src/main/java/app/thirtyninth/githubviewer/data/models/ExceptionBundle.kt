package app.thirtyninth.githubviewer.data.models

import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString
import okhttp3.HttpUrl

data class ExceptionBundle(
    val title: LocalizeString,
    val message: LocalizeString,
    val imageResId: Int,
    val colorResId: Int,
    val errorCode: Int? = null,
    val request:String? = null,
    val url: HttpUrl? = null
)
