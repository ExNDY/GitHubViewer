package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Owner
import app.thirtyninth.githubviewer.data.models.Readme
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
    private val gitHubService: GitHubService
) {

    suspend fun getUser(token: String): Result<Owner?> {
        return try {
            enqueue(gitHubService.getUser(token))
        } catch (e: Exception) {
            Result.failure(mapToDomainException(e))
        }
    }

    suspend fun getUserRepositoryList(): Result<List<GitHubRepository>?> {
        return try {
            enqueue(gitHubService.getUserRepositoryList())
        } catch (e: Exception) {
            Result.failure(mapToDomainException(e))
        }
    }

    suspend fun getRepositoryDetail(
        owner: String,
        repository: String
    ): Result<GitHubRepository?> {
        return try {
            enqueue(gitHubService.getRepositoryDetail(owner, repository))
        } catch (e: Exception) {
            Result.failure(mapToDomainException(e))
        }
    }

    suspend fun getReadmeData(
        owner: String,
        repository: String
    ): Result<Readme?> {
        return try {
            enqueue(gitHubService.getReadmeData(owner, repository))
        } catch (e: Exception) {
            Result.failure(mapToDomainException(e))
        }
    }

    private fun <T> enqueue(response: Response<T>): Result<T?> {
        try {
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
                    return Result.success(response.body())
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