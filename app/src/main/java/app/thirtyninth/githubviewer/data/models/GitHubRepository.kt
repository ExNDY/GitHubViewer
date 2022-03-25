package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepository(
    val description: String?,
    @SerialName("forks_count")
    val forksCount: Int? = 0,
    @SerialName("html_url")
    val htmlURL: String? = null,
    val id: Int? = 0,
    val language: String? = null,
    val license: License? = null,
    val name: String? = null,
    val owner: Owner? = null,
    val private: Boolean? = false,
    @SerialName("stargazers_count")
    val stargazersCount: Int? = 0,
    val url: String? = null,
    @SerialName("watchers_count")
    val watchersCount: Int? = 0,
)

@Serializable
data class License(
    @SerialName("spdx_id")
    val spdxId: String? = null,
)

@Serializable
data class Owner(
    val login: String,
)
