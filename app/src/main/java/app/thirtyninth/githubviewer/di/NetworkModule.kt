package app.thirtyninth.githubviewer.di

import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesBaseUrl(): String = Constants.BASE_URL

    @Singleton
    @Provides
    fun providesAcceptInterceptor() =
        AcceptInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(acceptInterceptor: AcceptInterceptor) = OkHttpClient.Builder()
        .addInterceptor(acceptInterceptor)
        .protocols(listOf(Protocol.HTTP_1_1))
        .callTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideRetrofit(BASE_URL: String, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubService =
        retrofit.create(GitHubService::class.java)

    @Provides
    @Singleton
    fun provideGitHubHelper(gitHubService: GitHubService): GitHubRemoteData =
        GitHubRemoteData(gitHubService)
}