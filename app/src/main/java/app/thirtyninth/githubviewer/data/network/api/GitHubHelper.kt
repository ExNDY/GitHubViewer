package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response

interface GitHubHelper {
    suspend fun getUser(token: String, username: String): Response<User>
    suspend fun getUserRepository(token: String, username: String)
    suspend fun getRepositoryInfo(token: String, url: String)
}