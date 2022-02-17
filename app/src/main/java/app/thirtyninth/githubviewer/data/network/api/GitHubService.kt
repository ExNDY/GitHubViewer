package app.thirtyninth.githubviewer.data.network.api

import app.thirtyninth.githubviewer.data.models.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubService {

    @GET("https://api.github.com/{user}")
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("user") userName: String
    ) : Response<User>
/*
    @GET("/{user}/repos/")
    suspend fun getUserRepos(
        @Header("Authorization:") token: String,
        @Path("user") userName: String
    )

    @GET("{url}")
    suspend fun getRepositoryInfo(
        @Header("Authorization:") token: String,
        @Path("url") url: String
    )*/
}