package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Readme(
    @SerialName("_links")
    val links: Links,
    val content: String,
    val download_url: String,
    val encoding: String,
    val git_url: String,
    val html_url: String,
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val type: String,
    val url: String
)

@Serializable
data class Links(
    val git: String,
    val html: String,
    val self: String
)