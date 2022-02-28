package app.thirtyninth.githubviewer.di

import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import app.thirtyninth.githubviewer.data.network.interceptors.AuthAuthenticator
import app.thirtyninth.githubviewer.data.network.interceptors.TokenProvider
import app.thirtyninth.githubviewer.data.repository.KeyValueStorage
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
    private const val BASE_URL = "https://api.github.com/"

    @Provides
    fun provideTokenProvider(keyValueStorage: KeyValueStorage): TokenProvider {
        return object : TokenProvider {
            override val token: String?
                get() = keyValueStorage.tokenStateFlow.value
        }
    }

    @Provides
    fun providesAcceptInterceptor(): AcceptInterceptor =
        AcceptInterceptor()

    @Provides
    fun providesAuthenticator(tokenProvider: TokenProvider): AuthAuthenticator =
        AuthAuthenticator(tokenProvider)


    @Provides
    @Singleton
    fun provideOkHttpClient(
        acceptInterceptor: AcceptInterceptor,
        authAuthenticator: AuthAuthenticator
    ) = OkHttpClient.Builder()
        .authenticator(authAuthenticator)
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
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
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