package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.GitHubRepository
import app.thirtyninth.githubviewer.data.models.Owner
import app.thirtyninth.githubviewer.data.models.Readme
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GitHubService {

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): Response<Owner>

    @GET("user/repos")
    suspend fun getUserRepositoryList(): Response<List<GitHubRepository>>

    @GET("repos/{owner}/{repository}")
    suspend fun getRepositoryDetail(
        @Path("owner") owner: String,
        @Path("repository") repository: String
    ): Response<GitHubRepository>

    @GET("/repos/{owner}/{repository}/readme")
    suspend fun getReadmeData(
        @Path("owner") owner: String,
        @Path("repository") repository: String
    ): Response<Readme>
}