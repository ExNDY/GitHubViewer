package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

// FIXME видно что форматирование поехало - нужно использовать автоформатирование в IDE
//  Option + Command + L (macOS)
@Serializable
data class LoginData(
    val username: String,
    val token: String = "token ")