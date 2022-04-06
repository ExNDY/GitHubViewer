package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Readme(
    @SerialName("download_url")
    val downloadUrl: String,
    val name: String,
)