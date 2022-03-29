package app.thirtyninth.githubviewer.data.models

import app.thirtyninth.githubviewer.ui.interfaces.LocalizeString

data class ExceptionBundle(
    val title:LocalizeString,
    val message:LocalizeString,
    val imageResId:Int,
    val titleColor: Int
)
