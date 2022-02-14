package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val username: String, val token: String = "")