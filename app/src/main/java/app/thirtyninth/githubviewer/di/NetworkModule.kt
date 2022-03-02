package app.thirtyninth.githubviewer.di

import androidx.datastore.core.DataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.common.Constants
import app.thirtyninth.githubviewer.data.network.api.GitHubRemoteData
import app.thirtyninth.githubviewer.data.network.api.GitHubService
import app.thirtyninth.githubviewer.data.network.interceptors.AcceptInterceptor
import app.thirtyninth.githubviewer.data.network.interceptors.AuthInterceptor
import app.thirtyninth.githubviewer.data.network.interceptors.TokenProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // FIXME примитивные типы такие как String не должны добавляться в граф хилта. Если прям
    //  очень надо - то нужно ставить квалификатор чтобы явно указывать куда эту строчку надо доставлять
    @Provides
    fun providesBaseUrl(): String = Constants.BASE_URL
//
//    @Provides
//    fun providesAcceptInterceptor() =
//        AcceptInterceptor()
//
//    @Provides
//    fun providesAuthInterceptor() =
//        AuthInterceptor()

    @Provides
    fun provideTokenProvider(keyValueStorage: KeyValueStorage, test: String): TokenProvider {
        return object: TokenProvider {
            override val token: String?
                get() = keyValueStorage.tokenStateFlow.value
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(acceptInterceptor: AcceptInterceptor, authInterceptor: AuthInterceptor) = OkHttpClient.Builder()
        .addInterceptor(acceptInterceptor)
        .addInterceptor(authInterceptor)
        // FIXME зачем указан протокол?
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
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideRetrofit(json: Json, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            // FIXME тут имеет смысл Json вынести как отдельный Provides
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