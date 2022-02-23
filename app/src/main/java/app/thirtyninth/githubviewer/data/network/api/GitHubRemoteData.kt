package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class GitHubRemoteData @Inject constructor(
    private val gitHubService: GitHubService
) {
    suspend fun getUser(token: String): Response<User>  = gitHubService.getUser(token)

    suspend fun getUserRepositoryList(token: String): Response<List<GitHubRepositoryModel>> =
        gitHubService.getUserRepositoryList(token)

    suspend fun getRepositoryInfo(
        token: String,
        username: String,
        repository: String
    ): Response<GitHubRepositoryModel> = gitHubService.getRepositoryInfo(token, username, repository)
}