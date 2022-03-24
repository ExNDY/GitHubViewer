package app.thirtyninth.githubviewer.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import app.thirtyninth.githubviewer.ProtoSettings
import app.thirtyninth.githubviewer.preferences.PreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {
    private const val PREFERENCES_NAME = "preferences"

    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): DataStore<ProtoSettings> {
        return DataStoreFactory.create(
            serializer = PreferencesSerializer,
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.dataStoreFile(PREFERENCES_NAME) }
        )
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
}