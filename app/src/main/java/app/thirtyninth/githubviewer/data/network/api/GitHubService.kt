package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

// FIXME довольно неудобно каждому методу с авторизацией передавать в аргумент токен.
//  Вижу что была попытка сделать единый interceptor для этого - такой подход проще и надежнее будет,
//  плюс будет централизованная обработка ошибки авторизации
interface GitHubService {

    @GET("/user")
    // FIXME раз есть interceptor для этого - то нет смысла это вручную поставлять
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<User>

    @GET("/user/repos")
    suspend fun getUserRepositoryList(
        @Header("Authorization") token: String
    ): Response<List<GitHubRepositoryModel>>

    @GET("/repos/{username}/{repository}")
    suspend fun getRepositoryInfo(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Path("repository") repository: String
    ): Response<GitHubRepositoryModel>
}