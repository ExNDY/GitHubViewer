package app.thirtyninth.githubviewer.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository
@Inject constructor(
    private val gitHub: GitHubRemoteData
) {
    suspend fun getUser(token: String) = gitHub.getUser(token)

    suspend fun getUserRepository(token: String):List<GitHubRepository> = gitHub.getUserRepositoryList(token)

    suspend fun getRepositoryInfo(token: String, userName: String, repositoryName: String) = gitHub.getRepositoryInfo(token, userName, repositoryName)
}