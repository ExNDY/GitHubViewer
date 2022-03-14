package app.thirtyninth.githubviewer.data.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.Readme
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.*
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
        username: String,
        repository: String
    ): Result<GitHubRepositoryModel?> {
        return enqueue(gitHub.getRepositoryInfo(username, repository))
    }

    suspend fun getReadmeData(
        username: String,
        repository: String
    ): Result<Readme?> {
        return enqueue(gitHub.getReadmeData(username, repository))
    }

    private fun <T> enqueue(response: Response<T>): Result<T?> {
        return try {
            if (response.code() == 401) {
                Result.Error(UnauthorizedException())
            }

            if (response.code() == 404) {
                Result.Error(NotFoundException())
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
            is IOException -> NoInternetException()
            is HttpException -> httpExceptionsMapper(remoteException)
            else -> UnexpectedException(remoteException)
        }
    }
}