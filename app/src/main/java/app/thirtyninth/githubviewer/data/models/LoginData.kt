package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    val authToken: String
)