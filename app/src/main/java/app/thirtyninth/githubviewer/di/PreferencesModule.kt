package app.thirtyninth.githubviewer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.preferences.UserPreferences
import app.thirtyninth.githubviewer.preferences.PreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context) = UserPreferences(context.dataStore)

    private val Context.dataStore : DataStore<ProtoSettings> by dataStore(
        fileName = "preferences.proto",
        serializer = PreferencesSerializer
    )
}