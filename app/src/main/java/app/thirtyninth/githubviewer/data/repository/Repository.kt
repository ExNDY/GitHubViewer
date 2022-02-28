package app.thirtyninth.githubviewer.data.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.NetworkExceptionType
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository
@Inject constructor(
    private val gitHub: GitHubRemoteData
) {
    suspend fun getUserInfo(token: String): Result<User?> {
        return unwrapResponse(gitHub.getUser(token))
    }

    suspend fun getRepositoryList(): Result<List<GitHubRepositoryModel>?> {
        return unwrapResponse(gitHub.getUserRepositoryList())
    }

    suspend fun getRepositoryInfo(
        username: String,
        repository: String
    ): Result<GitHubRepositoryModel?> {
        return unwrapResponse(gitHub.getRepositoryInfo(username, repository))
    }

    private fun <T> unwrapResponse(response: Response<T>): Result<T?> {
        val body = response.body()
        val error = response.errorBody()
        val code = response.code()

        when {
            error != null -> {
                when (code) {
                    304 -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.NOT_MODIFIED
                        )
                    }
                    401 -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.UNAUTHORIZED
                        )
                    }
                    403 -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.NOT_MODIFIED
                        )
                    }
                    404 -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.NOT_FOUND
                        )
                    }
                    422 -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.UNPROCESSABLE_ENTITY
                        )
                    }
                    else -> {
                        return Result.Error(
                            Exception(error.string()),
                            NetworkExceptionType.NOT_DETERMINED
                        )
                    }
                }
            }
            body != null -> {
                return Result.Success(body)
            }
            else -> {
                return Result.Error(
                    Exception("Unknown error: ${response.raw().message}"),
                    NetworkExceptionType.NOT_DETERMINED
                )
            }
        }
    }
}