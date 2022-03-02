package app.thirtyninth.githubviewer.repository

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.User
import app.thirtyninth.githubviewer.data.network.Exceptions
import app.thirtyninth.githubviewer.data.network.Result
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// FIXME сильно код едет без форматирования, надо автоформатирование всегда применять.
// FIXME имя класса слишком абстрактное, нужно точнее :)
@Singleton
class Repository @Inject constructor(
    private val gitHub: GitHubRemoteData
) {
    suspend fun getUser(token: String): Result<User?> = withContext(Dispatchers.IO) {
        // FIXME вся внутрянка дублируется между всеми 3 функциями. стоит выделить отдельную функцию
        //  которая содержит всю обработку ошибок
        try {
            val response = gitHub.getUser(token)

            if (response.isSuccessful) {
                val user = response.body()

                return@withContext Result.Success(user, response.code())
            } else {
                if (response.code() == 401) {
                    return@withContext Result.Error(Exception(Exceptions.AUTHENTICATOR_ERROR), 401)
                }

                return@withContext Result.Error(Exception(Exceptions.SERVER_ERROR), response.code())
            }
        } catch (ex: Exception) {
            return@withContext Result.Error(ex, 0)
        }
    }

    suspend fun getUserRepository(token: String): Result<List<GitHubRepositoryModel>?> =
        withContext(Dispatchers.IO) {
            try {
                val response = gitHub.getUserRepositoryList(token)

                if (response.isSuccessful) {
                    val list = response.body()
                    return@withContext Result.Success(list, response.code())
                } else {
                    if (response.code() == 401) {
                        return@withContext Result.Error(
                            Exception(Exceptions.AUTHENTICATOR_ERROR),
                            401
                        )
                    }

                    return@withContext Result.Error(
                        Exception(Exceptions.SERVER_ERROR),
                        response.code()
                    )
                }
            } catch (ex: Exception) {
                return@withContext Result.Error(ex, 0)
            }
        }


    suspend fun getRepositoryInfo(
        token: String, username: String,
        repository: String
    ): Result<GitHubRepositoryModel?> = withContext(Dispatchers.IO) {
        try {
            val response = gitHub.getRepositoryInfo(token, username, repository)

            if (response.isSuccessful) {
                val repo = response.body()

                return@withContext Result.Success(repo, response.code())
            } else {
                if (response.code() == 401) {
                    return@withContext Result.Error(Exception(Exceptions.AUTHENTICATOR_ERROR), 401)
                }

                return@withContext Result.Error(Exception(Exceptions.SERVER_ERROR), response.code())
            }
        } catch (ex: Exception) {
            return@withContext Result.Error(ex, 0)
        }
    }
}