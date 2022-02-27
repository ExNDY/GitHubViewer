package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepositoryModel
import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubService {

    @GET("/user")
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getUser(
        @Header("Authorization") token: String
    ) : Response<User>

    @GET("/user/repos")
    suspend fun getUserRepositoryList(
        @Header("Authorization") token: String
    ) : Response<List<GitHubRepositoryModel>>

    @GET("/repos/{owner}/{repository}")
    suspend fun getRepositoryInfo(
        @Header("Authorization") token: String,
        @Path("owner") username:String,
        @Path("repository") repository:String
    ): Response<GitHubRepositoryModel>
}