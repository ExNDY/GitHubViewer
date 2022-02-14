package app.thirtyninth.githubviewer.di

import android.content.Context
import app.thirtyninth.githubviewer.AuthInterceptor
import app.thirtyninth.githubviewer.common.Constants
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun providesBaseUrl() : String = "https://api.github.com/"

    @Singleton
    @Provides
    fun providesAuthInterceptor(@ApplicationContext context: Context) =
        AuthInterceptor(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor) = OkHttpClient
        .Builder()
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    @kotlinx.serialization.ExperimentalSerializationApi
    fun provideRetrofit(BASE_URL:String, okHttpClient: OkHttpClient): Retrofit? = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
}