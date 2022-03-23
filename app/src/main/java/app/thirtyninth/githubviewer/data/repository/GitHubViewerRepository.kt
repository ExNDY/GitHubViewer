package app.thirtyninth.githubviewer.data.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.NoInternetException
import app.thirtyninth.githubviewer.data.network.NotFoundException
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.data.network.UnauthorizedException
import app.thirtyninth.githubviewer.data.network.UnexpectedException
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubViewerRepository
@Inject constructor(
    private val gitHub: GitHubRemoteData
) {
    suspend fun getUserInfo(token: String): Result<User?> {
        return enqueue(gitHub.getUser(token))
    }

    suspend fun getRepositoryList(): Result<List<GitHubRepositoryModel>?> {
        return enqueue(gitHub.getUserRepositoryList())
    }

    suspend fun getRepositoryInfo(
        owner: String,
        repository: String
    ): Result<GitHubRepositoryModel?> {
        return enqueue(gitHub.getRepositoryInfo(owner, repository))
    }

    suspend fun getReadmeData(
        owner: String,
        repository: String
    ): Result<Readme?> {
        return enqueue(gitHub.getReadmeData(owner, repository))
    }

    private fun <T> enqueue(response: Response<T>): Result<T?> {
        return try {
            if (response.code() == 401) {
                Result.Error(
                    UnauthorizedException(
                        msg = response.raw().message,
                        url = response.raw().request.url
                    )
                )
            }

            if (response.code() == 404) {
                Result.Error(
                    NotFoundException(
                        msg = response.raw().message,
                        url = response.raw().request.url
                    )
                )
            } else {
                Result.Success(response.body())
            }
        } catch (exception: Exception) {
            Result.Error(mapToDomainException(exception))
        }
    }

    private fun mapToDomainException(
        remoteException: Exception,
        httpExceptionsMapper: (HttpException) -> Exception? = { null }
    ): Exception? {
        return when (remoteException) {
            is IOException -> NoInternetException(remoteException)
            is HttpException -> httpExceptionsMapper(remoteException)
            else -> UnexpectedException(remoteException)
        }
    }
}