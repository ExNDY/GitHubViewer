package app.thirtyninth.githubviewer.di

import app.thirtyninth.githubviewer.data.network.api.DownloadService
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import app.thirtyninth.githubviewer.data.network.interceptors.AuthInterceptor
import app.thirtyninth.githubviewer.data.network.interceptors.TokenProvider
import app.thirtyninth.githubviewer.preferences.KeyValueStorage
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.github.com/"

    @Provides
    fun provideTokenProvider(keyValueStorage: KeyValueStorage): TokenProvider {
        return object : TokenProvider {
            override val token: String?
                get() = keyValueStorage.tokenStateFlow.value
        }
    }

//    @Provides
//    fun provideAuthTokenProvider(keyValueStorage: KeyValueStorage):TokenProvider{
//        return object :TokenProvider{
//            override val token: String
//                get() = keyValueStorage.authToken
//        }
//    }

    @Provides
    fun providesAcceptInterceptor(): AcceptInterceptor =
        AcceptInterceptor()

    @Provides
    fun providesAuthInterceptor(tokenProvider: TokenProvider): AuthInterceptor =
        AuthInterceptor(tokenProvider)


    @Provides
    fun provideOkHttpClient(
        acceptInterceptor: AcceptInterceptor,
        authAuthenticator: AuthInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(acceptInterceptor)
        .addInterceptor(authAuthenticator)
        .build()

    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideGitHubService(retrofit: Retrofit): GitHubService =
        retrofit.create(GitHubService::class.java)

    @Provides
    @Singleton
    fun provideDownloadService(retrofit: Retrofit): DownloadService =
        retrofit.create(DownloadService::class.java)

    @Provides
    @Singleton
    fun provideGitHubHelper(
        gitHubService: GitHubService,
        downloadService: DownloadService
    ): GitHubRemoteData =
        GitHubRemoteData(gitHubService, downloadService)
}