package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Readme(
    val download_url: String,
    val name: String,
)