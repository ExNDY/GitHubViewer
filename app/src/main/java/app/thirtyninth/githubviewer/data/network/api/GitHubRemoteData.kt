package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response
import javax.inject.Inject

class GitHubRemoteData @Inject constructor(
    private val gitHubService: GitHubService
) {
    suspend fun getUser(token: String): Response<User> = gitHubService.getUser(token)

    suspend fun getUserRepositoryList(): Response<List<GitHubRepositoryModel>> =
        gitHubService.getUserRepositoryList()

    suspend fun getRepositoryInfo(
        username: String,
        repository: String
    ): Response<GitHubRepositoryModel> =
        gitHubService.getRepositoryInfo(username, repository)

    suspend fun getReadme(
        username: String,
        repository: String
    ): Response<Readme> = gitHubService.getReadme(username, repository)
}