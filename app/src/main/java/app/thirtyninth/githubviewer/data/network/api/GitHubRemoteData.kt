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
        owner: String,
        repository: String
    ): Response<GitHubRepositoryModel> =
        gitHubService.getRepositoryInfo(owner, repository)

    suspend fun getReadmeData(
        owner: String,
        repository: String
    ): Response<Readme> = gitHubService.getReadmeData(owner, repository)
}