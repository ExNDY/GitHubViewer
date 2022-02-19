package app.thirtyninth.githubviewer.di

import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesBaseUrl() : String = Constants.BASE_URL

    @Singleton
    @Provides
    fun providesAcceptInterceptor() =
        AcceptInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(acceptInterceptor: AcceptInterceptor) = OkHttpClient.Builder()
        .addInterceptor(acceptInterceptor)
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    @Provides
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideConverterFactory():Converter.Factory{
        val type = "application/json".toMediaType()
        return Json{
            ignoreUnknownKeys = true
        }.asConverterFactory(type)
    }

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL:String, okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit = Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubService = retrofit.create(GitHubService::class.java)

    @Provides
    @Singleton
    fun provideGitHubHelper(gitHubService: GitHubService) : GitHubRemoteData = GitHubRemoteData(gitHubService)
}