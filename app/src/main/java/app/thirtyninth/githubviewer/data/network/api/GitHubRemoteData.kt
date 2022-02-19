package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.User
import javax.inject.Inject

class GitHubRemoteData @Inject constructor(
    private val gitHubService: GitHubService
) {
    suspend fun getUser(token: String): User = gitHubService.getUser(token)

    suspend fun getUserRepositoryList(token: String): List<GitHubRepository> =
        gitHubService.getUserRepositoryList(token)

    suspend fun getRepositoryInfo(
        token: String,
        userName: String,
        repositoryName: String
    ): GitHubRepository = gitHubService.getRepositoryInfo(token, userName, repositoryName)
}