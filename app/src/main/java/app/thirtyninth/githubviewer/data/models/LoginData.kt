package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val username: String,
    val token: String = ""
)