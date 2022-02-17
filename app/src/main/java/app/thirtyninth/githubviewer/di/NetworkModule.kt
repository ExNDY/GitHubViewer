package app.thirtyninth.githubviewer.di

import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.network.api.GitHubHelper
import app.thirtyninth.githubviewer.data.network.api.GitHubHelperImpl
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import app.thirtyninth.githubviewer.utils.ConverterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.annotation.Nullable
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
        .build()

    @Provides
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideConverterFactory():Converter.Factory{
        val type = "application/json".toMediaType()
        return Json.asConverterFactory(type)
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
    fun provideGitHubHelper(gitHubHelper: GitHubHelperImpl) : GitHubHelper = gitHubHelper
}