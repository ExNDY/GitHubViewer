package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val avatar_url: String? = null,
    val bio: String? = null,
    val blog: String? = null,
    val collaborators: Int? = null,
    val company: String? = null,
    val created_at: String? = null,
    val disk_usage: Int? = null,
    val email: String? = null,
    val events_url: String? = null,
    val followers: Int? = null,
    val followers_url: String? = null,
    val following: Int = 0,
    val following_url: String? = null,
    val gists_url: String? = null,
    val gravatar_id: String? = null,
    val hireable: Boolean? = false,
    val html_url: String? = null,
    val id: Int = 0,
    val location: String? = null,
    val login: String? = null,
    val name: String? = null,
    val node_id: String? = null,
    val organizations_url: String? = null,
    val owned_private_repos: Int = 0,
    val plan: Plan,
    val private_gists: Int =0,
    val public_gists: Int=0,
    val public_repos: Int=0,
    val received_events_url: String? = null,
    val repos_url: String? = null,
    val site_admin: Boolean? = false,
    val starred_url: String ? = null,
    val subscriptions_url: String? = null,
    val total_private_repos: Int =0,
    val twitter_username: String ? = null,
    val two_factor_authentication: Boolean? = false,
    val type: String? = null,
    val updated_at: String? = null,
    val url: String? = null
)

@Serializable
data class Plan(
    val collaborators: Int,
    val name: String,
    val private_repos: Int,
    val space: Int
)