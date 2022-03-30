package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Owner
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.network.EmptyDataException
import app.thirtyninth.githubviewer.data.network.HttpCallException
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class GitHubRemoteData @Inject constructor(
    private val gitHubService: GitHubService,
    private val downloadService: DownloadService
) {

    suspend fun getUser(authToken: String): Result<Owner> {
        return enqueue { gitHubService.getUser("token $authToken") }
    }

    suspend fun getUserRepositoryList(): Result<List<GitHubRepository>> {
        return enqueue { gitHubService.getUserRepositoryList() }
    }

    suspend fun getRepositoryDetail(
        owner: String,
        repository: String
    ): Result<GitHubRepository> {
        return enqueue { gitHubService.getRepositoryDetail(owner, repository) }
    }

    suspend fun getReadmeData(
        owner: String,
        repository: String
    ): Result<Readme> {
        return enqueue { gitHubService.getReadmeDetail(owner, repository) }

    }

    suspend fun fetchReadme(url: String): Result<String> {
        return enqueue { downloadService.downloadReadme(url) }
    }

    private suspend fun <T> enqueue(responseBlock: suspend () -> Response<T>): Result<T> {
        try {
            val response = responseBlock()
            when (response.code()) {
                401 -> {
                    return Result.failure(
                        UnauthorizedException(
                            code = response.code(),
                            request = response.raw().request.toString(),
                            url = response.raw().request.url,
                        )
                    )
                }
                404 -> {
                    return Result.failure(
                        NotFoundException(
                            code = response.code(),
                            request = response.raw().request.toString(),
                            url = response.raw().request.url
                        )
                    )
                }
                else -> {
                    val responseBody = response.body()

                    return if (responseBody == null) {
                        Result.failure(EmptyDataException())
                    } else {
                        Result.success(responseBody)
                    }
                }
            }
        } catch (exception: Exception) {
            return Result.failure(mapToDomainException(exception))
        }
    }

    private fun mapToDomainException(
        remoteException: Exception,
    ): Throwable {
        return when (remoteException) {
            is IOException -> NoInternetException(remoteException)
            is HttpException -> HttpCallException(remoteException)
            else -> UnexpectedException(remoteException)
        }
    }
}