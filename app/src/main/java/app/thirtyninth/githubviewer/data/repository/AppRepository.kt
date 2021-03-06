package app.thirtyninth.githubviewer.data.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Owner
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository
@Inject constructor(
    private val gitHub: GitHubRemoteData
) {

    suspend fun getUserInfo(token: String): Result<Owner> {
        return gitHub.getUser(token)
    }

    suspend fun getRepositoryList(): Result<List<GitHubRepository>> {
        return gitHub.getUserRepositoryList()
    }

    suspend fun getRepositoryDetails(
        owner: String,
        repository: String
    ): Result<GitHubRepository> {
        return gitHub.getRepositoryDetail(owner, repository)
    }

    suspend fun getReadmeDetail(
        owner: String,
        repository: String
    ): Result<Readme> {
        return gitHub.getReadmeData(owner, repository)
    }

    suspend fun getReadmeMd(url: String): Result<String> {
        return gitHub.fetchReadme(url)
    }
}