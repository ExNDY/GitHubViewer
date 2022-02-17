package app.thirtyninth.githubviewer.data.models

import kotlinx.serialization.SerialName
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
    @SerialName("name")
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
    @SerialName("collaborators")
    val collaborators: Int,
    @SerialName("name")
    val name: String,
    @SerialName("private_repos")
    val private_repos: Int,
    @SerialName("space")
    val space: Int
)
/*

data class userTemp(
    val avatar_url: String,
    val bio: String,
    val blog: String,
    val collaborators: Int,
    val company: String,
    val created_at: String,
    val disk_usage: Int,
    val email: String,
    val events_url: String,
    val followers: Int,
    val followers_url: String,
    val following: Int,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val hireable: Boolean,
    val html_url: String,
    val id: Int,
    val location: String,
    val login: String,
    val name: String,
    val node_id: String,
    val organizations_url: String,
    val owned_private_repos: Int,
    val plan: Plan,
    val private_gists: Int,
    val public_gists: Int,
    val public_repos: Int,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val total_private_repos: Int,
    val twitter_username: String,
    val two_factor_authentication: Boolean,
    val type: String,
    val updated_at: String,
    val url: String
)

data class Plan(
    val collaborators: Int,
    val name: String,
    val private_repos: Int,
    val space: Int
){
 "login": "octocat",
  "id": 1,
  "node_id": "MDQ6VXNlcjE=",
  "avatar_url": "https://github.com/images/error/octocat_happy.gif",
  "gravatar_id": "",
  "url": "https://api.github.com/users/octocat",
  "html_url": "https://github.com/octocat",
  "followers_url": "https://api.github.com/users/octocat/followers",
  "following_url": "https://api.github.com/users/octocat/following{/other_user}",
  "gists_url": "https://api.github.com/users/octocat/gists{/gist_id}",
  "starred_url": "https://api.github.com/users/octocat/starred{/owner}{/repo}",
  "subscriptions_url": "https://api.github.com/users/octocat/subscriptions",
  "organizations_url": "https://api.github.com/users/octocat/orgs",
  "repos_url": "https://api.github.com/users/octocat/repos",
  "events_url": "https://api.github.com/users/octocat/events{/privacy}",
  "received_events_url": "https://api.github.com/users/octocat/received_events",
  "type": "User",
  "site_admin": false,
  "name": "monalisa octocat",
  "company": "GitHub",
  "blog": "https://github.com/blog",
  "location": "San Francisco",
  "email": "octocat@github.com",
  "hireable": false,
  "bio": "There once was...",
  "twitter_username": "monatheoctocat",
  "public_repos": 2,
  "public_gists": 1,
  "followers": 20,
  "following": 0,
  "created_at": "2008-01-14T04:33:35Z",
  "updated_at": "2008-01-14T04:33:35Z",
  "private_gists": 81,
  "total_private_repos": 100,
  "owned_private_repos": 100,
  "disk_usage": 10000,
  "collaborators": 8,
  "two_factor_authentication": true,
  "plan": {
    "name": "Medium",
    "space": 400,
    "private_repos": 20,
    "collaborators": 0
  }
  }
 */