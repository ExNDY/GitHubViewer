package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response
import javax.inject.Inject

class GitHubHelperImpl @Inject constructor(
    private val gitHubService: GitHubService
):GitHubHelper {
    override suspend fun getUser(token: String, username: String): Response<User> {
        return gitHubService.getUser(token, username)
    }

    override suspend fun getUserRepository(token: String, username: String) {

    }

    override suspend fun getRepositoryInfo(token: String, url: String) {

    }
}