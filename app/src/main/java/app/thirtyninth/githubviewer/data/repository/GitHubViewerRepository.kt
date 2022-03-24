package app.thirtyninth.githubviewer.data.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Owner
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubViewerRepository
@Inject constructor(
    private val gitHub: GitHubRemoteData
) {

    suspend fun getUserInfo(token: String): Result<Owner?> {
        return gitHub.getUser(token)
    }

    suspend fun getRepositoryList(): Result<List<GitHubRepositoryModel>?> {
        return gitHub.getUserRepositoryList()
    }

    suspend fun getRepositoryInfo(
        owner: String,
        repository: String
    ): Result<GitHubRepositoryModel?> {
        return gitHub.getRepositoryDetail(owner, repository)
    }

    suspend fun getReadmeData(
        owner: String,
        repository: String
    ): Result<Readme?> {
        return gitHub.getReadmeData(owner, repository)
    }
}