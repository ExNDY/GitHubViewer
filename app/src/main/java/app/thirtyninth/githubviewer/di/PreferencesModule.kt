package app.thirtyninth.githubviewer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    private const val PREFERENCES_NAME = "preferences"

    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): DataStore<ProtoSettings> {
        return DataStoreFactory.create(
            serializer = UserPreferences.PreferencesSerializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile(PREFERENCES_NAME) },
            corruptionHandler = null
        )
    }
}