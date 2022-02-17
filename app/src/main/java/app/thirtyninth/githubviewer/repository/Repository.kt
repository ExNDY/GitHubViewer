package app.thirtyninth.githubviewer.repository

import app.thirtyninth.githubviewer.data.network.api.GitHubHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository
@Inject constructor(
    private val gitHubHelper: GitHubHelper
) {
    suspend fun getUser(token: String, userName: String) = gitHubHelper.getUser(token, userName)
}